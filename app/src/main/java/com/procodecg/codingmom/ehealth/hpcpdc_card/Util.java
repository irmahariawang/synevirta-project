package com.procodecg.codingmom.ehealth.hpcpdc_card;

import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by neo on 24/01/18.
 */

public class Util {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static Date bytestoDate(byte[] dateBytes) {
        String hexDate = bytesToHex(dateBytes);
        int secs = Integer.parseInt(hexDate, 16);
        byte[] bytes = ByteBuffer.allocate(4).putInt(secs).array();
        int dis = ByteBuffer.wrap(bytes).getInt();
        long l = (long) dis*1000;
        return new Date(l);
    }

    public static String getformattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long hexStringToLong(String hex) {
        return Long.parseLong(hex, 16);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hex = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hex[j * 2] = hexArray[v >>> 4];
            hex[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hex);
    }

    public static byte[] trimZeroPadding(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public static String bytesToString(byte[] bytes) {
        String hex = bytesToHex(bytes);
        StringBuilder output = new StringBuilder();
        for (int i=0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

//    public static int getWriteIndex(ArrayList<MedrecDinamikData> mddArray) {
//        ArrayList<Long> timestamps = new ArrayList<>();
//        for (int i=0; i<MedrecDinamikData.MEDREC_DINAMIK_SIZE; i++) {
//            Long ts = new Long(mddArray.get(i).tglPeriksa);
//            timestamps.add(ts);
//        }
//
//        Long max = Collections.max(timestamps);
//        int maxIdx = timestamps.indexOf(max);
//
//        return (maxIdx+1) % MedrecDinamikData.MEDREC_DINAMIK_SIZE;
//    }

    public static int getWriteIndex(ArrayList<MedrecDinamikData> mddArray) {
        ArrayList<Date> dates = new ArrayList<>();
        for (int i=0; i < MedrecDinamikData.MEDREC_DINAMIK_SIZE; i++) {
            dates.add(mddArray.get(i).tglPeriksa);
        }

        Date mostRecent = Collections.max(dates);
        int maxIdx = dates.indexOf(mostRecent);
        return (maxIdx+1) % 5;
    }

    public static Date bytesToDate(byte[] dateBytes) {
        int secs = Integer.parseInt(bytesToHex(dateBytes), 16);
        System.out.println("secs: " + secs);
        byte[] bytes = ByteBuffer.allocate(4).putInt(secs).array();

        int dis = ByteBuffer.wrap(bytes).getInt();
        long l = (long) dis*1000;
        return new Date(l);
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
