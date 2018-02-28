package com.procodecg.codingmom.ehealth.rekam_medis;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthContract.RekamMedisEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.RecycleListAdapter;

import java.util.ArrayList;

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
                jwt = getActivity().getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = jwt.getString("ACCESS_TOKEN", "");

                settings = getActivity().getSharedPreferences("SETTING", MODE_PRIVATE);
                String username = settings.getString("USERNAME", "");
                String password = settings.getString("PASSWORD", "");
                String ts = settings.getString("LAST_TIMESTAMP", "");

                if(token.isEmpty()){
                    Toast.makeText(getActivity(), "Request token ...", Toast.LENGTH_SHORT).show();
                    new TokenRequest(getActivity()).execute(username, password);
                } else {
                    try {
                        getDataAndPost();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
    
    //menyiapkan data medrek dinamik dari SQLite untuk dikirim ke SIKDA dalam bentuk JSON Object
    public void getDataAndPost() throws ParseException {
        String timestamp;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //mengambil last timestamp yang dikirim
        settings = getActivity().getSharedPreferences("SETTING", MODE_PRIVATE);
        String ts = settings.getString("LAST_TIMESTAMP", "");

        //mengambil token yang sudah ada pada app
        jwt = getActivity().getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = jwt.getString("ACCESS_TOKEN", "");

        if(ts.isEmpty()){
            timestamp = "0";
        } else {
            timestamp = ts;
        }

        String[] columns = {
                //0-6 json object luar
                RekamMedisEntry.COLUMN_NAMA_DOKTER,
                RekamMedisEntry.COLUMN_TGL_PERIKSA,
                RekamMedisEntry.COLUMN_NIK,
                RekamMedisEntry.COLUMN_ID_PUSKESMAS,
                RekamMedisEntry.COLUMN_POLI,
                RekamMedisEntry.COLUMN_KELUHANUTAMA,
                RekamMedisEntry.COLUMN_RUJUKAN,
                //7-12 json array pelayanan
                RekamMedisEntry.COLUMN_KEPALA,
                RekamMedisEntry.COLUMN_SUHU,
                RekamMedisEntry.COLUMN_NADI,
                RekamMedisEntry.COLUMN_RESPIRASI,
                RekamMedisEntry.COLUMN_BERAT,
                RekamMedisEntry.COLUMN_TINGGI,
                //13-37 json array pelayanan_ket_tambahan
                RekamMedisEntry.COLUMN_SYSTOLE,
                RekamMedisEntry.COLUMN_DIASTOLE,
                RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG,
                RekamMedisEntry.COLUMN_PENYAKIT_DULU,
                RekamMedisEntry.COLUMN_PENYAKIT_KEL,
                RekamMedisEntry.COLUMN_KESADARAN,
                RekamMedisEntry.COLUMN_THORAX,
                RekamMedisEntry.COLUMN_ABDOMEN,
                RekamMedisEntry.COLUMN_GENITALIA,
                RekamMedisEntry.COLUMN_EXTREMITAS,
                RekamMedisEntry.COLUMN_KULIT,
                RekamMedisEntry.COLUMN_NEUROLOGI,
                RekamMedisEntry.COLUMN_LABORATORIUM,
                RekamMedisEntry.COLUMN_RADIOLOGI,
                RekamMedisEntry.COLUMN_STATUS_LABRADIO,
                RekamMedisEntry.COLUMN_DIAGNOSIS_KERJA,
                RekamMedisEntry.COLUMN_DIAGNOSIS_BANDING,
                RekamMedisEntry.COLUMN_RESEP,
                RekamMedisEntry.COLUMN_CATTRESEP,
                RekamMedisEntry.COLUMN_STATUSRESEP,
                RekamMedisEntry.COLUMN_REPETISIRESEP,
                RekamMedisEntry.COLUMN_TINDAKAN,
                RekamMedisEntry.COLUMN_AD_VITAM,
                RekamMedisEntry.COLUMN_AD_FUNCTIONAM,
                RekamMedisEntry.COLUMN_AD_SANATIONAM
        };
        Cursor cursor = db.query(RekamMedisEntry.TABLE_NAME, columns, ""+RekamMedisEntry.COLUMN_TGL_PERIKSA + "> ?", new String[]{timestamp} , null, null, null, "1");
        if(cursor.getCount()==0){
            Toast.makeText(getActivity(), "No Data Update", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();

            DateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            Date in = input.parse(cursor.getString(1));
            String out = output.format(in);

            JSONArray pelayanan_array = new JSONArray();
            JSONArray pelayanan_ket_array = new JSONArray();
            JSONObject data_param = new JSONObject();
            JSONObject pelayanan = new JSONObject();
            JSONObject pelayanan_ket_tambahan = new JSONObject();

            try {
                data_param.put("nama_dokter", cursor.getString(0));
                data_param.put("date", out);
                data_param.put("datetime", cursor.getString(1));
                data_param.put("nik", cursor.getString(2));
                data_param.put("kd_puskesmas", "P3273020203");
                data_param.put("poli", cursor.getString(4));
                data_param.put("anamnesa", cursor.getString(5));
                data_param.put("rujukan", cursor.getString(6));
                data_param.put("username", "admincaringin");

                for(int i=0; i<6; i++){
                    pelayanan.put(""+i, cursor.getString(i+7));
                }
                pelayanan_array.put(pelayanan);
                data_param.put("pelayanan", pelayanan_array);

                for(int i=0; i<25; i++){
                    pelayanan_ket_tambahan.put(""+i, cursor.getString(i+13));
                }
                pelayanan_ket_array.put(pelayanan_ket_tambahan);
                data_param.put("pelayanan_ket_tambahan", pelayanan_ket_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new UpdateMedrecDinamik(getActivity()).execute(data_param.toString(), token, "" + cursor.getString(1));
            Log.i("Array", data_param.toString());
        }
    }
}
