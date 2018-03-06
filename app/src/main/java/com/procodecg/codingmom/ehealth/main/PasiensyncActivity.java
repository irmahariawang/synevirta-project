package com.procodecg.codingmom.ehealth.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract.KartuEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecStatikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCDataActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by macbookpro on 7/27/17.
 */

public class PasiensyncActivity extends AppCompatActivity {

    Typeface font;
    Typeface fontbold;
    PDCDataActivity pdc;

    public static final int SELECTED_PICTURE = 1;
    //rivate static String currentNamaDokter;

    ImageView iv;
    private static String currentHPCNumber;
    public static String currentNamaDokter;

    /*
     * Komunikasi dengan kartu
     */
    final String TAG = "HPCPDCDUMMY";
    public final String ACTION_USB_PERMISSION = "com.nehceh.hpcpdc.USB_PERMISSION";

    String data;
    int i; // buat increment serial tulis apdu

    ByteBuffer respondData;
    IntentFilter filter;

    byte[] selectResponse;
    byte[] medrecStatikResponse;
    byte[] biodataResponse;

    MedrecStatikData msd;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_read_medrec_statik = {(byte) 0x80, (byte) 0xD4, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_biodata = {(byte) 0x80, (byte) 0xD3, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik1 = {(byte) 0x80, (byte) 0xD5, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik2 = {(byte) 0x80, (byte) 0xD5, 0x00, 0x01, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik3 = {(byte) 0x80, (byte) 0xD5, 0x00, 0x02, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik4 = {(byte) 0x80, (byte) 0xD5, 0x00, 0x03, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik5 = {(byte) 0x80, (byte) 0xD5, 0x00, 0x04, 0x00, 0x00, 0x00};
    /*
     * Komunikasi dengan kartu
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pasiensync);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        fontbold = Typeface.createFromAsset(getAssets(), "font1bold.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textNamaDokter);
        tv1.setTypeface(fontbold);
        tv2.setTypeface(font);
        tv3.setTypeface(fontbold);

        // Komunikasi dengan kartu
        i = 0;

        respondData = ByteBuffer.allocate(2469);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        // Komunikasi dengan kartu
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayNamaDokter();
    }

    public void goToProfil(View v) {
//        pdc = new PDCDataActivity(getApplicationContext());

        Intent activity = new Intent(this, BottombarActivity.class);
        startActivity(activity);
        finish();
    }

    //utk UPLOAD PHOTO
    public void imgClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    ImageView iv = (ImageView) findViewById(R.id.imageView);

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    Drawable d = new BitmapDrawable(yourSelectedImage);
                    iv.setBackground(d);
                }
        }
    }

    ;

    //utk DISPLAY NAMA DOKTER
    private void displayNamaDokter() {
        // Create and/or open a database to read from it
        EhealthDbHelper mDbHelper = new EhealthDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final TextView textNamaDoktertv = (TextView) findViewById(R.id.textNamaDokter);

        String[] projection = {
                KartuEntry.COLUMN_HPCNUMBER,
                KartuEntry.COLUMN_DOKTER,
                //KartuEntry.COLUMN_PIN_HPC,
        };

        Cursor cursor = db.query(KartuEntry.TABLE_NAME, projection, null, null, null, null, null);
        // TextView displayView = (TextView) findViewById(R.id.text_view_kartu);
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        // Cursor cursor = db.rawQuery("SELECT * FROM " + KartuEntry.TABLE_NAME, null);

        try {

            // Figure out the index of each column
            // int idColumnIndex = cursor.getColumnIndex(KartuEntry._ID);
            int HPCnumberColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_HPCNUMBER);
            int namaDokterColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_DOKTER);
            //int pinHPCColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_PIN_HPC);
            //int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Iterate through all the returned rows in the cursor

            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                // int currentID = cursor.getInt(idColumnIndex);
                currentHPCNumber = cursor.getString(HPCnumberColumnIndex);
                currentNamaDokter = cursor.getString(namaDokterColumnIndex);
                //String currentPinHPC = cursor.getString(pinHPCColumnIndex);
//              Menampilkan nama dokter
                textNamaDoktertv.setText(currentNamaDokter);
                textNamaDoktertv.setVisibility(View.VISIBLE);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    public static String getNamaDokter() {
        return currentNamaDokter;
    }


//    //SINKRONISASI TIDAK BERHASIL
//    Button mShowDialog = (Button) findViewById(R.id.btnShowDialog);
//        mShowDialog.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PasiensyncActivity.this);
//            mBuilder.setIcon(R.drawable.logo2);
//            mBuilder.setTitle("Kartu pasien tidak dapat diakses");
//            mBuilder.setMessage("Silahkan coba lagi");
//            mBuilder.setCancelable(false);
//            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialogInterface, int i) {
//            dialogInterface.dismiss();
//            }
//            });
//            AlertDialog alertDialog = mBuilder.create();
//            alertDialog.show();
//        }
//        });
//        }
//        }

    /*
     * Komunikasi dengan kartu
     */

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context.getApplicationContext(), "broadcastReceiver in", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context.getApplicationContext(), "Serial connection opened!", Toast.LENGTH_SHORT).show();

//                            try {
//                                send();
//                                Thread.sleep(2000);
//                                send();
//                                Thread.sleep(6000);
//                                send();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
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
                Toast.makeText(getApplicationContext(), "Usb device attached", Toast.LENGTH_SHORT).show();

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

//            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
//                Toast.makeText(getApplicationContext(), "Usb device detached", Toast.LENGTH_SHORT).show();
//                i = 0;
            } else {
                Log.w(TAG, "NO INTENT?");
            }
        }
    };

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = Util.bytesToHex(bytes);
            Log.d(TAG, "Data " + data);

            if (i == 1) { //select
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Select response string: " + Util.bytesToHex(selectResponse));
                    send();
                }
            } else if (i == 2) { //medrec statik
                respondData.put(bytes);
                byte golodar;
                byte[] al, operasi, rawatrs, kronis, bawaan, resiko;
                if (respondData.position() == 1390) {
                    medrecStatikResponse = new byte[1390];
                    respondData.rewind();
                    respondData.get(medrecStatikResponse);
                    respondData.position(0);

                    Log.i(TAG, "Medrec statik string: " + Util.bytesToHex(medrecStatikResponse));

                    golodar = medrecStatikResponse[0];
                    int g = golodar;
                    al = Arrays.copyOfRange(medrecStatikResponse, 1, 101);
                    operasi = Arrays.copyOfRange(medrecStatikResponse, 103, 358);
                    rawatrs = Arrays.copyOfRange(medrecStatikResponse, 360, 615);
                    kronis = Arrays.copyOfRange(medrecStatikResponse, 617, 872);
                    bawaan = Arrays.copyOfRange(medrecStatikResponse, 874, 1129);
                    resiko = Arrays.copyOfRange(medrecStatikResponse, 1131, 1386);
/*
                    tvSet(goldar, Integer.toString(g));
                    tvSet(alergi, Util.bytesToString(Util.trimZeroPadding(al)));
                    tvSet(riwayatOperasi, Util.bytesToString(Util.trimZeroPadding(operasi)));
                    tvSet(riwayatRawat, Util.bytesToString(Util.trimZeroPadding(rawatrs)));
                    tvSet(riwayatPenyakitKronis, Util.bytesToString(Util.trimZeroPadding(kronis)));
                    tvSet(riwayatPenyakitBawaan, Util.bytesToString(Util.trimZeroPadding(bawaan)));
                    tvSet(faktorRisiko, Util.bytesToString(Util.trimZeroPadding(resiko)));
*/
                    Log.i(TAG, "Goldar: " + g);
                    Log.i(TAG, "alergi: " + Util.bytesToString(Util.trimZeroPadding(al)));
                    Log.i(TAG, "operasi: " + Util.bytesToString(Util.trimZeroPadding(operasi)));
                    Log.i(TAG, "rawat rs: " + Util.bytesToString(Util.trimZeroPadding(rawatrs)));
                    Log.i(TAG, "kronis: " + Util.bytesToString(Util.trimZeroPadding(kronis)));
                    Log.i(TAG, "bawaan: " + Util.bytesToString(Util.trimZeroPadding(bawaan)));
                    Log.i(TAG, "resiko: " + Util.bytesToString(Util.trimZeroPadding(resiko)));
                    send();
                }
            } else if (i == 3) { // biodata
                respondData.put(bytes);
                if (respondData.position() == 826) {
                    byte[] nik, kategoriPasien, noAsuransi, tglDaftar, namaPasien, namaKK,
                            hubunganKeluarga, alamat, rt, rw, kelurahanDesa, kecamatan, kotaKabupaten, provinsi, kodepos,
                            isDalamWilayahKerja, tempatLahir, tglLahir, telepon, hp, jenisKelamin, agama, pendidikan,
                            pekerjaan, kelasPerawatan, email, statusPernikahan, kewarganegaraan, namaKerabat,
                            hubunganKerabat, alamatKerabat, kelurahanDesaKerabat, kecamatanKerabat, kotaKabupatenKerabat,
                            provinsiKerabat, kodeposKerabat, teleponKerabat, hpKerabat, namaKantor, alamatKantor,
                            kotaKabupatenKantor, teleponKantor, hpKantor;
                    biodataResponse = new byte[826];
                    respondData.rewind();
                    respondData.get(biodataResponse);
                    respondData.position(0);

                    Log.i(TAG, "Biodata: " + Util.bytesToHex(biodataResponse));

                    nik = Arrays.copyOfRange(biodataResponse, 0, 16);
                    kategoriPasien = Arrays.copyOfRange(biodataResponse, 16, 68);
                    noAsuransi = Arrays.copyOfRange(biodataResponse, 68, 150);
                    tglDaftar = Arrays.copyOfRange(biodataResponse, 150, 154);
                    namaPasien = Arrays.copyOfRange(biodataResponse, 154, 206);
                    namaKK = Arrays.copyOfRange(biodataResponse, 206, 258);
                    hubunganKeluarga = Arrays.copyOfRange(biodataResponse, 258, 259);
                    alamat = Arrays.copyOfRange(biodataResponse, 259, 361);
                    rt = Arrays.copyOfRange(biodataResponse, 361, 362);
                    rw = Arrays.copyOfRange(biodataResponse, 362, 363);
                    kelurahanDesa = Arrays.copyOfRange(biodataResponse, 363, 365);
                    kecamatan = Arrays.copyOfRange(biodataResponse, 365, 366);
                    kotaKabupaten = Arrays.copyOfRange(biodataResponse, 366, 367);
                    provinsi = Arrays.copyOfRange(biodataResponse, 367, 368);
                    kodepos = Arrays.copyOfRange(biodataResponse, 368, 371);
                    isDalamWilayahKerja = Arrays.copyOfRange(biodataResponse, 371, 372);
                    tempatLahir = Arrays.copyOfRange(biodataResponse, 372, 394);
                    tglLahir = Arrays.copyOfRange(biodataResponse, 394, 398);
                    telepon = Arrays.copyOfRange(biodataResponse, 398, 416);
                    hp = Arrays.copyOfRange(biodataResponse, 416, 434);
                    jenisKelamin = Arrays.copyOfRange(biodataResponse, 434, 435);
                    agama = Arrays.copyOfRange(biodataResponse, 435, 436);
                    pendidikan = Arrays.copyOfRange(biodataResponse, 436, 437);
                    pekerjaan = Arrays.copyOfRange(biodataResponse, 437, 438);
                    kelasPerawatan = Arrays.copyOfRange(biodataResponse, 438, 440);
                    email = Arrays.copyOfRange(biodataResponse, 440, 462);
                    statusPernikahan = Arrays.copyOfRange(biodataResponse, 462, 463);
                    kewarganegaraan = Arrays.copyOfRange(biodataResponse, 463, 464);

                    namaKerabat = Arrays.copyOfRange(biodataResponse, 464, 516);
                    hubunganKerabat = Arrays.copyOfRange(biodataResponse, 516, 517);
                    alamatKerabat = Arrays.copyOfRange(biodataResponse, 517, 619);
                    kelurahanDesaKerabat = Arrays.copyOfRange(biodataResponse, 619, 621);
                    kecamatanKerabat = Arrays.copyOfRange(biodataResponse, 621, 622);
                    kotaKabupatenKerabat = Arrays.copyOfRange(biodataResponse, 622, 623);
                    provinsiKerabat = Arrays.copyOfRange(biodataResponse, 623, 624);
                    kodeposKerabat = Arrays.copyOfRange(biodataResponse, 624, 627);
                    teleponKerabat = Arrays.copyOfRange(biodataResponse, 627, 645);
                    hpKerabat = Arrays.copyOfRange(biodataResponse, 645, 663);
                    namaKantor = Arrays.copyOfRange(biodataResponse, 663, 685);
                    alamatKantor = Arrays.copyOfRange(biodataResponse, 685, 787);
                    kotaKabupatenKantor = Arrays.copyOfRange(biodataResponse, 787, 788);
                    teleponKantor = Arrays.copyOfRange(biodataResponse, 788, 806);
                    hpKantor = Arrays.copyOfRange(biodataResponse, 806, 822);

                    PDCData.nik = Util.bytesToString(Util.trimZeroPadding(nik));
                    PDCData.kategoriPasien = Util.bytesToString(Util.trimZeroPadding(kategoriPasien));
                    PDCData.namaPasien = Util.bytesToString(Util.trimZeroPadding(namaPasien));
                    PDCData.tglLahir = Util.getformattedDate(Util.bytestoDate(tglLahir));
                    PDCData.jenisKelamin = Util.bytesToString(jenisKelamin);

                    send();
                }
            }
            else {
                Log.i(TAG, "no i " + i);
            }
        }
    };

    public void send() {
        if ( i == 0 ) {
            serialPort.write(APDU_select);
            i++;
            Log.i(TAG, "write apdu select");
        } else if (i == 1) {
            serialPort.write(APDU_read_medrec_statik);
            i++;
            Log.i(TAG, "write apdu read medrec statik");
        } else if (i == 2) {
            serialPort.write(APDU_read_biodata);
            i++;
            Log.i(TAG, "write apdu read biodata");
        }
        else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            unregisterReceiver(broadcastReceiver);
        }

    }

}