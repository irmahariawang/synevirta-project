package com.procodecg.codingmom.ehealth.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.main.MainActivity;

public class Edit extends SessionManagement {

    private EditText idPuskesmas;
    private EditText namaPuskesmas;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        prefs = getSharedPreferences("DATAPUSKES", MODE_PRIVATE);
        String idpuskes= prefs.getString("IDPUSKES","");
        String namapuskes= prefs.getString("NAMAPUSKES","");

        idPuskesmas=(EditText)findViewById(R.id.inputId);
        namaPuskesmas=(EditText)findViewById(R.id.inputNama);

        //set popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.70),(int)(height*.40));

    }

    //set close button
    public void closeToMain(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //set simpan data
    public void SimpanData(View view){
        //input data
        String idpuskes = idPuskesmas.getText().toString();
        String namapuskes = namaPuskesmas.getText().toString();

        //simpan data
        SharedPreferences.Editor editor =prefs.edit();
        editor.putString("IDPUSKES",idpuskes);
        editor.putString("NAMAPUSKES",namapuskes);
        editor.apply();

        //kembali ke mainVer2
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }


}


