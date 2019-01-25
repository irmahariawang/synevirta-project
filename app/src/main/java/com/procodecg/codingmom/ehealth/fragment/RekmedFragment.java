package com.procodecg.codingmom.ehealth.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
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
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedbaruActivity;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToDate;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToHex;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToLong;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.bytesToString;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.hexStringToByteArray;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.padVariableText;
import static com.procodecg.codingmom.ehealth.hpcpdc_card.Util.trimZeroPadding;

/**
 * (c) 2017
 * Created by :
 *      Coding Mom
 *      Annisa Alifiani
 *      Arieza Nadya
 */

public class RekmedFragment extends Fragment {

    // View
    Dialog myDialog;
    ProgressBar progressBar;
    private ProgressDialog progressDialog;
    TextView textView;

    // Variable
    int index;
    int progressStatus = 0;
    Handler handler = new Handler();

    // Db
    EhealthDbHelper mDbHelper;

    // USB Accesories
    final String TAG = "EHEALTHREKMEDFRAGMENT";
    public final String ACTION_USB_PERMISSION = "com.nehceh.hpcpdc.USB_PERMISSION";

    private byte[] chunk1, chunk2, chunk3, chunk4, chunk5, chunk6, chunk7, chunk8, chunk9, chunk10,
            chunk11, chunk12, chunk13;

    ArrayList<MedrecDinamikData> mddArray = new ArrayList<>();
    ArrayList<Date> timestamp = new ArrayList<>();
    ArrayList<String> tanggalPeriksa = new ArrayList<>();
    MedrecDinamikData mdd;
    String data, puskesmasID;
    int i; // buat increment serial tulis apdu

    ByteBuffer respondData;
    IntentFilter filter;

    byte[] selectResponse, indexResponse, medrecDinamikResponse, checkingResponse, timestampResponse, initTulisResponse;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    // APDU command
    //00 A4 04 00 08 50 44 43 44 55 4D 4D 59
    byte[] APDU_select = {0x00, (byte)0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    byte[] APDU_card_checking = {(byte)0x80, (byte)0xB2, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_index = {(byte)0x80, (byte)0xD7, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_timestamp = {(byte)0x80, (byte)0xD6, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik1 = {(byte)0x80, (byte)0xD5, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik2 = {(byte)0x80, (byte)0xD5, 0x00, 0x01, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik3 = {(byte)0x80, (byte)0xD5, 0x00, 0x02, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik4 = {(byte)0x80, (byte)0xD5, 0x00, 0x03, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrec_dinamik5 = {(byte)0x80, (byte)0xD5, 0x00, 0x04, 0x00, 0x00, 0x00};
    byte[] APDU_finish = {(byte)0x80, (byte)0xC7, 0x00, 0x00, 0x00, 0x00, 0x00};

    public static RekmedFragment newInstance() {
        RekmedFragment fragment = new RekmedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDbHelper = new EhealthDbHelper(getActivity());
        mDbHelper.openDB();
        Log.i(TAG, "Redmed fragment onCreate");

        SharedPreferences prefs = getContext().getSharedPreferences("DATAPUSKES", MODE_PRIVATE);
        puskesmasID = prefs.getString("IDPUSKES", "");
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
                Toast.makeText(getActivity(), "USB Empty", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Usb devices empty");

//                /*selama usb permissionnya belum bisa sekali, pake intent*/
//                myDialog.cancel();
//
//                ProfilpasienFragment fragment = new ProfilpasienFragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, fragment);
//                fragmentTransaction.commit();

                Intent intent = new Intent(getActivity(), PasiensyncActivity.class);
                startActivity(intent);
                getActivity().finish();

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
                            Toast.makeText(getActivity(), "Port not Open", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.w(TAG, "PORT IS NULL");
                        Toast.makeText(getActivity(), "Port is Null", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                Log.i(TAG,"ACTION_USB_DEVICE_DETACHED");

                i=0;
                getActivity().unregisterReceiver(broadcastReceiver);
                Intent activity = new Intent(getActivity(), PasiensyncActivity.class);
                startActivity(activity);

            } else {
                Log.w(TAG, "NO INTENT?");
            }
        }
    };

    // Membaca APDU response
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.d(TAG, "Received bytes");
            data = bytesToHex(bytes);
            Log.d(TAG, "Data " + data);

            if (i == 1) { // Select applet
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);
                    Log.i(TAG, "Select response string: " + bytesToHex(selectResponse));
                    send();
                }
            } else if (i == 2) { // Baca jumlah medrek dinamik
                respondData.put(bytes);
                if (respondData.position() == 3) {
                    byte[] recordIndex;
                    indexResponse = new byte[3];
                    respondData.rewind();
                    respondData.get(indexResponse);
                    respondData.position(0);

                    recordIndex = Arrays.copyOfRange(indexResponse, 0, 1);
                    index = Integer.valueOf(bytesToHex(recordIndex));
                    Log.i(TAG, "Select record index string: " + bytesToHex(indexResponse));
                    send();
                }
            } else if (i == 3) { // Baca timestamp rekmed dinamik
                respondData.put(bytes);
                byte[] response;
                if (index == 0 && respondData.position() == 2) {
                    timestampResponse = new byte[6];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    send();
                } else if (index == 1 && respondData.position() == 6) {
                    timestampResponse = new byte[6];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    response = Arrays.copyOfRange(timestampResponse, 0, 4);
                    processTimestamp(response);
                    send();
                } else if (index == 2 && respondData.position() == 10) {
                    timestampResponse = new byte[10];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    response = Arrays.copyOfRange(timestampResponse, 0, 8);
                    processTimestamp(response);
                    send();
                } else if (index == 3 && respondData.position() == 14) {
                    timestampResponse = new byte[14];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    response = Arrays.copyOfRange(timestampResponse, 0, 12);
                    processTimestamp(response);
                    send();
                } else if (index == 4 && respondData.position() == 18) {
                    timestampResponse = new byte[18];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    response = Arrays.copyOfRange(timestampResponse, 0, 16);
                    processTimestamp(response);
                    send();
                } else if (index == 5 && respondData.position() == 22) {
                    timestampResponse = new byte[22];
                    respondData.rewind();
                    respondData.get(timestampResponse);
                    respondData.position(0);

                    response = Arrays.copyOfRange(timestampResponse, 0, 20);
                    processTimestamp(response);
                    send();
                }
            } else if (i == 4) { // Baca flag dan puskesmas ID
                respondData.put(bytes);
                if (respondData.position() == 15) {
                    checkingResponse = new byte[15];
                    respondData.rewind();
                    respondData.get(checkingResponse);
                    respondData.position(0);

                    byte[] flag = Arrays.copyOfRange(checkingResponse, 0, 1);
                    byte[] puskesID = Arrays.copyOfRange(checkingResponse, 1, 12);

                    Log.i(TAG, "Checking flag: " + Integer.valueOf(bytesToHex(flag)) + "Puskesmas ID: " + bytesToString(puskesID));

                    if (Integer.valueOf(bytesToHex(flag)) != 2 && bytesToString(puskesID).equals(puskesmasID)) {
                        recoveryPermission();
                    } else {
                        i = 19;
                        send();
                    }
                }
            } else if (i == 5) { // Init tulis rekmed dinamik
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    initTulisResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(initTulisResponse);
                    respondData.position(0);

                    Log.i(TAG, "Init response: " + Util.bytesToHex(initTulisResponse));
                    if (!Util.bytesToHex(initTulisResponse).toString().equals("9000")) {
                        i--;
                        Log.e(TAG, "Init tulis gagal" + i);
                    } else {
                        send();
                    }
                }
            } else if (i > 5 && i < 20) { // Insert chunk 1-14 dan ins_final_tulis_medrec_dinamik
                respondData.put(bytes);
                if (respondData.position() == 2) {
                    selectResponse = new byte[2];
                    respondData.rewind();
                    respondData.get(selectResponse);
                    respondData.position(0);

                    Log.i(TAG, "Insert response: " + Util.bytesToHex(selectResponse));
                    if (!Util.bytesToHex(selectResponse).toString().equals("9000")) { // jika tidak berhasil
                        i--;
                        Log.e(TAG, "GAGAL INSERT: " + i);
                    } else {
                        Log.d(TAG, "Berhasil INSERT: " + i);
                        if(MedrecDinamikData.writeIndex == 0 && !MedrecDinamikData.fullData) {
                            i = 24;
                        } else {
                            send();
                        }
                    }
                }
            } else if (i > 19 && i < 25) { // Baca rekmed dinamik
                respondData.put(bytes);
                if (respondData.position() == 2336) {
                    medrecDinamikResponse = new byte[2336];
                    respondData.rewind();
                    respondData.get(medrecDinamikResponse);
                    respondData.position(0);

                    byte[] response = Arrays.copyOfRange(medrecDinamikResponse, 0, 2334);

                    int x = (i-19)*10;
                    setProgressBar(x);

                    if(responseVerifier(Util.bytesToHex(response))) {
                        processDinamikData(medrecDinamikResponse);
                        if(i-19 >= MedrecDinamikData.writeIndex && index != 5){
                            i = 24;
                            setProgressBar(50);
                        }
                    } else {
                        i = 24;
                        setProgressBar(50);
                    }

                    send();
                }
            } else if(i == 100){
                i = 24;
                send();
            } else {
                Log.i(TAG, "no i " + i);
            }
        }
    };

    // Filter APDU response
    private boolean responseVerifier(String response){
        Pattern pattern = Pattern.compile("[1-9]");
        Matcher matcher = pattern.matcher(response);

        return matcher.find();
    }

    // Convert timestamp
    public void processTimestamp(byte[] data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.rewind();

        int mod = data.length/4;
        byte[] date = new byte[4];

        for(int i=0; i<mod; i++){
            bb.get(date, 0, 4);
            String mTanggalPeriksa = String.valueOf(formatter.format(bytesToDate(date).getTime()));
            tanggalPeriksa.add(mTanggalPeriksa);
            try {
                Date tanggalPeriksa = formatter.parse(mTanggalPeriksa);
                timestamp.add(tanggalPeriksa);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        Log.i(TAG, timestamp.toString());
        Log.i(TAG, tanggalPeriksa.toString());
    }

    // Proses rekmed dinamik
    public void processDinamikData(byte[] data) {
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

    // Kirim APDU command
    public void send() {
        if (i == 0) {
            serialPort.write(APDU_select);
            i++;
            Log.i(TAG, "write apdu select");
        } else if (i == 1) {
            serialPort.write(APDU_read_index);
            i++;
            Log.i(TAG, "get record index");
        } else if (i == 2) {
            serialPort.write(APDU_read_medrec_timestamp);
            i++;
            Log.i(TAG, "get timestamp medrec");
        } else if (i == 3) {
            serialPort.write(APDU_card_checking);
            i++;
            Log.i(TAG, "pengecekan flag pada kartu");

            int ind;
            ind = Util.getWriteIndex(timestamp);
            MedrecDinamikData.writeIndex = ind;
            Log.d(TAG, "Write index = " + ind);

            if(MedrecDinamikData.writeIndex == 0 && index == 0){
                i = 100;
            }
        } else if (i == 4){
            String apduInit = "80c5000000000c";
            apduInit += padVariableText(puskesmasID, 12);
            serialPort.write(hexStringToByteArray(apduInit));
            i++;
            Log.i(TAG, "init tulis");
        } else if (i == 5) {
            serialPort.write(chunk1);
            i++;
            Log.i(TAG, "write insert medrec: c1");
        } else if (i == 6) {
            serialPort.write(chunk2);
            i++;
            Log.i(TAG, "write insert medrec: c2");
        } else if (i == 7) {
            serialPort.write(chunk3);
            i++;
            Log.i(TAG, "write insert medrec: c3");
        } else if (i == 8) {
            serialPort.write(chunk4);
            i++;
            Log.i(TAG, "write insert medrec: c4");
        } else if (i == 9) {
            serialPort.write(chunk5);
            i++;
            Log.i(TAG, "write insert medrec: c5");
        } else if (i == 10) {
            serialPort.write(chunk6);
            i++;
            Log.i(TAG, "write insert medrec: c6");
        } else if (i == 11) {
            serialPort.write(chunk7);
            i++;
            Log.i(TAG, "write insert medrec: c7");
        } else if (i == 12) {
            serialPort.write(chunk8);
            i++;
            Log.i(TAG, "write insert medrec: c8");
        } else if (i == 13) {
            serialPort.write(chunk9);
            i++;
            Log.i(TAG, "write insert medrec: c9");
        } else if (i == 14) {
            serialPort.write(chunk10);
            i++;
            Log.i(TAG, "write insert medrec: c10");
        } else if (i == 15) {
            serialPort.write(chunk11);
            i++;
            Log.i(TAG, "write insert medrec: c11");
        } else if (i == 16) {
            serialPort.write(chunk12);
            i++;
            Log.i(TAG, "write insert medrec: c12");
        } else if (i == 17) {
            serialPort.write(chunk13);
            i++;
            Log.i(TAG, "write insert medrec: c13");
        } else if (i == 18) {
            serialPort.write(APDU_finish);
            i++;
            Log.i(TAG, "write APDU finish");
            if(MedrecDinamikData.writeIndex == 4){
                MedrecDinamikData.fullData = true;
                MedrecDinamikData.writeIndex = 0;
            } else {
                MedrecDinamikData.writeIndex += 1;
            }
        } else if (i == 19) {
            serialPort.write(APDU_read_medrec_dinamik1);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 1");
        } else if (i == 20) {
            serialPort.write(APDU_read_medrec_dinamik2);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 2");
            showToastOnUi("Baca medrec 1 selesai");
        } else if (i == 21) {
            serialPort.write(APDU_read_medrec_dinamik3);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 3");
            showToastOnUi("Baca medrec 2 selesai");
        } else if (i == 22) {
            serialPort.write(APDU_read_medrec_dinamik4);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 4");
            showToastOnUi("Baca medrec 3 selesai");
        } else if (i == 23) {
            serialPort.write(APDU_read_medrec_dinamik5);
            i++;
            Log.i(TAG, "write apdu read medrec dinamik 5");
            showToastOnUi("Baca medrec 4 selesai");
        } else {
            serialPort.close();
            Log.i(TAG, "serial port closed");
            showToastOnUi("Baca medrec dinamik BERHASIL!");
            MedrecDinamikData.isInDatabase = 1;
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

    // Jika flag tulis rekmed dinamik tidak final
    private void recoveryPermission() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setIcon(R.drawable.logo2);
                mBuilder.setTitle("Perhatian");
                mBuilder.setMessage("Apakah Anda ingin melakukan recovery?");
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i1) {
                        SQLiteDatabase db = mDbHelper.getReadableDatabase();
                        String[] projection = {
                                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA
                        };
                        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, projection, ""+ EhealthContract.RekamMedisEntry.COLUMN_NIK+"=?", new String[]{PDCData.nik}, null, null, EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA+" DESC", "5");
                        ArrayList<String> listTanggal = new ArrayList<>();
                        int columnTanggal = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA);
                        if (cursor.moveToFirst()) {
                            do {
                                String tanggal = cursor.getString(columnTanggal);
                                listTanggal.add(tanggal);
                            } while (cursor.moveToNext());
                        }
                        Log.i(TAG, listTanggal.toString());

                        if(index == 5) {
                            MedrecDinamikData.writeIndex -= 1;
//                        } else {
//                            ArrayList<Integer> hasil_compare= new ArrayList<Integer>();
//                            for (String temp : listTanggal)
//                                hasil_compare.add(tanggalPeriksa.contains(temp) ? 1 : 0);
//
//                            Log.i(TAG+" compare", hasil_compare.toString());
//
//                            ArrayList<String> timestamp_recovery = new ArrayList<String>();
//                            for (int x=0; x<hasil_compare.size(); x++){
//                                if(hasil_compare.get(x) == 0){
//                                    timestamp_recovery.add(listTanggal.get(x));
//                                }
//                            }
//
//                            query = "SELECT * FROM "+ EhealthContract.RekamMedisEntry.TABLE_NAME+
//                                    " WHERE "+EhealthContract.RekamMedisEntry.COLUMN_NIK+" = '"+PDCData.nik+"' AND ";
//                            for(int x=0; x<timestamp_recovery.size(); x++) {
//                                query += EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA + " = '" + timestamp_recovery.get(x);
//                                if(x != timestamp_recovery.size()-1){
//                                    query += " OR ";
//                                }
//                            }
//                            query+= "';";
                        }

                        String query = "SELECT * FROM "+ EhealthContract.RekamMedisEntry.TABLE_NAME+
                                " WHERE "+EhealthContract.RekamMedisEntry.COLUMN_NIK+" = '"+PDCData.nik+"' AND "+
                                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA + " = '"+listTanggal.get(0)+"';";

                        Cursor getData = db.rawQuery(query, null);
                        Log.i(TAG, ""+getData.getCount());
                        ContentValues values = new ContentValues();
                        if(getData.moveToFirst()){
                            do {
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA, getData.getString(1));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER, getData.getString(2));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_NIK, getData.getString(3));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS, getData.getString(4));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_POLI, getData.getString(5));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN, getData.getString(6));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE, getData.getString(7));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE, getData.getString(8));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_SUHU, getData.getString(9));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_NADI, getData.getString(10));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI, getData.getString(11));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA, getData.getString(12));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG, getData.getString(13));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU, getData.getString(14));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL, getData.getString(15));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_TINGGI, getData.getString(16));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_BERAT, getData.getString(17));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_KESADARAN, getData.getString(18));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_KEPALA, getData.getString(19));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_THORAX, getData.getString(20));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN, getData.getString(21));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_GENITALIA, getData.getString(22));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS, getData.getString(23));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_KULIT, getData.getString(24));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI, getData.getString(25));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM, getData.getString(26));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI, getData.getString(27));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO, getData.getString(28));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA, getData.getString(29));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING, getData.getString(30));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA, getData.getString(31));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_RESEP, getData.getString(32));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP, getData.getString(33));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP, getData.getString(34));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP, getData.getString(35));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN, getData.getString(36));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM, getData.getString(37));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM, getData.getString(38));
                                values.put(EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM, getData.getString(39));
                            } while (getData.moveToNext());
                        }

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
//                        showLoader();
                    }
                });
                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();
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

    // Progress Bar
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
    }

    // Menyiapkan APDU command
    private String makeAPDUInsertCommand(ContentValues cv, int writeIndex, int chunk) {
        switch(chunk) {
            case 1:
                String cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00"; // 00
                cmd += "007b"; // Total length
                cmd += "0000"; // Start pointer
                cmd += "0077"; // actual data length
                cmd += Util.bytesToHex(Util.intToBytes(writeIndex)); // data index
                cmd += Util.bytesToHex(Util.dateToBytes(Util.getTimeMilisDate(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA)))); // tglperiksa
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS), 12); // idpuskesmas
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_POLI)); // poli
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN), 30); // pemberi rujukan
                cmd += Util.intToHex(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE)); // systole
                cmd += Util.intToHex(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE)); // diastole
                cmd += Util.bytesToHex(Util.floatToBytes(cv.getAsFloat(EhealthContract.RekamMedisEntry.COLUMN_SUHU))); // suhu
                cmd += Util.intToHex3(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_NADI)); // nadi
                cmd += Util.intToHex3(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI)); // respirasi
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA), 50); // keluhan utama
                assert cmd.length() == 260;
                return cmd;
            case 2:
                // cmd is declared during compiled time
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0077"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG), 200); // riwayat penyakit sekarang
                assert cmd.length() == 422;
                return cmd;
            case 3:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "013F"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU), 100);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL), 100);
                assert cmd.length() == 422;
                return cmd;
            case 4:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00A3"; // total length
                cmd += "0207"; // start pointer
                cmd += "009F"; // actual data length
                cmd += Util.intToHex(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_TINGGI));
                cmd += Util.intToHex(cv.getAsInteger(EhealthContract.RekamMedisEntry.COLUMN_BERAT));
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_KESADARAN));
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_KEPALA), 50);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_THORAX), 50);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN), 50);
                assert cmd.length() == 340;
                return cmd;
            case 5:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "02A6"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_GENITALIA), 50);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS), 50);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_KULIT), 50);
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI), 50);
                assert cmd.length() == 422;
                return cmd;
            case 6:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "036E"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM), 200);
                assert cmd.length() == 422;
                return cmd;
            case 7:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0436"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI), 200);
                assert cmd.length() == 422;
                return cmd;
            case 8:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CD"; // total length
                cmd += "04FE"; // start pointer
                cmd += "00C9"; // actual data length
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO));
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA), 200);
                assert cmd.length() == 424;
                return cmd;
            case 9:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "05C7"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING), 200);
                assert cmd.length() == 422;
                return cmd;
            case 10:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "068F"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA), 200);
                assert cmd.length() == 422;
                return cmd;
            case 11:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CC"; // total length
                cmd += "0757"; // start pointer
                cmd += "00C8"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_RESEP), 200);
                assert cmd.length() == 422;
                return cmd;
            case 12:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "0036"; // total length
                cmd += "081F"; // start pointer
                cmd += "0034"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP), 50);
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP));
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP));
                assert cmd.length() == 126;
                return cmd;
            case 13:
                cmd = "80c6"; // CLA|INS
                cmd += Util.bytesToHex(Util.intToShortToBytes(writeIndex)); // internal index / P1P2
                cmd += "00";
                cmd += "00CF"; // total length
                cmd += "0853"; // start pointer
                cmd += "00CB"; // actual data length
                cmd += Util.padVariableText(cv.getAsString(EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN), 200);
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM));
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM));
                cmd += String.format("%02X", cv.getAsByte(EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM));
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
        progressDialog = ProgressDialog.show(getActivity(), "E-health",
                "Melakukan recovery, harap tunggu", true);
    }
}