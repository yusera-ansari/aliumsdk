package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dwao.alium.listeners.ResponseListener;

import com.dwao.alium.models.SurConf;
import com.dwao.alium.models.TriggerRequest;
import com.dwao.alium.network.CustomNetworkService;

import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;
import com.dwao.alium.utils.preferences.AliumPreferences;


import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

//main class
//entry point for the SDK

public class Alium {


     static volatile SurConf surveyConfig =null;


     private static volatile Alium instance;

     private static String configURL;
     private AliumRequestManager aliumRequestManager=new AliumRequestManager();
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
            Log.e("Alium", "Configuration URL can't be empty. Please set a valid url: "+url );
            return;
        }
            if(configURL==null ){
               synchronized (Alium.class){
                   if(configURL==null){
                       Log.d("CONFIG", "url is null! setting....");
                       configURL=url;
                       instance.fetchConfigJson( );
                   }
               }
            }
            if(!configURL.equals(url)) {
              synchronized (Alium.class){
                  if(!configURL.equals(url)){
                      configURL = url;
                      instance.fetchConfigJson( );
                  }
              }
            }
        }




    public static void stop(String screenName){
        instance.aliumRequestQueue.offer(new AliumRequest(  screenName));
        if(configURL==null||isConfigFetching ){return;}
       instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);
    }

      void fetchConfigJson( ){
          isConfigFetching=true;
//          String config = preferences.getConfig();
          String config = null;
          try{
              if (config != null) {
                  surveyConfig = AliumJSONParser.getSurConfFromJSON(new JSONObject(config));
                  isConfigFetching=false;
              }else{
                  throw new NullPointerException("config in sharepref is null");
              }
          }catch (Exception e){
              Log.d("getConfig", e.toString());
              CustomNetworkService.getNetworkData(  configURL,
                      new Alium.ConfigURLResponseListener());
          }

    }

    private static void initiateTrigger(Object object,SurveyParameters parameters ){
        try{
            Log.e("trigger", " check url initiate trigger");
            if (configURL == null) {
                Log.e("Alium", "Configuration URL not set. Call configure() method first.");
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
                return;
            }
            Log.e("trigger", "url not null...");
            Log.d("initiates", "Thread is :"+ Thread.currentThread().getName());
//            instance.triggerRequestQueue.offer(new TriggerRequest(object, parameters));
            instance.aliumRequestQueue.offer(new AliumRequest(  new TriggerRequest(object, parameters)));
            Log.d("Thread", "Thread is :"+ Thread.currentThread().getName());
            for(AliumRequest request: instance.aliumRequestQueue){
                if(request.type.equals(AliumRequest.Request.TRIGGER)){
                    Log.d("Thread", "Thread is :"+ Thread.currentThread().getName());
                    Log.d("MyRequest", "request is not empty: "+request.request.surveyParameters.screenName);

                }
                }

            if(surveyConfig==null && !isConfigFetching) {
                instance.fetchConfigJson(  );
            }else{
                if(configURL==null||isConfigFetching ){return;}
                instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);

            }
        }catch (Exception e){
            Log.e("initiate", "request trigger: "+e);
        }
    }
    public static synchronized void trigger( Activity activity, SurveyParameters parameters){
        Log.e("trigger", "initiate trigger activity");
       initiateTrigger(activity, parameters);
    }


    private static class ConfigURLResponseListener implements ResponseListener{
        @Override
        public void onResponseReceived(JSONObject jsonObject) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("Alium", "on response received" + Thread.currentThread().getName());
                try{
                    surveyConfig= AliumJSONParser.getSurConfFromJSON(jsonObject);
                    preferences.storeConfig(jsonObject.toString());
                    isConfigFetching=false;
                    Log.d("ALium", "is config fetching...done");
                    if(configURL==null||isConfigFetching ){return;}
                    Log.d("ALium", "function didn't retrun");
                    instance.aliumRequestManager.executeNextRequest(instance.aliumRequestQueue);
                    Log.d("ALium", "try completed");
                }catch (Exception e){
                    Log.e("Alium","Alium"+ e);
                }
            }
        });

           }
     }

}
