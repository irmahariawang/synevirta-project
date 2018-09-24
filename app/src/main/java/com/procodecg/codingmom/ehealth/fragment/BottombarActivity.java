package com.procodecg.codingmom.ehealth.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.asynctask.AsyncResponse;
import com.procodecg.codingmom.ehealth.asynctask.TokenRequest;
import com.procodecg.codingmom.ehealth.asynctask.UpdateMedrecDinamik;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.main.MainActivity;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.main.PinActivity;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedDinamisFragment;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by macbookpro on 7/31/17.
 */

public class BottombarActivity extends SessionManagement implements AsyncResponse{

//    private void displayFragment(int position) {
//        // update the main content by replacing fragments
//        Fragment fragment = null;
//        String title = "";
//        switch (position) {
//            case 0:
//                fragment = new ProfilpasienFragment();
//                title = "Profil Pasien";
//                break;
//            case 1:
//                fragment = new RekammedisFragment();
//                title = "Rekam Medis";
//                break;
//            case 2:
//                fragment = new PencarianFragment();
//                title = "Pencarian";
//                break;
//
//            default:
//                break;
//        }
//
//        // update selected fragment and title
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, fragment).commit();
//            getSupportActionBar().setTitle(title);
//            // change icon to arrow drawable
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.logo2);
//        }
//    }
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtSubTitle;
    public static BottombarActivity instance;
    public static String hostStatus;
    
    private SharedPreferences jwt, settings;
    EhealthDbHelper mDbHelper;
    private static long back_pressed;

    Typeface fontBold;

    int i, j = 0;
    String timestampLama;

    ArrayList<String> lastDataNIK, lastDataTimestamp, dataNIK, dataTimestamp;
    Boolean habis = true;
    int jumlah = 0;

    //SEARCH menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_search, menu);
//        MenuItem searchViewItem = menu.findItem(R.id.action_search);
//        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //searchViewAndroidActionBar.clearFocus();
//                Intent intent = new Intent(getApplicationContext(), AutoComplete.class);
//                intent.putExtra("hasil", query);
//                startActivity(intent);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

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

        //utk title custom action bar
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
            //Toast.makeText(this, "Ada", Toast.LENGTH_SHORT).show();
        } else if (TableRekmedExist && !TableSyncExist){
            mDbHelper.createTableSync();
        } else if (!TableRekmedExist && TableSyncExist){
            mDbHelper.createTableRekMed();
        } else {
            mDbHelper.createTableRekMed();
            mDbHelper.createTableSync();
            //Toast.makeText(this, "Tidak ada", Toast.LENGTH_SHORT).show();
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
//                            case R.id.rekmeddinamis:
//                                selectedFragment = RekmedDinamisFragment.newInstance();
//                                break;
//                            case R.id.rekmedstatis:
//                                selectedFragment = RekmedStatisFragment.newInstance();
//                                break;
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

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ProfilpasienFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    public void setTitleText(String title){
        if(title.isEmpty()){
            txtTitle.setText("eHealth");
        }else{
            txtTitle.setText(title);
        }
    }

//    public void setSubTitleText(String title){
//        txtSubTitle.setText("dr X");
//    }

    //nama dokter
    public void setSubTitleText() {
        txtSubTitle.setText(PasiensyncActivity.getNamaDokter());
    }

    //menyiapkan data medrek dinamik dari SQLite untuk dikirim ke SIKDA dalam bentuk JSON Object
    public void getDataAndPost(Boolean first) throws ParseException {
        String timestamp;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //mengambil token yang sudah ada pada app
        jwt = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = jwt.getString("ACCESS_TOKEN", "");

        String[] columns = {
                //0-6 json object luar
                EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER,
                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA,
                EhealthContract.RekamMedisEntry.COLUMN_NIK,
                EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS,
                EhealthContract.RekamMedisEntry.COLUMN_POLI,
                EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA,
                EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN,
                //7-12 json array pelayanan
                EhealthContract.RekamMedisEntry.COLUMN_KEPALA,
                EhealthContract.RekamMedisEntry.COLUMN_SUHU,
                EhealthContract.RekamMedisEntry.COLUMN_NADI,
                EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI,
                EhealthContract.RekamMedisEntry.COLUMN_BERAT,
                EhealthContract.RekamMedisEntry.COLUMN_TINGGI,
                //13-37
                EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL,
                EhealthContract.RekamMedisEntry.COLUMN_KESADARAN,
                EhealthContract.RekamMedisEntry.COLUMN_THORAX,
                EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN,
                EhealthContract.RekamMedisEntry.COLUMN_GENITALIA,
                EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS,
                EhealthContract.RekamMedisEntry.COLUMN_KULIT,
                EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI,
                EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM,
                EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI,
                EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO,
                EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA,
                EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING,
                EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA,
                EhealthContract.RekamMedisEntry.COLUMN_RESEP,
                EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP,
                EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP,
                EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP,
                EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN,
                EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM,
                EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM,
                EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM
        };

        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, columns, null, null , null, null, null, "1");

        if(cursor.getCount()==0){
            lastDataNIK = new ArrayList<String>();
            lastDataTimestamp = new ArrayList<String>();

            SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
            int size = sync.getInt("size", -1);
            Log.i("JUMLAHTERAKHIR", String.valueOf(size));
            int x = 0;
            do {
                String nik = sync.getString(String.valueOf(x), "");
                if(!nik.isEmpty()) {
                    String ts = sync.getString(nik, "");
                    lastDataNIK.add(nik);
                    lastDataTimestamp.add(ts);
                }
                x++;
            } while(x < size);

            Log.i("LASTNIK", lastDataNIK.toString());
            Log.i("LASTTIMESTAMP", lastDataTimestamp.toString());

            SQLiteDatabase dbHelper = mDbHelper.getWritableDatabase();
            int index = 0;
            if(lastDataNIK.size() != 0) {
                do {
                    if (index < size) {
                        ContentValues values = new ContentValues();
                        values.put(EhealthContract.SyncEntry.COLUMN_LAST_TIMESTAMP, lastDataTimestamp.get(index));
                        values.put(EhealthContract.SyncEntry.COLUMN_DOKTER, HPCData.nik);

                        long updateRow = dbHelper.update(EhealthContract.SyncEntry.TABLE_NAME, values, "" + EhealthContract.SyncEntry.COLUMN_NIK + " = ?", new String[]{lastDataNIK.get(index)});

                        if (updateRow == -1) {
                            Log.i("Sync", "Update last timestamp gagal!");
                        } else {
                            Log.i("Sync", "Update last timestamp berhasil!");
                        }
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(EhealthContract.SyncEntry.COLUMN_NIK, lastDataNIK.get(index));
                        values.put(EhealthContract.SyncEntry.COLUMN_LAST_TIMESTAMP, lastDataTimestamp.get(index));
                        values.put(EhealthContract.SyncEntry.COLUMN_DOKTER, HPCData.nik);

                        // Insert a new row in the database, returning the ID of that new row.
                        long newRowId = dbHelper.insert(EhealthContract.SyncEntry.TABLE_NAME, null, values);

                        if (newRowId == -1) {
                            Log.i("Sync", "Penyimpanan last timestamp gagal!");
                        } else {
                            Log.i("Sync", "Penyimpanan last timestamp berhasil!");
                        }
                    }

                    index++;
                } while (index < lastDataNIK.size() && index < lastDataTimestamp.size());
            }

            SharedPreferences.Editor clearSync = sync.edit();
            clearSync.clear();
            clearSync.commit();
            Toast.makeText(BottombarActivity.this, "Sinkronisasi selesai", Toast.LENGTH_LONG).show();
        } else {
            dataNIK = new ArrayList<String>();
            dataTimestamp = new ArrayList<String>();

            if(first){
                SQLiteDatabase dbHelper = mDbHelper.getReadableDatabase();
                String[] Column = { EhealthContract.SyncEntry.COLUMN_NIK,
                                    EhealthContract.SyncEntry.COLUMN_LAST_TIMESTAMP};
                Cursor lastTS = dbHelper.query(EhealthContract.SyncEntry.TABLE_NAME, Column, null, null , null, null, null);
                int j = 0;
                if(lastTS.getCount()!=0){
                    SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sync.edit();

                    if(lastTS.moveToFirst()){
                        do{
                            editor.putString(""+j, lastTS.getString(0));
                            editor.putString(""+lastTS.getString(0), lastTS.getString(1));
                            j++;
                        }while (lastTS.moveToNext());
                    }

                    editor.putInt("size", j);
                    Log.i("SIZE", String.valueOf(j));
                    editor.commit();
                }
            }

            SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
            int jumlah = sync.getInt("size", -1);
            Log.i("JUMLAHBARU", String.valueOf(jumlah));
            int x = 0;
            do {
                String nik = sync.getString(String.valueOf(x), "");
                if(!nik.isEmpty()) {
                    String ts = sync.getString(nik, "");
                    dataNIK.add(nik);
                    dataTimestamp.add(ts);
                }
                x++;
            } while(x < jumlah);

            Log.i("NIKARRAYS", dataNIK.toString());
            Log.i("TSARRAYS", dataTimestamp.toString());


            cursor.moveToFirst();

            if(dataNIK.contains(cursor.getString(2))){
                int index = dataNIK.indexOf(cursor.getString(2));

                String lastTS = dataTimestamp.get(index);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timeLama = sdf.parse(lastTS);
                Date timeBaru = sdf.parse(cursor.getString(1));

                if(timeBaru.after(timeLama)){
                    DateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                    Date in = input.parse(cursor.getString(1));
                    String out = output.format(in);

                    JSONArray pelayanan_array = new JSONArray();
                    JSONArray pelayanan_ket_array = new JSONArray();
                    JSONObject data_param = new JSONObject();
                    JSONObject pelayanan = new JSONObject();
                    JSONObject pelayanan_ket_tambahan = new JSONObject();

                    try {
                        data_param.put("nama_dokter", cursor.getString(0));
                        data_param.put("date", out);
                        data_param.put("datetime", cursor.getString(1));
                        data_param.put("nik", cursor.getString(2));
                        data_param.put("kd_puskesmas", cursor.getString(3));
                        data_param.put("poli", cursor.getString(4));
                        data_param.put("anamnesa", cursor.getString(5));
                        data_param.put("rujukan", cursor.getString(6));
                        data_param.put("username", "admincaringin");

                        for(int i=0; i<6; i++){
                            pelayanan.put(""+i, cursor.getString(i+7));
                        }
                        pelayanan_array.put(pelayanan);
                        data_param.put("pelayanan", pelayanan_array);

                        for(int i=0; i<26; i++){
                            pelayanan_ket_tambahan.put(""+i, cursor.getString(i+13));
                        }
                        pelayanan_ket_array.put(pelayanan_ket_tambahan);
                        data_param.put("pelayanan_ket_tambahan", pelayanan_ket_array);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new UpdateMedrecDinamik(this).execute(data_param.toString(), token, "" + cursor.getString(1), cursor.getString(2));
                    Log.i("Array", data_param.toString());
                } else {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("code", "207");
                        jo.put("status", "Data sudah pernah dikirim");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    taskComplete(jo.toString(), cursor.getString(1), cursor.getString(2));
//                    getDataAndPost();
                }
            } else {
                DateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                Date in = input.parse(cursor.getString(1));
                String out = output.format(in);

                JSONArray pelayanan_array = new JSONArray();
                JSONArray pelayanan_ket_array = new JSONArray();
                JSONObject data_param = new JSONObject();
                JSONObject pelayanan = new JSONObject();
                JSONObject pelayanan_ket_tambahan = new JSONObject();

                try {
                    data_param.put("nama_dokter", cursor.getString(0));
                    data_param.put("date", out);
                    data_param.put("datetime", cursor.getString(1));
                    data_param.put("nik", cursor.getString(2));
                    data_param.put("kd_puskesmas", cursor.getString(3));
                    data_param.put("poli", cursor.getString(4));
                    data_param.put("anamnesa", cursor.getString(5));
                    data_param.put("rujukan", cursor.getString(6));
                    data_param.put("username", "admincaringin");

                    for(int i=0; i<6; i++){
                        pelayanan.put(""+i, cursor.getString(i+7));
                    }
                    pelayanan_array.put(pelayanan);
                    data_param.put("pelayanan", pelayanan_array);

                    for(int i=0; i<26; i++){
                        pelayanan_ket_tambahan.put(""+i, cursor.getString(i+13));
                    }
                    pelayanan_ket_array.put(pelayanan_ket_tambahan);
                    data_param.put("pelayanan_ket_tambahan", pelayanan_ket_array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UpdateMedrecDinamik(this).execute(data_param.toString(), token, "" + cursor.getString(1), cursor.getString(2));
                Log.i("Array", data_param.toString());
            }
        }
    }

    //fungsi untuk membaca response dari rest service saat melakukan update medrek dinamik
    @Override
    public void taskComplete(String output, String timestamp, String nik) {
        JSONObject obj;
        String code = "", error_code = "";
        try {
            obj = new JSONObject(output);
            code = obj.getString("code");
            error_code = obj.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Status", error_code);

        settings = getSharedPreferences("SETTING", MODE_PRIVATE);
        String username = settings.getString("USERNAME", "");
        String password = settings.getString("PASSWORD", "");

        if(code.equals("203")){
            Toast.makeText(BottombarActivity.this, "Generating new token ... ", Toast.LENGTH_SHORT).show();
            new TokenRequest(this).execute(username, password);
        } else if(code.equals("216") || code.equals("207")){
            try {
//                do {
//                    String dataNIK = settings.getString(String.valueOf(jumlah), "");
//                    if(!dataNIK.isEmpty()){
//                        lastDataNIK.add(nik);
//                        jumlah++;
//                    } else {
//                        habis = false;
//                    }
//                } while(habis);

                Log.i("NIK "+nik, ""+ dataNIK.contains(nik));
                SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
                SharedPreferences.Editor editor = sync.edit();
                if(!dataNIK.contains(nik)){
                    editor.putString(String.valueOf(dataNIK.size()), nik);
//                    dataNIK.add(nik);
                }

                String lastTimestamp = sync.getString(nik, "");
                if(lastTimestamp.isEmpty() || lastTimestamp == null){
                    timestampLama = "0000-00-00 00:00:00";
                } else {
                    timestampLama = lastTimestamp;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timeLama = sdf.parse(timestampLama);
                Date timeBaru = sdf.parse(timestamp);

                if(timeBaru.after(timeLama)) {
                    editor.putString(nik, timestamp);
                    timestampLama = timestamp;
                }
                editor.apply();
                
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                db.delete(EhealthContract.RekamMedisEntry.TABLE_NAME, EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA+"=?", new String[]{timestamp});

                getDataAndPost(false);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if(code.equals("206")){
            Toast.makeText(BottombarActivity.this, "Update data gagal", Toast.LENGTH_SHORT).show();
        }
    }
    
    //fungsi untuk membaca response dari rest service saat melakukan token request
    @Override
    public void tokenRequest(String output) {
        JSONObject obj;
        String code = "", accessToken = "";
        try {
            obj = new JSONObject(output);
            code = obj.getString("code");
            accessToken = obj.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(code.equals("111")){
            SharedPreferences sp = getSharedPreferences("TOKEN", MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("ACCESS_TOKEN", accessToken);
            editor.apply();

            Toast.makeText(BottombarActivity.this, "Generate token success", Toast.LENGTH_SHORT).show();
            
            try {
                getDataAndPost(true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void hostDetected(Boolean output) {
        SharedPreferences sp = getSharedPreferences("HOST", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("DETECTED", output);
        editor.apply();
//        hostStatus = "reachable";
    }

    // Method to change the text status
    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Internet Connection Detected", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

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
