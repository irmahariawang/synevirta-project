package com.procodecg.codingmom.ehealth.pasien;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;

/**
 * Created by macbookpro on 8/12/17.
 */

public class KeluargaActivity extends AppCompatActivity {

    private TextView txtTitle;
    Typeface fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MENGHILANGKAN TOOLBAR
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_keluarga);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Kontak Keluarga");

        fontBold = Typeface.createFromAsset(getAssets(),"font1bold.ttf");
        txtTitle.setTypeface(fontBold);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.xblue);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BottombarActivity.class));
            }
        });

        //set popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.92),(int)(height*.85));

        TextView namaKerabatTv = (TextView) findViewById(R.id.textNamaKerabat);
        namaKerabatTv.setText(PDCData.kewarganegaraan);

        TextView hubunganKerabatTv = (TextView) findViewById(R.id.textHubunganKerabat);
        hubunganKerabatTv.setText(PDCData.hubunganKerabat);

        TextView alamatKerabatTv = (TextView) findViewById(R.id.textAlamatKerabat);
        alamatKerabatTv.setText(PDCData.alamatKerabat);

        TextView kelurahanDesaTv = (TextView) findViewById(R.id.textKelurahanDesaKerabat);
        kelurahanDesaTv.setText(PDCData.kelurahanDesaKerabat);

        TextView kecamatanTv = (TextView) findViewById(R.id.textKecamatanKerabat);
        kecamatanTv.setText(PDCData.kecamatanKerabat);

        TextView kotaKabupatenTv = (TextView) findViewById(R.id.textKotaKebupatenKerabat);
        kotaKabupatenTv.setText(PDCData.kotaKabupatenKerabat);

        TextView propinsiTv = (TextView) findViewById(R.id.textPropinsiKerabat);
        propinsiTv.setText(PDCData.provinsiKerabat);

        TextView kodePosTv = (TextView) findViewById(R.id.textKodePosKerabat);
        kodePosTv.setText(PDCData.kodeposKerabat);

        TextView teleponKerabatTv = (TextView) findViewById(R.id.textTeleponKerabat);
        teleponKerabatTv.setText(PDCData.teleponKerabat);

        TextView hpKerabatTv = (TextView) findViewById(R.id.textHpKerabat);
        hpKerabatTv.setText(PDCData.hpKerabat);

        TextView namaKantorTv = (TextView) findViewById(R.id.textNamaKantor);
        namaKantorTv.setText(PDCData.namaKantor);

        TextView alamatKantorTv = (TextView) findViewById(R.id.textAlamatKantor);
        alamatKantorTv.setText(PDCData.alamatKantor);

        TextView kotaKabupatenKantorTv = (TextView) findViewById(R.id.textKotaKabupatenKantor);
        kotaKabupatenKantorTv.setText(PDCData.kotaKabupatenKantor);

        TextView teleponKantorTv = (TextView) findViewById(R.id.textTeleponKantor);
        teleponKantorTv.setText(PDCData.teleponKantor);

        TextView hpKantorTv = (TextView) findViewById(R.id.textHpKantor);
        hpKantorTv.setText(PDCData.hpKantor);

    }
}
