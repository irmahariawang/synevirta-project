package com.procodecg.codingmom.ehealth.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.asynctask.AsyncResponse;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;

import java.util.ArrayList;

/**
 * (c) 2017
 * Created by :
 *      Coding Mom
 *      Annisa Alifiani
 */

public class BottombarActivity extends SessionManagement {

    private static long back_pressed;

    // View
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtSubTitle;
    public static BottombarActivity instance;

    // Db
    EhealthDbHelper mDbHelper;

    Typeface fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottombar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        instance = this;

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtSubTitle = (TextView) findViewById(R.id.txt_namaDokter);

        fontBold = Typeface.createFromAsset(getAssets(),"font1bold.ttf");
        txtTitle.setTypeface(fontBold);
        txtSubTitle.setTypeface(fontBold);

        // Untuk title custom action bar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mDbHelper = new EhealthDbHelper(this);
        mDbHelper.openDB();
        boolean TableRekmedExist = mDbHelper.isTableExists(EhealthContract.RekamMedisEntry.TABLE_NAME, true);
        boolean TableSyncExist = mDbHelper.isTableExists(EhealthContract.SyncEntry.TABLE_NAME, true);

        if (TableRekmedExist && TableSyncExist) {
        } else if (TableRekmedExist && !TableSyncExist){
            mDbHelper.createTableSync();
        } else if (!TableRekmedExist && TableSyncExist){
            mDbHelper.createTableRekMed();
        } else {
            mDbHelper.createTableRekMed();
            mDbHelper.createTableSync();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.profil:
                                selectedFragment = ProfilpasienFragment.newInstance();
                                break;
                            case R.id.rekammedis:
                                selectedFragment = RekmedFragment.newInstance();
                                break;
                            case R.id.pencarian:
                                selectedFragment = PencarianFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        // Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ProfilpasienFragment.newInstance());
        transaction.commit();
    }

    public void setTitleText(String title){
        if(title.isEmpty()){
            txtTitle.setText("eHealth");
        }else{
            txtTitle.setText(title);
        }
    }

    // Nama dokter
    public void setSubTitleText() {
        txtSubTitle.setText(PasiensyncActivity.getNamaDokter());
    }

    @Override
    public void onBackPressed(){
        if (back_pressed + 2000 > System.currentTimeMillis()){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(BottombarActivity.this);
            mBuilder.setIcon(R.drawable.logo2);
            mBuilder.setTitle("Konfirmasi");
            mBuilder.setMessage("Apakah Anda ingin keluar dari profil pasien?");
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
                    Intent intent = new Intent(BottombarActivity.this, PasiensyncActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(BottombarActivity.this, "Tekan lagi untuk keluar dari profil pasien", Toast.LENGTH_SHORT).show();
        }

        back_pressed = System.currentTimeMillis();
    }

}
