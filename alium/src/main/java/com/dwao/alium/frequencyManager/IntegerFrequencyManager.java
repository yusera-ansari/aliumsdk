package com.dwao.alium.frequencyManager;

import android.util.Log;

import com.dwao.alium.survey.CustomFreqSurveyData;
import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

 class IntegerFrequencyManager extends SurveyFrequencyManager {
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

    @Override
    public boolean shouldSurveyLoad(String key, String srvshowfrq) throws ParseException, JSONException {
            String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
            Log.d("showFreq", "outside frequency comparison---"+ freqDetailString);
            JSONObject freqDetailJsonObject=new JSONObject();
            if(freqDetailString.isEmpty()){
                return true;
            }
            try {
                //check if stored frequency is an object -
                // frq data for integer and custom must be an object
                freqDetailJsonObject = new JSONObject(freqDetailString);
            } catch (JSONException e) {
                Log.e("frequency-Exception", " removing the key: " + freqDetailString + e.toString());
                aliumPreferences.removeFromAliumPreferences(key);
                return true;
            }

        int freq=Integer.parseInt(srvshowfrq);
        Log.d("showFreq", "outside frequency comparison 1" + freq);

        //this only checks if survey has reached its frequency count
        Log.d("showFreq", "outside frequency comparison 2 "+freqDetailJsonObject);
        try{
            if(freqDetailJsonObject.getInt("showFreq")==freq){
                if(freqDetailJsonObject.getInt("counter")==freq){
                    Log.d("showFreq", "compared and equal");
                    return false;
                }
            }
        }catch (Exception e){
            Log.i("frequency","couldn;t convert freq to int "
                    +freqDetailJsonObject+ e.toString());
            Log.i("frequency", "resetting again...");
            aliumPreferences.removeFromAliumPreferences(key);
            return true;

        }

//            }
        Log.d("showFreq", "after frequency comparison");



        return true;
    }
}
