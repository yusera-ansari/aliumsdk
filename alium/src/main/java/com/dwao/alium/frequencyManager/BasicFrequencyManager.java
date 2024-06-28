package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

 class BasicFrequencyManager extends SurveyFrequencyManager {
    public BasicFrequencyManager(AliumPreferences aliumPreferences
    , String srvKey, String srvShowFreq
    ) {
        super(aliumPreferences, srvKey, srvShowFreq);
    }

    @Override
    public void handleFrequency( ) {
        if(this.srvShowFreq.equals("overandover")){
            Log.i("srvshowfrq", "show survey frequency: overandover");
            return;
        }
        Log.i("srvshowfrq", "show survey frequency: "
        +this.srvShowFreq);
        aliumPreferences.addToAliumPreferences( this.surveyKey,
                this.srvShowFreq);
    }
    private  boolean checkForBasicFrequencyUpdate() {
        if (!aliumPreferences.getFromAliumPreferences(this.surveyKey).isEmpty()) {
            Log.i("srvshowfrq-changed", aliumPreferences.getFromAliumPreferences(this.surveyKey)+" "+this.surveyKey+
                    this.srvShowFreq);
            if (!aliumPreferences.getFromAliumPreferences(this.surveyKey).equals(this.srvShowFreq)) {
                Log.i("srvshowfrq-changed", "updating stored preferences data"+this.surveyKey+ this.srvShowFreq);
                aliumPreferences.removeFromAliumPreferences(this.surveyKey);
                return true;

            } else {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean shouldSurveyLoad() throws ParseException, JSONException {
        String freqDetailString=aliumPreferences.getFromAliumPreferences(this.surveyKey);
        Log.d("showFreq", "outside frequency comparison---"+ freqDetailString);
        if (!freqDetailString.isEmpty() &&!checkForBasicFrequencyUpdate()) {
            Log.e("frequency-Exception", "--removing the key: " + freqDetailString);
            return false;
        }
        return true;
    }
}
