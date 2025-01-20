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
     private static volatile Map<String, SurveyConfig> surveyConfigMap =new HashMap<>();
     protected static AppLifeCycleListener appLifeCycleListener;
     private static volatile Alium instance;
     private static boolean appState=false;
     static boolean isAppInForeground(){
        return appState;
    }
     private static VolleyService volleyService;
     private static String configURL;
     private static Map<String, ExecSurLoaderDM> surveyExecutingMap=new HashMap<>();
     private  Queue<TriggerRequest> triggerRequestQueue=new LinkedList<>();
     private static volatile boolean isConfigFetching=false;
     private static boolean isTriggerExecuting=false;
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
            if(configURL==null){
               synchronized (Alium.class){
                   if(configURL==null){
                       Log.d("CONFIG", "url is null! setting....");
                       configURL=url;
                       surveyConfigJSON=new JSONObject();
                       instance.fetchConfigJson(application);
                   }
               }
            }
            if(!configURL.equals(url)) {
              synchronized (Alium.class){
                  if(!configURL.equals(url)){
                      configURL = url;
                      surveyConfigJSON=new JSONObject();
                      instance.fetchConfigJson(application);
                  }
              }
            }
        }

    static synchronized AliumSurveyLoader.SurveyDialogCallback reAttachCallback(String id, String screenName){
        ExecSurLoaderDM execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null){
            Queue<AliumSurveyLoader> loadedQueue=execSurLoaderDM.loadedQueue;
            if(!loadedQueue.isEmpty()){
                Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                while( iterator.hasNext()){
                    AliumSurveyLoader loader=iterator.next();
                    if(loader.getLoaderId().equals(id)){
                        return loader.surveyDialogCallback;
                    }
                }
            }
        }
        return null;
    };

     static synchronized void updateExecLoaderData(String id, String screenName){
         ExecSurLoaderDM execSurLoaderDM= surveyExecutingMap.get(screenName);
         if(execSurLoaderDM!=null){ //should try with iterator
             Queue<AliumSurveyLoader> loadedQueue=execSurLoaderDM.loadedQueue;
             if(!loadedQueue.isEmpty()){
                 Log.d("LoadedQueue", "Loaded queue is mot empty we will update it!");
                 Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                 while( iterator.hasNext()) {
                     AliumSurveyLoader loader = iterator.next();
                     Log.d("Loaded-", "loaded Loader: "+loader);
                     if(loader.getLoaderId().equals(id)){
                        Log.d("Loaded-", "removing it loaded Loader: "+loader);
                        loader.callback.onQuitLoader(loader);
                        iterator.remove();

                     }
                 }
                Log.d("LOadd"," loaded queue: "+loadedQueue+" "+execSurLoaderDM.aliumSurveyLoaderQueue);
             }
         }
     }

    public static void stop(String screenName){
        ExecSurLoaderDM execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null){
            execSurLoaderDM.stop();
        }
    }



     private synchronized void executeNextTrigger(){
         if(isTriggerExecuting||configURL==null||isConfigFetching||triggerRequestQueue.isEmpty()){
             return;
         }
         isTriggerExecuting=true;
         TriggerRequest request= triggerRequestQueue.poll();
         ExecSurLoaderDM execSurLoaderDM= surveyExecutingMap.get(request.surveyParameters.screenName);
         if(execSurLoaderDM==null){
             Log.d("TRIGGER", "CALLING FROM ELSE-BLOCK!! "+request.surveyParameters.screenName);
             Log.d("THREAD", Thread.currentThread().getName());
             ExecSurLoaderDM  execSurLoaderDMTmp=new ExecSurLoaderDM(request.surveyParameters.screenName);
             surveyExecutingMap.put(request.surveyParameters.screenName, execSurLoaderDMTmp);
             execSurLoaderDMTmp.offer(request  ,surveyConfigMap);
         }
         else{
             execSurLoaderDM.offer(request , surveyConfigMap);
         }
         isTriggerExecuting=false;
         executeNextTrigger();
    }

      void fetchConfigJson(Context context ){
        Log.d("MAPMAP: ","Survey config is empty!!: "+ isConfigFetching);
        isConfigFetching=true;
        Log.d("MAPMAP-2: ","Survey config is empty!!: "+ isConfigFetching);
        volleyService.callVolley(  configURL,
                new Alium.ConfigURLResponseListener());
    }

    public static synchronized void trigger( Activity activity, SurveyParameters parameters){
        if (configURL == null) {
            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
        }
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("My request", "request is not empty: "+request.surveyParameters.screenName);
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(activity, parameters));
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson(activity );
         }else{
            instance.executeNextTrigger();
        }
    }

    public static synchronized void trigger( Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
        }
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("My request", "request is not empty: "+request.surveyParameters.screenName);
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(fragment, parameters));
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson(fragment.getActivity() );
        }else{
            instance.executeNextTrigger();
        }
    }

    public static synchronized void trigger(android.app.Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
        }
        for(TriggerRequest request: instance.triggerRequestQueue){
            Log.d("My request", "request is not empty: "+request.surveyParameters.screenName);
        }
        instance.triggerRequestQueue.offer(new TriggerRequest(fragment, parameters));
        if(surveyConfigMap.isEmpty() && !isConfigFetching) {
            instance.fetchConfigJson(fragment.getActivity() );
        }else{
            instance.executeNextTrigger();
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
            instance.executeNextTrigger();
        }
     }

}
