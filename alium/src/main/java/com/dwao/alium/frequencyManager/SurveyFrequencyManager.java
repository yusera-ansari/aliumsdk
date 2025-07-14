package com.dwao.alium.frequencyManager;

import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;

import java.text.ParseException;

public abstract class SurveyFrequencyManager {
    protected AliumPreferences aliumPreferences;

    protected String surveyKey, srvShowFreq;
    private SurveyFrequencyManager() {}

    protected SurveyFrequencyManager(AliumPreferences aliumPreferences,
                                     String srvKey, String srvShowFreq) {
        this.aliumPreferences=aliumPreferences;
        this.surveyKey=srvKey;
        this.srvShowFreq=srvShowFreq;
    }


    public abstract void handleFrequency();

    public void recordSurveyTriggerOnPreferences(){
        handleFrequency();
    }

    public abstract boolean shouldSurveyLoad()
            throws ParseException, JSONException;


}
