package com.procodecg.codingmom.ehealth.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecDinamikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util.*;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.main.WelcomeActivity;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedDinamisFragment;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedStatisFragment;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToDate;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToHex;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToString;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.trimZeroPadding;

/**
 * Created by macbookpro on 8/29/17.
 */

public class RekmedFragment extends Fragment {

    Dialog myDialog;
    ProgressBar progressBar;
    TextView textView;

    int progressStatus = 0;
    Handler handler = new Handler();

    /*
     * Komunikasi dengan kartu
     */
    static final String[] ASD = new String[] { "1", "2", "3", "4", "5"};
    final String TAG = "HPCPDCDUMMY";
    public final String ACTION_USB_PERMISSION = "com.nehceh.hpcpdc.USB_PERMISSION";

    ArrayList<MedrecDinamikData> mddArray = new ArrayList<>();
    MedrecDinamikData mdd;
    String data;
    int i; // buat increment serial tulis apdu

    ByteBuffer respondData;
    IntentFilter filter;

    byte[] selectResponse, medrecDinamikResponse;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    //00 A4 04 00 08 50 44 43 44 55 4D 4D 59
    byte[] APDU_select = {0x00, (byte)0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_read_medrec_dinamik1 = {(byte)0x80, (byte)0xD5, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik2 = {(byte)0x80, (byte)0xD5, 0x00, 0x01, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik3 = {(byte)0x80, (byte)0xD5, 0x00, 0x02, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik4 = {(byte)0x80, (byte)0xD5, 0x00, 0x03, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik5 = {(byte)0x80, (byte)0xD5, 0x00, 0x04, 0x00, 0x00, 0x00};
    /*
     * Komunikasi dengan kartu
     */

    public static RekmedFragment newInstance() {
        RekmedFragment fragment = new RekmedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i(TAG, "Redmed fragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rekmed,container, false);
        ((BottombarActivity) getActivity()).setTitleText("Rekam Medis");
        ((BottombarActivity) getActivity()).setSubTitleText();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.result_tabs);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);


        return view;

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new RekmedDinamisFragment(), "Dinamis");
        adapter.addFragment(new RekmedStatisFragment(), "Statis");
        viewPager.setAdapter(adapter);
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(RekmedDinamisFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(RekmedStatisFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "Redmed fragment onResume");
        super.onResume();

        if (MedrecDinamikData.isInDatabase == 0) {
            bacaMedrekDinamik();

            // Komunikasi dengan kartu
            i = 0;

            // TODO medrec dinamik length baru + status code
            respondData = ByteBuffer.allocate(2336);

            usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
            filter = new IntentFilter();
            filter.addAction(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            getActivity().registerReceiver(broadcastReceiver, filter);
            // Komunikasi dengan kartu
            // connect usb device
            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    usbDevice = entry.getValue();
                    int deviceID = usbDevice.getVendorId();
                    if (deviceID == 1027 || deviceID == 9025) {
                        Log.d(TAG, "Device ID " + deviceID);
                        PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
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
                Log.d(TAG, "Usb devices empty");
            }
        }
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
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                i=0;
                getActivity().unregisterReceiver(broadcastReceiver);
                Intent activity = new Intent(getActivity(), PasiensyncActivity.class);
                startActivity(activity);

            } else {
                Log.w(TAG, "NO INTENT?");
            }
        }
    };

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = bytesToHex(bytes);
            Log.d(TAG, "Data " + data);

            if (i == 1) { //select
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);
                    Log.i(TAG, "Select response string: " + bytesToHex(selectResponse));
                    send();
                }
            } else if (i == 2 || i == 3 || i == 4 || i == 5 || i == 6) { //medrec dinamik
                respondData.put(bytes);

                // TODO medrec dinamik length baru + status code
                if (respondData.position() == 2336) {
                    medrecDinamikResponse = new byte[2336];
                    respondData.rewind();
                    respondData.get(medrecDinamikResponse);
                    respondData.position(0);
//                    Log.i(TAG, "Medrec dinamik string: " + Util.bytesToHex(medrecDinamikResponse));

                    byte[] response = Arrays.copyOfRange(medrecDinamikResponse, 0, 2334);

                    int x = (i-1)*10;
                    setProgressBar(x);

                    if(responseVerifier(Util.bytesToHex(response))) {
                        processDinamikData(medrecDinamikResponse);
                    } else {
                        i = 6;
                        setProgressBar(50);
                    }

                    send();
                }
            }
            else {
                Log.i(TAG, "no i " + i);
            }
        }
    };

    private boolean responseVerifier(String response){
        Pattern pattern = Pattern.compile("[1-9]");
        Matcher matcher = pattern.matcher(response);

        return matcher.find();
    }

    public void processDinamikData(byte[] data) {
        //TODO
        ByteBuffer bb = ByteBuffer.wrap(medrecDinamikResponse);
        bb.rewind();

        // no index
        int noIdx = bb.getInt();
        Log.i(TAG,"No. Index: " + noIdx);

        // tgl periksa
        byte[] date = new byte[4];
        bb.get(date, 0, 4);
        Log.i(TAG,"Date: " + bytesToHex(date));

        // id puskemsmas
        byte[] idpuskesmas = new byte[12];
        bb.get(idpuskesmas, 0, 12);
        Log.i(TAG,"ID Puskesmas: " + bytesToString(idpuskesmas));

        // poli yang dituju
        byte poli = bb.get();
        Log.i(TAG,"Poli yang dituju: " + poli);

        // pemberi rujukan
        byte[] pemberiRujukan = new byte[30];
        bb.get(pemberiRujukan, 0, 30);
        Log.i(TAG,"Pemberi Rujukan: " + bytesToString(trimZeroPadding(pemberiRujukan)));

        // systole
        int systole = bb.getInt();
        Log.i(TAG,"Systole: " + systole);

        // diastole
        int diastole = bb.getInt();
        Log.i(TAG,"Diastole: " + diastole);

        // suhu
        float suhu = bb.getFloat();
        Log.i(TAG,"Suhu: " + suhu);

        // nadi
        byte[] nadi = new byte[4];
        bb.get(nadi, 1, 3);
        nadi[0] = 0;
        Log.i(TAG,"Nadi: " + ByteBuffer.wrap(nadi).getInt());

        // respirasi
        byte[] respirasi = new byte[4];
        bb.get(respirasi, 1, 3);
        respirasi[0] = 0;
        Log.i(TAG,"Respirasi: " + ByteBuffer.wrap(respirasi).getInt());

        // keluhan utama
        byte[] keluhanUtama = new byte[50];
        bb.get(keluhanUtama, 0, 50);
        Log.i(TAG,"Keluhan Utama: " + bytesToString(trimZeroPadding(keluhanUtama)));

        // riwayat penyakit sekarang
        byte[] riwayatPenyakitSekarang = new byte[200];
        bb.get(riwayatPenyakitSekarang, 0, 200);
        Log.i(TAG,"Riwayat penyakit Sekarang: " + bytesToString(trimZeroPadding(riwayatPenyakitSekarang)));

        // riwayat penyakit dahulu
        byte[] riwayatPenyakitDahulu = new byte[100];
        bb.get(riwayatPenyakitDahulu, 0, 100);
        Log.i(TAG,"Riwayat Penyakit Dahulu: " + bytesToString(trimZeroPadding(riwayatPenyakitDahulu)));

        // riwayat penyakit keluarga
        byte[] riwayatPenyakitKeluarga = new byte[100];
        bb.get(riwayatPenyakitKeluarga, 0, 100);
        Log.i(TAG,"Riwayat Penyakit Keluarga: " + bytesToString(trimZeroPadding(riwayatPenyakitKeluarga)));

        // tinggi
        int tinggi = bb.getInt();
        Log.i(TAG,"Tinggi: " + tinggi);

        // berat
        int berat = bb.getInt();
        Log.i(TAG,"Berat: " + berat);

        // kesadaran
        byte kesadaran = bb.get();
        Log.i(TAG,"Kesadaran: " + kesadaran);

        // kepala
        byte[] kepala = new byte[50];
        bb.get(kepala, 0, 50);
        Log.i(TAG,"Kepala: " + bytesToString(trimZeroPadding(kepala)));

        // thorax
        byte[] thorax = new byte[50];
        bb.get(thorax, 0, 50);
        Log.i(TAG,"Thorax: " + bytesToString(trimZeroPadding(thorax)));

        // abdomen
        byte[] abdomen = new byte[50];
        bb.get(abdomen, 0, 50);
        Log.i(TAG,"Abdomen: " + bytesToString(trimZeroPadding(abdomen)));

        // genitalia
        byte[] genitalia = new byte[50];
        bb.get(genitalia, 0, 50);
        Log.i(TAG,"Genitalia: " + bytesToString(trimZeroPadding(genitalia)));

        // extremitas
        byte[] extremitas = new byte[50];
        bb.get(extremitas, 0, 50);
        Log.i(TAG,"Extremitas: " + bytesToString(trimZeroPadding(extremitas)));

        // kulit
        byte[] kulit = new byte[50];
        bb.get(kulit, 0, 50);
        Log.i(TAG,"Kulit: " + bytesToString(trimZeroPadding(kulit)));

        // neurologi
        byte[] neurologi = new byte[50];
        bb.get(neurologi, 0, 50);
        Log.i(TAG,"Neurologi: " + bytesToString(trimZeroPadding(neurologi)));

        // laboratorium
        byte[] laboratorium = new byte[200];
        bb.get(laboratorium, 0, 200);
        Log.i(TAG,"Laboratorium: " + bytesToString(trimZeroPadding(laboratorium)));

        // radiolgi
        byte[] radiologi = new byte[200];
        bb.get(radiologi, 0, 200);
        Log.i(TAG,"Radiologi: " + bytesToString(trimZeroPadding(radiologi)));

        // status labradio
        byte statusLabRadio = bb.get();
        Log.i(TAG,"Status LabRadio: " + statusLabRadio);

        // diagnosis kerja
        byte[] diagnosisKerja = new byte[200];
        bb.get(diagnosisKerja, 0, 200);
        Log.i(TAG,"Diagnosis Kerja: " + bytesToString(trimZeroPadding(diagnosisKerja)));

        // diagnosis banding
        byte[] diagnosisBanding = new byte[200];
        bb.get(diagnosisBanding, 0, 200);
        Log.i(TAG,"Diagnosis Banding: " + bytesToString(trimZeroPadding(diagnosisBanding)));

        // icd10
        byte[] icd10 = new byte[200];
        bb.get(icd10, 0, 200);
        Log.i(TAG,"ICD 10: " + bytesToString(trimZeroPadding(icd10)));

        // resep
        byte[] resep = new byte[200];
        bb.get(resep, 0, 200);
        Log.i(TAG,"Resep: " + bytesToString(trimZeroPadding(resep)));

        // catatan resep
        byte[] catatanResep = new byte[50];
        bb.get(catatanResep, 0, 50);
        Log.i(TAG,"Catatan Resep: " + bytesToString(trimZeroPadding(catatanResep)));

        // status resep
        byte statusResep = bb.get();
        Log.i(TAG,"Status Resep: " + statusResep);

        // repetisi resep
        byte repetisiResep = bb.get();
        Log.i(TAG,"Repetisi Resep: " + repetisiResep);

        // tindakan
        byte[] tindakan = new byte[200];
        bb.get(tindakan, 0, 200);
        Log.i(TAG,"Tindakan: " + bytesToString(trimZeroPadding(tindakan)));

        // advitam
        byte adVitam = bb.get();
        Log.i(TAG,"Ad vitam: " + adVitam);

        // ad functionam
        byte adFunctionam = bb.get();
        Log.i(TAG,"Ad functionam: " + adFunctionam);

        // ad sanationam
        byte adSanationam = bb.get();
        Log.i(TAG,"Ad sanationam: " + adSanationam);

        mdd = new MedrecDinamikData(noIdx,
                bytesToDate(date),
                bytesToString(idpuskesmas),
                poli,
                bytesToString(pemberiRujukan),
                systole,
                diastole,
                suhu,
                ByteBuffer.wrap(nadi).getInt(),
                ByteBuffer.wrap(respirasi).getInt(),
                bytesToString(keluhanUtama),
                bytesToString(riwayatPenyakitSekarang),
                bytesToString(riwayatPenyakitDahulu),
                bytesToString(riwayatPenyakitKeluarga),
                tinggi,
                berat,
                kesadaran,
                bytesToString(kepala),
                bytesToString(thorax),
                bytesToString(abdomen),
                bytesToString(genitalia),
                bytesToString(extremitas),
                bytesToString(kulit),
                bytesToString(neurologi),
                bytesToString(laboratorium),
                bytesToString(radiologi),
                statusLabRadio,
                bytesToString(diagnosisKerja),
                bytesToString(diagnosisBanding),
                bytesToString(icd10),
                bytesToString(resep),
                bytesToString(catatanResep),
                statusResep,
                repetisiResep,
                bytesToString(tindakan),
                adVitam,
                adFunctionam,
                adSanationam
                );

        mddArray.add(mdd);

        /*
         * Masukkan ke internal DB
         */

        EhealthDbHelper mDbHelper = new EhealthDbHelper(getActivity());
        mDbHelper.openDB();
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mTanggalPeriksa = String.valueOf(formatter.format(bytesToDate(date).getTime()));

        Cursor cursor = db.rawQuery("SELECT * FROM "+EhealthContract.RekamMedisEntry.TABLE_NAME+" WHERE "+EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA+" = '"+mTanggalPeriksa+"';", null);
        if(cursor.getCount()==0) {
            values.put(EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA, mTanggalPeriksa);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER, HPCData.nama);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_NIK, PDCData.nik);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS, bytesToString(idpuskesmas));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_POLI, poli);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN, bytesToString(pemberiRujukan));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE, systole);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE, diastole);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_SUHU, suhu);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_NADI, ByteBuffer.wrap(nadi).getInt());
            values.put(EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI, ByteBuffer.wrap(respirasi).getInt());
            values.put(EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA, bytesToString(keluhanUtama));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG, bytesToString(riwayatPenyakitSekarang));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU, bytesToString(riwayatPenyakitDahulu));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL, bytesToString(riwayatPenyakitKeluarga));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_TINGGI, tinggi);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_BERAT, berat);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_KESADARAN, kesadaran);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_KEPALA, bytesToString(kepala));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_THORAX, bytesToString(thorax));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN, bytesToString(abdomen));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_GENITALIA, bytesToString(genitalia));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS, bytesToString(extremitas));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_KULIT, bytesToString(kulit));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI, bytesToString(neurologi));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM, bytesToString(laboratorium));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI, bytesToString(radiologi));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO, statusLabRadio);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA, bytesToString(diagnosisKerja));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING, bytesToString(diagnosisBanding));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA, bytesToString(icd10));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_RESEP, bytesToString(resep));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP, bytesToString(catatanResep));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP, statusResep);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP, repetisiResep);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN, bytesToString(tindakan));
            values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM, adVitam);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM, adFunctionam);
            values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM, adSanationam);

            // Insert a new row for pet in the database, returning the ID of that new row.
            long newRowId = db.insert(EhealthContract.RekamMedisEntry.TABLE_NAME, null, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                //Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                //Toast.makeText(this, "Data saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
                //simpanData();
                //finish();
            }
        }
        mDbHelper.closeDB();
    }

    public void send() {
        if ( i == 0 ) {
            serialPort.write(APDU_select);
            i++;
            Log.i(TAG, "write apdu select");
        } else if (i == 1) {
            serialPort.write(APDU_read_medrec_dinamik1);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 1");
        } else if (i == 2) {
            serialPort.write(APDU_read_medrec_dinamik2);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 2");
            showToastOnUi("Baca medrec 1 selesai");
        } else if (i == 3) {
            serialPort.write(APDU_read_medrec_dinamik3);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 3");
            showToastOnUi("Baca medrec 2 selesai");
        } else if (i == 4) {
            serialPort.write(APDU_read_medrec_dinamik4);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 4");
            showToastOnUi("Baca medrec 3 selesai");
        } else if (i == 5) {
            serialPort.write(APDU_read_medrec_dinamik5);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 5");
            showToastOnUi("Baca medrec 4 selesai");
        }
        else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            int index;
            index = Util.getWriteIndex(mddArray);
            Log.d(TAG, "Write index = " + index);
            showToastOnUi("Baca medrec dinamik BERHASIL!");
            MedrecDinamikData.isInDatabase = 1;
            MedrecDinamikData.writeIndex =  index;
//            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    private void showToastOnUi(String text) {
        final String ftext = text;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), ftext, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bacaMedrekDinamik(){
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.progress_bar);
        myDialog.setCancelable(false);

        textView = (TextView) myDialog.findViewById(R.id.textView);
        progressBar = (ProgressBar) myDialog.findViewById(R.id.progressBar);

        myDialog.show();
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
                                myDialog.cancel();
                                getFragmentManager().beginTransaction().detach(RekmedFragment.this).attach(RekmedFragment.this).commit();
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

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "RekmedFragment.onPause() has been called.");
        try {
            if (broadcastReceiver != null) {
                getActivity().unregisterReceiver(broadcastReceiver);
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG,"epicReciver is already unregistered");
//            broadcastReceiver = null;
        }
    }

}