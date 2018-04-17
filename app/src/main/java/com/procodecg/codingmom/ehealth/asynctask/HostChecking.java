package com.procodecg.codingmom.ehealth.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
 * Created by idedevteam on 3/5/18.
 */

public class HostChecking extends AsyncTask<Void, Void, Boolean> {

    private SharedPreferences settings;
    public AsyncResponse asyncResponse;

    private Context mContext;

    public HostChecking(Context context) {
        this.mContext = context;
        this.asyncResponse = (AsyncResponse) context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isReachable = false;
        try{
            settings = mContext.getSharedPreferences("SETTING", MODE_PRIVATE);
            String IP = settings.getString("ADDRESS", "");

            URL url = new URL("https://"+IP+"/");
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(20000);
            connection.setHostnameVerifier(new HostnameVerifier() {
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
                connection.setSSLSocketFactory(context.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connection.connect();
            isReachable = (connection.getResponseCode() == 200);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            isReachable = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isReachable;
    }

    @Override
    protected void onPostExecute(Boolean isReachable) {
        Log.i("Connected", String.valueOf(isReachable));
        asyncResponse.hostDetected(isReachable);
    }
}
