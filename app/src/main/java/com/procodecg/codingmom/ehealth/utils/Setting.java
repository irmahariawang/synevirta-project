package com.procodecg.codingmom.ehealth.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.main.MainActivity;

public class Setting extends AppCompatActivity {

    private EditText settusername;
    private EditText settpassword;
    private EditText setip;
    private EditText settimeout;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        preferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        String username= preferences.getString("USERNAME","");
        String password= preferences.getString("PASSWORD","");
        String address= preferences.getString("ADDRESS","");
        Integer time= preferences.getInt("TIME",0);


        settusername=(EditText)findViewById(R.id.SetUser);
        settpassword=(EditText)findViewById(R.id.SetPass);
        setip=(EditText)findViewById(R.id.SetIp);
        settimeout=(EditText)findViewById(R.id.Settime);

        ((TextView) findViewById(R.id.SetUser)).setText(username);
        ((TextView) findViewById(R.id.SetPass)).setText(password);
        ((TextView) findViewById(R.id.SetIp)).setText(address);
        if(time != 0) {
            ((TextView) findViewById(R.id.Settime)).setText(String.valueOf(time));
        }

        //privacy policy and terms of service
        TextView tv1 = (TextView) findViewById(R.id.text_Policy);
        TextView tv2 = (TextView) findViewById(R.id.text_Terms);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, Policy.class);
                startActivity(intent);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, Terms.class);
                startActivity(intent);
            }
        });


        //set popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.80),(int)(height*.70));

    }

    //set close button
    public void closeToMain(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //set simpan data
    public void SaveSett(View view){
        //input data
        String username= settusername.getText().toString();
        String password= settpassword.getText().toString();
        String address= setip.getText().toString();
        String time= settimeout.getText().toString();

        if(TextUtils.isEmpty(time)){
            settimeout.setError("Timeout tidak boleh kosong!");
            settimeout.setText("90");
            return;
        } else if(Integer.valueOf(time)<90){
            settimeout.setError("Timeout tidak boleh kurang dari 90!");
            settimeout.setText("90");
            return;
        }

        //simpan data
        SharedPreferences.Editor editor =preferences.edit();
        editor.putString("USERNAME",username);
        editor.putString("PASSWORD",password);
        editor.putString("ADDRESS",address);
        editor.putInt("TIME",Integer.valueOf(time));
        editor.apply();


        //kembali ke mainVer2
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }


}
