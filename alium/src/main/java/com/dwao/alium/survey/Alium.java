package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.ProcessLifecycleOwner;

import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Alium {
     private static JSONObject surveyConfigJSON;
    private static HashMap<String, SurveyConfig> surveyConfigMap =new HashMap<>();
    protected static AppLifeCycleListener appLifeCycleListener;
     private static  Alium instance;
     private static boolean appState=false;
    static boolean isAppInForeground(){
        return appState;
    }

     private static VolleyService volleyService;
     private static String configURL;
     private  Alium(){
         volleyService=new VolleyService();
         surveyConfigJSON=new JSONObject();
     }

    public static void config(Application application,String url){
            if(instance==null){
                instance=new Alium();
            }
            if(configURL==null){
                configURL=url;
            }
            if(!configURL.equals(url)) {
                configURL = url;
                surveyConfigJSON=new JSONObject();
            }
//            application.registerActivityLifecycleCallbacks(new App());
//        appLifeCycleListener=new AppLifeCycleListener() {
//            @Override
//            public void onCreate(@NonNull LifecycleOwner owner) {
//
//                Log.d("LifeCycle", "oncraete listener"+owner.getLifecycle().getCurrentState().toString());
//            }
//
//            @Override
//            public void onDestroy(@NonNull LifecycleOwner owner) {
//                Log.d("LifeCycle", "ondestroy listener"+owner.getLifecycle().getCurrentState().toString());
//            }
//
//            @Override
//            public void onResume(@NonNull LifecycleOwner owner) {
//                Log.d("LifeCycle", "onResume listener"+owner.getLifecycle().getCurrentState().toString());
//                appState=true;
//            }
//
//            @Override
//            public void onPause(@NonNull LifecycleOwner owner) {
//                Log.d("LifeCycle", "onPause listener"+owner.getLifecycle().getCurrentState().toString());
//                appState=false;
//            }
//        };
//        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifeCycleListener);
        }

        public static void trigger(Activity activity, SurveyParameters parameters){
            if (configURL == null) {
                throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            }
            if(surveyConfigMap.isEmpty()) {
                volleyService.callVolley(activity, configURL, new ConfigURLResponseListener(activity, parameters));
                Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
            }else{
                Log.d("helllo", surveyConfigJSON.toString());
                new AliumSurveyLoader(activity, parameters, surveyConfigMap)
                        .showSurvey();
            }

        }
//    public static void trigger(FragmentActivity activity, SurveyParameters parameters){
//        if (configURL == null) {
//            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
//        }
//        if(surveyConfigMap.isEmpty()) {
//            volleyService.callVolley(activity, configURL, new ConfigURLResponseListener(activity, parameters));
//            Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
//        }else{
//            Log.d("helllo", surveyConfigJSON.toString());
//            new AliumSurveyLoader(activity, parameters, surveyConfigMap)
//                    .showSurvey();
//        }
//
//    }
    public static void trigger( Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
        }
        if(surveyConfigMap.isEmpty()) {
            volleyService.callVolley(fragment.getContext(), configURL, new ConfigURLResponseListener(fragment, parameters));
            Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
        }else{
            Log.d("helllo", surveyConfigJSON.toString());
            new AliumSurveyLoader(fragment, parameters, surveyConfigMap)
                    .showSurvey();
        }

    }
    public static void trigger(android.app.Fragment fragment, SurveyParameters parameters){
        if (configURL == null) {
            throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
        }
        if(surveyConfigMap.isEmpty()) {
            volleyService.callVolley(fragment.getActivity(), configURL, new ConfigURLResponseListener(fragment, parameters));
            Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
        }else{
            Log.d("helllo", surveyConfigJSON.toString());
            new AliumSurveyLoader(fragment, parameters, surveyConfigMap)
                    .showSurvey();
        }

    }
        private static class ConfigURLResponseListener implements VolleyResponseListener{
            Activity activity;
            SurveyParameters surveyParameters;
            Fragment xfragment;
            android.app.Fragment fragment;
            ConfigURLResponseListener(Activity activity,SurveyParameters parameters){
                this.activity=activity;
                surveyParameters=parameters;
            }
            ConfigURLResponseListener(Fragment xfragment,SurveyParameters parameters){
                this.xfragment=xfragment;
                surveyParameters=parameters;
            }
            ConfigURLResponseListener(android.app.Fragment fragment,SurveyParameters parameters){
                this.fragment=fragment;
                surveyParameters=parameters;
            }
            @Override
            public void onResponseReceived(JSONObject jsonObject) {
                surveyConfigJSON=jsonObject;
                Log.d("Alium-SurveyConfig", jsonObject.toString());
                Gson gson=new Gson();
                Type mapType = new TypeToken<HashMap<String,SurveyConfig >>(){}.getType();

                surveyConfigMap = gson.fromJson(jsonObject.toString(), mapType);


                Log.d("SurveyConfig", surveyConfigMap.toString());
              if(activity!=null){
                  new AliumSurveyLoader(activity, surveyParameters, surveyConfigMap)
                          .showSurvey();
              }else if(fragment!=null){
                  new AliumSurveyLoader(fragment, surveyParameters, surveyConfigMap)
                          .showSurvey();
              }

            }
        }




}
