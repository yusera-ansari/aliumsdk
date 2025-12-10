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
import java.util.concurrent.ConcurrentLinkedQueue;

//main class
//entry point for the SDK

public class Alium {


     static volatile SurveyConfig surveyConfig =null;


     private static volatile Alium instance;

     private static volatile String configURL;
     private AliumRequestManager aliumRequestManager=  AliumRequestManager.getManager();
     private static AliumPreferences preferences;
     private static boolean shouldResetOnBackground = false;

     private final Queue<AliumRequest> aliumRequestQueue = new ConcurrentLinkedQueue<>();
     private static volatile boolean isConfigFetching=false;
    private static final Object configLock = new Object();
     private  Alium(){

     }

     static boolean isShouldResetOnBackground() {
        return shouldResetOnBackground;
    }

    public static void setShouldResetOnBackground(boolean shouldResetOnBackground) {
        Alium.shouldResetOnBackground = shouldResetOnBackground;
    }
private static void fetchConfigSafely(){
    synchronized (configLock){
        if(isConfigFetching)return;
        isConfigFetching=true;
    }
    instance.fetchConfigJson();
}
    public static void config(Application application, String url){
            if(instance==null||preferences==null){
                synchronized (Alium.class){
                    if(instance==null){
                        Logger.log(Logger.LogLevel.INFO, "config","Initialized Alium()...");
                        instance=new Alium();
                    }
                    if(preferences==null){
                        Logger.log(Logger.LogLevel.INFO, "config","Initialized preferences...");
                        preferences= AliumPreferences.setInstance(application);
                    }
                }
            }

        if( url.trim().isEmpty()){
            Logger.log(Logger.LogLevel.ERROR, "config","Configuration URL can't be empty. Please set a valid url: "+url );
            return;
        }
        synchronized (configLock) {
            if (configURL == null) {
                configURL = url;
                Logger.log(Logger.LogLevel.INFO, "config",
                        "Config URL set to " + url);
                fetchConfigSafely();
            } else {
                Logger.log(Logger.LogLevel.INFO, "config",
                        "Config already set. Using " + configURL);
            }
        }
//            if(configURL==null ){
//               synchronized (Alium.class){
//                   if(configURL==null ){
//                       configURL=url;
//                       Logger.log(Logger.LogLevel.INFO, "config", "url set to "+url);
////                       instance.fetchConfigJson( );
//                       fetchConfigSafely();
//                   }else{
//                       Logger.log(Logger.LogLevel.INFO, "config", "url already set to and fetching for: "+configURL);
//                   }
//               }
//            }else{
//                Logger.log(Logger.LogLevel.INFO, "config", "url already set to and fetching for: "+configURL);
//            }
//            else if(!configURL.equals(url)) {
//              synchronized (Alium.class){
//                  if(!configURL.equals(url)){
//                      configURL = url;
//                      Logger.log(Logger.LogLevel.INFO, "config", "url set to "+url);
////                      instance.fetchConfigJson( );
//                      fetchConfigSafely();
//                  }
//              }
//            }
     }




    public static void stop(String screenName){
         if(instance==null) {
             Logger.log(Logger.LogLevel.INFO, "stop", "Alium instance is null, please ensure that you have called configure() first.");
             return;
         }
         Logger.log(Logger.LogLevel.INFO, "stop", "called on stop on "+screenName);
         instance.aliumRequestQueue.offer(new AliumRequest(  screenName));
            synchronized (configLock){
            if (configURL == null || isConfigFetching) {
                return;
            }
            }
         instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);
    }

      void fetchConfigJson(){

          Logger.log(Logger.LogLevel.DEBUG, "fConfig", "config details fetching...");
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
        if (instance == null || configURL == null) {
            Logger.log(Logger.LogLevel.ERROR, "iTrigger", "Alium not configured. Call config() before trigger().");
            return;
        }
        try{
//            Logger.log(Logger.LogLevel.INFO, "init-trigger", "trigger called on"+parameters.screenName);
//            if (configURL == null) {
//                Logger.log(Logger.LogLevel.ERROR,"init-trigger", "Configuration URL not set. Call configure() method first.");
//                return;
//            }

            Logger.log(Logger.LogLevel.DEBUG, "iTrigger", "adding to request queue...");
            instance.aliumRequestQueue.offer(new AliumRequest(  new TriggerRequest(object, parameters)));
            synchronized (configLock){
                if(surveyConfig==null && !isConfigFetching){
                    fetchConfigSafely();
                    return;
                }
                if(isConfigFetching){
                    return;
                }
            }
            instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);

//            if(surveyConfig==null && !isConfigFetching) {
////                instance.fetchConfigJson();
//                fetchConfigSafely();
//            }else{
//                if(configURL==null||isConfigFetching ){return;}
//                instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);
//
//            }
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR, "init-trigger", e.toString());

        }
    }
    public static synchronized void trigger( Activity activity, SurveyParameters parameters){
        if (instance == null) {
            Logger.log(Logger.LogLevel.ERROR, "trigger", "Alium not configured. Call config() before trigger().");
            return;
        }
       initiateTrigger(activity, parameters);
    }
public static void cleanup(){
      AliumRequestManager.getManager().cleanup();
}

    private static class ConfigURLResponseListener implements ResponseListener{
        @Override
        public void onResponseReceived(JSONObject json) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {

                try{
                    SurveyConfig parsed = AliumJSONParser.getSurConfFromJSON(json);
                    Logger.log(Logger.LogLevel.INFO, "config-resp",
                            "received response for config "+parsed);
                    preferences.storeConfig(json.toString());
                    synchronized (configLock){
                        isConfigFetching = false;
                        surveyConfig= parsed;

                    }

                    instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);

                }catch (Exception e){
                    Logger.log(Logger.LogLevel.ERROR, "config-resp", e.toString());
                    synchronized (configLock){
                        isConfigFetching = false;
                        surveyConfig = null;
                    }

                }
//            }
//        });

           }

        @Override
        public void onRequestFailed(Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Config-resp", e.toString());
            synchronized (configLock){
                isConfigFetching = false;
                surveyConfig = null;
            }
        }
    }

}
