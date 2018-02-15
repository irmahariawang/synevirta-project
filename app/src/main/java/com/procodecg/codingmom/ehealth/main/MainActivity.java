package com.procodecg.codingmom.ehealth.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.utils.Setting;
import com.procodecg.codingmom.ehealth.data.CopyDBHelper;
import com.procodecg.codingmom.ehealth.utils.Edit;

import java.io.File;
import java.io.IOException;

import static com.procodecg.codingmom.ehealth.main.PinActivity.hideKeyboard;

public class MainActivity extends AppCompatActivity {

    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideKeyboard(MainActivity.this);

        //deklarasi KEY untuk SP
        SharedPreferences prefs = getSharedPreferences("DATAPUSKES", MODE_PRIVATE);
        //default values
        String idpuskes = prefs.getString("IDPUSKES", "________");
        String namapuskes = prefs.getString("NAMAPUSKES", "________");

        //set values
        ((TextView) findViewById(R.id.txt_idPuskesmas)).setText(idpuskes);
        ((TextView) findViewById(R.id.txt_namaPuskesmas)).setText(namapuskes);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textIdPuskes);
        TextView tv4 = (TextView) findViewById(R.id.textNamaPuskes);
        tv1.setTypeface(font);
        tv2.setTypeface(font);
        tv3.setTypeface(font);
        tv4.setTypeface(font);

        if (doesDatabaseExist(getApplicationContext(),"ehealth.db"))
        {
            //Toast.makeText(this, "DB ada", Toast.LENGTH_SHORT).show();
        }
        else
        {
            copyDBEhealth();
        }
        //getHPCdata();
    }

      public void showEdit(View view) {
      startActivity(new Intent(getApplicationContext(),Edit.class));

    //copyDBEhealth();
        //getHPCdata();

    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    public void goToPin(View v){

        Intent activity = new Intent(this, PinActivity.class);
        startActivity(activity);
        finish();
    }

    //tambahan untuk masuk ke menu setting
    public void showSett(View view) {
        startActivity(new Intent(getApplicationContext(), Setting.class));
    }

    public void copyDBEhealth(){

        CopyDBHelper mDBHelper = new CopyDBHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        //mDBHelper.createTableKartu();
        mDBHelper.close();
    }


}


