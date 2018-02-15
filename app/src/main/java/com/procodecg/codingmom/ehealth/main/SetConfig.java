package com.procodecg.codingmom.ehealth.main;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Hp on 10/14/2017.
 */

public class SetConfig {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context _context;
    private static final String PREF_NAME = "SetConfig";

    //mendefinisikan KEY
    public static final String KEY_IDPUSKES ="idpuskes";
    public static final String KEY_NAMAPUSKES ="namapuskesmas";


    public SetConfig(Context context){
    this._context = context;
        int PRIVATE_MODE = 0;
        preferences = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);

        editor=preferences.edit();
    }

public void createSetConfig(String idpuskes, String namapuskesmas){
    editor.putString(KEY_IDPUSKES, idpuskes);
    editor.putString(KEY_NAMAPUSKES, namapuskesmas);
//    editor.putString(KEY_TIMEOUT, timeout);
    editor.commit();
}

public HashMap<String, String> getDetail(){
    HashMap<String, String> user = new HashMap<String, String>();
    user.put(KEY_IDPUSKES, preferences.getString(KEY_IDPUSKES,null));
    user.put(KEY_NAMAPUSKES, preferences.getString(KEY_NAMAPUSKES,null));
//    user.put(KEY_TIMEOUT, preferences.getString(KEY_TIMEOUT,null));

    return user;

    }
}
