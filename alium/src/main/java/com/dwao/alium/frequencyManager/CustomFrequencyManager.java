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
        return true;
    }

    ;

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
    private boolean handleShouldShowOnTime(String key, CustomFreqSurveyData customFreqSurveyData) throws JSONException, ParseException {
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
