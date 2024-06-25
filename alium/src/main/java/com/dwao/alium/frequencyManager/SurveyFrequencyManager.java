package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.survey.CustomFreqSurveyData;
import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class SurveyFrequencyManager {
    protected AliumPreferences aliumPreferences;


    private SurveyFrequencyManager() {}

    protected SurveyFrequencyManager(AliumPreferences aliumPreferences) {
        this.aliumPreferences=aliumPreferences;
        
    }


    public abstract void handleFrequency(String survFreq, String survKey);

    public void recordSurveyTriggerOnPreferences(String surveyKey, String survFreq){
        handleFrequency(survFreq, surveyKey);
    }

    public abstract boolean shouldSurveyLoad(String key, String srvshowfrq)
            throws ParseException, JSONException;


}
