package com.procodecg.codingmom.ehealth.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.Util;
import com.procodecg.codingmom.ehealth.main.PasiensyncActivity;
import com.procodecg.codingmom.ehealth.pasien.KeluargaActivity;
import com.procodecg.codingmom.ehealth.pasien.PasiendetailActivity;
import com.procodecg.codingmom.ehealth.utils.FunctionSupport;
import com.procodecg.codingmom.ehealth.utils.SessionManagement;

import java.util.Calendar;
import java.util.Date;

/**
 * (c) 2017
 * Created by :
 *      Coding Mom
 *      Annisa Alifiani
 *      Arieza Nadya
 */

public class ProfilpasienFragment extends Fragment {

    private SessionManagement sessionManagement;

    public static Fragment newInstance() {
        ProfilpasienFragment fragment = new ProfilpasienFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profilpasien, container, false);
        ((BottombarActivity) getActivity()).setTitleText("Profil Pasien");
        ((BottombarActivity) getActivity()).setSubTitleText();

        FloatingActionButton fabDetail = (FloatingActionButton) view.findViewById(R.id.fabDetail);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(getActivity(), PasiendetailActivity.class);
                startActivity(activity);
            }
        };
        fabDetail.setOnClickListener(listener);

        FloatingActionButton fabKeluarga = (FloatingActionButton) view.findViewById(R.id.fabKeluarga);
        View.OnClickListener listenerKel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(getActivity(), KeluargaActivity.class);
                startActivity(activity);
            }
        };
        fabKeluarga.setOnClickListener(listenerKel);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView namaPasienTv = (TextView) getView().findViewById(R.id.textNamaPasien);
        namaPasienTv.setText(PDCData.namaPasien);

        TextView tglLahirTv = (TextView) getView().findViewById(R.id.textTglLahir);
        String tglLahirS = Util.getformattedDate(PDCData.tglLahir);
        tglLahirTv.setText(tglLahirS);

        TextView umurTv = (TextView) getView().findViewById(R.id.textUmur);
        Date tglLahir = (PDCData.tglLahir);
        Calendar cal = Calendar.getInstance();
        cal.setTime(tglLahir);
        String umurS = FunctionSupport.getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        umurTv.setText(umurS);

        TextView jenisKelaminTv = (TextView) getView().findViewById(R.id.textJenisKlmn);
        String jenisKelaminS = "Pria";
        if (PDCData.jenisKelamin == "2"){
            jenisKelaminS = "Wanita";
        }
        jenisKelaminTv.setText(jenisKelaminS);
    }

}