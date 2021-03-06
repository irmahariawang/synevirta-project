package com.procodecg.codingmom.ehealth.rekam_medis;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cielyang.android.clearableedittext.ClearableEditText;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecDinamikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.main.WelcomeActivity;
import com.procodecg.codingmom.ehealth.utils.Edit;
import com.procodecg.codingmom.ehealth.utils.NothingSelectedSpinnerAdapter;
import com.procodecg.codingmom.ehealth.utils.Validation;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by macbookpro on 9/4/17.
 */

public class RekmedbaruActivity extends AppCompatActivity {
    String TAG = "hpcpdcdummy";
    public final String ACTION_USB_PERMISSION = "com.nehceh.hpcpdc.USB_PERMISSION";
    Typeface fontBold;

    private byte[] chunk1, chunk2, chunk3, chunk4, chunk5, chunk6, chunk7, chunk8, chunk9, chunk10,
                    chunk11, chunk12, chunk13;

    private TextView txtTitle;
    private int mPoli = RekamMedisEntry.POLI_UMUM;
    private int mKesadaran = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
    private int mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
    private int mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
    private RadioGroup radioGroup;
    private int mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_TIDAK;
    private int mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
    private int mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
    private int mAdSanationam = RekamMedisEntry.SANATIONAM_ADBONAM;

    private EditText mIDPuskesmas, mPemberiRujukan, mSystole, mDiastole, mSuhu, mNadi, mRespirasi, mKeluhanUtama, mRiwayatPenyakitSkr,
            mRiwayatPenyakitDulu, mRiwayatPenyakitKel, mTinggi, mBerat, mKepala, mThorax, mAbdomen, mGenitalia, mExtremitas,
            mKulit, mNeurologi, mLaboratorium, mRadiologi, mDiagnosisKerja, mDiagnosisBanding, mResep, mCatatanResep;
    private Spinner mPoliSpinner, mKesadaranSpinner, mStatusLabRadioSpinner, mStatusResepSpinner, mAdVitamSpinner, mAdFunctionamSpinner, mAdSanationamSpinner;
    private AutoCompleteTextView mICD10;
    private RadioGroup mRepetisiResepBtn;
    private EditText mTindakan;

    private EditText idPuskesmas;
    private SharedPreferences prefs;

    private ProgressDialog progressDialog;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    String data;
    int i; // buat increment serial tulis apdu

    ByteBuffer respondData;
    IntentFilter filter;
    byte[] selectResponse;
    byte[] APDU_select = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_insert;

    Activity mActivity;

//    aktivasi tombol x-clear


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MENGHILANGKAN TOOLBAR
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rekmedbaru);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Rekam Medis Baru");

        fontBold = Typeface.createFromAsset(getAssets(),"font1bold.ttf");
        txtTitle.setTypeface(fontBold);

        //menampilkan nama puskesmas
        prefs=getSharedPreferences("DATAPUSKES",MODE_PRIVATE);
        String namapuskes=prefs.getString("IDPUSKES","");

        idPuskesmas = (ClearableEditText) findViewById(R.id.idPuskesmas);
        idPuskesmas.setText(namapuskes);


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setScrollbarFadingEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setNavigationIcon(R.drawable.ic_xclose);
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        View logo = getLayoutInflater().inflate(R.layout.activity_rekmedbaru, null);
//        mToolbar.addView(logo, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.setContentInsetsAbsolute(0,0);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView txt = (TextView) findViewById(R.id.txt_title);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(txt.getLayoutParams());
//        lp.setMargins(0, 0, 0, 0);
//        txt.setLayoutParams(lp);
//        toolbar.setTitleMarginStart(0);
//        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) toolbar.getLayoutParams();
//        layoutParams.setMargins(0, 0, 0, 0);
//        toolbar.setLayoutParams(layoutParams);
//        getResources().getDimension(R.dimen.toolbar_right);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),BottombarActivity.class));

            }
        });

        // Find all relevant views that we will need to read user input from
        mIDPuskesmas = (EditText) findViewById(R.id.idPuskesmas);
        mPoliSpinner = (Spinner) findViewById(R.id.poli_spinner);
        mPemberiRujukan = (EditText) findViewById(R.id.pemberiRujukan);
        mSystole = (EditText) findViewById(R.id.systole);
        mDiastole = (EditText) findViewById(R.id.diastole);
        mSuhu = (EditText) findViewById(R.id.suhu);
        mNadi = (EditText) findViewById(R.id.nadi);
        mRespirasi = (EditText) findViewById(R.id.respirasi);
        mKeluhanUtama = (EditText) findViewById(R.id.keluhanUtama);
        mRiwayatPenyakitSkr = (EditText) findViewById(R.id.rps);
        mRiwayatPenyakitDulu = (EditText) findViewById(R.id.rpd);
        mRiwayatPenyakitKel = (EditText) findViewById(R.id.rpk);
        mTinggi = (EditText) findViewById(R.id.tinggi);
        mBerat = (EditText) findViewById(R.id.berat);
        mKesadaranSpinner = (Spinner) findViewById(R.id.kesadaran_spinner);
        mKepala = (EditText) findViewById(R.id.pemeriksaanKepala);
        mThorax = (EditText) findViewById(R.id.pemeriksaanThorax);
        mAbdomen = (EditText) findViewById(R.id.pemeriksaanAbdomen);
        mGenitalia = (EditText) findViewById(R.id.pemeriksaanGenitalia);
        mExtremitas = (EditText) findViewById(R.id.pemeriksaanExtremitas);
        mKulit = (EditText) findViewById(R.id.pemeriksaanKulit);
        mNeurologi = (EditText) findViewById(R.id.pemeriksaanNeuro);
        mLaboratorium = (EditText) findViewById(R.id.laboratorium);
        mRadiologi = (EditText) findViewById(R.id.radiologi);
        mStatusLabRadioSpinner = (Spinner) findViewById(R.id.statusLabRadio);
        mDiagnosisKerja = (EditText) findViewById(R.id.diagnosisKerja);
        mDiagnosisBanding = (EditText) findViewById(R.id.diagnosisBanding);
        mICD10 = (AutoCompleteTextView) findViewById(R.id.statusDiagnosis);
        mResep = (EditText) findViewById(R.id.resep);
        mCatatanResep = (EditText) findViewById(R.id.catatanResep);
        mStatusResepSpinner = (Spinner) findViewById(R.id.statusResep);
        mRepetisiResepBtn = (RadioGroup) findViewById(R.id.repetisiResep);
        mTindakan = (EditText) findViewById(R.id.tindakan);
        mAdVitamSpinner = (Spinner) findViewById(R.id.adVitam);
        mAdFunctionamSpinner = (Spinner) findViewById(R.id.adFunctionam);
        mAdSanationamSpinner = (Spinner) findViewById(R.id.adSanationam);


        // setup spinner
        setupSpinner();

        //setup autocomplete
        setupAutoComplete();



    //BUTTON SAVE
    Button mShowDialog = (Button) findViewById(R.id.btnShowDialog);
        mShowDialog.setOnClickListener(new View.OnClickListener() {
    @Override
        public void onClick(final View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RekmedbaruActivity.this);
        mBuilder.setIcon(R.drawable.logo2);
        mBuilder.setTitle("Data yang Anda masukkan tidak dapat dirubah lagi");
        mBuilder.setMessage("Apakah Anda akan menyimpan data sekarang?");
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
                simpanData();
                dialogInterface.dismiss();
                //startActivity(new Intent(getApplicationContext(),BottombarActivity.class));
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
        }
        });


        respondData = ByteBuffer.allocate(2);
        i = 0;
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        // connect usb device
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                usbDevice = entry.getValue();
                int deviceID = usbDevice.getVendorId();
                if (deviceID == 1027 || deviceID == 9025) {
                    Log.d(TAG, "Device ID " + deviceID);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(usbDevice, pi);
                    keep = false;
                } else {
                    usbConn = null;
                    usbDevice = null;
                }

                if (!keep)
                    break;
            }
        }
        }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "broadcastReceiver in", Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(getApplicationContext(), "Serial connection opened!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Ok");
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
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                i=0;
                Intent activity = new Intent(getApplicationContext(), PasiensyncActivity.class);
                startActivity(activity);
                finish();
            }
        }
    };

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = Util.bytesToHex(bytes);
            Log.d(TAG, "Data " + data);
            Log.i(TAG, "i: " + i);

            if (i == 1) { //select
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Select response string: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) {
                        i--;
                    }
                }
            } else if (i == 2) { // insert chunk 1
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 1");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 1");
                        send();
                    }
                }
            } else if (i == 3) { // insert chunk 2
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 2");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 2");
                        send();
                    }
                }
            } else if (i == 4) { // insert chunk 3
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 3");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 3");
                        send();
                    }
                }
            } else if (i == 5) { // insert chunk 4
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 4");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 4");
                        send();
                    }
                }
            }
            else if (i == 6) { // insert chunk 5
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 5");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 5");
                        send();
                    }
                }
            }
            else if (i == 7) { // insert chunk 6
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 6");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 6");
                        send();
                    }
                }
            }
            else if (i == 8) { // insert chunk 7
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 7");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 7");
                        send();
                    }
                }
            }
            else if (i == 9) { // insert chunk 8
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 8");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 8");
                        send();
                    }
                }
            }
            else if (i == 10) { // insert chunk 9
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 9");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 9");
                        send();
                    }
                }
            }
            else if (i == 11) { // insert chunk 10
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 10");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 10");
                        send();
                    }
                }
            }
            else if (i == 12) { // insert chunk 11
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 11");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 11");
                        send();
                    }
                }
            }
            else if (i == 13) { // insert chunk 12
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 12");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 12");
                        send();
                    }
                }
            }
            else if (i == 14) { // insert chunk 13
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: 13");
                    } else {
                        Log.d(TAG, "Berhasil INSERT: 13");
//                        progressDialog.dismiss();
                        send();
                    }
                }
            }
            else {
                Log.e(TAG, "i: " + i);
            }
        }
    };

    public void send() {
        if ( i == 0 ) {
            serialPort.write(APDU_select);
            i++;
            Log.i(TAG, "write apdu select");
        }
        else if (i == 1) {
            serialPort.write(chunk1);
            i++;
            Log.i(TAG, "write insert medrec: c1");
        }
        else if (i == 2) {
            serialPort.write(chunk2);
            i++;
            Log.i(TAG, "write insert medrec: c2");
        }
        else if (i == 3) {
            serialPort.write(chunk3);
            i++;
            Log.i(TAG, "write insert medrec: c3");
        }
        else if (i == 4) {
            serialPort.write(chunk4);
            i++;
            Log.i(TAG, "write insert medrec: c4");
        }
        else if (i == 5) {
            serialPort.write(chunk5);
            i++;
            Log.i(TAG, "write insert medrec: c5");
        }
        else if (i == 6) {
            serialPort.write(chunk6);
            i++;
            Log.i(TAG, "write insert medrec: c6");
        }
        else if (i == 7) {
            serialPort.write(chunk7);
            i++;
            Log.i(TAG, "write insert medrec: c7");
        }
        else if (i == 8) {
            serialPort.write(chunk8);
            i++;
            Log.i(TAG, "write insert medrec: c8");
        }
        else if (i == 9) {
            serialPort.write(chunk9);
            i++;
            Log.i(TAG, "write insert medrec: c9");
        }
        else if (i == 10) {
            serialPort.write(chunk10);
            i++;
            Log.i(TAG, "write insert medrec: c10");
        }
        else if (i == 11) {
            serialPort.write(chunk11);
            i++;
            Log.i(TAG, "write insert medrec: c11");
        }
        else if (i == 12) {
            serialPort.write(chunk12);
            i++;
            Log.i(TAG, "write insert medrec: c12");
        }
        else if (i == 13) {
            serialPort.write(chunk13);
            i++;
            Log.i(TAG, "write insert medrec: c13");
        }
        else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            try {
                if (broadcastReceiver != null) {
                    unregisterReceiver(broadcastReceiver);
                    finish();
                }
            } catch (IllegalArgumentException e) {
                Log.i(TAG,"RekmedBaruActivity:Receiver is already unregistered");
                finish();
            }
        }
    }

        private void setupSpinner(){

            //spinner poli
            Spinner spinnerPoli = (Spinner) findViewById(R.id.poli_spinner);
            ArrayAdapter<CharSequence> adapterPoli = ArrayAdapter.createFromResource(this,
                    R.array.poli, android.R.layout.simple_spinner_item);
            adapterPoli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPoli.setPrompt("Pilih poli tujuan");
            spinnerPoli.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterPoli,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerPoli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Gigi")) {
                            mPoli = RekamMedisEntry.POLI_GIGI;
                        } else {
                            mPoli = RekamMedisEntry.POLI_UMUM;
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mPoli = RekamMedisEntry.POLI_UMUM;
                }
            });


            //spinner kesadaran
            Spinner spinnerKesadaran = (Spinner) findViewById(R.id.kesadaran_spinner);
            ArrayAdapter<CharSequence> adapterKesadaran = ArrayAdapter.createFromResource(this,
                    R.array.kesadaran, android.R.layout.simple_spinner_item);
            adapterKesadaran.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerKesadaran.setPrompt("Pilih tingkat kesadaran");
            spinnerKesadaran.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterKesadaran,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerKesadaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Apatis")) {
                            mKesadaran = RekamMedisEntry.KESADARAN_APATIS;
                        } else if (selection.equals("Delirium")){
                            mKesadaran = RekamMedisEntry.KESADARAN_DELIRIUM;
                        } else if (selection.equals("Somnolen")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SOMNOLEN;
                        } else if (selection.equals("Sopor")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SOPOR;
                        } else if (selection.equals("Semi-coma")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SEMICOMA;
                        } else if (selection.equals("Coma")){
                            mKesadaran = RekamMedisEntry.KESADARAN_COMA;
                        } else {
                            mPoli = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mKesadaran = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
                }
            });

            //spinner status Laboratorium Radiologi
            Spinner spinnerLabRadio = (Spinner) findViewById(R.id.statusLabRadio);
            ArrayAdapter<CharSequence> adapterLabRadio = ArrayAdapter.createFromResource(this,
                    R.array.status_labradio, android.R.layout.simple_spinner_item);
            adapterLabRadio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLabRadio.setPrompt("Pilih status Laboratorium Radiologi");
            spinnerLabRadio.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterLabRadio,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerLabRadio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dilayani sebagian")) {
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANISEBAGIAN;
                        } else if (selection.equals("Tidak dilayani sama sekali")){
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_TIDAKDILAYANI;
                        } else {
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
                }
            });

            //spinner status resep
            Spinner spinnerResep = (Spinner) findViewById(R.id.statusResep);
            ArrayAdapter<CharSequence> adapterResep = ArrayAdapter.createFromResource(this,
                    R.array.status_resep, android.R.layout.simple_spinner_item);
            adapterResep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerResep.setPrompt("Pilih status resep");
            spinnerResep.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterResep,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerResep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dilayani sebagian")) {
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_SEBAGIAN;
                        } else if (selection.equals("Dilayani ada penggantian")){
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENGGANTIAN;
                        } else if (selection.equals("Dilayani sebagian dan ada penggantian")){
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_SEBAGIAN_PENGGANTIAN;
                        } else if (selection.equals("Tidak dilayani sama sekali")){
                            mStatusResep = RekamMedisEntry.RESEP_TIDAK_DILAYANI;
                        } else {
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
                }
            });

            //Radio button repetisi resep
            radioGroup = (RadioGroup) findViewById(R.id.repetisiResep);
            radioGroup.clearCheck();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        if (checkedId != 0) {
                            mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_YA;
                        } else {
                            mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_TIDAK;
                        }
                    }

                }
            });


            //spinner status prognosis ad vitam
            Spinner spinnerAdVitam = (Spinner) findViewById(R.id.adVitam);
            ArrayAdapter<CharSequence> adapterAdVitam = ArrayAdapter.createFromResource(this,
                    R.array.ad_vitam, android.R.layout.simple_spinner_item);
            adapterAdVitam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdVitam.setPrompt("Pilih");
            spinnerAdVitam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdVitam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdVitam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdVitam = RekamMedisEntry.VITAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdVitam = RekamMedisEntry.VITAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdVitam = RekamMedisEntry.VITAM_ADMALAM;
                        } else {
                            mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
                }
            });


            //spinner status prognosis ad functionam
            Spinner spinnerAdFunctionam = (Spinner) findViewById(R.id.adFunctionam);
            ArrayAdapter<CharSequence> adapterAdFunctionam = ArrayAdapter.createFromResource(this,
                    R.array.ad_functionam, android.R.layout.simple_spinner_item);
            adapterAdFunctionam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdFunctionam.setPrompt("Pilih");
            spinnerAdFunctionam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdFunctionam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdFunctionam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADMALAM;
                        } else {
                            mAdVitam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
                }
            });
            //spinner status prognosis ad sanationam
            Spinner spinnerAdSanationam = (Spinner) findViewById(R.id.adSanationam);
            ArrayAdapter<CharSequence> adapterAdSanationam = ArrayAdapter.createFromResource(this,
                    R.array.ad_sanationam, android.R.layout.simple_spinner_item);
            adapterAdSanationam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdSanationam.setPrompt("Pilih");
            spinnerAdSanationam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdSanationam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdSanationam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdSanationam = RekamMedisEntry.SANATIONAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdSanationam = RekamMedisEntry.SANATIONAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdSanationam = RekamMedisEntry.SANATIONAM_ADMALAM;
                        } else {
                            mAdSanationam = RekamMedisEntry.SANATIONAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdSanationam = RekamMedisEntry.VITAM_ADBONAM;
                }
            });
        }

        private void setupAutoComplete(){
            AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.statusDiagnosis);
            EhealthDbHelper dbHelper = new EhealthDbHelper(getApplicationContext());
            dbHelper.openDB();
            //String pencarian = getIntent().getStringExtra("hasil");
            String[] diagnosa = dbHelper.getAllDiagnosa();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, diagnosa);
            textView.setAdapter(adapter);
            dbHelper.closeDB();
        }

        private void simpanData(){

            EhealthDbHelper mDbHelper = new EhealthDbHelper(this);
            mDbHelper.openDB();
            //mDbHelper.createTableRekMed();

            if(validateData()){
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //String mTanggalPeriksa = sdf.format(new java.util.Date());

                //setting format tanggal device yg baru
                Long tsLong = System.currentTimeMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(tsLong);
                String mTanggalPeriksa = String.valueOf(formatter.format(calendar.getTime()));

                String mIDPuskesmasString = mIDPuskesmas.getText().toString().trim();
                String mNamaDokterString = PasiensyncActivity.getNamaDokter();
                //  PoliSpinner
                String mPemberiRujukanString = mPemberiRujukan.getText().toString().trim();
                String mSystoleString = mSystole.getText().toString().trim();
                String mDiastoleString = mDiastole.getText().toString().trim();
                String mSuhuString = mSuhu.getText().toString().trim();
                String mNadiString = mNadi.getText().toString().trim();
                String mRespirasiString = mRespirasi.getText().toString().trim();
                String mKeluhanUtamaString = mKeluhanUtama.getText().toString().trim();
                String mRiwayatPenyakitSkrString = mRiwayatPenyakitSkr.getText().toString().trim();
                String mRiwayatPenyakitDuluString = mRiwayatPenyakitDulu.getText().toString().trim();
                String mRiwayatPenyakitKelString = mRiwayatPenyakitKel.getText().toString().trim();
                String mTinggiString = mTinggi.getText().toString().trim();
                String mBeratString = mBerat.getText().toString().trim();
                // KesadaranSpinner
                String mKepalaString = mKepala.getText().toString().trim();
                String mThoraxString = mThorax.getText().toString().trim();
                String mAbdomenString = mAbdomen.getText().toString().trim();
                String mGenitaliaString = mGenitalia.getText().toString().trim();
                String mExtremitasString = mExtremitas.getText().toString().trim();
                String mKulitString = mKulit.getText().toString().trim();
                String mNeurologiString = mNeurologi.getText().toString().trim();
                String mLaboratoriumString = mLaboratorium.getText().toString().trim();
                String mRadiologiString = mRadiologi.getText().toString().trim();
                // StatusLabRadioSpinner
                String mDiagnosisKerjaString = mDiagnosisKerja.getText().toString().trim();
                String mDiagnosisBandingString = mDiagnosisBanding.getText().toString().trim();
                String mICD10String = mICD10.getText().toString().trim();
                String mResepString = mResep.getText().toString().trim();
                String mCatatanResepString = mCatatanResep.getText().toString().trim();
                // StatusResepSpinner
                String mTindakanString = mTindakan.getText().toString().trim();
                // AdVitamSpinner
                // AdFunctionamSpinner
                // AdSanationamSpinner

                // Gets the database in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(RekamMedisEntry.COLUMN_TGL_PERIKSA, mTanggalPeriksa);
                values.put(RekamMedisEntry.COLUMN_NAMA_DOKTER, mNamaDokterString);
                values.put(RekamMedisEntry.COLUMN_NIK, PDCData.nik);
                values.put(RekamMedisEntry.COLUMN_ID_PUSKESMAS, mIDPuskesmasString);
                values.put(RekamMedisEntry.COLUMN_POLI, mPoli);
                values.put(RekamMedisEntry.COLUMN_RUJUKAN, mPemberiRujukanString);
                values.put(RekamMedisEntry.COLUMN_SYSTOLE, mSystoleString);
                values.put(RekamMedisEntry.COLUMN_DIASTOLE, mDiastoleString);
                values.put(RekamMedisEntry.COLUMN_SUHU, mSuhuString);
                values.put(RekamMedisEntry.COLUMN_NADI, mNadiString);
                values.put(RekamMedisEntry.COLUMN_RESPIRASI, mRespirasiString);
                values.put(RekamMedisEntry.COLUMN_KELUHANUTAMA, mKeluhanUtamaString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG, mRiwayatPenyakitSkrString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_DULU, mRiwayatPenyakitDuluString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_KEL, mRiwayatPenyakitKelString);
                values.put(RekamMedisEntry.COLUMN_TINGGI, mTinggiString);
                values.put(RekamMedisEntry.COLUMN_BERAT, mBeratString);
                values.put(RekamMedisEntry.COLUMN_KESADARAN, mKesadaran);
                values.put(RekamMedisEntry.COLUMN_KEPALA, mKepalaString);
                values.put(RekamMedisEntry.COLUMN_THORAX, mThoraxString);
                values.put(RekamMedisEntry.COLUMN_ABDOMEN, mAbdomenString);
                values.put(RekamMedisEntry.COLUMN_GENITALIA, mGenitaliaString);
                values.put(RekamMedisEntry.COLUMN_EXTREMITAS, mExtremitasString);
                values.put(RekamMedisEntry.COLUMN_KULIT, mKulitString);
                values.put(RekamMedisEntry.COLUMN_NEUROLOGI, mNeurologiString);
                values.put(RekamMedisEntry.COLUMN_LABORATORIUM, mLaboratoriumString);
                values.put(RekamMedisEntry.COLUMN_RADIOLOGI, mRadiologiString);
                values.put(RekamMedisEntry.COLUMN_STATUS_LABRADIO, mStatusLabRadio);
                values.put(RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA, mDiagnosisKerjaString);
                values.put(RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING, mDiagnosisBandingString);
                values.put(RekamMedisEntry.COLUMN_ICD10_DIAGNOSA, mICD10String);
                values.put(RekamMedisEntry.COLUMN_RESEP, mResepString);
                values.put(RekamMedisEntry.COLUMN_CATTRESEP, mCatatanResepString);
                values.put(RekamMedisEntry.COLUMN_STATUSRESEP, mStatusResep);
                values.put(RekamMedisEntry.COLUMN_REPETISIRESEP, mRepetisiResep);
                values.put(RekamMedisEntry.COLUMN_TINDAKAN, mTindakanString);
                values.put(RekamMedisEntry.COLUMN_AD_VITAM, mAdVitam);
                values.put(RekamMedisEntry.COLUMN_AD_FUNCTIONAM, mAdFunctionam);
                values.put(RekamMedisEntry.COLUMN_AD_SANATIONAM, mAdSanationam);

                chunk1 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 1));
                chunk2 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 2));
                chunk3 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 3));
                chunk4 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 4));
                chunk5 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 5));
                chunk6 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 6));
                chunk7 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 7));
                chunk8 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 8));
                chunk9 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 9));
                chunk10 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 10));
                chunk11 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 11));
                chunk12 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 12));
                chunk13 = Util.hexStringToByteArray(makeAPDUInsertCommand(values, MedrecDinamikData.writeIndex, 13));
                send();
                showLoader();

                // Insert a new row for pet in the database, returning the ID of that new row.
                long newRowId = db.insert(RekamMedisEntry.TABLE_NAME, null, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newRowId == -1) {
                    // If the row ID is -1, then there was an error with insertion.
                    //Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast with the row ID.
                    //Toast.makeText(this, "Data saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
                    //simpanData();
//                    finish();
                }
                mDbHelper.closeDB();
            }
        }

    //VALIDASI ISIAN DATA
    private boolean validateData(){
        boolean valid = true;

        EditText[] editTextMandatory = {mSystole, mDiastole, mSuhu, mNadi, mRespirasi, mKeluhanUtama,
                mBerat, mTinggi, mKepala, mThorax, mAbdomen, mDiagnosisKerja, mICD10, mResep};

        EditText[] allEditText = {mIDPuskesmas, mPemberiRujukan, mSystole, mDiastole, mSuhu, mNadi, mRespirasi, mKeluhanUtama, mRiwayatPenyakitSkr,
                mRiwayatPenyakitDulu, mRiwayatPenyakitKel, mTinggi, mBerat, mKepala, mThorax, mAbdomen, mGenitalia, mExtremitas,
                mKulit, mNeurologi, mLaboratorium, mRadiologi, mDiagnosisKerja, mDiagnosisBanding, mResep, mCatatanResep};

        for(int i=0; i<editTextMandatory.length; i++){
            if (!Validation.hasText(editTextMandatory[i])) valid  = false;
        }

        for(int i=0; i<allEditText.length; i++){
            if (Validation.hasSpecialCharacter(allEditText[i])) valid  = false;
        }

//        if (!Validation.hasText(mSystole, "Systole") || Validation.hasSpecialCharacter(mSystole)) valid = false;
//        if (!Validation.hasText(mDiastole, "Diastole") || Validation.hasSpecialCharacter(mDiastole)) valid = false;
//        if (!Validation.hasText(mSuhu, "Suhu") || Validation.hasSpecialCharacter(mSuhu)) valid = false;
//        if (!Validation.hasText(mNadi, "Nadi") || Validation.hasSpecialCharacter(mNadi)) valid = false;
//        if (!Validation.hasText(mRespirasi, "Respirasi") || Validation.hasSpecialCharacter(mRespirasi)) valid = false;
//        if (!Validation.hasText(mKeluhanUtama, "Keluhan Utama") || Validation.hasSpecialCharacter(mKeluhanUtama)) valid = false;
//        if (!Validation.hasText(mBerat, "Berat") || Validation.hasSpecialCharacter(mBerat)) valid = false;
//        if (!Validation.hasText(mTinggi, "Tinggi") || Validation.hasSpecialCharacter(mTinggi)) valid = false;
//        if (!Validation.hasText(mKepala, "Kepala") || Validation.hasSpecialCharacter(mKepala)) valid = false;
//        if (!Validation.hasText(mThorax, "Thorax") || Validation.hasSpecialCharacter(mThorax)) valid = false;
//        if (!Validation.hasText(mAbdomen, "Abdomen") || Validation.hasSpecialCharacter(mAbdomen)) valid = false;
//        if (!Validation.hasText(mDiagnosisKerja, "Diagnosis Kerja") || Validation.hasSpecialCharacter(mDiagnosisBanding)) valid = false;
//        if (!Validation.hasText(mICD10, "ICD10") || Validation.hasSpecialCharacter(mICD10)) valid = false;
//        if (!Validation.hasText(mResep, "Resep") || Validation.hasSpecialCharacter(mResep)) valid = false;

        return valid;
    }

    private String makeAPDUInsertCommand(ContentValues cv, int writeIndex, int chunk) {
        switch(chunk) {
            case 1:
                String cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00"; // 00
                cmd += "007b"; // Total length
                cmd += "0000"; // Start pointer
                cmd += "0077"; // actual data length
                cmd += Util.bytesToHex(Util.intToBytes(writeIndex)); // data index
                cmd += Util.bytesToHex(Util.dateToBytes(Util.getCurrentDate())); // tglperiksa
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_ID_PUSKESMAS), 12); // idpuskesmas
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_POLI)); // poli
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_RUJUKAN), 30); // pemberi rujukan
                cmd += Util.intToHex(cv.getAsInteger(RekamMedisEntry.COLUMN_SYSTOLE)); // systole
                cmd += Util.intToHex(cv.getAsInteger(RekamMedisEntry.COLUMN_DIASTOLE)); // diastole
                cmd += Util.bytesToHex(Util.floatToBytes(cv.getAsFloat(RekamMedisEntry.COLUMN_SUHU))); // suhu
                cmd += Util.intToHex3(cv.getAsInteger(RekamMedisEntry.COLUMN_NADI)); // nadi
                cmd += Util.intToHex3(cv.getAsInteger(RekamMedisEntry.COLUMN_RESPIRASI)); // respirasi
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_KELUHANUTAMA), 50); // keluhan utama
                assert cmd.length() == 260;
                return cmd;
            case 2:
                // cmd is declared during compiled time
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0077"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG), 200); // riwayat penyakit sekarang
                assert cmd.length() == 422;
                return cmd;
            case 3:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "013F"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_PENYAKIT_DULU), 100);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_PENYAKIT_KEL), 100);
                assert cmd.length() == 422;
                return cmd;
            case 4:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00A3"; // total length
                cmd += "0207"; // start pointer
                cmd += "009F"; // actual data length
                cmd += Util.intToHex(cv.getAsInteger(RekamMedisEntry.COLUMN_TINGGI));
                cmd += Util.intToHex(cv.getAsInteger(RekamMedisEntry.COLUMN_BERAT));
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_KESADARAN));
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_KEPALA), 50);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_THORAX), 50);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_ABDOMEN), 50);
                assert cmd.length() == 340;
                return cmd;
            case 5:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "02A6"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_GENITALIA), 50);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_EXTREMITAS), 50);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_KULIT), 50);
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_NEUROLOGI), 50);
                assert cmd.length() == 422;
                return cmd;
            case 6:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "036E"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_LABORATORIUM), 200);
                assert cmd.length() == 422;
                return cmd;
            case 7:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0436"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_RADIOLOGI), 200);
                assert cmd.length() == 422;
                return cmd;
            case 8:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CD"; // total length
                cmd += "04FE"; // start pointer
                cmd += "00C9"; // actual data length
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_STATUS_LABRADIO));
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA), 200);
                assert cmd.length() == 424;
                return cmd;
            case 9:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "05C7"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING), 200);
                assert cmd.length() == 422;
                return cmd;
            case 10:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "068F"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_ICD10_DIAGNOSA), 200);
                assert cmd.length() == 422;
                return cmd;
            case 11:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0757"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_RESEP), 200);
                assert cmd.length() == 422;
                return cmd;
            case 12:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "0036"; // total length
                cmd += "081F"; // start pointer
                cmd += "0034"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_CATTRESEP), 50);
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_STATUSRESEP));
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_REPETISIRESEP));
                assert cmd.length() == 126;
                return cmd;
            case 13:
                cmd = "80c5"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CF"; // total length
                cmd += "0853"; // start pointer
                cmd += "00CB"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(RekamMedisEntry.COLUMN_TINDAKAN), 200);
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_AD_VITAM));
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_AD_FUNCTIONAM));
                cmd += String.format("%02X", cv.getAsByte(RekamMedisEntry.COLUMN_AD_SANATIONAM));
                assert cmd.length() == 428;
                return cmd;
            default:
                throw new WrongInputException("Wrong medrec chunk number");
        }
    }

    class WrongInputException extends RuntimeException {
        public WrongInputException(String message) {
            super(message);
        }
    }

    private void showLoader() {
        progressDialog = ProgressDialog.show(this, "E-health",
                "Menulis medrec dinamik, harap tunggu", true);
    }

}
