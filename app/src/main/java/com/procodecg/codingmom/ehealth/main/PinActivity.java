package com.procodecg.codingmom.ehealth.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.goodiebag.pinview.Pinview;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;
import com.procodecg.codingmom.ehealth.utils.States;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PinActivity extends SessionManagement {

    Typeface font;
    Typeface fontbold;
    HPCActivity hpc;

    /*
     *   Komunikasi dengan Kartu
     */
    final String TAG = "HPCPDCDUMMY";
    public final String ACTION_USB_PERMISSION = "com.procodecg.codingmom.ehealth.USB_PERMISSION";
    public final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    int i, isCommandReceived;
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

    TextView attemptslefttv;
    TextView numberOfRemainingLoginAttemptstv;
    TextView textviewkali;

    Pinview pinview;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    byte[] APDU_owner_auth = {(byte) 0x80, (byte) 0xC3, 0x00, 0x00, 0x00, 0x00, 0x05, 0x31, 0x32, 0x33, 0x34, 0x35};
    // 00A4040008 48504344554D4D59
    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x48, 0x50, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    // 80D10000000000
    byte[] APDU_read_HPData = {(byte) 0x80, (byte) 0xD1, 0x00, 0x00, 0x00, 0x00, 0x00};
    // 80D20000000000
    byte[] APDU_read_cert = {(byte) 0x80, (byte) 0xD2, 0x00, 0x00, 0x00, 0x00, 0x00};

    /*
     *   Komunikasi dengan Kartu
     */

    // batas jumlah input pin salah yang diperbolehkan
    private int numberOfRemainingLoginAttempts = 3;

    @Override
    protected void onStart() {
        super.onStart();

        if (!States.CheckHPC) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PinActivity.this);
            mBuilder.setIcon(R.drawable.logo2);
            mBuilder.setTitle("Kartu yang Anda masukkan tidak dapat diakses");
            mBuilder.setMessage("Silahkan coba lagi atau masukkan kartu lain");
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent activity = new Intent(PinActivity.this, MainActivity.class);
                    startActivity(activity);
                }
            });

            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
    }


    // fungsi sembunyikan keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//  fungsi menghapus pin input yang salah
    private void clearPin(ViewGroup group)
    {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }
            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearPin((ViewGroup)view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);

        //TODO diganti
//        final TextView attemptslefttv = (TextView) findViewById(R.id.attemptsLeftTV);
//        final TextView numberOfRemainingLoginAttemptstv = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
//        final TextView textviewkali = (TextView) findViewById(R.id.textViewKali);

        attemptslefttv = (TextView) findViewById(R.id.attemptsLeftTV);
        numberOfRemainingLoginAttemptstv = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
        textviewkali = (TextView) findViewById(R.id.textViewKali);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        fontbold = Typeface.createFromAsset(getAssets(), "font1bold.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textPin);
        TextView tv2 = (TextView) findViewById(R.id.attemptsLeftTV);
        TextView tv3 = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
        TextView tv4 = (TextView) findViewById(R.id.textViewKali);
        tv1.setTypeface(font);
        tv2.setTypeface(fontbold);
        tv3.setTypeface(fontbold);
        tv4.setTypeface(fontbold);

        pinview = (Pinview) findViewById(R.id.pinView);

//        getHPCdata();

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {
                String cmd_auth = "80c30000000005" + Util.asciiToHex(pinview.getValue().toString());
                APDU_owner_auth = Util.hexStringToByteArray(cmd_auth);
                send();
////          jika pin benar
//                if (pinview.getValue().toString().equals("12345")) {
//                    Toast.makeText(PinActivity.this, "Pin Anda benar", Toast.LENGTH_SHORT).show();
//                    hideKeyboard(PinActivity.this);
//                    Intent activity = new Intent(PinActivity.this, PasiensyncActivity.class);
//                    startActivity(activity);
//                    finish();
//
////          jika pin salah
//                } else {
//
//                    clearPin((ViewGroup) pinview);
//                    pinview.clearFocus();
//                    Toast.makeText(getApplicationContext(), "PIN yang Anda masukkan salah",
//                            Toast.LENGTH_SHORT).show();
//
//                    numberOfRemainingLoginAttempts--;
//                    numberOfRemainingLoginAttemptstv.setText(Integer.toString(numberOfRemainingLoginAttempts));
//
////                  tampilkan text "Kesempatan login : x kali"
//                    attemptslefttv.setVisibility(View.VISIBLE);
//                    numberOfRemainingLoginAttemptstv.setVisibility(View.VISIBLE);
//                    textviewkali.setVisibility(View.VISIBLE);
//
////                  jika kesempatan login habis
//                    if (numberOfRemainingLoginAttempts == 0) {
//                        hideKeyboard(PinActivity.this);
////                      tampilkan dialog box alert
//                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PinActivity.this);
//                        mBuilder.setTitle(R.string.dialog_title_pin);
//                        mBuilder.setMessage(R.string.dialog_msg_pin);
//                        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                Intent activity = new Intent(PinActivity.this, MainActivity.class);
//                                startActivity(activity);
//                                finish();
//                            }
//                        });
//
//                        AlertDialog alertDialog = mBuilder.create();
//                        alertDialog.show();
//
//                    }
//                }

            }
        });

        /*
         * Komunikasi dengan kartu
         */
        i = 0;
        isCommandReceived = 0;
        respondData = ByteBuffer.allocate(102);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        connectUsbDevice();
        /*
         * Komunikasi dengan kartu
         */
    }


    /** mengambil data dari kartu HPC
     *
     */

    public void getHPCdata() {
        Log.i(TAG, "nik: " + HPCData.nik);
        Log.i(TAG, "nama: " + HPCData.nama);
        Log.i(TAG, "sip: " + HPCData.sip);

        //Boolean statusKartuHPC = true;
        String HPCnumberString = "D12345";
        String namaDokterString = "dr. Sinta";

            //Toast.makeText(this, "true ", Toast.LENGTH_SHORT).show();
            // Create database helper
            EhealthDbHelper db = new EhealthDbHelper(getApplicationContext());
            db.openDB();
            db.createTableKartu();
            //db.createTableRekMed();
            //mDbHelper.deleteAll();
            // Gets the database in write mode
            SQLiteDatabase mDbHelper = db.getWritableDatabase();

            // Create a ContentValues object where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EhealthContract.KartuEntry.COLUMN_HPCNUMBER, HPCData.nik);
//            values.put(KartuEntry.COLUMN_PIN_HPC, PIN_HPC);
            values.put(EhealthContract.KartuEntry.COLUMN_DOKTER, HPCData.nama);

            // Insert a new row in the database, returning the ID of that new row.
            long newRowId = mDbHelper.insert(EhealthContract.KartuEntry.TABLE_NAME, null, values);
            mDbHelper.close();
            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                showToastOnUi("Sinkronisasi kartu HPC GAGAL!");
//                Toast.makeText(this, "Sinkronisasi kartu HPC GAGAL!", Toast.LENGTH_SHORT).show();
                Intent activity = new Intent(PinActivity.this, MainActivity.class);
                startActivity(activity);
                finish();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                showToastOnUi("Sinkronisasi kartu HPC BERHASIL!");
//                Toast.makeText(this, "Sinkronisasi kartu HPC BERHASIL! ", Toast.LENGTH_SHORT).show();
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
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) {
                        showToastOnUi("Koneksi applet gagal");
                    } else {
                        isCommandReceived = 1;
                    }

                }
            } else if (i == 2) {
                respondData.put(bytes);

                if (respondData.position() == 2) {
                    authResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(authResponse);
                    respondData.position(0);

                    Log.i(TAG, "Auth response string: " + Util.bytesToHex(authResponse));
                    if (!Util.bytesToHex(authResponse).toString().equals("9000")) {
                        showToastOnUi("PIN SALAH");
                        i--;
                        //TODO pin salah handler, buat di thread ui
                        clearPinUi();
                    } else { // pin berhasil
                        send();
                    }
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
            try {
                serialPort.write(APDU_select);
                i++;
                Log.d(TAG, "Apdu select");
                Thread.sleep(1500);
                if (isCommandReceived != 1) {
                    Log.i(TAG, "Koneksi kartu gagal");
                    showToastOnUi("Koneksi kartu gagal, silakan cabut pasang kartu.");
                    unregisterReceiver(broadcastReceiver);
                    Intent activity = new Intent(PinActivity.this, WelcomeActivity.class);
                    startActivity(activity);
                } else {
                    showToastOnUi("Berhasil koneksi");
                    Log.i(TAG, "Berhasil koneksi");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (i == 1) {
            serialPort.write(APDU_owner_auth);
            i++;
            Log.d(TAG, "Apdu owner auth");
        } else if (i == 2) {
            serialPort.write(APDU_read_HPData);
            i++;
            Log.d(TAG, "Apdu read hp data");
        } else if (i == 3) {
            serialPort.write(APDU_read_cert);
            i++;
            Log.d(TAG, "Apdu read cert");
        } else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            unregisterReceiver(broadcastReceiver);
            getHPCdata();
            hideKeyboard(PinActivity.this);
            Intent activity = new Intent(PinActivity.this, PasiensyncActivity.class);
            startActivity(activity);
            finish();
        }
    }

    //TODO lakukan ini di thread ui, serial port close, unreg receiver?
    private void clearPinUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearPin(pinview);
                pinview.clearFocus();

                numberOfRemainingLoginAttempts--;
                numberOfRemainingLoginAttemptstv.setText(Integer.toString(numberOfRemainingLoginAttempts));

//                  tampilkan text "Kesempatan login : x kali"
                attemptslefttv.setVisibility(View.VISIBLE);
                numberOfRemainingLoginAttemptstv.setVisibility(View.VISIBLE);
                textviewkali.setVisibility(View.VISIBLE);

//                  jika kesempatan login habis
                if (numberOfRemainingLoginAttempts == 0) {
                    serialPort.close();
                    hideKeyboard(PinActivity.this);
//                      tampilkan dialog box alert
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(PinActivity.this);
                    mBuilder.setTitle(R.string.dialog_title_pin);
                    mBuilder.setMessage(R.string.dialog_msg_pin);
                    mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent activity = new Intent(PinActivity.this, MainActivity.class);
                            startActivity(activity);
                            finish();
                        }
                    });

                    AlertDialog alertDialog = mBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void showToastOnUi(String text) {
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PinActivity.this, ftext, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectUsbDevice() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                usbDevice = entry.getValue();
                int deviceID = usbDevice.getVendorId();
                if (deviceID == 1027 || deviceID == 9025) {
                    Log.d(TAG, "Device ID " + deviceID);
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
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

}
