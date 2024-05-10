package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        Log.d("Alium-Target2", surveyParameters.screenName);
//        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        Iterator<String> keys = surveyConfigJSON.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject jsonObject = surveyConfigJSON.getJSONObject(key);
                JSONObject ppupsrvObject = jsonObject.getJSONObject("appsrv");
                Uri spath=Uri.parse(jsonObject.getString("spath"));
                Log.d("URI", spath.toString());
                String urlValue = ppupsrvObject.getString("url");
                Log.d("Alium-Target2", "Key: " + key + ", URL: " + urlValue);
                if (surveyParameters.screenName.equals(urlValue)){
                    String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
                    String thankyouObj = ppupsrvObject.getString("thnkMsg");
                    Log.e("Alium-True","True");
                    Log.d("Alium-url-match",""+true);

                    if(aliumPreferences.checkForUpdate(key, srvshowfrq)){
                        loadSurvey( new LoadableSurveySpecs(key, srvshowfrq, spath, thankyouObj));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        String surURL=loadableSurveySpecs.uri.toString();
        switch (loadableSurveySpecs.surveyFreq) {
            case "onlyonce":
                Log.i("srvshowfrq", "show survey frequency: onlyonce");
                aliumPreferences.addToAliumPreferences(loadableSurveySpecs.key,
                        loadableSurveySpecs.surveyFreq);
                break;
            case "overandover":
                Log.i("srvshowfrq", "show survey frequency: overandover");
                break;
            case "untilresponse":
                Log.i("srvshowfrq", "show survey frequency: untilresponse");
                break;
        }
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
