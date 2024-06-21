package com.dwao.alium.frequencyManager;

import android.util.Log;

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
    public static SurveyFrequencyManager getFrequencyManager(AliumPreferences aliumPreferences,
                                                      String frq){
     if(frq.matches("\\d+")){
         return new IntegerFrequencyManager(aliumPreferences);
     }
     else if(frq.matches("\\d+-[dwm]")){
         return new PeriodicFrequencyManager(aliumPreferences);
     }
      else  if(frq.matches("overandover")||frq.matches("onlyonce")||
        frq.matches("untilresponse")){
            return new BasicFrequencyManager(aliumPreferences);
        }
     else{
         throw new InvalidFrequencyException("The survey show frequency type: \""+frq+ "\" doesn't exist");

     }
    }

    public abstract void handleFrequency(String survFreq, String survKey);

    protected  boolean checkForBasicFrequencyUpdate(String key, String freq) {
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


    public void recordSurveyTriggerOnPreferences(String surveyKey, String survFreq){
        handleFrequency(survFreq, surveyKey);
    }


    public boolean shouldSurveyLoad(String key, String srvshowfrq) throws ParseException, JSONException {
        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
        Log.d("showFreq", "outside frequency comparison"+ freqDetailString);
        JSONObject freqDetailJsonObject=new JSONObject();
        if(!freqDetailString.isEmpty()&&((srvshowfrq.matches("\\d+")||
                srvshowfrq.matches("\\d+-[dwm]"))
        )) {
            try {
                freqDetailJsonObject = new JSONObject(freqDetailString);
                if( srvshowfrq.matches("\\d+-[dwm]")){
                    Date today=Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date lastShownOn=simpleDateFormat.parse(freqDetailJsonObject.getString("lastShownOn"));

                    if(today.before(lastShownOn)){
                        Log.i("frequency", "todays before lastshown "+ today+" "+lastShownOn);
                        aliumPreferences.removeFromAliumPreferences(key);
                        return true;
                    }
                }
            } catch (JSONException e) {
                Log.e("frequency-Exception", " removing the key: " + freqDetailString + e.toString());
                aliumPreferences.removeFromAliumPreferences(key);
                return true;
            }
        }else if (!checkForBasicFrequencyUpdate(key, srvshowfrq)) {
            return false;
        }


        if(srvshowfrq.matches("\\d+")) {
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

        }
        else if(srvshowfrq.matches("\\d+-[dwm]")) {
            //for periodic freq
            String[] periodicFreq=srvshowfrq.split("-");
            int freqCount=Integer.parseInt(periodicFreq[0]);

            if(freqDetailJsonObject.has("showFreq")){
                if(freqDetailJsonObject.getString("showFreq").equals(srvshowfrq)){

                    if(freqDetailJsonObject.has("counter")){
                        if( freqCount==freqDetailJsonObject.getInt("counter")){
                            Date today= Calendar.getInstance().getTime();
                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                            String date=format.format(today);
                            if(freqDetailJsonObject.getString("lastShownOn").equals(date)){
                                return false;
                            }
                            if(periodicFreq[1].equals("m")||periodicFreq[1].equals("w")){
                                if(
                                        today.compareTo(format.parse(freqDetailJsonObject.getString("nextShowOn")))
                                                <0
                                ){
                                    return false;
                                }
                            }

                        }
                    }
                }
            }

        }

        return true;
    }
}
