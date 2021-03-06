package com.procodecg.codingmom.ehealth.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;

/**
 * Created by idedevteam on 3/13/18.
 */

public class InternetConnectivityReceiver extends BroadcastReceiver {
    public InternetConnectivityReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        BottombarActivity ba = BottombarActivity.instance;

        // Check internet connection and accrding to state change the
        // text of activity by calling method
//        if (networkInfo != null && networkInfo.isConnected()) {
//            ba.changeTextStatus(true);
//        } else {
//            ba.changeTextStatus(false);
//        }
    }
}
