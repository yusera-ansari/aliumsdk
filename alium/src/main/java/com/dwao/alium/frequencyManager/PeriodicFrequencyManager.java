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

 class PeriodicFrequencyManager extends SurveyFrequencyManager {
    private String TAG="PeriodicFrequencyManager";
    public PeriodicFrequencyManager(AliumPreferences aliumPreferences) {
        super(aliumPreferences);
    }

    @Override
    public void handleFrequency(String survFreq, String key) {
       try{
           String[] freqData=survFreq.split("-");
           int days=getPeriodCount(freqData[1]);
           JSONObject freqObj = new JSONObject();
           freqObj.put("showFreq", freqData[0] + "-" + freqData[1]);
           freqObj.put("counter", 0);
           freqObj.put("maxCount", Integer.parseInt(freqData[0]));
           freqObj.put("period", freqData[1]);
           Date today = Calendar.getInstance().getTime();
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
           String date = format.format(today);
           Calendar nextShowOn = Calendar.getInstance();
           nextShowOn.add(Calendar.DAY_OF_MONTH, days);
           String nextShowOnDate = format.format(nextShowOn.getTime());
           freqObj.put("lastShownOn", date);
           freqObj.put("nextShowOn", nextShowOnDate);
           //check if key exists
           if (!aliumPreferences.getFromAliumPreferences(key).isEmpty()) {
               JSONObject storedFreq =
                       new JSONObject(aliumPreferences.getFromAliumPreferences(key));
               Log.d("frequency", "alium preference does have your key " + storedFreq);

               if (
                   //check if show frequency has changed
                       !storedFreq.getString("showFreq").equals(freqData[0] + "-" + freqData[1])
                               ||
                               //check if reset date and today's date are equal
                               storedFreq.getString("nextShowOn").equals(date))
               {
                   //if changed update to new
                   freqObj.put("counter", 1);
                   Log.d("frequency", "stored freq and current freq are not same " + storedFreq.getString("showFreq")
                           + " current " + freqData[0] + "-" + freqData[1]);
               } else if (storedFreq.getString("lastShownOn").equals(date)) {
                   //check if last shown is equals to today's date
                   if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                       //check if counter has reached max value, if not update!
                       Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                               " counter: " + storedFreq.getInt("counter"));
                       freqObj.put("counter", storedFreq.getInt("counter") + 1);
                   }
               } else {
                   Log.d("frequency", "else");
                   Date todaysDate=format.parse(date);
                   Date nextShowOnDateParsed=format.parse(storedFreq.getString("nextShowOn"));
                   if(todaysDate.compareTo(nextShowOnDateParsed)>0){//today is after next show
                       freqObj.put("counter", 1);
                       Log.d("frequency", "else---"+freqObj);
                   } else if(todaysDate.compareTo(nextShowOnDateParsed)<0){//today is before next show
                       Log.d("frequency", "today is before " + storedFreq.getInt("maxCount") +
                               " counter: " + storedFreq.getInt("counter"));
                       if (storedFreq.getInt("counter") != storedFreq.getInt("maxCount")) {
                           //check if counter has reached max value, if not update!
                           Log.d("frequency", "max-count: " + storedFreq.getInt("maxCount") +
                                   " counter: " + storedFreq.getInt("counter"));
                           freqObj.put("counter", storedFreq.getInt("counter") + 1);
                           freqObj.put("lastShownOn", storedFreq.getString("lastShownOn"));
                           freqObj.put("nextShowOn", storedFreq.getString("nextShowOn"));
                       }else{
                           return;
                       }
                   }else {
                       Log.d("sfinal-frequency", storedFreq.toString());
                       return;
                   }
               }
           } else { //key doesn't exists
               Log.d("frequency", "alium preference does not have your key ");
               freqObj.put("counter", 1);
           }
           Log.d("final-frequency", freqObj.toString());
           aliumPreferences.addToAliumPreferences(key, freqObj.toString());
       }catch (Exception e){
           Log.e(TAG, e.toString());
       }
    }

    @Override
    public boolean shouldSurveyLoad(String key, String srvshowfrq) throws ParseException, JSONException {
        return false;
    }

    private int getPeriodCount(String period)  {

        Log.d("handlePeriodicFreqCount", "frequency pppp"+ period);
        switch (period){
            case "d": {
                return 1;

            }
            case "w": {
                return 7;
            }
            case "m":
            {
                return 30;
            }

            case "y":
                Log.d("frequency[1]", "frequency[1] is   in range..."+ period);
                return 0;
            default:
                Log.d("frequency[1]", "frequency[1] is not in range..."+ period);
                return 0;

        }

    };
}
