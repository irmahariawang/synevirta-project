package com.procodecg.codingmom.ehealth.hpcpdc_card;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.util.Log;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by irma on 24/01/18.
 */

public class HPCActivity{

    final String TAG = "HPCPDCDUMMY";

    public final String ACTION_USB_PERMISSION = "com.procodecg.codingmom.ehealth.USB_PERMISSION";
    public final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    int i;
    String data;
    byte[] selectResponse;
    byte[] authResponse;
    byte[] hpdata;
    byte[] cert;

    ByteBuffer respondData;

    IntentFilter filter;

    TextView hpcNama;
    TextView hpcRole;
    TextView hpcSIP;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    byte[] APDU_owner_auth = {(byte) 0x80, (byte) 0xC3, 0x00, 0x00, 0x00, 0x00, 0x06, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36};
    // 00A4040008 48504344554D4D59
    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x48, 0x50, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    // 80D10000000000
    byte[] APDU_read_HPData = {(byte) 0x80, (byte) 0xD1, 0x00, 0x00, 0x00, 0x00, 0x00};
    // 80D20000000000
    byte[] APDU_read_cert = {(byte) 0x80, (byte) 0xD2, 0x00, 0x00, 0x00, 0x00, 0x00};

    //@Override
    public HPCActivity (Context context){
        i = 0;
        respondData = ByteBuffer.allocate(102);

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(broadcastReceiver, filter);

        // connect usb device
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                usbDevice = entry.getValue();
                int deviceID = usbDevice.getVendorId();
                if (deviceID == 1027 || deviceID == 9025) {
                    Log.d(TAG, "Device ID " + deviceID);
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(usbDevice, pi);
                    keep = false;
                } else {
                    usbConn = null;
                    usbDevice = null;
                }

                if (!keep)
                    break;
            }
        } else {
            //Toast.makeText(getApplicationContext(), "Usb devices empty", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getApplicationContext(), "broadcastReceiver in", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "intent.getAction() " + intent.getAction());

            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    Log.d(TAG, "Permission granted");
                    usbConn = usbManager.openDevice(usbDevice);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbConn);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            // set serial connection parameters
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            Log.i(TAG, "Serial port opened");
                            //Toast.makeText(getApplicationContext(), "Serial connection opened!", Toast.LENGTH_SHORT).show();

                            send();

                            Log.d(TAG, "Ok");
                        } else {
                            Log.w(TAG, "PORT NOT OPEN");
                        }
                    } else {
                        Log.w(TAG, "PORT IS NULL");
                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                }
            }
        }
    };

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        // triggers whenever data is read
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = null;
            data = Util.bytesToHex(bytes);

            Log.d(TAG, "Data " + data);
            Log.d(TAG, "i: " + i);

            if (i == 1) {
                respondData.put(bytes);

                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Select response string: " + Util.bytesToHex(selectResponse));
                    send();
                }
            } else if (i == 2) {
                respondData.put(bytes);

                if (respondData.position() == 2) {
                    authResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(authResponse);
                    respondData.position(0);

                    Log.i(TAG, "Auth response string: " + Util.bytesToHex(authResponse));
                    send();
                }
            } else if (i == 3) {
                respondData.put(bytes);
                Log.d(TAG, "respondData pos: " + respondData.position());

                if (respondData.position() == 102) {
                    byte[] nik, nama, sip;
                    hpdata = new byte[102];
                    respondData.rewind();
                    respondData.get(hpdata);
                    respondData.position(0);

                    Log.i(TAG, "HP Data response string: " + Util.bytesToHex(hpdata));

                    nik = Arrays.copyOfRange(hpdata, 0, 16);
                    byte nameLen = hpdata[16];
                    nama = Arrays.copyOfRange(hpdata, 17, 17 + nameLen);
                    byte sipLen = hpdata[17 + nameLen];
                    sip = Arrays.copyOfRange(hpdata, 17 + nameLen + 1, 17 + nameLen + 1 + sipLen);

                    Log.i(TAG, "nik: " + Util.bytesToString(nik));
                    Log.i(TAG, "nama: " + Util.bytesToString(nama));
                    Log.i(TAG, "sip: " + Util.bytesToString(sip));

                    HPCData.nik = Util.bytesToString(nik);
                    HPCData.nama = Util.bytesToString(nama);
                    HPCData.sip = Util.bytesToString(sip);
/*
                    tvSet(hpcNama, CardUtil.bytesToString(nama));
                    tvSet(hpcRole, "Dokter");
                    tvSet(hpcSIP, CardUtil.bytesToString(sip));
*/
                    send();
                }
            } else if (i == 4) {
                respondData.put(bytes);

                if (respondData.position() == 19) {
                    byte role;
                    cert = new byte[19];
                    respondData.rewind();
                    respondData.get(cert);
                    respondData.position(0);

                    Log.i(TAG, "Cert response string: " + Util.bytesToHex(cert));

                    role = cert[0];

                    Log.i(TAG, "hp role: " + role);

                    send();
                }
            } else {
                Log.e(TAG, "No i.");
            }

        }
    };

    public void send() {
        if (i == 0) {
            Log.d(TAG, "Write");
            serialPort.write(APDU_select);
            Log.d(TAG, "Increment i");
            i++;
            Log.d(TAG, "Apdu select");
        } else if (i == 1) {
            Log.d(TAG, "Write");
            serialPort.write(APDU_owner_auth);
            Log.d(TAG, "Increment i");
            i++;
            Log.d(TAG, "Apdu owner auth");
        } else if (i == 2) {
            Log.d(TAG, "Write");
            serialPort.write(APDU_read_HPData);
            Log.d(TAG, "Increment i");
            i++;
            Log.d(TAG, "Apdu read hp data");
        } else if (i == 3) {
            Log.d(TAG, "Write");
            serialPort.write(APDU_read_cert);
            Log.d(TAG, "Increment i");
            i++;
            Log.d(TAG, "Apdu read cert");
        } else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
        }
    }
}