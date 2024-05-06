package com.dwao.alium.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AliumPreferences {
    private static AliumPreferences instance;
    private static final String ALIUM_PREFS = "AliumPrefs";
    private SharedPreferences.Editor editor;
    private SharedPreferences aliumSharedPreferences;

    public SharedPreferences getAliumSharedPreferences() {
        return aliumSharedPreferences;
    }
    public void setCustomerId(String customerId){
      editor.putString("customerId", customerId);
      editor.apply();
      Log.i("customerId", "Customer Id generated "+
              aliumSharedPreferences.getString("customerId",""));
    }
    public String getCustomerId(){
        return aliumSharedPreferences.getString("customerId", "");
    }

    public static AliumPreferences getInstance(Context ctx) {
        if (instance == null) {
            synchronized (AliumPreferences.class) {
                instance = new AliumPreferences(ctx);
            }
        }
        return instance;
    }

    private AliumPreferences() {}

    private AliumPreferences(Context context) {
        aliumSharedPreferences = context.getSharedPreferences(ALIUM_PREFS, Context.MODE_PRIVATE);
        editor = aliumSharedPreferences.edit();
    }

    public boolean checkForUpdate(String key, String freq) {
        if (!aliumSharedPreferences.getString(key, "").isEmpty()) {
            if (!aliumSharedPreferences.getString(key, "").equals(freq)) {
                Log.i("srvshowfrq-changed", "updating stored preferences data");
                editor.remove(key);
                editor.apply();
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
    public void addToAliumPreferences(String key, String value){
        editor.putString(key,value);
        editor.apply();

    }

}
