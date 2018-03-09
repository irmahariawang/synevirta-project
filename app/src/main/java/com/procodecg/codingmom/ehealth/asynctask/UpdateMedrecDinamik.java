package com.procodecg.codingmom.ehealth.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by idedevteam on 2/12/18.
 */

public class UpdateMedrecDinamik extends AsyncTask<String, String, String> {
    public AsyncResponse asyncResponse;

    private Context mContext;
    private String response = "", timestamp = "";
    private SharedPreferences settings;

    public UpdateMedrecDinamik(Context context) {
        this.mContext = context;
        this.asyncResponse = (AsyncResponse) context;
    }

    @Override
    protected String doInBackground(String... array) {
        try{
            settings = mContext.getSharedPreferences("SETTING", MODE_PRIVATE);
            String IP = settings.getString("ADDRESS", "");

            URL url = new URL("https://"+IP+"/service/rest/MedrecDinamik.php");

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(60000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", array[1]);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // SSL setting
            SSLContext context;
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, new TrustManager[]{
                        new javax.net.ssl.X509TrustManager() {

                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        }
                }, null);
                conn.setSSLSocketFactory(context.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(array[0].toString());
            dos.close();

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
                Log.e("Failed : ", conn.getResponseCode() + conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            response = br.readLine();
            timestamp = array[2];

        } catch (SocketTimeoutException e){
            response = "timeout";
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        asyncResponse.taskComplete(result, timestamp);
    }
}
