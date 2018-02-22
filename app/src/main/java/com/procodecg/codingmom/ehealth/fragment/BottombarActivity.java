package com.procodecg.codingmom.ehealth.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;

/**
 * Created by macbookpro on 7/31/17.
 */

public class BottombarActivity extends AppCompatActivity implements AsyncResponse{

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

        EhealthDbHelper mDbHelper = new EhealthDbHelper(this);
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
        i++;
        String timestamp;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //get last timestamp yang dikirim
        settings = getSharedPreferences("SETTING", MODE_PRIVATE);
        String ts = settings.getString("LAST_TIMESTAMP", "");

        //get token yang sudah ada pada app
        jwt = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = jwt.getString("ACCESS_TOKEN", "");

        if(ts.isEmpty()){
            timestamp = "0";
        } else {
            timestamp = ts;
        }
        
        String[] columns = {
                //kode pelayanan
                EhealthContract.RekamMedisEntry._ID,
                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA,
                //NIK
                EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA,
                EhealthContract.RekamMedisEntry.COLUMN_KEPALA,
                EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO,
                EhealthContract.RekamMedisEntry.COLUMN_SUHU,
                EhealthContract.RekamMedisEntry.COLUMN_NADI,
                EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI,
                EhealthContract.RekamMedisEntry.COLUMN_BERAT,
                EhealthContract.RekamMedisEntry.COLUMN_TINGGI
        };
        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, columns, ""+ EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA + "> ?", new String[]{timestamp} , null, null, null, "1");
        if(cursor.getCount()==0){
            Toast.makeText(BottombarActivity.this, "No Data Update", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();

            DateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            Date in = input.parse(cursor.getString(1));
            String out = output.format(in);

            //KD_PELAYANAN[0] belum jelas definisi kodenya jadi sementara menggunakan kode mulai dari 10001
            //KD_PUSKESMAS[1] dan NIK[3] belum tersedia di SQLite jadi di hard core untuk sementara
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put("0", "1000" + i);
                postDataParams.put("1", "P3344556677");
                postDataParams.put("2", out);
                postDataParams.put("3", "111111111111111" + i);
                postDataParams.put("4", cursor.getString(2));
                postDataParams.put("5", cursor.getString(3));
                postDataParams.put("6", cursor.getString(4));
                postDataParams.put("7", cursor.getString(5));
                postDataParams.put("8", cursor.getString(6));
                postDataParams.put("9", cursor.getString(7));
                postDataParams.put("10", cursor.getString(8));
                postDataParams.put("11", cursor.getString(9));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //memanggil asynctask
            new UpdateMedrecDinamik(this).execute(postDataParams.toString(), token, "" + cursor.getString(1));
            Log.i("Array", postDataParams.toString());
        }
    }

    //fungsi untuk membaca response dari rest service saat melakukan update medrek dinamik
    @Override
    public void taskComplete(String output) {
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

        if(code.equals("0x23")){
            Toast.makeText(BottombarActivity.this, "Generating new token ... ", Toast.LENGTH_SHORT).show();
            new TokenRequest(this).execute(username, password);
        } else if(code.equals("1x26")){
            try {
                getDataAndPost();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(BottombarActivity.this, "Update data berhasil", Toast.LENGTH_SHORT).show();
        } else if(code.equals("0x26")){
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

        if(code.equals("1x11")){
            SharedPreferences sp = getSharedPreferences("TOKEN", MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("ACCESS_TOKEN", accessToken);
            editor.apply();

            Toast.makeText(BottombarActivity.this, "Generate token success", Toast.LENGTH_SHORT).show();
        }
    }
    
//    public void setSubTitleText(String title){
//            txtSubTitle.setText(title);
//        }

}
