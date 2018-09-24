package com.procodecg.codingmom.ehealth.main;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract.KartuEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.data.WilayahDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecDinamikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecStatikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCDataActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData.nama;

/**
 * Created by macbookpro on 7/27/17.
 */

public class PasiensyncActivity extends SessionManagement {

    Boolean personalized;

    TextView tv1, tv2, tv3;

    Typeface font;
    Typeface fontbold;
    ProgressBar progressBar;
    PDCDataActivity pdc;

    private static long back_pressed;

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
    int isCommandReceived;

    int progressStatus = 0;
    Handler handler = new Handler();

    ByteBuffer respondData;
    IntentFilter filter;

    EhealthDbHelper mDbHelper;
    WilayahDbHelper wDbHelper;

    byte[] selectResponse, cardCheckingResponse, medrecStatikResponse, biodataResponse;

    MedrecStatikData msd;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_card_checking = {(byte)0x80, (byte)0xB1, 0x00, 0x00, 0x00, 0x00, 0x00};
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
        mDbHelper = new EhealthDbHelper(this);
        mDbHelper.openDB();
        wDbHelper = new WilayahDbHelper(this);
        wDbHelper.openDB();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pasiensync);
        personalized = true;

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        fontbold = Typeface.createFromAsset(getAssets(), "font1bold.ttf");
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textNamaDokter);
        tv1.setTypeface(fontbold);
        tv2.setTypeface(font);
        tv3.setTypeface(fontbold);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Komunikasi dengan kartu
        i = 0;
        isCommandReceived = 0;

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
                        setTextView("Port is Null\nSilahkan cabut pasang kembali kartu");
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
                setTextView("Silahkan masukkan kartu PDC Pasien");
                i = 0;
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
                    if (Util.bytesToHex(selectResponse).toString().equals("9000")) {
                        isCommandReceived = 1;
                        send();
                    }
                }
            } else if (i == 2) { //select
                respondData.put(bytes);
                if (respondData.position() == 3) {
                    cardCheckingResponse = new byte[3];
                    respondData.rewind();
                    respondData.get(cardCheckingResponse);
                    respondData.position(0);

                    Log.i(TAG, "Select response string: " + Util.bytesToHex(cardCheckingResponse));
                    if (Util.bytesToHex(Arrays.copyOfRange(cardCheckingResponse, 0, 1)).toString().equals("11")
                            || Util.bytesToHex(Arrays.copyOfRange(cardCheckingResponse, 0, 1)).toString().equals("01")) {
//                        progressStatus = 0;
                        i = 2;
                    } else {
                        personalized = false;
                        i = 4;
                    }

                    send();
                }
            } else if (i == 3) { //medrec statik
                respondData.put(bytes);
                byte golodar;
                byte[] al, operasi, rawatrs, kronis, bawaan, resiko;
                Log.i(TAG, "Medrec statik length : " + respondData.position());
                if (respondData.position() == 1105) {
                    medrecStatikResponse = new byte[1105];
                    respondData.rewind();
                    respondData.get(medrecStatikResponse);
                    respondData.position(0);

                    Log.i(TAG, "Medrec statik string: " + Util.bytesToHex(medrecStatikResponse));
                    if(responseVerifier(Util.bytesToHex(Arrays.copyOfRange(medrecStatikResponse, 1, 1103)))) {
                        golodar = medrecStatikResponse[0];
                        int g = golodar;
                        setProgressBar(1);
                        al = Arrays.copyOfRange(medrecStatikResponse, 1, 102);
                        setProgressBar(2);
                        operasi = Arrays.copyOfRange(medrecStatikResponse, 103, 303);
                        setProgressBar(3);
                        rawatrs = Arrays.copyOfRange(medrecStatikResponse, 303, 503);
                        setProgressBar(4);
                        kronis = Arrays.copyOfRange(medrecStatikResponse, 503, 703);
                        setProgressBar(5);
                        bawaan = Arrays.copyOfRange(medrecStatikResponse, 703, 903);
                        setProgressBar(6);
                        resiko = Arrays.copyOfRange(medrecStatikResponse, 903, 1103);
                        setProgressBar(7);

                        Log.i(TAG, "Goldar: " + g);
                        Log.i(TAG, "alergi: " + Util.bytesToString(Util.trimZeroPadding(al)));
                        Log.i(TAG, "operasi: " + Util.bytesToString(Util.trimZeroPadding(operasi)));
                        Log.i(TAG, "rawat rs: " + Util.bytesToString(Util.trimZeroPadding(rawatrs)));
                        Log.i(TAG, "kronis: " + Util.bytesToString(Util.trimZeroPadding(kronis)));
                        Log.i(TAG, "bawaan: " + Util.bytesToString(Util.trimZeroPadding(bawaan)));
                        Log.i(TAG, "resiko: " + Util.bytesToString(Util.trimZeroPadding(resiko)));
                    }

                    send();
                }
            } else if (i == 4) { // biodata
                respondData.put(bytes);
                Log.i(TAG, "Biodata statik length : " + respondData.position());
                if (respondData.position() == 951) {
                    byte[] nik, kategoriPasien, noAsuransi, tglDaftar, namaPasien, namaKK,
                            hubunganKeluarga, alamat, rt, rw, kelurahanDesa, kecamatan, kotaKabupaten, provinsi, kodepos,
                            isDalamWilayahKerja, tempatLahir, tglLahir, telepon, hp, jenisKelamin, agama, pendidikan,
                            pekerjaan, kelasPerawatan, email, statusPernikahan, kewarganegaraan, namaKerabat,
                            hubunganKerabat, alamatKerabat, kelurahanDesaKerabat, kecamatanKerabat, kotaKabupatenKerabat,
                            provinsiKerabat, kodeposKerabat, teleponKerabat, hpKerabat, namaKantor, alamatKantor,
                            kotaKabupatenKantor, teleponKantor, hpKantor;
                    biodataResponse = new byte[951];
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
                    rt = Arrays.copyOfRange(biodataResponse, 361, 365);
                    rw = Arrays.copyOfRange(biodataResponse, 365, 369);
                    kelurahanDesa = Arrays.copyOfRange(biodataResponse, 369, 379);
                    kecamatan = Arrays.copyOfRange(biodataResponse, 379, 386);
                    kotaKabupaten = Arrays.copyOfRange(biodataResponse, 386, 390);
                    provinsi = Arrays.copyOfRange(biodataResponse, 390, 392);
                    kodepos = Arrays.copyOfRange(biodataResponse, 392, 397);
                    isDalamWilayahKerja = Arrays.copyOfRange(biodataResponse, 397, 398);
                    tempatLahir = Arrays.copyOfRange(biodataResponse, 398, 420);
                    tglLahir = Arrays.copyOfRange(biodataResponse, 420, 424);
                    telepon = Arrays.copyOfRange(biodataResponse, 424, 440);
                    hp = Arrays.copyOfRange(biodataResponse, 440, 456);
                    jenisKelamin = Arrays.copyOfRange(biodataResponse, 456, 457);
                    agama = Arrays.copyOfRange(biodataResponse, 457, 458);
                    pendidikan = Arrays.copyOfRange(biodataResponse, 458, 459);
                    pekerjaan = Arrays.copyOfRange(biodataResponse, 459, 460);
                    kelasPerawatan = Arrays.copyOfRange(biodataResponse, 460, 462);
                    email = Arrays.copyOfRange(biodataResponse, 462, 514);
                    statusPernikahan = Arrays.copyOfRange(biodataResponse, 514, 515);
                    kewarganegaraan = Arrays.copyOfRange(biodataResponse, 515, 516);

                    namaKerabat = Arrays.copyOfRange(biodataResponse, 516, 568);
                    hubunganKerabat = Arrays.copyOfRange(biodataResponse, 568, 569);
                    alamatKerabat = Arrays.copyOfRange(biodataResponse, 569, 671);
                    kelurahanDesaKerabat = Arrays.copyOfRange(biodataResponse, 671, 681);
                    kecamatanKerabat = Arrays.copyOfRange(biodataResponse, 681, 688);
                    kotaKabupatenKerabat = Arrays.copyOfRange(biodataResponse, 688, 692);
                    provinsiKerabat = Arrays.copyOfRange(biodataResponse, 692, 694);
                    kodeposKerabat = Arrays.copyOfRange(biodataResponse, 694, 699);
                    teleponKerabat = Arrays.copyOfRange(biodataResponse, 699, 715);
                    hpKerabat = Arrays.copyOfRange(biodataResponse, 715, 731);
                    namaKantor = Arrays.copyOfRange(biodataResponse, 731, 783);
                    alamatKantor = Arrays.copyOfRange(biodataResponse, 783, 885);
                    kotaKabupatenKantor = Arrays.copyOfRange(biodataResponse, 885, 889);
                    teleponKantor = Arrays.copyOfRange(biodataResponse, 889, 905);
                    hpKantor = Arrays.copyOfRange(biodataResponse, 905, 921);

                    SQLiteDatabase sql = wDbHelper.getReadableDatabase();

                    PDCData.nik = Util.bytesToString(Util.trimZeroPadding(nik));
                    setProgressBar(8);
                    PDCData.kategoriPasien = Util.bytesToString(Util.trimZeroPadding(kategoriPasien));
                    setProgressBar(9);
                    PDCData.noAsuransi = Util.bytesToString(noAsuransi);
                    setProgressBar(10);
                    PDCData.tglDaftar = Util.bytesToDate(tglDaftar);
                    setProgressBar(11);
                    PDCData.namaPasien = Util.bytesToString(Util.trimZeroPadding(namaPasien));
                    setProgressBar(12);
                    PDCData.namaKK = Util.bytesToString(Util.trimZeroPadding(namaKK));
                    setProgressBar(13);
                    PDCData.hubunganKeluarga = Util.bytesToString(hubunganKeluarga);
                    setProgressBar(14);
                    PDCData.alamat = Util.bytesToString(Util.trimZeroPadding(alamat));
                    setProgressBar(15);
                    PDCData.rt = Util.bytesToString(rt);
                    setProgressBar(16);
                    PDCData.rw = Util.bytesToString(rw);
                    setProgressBar(17);
                    Cursor kd_kelurahan = sql.query("mst_kelurahan", new String[]{"KELURAHAN"}, "KD_KELURAHAN = ?", new String[]{Util.bytesToString(kelurahanDesa)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.kelurahanDesa = kd_kelurahan.getString(0);
                    } else {
                        PDCData.kelurahanDesa = "Kode kelurahan tidak dikenali";
                    }
                    setProgressBar(18);
                    Cursor kd_kecamatan = sql.query("mst_kecamatan", new String[]{"KECAMATAN"}, "KD_KECAMATAN = ?", new String[]{Util.bytesToString(kecamatan)}, null, null, null);
                    if(kd_kecamatan.moveToFirst()) {
                        PDCData.kecamatan = kd_kecamatan.getString(0);
                    } else {
                        PDCData.kecamatan = "Kode kecamatan tidak dikenali";
                    }
                    setProgressBar(19);
                    Cursor kd_kabupaten = sql.query("mst_kabupaten", new String[]{"KABUPATEN"}, "KD_KABUPATEN = ?", new String[]{Util.bytesToString(kotaKabupaten)}, null, null, null);
                    if(kd_kabupaten.moveToFirst()) {
                        PDCData.kotaKabupaten = kd_kabupaten.getString(0);
                    } else {
                        PDCData.kotaKabupaten = "Kode kabupaten tidak dikenali";
                    }
                    setProgressBar(20);
                    Cursor kd_provinsi = sql.query("mst_provinsi", new String[]{"PROVINSI"}, "KD_PROVINSI = ?", new String[]{Util.bytesToString(provinsi)}, null, null, null);
                    if(kd_provinsi.moveToFirst()) {
                        PDCData.provinsi = kd_provinsi.getString(0);
                    } else {
                        PDCData.provinsi = "Kode provinsi tidak dikenali";
                    }
                    setProgressBar(21);
                    PDCData.kodepos = Util.bytesToString(kodepos);
                    setProgressBar(22);
                    PDCData.isDalamWilayahKerja = Util.bytesToHex(isDalamWilayahKerja);
                    setProgressBar(23);
                    PDCData.tempatLahir = Util.bytesToString(Util.trimZeroPadding(tempatLahir));
                    setProgressBar(24);
                    PDCData.tglLahir = Util.bytesToDate(tglLahir);
                    setProgressBar(25);
                    PDCData.telepon = Util.bytesToString(Util.trimZeroPadding(telepon));
                    setProgressBar(26);
                    PDCData.hp = Util.bytesToString(Util.trimZeroPadding(hp));
                    setProgressBar(27);
                    PDCData.jenisKelamin = Util.bytesToString(jenisKelamin);
                    setProgressBar(28);
                    PDCData.agama = Util.bytesToHex(agama);
                    setProgressBar(29);
                    PDCData.pendidikan = Util.bytesToHex(pendidikan);
                    setProgressBar(30);
                    PDCData.pekerjaan = Util.bytesToHex(pekerjaan);
                    setProgressBar(31);
                    PDCData.kelasPerawatan = Util.bytesToHex(kelasPerawatan);
                    setProgressBar(32);
                    PDCData.email = Util.bytesToString(Util.trimZeroPadding(email));
                    setProgressBar(33);
                    PDCData.statusPernikahan = Util.bytesToHex(statusPernikahan);
                    setProgressBar(34);
                    PDCData.kewarganegaraan = Util.bytesToHex(kewarganegaraan);
                    setProgressBar(35);
                    PDCData.namaKerabat = Util.bytesToString(Util.trimZeroPadding(namaKerabat));
                    setProgressBar(36);
                    PDCData.hubunganKerabat = Util.bytesToHex(hubunganKerabat);
                    setProgressBar(37);
                    PDCData.alamatKerabat = Util.bytesToString(Util.trimZeroPadding(alamatKerabat));
                    setProgressBar(38);
                    Cursor kd_kel_kerabat = sql.query("mst_kelurahan", new String[]{"KELURAHAN"}, "KD_KELURAHAN = ?", new String[]{Util.bytesToString(kelurahanDesaKerabat)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.kelurahanDesaKerabat = kd_kel_kerabat.getString(0);
                    } else {
                        PDCData.kelurahanDesaKerabat = "Kode kelurahan tidak dikenali";
                    }
                    setProgressBar(39);
                    Cursor kd_kec_kerabat = sql.query("mst_kecamatan", new String[]{"KECAMATAN"}, "KD_KECAMATAN = ?", new String[]{Util.bytesToString(kecamatanKerabat)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.kecamatanKerabat = kd_kec_kerabat.getString(0);
                    } else {
                        PDCData.kecamatanKerabat = "Kode kecamatan tidak dikenali";
                    }
                    setProgressBar(40);
                    Cursor kd_kab_kerabat = sql.query("mst_kabupaten", new String[]{"KABUPATEN"}, "KD_KABUPATEN = ?", new String[]{Util.bytesToString(kotaKabupatenKerabat)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.kotaKabupatenKerabat = kd_kab_kerabat.getString(0);
                    } else {
                        PDCData.kotaKabupatenKerabat = "Kode kabupaten tidak dikenali";
                    }
                    setProgressBar(41);
                    Cursor kd_prov_kerabat = sql.query("mst_provinsi", new String[]{"PROVINSI"}, "KD_PROVINSI = ?", new String[]{Util.bytesToString(provinsiKerabat)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.provinsiKerabat = kd_prov_kerabat.getString(0);
                    } else {
                        PDCData.provinsiKerabat = "Kode provinsi tidak dikenali";
                    }
                    setProgressBar(42);
                    PDCData.kodeposKerabat = Util.bytesToString(kodeposKerabat);
                    setProgressBar(43);
                    PDCData.teleponKerabat = Util.bytesToString(Util.trimZeroPadding(teleponKerabat));
                    setProgressBar(44);
                    PDCData.hpKerabat =  Util.bytesToString(Util.trimZeroPadding(hpKerabat));
                    setProgressBar(45);
                    PDCData.namaKantor = Util.bytesToString(Util.trimZeroPadding(namaKantor));
                    setProgressBar(46);
                    PDCData.alamatKantor = Util.bytesToString(Util.trimZeroPadding(alamatKantor));
                    setProgressBar(47);
                    Cursor kd_kab_kantor = sql.query("mst_kabupaten", new String[]{"KABUPATEN"}, "KD_KABUPATEN = ?", new String[]{Util.bytesToString(kotaKabupatenKantor)}, null, null, null);
                    if(kd_kelurahan.moveToFirst()) {
                        PDCData.kotaKabupatenKantor = kd_kab_kantor.getString(0);
                    } else {
                        PDCData.kotaKabupatenKantor = "Kode kabupaten tidak dikenali";
                    }
                    setProgressBar(48);
                    PDCData.teleponKantor = Util.bytesToString(Util.trimZeroPadding(teleponKantor));
                    setProgressBar(49);
                    PDCData.hpKantor = Util.bytesToString(Util.trimZeroPadding(hpKantor));
                    setProgressBar(50);

                    send();

                }
            } else {
                Log.i(TAG, "no i " + i);
            }
        }
    };

    public void send() {
        if ( i == 0 ) {
            try {
                serialPort.write(APDU_select);
                i++;
                Log.i(TAG, "write apdu select");
                Thread.sleep(2000);
                if (isCommandReceived != 1) {
                    i = 0;
                    setTextView("Koneksi kartu gagal\nSilahkan cabut pasang kartu");
                    Log.i(TAG, "Koneksi kartu gagal");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    setTextView("Berhasil koneksi");
                    Log.i(TAG, "Berhasil koneksi");
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        } else if (i == 1) {
            serialPort.write(APDU_card_checking);
            i++;
            setTextView("Card Checking");
            Log.i(TAG, "write apdu card checking");
        } else if (i == 2) {
            serialPort.write(APDU_read_medrec_statik);
            i++;
            setTextView("Membaca rekmed statik");
            Log.i(TAG, "write apdu read medrec statik");
        } else if (i == 3) {
            serialPort.write(APDU_read_biodata);
            i++;
            setTextView("Membaca biodata pasien");
            Log.i(TAG, "write apdu read biodata");
        } else {
            if(personalized) {
                serialPort.close();
                Log.i(TAG, "serial port closed");
                unregisterReceiver(broadcastReceiver);
                showToastOnUi("Baca data PDC BERHASIL!");
                MedrecDinamikData.isInDatabase = 0;
            } else {
                setTextView("PDC belum dipersonalisasi\nSilahkan masukkan PDC lain");
                personalized = true;
//                progressBar.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void setProgressBar(final int max){
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < max) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);

                            if(progressStatus == 50){
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent activity = new Intent(PasiensyncActivity.this, BottombarActivity.class);
                                startActivity(activity);
                            }
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void showToastOnUi(String text) {
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PasiensyncActivity.this, ftext, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextView(final String text){
        final String ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv2.setText(ftext);
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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PasiensyncActivity.this);
            mBuilder.setIcon(R.drawable.logo2);
            mBuilder.setTitle("Konfirmasi");
            mBuilder.setMessage("Apakah Anda ingin keluar dari profil dokter?");
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(PasiensyncActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(PasiensyncActivity.this, "Tekan lagi untuk keluar dari profil pasien", Toast.LENGTH_SHORT).show();
        }

        back_pressed = System.currentTimeMillis();
    }
}