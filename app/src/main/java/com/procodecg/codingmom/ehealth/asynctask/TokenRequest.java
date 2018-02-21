package com.procodecg.codingmom.ehealth.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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

public class TokenRequest extends AsyncTask<String, String, String> {
    private URL url;
    private SharedPreferences settings, jwt;

    public AsyncResponse asyncResponse;

    private Context mContext;
    private String response = "";

    public TokenRequest(Context context) {
        this.mContext = context;
        this.asyncResponse = (AsyncResponse) context;
    }

    @Override
    protected String doInBackground(String... args) {
        try{
            settings = mContext.getSharedPreferences("SETTING", MODE_PRIVATE);
            String IP = settings.getString("ADDRESS", "");

            jwt = mContext.getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = jwt.getString("ACCESS_TOKEN", "");

            JSONObject postDataParams = new JSONObject();

            //token baru
            if(token==null || token.isEmpty()){
                url = new URL("https://"+IP+"/oop-copy/index.php");

                postDataParams.put("0", args[0]);
                postDataParams.put("1", args[1]);
                Log.e("params", postDataParams.toString());
            } else { //token expired
                url = new URL("https://"+IP+"/oop-copy/index.php");

                postDataParams.put("0", args[0]);
                postDataParams.put("1", args[1]);
                postDataParams.put("2", token);
                Log.e("params", postDataParams.toString());
            }

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // Ignore SSL Certificate Validation
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
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(postDataParams.toString());
            dos.close();

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
                Log.e("Failed : ", conn.getResponseCode() + conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            response = br.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        asyncResponse.tokenRequest(result);
    }
}
