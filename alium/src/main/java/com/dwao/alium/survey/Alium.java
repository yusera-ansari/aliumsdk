package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ProcessLifecycleOwner;

import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TriggerRequest;
import com.dwao.alium.network.VolleyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

//main class
//entry point for the SDK

public class Alium {

     private static JSONObject surveyConfigJSON;
     static volatile Map<String, SurveyConfig> surveyConfigMap =new HashMap<>();

     private static volatile Alium instance;

     private static VolleyService volleyService;
     private static String configURL;
     private SLQHandlerManager slqHandlerManager=new SLQHandlerManager();
     private volatile   Queue<TriggerRequest> triggerRequestQueue=new LinkedList<>();
     private static volatile boolean isConfigFetching=false;
     private  Alium(){
         surveyConfigJSON=new JSONObject();
     }

    public static void config(Application application,String url){

            if(instance==null){
                synchronized (Alium.class){
                    if(instance==null){
                        volleyService=VolleyService.getInstance(application);
                        instance=new Alium();
                    }
                }
            }
        if(url==null || url.trim().isEmpty()){
            Log.e("Alium", "Configuration URL can't be empty. Please set a valid url: "+url );
//             throw new IllegalStateException("Configuration URL can't be empty. Please set a valid url: "+url );
            return;
        }
            if(configURL==null ){
               synchronized (Alium.class){
                   if(configURL==null){
                       Log.d("CONFIG", "url is null! setting....");
                       configURL=url;
                       surveyConfigJSON=new JSONObject();
                       instance.fetchConfigJson( );
                   }
               }
            }
            //reset url
            if(!configURL.equals(url)) {
              synchronized (Alium.class){
                  if(!configURL.equals(url)){
                      configURL = url;
                      surveyConfigJSON=new JSONObject();
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
        volleyService.callVolley(  configURL,
                new Alium.ConfigURLResponseListener());
    }

    public static synchronized void trigger( Activity activity, SurveyParameters parameters){
        if (configURL == null) {
            Log.e("Alium", "Configuration URL not set. Call configure() method first.");
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            return;
        }
        //send the request in queue
        instance.triggerRequestQueue.offer(new TriggerRequest(activity, parameters));

        //if config-map/first-json isn't available or isn't being fetched we fetch it
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson(  );
         }else{ //if config json is present and isn't being fetched , we execute survey request
            if(configURL==null||isConfigFetching ){return;}  instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);
        }
    }

    public static synchronized void trigger( Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            Log.e("Alium", "Configuration URL not set. Call configure() method first.");
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            return;
        }
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("My request", "request is not empty: "+request.surveyParameters.screenName);
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(fragment, parameters));
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson( );
        }else{
            if(configURL==null||isConfigFetching ){return;}   instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);
        }
    }

    public static synchronized void trigger(android.app.Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            Log.e("Alium", "Configuration URL not set. Call configure() method first.");
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            return;
        }
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("My request", "request is not empty: "+request.surveyParameters.screenName);
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(fragment, parameters));
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson(  );
        }else{
            if(configURL==null||isConfigFetching ){return;}   instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);
        }
    }

    private static class ConfigURLResponseListener implements VolleyResponseListener{
        @Override
        public void onResponseReceived(JSONObject jsonObject) {
            surveyConfigJSON=jsonObject;
            Gson gson=new Gson();
            Type mapType = new TypeToken<HashMap<String,SurveyConfig >>(){}.getType();
            surveyConfigMap = gson.fromJson(jsonObject.toString(), mapType);
            Log.d("THREAD", "on response received: "+Thread.currentThread().getName());
            isConfigFetching=false;
            if(configURL==null||isConfigFetching ){return;}  instance.slqHandlerManager.executeNextTrigger(instance.triggerRequestQueue);
        }
     }

}
