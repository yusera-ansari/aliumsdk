package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.survey.CustomFreqSurveyData;
import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomFrequencyManager extends SurveyFrequencyManager{
    JSONObject freqObj = new JSONObject();


    CustomFreqSurveyData customFreqSurveyData ;
    private String TAG="CustomFrequencyManager";
    protected CustomFrequencyManager(AliumPreferences aliumPreferences, CustomFreqSurveyData
                                     customFreqSurveyData) {
        super(aliumPreferences);
        this.customFreqSurveyData=customFreqSurveyData;

    }

    @Override
    public void handleFrequency(String survFreq, String key) {
        Log.d("frequency", "custom frequency");

        try {


                handleFrequencyPeriodForMinutes(survFreq, key);


        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
    };

    void handleFrequencyPeriodForMinutes(String survFreq, String key) throws JSONException {
        String[] freqData = customFreqSurveyData.freq.split("-");
        freqObj.put("showFreq", survFreq);
        freqObj.put("freq", customFreqSurveyData.freq);
        freqObj.put("startOn", customFreqSurveyData.startOn);
        freqObj.put("endOn", customFreqSurveyData.endOn);
        freqObj.put("retakeInterval", Integer.parseInt(freqData[0]));
        freqObj.put("period", freqData[1]);
        if(freqData[1].equals("d")){
            Date today=Calendar.getInstance().getTime();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate=format.format(today);
            freqObj.put("lastShownOn",formattedDate);
        }else{
            freqObj.put("lastShownOnMillis", System.currentTimeMillis());
        }

        //key doesn't exists
            Log.d("frequency", "alium preference does not have your key ");

        Log.d("final-frequency", freqObj.toString());
        aliumPreferences.addToAliumPreferences(key, freqObj.toString());
        Log.d(TAG, freqObj.toString());
    }
}
