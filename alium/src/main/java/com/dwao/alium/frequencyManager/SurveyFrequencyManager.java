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
    public static SurveyFrequencyManager getFrequencyManager(AliumPreferences aliumPreferences,
                                                      String frq, CustomFreqSurveyData customFreqSurveyData){
     if(frq.matches("\\d+")){
         return new IntegerFrequencyManager(aliumPreferences);
     }
//     else if(frq.matches("\\d+-[dwm]")){
//         return new PeriodicFrequencyManager(aliumPreferences);
//     }
     else if(frq.matches("custom")){
        return new CustomFrequencyManager(aliumPreferences, customFreqSurveyData);
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

    private  boolean checkForBasicFrequencyUpdate(String key, String freq) {
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


    public void recordSurveyTriggerOnPreferences(String surveyKey, String survFreq
                                                 ){
        handleFrequency(survFreq, surveyKey);
    }


    public boolean shouldSurveyLoad(String key, String srvshowfrq,
                                    CustomFreqSurveyData customFreqSurveyData
                                    ) throws ParseException, JSONException {

        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
        Log.d("showFreq", "outside frequency comparison---"+ freqDetailString);
        JSONObject freqDetailJsonObject=new JSONObject();
        if(freqDetailString.isEmpty()){
            return true;
        }
        if(srvshowfrq.matches("\\d+")||
                srvshowfrq.matches("custom")
        ) {
            try {
                //check if stored frequency is an object -
                // frq data for integer and custom must be an object
                freqDetailJsonObject = new JSONObject(freqDetailString);
            } catch (JSONException e) {
                Log.e("frequency-Exception", " removing the key: " + freqDetailString + e.toString());
                aliumPreferences.removeFromAliumPreferences(key);
                return true;
            }
        }else if (!checkForBasicFrequencyUpdate(key, srvshowfrq)) {
            Log.e("frequency-Exception", "--removing the key: " + freqDetailString );
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
        else if(srvshowfrq.equals("custom")){
            Log.i("handleShouldShowOnTime","custom freq");
            
            if(!customFreqSurveyData.freq.matches("\\d+-min$")
                   &&
                !customFreqSurveyData.freq.matches("\\d+-hrs$")
                   &&
                !customFreqSurveyData.freq.matches("\\d+-d$")
            ){
                return false;
            }
            Log.i("handleShouldShowOnTime", customFreqSurveyData.toString());
            String[] freqData = customFreqSurveyData.freq.split("-");

            if(freqData[1].equals("min")||freqData[1].equals("hrs")){
                Log.i("handleShouldShowOnTime", customFreqSurveyData.toString());
                return handleShouldShowOnTime(key,customFreqSurveyData); //throw invalid custom frequency error
            }
            else if(freqData[1].equals("d")) {

                String TAG="handleShouldShowForMin";
                boolean showFreqIsNotEqual=!freqDetailJsonObject.getString("showFreq").equals("custom");
                boolean freqPeriodIsNotEqual=!freqDetailJsonObject.getString("freq").equals(
                        customFreqSurveyData.freq
                );

                boolean startDateIsNotEqual=!freqDetailJsonObject.getString("startOn").equals(
                        customFreqSurveyData.startOn
                );
                if(showFreqIsNotEqual || freqPeriodIsNotEqual||startDateIsNotEqual){
                    aliumPreferences.removeFromAliumPreferences(key);
                    return true;
                }
                if(!freqDetailJsonObject.getString("startOn").equals("immediately")){
                    try{
                        Date today= Calendar.getInstance().getTime();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        String date=format.format(today);
                        boolean isTodayBeforeStartDate=  today.compareTo(format.parse(freqDetailJsonObject.getString("startOn")))
                                <0;
                        if(
                                isTodayBeforeStartDate
                        ){
                            return false;
                        }
                    }catch (Exception e){
                        Log.e("date", "frequency date"+e.toString());
                        return false;
                    }
                }

                if(!freqDetailJsonObject.getString("endOn").equals("never")){
                    try{
                        Date today= Calendar.getInstance().getTime();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        String date=format.format(today);

                        boolean isTodayAfterEndDate=today.compareTo(format.parse(freqDetailJsonObject.getString("endOn")))
                                >0;
                        if(
                                isTodayAfterEndDate
                        ){
                            return false;
                        }
                    }catch (Exception e){
                        Log.e("date", "frequency date"+e.toString());
                    }
                }
              ///pending next count/reset count
                Date todaysDate=Calendar.getInstance().getTime();
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                Date lastShownOn=format.parse( freqDetailJsonObject.getString("lastShownOn"));
                calendar.setTime(lastShownOn);
                calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(freqData[0]));
                Date nextShowOn=calendar.getTime();


                if(!((todaysDate.compareTo(nextShowOn)>0)||(todaysDate.compareTo(nextShowOn)==0))){//today is after next show

                    Log.d("frequency", "else--- cant show next no match");
                    return false;
                }
                return  true;


            }

        }
        return true;
    }
    boolean handleShouldShowOnTime(String key, CustomFreqSurveyData customFreqSurveyData) throws JSONException, ParseException {
         String TAG="handleShouldShowForMin";



        Log.i("handleShouldShowOnTime", customFreqSurveyData.toString());
        String[] freqData = customFreqSurveyData. freq.split("-");
        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
        JSONObject freqDetailJsonObject=new JSONObject(freqDetailString);
        boolean showFreqIsNotEqual=!freqDetailJsonObject.getString("showFreq").equals("custom");
        boolean freqPeriodIsNotEqual=!freqDetailJsonObject.getString("freq").equals(
                customFreqSurveyData.freq
        );

        boolean startDateIsNotEqual=!freqDetailJsonObject.getString("startOn").equals(
                customFreqSurveyData. startOn
        );
        if(showFreqIsNotEqual || freqPeriodIsNotEqual||startDateIsNotEqual){
            aliumPreferences.removeFromAliumPreferences(key);
            return true;
        }
        if(!freqDetailJsonObject.getString("startOn").equals("immediately")){
            try{
                Date today= Calendar.getInstance().getTime();
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                String date=format.format(today);
                boolean isTodayBeforeStartDate=  today.compareTo(format.parse(freqDetailJsonObject.getString("startOn")))
                        <0;
                if(
                        isTodayBeforeStartDate
                ){
                    return false;
                }
            }catch (Exception e){
                Log.e("date", "frequency date"+e.toString());
                return false;
            }
        }

       if(!freqDetailJsonObject.getString("endOn").equals("never")){
           try{
               Date today= Calendar.getInstance().getTime();
               SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
               String date=format.format(today);

               boolean isTodayAfterEndDate=today.compareTo(format.parse(freqDetailJsonObject.getString("endOn")))
                       >0;
               if(
                       isTodayAfterEndDate
               ){
                   return false;
               }
           }catch (Exception e){
               Log.e("date", "frequency date"+e.toString());
           }
       }
        long intervalInMillis;
       if(freqData[1].equals("min")){
        intervalInMillis= (long) Integer.parseInt(freqData[0]) *60*1000;
       }else{
           intervalInMillis= (long) Integer.parseInt(freqData[0]) *60*60*1000;
       }
        boolean hasIntervalCrossed=(System.currentTimeMillis()-
                freqDetailJsonObject.getLong("lastShownOnMillis")) >=intervalInMillis;
        if(!hasIntervalCrossed){
            Log.d(TAG, "interval hasn't crossed");
            return false;
        }



        return  true;
    }
}
