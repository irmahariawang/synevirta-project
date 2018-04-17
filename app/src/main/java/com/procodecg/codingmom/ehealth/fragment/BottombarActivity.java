package com.procodecg.codingmom.ehealth.fragment;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.asynctask.AsyncResponse;
import com.procodecg.codingmom.ehealth.asynctask.TokenRequest;
import com.procodecg.codingmom.ehealth.asynctask.UpdateMedrecDinamik;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedDinamisFragment;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    
    private SharedPreferences jwt, settings;
    int i = 1;
    
    EhealthDbHelper mDbHelper;

    Typeface fontBold;

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
        boolean Tableexist = mDbHelper.isTableExists(EhealthContract.RekamMedisEntry.TABLE_NAME, true);

        if (Tableexist) {
            //Toast.makeText(this, "Ada", Toast.LENGTH_SHORT).show();
        }else{
            mDbHelper.createTableRekMed();
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
    public void getDataAndPost() throws ParseException {
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
            Toast.makeText(BottombarActivity.this, "Sinkronisasi selesai", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();

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

            new UpdateMedrecDinamik(this).execute(data_param.toString(), token, "" + cursor.getString(1));
            Log.i("Array", data_param.toString());
        }
    }

    //fungsi untuk membaca response dari rest service saat melakukan update medrek dinamik
    @Override
    public void taskComplete(String output, String timestamp) {
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
//            callLoginDialog();
            Toast.makeText(BottombarActivity.this, "Generating new token ... ", Toast.LENGTH_SHORT).show();
            new TokenRequest(this).execute(username, password);
        } else if(code.equals("216") || code.equals("207")){
            try {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("LAST_TIMESTAMP", timestamp);
                editor.apply();
                
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                db.delete(EhealthContract.RekamMedisEntry.TABLE_NAME, EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA+"=?", new String[]{timestamp});

                getDataAndPost();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(BottombarActivity.this, "Update data berhasil", Toast.LENGTH_SHORT).show();
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
            accessToken = obj.getString("token");
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
                getDataAndPost();
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

//    public void setSubTitleText(String title){
//            txtSubTitle.setText(title);
//        }

}
