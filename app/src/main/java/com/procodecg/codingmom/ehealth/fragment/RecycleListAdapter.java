package com.procodecg.codingmom.ehealth.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.model.RekamMedisModel;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedlamaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * (c) 2017
 * Created by :
 *      Coding Mom
 */

public class RecycleListAdapter extends RecyclerView.Adapter<RecycleListAdapter.ViewHolder> {

    // Variable
    private ArrayList<String> listTanggal;
    private ArrayList<String> listNamaDokter;
    private Activity activity;
    public static String tanggalPelayanan;

    private int[] ic;

    Typeface font;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView1;
        private TextView mTextView2;
        private ImageView mImage;
        private RelativeLayout rootLayout;

        public ViewHolder(View v) {
            super(v);
            mTextView1  =(TextView)v.findViewById(R.id.txt_tanggal);
            mTextView2  =(TextView)v.findViewById(R.id.txt_namaDokter);
            mImage      =(ImageView)v.findViewById(R.id.img_card);
            rootLayout  =(RelativeLayout)v.findViewById(R.id.rootLayout);
        }
    }

    public RecycleListAdapter(Activity activity, ArrayList<String> listTanggal, ArrayList<String> listNamaDokter, int[] icons) {
        this.listTanggal = listTanggal;
        this.listNamaDokter = listNamaDokter;
        this.activity = activity;
        this.ic = icons;
    }

    @Override
    public int getItemCount() {
        return (null != listNamaDokter ? listNamaDokter.size():null);
    }

    @Override
    public RecycleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycle_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //font
        font = Typeface.createFromAsset(activity.getAssets(),"font1.ttf");
        holder.mTextView1.setTypeface(font);

        holder.mTextView1.setText(listTanggal.get(position));
        holder.mTextView2.setText(listNamaDokter.get(position));

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tanggalPelayanan = holder.mTextView1.getText().toString();
                Toast.makeText(activity, tanggalPelayanan, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, RekmedlamaActivity.class);
                activity.startActivity(intent);
            }

        });
    }

    public static String getTanggalPelayanan(){
        return tanggalPelayanan;
    }
}
