package com.procodecg.codingmom.ehealth.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by idedevteam on 9/24/18.
 */

public class WilayahDbHelper extends SQLiteOpenHelper{
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "kode_wilayah.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link EhealthDbHelper}.
     *
     * @param context of the app
     */

    public WilayahDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        createTableKartu();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void openDB(){
        db = getReadableDatabase();
    }

    //public void check

    public void closeDB(){
        if(db != null && db.isOpen()){
            db.close();
        }
    }

    public boolean checkDbOpen(){
        if(db != null && db.isOpen()){
            return true;
        }else{
            return false;
        }
    }
}
