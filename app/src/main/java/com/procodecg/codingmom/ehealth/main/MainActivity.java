package com.procodecg.codingmom.ehealth.main;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedbaruActivity;
import com.procodecg.codingmom.ehealth.utils.Setting;
import com.procodecg.codingmom.ehealth.data.CopyDBHelper;
import com.procodecg.codingmom.ehealth.data.CopyWilayahDBHelper;
import com.procodecg.codingmom.ehealth.utils.Edit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.procodecg.codingmom.ehealth.main.PinActivity.hideKeyboard;

/**
 * (c) 2017
 * Created by :
 *      Coding Mom
 *      Annisa Alifiani
 */

public class MainActivity extends AppCompatActivity {

    private static long back_pressed;

    // TAG
    final String TAG = "EHEALTHMAIN";
    public final String ACTION_USB_PERMISSION = "com.procodecg.codingmom.ehealth.USB_PERMISSION";

    // Variable
    int i, isCommandReceived;
    String data;

    // View
    Typeface font;
    SharedPreferences pref;
    TextView tv1, tv2, tv3, tv4;

    // USB Accessories
    byte[] selectResponse, cardChecking;
    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;
    ByteBuffer respondData;
    IntentFilter filter;

    // APDU Command
    byte[] APDUSelect = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x48, 0x50, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDUCardCheck = {(byte) 0x80, (byte) 0xE1, 0x00, 0x00, 0x00, 0x00, 0x00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideKeyboard(MainActivity.this);

        // File penyimpanan data puskesmas
        SharedPreferences prefs = getSharedPreferences("DATAPUSKES", MODE_PRIVATE);
        String idpuskes = prefs.getString("IDPUSKES", "________");
        String namapuskes = prefs.getString("NAMAPUSKES", "________");

        ((TextView) findViewById(R.id.txt_idPuskesmas)).setText(idpuskes);
        ((TextView) findViewById(R.id.txt_namaPuskesmas)).setText(namapuskes);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textIdPuskes);
        tv4 = (TextView) findViewById(R.id.textNamaPuskes);
        tv1.setTypeface(font);
        tv2.setTypeface(font);
        tv3.setTypeface(font);
        tv4.setTypeface(font);

        // Pengecekan db awal
        if (!doesDatabaseExist(getApplicationContext(),"ehealth.db") && doesDatabaseExist(getApplicationContext(),"kode_wilayah.db")) {
            copyDBEhealth();
            Log.i(TAG, "create db ehealth");
            Toast.makeText(MainActivity.this, "create db ehealth", Toast.LENGTH_LONG).show();
        } else if (doesDatabaseExist(getApplicationContext(),"ehealth.db") && !doesDatabaseExist(getApplicationContext(),"kode_wilayah.db")) {
            copyDBWilayah();
            Log.i(TAG, "create db wilayah");
            Toast.makeText(MainActivity.this, "create db wilayah", Toast.LENGTH_LONG).show();
        } else if (!doesDatabaseExist(getApplicationContext(),"ehealth.db") && !doesDatabaseExist(getApplicationContext(),"kode_wilayah.db")) {
            copyDBEhealth();
            copyDBWilayah();
            Log.i(TAG, "create db ehealth & wilayah");
            Toast.makeText(MainActivity.this, "create all db", Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "all db exist");
            Toast.makeText(MainActivity.this, "all db exist", Toast.LENGTH_LONG).show();
        }

        // USB Accessories
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

    // Edit data puskesmas
    public void showEdit(View view) {
      startActivity(new Intent(getApplicationContext(),Edit.class));
    }

    // Pengecekan data puskesmas, data puskesmas harus diisi untuk lanjut ke PinActivity
    public void goToPin(){
        pref = getSharedPreferences("DATAPUSKES",MODE_PRIVATE);
        String idpuskes = pref.getString("IDPUSKES","________");
        String namapuskes = pref.getString("NAMAPUSKES","________");
        if(idpuskes.equals("") || namapuskes.equals("") || idpuskes.equals("________") || namapuskes.equals("________")){
            setTextView("DATA PUSKESMAS HARUS DIISI");
        } else{
            startActivity(new Intent(getApplicationContext(),PinActivity.class));
            finish();
        }
    }

    // Masuk ke Setting
    public void showSett(View view) {
        startActivity(new Intent(getApplicationContext(), Setting.class));
    }

    // Pengecekan db
    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    // Copy ehealth.db : kode icd
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

        mDBHelper.close();
    }

    // Copy wilayah.db : master kode wilayah provinsi, kabupaten kota, kecamatan, kelurahan desa
    public void copyDBWilayah(){
        CopyWilayahDBHelper wDBHelper = new CopyWilayahDBHelper(this);

        try {
            wDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            SQLiteDatabase mDb = wDBHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        wDBHelper.close();
    }

    // Broadcast USB untuk mendeteksi kartu pada card reader
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
                        setTextView("Port in Null\nSilahkan cabut pasang kembali kartu");
                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {

                setTextView("Pengecekan kartu");

                // Connect usb device
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
                    setTextView("USB Device Empty\nSilahkan cabut pasang kembali kartu");
                }

            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                i=0;
                setTextView("Masukkan kartu HPC Anda");
            } else {
                Log.w(TAG, "NO INTENT?");
            }
        }
    };

    // Membaca APDU response
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        // triggers whenever data is read
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = null;
            data = Util.bytesToHex(bytes);

            Log.d(TAG, "Data " + data);
            Log.d(TAG, "i: " + i);

            if (i == 1) { // Select Applet
                respondData.put(bytes);

                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Select response string: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) {
                        setTextView("Koneksi applet gagal\nSilahkan masukan kartu yang lain");
                    } else {
                        isCommandReceived = 1;
                        send();
                    }
                }
            } else if (i == 2) { // Cek personalized status
                respondData.put(bytes);

                if(respondData.position() == 3){
                    cardChecking = new byte[3];
                    respondData.rewind();
                    respondData.get(cardChecking);
                    respondData.position(0);

                    Log.i(TAG, "Cert response string: " + Util.bytesToHex(cardChecking));
                    if (Util.bytesToHex(Arrays.copyOfRange(cardChecking, 0, 1)).equals("11")){
                        send();
                    } else {
                        setTextView("HPC belum dipersonalisasi \nSilahkan masukan HPC lain");
                    }
                }
            } else {
                Log.e(TAG, "No i.");
            }
        }
    };

    // Kirim APDU command
    public void send() {
        if (i == 0) {
            try {
                serialPort.write(APDUSelect);
                i++;
                Log.d(TAG, "Apdu select");
                Thread.sleep(1500);
                if (isCommandReceived != 1) {
                    setTextView("Koneksi kartu gagal\nSilahkan cabut pasang kartu");
                    Log.i(TAG, "Koneksi kartu gagal");
                } else {
                    Log.i(TAG, "Berhasil koneksi");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (i == 1){
            serialPort.write(APDUCardCheck);
            i++;
            Log.d(TAG, "Apdu cert");
        } else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            unregisterReceiver(broadcastReceiver);
            goToPin();
        }
    }

    // Set text
    private void setTextView(final String text) {
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv2.setText(text);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (back_pressed + 2000 > System.currentTimeMillis()){
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Tekan lagi untuk keluar aplikasi", Toast.LENGTH_SHORT).show();
        }

        back_pressed = System.currentTimeMillis();
    }
}


