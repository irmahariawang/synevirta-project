package com.procodecg.codingmom.ehealth.utils;

import android.util.Log;

import java.util.Calendar;

public  class FunctionSupport {

    final String TAG = "umur";

        //Log.d(TAG, "hasil umur : " + getAge(2013, 8, 12));

    public static String getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;
        int ageMonth;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if(DOBmonth > currentMonth){
            --age;
            ageMonth = 12-(DOBmonth-currentMonth);

        } else if(DOBmonth == currentMonth){
            ageMonth = 0;
            if(DOBday > todayDay){
                --age;
            }
        } else {
            ageMonth = currentMonth - DOBmonth;
        }

        Integer ageInt = new Integer(age);
        Integer ageMonthInt = new Integer(ageMonth);
        String ageS = ageInt.toString();
        String ageMonthS = ageMonthInt.toString();
        String ageAll = ageS + " tahun " + ageMonthS + " bulan";

        return ageAll;

        //return age;
    }

}
