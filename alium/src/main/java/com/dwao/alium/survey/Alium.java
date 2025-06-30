package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;

import android.util.Log;

import androidx.fragment.app.Fragment;

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
     private static boolean appState=false;
     static boolean isAppInForeground(){
        return appState;
    }

     private static String configURL;
     private SLQHandlerManager slqHandlerManager=new SLQHandlerManager();
     private static AliumPreferences preferences;
     private volatile   Queue<TriggerRequest> triggerRequestQueue=new LinkedList<>();
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
       instance.slqHandlerManager.stop(screenName);
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
        if (configURL == null) {
            Log.e("Alium", "Configuration URL not set. Call configure() method first.");
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            return;
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(object, parameters));
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("Thread", "Thread is :"+ Thread.currentThread().getName());
            Log.d("MyRequest", "request is not empty: "+request.surveyParameters.screenName);
        }

        if(surveyConfig==null && !isConfigFetching) {
            instance.fetchConfigJson(  );
        }else{
            if(configURL==null||isConfigFetching ){return;}  instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);
        }
    }
    public static synchronized void trigger( Activity activity, SurveyParameters parameters){
       initiateTrigger(activity, parameters);
    }

    public static synchronized void trigger( Fragment fragment, SurveyParameters parameters){
        initiateTrigger(fragment, parameters);

    }

    public static synchronized void trigger(android.app.Fragment fragment, SurveyParameters parameters){
        initiateTrigger(fragment, parameters);
    }

    private static class ConfigURLResponseListener implements ResponseListener{
        @Override
        public void onResponseReceived(JSONObject jsonObject) {

         try{
             surveyConfig= AliumJSONParser.getSurConfFromJSON(jsonObject);
             preferences.storeConfig(jsonObject.toString());
             isConfigFetching=false;
             if(configURL==null||isConfigFetching ){return;}  instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);

         }catch (Exception e){
             Log.e("ConfigURLResponseList",e.toString());
         }
           }
     }

}
