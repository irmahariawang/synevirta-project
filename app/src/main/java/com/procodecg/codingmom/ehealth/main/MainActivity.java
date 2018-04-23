package com.procodecg.codingmom.ehealth.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.utils.Setting;
import com.procodecg.codingmom.ehealth.data.CopyDBHelper;
import com.procodecg.codingmom.ehealth.utils.Edit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.procodecg.codingmom.ehealth.main.PinActivity.hideKeyboard;

public class MainActivity extends AppCompatActivity {

    final String TAG = "HPCPDCDUMMY";
    public final String ACTION_USB_PERMISSION = "com.procodecg.codingmom.ehealth.USB_PERMISSION";

    int i, isCommandReceived;
    String data;
    byte[] selectResponse;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    ByteBuffer respondData;

    IntentFilter filter;

    Typeface font;
    SharedPreferences pref;

    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x48, 0x50, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideKeyboard(MainActivity.this);

        //deklarasi KEY untuk SP
        SharedPreferences prefs = getSharedPreferences("DATAPUSKES", MODE_PRIVATE);
        //default values
        String idpuskes = prefs.getString("IDPUSKES", "________");
        String namapuskes = prefs.getString("NAMAPUSKES", "________");

        //set values
        ((TextView) findViewById(R.id.txt_idPuskesmas)).setText(idpuskes);
        ((TextView) findViewById(R.id.txt_namaPuskesmas)).setText(namapuskes);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textIdPuskes);
        TextView tv4 = (TextView) findViewById(R.id.textNamaPuskes);
        tv1.setTypeface(font);
        tv2.setTypeface(font);
        tv3.setTypeface(font);
        tv4.setTypeface(font);

        if (doesDatabaseExist(getApplicationContext(),"ehealth.db"))
        {
            //Toast.makeText(this, "DB ada", Toast.LENGTH_SHORT).show();
        }
        else
        {
            copyDBEhealth();
        }
        //getHPCdata();

        i = 0;
        isCommandReceived = 0;
        respondData = ByteBuffer.allocate(102);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    //edit data Puskesmas
      public void showEdit(View view) {
      startActivity(new Intent(getApplicationContext(),Edit.class));
      }

    //cek isi data Puskesmas sebelum masukin PIN
    public void goToPin(){
        pref = getSharedPreferences("DATAPUSKES",MODE_PRIVATE);
        String idpuskes = pref.getString("IDPUSKES","________");
        String namapuskes = pref.getString("NAMAPUSKES","________");
        if(idpuskes.equals("") || namapuskes.equals("") || idpuskes.equals("________") || namapuskes.equals("________")){
//            Toast.makeText(getApplicationContext(),"DATA PUSKESMAS HARUS DIISI",Toast.LENGTH_SHORT).show();
            showToastOnUi("DATA PUSKESMAS HARUS DIISI");

        } else{
            startActivity(new Intent(getApplicationContext(),PinActivity.class));
            finish();
        }
    }

    //masuk ke Activity setting
    public void showSett(View view) {
        startActivity(new Intent(getApplicationContext(), Setting.class));
    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public void copyDBEhealth(){

        CopyDBHelper mDBHelper = new CopyDBHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        //mDBHelper.createTableKartu();
        mDBHelper.close();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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

                            send();
                        } else {
                            Log.w(TAG, "PORT NOT OPEN");
                        }
                    } else {
                        Log.w(TAG, "PORT IS NULL");
                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {

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
                    Toast.makeText(context.getApplicationContext(), "Usb devices empty", Toast.LENGTH_SHORT).show();
                }

            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                i=0;
            } else {
                Log.w(TAG, "NO INTENT?");
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
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) {
                        showToastOnUi("Koneksi applet gagal");
                    } else {
                        isCommandReceived = 1;
                        send();
                    }

                }
            } else {
                Log.e(TAG, "No i.");
            }
        }
    };

    public void send() {
        if (i == 0) {
            try {
                serialPort.write(APDU_select);
                i++;
                Log.d(TAG, "Apdu select");
                Thread.sleep(1500);
                if (isCommandReceived != 1) {
                    Log.i(TAG, "Koneksi kartu gagal");
                    showToastOnUi("Koneksi kartu gagal, silakan cabut pasang kartu.");
                } else {
                    showToastOnUi("Berhasil koneksi");
                    Log.i(TAG, "Berhasil koneksi");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            unregisterReceiver(broadcastReceiver);
            goToPin();
        }
    }

    private void showToastOnUi(String text) {
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, ftext, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


