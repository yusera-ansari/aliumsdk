package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.utils.preferences.AliumPreferences;

public class BasicFrequencyManager extends SurveyFrequencyManager {
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
}
