package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONObject;

public class IntegerFrequencyManager extends SurveyFrequencyManager {
    private String TAG="IntegerFrequencyManager";
    public IntegerFrequencyManager(AliumPreferences aliumPreferences) {
        super(aliumPreferences);
    }

    @Override
    public void handleFrequency(String survFreq, String key) {
        try{
            int freq=Integer.parseInt(survFreq);
            JSONObject freqObj=new JSONObject();
            Log.d("showFreq", " "+freq);
            freqObj.put("showFreq",freq);
            freqObj.put("counter", 0);
            Log.i("frequency-simple",aliumPreferences.getFromAliumPreferences(key));
            if (!aliumPreferences.getFromAliumPreferences(key).isEmpty()) {
                JSONObject storedFreq=
                        new JSONObject(aliumPreferences.getFromAliumPreferences(key))
                        ;

                if(storedFreq.getInt("showFreq")!=freq){
                    freqObj.put("counter", 1);
                }else if(storedFreq.getInt("counter")!=freq){
                    freqObj.put("counter", storedFreq.getInt("counter")+1);
                }else{
                    return;
                }
            } else {
                freqObj.put("counter",1);
            }
            aliumPreferences.addToAliumPreferences(key, freqObj.toString());
            Log.i("showFreq-changed", ""+aliumPreferences.getFromAliumPreferences(key)
                    +" "+freqObj);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
}
