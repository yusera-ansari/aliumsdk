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

 class CustomFrequencyManager extends SurveyFrequencyManager{
    JSONObject freqObj = new JSONObject();


    CustomFreqSurveyData customFreqSurveyData ;
    private String TAG="CustomFrequencyManager";
    protected CustomFrequencyManager(AliumPreferences aliumPreferences,
                                     String srvKey, String srvShowFreq,
                                     CustomFreqSurveyData
                                     customFreqSurveyData) {
        super(aliumPreferences, srvKey, srvShowFreq);
        this.customFreqSurveyData=customFreqSurveyData;

    }

    @Override
    public void handleFrequency( ) {
        Log.d("frequency", "custom frequency");
        try {
            String[] freqData = customFreqSurveyData.freq.split("-");
            freqObj.put("showFreq", this.srvShowFreq);
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
            aliumPreferences.addToAliumPreferences(this.surveyKey, freqObj.toString());
            Log.d(TAG, freqObj.toString());
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

     private boolean isValidFrequency(String freq) {
         return freq.matches("\\d+-min$") || freq.matches("\\d+-hrs$") || freq.matches("\\d+-d$");
     }
    @Override
    public boolean shouldSurveyLoad( ) throws ParseException, JSONException {

        if(!isValidFrequency(customFreqSurveyData.freq)
        ){
            throw new InavlidCustomFrequencyTypeException("The custom frequency of type \""
            +customFreqSurveyData.freq+"\" is inavlid!");
//            return false;
        }

        String freqDetailString=aliumPreferences.getFromAliumPreferences(this.surveyKey);
        Log.d("showFreq", "outside frequency comparison---"+ freqDetailString);
        JSONObject freqDetailJsonObject=null;
        String[] freqData = customFreqSurveyData.freq.split("-");
        if(!freqDetailString.isEmpty()) {
            try {
                //check if stored frequency is an object -
                // frq data for integer and custom must be an object
                freqDetailJsonObject = new JSONObject(freqDetailString);
            } catch (JSONException e) {
                Log.e("frequency-Exception", " removing the key: " + freqDetailString + e.toString());
                aliumPreferences.removeFromAliumPreferences(this.surveyKey);
            }
            Log.i("handleShouldShowOnTime","custom freq");

        }
       if(freqDetailJsonObject!=null){
           if (hasFrequencyChanged(customFreqSurveyData, freqDetailJsonObject)) {
               aliumPreferences.removeFromAliumPreferences(this.surveyKey);
               Log.i(TAG,"1"+ freqDetailJsonObject.toString());
           }
       }
        Log.i("handleShouldShowOnTime", customFreqSurveyData.toString());
        if(freqData[1].equals("min")||freqData[1].equals("hrs")){
            Log.i("handleShouldShowOnTime", customFreqSurveyData.toString());
            return handleShouldShowOnTime(this.surveyKey); //throw invalid custom frequency error
        }
        else  {
           return handleShouldShowOnDay(this.surveyKey);

        }
    }

    private boolean handleShouldShowOnDay(
            String key
    ) throws JSONException, ParseException {
        String TAG="handleShouldShowOnDay";
        Log.i(TAG, customFreqSurveyData.toString());
        String[] freqData = customFreqSurveyData. freq.split("-");
        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);

        String lastShown="";
        if(!freqDetailString.isEmpty()){
            JSONObject freqDetailJsonObject=new JSONObject(freqDetailString);
            if(freqDetailJsonObject.has("lastShownOn")){
                lastShown=freqDetailJsonObject.getString("lastShownOn");
            }
        }

        if (isTodayBeforeStartDate(customFreqSurveyData.startOn)) {
            return false;
        }

        if (isTodayAfterEndDate(customFreqSurveyData.endOn)) {
            return false;
        }

        if(lastShown.isEmpty()){
            return true;
        }
       return isNextShowDateReached(lastShown, freqData);

    }

     private boolean isNextShowDateReached(String lastShownDate, String[] freqData) throws JSONException, ParseException {
         ///pending next count/reset count
         Calendar todayCal= Calendar.getInstance() ;
      setTimeToMidNight(todayCal);
         Date today = todayCal.getTime();
         Calendar calendar=Calendar.getInstance();
         SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
         Date lastShownOn=format.parse(lastShownDate);
         calendar.setTime(lastShownOn);
       setTimeToMidNight(calendar);
         calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(freqData[0]));
         Date nextShowOn=calendar.getTime();
         Log.d("last shown", lastShownDate+" "+format.format(today));
         return today.compareTo(nextShowOn)>=0;
     }

     private boolean isTodayAfterEndDate(String endOn) throws JSONException, ParseException {
         if(endOn.equals("never")) {
             return false;
         }
         Calendar todayCal= Calendar.getInstance() ;
       setTimeToMidNight(todayCal);
         Date today = todayCal.getTime();
                 SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                 boolean isTodayAfter=today.compareTo(format.parse(endOn))>0;
                 Log.d("todays",endOn+" "+format.format(today)
                 +" "+isTodayAfter+" "+today.compareTo(format.parse(endOn)));
                 //>0 means after end date
                 return today.compareTo(format.parse(endOn))>0;

     }

     private boolean isTodayBeforeStartDate(String  startOn)
             throws JSONException, ParseException {
         if(startOn.equals("immediately")) {
         return false;
         }

         Calendar todayCal= Calendar.getInstance() ;
            setTimeToMidNight(todayCal);
         Date today = todayCal.getTime();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
         Log.d("beforeSTrat", startOn+" "+format.format(today));
            //<0 before to start date
            return today.compareTo(format.parse(startOn))<0;
     }

     private boolean hasFrequencyChanged(CustomFreqSurveyData customFreqSurveyData,
                                            JSONObject freqDetailJsonObject) throws JSONException {
         boolean showFreqIsNotEqual=!freqDetailJsonObject.getString("showFreq").equals("custom");
         if(showFreqIsNotEqual){
             return true;
         }
         boolean freqPeriodIsNotEqual=!freqDetailJsonObject.getString("freq").equals(
                 customFreqSurveyData.freq
         );

         boolean startDateIsNotEqual=!freqDetailJsonObject.getString("startOn").equals(
                 customFreqSurveyData.startOn
         );
         return showFreqIsNotEqual || freqPeriodIsNotEqual||startDateIsNotEqual;

    }

     private boolean handleShouldShowOnTime(String key) throws JSONException, ParseException {
        String TAG="handleShouldShowForMin";
                Log.i(TAG, customFreqSurveyData.toString());
        String[] freqData = customFreqSurveyData.freq.split("-");
        String freqDetailString=aliumPreferences.getFromAliumPreferences(key);
        JSONObject freqDetailJsonObject=null;

        long lastShownOnMillis=lastShownOnMillis=0L;
         if(!freqDetailString.isEmpty()){
             freqDetailJsonObject=new JSONObject(freqDetailString);
             Log.i(TAG,"0"+ freqDetailJsonObject.toString());
             if(freqDetailJsonObject.has("lastShownOnMillis")){
                 lastShownOnMillis=freqDetailJsonObject.getLong("lastShownOnMillis");
             }
         }
         Log.i(TAG,"2"+ customFreqSurveyData.toString());
         if (isTodayBeforeStartDate(customFreqSurveyData.startOn)) {
             return false;
         }
         Log.i(TAG,"3"+ customFreqSurveyData.toString());
         if (isTodayAfterEndDate(customFreqSurveyData.endOn)) {
             return false;
         }
         Log.i(TAG,"4"+ customFreqSurveyData.toString());
        long intervalInMillis=getLongIntervalInMillis(freqData);
        boolean hasIntervalCrossed=(System.currentTimeMillis()-
                lastShownOnMillis) >=intervalInMillis;
        if(!hasIntervalCrossed){
            Log.d(TAG, "interval hasn't crossed");
            return false;
        }

        return  true;
    }
    private long getLongIntervalInMillis(String[] freqData){

        long intervalInMillis;
        if(freqData[1].equals("min")){
            intervalInMillis= (long) Integer.parseInt(freqData[0]) *60*1000;
        }else{
            intervalInMillis= (long) Integer.parseInt(freqData[0]) *60*60*1000;
        }
        return intervalInMillis;
    }
    private void setTimeToMidNight(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}
