package com.procodecg.codingmom.ehealth.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecDinamikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.MedrecStatikData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedDinamisFragment;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedStatisFragment;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookpro on 8/29/17.
 */

public class RekmedFragment extends Fragment {

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
        // Komunikasi dengan kartu
        i = 0;

        respondData = ByteBuffer.allocate(2471);

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
            } else if (i == 2 || i == 3 || i == 4 || i == 5 || i == 6) { //medrec dinamik
                respondData.put(bytes);

                if (respondData.position() == 2471) {
                    medrecDinamikResponse = new byte[2471];
                    respondData.rewind();
                    respondData.get(medrecDinamikResponse);
                    respondData.position(0);
                    Log.i(TAG, "Medrec dinamik string: " + Util.bytesToHex(medrecDinamikResponse));

//                    processDinamikData(medrecDinamikResponse);
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
            Log.d(TAG, "write apdu select");
        } else if (i == 1) {
            serialPort.write(APDU_read_medrec_dinamik1);
            i++;
            Log.d(TAG, "write apdu read medrec dinamik 1");
        } else if (i == 2) {
            serialPort.write(APDU_read_medrec_dinamik2);
            i++;
            Log.d(TAG, "write apdu read medrec dinamik 2");
        } else if (i == 3) {
            serialPort.write(APDU_read_medrec_dinamik3);
            i++;
            Log.d(TAG, "write apdu read medrec dinamik 3");
        } else if (i == 4) {
            serialPort.write(APDU_read_medrec_dinamik4);
            i++;
            Log.d(TAG, "write apdu read medrec dinamik 4");
        } else if (i == 5) {
            serialPort.write(APDU_read_medrec_dinamik5);
            i++;
            Log.d(TAG, "write apdu read medrec dinamik 5");
        }
        else {
            serialPort.close();
            Log.d(TAG, "serial port closed");
        }
    }

}