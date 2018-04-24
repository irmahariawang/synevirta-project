package com.procodecg.codingmom.ehealth.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;

import java.util.ArrayList;


public class PencarianFragment extends Fragment {

    public static PencarianFragment newInstance() {
        PencarianFragment fragment = new PencarianFragment();
        return fragment;
    }

    private RecycleListAdapter rAdapter;

    private ArrayList<String> listTanggal;
    private ArrayList<String> listNamaDokter;
    //private ArrayList<String> listIDPuskesmas;
    private ArrayList<Pencarian> listPencarian;
    EhealthDbHelper dbHelper;
    boolean Tableexist;
    private SearchView sv;

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

        ((BottombarActivity) getActivity()).setTitleText("Pencarian");
        ((BottombarActivity) getActivity()).setSubTitleText();

//        return inflater.inflate(R.layout.fragment_pencarian, container, false);

        final View view = inflater.inflate(R.layout.fragment_pencarian, container, false);
//        //Toast.makeText(getActivity(), "Table exist", Toast.LENGTH_SHORT).show();
//

        dbHelper = new EhealthDbHelper(getActivity());

        final SearchView sView= (SearchView) view.findViewById(R.id.inputPencarian);
        sView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sView.setIconified(false);
            }
        });
        int searchCloseButtonId = sView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        ImageView closeButton = (ImageView) sView.findViewById(searchCloseButtonId);

//        closeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Clear", Toast.LENGTH_SHORT);

//                Intent pencarian = new Intent (getActivity(),PencarianFragment.class);
//                startActivity(pencarian);

//            }
//        });

        //sView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String searchInput){
                searchInput = searchInput.toLowerCase();
                /*ArrayList<String> newList = new ArrayList<>();
                for (String rekmedfilter : listNamaDokter) {
//                        String name = beneficiary.getName().toLowerCase();
                    String name = rekmedfilter.toLowerCase();

                            if (name.contains(newText)){
                        newList.add(rekmedfilter);
                    }
                }*/

                if (searchInput.length() > 2) {

                    if(dbHelper.checkDbOpen()){
                        dbHelper.closeDB();
                        dbHelper.openDB();
                    }else{
                        dbHelper.openDB();
                    }

                    Tableexist = dbHelper.isTableExists(EhealthContract.RekamMedisEntry.TABLE_NAME, true);
                    if (Tableexist) {
                        //Toast.makeText(getActivity(), "Table exist", Toast.LENGTH_SHORT).show();
                        loadTable(view, searchInput);

                    }
                    //Toast.makeText(getActivity(), newText, Toast.LENGTH_SHORT).show();
                } else
                {
                    clearTable(view);
                    Toast.makeText(getActivity(),"clear",Toast.LENGTH_SHORT).show();

                }
//                getRekmedFilter(newText);
                return false;
            }
        });

        return view;
    }

    private void getRekmedFilter(String searchTerm){

    }

    private void clearTable(View view){
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.listPencarian);
        rView.removeAllViewsInLayout();

    }

    private void loadTable(View view, String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        RecyclerView rView = (RecyclerView) view.findViewById(R.id.listPencarian);
        rView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(llm);

        String[] projection = {
                EhealthContract.RekamMedisEntry._ID,
                EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER,
                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA,
                //RekamMedisEntry.COLUMN_ID_PUSKESMAS
        };

        //Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, projection, null, null, null, null, null);
        Cursor cursor = dbHelper.retrieve(query);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // table in the database).
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry._ID);
            int namaDokterIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER);
            int tanggalPeriksaIndex = cursor.getColumnIndex(EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA);
            //int IDPuskesmasIndex = cursor.getColumnIndex(RekamMedisEntry.COLUMN_ID_PUSKESMAS);
            listNamaDokter = new ArrayList<>();
            //listIDPuskesmas = new ArrayList<>();
            listTanggal = new ArrayList<>();

            // Iterate through all the returned rows in the cursor
            if (cursor.moveToFirst()) {
                do {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
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
        //rAdapter=new RecycleListAdapter(getActivity(), listTanggal, listNamaDokter, icons, currentID);
        //rView.setAdapter(rAdapter);
        //dbHelper.closeDB();
    }




}