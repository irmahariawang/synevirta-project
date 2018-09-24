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

public class MainActivity extends AppCompatActivity {

    private static long back_pressed;

    final String TAG = "HPCPDCDUMMY";
    public final String ACTION_USB_PERMISSION = "com.procodecg.codingmom.ehealth.USB_PERMISSION";

    int i, isCommandReceived;
    String data;
    byte[] selectResponse, cardChecking;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    ByteBuffer respondData;

    IntentFilter filter;

    Typeface font;
    SharedPreferences pref;

    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x48, 0x50, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_card_check = {(byte) 0x80, (byte) 0xE1, 0x00, 0x00, 0x00, 0x00, 0x00};

    TextView tv1, tv2, tv3, tv4;

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
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textIdPuskes);
        tv4 = (TextView) findViewById(R.id.textNamaPuskes);
        tv1.setTypeface(font);
        tv2.setTypeface(font);
        tv3.setTypeface(font);
        tv4.setTypeface(font);

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
            setTextView("DATA PUSKESMAS HARUS DIISI");

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
        //mDBHelper.createTableKartu();
        wDBHelper.close();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent.getAction() " + intent.getAction());

            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {

//                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
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
                        setTextView("Port in Null\nSilahkan cabut pasang kemabli kartu");
                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {

                setTextView("Pengecekan kartu");

                // connect usb device
                HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
                if (!usbDevices.isEmpty()) {
                    boolean keep = true;
                    for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                        usbDevice = entry.getValue();
                        int deviceID = usbDevice.getVendorId();
                        if (deviceID == 1027 || deviceID == 9025) {
//                            if(!usbManager.hasPermission(usbDevice)) {
                                Log.d(TAG, "Device ID " + deviceID);
                                PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                                usbManager.requestPermission(usbDevice, pi);
                                keep = false;
//                            }
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
                        setTextView("Koneksi applet gagal\nSilahkan masukan kartu yang lain");
                    } else {
                        isCommandReceived = 1;
                        send();
                    }

                }
            } else if (i == 2) {
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

    public void send() {
        if (i == 0) {
            try {
                serialPort.write(APDU_select);
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
            serialPort.write(APDU_card_check);
            i++;
            Log.d(TAG, "Apdu cert");
        } else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            unregisterReceiver(broadcastReceiver);
            goToPin();
        }
    }

    private void setTextView(final String text) {
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainActivity.this, ftext, Toast.LENGTH_SHORT).show();
                tv2.setText(text);
            }
        });
    }

    private boolean responseVerifier(String response){
        Pattern pattern = Pattern.compile("[1-9]");
        Matcher matcher = pattern.matcher(response);

        return matcher.find();
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


