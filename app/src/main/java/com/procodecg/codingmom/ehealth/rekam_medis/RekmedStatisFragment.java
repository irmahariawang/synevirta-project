package com.procodecg.codingmom.ehealth.rekam_medis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.procodecg.codingmom.ehealth.R;

/**
 * Created by macbookpro on 8/29/17.
 */

public class RekmedStatisFragment extends Fragment {
    public static RekmedStatisFragment newInstance() {
        RekmedStatisFragment fragment = new RekmedStatisFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //aktifkan jika TIDAK menggunakan TabLayout Dinamis-Statis
//        ((BottombarActivity) getActivity()).setTitleText("Rekam Medis Statis");
//        ((BottombarActivity) getActivity()).setSubTitleText();

        return inflater.inflate(R.layout.fragment_rekmedstatis, container, false);
    }
}