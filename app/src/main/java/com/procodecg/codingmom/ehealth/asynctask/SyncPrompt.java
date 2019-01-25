package com.procodecg.codingmom.ehealth.asynctask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.HPCData;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * (c) 2018
 * Created by :
 *      Annisa Alifiani
 */

public class SyncPrompt extends Activity implements AsyncResponse{

    //Class
    EhealthDbHelper mDbHelper;

    //Variable
    Boolean hostStatus;
    String username, password, timestampLama;
    ArrayList<String> text = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayList<String> lastDataNIK, lastDataTimestamp, dataNIK, dataTimestamp;

    //View
    ListView listSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_prompt);

        //Ambil username dan password untuk token
        SharedPreferences settings = getSharedPreferences("SETTING", MODE_PRIVATE);
        username = settings.getString("USERNAME", "");
        password = settings.getString("PASSWORD", "");

        listSync = (ListView) findViewById(R.id.listSync);
        text.add("Persiapan sinkronisasi ke SIKDA ...");
        adapter = new ArrayAdapter<String>(this, R.layout.list_sync_prompt, R.id.text, text);
        listSync.setAdapter(adapter);

        mDbHelper = new EhealthDbHelper(this);
        mDbHelper.openDB();

        //Pengecekan koneksi internet pada device
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            text.add("Connection status : Connect");

            //Pengecekan host SIKDA
            new HostChecking(this).execute();
        } else {
            text.add("Connection status : Disconnect");
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    //Response asynctask setelah mengirim data yang disinkronisasi ke SIKDA
    @Override
    public void taskComplete(String output, String timestamp, String nik) {
        //Parse respon
        JSONObject obj;
        String code = "", status = "";
        try {
            obj = new JSONObject(output);
            code = obj.getString("code");
            status = obj.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(code.equals("203")){
            text.add("Request toke baru ...");
            new TokenRequest(this).execute(username, password);
        } else if(code.equals("216") || code.equals("207")){
            text.add(status);
            try {
                //Menyimpan timestamp yang terakhir disinkronisasi ke SIKDA
                //Timestamp disimpan berdasarkan NIK
                text.add("NIK "+ dataNIK.contains(nik));
                SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
                int j = sync.getInt("size", -1);
                if(j == -1){
                    j = 0;
                }
                SharedPreferences.Editor editor = sync.edit();

                if(!dataNIK.contains(nik)){
                    editor.putString(""+ j, nik);
                    text.add("Tambah NIK "+nik+" ke NIKArray");
                    j+=1;
                    editor.putInt("size", j);
                    text.add("Jumlah NIKArray "+ j);
                }

                String lastTimestamp = sync.getString(nik, "");
                text.add("Timestamp NIK "+lastTimestamp);
                if(lastTimestamp.isEmpty() || lastTimestamp == null){
                    timestampLama = "0000-00-00 00:00:00";
                } else {
                    timestampLama = lastTimestamp;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timeLama = sdf.parse(timestampLama);
                Date timeBaru = sdf.parse(timestamp);

                if(timeBaru.after(timeLama)) {
                    editor.putString(nik, timestamp);
                    timestampLama = timestamp;
                }
                editor.commit();

                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                db.delete(EhealthContract.RekamMedisEntry.TABLE_NAME, EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA+"=?", new String[]{timestamp});

                getDataAndPost();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if(code.equals("206")){
            text.add("Sinkronisasi gagal : " +status);
        }
        adapter.notifyDataSetChanged();
    }

    //Response asynctask setelah mengirim request token
    @Override
    public void tokenRequest(String output) {
        //Parse respon
        JSONObject obj;
        String code = "", status = "";
        try {
            obj = new JSONObject(output);
            code = obj.getString("code");
            status = obj.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(code.equals("111")){
            //Menyimpan token pada device
            text.add("Token baru diterima ...");
            text.add("Token baru : " + status);
            SharedPreferences sp = getSharedPreferences("TOKEN", MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("ACCESS_TOKEN", status);
            editor.commit();

            text.add("Menyimpan token ...");
            try {
                getDataAndPost();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if(code.equals("101")){
            text.add("Autentikasi untuk token gagal");
            text.add("Coba cek username dan password kembali");
        } else if(code.equals("103")){
            text.add("Token check : " + status);
            text.add("Request token baru ...");
            new TokenRequest(this).execute(username, password);
        } else if(code.equals("113")){
            text.add("Sinkronisasi ke SIKDA siap");
            try {
                getDataAndPost();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();
    }

    //Response asyctask setelah melakukan pengecekan host SIKDA
    @Override
    public void hostDetected(Boolean output) {
        hostStatus = output;
        Log.i("HOST ", String.valueOf(hostStatus));

        if(hostStatus) {
            text.add("Host checking : Reachable");

            SharedPreferences tokens = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = tokens.getString("ACCESS_TOKEN", "");

            if(token.isEmpty()){
                new TokenRequest(this).execute(username, password);
                text.add("Request token ...");
            } else {
                new TokenRequest(this).execute(token);
                text.add("Access token : " + token);
            }
        } else {
            text.add("Host checking : Unreachable");
        }
        adapter.notifyDataSetChanged();
    }

    //Menyiapkan data medrek dinamik dari SQLite untuk dikirim ke SIKDA
    //Data yang dikirim ke SIKDA disiapkan dalam bentuk JSON Object
    public void getDataAndPost() throws ParseException {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Mengambil token yang ada pada device
        SharedPreferences jwt = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = jwt.getString("ACCESS_TOKEN", "");

        String[] columns = {
                //0-6 json object luar
                EhealthContract.RekamMedisEntry.COLUMN_NAMA_DOKTER,
                EhealthContract.RekamMedisEntry.COLUMN_TGL_PERIKSA,
                EhealthContract.RekamMedisEntry.COLUMN_NIK,
                EhealthContract.RekamMedisEntry.COLUMN_ID_PUSKESMAS,
                EhealthContract.RekamMedisEntry.COLUMN_POLI,
                EhealthContract.RekamMedisEntry.COLUMN_KELUHANUTAMA,
                EhealthContract.RekamMedisEntry.COLUMN_RUJUKAN,
                //7-12 json array pelayanan
                EhealthContract.RekamMedisEntry.COLUMN_KEPALA,
                EhealthContract.RekamMedisEntry.COLUMN_SUHU,
                EhealthContract.RekamMedisEntry.COLUMN_NADI,
                EhealthContract.RekamMedisEntry.COLUMN_RESPIRASI,
                EhealthContract.RekamMedisEntry.COLUMN_BERAT,
                EhealthContract.RekamMedisEntry.COLUMN_TINGGI,
                //13-37 json array pelayanan keterangan tambahan
                EhealthContract.RekamMedisEntry.COLUMN_SYSTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_DIASTOLE,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_SEKARANG,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_DULU,
                EhealthContract.RekamMedisEntry.COLUMN_PENYAKIT_KEL,
                EhealthContract.RekamMedisEntry.COLUMN_KESADARAN,
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

        Cursor cursor = db.query(EhealthContract.RekamMedisEntry.TABLE_NAME, columns, null, null , null, null, null, "1");

        if(cursor.getCount()==0){
            text.add("Sinkronisasi dengan SIKDA selesai");
        } else {
            dataNIK = new ArrayList<String>();
            dataTimestamp = new ArrayList<String>();

            SharedPreferences sync = getSharedPreferences("SYNC", MODE_PRIVATE);
            int jumlah = sync.getInt("size", -1);
            if(jumlah == -1){
                jumlah = 0;
            }
            text.add("JML_TS "+ String.valueOf(jumlah));
            int x = 0;
            do {
                String nik = sync.getString(String.valueOf(x), "");
                if(!nik.isEmpty()) {
                    String ts = sync.getString(nik, "");
                    dataNIK.add(nik);
                    dataTimestamp.add(ts);
                }
                x++;
            } while(x < jumlah);

            //Untuk debug timestamp dan NIK yang dikirim terakhir
            text.add("NIK_ARRAYS "+ dataNIK.toString());
            text.add("TS_ARRAYS "+ dataTimestamp.toString());

            //Mengambil data dari SQLite
            cursor.moveToFirst();
            if(dataNIK.contains(cursor.getString(2))){
                int index = dataNIK.indexOf(cursor.getString(2));

                //Ambil timestamp terakhir
                String lastTS = dataTimestamp.get(index);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timeLama = sdf.parse(lastTS);
                Date timeBaru = sdf.parse(cursor.getString(1));

                //Pengecekan timestamp data yang akan dikirim
                if(timeBaru.after(timeLama)){
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
                        data_param.put("kd_puskesmas", cursor.getString(3));
                        data_param.put("poli", cursor.getString(4));
                        data_param.put("anamnesa", cursor.getString(5));
                        data_param.put("rujukan", cursor.getString(6));
                        data_param.put("username", "admincaringin");

                        for(int i=0; i<6; i++){
                            pelayanan.put(""+i, cursor.getString(i+7));
                        }
                        pelayanan_array.put(pelayanan);
                        data_param.put("pelayanan", pelayanan_array);

                        for(int i=0; i<26; i++){
                            pelayanan_ket_tambahan.put(""+i, cursor.getString(i+13));
                        }
                        pelayanan_ket_array.put(pelayanan_ket_tambahan);
                        data_param.put("pelayanan_ket_tambahan", pelayanan_ket_array);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new UpdateMedrecDinamik(this).execute(data_param.toString(), token, "" + cursor.getString(1), cursor.getString(2));
                    Log.i("Array", data_param.toString());
                    text.add("Kirim data");
                } else {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("code", "207");
                        jo.put("status", "Data sudah pernah dikirim");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    taskComplete(jo.toString(), cursor.getString(1), cursor.getString(2));
                }
            } else {
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
                    data_param.put("kd_puskesmas", cursor.getString(3));
                    data_param.put("poli", cursor.getString(4));
                    data_param.put("anamnesa", cursor.getString(5));
                    data_param.put("rujukan", cursor.getString(6));
                    data_param.put("username", "admincaringin");

                    for(int i=0; i<6; i++){
                        pelayanan.put(""+i, cursor.getString(i+7));
                    }
                    pelayanan_array.put(pelayanan);
                    data_param.put("pelayanan", pelayanan_array);

                    for(int i=0; i<26; i++){
                        pelayanan_ket_tambahan.put(""+i, cursor.getString(i+13));
                    }
                    pelayanan_ket_array.put(pelayanan_ket_tambahan);
                    data_param.put("pelayanan_ket_tambahan", pelayanan_ket_array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UpdateMedrecDinamik(this).execute(data_param.toString(), token, "" + cursor.getString(1), cursor.getString(2));
                Log.i("Array", data_param.toString());
                text.add("Kirim data");
            }
        }
        adapter.notifyDataSetChanged();
    }
}
