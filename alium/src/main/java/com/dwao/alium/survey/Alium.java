package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;

import android.os.Handler;
import android.os.Looper;

import com.dwao.alium.listeners.ResponseListener;

import com.dwao.alium.models.AliumRequest;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.models.TriggerRequest;
import com.dwao.alium.network.CustomNetworkService;

import com.dwao.alium.services.Logger;
import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;
import com.dwao.alium.utils.preferences.AliumPreferences;


import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

//main class
//entry point for the SDK

public class Alium {


     static volatile SurveyConfig surveyConfig =null;


     private static volatile Alium instance;

     private static volatile String configURL;
     private AliumRequestManager aliumRequestManager=  AliumRequestManager.getManager();
     private static AliumPreferences preferences;

     private volatile  Queue<AliumRequest> aliumRequestQueue = new LinkedList<>();
     private static volatile boolean isConfigFetching=false;
     private  Alium(){

     }

    public static void config(Application application,String url){
            if(instance==null){
                synchronized (Alium.class){
                    if(instance==null){
                        instance=new Alium();
                        preferences= AliumPreferences.setInstance(application);
                    }
                }
            }
        if( url.trim().isEmpty()){
            Logger.log(Logger.LogLevel.ERROR, "config","Configuration URL can't be empty. Please set a valid url: "+url );
            return;
        }
            if(configURL==null ){
               synchronized (Alium.class){
                   if(configURL==null){
//                       android.util.Log.d("CONFIG", "url is null! setting....");
                       Logger.log(Logger.LogLevel.DEBUG, "config","url is null! setting value to: "+url);
                       configURL=url;
                       Logger.log(Logger.LogLevel.INFO, "config", "url set to "+url);
                       instance.fetchConfigJson( );
                   }
               }
            }
            else if(!configURL.equals(url)) {
              synchronized (Alium.class){
                  if(!configURL.equals(url)){
                      configURL = url;
                      Logger.log(Logger.LogLevel.INFO, "config", "url set to "+url);
                      instance.fetchConfigJson( );
                  }
              }
            }
     }




    public static void stop(String screenName){
         Logger.log(Logger.LogLevel.INFO, "stop", "called on stop on "+screenName);
         instance.aliumRequestQueue.offer(new AliumRequest(  screenName));
         if(configURL==null||isConfigFetching ){return;}
         instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);
    }

      void fetchConfigJson(){
          isConfigFetching=true;
          Logger.log(Logger.LogLevel.DEBUG, "fetch-config", "initiate config details fetching...");
          CustomNetworkService.getNetworkData(  configURL,
                  new Alium.ConfigURLResponseListener());
//          disabled shared pref
//          String config = preferences.getConfig();

          /*  String config = null;
          try{
              if (config != null) {
                  surveyConfig = AliumJSONParser.getSurConfFromJSON(new JSONObject(config));
                  isConfigFetching=false;
              }else{
                  throw new NullPointerException("config in sharepref is null");
              }
          }catch (Exception e){
              Logger.log(Logger.LogLevel.ERROR,"fetch-config", e.toString());
              Logger.log(Logger.LogLevel.INFO, "fetch-config", "Preferences is null so fetching config from config url: "+configURL);
              CustomNetworkService.getNetworkData(  configURL,
                      new Alium.ConfigURLResponseListener());
          }*/

    }

    private static void initiateTrigger(Object object, SurveyParameters parameters ){
        try{
            Logger.log(Logger.LogLevel.INFO, "init-trigger", "trigger called on"+parameters.screenName);
            if (configURL == null) {
                Logger.log(Logger.LogLevel.ERROR,"init-trigger", "Configuration URL not set. Call configure() method first.");
                return;
            }

            Logger.log(Logger.LogLevel.DEBUG, "init-trigger", "adding to request queue...");
            instance.aliumRequestQueue.offer(new AliumRequest(  new TriggerRequest(object, parameters)));


            if(surveyConfig==null && !isConfigFetching) {
                instance.fetchConfigJson();
            }else{
                if(configURL==null||isConfigFetching ){return;}
                instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);

            }
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR, "init-trigger", e.toString());

        }
    }
    public static synchronized void trigger( Activity activity, SurveyParameters parameters){

       initiateTrigger(activity, parameters);
    }
public static void cleanup(){
      AliumRequestManager.getManager().cleanup();
}

    private static class ConfigURLResponseListener implements ResponseListener{
        @Override
        public void onResponseReceived(JSONObject jsonObject) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {

                try{
                    surveyConfig= AliumJSONParser.getSurConfFromJSON(jsonObject);
                    Logger.log(Logger.LogLevel.INFO, "config-resp",
                            "received response for config "+surveyConfig.toString());
                    preferences.storeConfig(jsonObject.toString());
                    isConfigFetching=false;
                    if(configURL==null||isConfigFetching ){return;}
                    instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);

                }catch (Exception e){
                    Logger.log(Logger.LogLevel.ERROR, "config-resp", e.toString());
                    isConfigFetching = false;
                    surveyConfig = null;
                }
//            }
//        });

           }

        @Override
        public void onRequestFailed(Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Config-resp", e.toString());
            isConfigFetching = false;
            surveyConfig = null;
        }
    }

}
