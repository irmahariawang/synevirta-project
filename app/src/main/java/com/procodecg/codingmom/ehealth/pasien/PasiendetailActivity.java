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
 * Created by macbookpro on 8/9/17.
 */

public class PasiendetailActivity extends AppCompatActivity {

    private TextView txtTitle;
    Typeface fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MENGHILANGKAN TOOLBAR
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pasiendetail);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Bio Data Pasien");

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

        getWindow().setLayout((int)(width*.92),(int)(height*.88));


        //TextView namaPasienTv = (TextView) findViewById(R.id.textNoSmartCard);
        //namaPasienTv.setText(PDCData.noSmartcard);

        TextView kategoriPasienTv = (TextView) findViewById(R.id.textKategoriPasien);
        kategoriPasienTv.setText(PDCData.kategoriPasien);

        TextView nomerAsuransiTv = (TextView) findViewById(R.id.textNoAsuransi);
        nomerAsuransiTv.setText(PDCData.noAsuransi);

        TextView tanggalDaftarTv = (TextView) findViewById(R.id.textTglDaftar);
        tanggalDaftarTv.setText(PDCData.tglDaftar.toString());

        TextView kelasPerawatanTv = (TextView) findViewById(R.id.textKelasPerawatan);
        kelasPerawatanTv.setText(PDCData.kelasPerawatan);

        TextView namaPasienTv = (TextView) findViewById(R.id.textNamaPasienDetail);
        kategoriPasienTv.setText(PDCData.kategoriPasien);

        TextView namaKKTv = (TextView) findViewById(R.id.textNamaKK);
        namaKKTv.setText(PDCData.namaKK);

        TextView hubunganKeluargaTv = (TextView) findViewById(R.id.textHubKeluarga);
        hubunganKeluargaTv.setText(PDCData.hubunganKeluarga);

        TextView alamatTv = (TextView) findViewById(R.id.textAlamat);
        alamatTv.setText(PDCData.alamat);

        TextView RtRwTv = (TextView) findViewById(R.id.textRtRw);
        String RtRw = PDCData.rt + "/" + PDCData.rw;
        RtRwTv.setText(RtRw);

        TextView kelurahanTv = (TextView) findViewById(R.id.textKelurahan);
        kelurahanTv.setText(PDCData.kelurahanDesa);

        TextView kecamatanTv = (TextView) findViewById(R.id.textKecamatan);
        kecamatanTv.setText(PDCData.kecamatan);

        TextView kotaKabTv = (TextView) findViewById(R.id.textKotaKab);
        kotaKabTv.setText(PDCData.kotaKabupaten);

        TextView propinsiTv = (TextView) findViewById(R.id.textPropinsi);
        propinsiTv.setText(PDCData.provinsi);

        TextView kodePosTv = (TextView) findViewById(R.id.textKodePos);
        kodePosTv.setText(PDCData.kodepos);

        TextView isDalamWilayahKerjaTv = (TextView) findViewById(R.id.textIsDalamWilayahKerja);
        isDalamWilayahKerjaTv.setText(PDCData.isDalamWilayahKerja);

        TextView tempatTglLahirTv = (TextView) findViewById(R.id.textTempatTanggalLahir);
        String tempatTglLahir = PDCData.tempatLahir + "-" + PDCData.tglLahir;
        tempatTglLahirTv.setText(tempatTglLahir);

        TextView teleponTv = (TextView) findViewById(R.id.textTelepon);
        teleponTv.setText(PDCData.telepon);

        TextView hpTv = (TextView) findViewById(R.id.textHp);
        hpTv.setText(PDCData.hp);

        TextView NIKTv = (TextView) findViewById(R.id.textNIK);
        NIKTv.setText(PDCData.nik);

        TextView jenisKelaminTv = (TextView) findViewById(R.id.textJenisKlmnDetail);
        jenisKelaminTv.setText(PDCData.jenisKelamin);

        TextView agamaTv = (TextView) findViewById(R.id.textAgama);
        agamaTv.setText(PDCData.agama);

        TextView pendidikanTv = (TextView) findViewById(R.id.textPendidikan);
        pendidikanTv.setText(PDCData.pendidikan);

        TextView pekerjaanTv = (TextView) findViewById(R.id.textPekerjaan);
        pekerjaanTv.setText(PDCData.pekerjaan);

        TextView emailTv = (TextView) findViewById(R.id.textEmail);
        emailTv.setText(PDCData.email);

        TextView statusPernikahanTv = (TextView) findViewById(R.id.textStatusPernikahan);
        statusPernikahanTv.setText(PDCData.statusPernikahan);

        TextView kewarganegaraanTv = (TextView) findViewById(R.id.textKewarganegaraan);
        kewarganegaraanTv.setText(PDCData.kewarganegaraan);


    }
}
