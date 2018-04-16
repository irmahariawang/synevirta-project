package com.procodecg.codingmom.ehealth.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.procodecg.codingmom.ehealth.model.RekamMedisModel;
import com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry;

import java.util.ArrayList;
import java.util.List;

import static com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER;
import static com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA;

/**
 * Created by neo on 11/21/17.
 */

public class EhealthDbHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "ehealth.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link EhealthDbHelper}.
     *
     * @param context of the app
     */

    public EhealthDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTableKartu();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void openDB(){
        db = getWritableDatabase();
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

    public String[] getAllDiagnosa(){
        Cursor cursor = db.query(
                EhealthContract.DiagnosaEntry.TABLE_NAME,
                new String[] {EhealthContract.DiagnosaEntry.COLUMN_DIAGNOSA},
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.getCount() >0) {
            String[] str = new String[cursor.getCount()];
            int i = 0;

            while (cursor.moveToNext()) {
                str[i] = cursor.getString(cursor.getColumnIndex(EhealthContract.DiagnosaEntry.COLUMN_DIAGNOSA));
                i++;
            }
            return str;
        }else {
            return new String[] {};
        }
    }

    public void createTableRekMed(){

        String SQL_CREATE_REKMED_TABLE =  "CREATE TABLE IF NOT EXISTS " + EhealthContract.RekamMedisEntry.TABLE_NAME + " ("
                + RekamMedisEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TGL_PERIKSA + " DATETIME, "
                + COLUMN_NAMA_DOKTER + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_NIK + " TEXT, "
                + RekamMedisEntry.COLUMN_ID_PUSKESMAS + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_POLI + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_SUHU + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_NADI + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_TINGGI + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_BERAT + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_KESADARAN + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_KEPALA + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_THORAX + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_GENITALIA + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_KULIT + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_RESEP + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN + " TEXT, "
                + EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM + " INTEGER, "
                + EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM + " INTEGER);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_REKMED_TABLE);
    }

    public void createTableKartu(){
        String SQL_CREATE_KARTU_TABLE =  "CREATE TABLE IF NOT EXISTS " + EhealthContract.KartuEntry.TABLE_NAME + " ("
                + EhealthContract.KartuEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EhealthContract.KartuEntry.COLUMN_HPCNUMBER + " TEXT, "
                + EhealthContract.KartuEntry.COLUMN_DOKTER + " TEXT, "
                + EhealthContract.KartuEntry.COLUMN_PDCNUMBER + " TEXT, "
                + EhealthContract.KartuEntry.COLUMN_NAMAPASIEN + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_KARTU_TABLE);
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if(openDb) {
            if(db == null || !db.isOpen()) {
                db = getReadableDatabase();
            }

            if(!db.isReadOnly()) {
                db.close();
                db = getReadableDatabase();
            }
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    //setting untuk Pencarian

//    public void addBeneficiary(Pencarian pencarian) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(EhealthContract.RekamMedisEntry._ID, pencarian.getId());
//        values.put(COLUMN_NAMA_DOKTER, pencarian.getNamaDokter());
//        values.put(EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA, pencarian.getEmail());
//        values.put(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA, pencarian.getAddress());
//        values.put(EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA, pencarian.getCountry());
//
//        db.insert(EhealthContract.RekamMedisEntry.TABLE_NAME, null, values);
//        db.close();
//    }

    public Cursor retrieve (String searchTerm)
    {
        String[] columns={EhealthContract.RekamMedisEntry._ID, COLUMN_NAMA_DOKTER};
        Cursor context =null;

        if(searchTerm != null && searchTerm.length()>0)
        {
            String sql="SELECT * FROM "+EhealthContract.RekamMedisEntry.TABLE_NAME+" WHERE "+RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA+" LIKE '%"+searchTerm+"%'";
            context=db.rawQuery(sql,null);
            return context;

        }

        context=db.query(EhealthContract.RekamMedisEntry.TABLE_NAME,columns,null,null,null,null,COLUMN_TGL_PERIKSA+" DESC");
        return context;
    }

    public List<RekamMedisModel> getRekamMedisModel(){
        List<RekamMedisModel> RekamMedisModelList = new ArrayList<>();
        String selectQuery = "SELECT _ID, tgl_periksa, nama_dokter FROM " + RekamMedisEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                RekamMedisModel rekammedismodel = new RekamMedisModel(cursor.getInt(1), cursor.getString(2), cursor.getString(3));
                RekamMedisModelList.add(rekammedismodel);
            } while (cursor.moveToNext());
        }

        return RekamMedisModelList;

    }

}
