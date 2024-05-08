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
    private String thankyouObj;
    private String currentSurveyIndx;

    public String getCurrentSurveyIndx() {
        return currentSurveyIndx;
    }

    public void setCurrentSurveyIndx(String currentSurveyIndx) {
        this.currentSurveyIndx = currentSurveyIndx;
    }

    private String currentSurveyFrequency;
    private  String currentSurveyKey;
//        private static Map<String, SurveyConfig> surveyConfigMap;

    private  Gson gson;
    private static  Alium instance;
    private  Context context;
    private static String customerId="";
    private JSONArray surveyQuestions;
    private JSONObject surveyUi;
    private JSONObject surveyInfo;
    private static VolleyService volleyService;
    String uuid, currentScreen;
    private static String configURL;
    private SurveyParameters surveyParameters;

    private  AliumSurveyLoader(){}
    public AliumSurveyLoader(SurveyParameters surveyParameters, JSONObject surveyConf){
        this.surveyParameters=surveyParameters;
        surveyConfigJSON=surveyConf;
        volleyService=new VolleyService();
    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public String getUuid() {
        return uuid;
    }

    public JSONArray getSurveyQuestions(){
        return surveyQuestions;
    }
    public JSONObject getSurveyUi() {
        return surveyUi;
    }

    public JSONObject getSurveyInfo() {
        return surveyInfo;
    }

//    public static Map<String, SurveyConfig> getSurveyConfigMap() {
//        return surveyConfigMap;
//    }

//    public static void setSurveyConfigMap(Map<String, SurveyConfig> surveyConfigMap) {
//        Alium.surveyConfigMap = surveyConfigMap;
//    }

    public String getCurrentScreen() {
        return currentScreen;
    }

    public static String getCustomerId() {
        return customerId;
    }
    public void showSurvey(Context ctx, String currentScreen){
        Log.d("Alium", "showing survey on :"+currentScreen);
        context=ctx;
        gson=new Gson();
        aliumPreferences= AliumPreferences.getInstance(ctx);
        if(aliumPreferences.getCustomerId().length()==0){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
        customerId= aliumPreferences.getCustomerId();
        this.currentScreen=currentScreen;
        uuid= UUID.randomUUID().toString();
        surveyResponse(surveyConfigJSON, this.currentScreen);
    }


    private void surveyResponse(JSONObject response, String checkURL) {
        Log.d("Alium-Target2", checkURL);
//        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        Iterator<String> keys = response.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject jsonObject = response.getJSONObject(key);
                JSONObject ppupsrvObject = jsonObject.getJSONObject("appsrv");
                Uri spath=Uri.parse(jsonObject.getString("spath"));
                Log.d("URI", spath.toString());
                String urlValue = ppupsrvObject.getString("url");
                Log.d("Alium-Target2", "Key: " + key + ", URL: " + urlValue);

                if (checkURL.equals(urlValue)){
                    String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
                    thankyouObj=ppupsrvObject.getString("thnkMsg");
                    Log.e("Alium-True","True");
                    Log.d("Alium-url-match",""+true);

                    if(aliumPreferences.checkForUpdate(key, srvshowfrq)){
                        loadSurvey(key, srvshowfrq, spath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSurvey(String key, String srvshowfrq, Uri uri) {
        currentSurveyFrequency=srvshowfrq;
        currentSurveyKey=key;
        String surURL=uri.toString();
        VolleyResponseListener volleyResponseListener2=new VolleyResponseListener() {
            @Override
            public void onResponseReceived(JSONObject json) {
                Log.d("Alium-survey loaded", json.toString());

                try {
                    surveyQuestions=json.getJSONArray("surveyQuestions");
//                    convertWithGson(surveyQuestions);
                    if(json.has("surveyUI")){
                        surveyUi=json.getJSONObject("surveyUI");
                    }
                    if(json.has("surveyInfo")){
                        surveyInfo=json.getJSONObject("surveyInfo");
                    }

//                    SurveyDialog showSurveyQuestion=new SurveyDialog(context, AliumSurveyLoader.this);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSurveyQuestion.show();
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        if(currentSurveyFrequency.equals("onlyonce")){
            Log.i("srvshowfrq", "show survey frequency: onlyonce");
            volleyService.callVolley(context, surURL,volleyResponseListener2 );
            aliumPreferences.addToAliumPreferences(key, srvshowfrq);
        }else if(currentSurveyFrequency.equals("overandover")){
            Log.i("srvshowfrq", "show survey frequency: overandover");
            volleyService.callVolley(context, surURL,volleyResponseListener2 );
        }else if(currentSurveyFrequency.equals("untilresponse")){
            Log.i("srvshowfrq", "show survey frequency: untilresponse");
            volleyService.callVolley(context, surURL,volleyResponseListener2 );
        }


    }



    protected void trackWithAlium() {
        try{
            Log.d("track- first hit", SurveyTracker.getUrl(
                    surveyInfo.getString("surveyId"),uuid, currentScreen,
                    surveyInfo.getString("orgId"),
                    customerId
            ) +"&"+SurveyTracker.getAppendableCustomerVariables(surveyParameters.customerVariables));
            volleyService.loadRequestWithVolley(context, SurveyTracker.getUrl(
                    surveyInfo.getString("surveyId"),uuid, currentScreen,
                    surveyInfo.getString("orgId"),
                    customerId
            ) +SurveyTracker.getAppendableCustomerVariables(surveyParameters.customerVariables));
        }catch(Exception e){
            Log.d("trackWithAlium()", e.toString());
        }
    }

    public String getThankyouObj() {
        return thankyouObj;
    }

    protected void submitSurvey(Dialog dialog){
        if(currentSurveyFrequency.equals("untilresponse")){
            aliumPreferences.addToAliumPreferences(currentSurveyKey,currentSurveyFrequency);
        }
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 2000);

    }
}
