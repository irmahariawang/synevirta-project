package com.procodecg.codingmom.ehealth.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;
import com.procodecg.codingmom.ehealth.pasien.KeluargaActivity;
import com.procodecg.codingmom.ehealth.pasien.PasiendetailActivity;

/**
 * Created by macbookpro on 7/30/17.
 */

public class ProfilpasienFragment extends Fragment {

//    Typeface font;

    public static Fragment newInstance() {
            ProfilpasienFragment fragment = new ProfilpasienFragment();
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.fragment_profilpasien, container, false);
            ((BottombarActivity) getActivity()).setTitleText("Profil Pasien");
            ((BottombarActivity) getActivity()).setSubTitleText();
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("dr X");

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

            //font
//            font = Typeface.createFromAsset(getActivity().getAssets(),"font1.ttf");
//            TextView tv1 = (TextView) view.findViewById(R.id.textNamaPasien);
//            TextView tv2 = (TextView) view.findViewById(R.id.textTglLahir);
//            TextView tv3 = (TextView) view.findViewById(R.id.textUmur);
//            TextView tv4 = (TextView) view.findViewById(R.id.textJenisKlmn);
//            tv1.setTypeface(font);
//            tv2.setTypeface(font);
//            tv3.setTypeface(font);
//            tv4.setTypeface(font);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // TODO data pasien diambil data PDCData
            TextView namaPasienTv = (TextView) getView().findViewById(R.id.textNamaPasien);
            namaPasienTv.setText(PDCData.namaPasien);

            TextView tglLahirTv = (TextView) getView().findViewById(R.id.textTglLahir);
            tglLahirTv.setText(PDCData.tglLahir);

            TextView umurTv = (TextView) getView().findViewById(R.id.textUmur);
            //umurTv.setText();

            TextView jenisKlmnTv = (TextView) getView().findViewById(R.id.textJenisKlmn);
            jenisKlmnTv.setText(PDCData.jenisKelamin);
        }



}