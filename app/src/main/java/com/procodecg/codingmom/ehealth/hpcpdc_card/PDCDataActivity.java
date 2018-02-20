package com.procodecg.codingmom.ehealth.hpcpdc_card;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Atia on 20-Nov-17.
 */

public class PDCDataActivity {
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

    TextView goldar, alergi, riwayatOperasi, riwayatRawat, riwayatPenyakitKronis, riwayatPenyakitBawaan, faktorRisiko,
            nikTv, namaTv, alamatTv, kategoriPasienTv, noAsuransiTv, tglDaftarTv, namaKKTv,hubKeluargaTv;

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbDeviceConnection usbConn;
    UsbSerialDevice serialPort;

    //00 A4 04 00 08 50 44 43 44 55 4D 4D 59
    byte[] APDU_select = {0x00, (byte)0xA4, 0x04, 0x00, 0x08, 0x50, 0x44, 0x43, 0x44, 0x55, 0x4D, 0x4D, 0x59};
    // 80D40000000000
    byte[] APDU_read_medrec_statik = {(byte)0x80, (byte)0xD4, 0x00, 0x00, 0x00, 0x00, 0x00};
    // 80D30000000000
    byte[] APDU_read_biodata = {(byte)0x80, (byte)0xD3, 0x00, 0x00, 0x00, 0x00, 0x00};
    // 80D50001000000
    byte[] APDU_read_medrecDinamik1 = {(byte)0x80, (byte)0xD5, 0x00, 0x00, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik2 = {(byte)0x80, (byte)0xD5, 0x00, 0x01, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik3 = {(byte)0x80, (byte)0xD5, 0x00, 0x02, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik4 = {(byte)0x80, (byte)0xD5, 0x00, 0x03, 0x00, 0x00, 0x00};
    byte[] APDU_read_medrecDinamik5 = {(byte)0x80, (byte)0xD5, 0x00, 0x04, 0x00, 0x00, 0x00};

    public PDCDataActivity (Context context) {

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdc_fragment_medrecstatik);
*/
        /**/
//        setContentView(R.layout.pdcdata_activity);

//        ViewPager viewPager = findViewById(R.id.pdc_pager);
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        adapter.addFragment(new PDCBiodataFragment(), "Biodata");
//        adapter.addFragment(new PDCMedrecStatikFragment(), "Medrec Statik");
//
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = findViewById(R.id.pdc_tabs);
//        tabLayout.setupWithViewPager(viewPager);

        /*
        goldar = findViewById(R.id.pdc_data_golongandarah);
        alergi = findViewById(R.id.pdc_data_alergi);
        riwayatOperasi = findViewById(R.id.pdc_data_riwayatoperasi);
        riwayatRawat = findViewById(R.id.pdc_data_riwayatrawatrs);
        riwayatPenyakitKronis = findViewById(R.id.pdc_data_riwayatpenyakitkronis);
        riwayatPenyakitBawaan = findViewById(R.id.pdc_data_riwayatpenyakitbawaan);
        faktorRisiko = findViewById(R.id.pdc_data_faktorrisiko);

        nikTv = findViewById(R.id.pdc_data_nik);
        kategoriPasienTv = findViewById(R.id.pdc_data_kategoripasien);
        noAsuransiTv = findViewById(R.id.pdc_data_nomorasuransi);
        tglDaftarTv = findViewById(R.id.pdc_data_tgldaftar);
        namaTv = findViewById(R.id.pdc_data_namapasien);
        namaKKTv = findViewById(R.id.pdc_data_namakk);
        hubKeluargaTv = findViewById(R.id.pdc_data_hubungankeluarga);
        alamatTv = findViewById(R.id.pdc_data_alamat);
*/
        i=0;

        respondData = ByteBuffer.allocate(2469);

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
            Toast.makeText(context.getApplicationContext(), "Usb devices empty", Toast.LENGTH_SHORT).show();
        }
    }

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }

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

                            send();
//                            try {
//                                send();
//                                Thread.sleep(2000);
//                                send();
//                                Thread.sleep(6000);
//                                send();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        } else {
                            Log.w(TAG, "PORT NOT OPEN");
                        }
                    } else {
                        Log.w(TAG, "PORT IS NULL");
                    }
                } else {
                    Log.w(TAG, "PERMISSION NOT GRANTED");
                }
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
                    byte[] nik, kategoriPasien, nomorAsuransi, tglDaftar, nama, namaKK, hubunganKeluarga, alamat;
                    biodataResponse = new byte[826];
                    respondData.rewind();
                    respondData.get(biodataResponse);
                    respondData.position(0);

                    Log.i(TAG, "Biodata: " + Util.bytesToHex(biodataResponse));

                    nik = Arrays.copyOfRange(biodataResponse, 0, 16);
                    kategoriPasien = Arrays.copyOfRange(biodataResponse, 16, 68);
                    nomorAsuransi = Arrays.copyOfRange(biodataResponse, 68, 150);
                    tglDaftar = Arrays.copyOfRange(biodataResponse, 150, 154);
                    nama = Arrays.copyOfRange(biodataResponse, 154, 206);
                    namaKK = Arrays.copyOfRange(biodataResponse, 206, 258);
                    hubunganKeluarga = Arrays.copyOfRange(biodataResponse, 258, 259);
                    alamat = Arrays.copyOfRange(biodataResponse, 259, 361);
/*
                    tvSet(nikTv, Util.bytesToString(Util.trimZeroPadding(nik)));
                    tvSet(kategoriPasienTv, Util.bytesToString(Util.trimZeroPadding(kategoriPasien)));
                    tvSet(noAsuransiTv, Util.bytesToString(Util.trimZeroPadding(nomorAsuransi)));
                    tvSet(namaTv, Util.bytesToString(Util.trimZeroPadding(nama)));
                    tvSet(namaKKTv, Util.bytesToString(Util.trimZeroPadding(namaKK)));
                    tvSet(hubKeluargaTv, Util.bytesToString(Util.trimZeroPadding(hubunganKeluarga)));
                    tvSet(alamatTv, Util.bytesToString(Util.trimZeroPadding(alamat)));
*/
//                    Log.i(TAG, "nik: " + Util.bytesToString(Util.trimZeroPadding(nik)));
//                    Log.i(TAG, "kategori pasien: " + Util.bytesToString(Util.trimZeroPadding(kategoriPasien)));
//                    Log.i(TAG, "nomor asuransi: " + Util.bytesToString(Util.trimZeroPadding(nomorAsuransi)));
//                    Log.i(TAG, "tgl daftar: " + Util.bytesToString(Util.trimZeroPadding(tglDaftar)));
//                    Log.i(TAG, "nama: " + Util.bytesToHex(nama));
//                    Log.i(TAG, "namakk: " + Util.bytesToHex(namaKK));
//                    Log.i(TAG, "hub keluarga: " + Util.bytesToString(Util.trimZeroPadding(hubunganKeluarga)));
//                    Log.i(TAG, "alamat: " + Util.bytesToHex(alamat));

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
        }

    }
/*
    private void tvSet(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ftv.setText(ftext);
            }
        });
    }
*/

/*
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
*/
}
