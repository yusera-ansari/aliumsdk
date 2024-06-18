package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class AliumSurveyLoader {
    AliumPreferences aliumPreferences;
    private static JSONObject surveyConfigJSON;
    //        private static Map<String, SurveyConfig> surveyConfigMap;

    private  Gson gson;
    private  Context context;

    private static VolleyService volleyService;

    private SurveyParameters surveyParameters;

    private  AliumSurveyLoader(){}
    public AliumSurveyLoader(Context context,SurveyParameters surveyParameters, JSONObject surveyConf){
        this.surveyParameters=surveyParameters;
        surveyConfigJSON=surveyConf;
        volleyService=new VolleyService();
        this.context=context;
        gson=new Gson();
        aliumPreferences= AliumPreferences.getInstance(context);
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public void showSurvey( ){
        Log.d("Alium", "showing survey on :"+surveyParameters.screenName);
        findAndLoadSurveyForCurrentScr();
    }


    private void findAndLoadSurveyForCurrentScr() {
//        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        Iterator<String> keys = surveyConfigJSON.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject jsonObject = surveyConfigJSON.getJSONObject(key);
                JSONObject ppupsrvObject = jsonObject.getJSONObject("appsrv");
                String screenName = ppupsrvObject.getString("url");
                if (surveyParameters.screenName.equals(screenName)){
                    loadSurveyIfShouldBeLoaded(jsonObject, key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSurveyIfShouldBeLoaded(JSONObject currentSurveyJson, String key) throws Exception {
        JSONObject ppupsrvObject = currentSurveyJson.getJSONObject("appsrv");
        Uri spath=Uri.parse(currentSurveyJson.getString("spath"));
        Log.d("URI", spath.toString());
//        String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
        String srvshowfrq="overandover";
        String thankyouObj = ppupsrvObject.getString("thnkMsg");

//        if(srvshowfrq.matches("\\d+")) {
//                int freq=Integer.parseInt(srvshowfrq);
//                Log.d("showFreq", "outside frequency comparison");
//                String freqDetailString=aliumPreferences.getAliumSharedPreferences().getString(key,"");
//                Log.d("showFreq", "outside frequency comparison"+ freqDetailString);
//                //this only checks if survey has reached its frequency count
//                if(!freqDetailString.isEmpty()){
//
//                    JSONObject freqDetailJsonObject=new JSONObject(freqDetailString);
//                    Log.d("showFreq", "outside frequency comparison"+freqDetailJsonObject);
//                    if(freqDetailJsonObject.getInt("showFreq")==freq){
//                        if(freqDetailJsonObject.getInt("counter")==freq){
//                            Log.d("showFreq", "compared and equal");
//                            return;
//                        }
//                    }
//                }
//                Log.d("showFreq", "after frequency comparison");
//
//        }
//        else if(srvshowfrq.matches("\\d+-[dwm]")) {
//            //for periodic freq
//            String[] periodicFreq=srvshowfrq.split("-");
//                    int freqCount=Integer.parseInt(periodicFreq[0]);
//                    if(!aliumPreferences.getAliumSharedPreferences().getString(key, "").isEmpty()){
//                        JSONObject object=new JSONObject(aliumPreferences.getAliumSharedPreferences().getString(key, ""));
//                        if(object.has("showFreq")){
//                            if(object.getString("showFreq").equals(srvshowfrq)){
//
//                                if(object.has("counter")){
//                                    if( freqCount==object.getInt("counter")){
//                                        Date today= Calendar.getInstance().getTime();
//                                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//                                        String date=format.format(today);
//                                        if(object.getString("lastShownOn").equals(date)){
//                                            return;
//                                        }
//                                        if(periodicFreq[1].equals("m")||periodicFreq[1].equals("w")){
//                                           if(
//                                                   today.compareTo(format.parse(object.getString("nextShowOn")))
//                                                   <0
//                                           ){
//                                               return;
//                                           }
//                                        }
//
//                                    }
//                                }
//                            }
//                        }}
//        }
//        else if (!aliumPreferences.checkForUpdate(key, srvshowfrq)) {
//            return;
//        }
       if(aliumPreferences.shouldSurveyLoad(key, srvshowfrq)){
           loadSurvey( new LoadableSurveySpecs(key, srvshowfrq, spath, thankyouObj));
       }
    }
    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        String surURL=loadableSurveySpecs.uri.toString();
        volleyService.callVolley(context, surURL,new LoadSurveyFromAPI(loadableSurveySpecs) );
    }

    class LoadSurveyFromAPI implements VolleyResponseListener{
        LoadableSurveySpecs loadableSurveySpecs;
        private LoadSurveyFromAPI(){}
        public LoadSurveyFromAPI(LoadableSurveySpecs loadableSurveySpecs) {
            this.loadableSurveySpecs=loadableSurveySpecs;
        }

        @Override
        public void onResponseReceived(JSONObject json) {
            Log.d("Alium-survey loaded", json.toString());
            ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(json
                    , loadableSurveySpecs);
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new SurveyDialog(context, executableSurveySpecs,
                            surveyParameters)
                            .show();
                }
            });


        }
    }



}