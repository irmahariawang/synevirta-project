package com.procodecg.codingmom.ehealth.rekam_medis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.RecycleListAdapter;

/**
 * Created by macbookpro on 8/30/17.
 */

public class RekmedlamaActivity extends AppCompatActivity {

    private TextView txtTitle;
    private EhealthDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MENGHILANGKAN TOOLBAR
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rekmedlama);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Rekam Medis");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.xblue);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),BottombarActivity.class));
                finish();
            }
        });

        final TextView textIDPuskesmas = (TextView) findViewById(R.id.showIDPuskesmas);
        final TextView textPoli = (TextView) findViewById(R.id.showPoli);
        final TextView textPemberiRujukan = (TextView) findViewById(R.id.showPemberiRujukan);
        final TextView textSystole = (TextView) findViewById(R.id.showSystole);
        final TextView textDiastole = (TextView) findViewById(R.id.showDiastole);
        final TextView textSuhu = (TextView) findViewById(R.id.showSuhu);
        final TextView textNadi = (TextView) findViewById(R.id.showNadi);
        final TextView textRespirasi = (TextView) findViewById(R.id.showRespirasi);
        final TextView textKeluhanUtama = (TextView) findViewById(R.id.showKeluhan);
        final TextView textRiwayatPenyakitSkr = (TextView) findViewById(R.id.showRiwayatPenyakitSkr);
        final TextView textRiwayatPenyakitDulu= (TextView) findViewById(R.id.showRiwayatPenyakitDulu);
        final TextView textRiwayatPenyakitKel= (TextView) findViewById(R.id.showRiwayatPenyakitKel);
        final TextView textTinggi = (TextView) findViewById(R.id.showTinggi);
        final TextView textBerat= (TextView) findViewById(R.id.showBerat);
        final TextView textKesadaran= (TextView) findViewById(R.id.showKesadaran);
        final TextView textKepala = (TextView) findViewById(R.id.showKepala);
        final TextView textThorax= (TextView) findViewById(R.id.showThorax);
        final TextView textAbdomen= (TextView) findViewById(R.id.showAbdomen);
        final TextView textGenitalia = (TextView) findViewById(R.id.showGenitalia);
        final TextView textExtremitas= (TextView) findViewById(R.id.showExtremitas);
        final TextView textKulit= (TextView) findViewById(R.id.showKulit);
        final TextView textNeurologi= (TextView) findViewById(R.id.showNeurologi);
        final TextView textLab = (TextView) findViewById(R.id.showLaboratorium);
        final TextView textRadio= (TextView) findViewById(R.id.showRadiologi);
        final TextView textStatusLabRadio= (TextView) findViewById(R.id.showStatusLabRadio);
        final TextView textDiagnosaKerja = (TextView) findViewById(R.id.showDiagnosaKerja);
        final TextView textDiagnosaBanding= (TextView) findViewById(R.id.showDiagnosaBanding);
        final TextView textICD10= (TextView) findViewById(R.id.showICD10);
        final TextView textResep= (TextView) findViewById(R.id.showResep);
        final TextView textCatResep = (TextView) findViewById(R.id.showCatResep);
        final TextView textStatusResep= (TextView) findViewById(R.id.showStatusResep);
        // repetisi resep
        final TextView textRepetisiResep= (TextView) findViewById(R.id.showrepetisiResep);
        final TextView textTindakan= (TextView) findViewById(R.id.showTindakan);
        final TextView textAdVitam = (TextView) findViewById(R.id.showAdVitam);
        final TextView textAdFunctionam= (TextView) findViewById(R.id.showAdFunctionam);
        final TextView textAdSanationam= (TextView) findViewById(R.id.showAdSanationam);


        mDbHelper = new EhealthDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                EhealthContract.RekamMedisEntry._ID,
                EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS,
                EhealthContract.RekamMedisEntry.COLUMN_POLI,
                EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN,
                EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_SUHU,
                EhealthContract.RekamMedisEntry.COLUMN_NADI,
                EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI,
                EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL,
                EhealthContract.RekamMedisEntry.COLUMN_TINGGI,
                EhealthContract.RekamMedisEntry.COLUMN_BERAT,
                EhealthContract.RekamMedisEntry.COLUMN_KESADARAN,
                EhealthContract.RekamMedisEntry.COLUMN_KEPALA,
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

        String whereClause = EhealthContract.RekamMedisEntry._ID + " = ? ";
        String[] whereArgs = new String[]{RecycleListAdapter.getCurrentPosition()};

        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME,projection, whereClause,whereArgs,null,null,null);

        try{
            int idColumnIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry._ID);
            int idPuskesmasIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS);
            int idPoliIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_POLI);
            int idRujukanIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN);
            int idSystoleIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE);
            int idDiastoleIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE);
            int idSuhuIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_SUHU);
            int idNadiIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_NADI);
            int idRespirasiIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI);
            int idKeluhanIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA);
            int idPenyakitSkrIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG);
            int idPenyakitDuluIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU);
            int idPenyakitKelIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL);
            int idTinggiIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_TINGGI);
            int idBeratIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_BERAT);
            int idKesadaranIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_KESADARAN);
            int idKepalaIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_KEPALA);
            int idThoraxIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_THORAX);
            int idAbdomenIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_ABDOMEN);
            int idGenitaliaIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_GENITALIA);
            int idExtremitasIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_EXTREMITAS);
            int idKulitIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_KULIT);
            int idNeurologiIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_NEUROLOGI);
            int idLabIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_LABORATORIUM);
            int idRadioIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_RADIOLOGI);
            int idStatusLabRadioIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_STATUS_LABRADIO);
            int idDiagKerjaIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA);
            int idDiagBandingIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING);
            int idICD10Index = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_ICD10_DIAGNOSA);
            int idResepIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_RESEP);
            int idCatResepIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_CATTRESEP);
            int idStatusResepIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_STATUSRESEP);
            int idRepetisiResepIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_REPETISIRESEP);
            int idTindakanIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_TINDAKAN);
            int idAdVitamIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_AD_VITAM);
            int idAdFunctionamIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_AD_FUNCTIONAM);
            int idAdSanationamIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_AD_SANATIONAM);






            while (cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);

                String currentName = cursor.getString(idPuskesmasIndex);
                textIDPuskesmas.setText(currentName);

                int currentPoli = cursor.getInt(idPoliIndex);
                textPoli.setText(getPoliString(currentPoli));

                String currentRujukan = cursor.getString(idRujukanIndex);
                textPemberiRujukan.setText(currentRujukan);

                String currentSystole = cursor.getString(idSystoleIndex);
                textSystole.setText(currentSystole);

                String currentDiastole = cursor.getString(idDiastoleIndex);
                textDiastole.setText(currentDiastole);

                String currentSuhu = cursor.getString(idSuhuIndex);
                textSuhu.setText(currentSuhu);

                String currentNadi = cursor.getString(idNadiIndex);
                textNadi.setText(currentNadi);

                String currentRespirasi = cursor.getString(idRespirasiIndex);
                textRespirasi.setText(currentRespirasi);

                String currentKeluhan = cursor.getString(idKeluhanIndex);
                textKeluhanUtama.setText(currentKeluhan);

                String currentPenyakitSkr = cursor.getString(idPenyakitSkrIndex);
                textRiwayatPenyakitSkr.setText(currentPenyakitSkr);

                String currentPenyakitDulu = cursor.getString(idPenyakitDuluIndex);
                textRiwayatPenyakitDulu.setText(currentPenyakitDulu);

                String currentPenyakitKel = cursor.getString(idPenyakitKelIndex);
                textRiwayatPenyakitKel.setText(currentPenyakitKel);

                String currentTinggi = cursor.getString(idTinggiIndex);
                textTinggi.setText(currentTinggi);

                String currentBerat = cursor.getString(idBeratIndex);
                textBerat.setText(currentBerat);

                int currentKesadaran = cursor.getInt(idKesadaranIndex);
                textKesadaran.setText(getKesadaranString(currentKesadaran));

                String currentKepala = cursor.getString(idKepalaIndex);
                textKepala.setText(currentKepala);

                String currentThorax = cursor.getString(idThoraxIndex);
                textThorax.setText(currentThorax);

                String currentAbdomen = cursor.getString(idAbdomenIndex);
                textAbdomen.setText(currentAbdomen);

                String currentGenitalia = cursor.getString(idGenitaliaIndex);
                textGenitalia.setText(currentGenitalia);

                String currentExtremitas = cursor.getString(idExtremitasIndex);
                textExtremitas.setText(currentExtremitas);

                String currentKulit = cursor.getString(idKulitIndex);
                textKulit.setText(currentKulit);

                String currentNeurologi = cursor.getString(idNeurologiIndex);
                textNeurologi.setText(currentNeurologi);

                String currentLab = cursor.getString(idLabIndex);
                textLab.setText(currentLab);

                String currentRadio = cursor.getString(idRadioIndex);
                textRadio.setText(currentRadio);

                int currentStatusLabRadio = cursor.getInt(idStatusLabRadioIndex);
                textStatusLabRadio.setText(getStatusLabRadio(currentStatusLabRadio));

                String currentDiagKerja = cursor.getString(idDiagKerjaIndex);
                textDiagnosaKerja.setText(currentDiagKerja);

                String currentDiagBanding = cursor.getString(idDiagBandingIndex);
                textDiagnosaBanding.setText(currentDiagBanding);

                String currentICD10 = cursor.getString(idICD10Index);
                textICD10.setText(currentICD10);

                String currentResep = cursor.getString(idResepIndex);
                textResep.setText(currentResep);

                String currentCatResep = cursor.getString(idCatResepIndex);
                textCatResep.setText(currentCatResep);

                int currentStatusResep = cursor.getInt(idStatusResepIndex);
                textStatusResep.setText(getStatusResep(currentStatusResep));

                int currentRepetisiResep = cursor.getInt(idRepetisiResepIndex);
                textRepetisiResep.setText(getRepetisiResepString(currentRepetisiResep));

                String currentTindakan = cursor.getString(idTindakanIndex);
                textTindakan.setText(currentTindakan);

                int currentAdVitam = cursor.getInt(idAdVitamIndex);
                textAdVitam.setText(getAd(currentAdVitam));

                int currentAdFunctionam = cursor.getInt(idAdFunctionamIndex);
                textAdFunctionam.setText(getAd(currentAdFunctionam));

                int currentAdSanationam = cursor.getInt(idAdSanationamIndex);
                textAdSanationam.setText(getAd(currentAdSanationam));

            }
        } finally {
            cursor.close();
        }

        //TextView displayView = (TextView) findViewById(R.id.text_view_pet);
        Toast.makeText(this, RecycleListAdapter.getCurrentPosition(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, getIntent().getStringExtra("dokter"), Toast.LENGTH_SHORT).show();
    }

    public String getPoliString (int currentPoli){
        String Poli = "";

        if (currentPoli == 0) {
             Poli = "Umum";
        } else {
            Poli = "Gigi";
        }
        return Poli;
    }

    public String getKesadaranString (int currentKesadaran){
        String Kesadaran = "";

        if (currentKesadaran == 0) {
            Kesadaran = "Composmentis";
        } else if (currentKesadaran == 1) {
            Kesadaran = "Apatis";
        } else if (currentKesadaran == 2) {
            Kesadaran = "Delirium";
        } else if (currentKesadaran == 3) {
            Kesadaran = "Somnolen";
        } else if (currentKesadaran == 4) {
            Kesadaran = "Sopor";
        } else if (currentKesadaran == 5) {
            Kesadaran = "Semicoma";
        } else {
            Kesadaran = "Coma";
        }
        return Kesadaran;
    }

    public String getStatusLabRadio (int currentStatusLabRadio){
        String StatusLabRadio = "";

        if (currentStatusLabRadio == 0) {
            StatusLabRadio = "Dilayani penuh";
        } else if (currentStatusLabRadio == 1) {
            StatusLabRadio = "Dilayani sebagian";
        } else {
            StatusLabRadio = "Tidak dilayani";
        }
        return StatusLabRadio;
    }
    public String getStatusResep (int currentStatusResep){
        String statusResep = "";

        if (currentStatusResep == 0) {
            statusResep = "Resep dilayani penuh";
        } else if (currentStatusResep == 1) {
            statusResep = "Resep tidak dilayani";
        } else if (currentStatusResep == 2) {
            statusResep = "Resep dilayani sebagian";
        } else if (currentStatusResep == 3) {
            statusResep = "Resep dilayani penggantian";
        } else {
            statusResep = "Resep dilayani sebagian penggantian";
        }
        return statusResep;
    }

    public String getRepetisiResepString (int currentRepetisiResep){
        String repetisiResep = "";

        if (currentRepetisiResep == 0) {
            repetisiResep = "Tidak";
        } else {
            repetisiResep = "Ya";
        }
        return repetisiResep;
    }

    public String getAd (int currentStatusAd){
        String statusAd = "";

        if (currentStatusAd == 0) {
            statusAd = "Ad Bonam";
        } else if (currentStatusAd == 1) {
            statusAd = "Dubia Ad Bonam";
        } else if (currentStatusAd == 2) {
            statusAd = "Dubia Ad Malam";
        } else {
            statusAd = "Ad Malam";
        }
        return statusAd;
    }

}