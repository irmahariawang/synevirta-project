package com.procodecg.codingmom.ehealth.rekam_medis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.asynctask.HostChecking;
import com.procodecg.codingmom.ehealth.asynctask.TokenRequest;
import com.procodecg.codingmom.ehealth.asynctask.UpdateMedrecDinamik;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.fragment.RecycleListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA;


/**
 * Created by macbookpro on 8/29/17.
 */

public class RekmedDinamisFragment extends Fragment {
    //private EhealthDbHelper mDbHelper;

    public static RekmedDinamisFragment newInstance() {
        RekmedDinamisFragment fragment = new RekmedDinamisFragment();
        return fragment;
    }

    private RecycleListAdapter rAdapter;
    
    private SharedPreferences jwt, settings;

    private ArrayList<String> listTanggal;
    private ArrayList<String> listNamaDokter;
    private int currentID;
    //private ArrayList<String> listIDPuskesmas;
    EhealthDbHelper dbHelper;
    boolean Tableexist;


    public static int icons[] = {
            R.drawable.folder4,
            R.drawable.folder4,
//            R.drawable.folder3,
//            R.drawable.tips4,
//            R.drawable.tips5,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_rekmeddinamis, container, false);
        //Toast.makeText(getActivity(), "Table exist", Toast.LENGTH_SHORT).show();

        dbHelper = new EhealthDbHelper(getActivity());
        dbHelper.openDB();
        Tableexist = dbHelper.isTableExists(RekamMedisEntry.TABLE_NAME, true);

        if (Tableexist == true) {
            //Toast.makeText(getActivity(), "Table exist", Toast.LENGTH_SHORT).show();
            loadTable(view);

        } else {
            //Toast.makeText(getActivity(), "Table not exist", Toast.LENGTH_SHORT).show();
        }

            //Floating Action Button
        FloatingActionButton fabRekmedBaru = (FloatingActionButton) view.findViewById(R.id.fabRekmedBaru);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent activity = new Intent(getActivity(), RekmedbaruActivity.class);
                startActivity(activity);


            }
        };
        fabRekmedBaru.setOnClickListener(listener);
        
        FloatingActionButton fabSinkronSikda = (FloatingActionButton) view.findViewById(R.id.fabSinkronSikda);
        fabSinkronSikda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
//                    ((BottombarActivity)getActivity()).changeTextStatus(true);
                    new HostChecking(getActivity()).execute();

                    settings = getActivity().getSharedPreferences("HOST", MODE_PRIVATE);
                    Boolean hostDetected = settings.getBoolean("DETECTED", true);

                    settings = getActivity().getSharedPreferences("SETTING", MODE_PRIVATE);
                    String username = settings.getString("USERNAME", "");
                    String password = settings.getString("PASSWORD", "");
                    String timestamp = settings.getString("LAST_TIMESTAMP", "");

                    if(hostDetected){
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, null, ""+ EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA + "> ?", new String[]{timestamp} , null, null, null, "1");
                        if(cursor.getCount()==0){
                            Toast.makeText(getActivity(), "Data tidak ditemukan", Toast.LENGTH_LONG).show();
                        } else {
                            jwt = getActivity().getSharedPreferences("TOKEN", MODE_PRIVATE);
                            String token = jwt.getString("ACCESS_TOKEN", "");

                            if (token.isEmpty()) {
                                Toast.makeText(getActivity(), "Request token ...", Toast.LENGTH_SHORT).show();
                                new TokenRequest(getActivity()).execute(username, password);
                            } else {
                                try {
                                    ((BottombarActivity) getActivity()).getDataAndPost();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Host not detected", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ((BottombarActivity)getActivity()).changeTextStatus(false);
                }
            }
        });

        return view;
    }

    private void loadTable(View view) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        RecyclerView rView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        rView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(llm);

        String[] projection = {
                RekamMedisEntry._ID,
                RekamMedisEntry.COLUMN_NAMA_DOKTER,
                COLUMN_TGL_PERIKSA,
                //RekamMedisEntry.COLUMN_ID_PUSKESMAS
        };
        Cursor cursor = db.query(RekamMedisEntry.TABLE_NAME, projection, null, null, null, null, EhealthContract.RekamMedisEntry._ID+" DESC");

//        Cursor cursor = db.query(RekamMedisEntry.TABLE_NAME, projection, null, null, null, null, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // table in the database).
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(RekamMedisEntry._ID);
            int namaDokterIndex = cursor.getColumnIndex(RekamMedisEntry.COLUMN_NAMA_DOKTER);
            int tanggalPeriksaIndex = cursor.getColumnIndex(COLUMN_TGL_PERIKSA);
            //int IDPuskesmasIndex = cursor.getColumnIndex(RekamMedisEntry.COLUMN_ID_PUSKESMAS);
            listNamaDokter = new ArrayList<>();
            //listIDPuskesmas = new ArrayList<>();
            listTanggal = new ArrayList<>();

            // Iterate through all the returned rows in the cursor
            if (cursor.moveToFirst()) {
                do {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    currentID = cursor.getInt(idColumnIndex);
                    String currentNamaDokter = cursor.getString(namaDokterIndex);
                    String currentTanggalPeriksa = cursor.getString(tanggalPeriksaIndex);
                    //String currentIDPuskesmas = cursor.getString(IDPuskesmasIndex);
                    //Toast.makeText(getActivity(), currentID, Toast.LENGTH_SHORT).show();

                    //list tanggal folder dan nama dokter pemeriksanya

                    listTanggal.add(currentTanggalPeriksa);
                    //listTanggal.add("6-02-2017");

                    listNamaDokter.add(currentNamaDokter);
                    //listNamaDokter.add("dr Susan");

                    //listIDPuskesmas.add(currentIDPuskesmas);

                    rAdapter = new RecycleListAdapter(getActivity(), listTanggal, listNamaDokter, icons);
                    rView.setAdapter(rAdapter);

                } while (cursor.moveToNext());

            }
        }finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }


        //list tanggal folder dan nama dokter pemeriksanya

            /*
            listTanggal = new ArrayList<>();
            listTanggal.add("25-08-2017");
            listTanggal.add("6-02-2017");

            listNamaDokter = new ArrayList<>();
            listNamaDokter.add("dr Adrian");
            listNamaDokter.add("dr Susan");
*/
        rAdapter=new RecycleListAdapter(getActivity(), listTanggal, listNamaDokter, icons);
        rView.setAdapter(rAdapter);
        dbHelper.closeDB();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Tableexist == true) {
            //Toast.makeText(getActivity(), "Table exist", Toast.LENGTH_SHORT).show();
            rAdapter.notifyDataSetChanged();
            loadTable(getView());

        } else {
            //Toast.makeText(getActivity(), "Table not exist", Toast.LENGTH_SHORT).show();
        }
    }
}
