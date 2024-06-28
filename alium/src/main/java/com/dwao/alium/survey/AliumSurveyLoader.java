package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;

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
                Log.i("error", "inside catch block");
                e.printStackTrace();
            }
        }
    }

    private void loadSurveyIfShouldBeLoaded(JSONObject currentSurveyJson, String key)  {
       try{
           JSONObject ppupsrvObject = currentSurveyJson.getJSONObject("appsrv");
           Uri spath=Uri.parse(currentSurveyJson.getString("spath"));
           Log.d("URI", spath.toString());
           String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
           CustomFreqSurveyData customFreqSurveyData=null;
           if(ppupsrvObject.has("customSurveyDetails")){
               JSONObject customSurveyDetails=ppupsrvObject.getJSONObject("customSurveyDetails");

               customFreqSurveyData=new CustomFreqSurveyData(
                       customSurveyDetails.getString("freq"),
                       customSurveyDetails.getString("startOn"),
                       customSurveyDetails.getString("endOn")
               );
           }
//           String srvshowfrq="custom";
//           customFreqSurveyData=new CustomFreqSurveyData(
//                  "2-d",
//                  "2024-06-23",
//                  "2024-06-27"
//          );

           String thankyouObj = ppupsrvObject.getString("thnkMsg");
           if(   FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,key, srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad()){
               loadSurvey( new LoadableSurveySpecs(
                       key, srvshowfrq, spath, thankyouObj,
                       customFreqSurveyData
               ));
           }
       }catch (Exception e){
           Log.e("loadSurveyIfShouldLoad", e.toString());
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