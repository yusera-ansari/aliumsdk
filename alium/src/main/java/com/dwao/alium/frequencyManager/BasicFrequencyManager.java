package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

 class BasicFrequencyManager extends SurveyFrequencyManager {
    public BasicFrequencyManager(AliumPreferences aliumPreferences) {
        super(aliumPreferences);
    }

    @Override
    public void handleFrequency(String survFreq, String survKey) {
        if(survFreq.equals("overandover")){
            Log.i("srvshowfrq", "show survey frequency: overandover");
            return;
        }
        Log.i("srvshowfrq", "show survey frequency: "
        +survFreq);
        aliumPreferences.addToAliumPreferences( survKey,
                survFreq);
    }
    private  boolean checkForBasicFrequencyUpdate(String key, String freq) {
        if (!aliumPreferences.getFromAliumPreferences(key).isEmpty()) {
            Log.i("srvshowfrq-changed", aliumPreferences.getFromAliumPreferences(key)+" "+key+ freq);
            if (!aliumPreferences.getFromAliumPreferences(key).equals(freq)) {
                Log.i("srvshowfrq-changed", "updating stored preferences data"+key+ freq);
                aliumPreferences.removeFromAliumPreferences(key);
                return true;

            } else {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean shouldSurveyLoad(String key, String srvshowfrq) throws ParseException, JSONException {
        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
        Log.d("showFreq", "outside frequency comparison---"+ freqDetailString);
        if (!freqDetailString.isEmpty() &&!checkForBasicFrequencyUpdate(key, srvshowfrq)) {
            Log.e("frequency-Exception", "--removing the key: " + freqDetailString);
            return false;
        }
        return true;
    }
}
