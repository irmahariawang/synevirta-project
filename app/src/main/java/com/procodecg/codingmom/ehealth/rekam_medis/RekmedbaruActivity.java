package com.procodecg.codingmom.ehealth.rekam_medis;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cielyang.android.clearableedittext.ClearableEditText;
import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.utils.NothingSelectedSpinnerAdapter;
import com.procodecg.codingmom.ehealth.utils.Validation;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by macbookpro on 9/4/17.
 */

public class RekmedbaruActivity extends AppCompatActivity {

    Typeface fontBold;

    private TextView txtTitle;
    private int mPoli = RekamMedisEntry.POLI_UMUM;
    private int mKesadaran = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
    private int mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
    private int mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
    private RadioGroup radioGroup;
    private int mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_TIDAK;
    private int mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
    private int mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
    private int mAdSanationam = RekamMedisEntry.SANATIONAM_ADBONAM;

    private EditText mIDPuskesmas; private Spinner mPoliSpinner; private EditText mPemberiRujukan;
    private EditText mSystole; private EditText mDiastole; private EditText mSuhu; private EditText mNadi; private EditText mRespirasi;
    private EditText mKeluhanUtama; private EditText mRiwayatPenyakitSkr; private EditText mRiwayatPenyakitDulu; private EditText mRiwayatPenyakitKel;
    private EditText mTinggi; private EditText mBerat; private Spinner mKesadaranSpinner;
    private EditText mKepala; private EditText mThorax; private EditText mAbdomen;private EditText mGenitalia; private EditText mExtremitas; private EditText mKulit; private EditText mNeurologi;
    private EditText mLaboratorium; private EditText mRadiologi; private Spinner mStatusLabRadioSpinner;
    private EditText mDiagnosisKerja; private EditText mDiagnosisBanding; private AutoCompleteTextView mICD10;
    private EditText mResep; private EditText mCatatanResep; private Spinner mStatusResepSpinner; private RadioGroup mRepetisiResepBtn; private ClearableEditText mTindakan;
    private Spinner mAdVitamSpinner; private Spinner mAdFunctionamSpinner; private Spinner mAdSanationamSpinner;

    private ClearableEditText idPuskesmas;
    private SharedPreferences prefs;

    Activity mActivity;

//    aktivasi tombol x-clear


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MENGHILANGKAN TOOLBAR
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rekmedbaru);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Rekam Medis Baru");

        fontBold = Typeface.createFromAsset(getAssets(),"font1bold.ttf");
        txtTitle.setTypeface(fontBold);

        //menampilkan nama puskesmas
        prefs=getSharedPreferences("DATAPUSKES",MODE_PRIVATE);
        String namapuskes=prefs.getString("IDPUSKES","");

        idPuskesmas = (ClearableEditText) findViewById(R.id.idPuskesmas);
        idPuskesmas.setText(namapuskes);


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setScrollbarFadingEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setNavigationIcon(R.drawable.ic_xclose);
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        View logo = getLayoutInflater().inflate(R.layout.activity_rekmedbaru, null);
//        mToolbar.addView(logo, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.setContentInsetsAbsolute(0,0);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView txt = (TextView) findViewById(R.id.txt_title);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(txt.getLayoutParams());
//        lp.setMargins(0, 0, 0, 0);
//        txt.setLayoutParams(lp);
//        toolbar.setTitleMarginStart(0);
//        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) toolbar.getLayoutParams();
//        layoutParams.setMargins(0, 0, 0, 0);
//        toolbar.setLayoutParams(layoutParams);
//        getResources().getDimension(R.dimen.toolbar_right);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),BottombarActivity.class));

            }
        });

        // Find all relevant views that we will need to read user input from
        mIDPuskesmas = (EditText) findViewById(R.id.idPuskesmas);
        mPoliSpinner = (Spinner) findViewById(R.id.poli_spinner);
        mPemberiRujukan = (EditText) findViewById(R.id.pemberiRujukan);
        mSystole = (EditText) findViewById(R.id.systole);
        mDiastole = (EditText) findViewById(R.id.diastole);
        mSuhu = (EditText) findViewById(R.id.suhu);
        mNadi = (EditText) findViewById(R.id.nadi);
        mRespirasi = (EditText) findViewById(R.id.respirasi);
        mKeluhanUtama = (EditText) findViewById(R.id.keluhanUtama);
        mRiwayatPenyakitSkr = (EditText) findViewById(R.id.rps);
        mRiwayatPenyakitDulu = (EditText) findViewById(R.id.rpd);
        mRiwayatPenyakitKel = (EditText) findViewById(R.id.rpk);
        mTinggi = (EditText) findViewById(R.id.tinggi);
        mBerat = (EditText) findViewById(R.id.berat);
        mKesadaranSpinner = (Spinner) findViewById(R.id.kesadaran_spinner);
        mKepala = (EditText) findViewById(R.id.pemeriksaanKepala);
        mThorax = (EditText) findViewById(R.id.pemeriksaanThorax);
        mAbdomen = (EditText) findViewById(R.id.pemeriksaanAbdomen);
        mGenitalia = (EditText) findViewById(R.id.pemeriksaanGenitalia);
        mExtremitas = (EditText) findViewById(R.id.pemeriksaanExtremitas);
        mKulit = (EditText) findViewById(R.id.pemeriksaanKulit);
        mNeurologi = (EditText) findViewById(R.id.pemeriksaanNeuro);
        mLaboratorium = (EditText) findViewById(R.id.laboratorium);
        mRadiologi = (EditText) findViewById(R.id.radiologi);
        mStatusLabRadioSpinner = (Spinner) findViewById(R.id.statusLabRadio);
        mDiagnosisKerja = (EditText) findViewById(R.id.diagnosisKerja);
        mDiagnosisBanding = (EditText) findViewById(R.id.diagnosisBanding);
        mICD10 = (AutoCompleteTextView) findViewById(R.id.statusDiagnosis);
        mResep = (EditText) findViewById(R.id.resep);
        mCatatanResep = (EditText) findViewById(R.id.catatanResep);
        mStatusResepSpinner = (Spinner) findViewById(R.id.statusResep);
        mRepetisiResepBtn = (RadioGroup) findViewById(R.id.repetisiResep);
        mTindakan = (ClearableEditText) findViewById(R.id.tindakan);
        mAdVitamSpinner = (Spinner) findViewById(R.id.adVitam);
        mAdFunctionamSpinner = (Spinner) findViewById(R.id.adFunctionam);
        mAdSanationamSpinner = (Spinner) findViewById(R.id.adSanationam);


        // setup spinner
        setupSpinner();

        //setup autocomplete
        setupAutoComplete();



    //BUTTON SAVE
    Button mShowDialog = (Button) findViewById(R.id.btnShowDialog);
        mShowDialog.setOnClickListener(new View.OnClickListener() {
    @Override
        public void onClick(final View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RekmedbaruActivity.this);
        mBuilder.setIcon(R.drawable.logo2);
        mBuilder.setTitle("Data yang Anda masukkan tidak dapat dirubah lagi");
        mBuilder.setMessage("Apakah Anda akan menyimpan data sekarang?");
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
                simpanData();
                dialogInterface.dismiss();
                //startActivity(new Intent(getApplicationContext(),BottombarActivity.class));
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
        }
        });




//menampilkan nama puskesmas
/*
        HashMap<String, String> setting = setconfig.getDetail();
        idpuskes.setText(setting.get(SetConfig.KEY_IDPUSKES));
        namapuskes.setText(setting.get(SetConfig.KEY_NAMAPUSKES));
*/
        }

        private void setupSpinner(){

            //spinner poli
            Spinner spinnerPoli = (Spinner) findViewById(R.id.poli_spinner);
            ArrayAdapter<CharSequence> adapterPoli = ArrayAdapter.createFromResource(this,
                    R.array.poli, android.R.layout.simple_spinner_item);
            adapterPoli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPoli.setPrompt("Pilih poli tujuan");
            spinnerPoli.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterPoli,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerPoli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Gigi")) {
                            mPoli = RekamMedisEntry.POLI_GIGI;
                        } else {
                            mPoli = RekamMedisEntry.POLI_UMUM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mPoli = RekamMedisEntry.POLI_UMUM;
                }
            });


            //spinner kesadaran
            Spinner spinnerKesadaran = (Spinner) findViewById(R.id.kesadaran_spinner);
            ArrayAdapter<CharSequence> adapterKesadaran = ArrayAdapter.createFromResource(this,
                    R.array.kesadaran, android.R.layout.simple_spinner_item);
            adapterKesadaran.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerKesadaran.setPrompt("Pilih tingkat kesadaran");
            spinnerKesadaran.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterKesadaran,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerKesadaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Apatis")) {
                            mKesadaran = RekamMedisEntry.KESADARAN_APATIS;
                        } else if (selection.equals("Delirium")){
                            mKesadaran = RekamMedisEntry.KESADARAN_DELIRIUM;
                        } else if (selection.equals("Somnolen")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SOMNOLEN;
                        } else if (selection.equals("Sopor")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SOPOR;
                        } else if (selection.equals("Semi-coma")){
                            mKesadaran = RekamMedisEntry.KESADARAN_SEMICOMA;
                        } else if (selection.equals("Coma")){
                            mKesadaran = RekamMedisEntry.KESADARAN_COMA;
                        } else {
                            mPoli = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mKesadaran = RekamMedisEntry.KESADARAN_COMPOSMENTIS;
                }
            });

            //spinner status Laboratorium Radiologi
            Spinner spinnerLabRadio = (Spinner) findViewById(R.id.statusLabRadio);
            ArrayAdapter<CharSequence> adapterLabRadio = ArrayAdapter.createFromResource(this,
                    R.array.status_labradio, android.R.layout.simple_spinner_item);
            adapterLabRadio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLabRadio.setPrompt("Pilih status Laboratorium Radiologi");
            spinnerLabRadio.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterLabRadio,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerLabRadio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dilayani sebagian")) {
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANISEBAGIAN;
                        } else if (selection.equals("Tidak dilayani sama sekali")){
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_TIDAKDILAYANI;
                        } else {
                            mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mStatusLabRadio = RekamMedisEntry.LABRADIO_DILAYANIPENUH;
                }
            });

            //spinner status resep
            Spinner spinnerResep = (Spinner) findViewById(R.id.statusResep);
            ArrayAdapter<CharSequence> adapterResep = ArrayAdapter.createFromResource(this,
                    R.array.status_resep, android.R.layout.simple_spinner_item);
            adapterResep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerResep.setPrompt("Pilih status resep");
            spinnerResep.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterResep,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerResep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dilayani sebagian")) {
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_SEBAGIAN;
                        } else if (selection.equals("Dilayani ada penggantian")){
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENGGANTIAN;
                        } else if (selection.equals("Dilayani sebagian dan ada penggantian")){
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_SEBAGIAN_PENGGANTIAN;
                        } else if (selection.equals("Tidak dilayani sama sekali")){
                            mStatusResep = RekamMedisEntry.RESEP_TIDAK_DILAYANI;
                        } else {
                            mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mStatusResep = RekamMedisEntry.RESEP_DILAYANI_PENUH;
                }
            });

            //Radio button repetisi resep
            radioGroup = (RadioGroup) findViewById(R.id.repetisiResep);
            radioGroup.clearCheck();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        if (checkedId != 0) {
                            mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_YA;
                        } else {
                            mRepetisiResep = RekamMedisEntry.RESEP_REPETISI_TIDAK;
                        }
                    }

                }
            });


            //spinner status prognosis ad vitam
            Spinner spinnerAdVitam = (Spinner) findViewById(R.id.adVitam);
            ArrayAdapter<CharSequence> adapterAdVitam = ArrayAdapter.createFromResource(this,
                    R.array.ad_vitam, android.R.layout.simple_spinner_item);
            adapterAdVitam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdVitam.setPrompt("Pilih");
            spinnerAdVitam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdVitam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdVitam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdVitam = RekamMedisEntry.VITAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdVitam = RekamMedisEntry.VITAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdVitam = RekamMedisEntry.VITAM_ADMALAM;
                        } else {
                            mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdVitam = RekamMedisEntry.VITAM_ADBONAM;
                }
            });


            //spinner status prognosis ad functionam
            Spinner spinnerAdFunctionam = (Spinner) findViewById(R.id.adFunctionam);
            ArrayAdapter<CharSequence> adapterAdFunctionam = ArrayAdapter.createFromResource(this,
                    R.array.ad_functionam, android.R.layout.simple_spinner_item);
            adapterAdFunctionam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdFunctionam.setPrompt("Pilih");
            spinnerAdFunctionam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdFunctionam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdFunctionam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADMALAM;
                        } else {
                            mAdVitam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdFunctionam = RekamMedisEntry.FUNCTIONAM_ADBONAM;
                }
            });
            //spinner status prognosis ad sanationam
            Spinner spinnerAdSanationam = (Spinner) findViewById(R.id.adSanationam);
            ArrayAdapter<CharSequence> adapterAdSanationam = ArrayAdapter.createFromResource(this,
                    R.array.ad_sanationam, android.R.layout.simple_spinner_item);
            adapterAdSanationam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdSanationam.setPrompt("Pilih");
            spinnerAdSanationam.setAdapter(new NothingSelectedSpinnerAdapter(
                    adapterAdSanationam,
                    R.layout.contact_spinner_row_nothing_selected,
                    // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    this));
            spinnerAdSanationam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals("Dubia ad bonam")) {
                            mAdSanationam = RekamMedisEntry.SANATIONAM_DUBIAADBONAM;
                        } else if (selection.equals("Dubia ad malam")){
                            mAdSanationam = RekamMedisEntry.SANATIONAM_DUBIAADMALAM;
                        } else if (selection.equals("Ad malam")){
                            mAdSanationam = RekamMedisEntry.SANATIONAM_ADMALAM;
                        } else {
                            mAdSanationam = RekamMedisEntry.SANATIONAM_ADBONAM;
                        }
                    }
                }
                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mAdSanationam = RekamMedisEntry.VITAM_ADBONAM;
                }
            });
        }

        private void setupAutoComplete(){
            AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.statusDiagnosis);
            EhealthDbHelper dbHelper = new EhealthDbHelper(getApplicationContext());
            dbHelper.openDB();
            //String pencarian = getIntent().getStringExtra("hasil");
            String[] diagnosa = dbHelper.getAllDiagnosa();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, diagnosa);
            textView.setAdapter(adapter);
            dbHelper.closeDB();
        }

        private void simpanData(){

            EhealthDbHelper mDbHelper = new EhealthDbHelper(this);
            mDbHelper.openDB();
            //mDbHelper.createTableRekMed();

            if(validateData()){
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //String mTanggalPeriksa = sdf.format(new java.util.Date());

                //setting format tanggal device yg baru
                Long tsLong = System.currentTimeMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(tsLong);
                String mTanggalPeriksa = String.valueOf(formatter.format(calendar.getTime()));

                String mIDPuskesmasString = mIDPuskesmas.getText().toString().trim();
                String mNamaDokterString = PasiensyncActivity.getNamaDokter();
                //  PoliSpinner
                String mPemberiRujukanString = mPemberiRujukan.getText().toString().trim();
                String mSystoleString = mSystole.getText().toString().trim();
                String mDiastoleString = mDiastole.getText().toString().trim();
                String mSuhuString = mSuhu.getText().toString().trim();
                String mNadiString = mNadi.getText().toString().trim();
                String mRespirasiString = mRespirasi.getText().toString().trim();
                String mKeluhanUtamaString = mKeluhanUtama.getText().toString().trim();
                String mRiwayatPenyakitSkrString = mRiwayatPenyakitSkr.getText().toString().trim();
                String mRiwayatPenyakitDuluString = mRiwayatPenyakitDulu.getText().toString().trim();
                String mRiwayatPenyakitKelString = mRiwayatPenyakitKel.getText().toString().trim();
                String mTinggiString = mTinggi.getText().toString().trim();
                String mBeratString = mBerat.getText().toString().trim();
                // KesadaranSpinner
                String mKepalaString = mKepala.getText().toString().trim();
                String mThoraxString = mThorax.getText().toString().trim();
                String mAbdomenString = mAbdomen.getText().toString().trim();
                String mGenitaliaString = mGenitalia.getText().toString().trim();
                String mExtremitasString = mExtremitas.getText().toString().trim();
                String mKulitString = mKulit.getText().toString().trim();
                String mNeurologiString = mNeurologi.getText().toString().trim();
                String mLaboratoriumString = mLaboratorium.getText().toString().trim();
                String mRadiologiString = mRadiologi.getText().toString().trim();
                // StatusLabRadioSpinner
                String mDiagnosisKerjaString = mDiagnosisKerja.getText().toString().trim();
                String mDiagnosisBandingString = mDiagnosisBanding.getText().toString().trim();
                String mICD10String = mICD10.getText().toString().trim();
                String mResepString = mResep.getText().toString().trim();
                String mCatatanResepString = mCatatanResep.getText().toString().trim();
                // StatusResepSpinner
                String mTindakanString = mTindakan.getText().toString().trim();
                // AdVitamSpinner
                // AdFunctionamSpinner
                // AdSanationamSpinner

                // Gets the database in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(RekamMedisEntry.COLUMN_TGL_PERIKSA, mTanggalPeriksa);
                values.put(RekamMedisEntry.COLUMN_NAMA_DOKTER, mNamaDokterString);
                values.put(RekamMedisEntry.COLUMN_ID_PUSKESMAS, mIDPuskesmasString);
                values.put(RekamMedisEntry.COLUMN_POLI, mPoli);
                values.put(RekamMedisEntry.COLUMN_RUJUKAN, mPemberiRujukanString);
                values.put(RekamMedisEntry.COLUMN_SYSTOLE, mSystoleString);
                values.put(RekamMedisEntry.COLUMN_DIASTOLE, mDiastoleString);
                values.put(RekamMedisEntry.COLUMN_SUHU, mSuhuString);
                values.put(RekamMedisEntry.COLUMN_NADI, mNadiString);
                values.put(RekamMedisEntry.COLUMN_RESPIRASI, mRespirasiString);
                values.put(RekamMedisEntry.COLUMN_KELUHANUTAMA, mKeluhanUtamaString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG, mRiwayatPenyakitSkrString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_DULU, mRiwayatPenyakitDuluString);
                values.put(RekamMedisEntry.COLUMN_PENYAKIT_KEL, mRiwayatPenyakitKelString);
                values.put(RekamMedisEntry.COLUMN_TINGGI, mTinggiString);
                values.put(RekamMedisEntry.COLUMN_BERAT, mBeratString);
                values.put(RekamMedisEntry.COLUMN_KESADARAN, mKesadaran);
                values.put(RekamMedisEntry.COLUMN_KEPALA, mKepalaString);
                values.put(RekamMedisEntry.COLUMN_THORAX, mThoraxString);
                values.put(RekamMedisEntry.COLUMN_ABDOMEN, mAbdomenString);
                values.put(RekamMedisEntry.COLUMN_GENITALIA, mGenitaliaString);
                values.put(RekamMedisEntry.COLUMN_EXTREMITAS, mExtremitasString);
                values.put(RekamMedisEntry.COLUMN_KULIT, mKulitString);
                values.put(RekamMedisEntry.COLUMN_NEUROLOGI, mNeurologiString);
                values.put(RekamMedisEntry.COLUMN_LABORATORIUM, mLaboratoriumString);
                values.put(RekamMedisEntry.COLUMN_RADIOLOGI, mRadiologiString);
                values.put(RekamMedisEntry.COLUMN_STATUS_LABRADIO, mStatusLabRadio);
                values.put(RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA, mDiagnosisKerjaString);
                values.put(RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING, mDiagnosisBandingString);
                values.put(RekamMedisEntry.COLUMN_ICD10_DIAGNOSA, mICD10String);
                values.put(RekamMedisEntry.COLUMN_RESEP, mResepString);
                values.put(RekamMedisEntry.COLUMN_CATTRESEP, mCatatanResepString);
                values.put(RekamMedisEntry.COLUMN_STATUSRESEP, mStatusResep);
                values.put(RekamMedisEntry.COLUMN_REPETISIRESEP, mRepetisiResep);
                values.put(RekamMedisEntry.COLUMN_TINDAKAN, mTindakanString);
                values.put(RekamMedisEntry.COLUMN_AD_VITAM, mAdVitam);
                values.put(RekamMedisEntry.COLUMN_AD_FUNCTIONAM, mAdFunctionam);
                values.put(RekamMedisEntry.COLUMN_AD_SANATIONAM, mAdSanationam);


                // Insert a new row for pet in the database, returning the ID of that new row.
                long newRowId = db.insert(RekamMedisEntry.TABLE_NAME, null, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newRowId == -1) {
                    // If the row ID is -1, then there was an error with insertion.
                    //Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast with the row ID.
                    //Toast.makeText(this, "Data saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
                    //simpanData();
                    finish();
                }
                mDbHelper.closeDB();
            }
        }

    //VALIDASI ISIAN DATA
    private boolean validateData(){
        boolean valid = true;

        if (!Validation.hasText(mSystole, "Systole")) valid = false;
        if (!Validation.hasText(mDiastole, "Diastole")) valid = false;
        if (!Validation.hasText(mSuhu, "Suhu")) valid = false;
        if (!Validation.hasText(mNadi, "Nadi")) valid = false;
        if (!Validation.hasText(mRespirasi, "Respirasi")) valid = false;
        if (!Validation.hasText(mKeluhanUtama, "Keluhan Utama")) valid = false;
//        if (!Validation.hasText(mRiwayatPenyakitSkr, "Riwayat Penyakit Sekarang ")) valid = false;
//        if (!Validation.hasText(mRiwayatPenyakitDulu, "Riwayat Penyakit Dahulu")) valid = false;
//        if (!Validation.hasText(mRiwayatPenyakitKel, "Riwayat Penyakit Kelaurga")) valid = false;
        if (!Validation.hasText(mBerat, "Berat")) valid = false;
        if (!Validation.hasText(mKepala, "Kepala")) valid = false;
        if (!Validation.hasText(mThorax, "Thorax")) valid = false;
        if (!Validation.hasText(mAbdomen, "Abdomen")) valid = false;
        if (!Validation.hasText(mDiagnosisKerja, "Diagnosis Kerja")) valid = false;
        if (!Validation.hasText(mICD10, "ICD10")) valid = false;
        if (!Validation.hasText(mResep, "Resep")) valid = false;

        return valid;
    }
}