package com.procodecg.codingmom.ehealth.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCActivity;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;
import com.procodecg.codingmom.ehealth.utils.States;

public class PinActivity extends SessionManagement {

    Typeface font;
    Typeface fontbold;
    HPCActivity hpc;


    // batas jumlah input pin salah yang diperbolehkan
    private int numberOfRemainingLoginAttempts = 3;

    @Override
    protected void onStart() {
        super.onStart();

        if (!States.CheckHPC) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PinActivity.this);
            mBuilder.setIcon(R.drawable.logo2);
            mBuilder.setTitle("Kartu yang Anda masukkan tidak dapat diakses");
            mBuilder.setMessage("Silahkan coba lagi atau masukkan kartu lain");
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent activity = new Intent(PinActivity.this, MainActivity.class);
                    startActivity(activity);
                }
            });

            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
    }


    // fungsi sembunyikan keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//  fungsi menghapus pin input yang salah
    private void clearPin(ViewGroup group)
    {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).getText().clear();
            }
            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearPin((ViewGroup)view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pin);

        final TextView attemptslefttv = (TextView) findViewById(R.id.attemptsLeftTV);
        final TextView numberOfRemainingLoginAttemptstv = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
        final TextView textviewkali = (TextView) findViewById(R.id.textViewKali);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        fontbold = Typeface.createFromAsset(getAssets(), "font1bold.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textPin);
        TextView tv2 = (TextView) findViewById(R.id.attemptsLeftTV);
        TextView tv3 = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
        TextView tv4 = (TextView) findViewById(R.id.textViewKali);
        tv1.setTypeface(font);
        tv2.setTypeface(fontbold);
        tv3.setTypeface(fontbold);
        tv4.setTypeface(fontbold);

        Pinview pinview = (Pinview) findViewById(R.id.pinView);

        getHPCdata();

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {
//          jika pin benar
                if (pinview.getValue().toString().equals("12345")) {
                    Toast.makeText(PinActivity.this, "Pin Anda benar", Toast.LENGTH_SHORT).show();
                    hideKeyboard(PinActivity.this);
                    Intent activity = new Intent(PinActivity.this, PasiensyncActivity.class);
                    startActivity(activity);
                    finish();

//          jika pin salah
                } else {

                    clearPin((ViewGroup) pinview);
                    pinview.clearFocus();
                    Toast.makeText(getApplicationContext(), "PIN yang Anda masukkan salah",
                            Toast.LENGTH_SHORT).show();

                    numberOfRemainingLoginAttempts--;
                    numberOfRemainingLoginAttemptstv.setText(Integer.toString(numberOfRemainingLoginAttempts));

//                  tampilkan text "Kesempatan login : x kali"
                    attemptslefttv.setVisibility(View.VISIBLE);
                    numberOfRemainingLoginAttemptstv.setVisibility(View.VISIBLE);
                    textviewkali.setVisibility(View.VISIBLE);

//                  jika kesempatan login habis
                    if (numberOfRemainingLoginAttempts == 0) {
                        hideKeyboard(PinActivity.this);
//                      tampilkan dialog box alert
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PinActivity.this);
                        mBuilder.setTitle(R.string.dialog_title_pin);
                        mBuilder.setMessage(R.string.dialog_msg_pin);
                        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent activity = new Intent(PinActivity.this, MainActivity.class);
                                startActivity(activity);
                                finish();
                            }
                        });

                        AlertDialog alertDialog = mBuilder.create();
                        alertDialog.show();

                    }
                }


            }
        });
    }


    /** mengambil data dari kartu HPC
     *
     */

    public void getHPCdata() {

        hpc = new HPCActivity(getApplicationContext());
        //Boolean statusKartuHPC = true;
        String HPCnumberString = "D12345";
        String namaDokterString = "dr. Sinta";

            //Toast.makeText(this, "true ", Toast.LENGTH_SHORT).show();
            // Create database helper
            EhealthDbHelper db = new EhealthDbHelper(getApplicationContext());
            db.openDB();
            db.createTableKartu();
            //db.createTableRekMed();
            //mDbHelper.deleteAll();
            // Gets the database in write mode
            SQLiteDatabase mDbHelper = db.getWritableDatabase();

            // Create a ContentValues object where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EhealthContract.KartuEntry.COLUMN_HPCNUMBER, HPCnumberString);
//            values.put(KartuEntry.COLUMN_PIN_HPC, PIN_HPC);
            values.put(EhealthContract.KartuEntry.COLUMN_DOKTER, namaDokterString);

            // Insert a new row in the database, returning the ID of that new row.
            long newRowId = mDbHelper.insert(EhealthContract.KartuEntry.TABLE_NAME, null, values);
            mDbHelper.close();
            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Sinkronisasi kartu HPC GAGAL!", Toast.LENGTH_SHORT).show();
                Intent activity = new Intent(PinActivity.this, MainActivity.class);
                startActivity(activity);
                finish();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "Sinkronisasi kartu HPC BERHASIL! ", Toast.LENGTH_SHORT).show();
            }

    }

}
