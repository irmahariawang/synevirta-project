package com.procodecg.codingmom.ehealth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.procodecg.codingmom.ehealth.main.MainActivity;


public class SessionManagement extends Activity {

    public static final long DISCONNECT_TIMEOUT = 30000; // 30 sec = 30 * 1000 ms

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    SessionManagement.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Alert");
            alertDialog
                    .setMessage("Session telah habis, Tekan OK untuk kembali ke halaman awal.");
            alertDialog.setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SessionManagement.this,
                                    MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            dialog.cancel();
                        }
                    });

            alertDialog.show();
            // Perform any required operation on disconnect
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}

