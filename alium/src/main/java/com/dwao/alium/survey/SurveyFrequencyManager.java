package com.dwao.alium.survey;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SurveyFrequencyManager {
    private AliumPreferences aliumPreferences;
    private static SurveyFrequencyManager instance;
    
    public static SurveyFrequencyManager getInstance(AliumPreferences aliumPreferences) {
        if (instance == null) {
            synchronized (AliumPreferences.class) {
                instance = new SurveyFrequencyManager(aliumPreferences);
            }
        }
        return instance;
    }

    private SurveyFrequencyManager() {}

    private SurveyFrequencyManager(AliumPreferences aliumPreferences) {
        this.aliumPreferences=aliumPreferences;
        
    }

    public boolean checkForBasicFrequencyUpdate(String key, String freq) {
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

    public void handleSimpleFrequencyCount(int freq, String key) throws JSONException {
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
    }


    public void handlePeriodicFreqUpdate(String[] freqData, String key, int days) throws JSONException, ParseException {
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
    }
    public void handlePeriodicFrequencyCount(String[] freqData, String key)  {
        try{
            Log.d("handlePeriodicFreqCount", "frequency pppp"+ Arrays.toString(freqData));
            int days=0;
            switch (freqData[1]){
                case "d": {
                    days=1;
                    break;
                }
                case "w": {
                    days=7;
                    break;
                }
                case "m":
                {
                    days=30;
                    break;
                }

                case "y":
                    Log.d("frequency[1]", "frequency[1] is   in range..."+ freqData[1]);
                    break;
                default:
                    Log.d("frequency[1]", "frequency[1] is not in range..."+ freqData[1]);
                    break;

            }
            if(days>0)handlePeriodicFreqUpdate(freqData, key, days);
        }catch (Exception e){
            Log.e("handlePeriodicFrq", e.toString());
        }
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
