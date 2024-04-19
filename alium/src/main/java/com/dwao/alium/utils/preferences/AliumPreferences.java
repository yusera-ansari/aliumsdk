package com.dwao.alium.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AliumPreferences {
    private static AliumPreferences instance;
    private static final String ALIUM_PREFS = "AliumPrefs";
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public static AliumPreferences getInstance(Context ctx) {
        if (instance == null) {
            synchronized (AliumPreferences.class) {
                instance = new AliumPreferences(ctx);
            }
        }
        return instance;
    }

    private AliumPreferences() {
    }

    private AliumPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(ALIUM_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean checkForUpdate(String key, String freq) {
        if (!sharedPreferences.getString(key, "").isEmpty()) {
            if (!sharedPreferences.getString(key, "").equals(freq)) {
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
