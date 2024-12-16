package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ProcessLifecycleOwner;

import com.dwao.alium.listeners.SurveyLoader;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class Alium {
     private static JSONObject surveyConfigJSON;
     private static HashMap<String, SurveyConfig> surveyConfigMap =new HashMap<>();
     protected static AppLifeCycleListener appLifeCycleListener;
     private static  Alium instance;
     private static boolean appState=false;
     static boolean isAppInForeground(){
        return appState;
    }
     static Map<String, SurveyLoader> surveyLoaderMap=new HashMap<>();
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

        }

        public static SurveyLoader trigger(Activity activity, SurveyParameters parameters){
         Log.d("surveyObserver", ""+surveyLoaderMap);
            if (configURL == null) {
                throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            }
            SurveyLoader surveyLoader=new SurveyLoaderObserver(parameters.screenName);
            surveyLoader.setIsStopped(false);
            if(surveyConfigMap.isEmpty()) {
                volleyService.callVolley(activity, configURL, new ConfigURLResponseListener(activity, parameters, surveyLoader,
                        new ConfigURLResponseListener.Callback() {
                            @Override
                            public void onCall() {
                                AliumSurveyLoader aliumSurveyLoader=   AliumSurveyLoader.createInstance(activity, parameters, surveyConfigMap);
                                Log.d("instance", ""+(aliumSurveyLoader!=null));
                                if(aliumSurveyLoader!=null) {

                                    aliumSurveyLoader.showSurvey();
                                    if (surveyLoaderMap.get(parameters.screenName) != null) {
                                        surveyLoaderMap.get(parameters.screenName).setIsStopped(false);
                                        surveyLoaderMap.get(parameters.screenName).addSurveyLoader(aliumSurveyLoader);
                                    } else {

                                        surveyLoaderMap.put(parameters.screenName, surveyLoader);
                                        surveyLoader.addSurveyLoader(aliumSurveyLoader);
                                    }
                                }
                            }
                        }));
                Log.d("Alium-initialized", "calling survey on" + parameters.screenName);

            }

           else {
                Log.d("helllo", surveyConfigJSON.toString());
                AliumSurveyLoader aliumSurveyLoader =   AliumSurveyLoader.createInstance(activity, parameters, surveyConfigMap);
                Log.d("instance", ""+(aliumSurveyLoader!=null));
                if(aliumSurveyLoader!=null){
                    aliumSurveyLoader.showSurvey();
                    if (surveyLoaderMap.get(parameters.screenName) != null) {
                        surveyLoaderMap.get(parameters.screenName).setIsStopped(false);
                        surveyLoaderMap.get(parameters.screenName).addSurveyLoader(aliumSurveyLoader);
                    } else {
                        surveyLoaderMap.put(parameters.screenName, surveyLoader);
                        surveyLoader.addSurveyLoader(aliumSurveyLoader);

                    }
                }
            }

            return surveyLoaderMap.get(parameters.screenName)!=null?surveyLoaderMap.get(parameters.screenName):surveyLoader;

        }

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
         new AliumSurveyLoader(fragment, parameters, surveyConfigMap).showSurvey();

        }

    }
        private static class ConfigURLResponseListener implements VolleyResponseListener{
            Activity activity;
            SurveyParameters surveyParameters;
            Fragment xfragment;
            android.app.Fragment fragment;
            SurveyLoader surveyLoader;
            Callback callback;
             interface Callback{
                public void onCall();
            }
            ConfigURLResponseListener(Activity activity,SurveyParameters parameters, SurveyLoader surveyLoader, Callback callback){
                this.surveyLoader=surveyLoader;
                this.activity=activity;
                surveyParameters=parameters;
                this.callback=callback;
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
              if(xfragment!=null){
                  trigger(xfragment, surveyParameters);
//                  new AliumSurveyLoader(fragment, surveyParameters, surveyConfigMap)
//                          .showSurvey();
              }   else if(fragment!=null){
                  trigger(fragment, surveyParameters);
//                  new AliumSurveyLoader(fragment, surveyParameters, surveyConfigMap)
//                          .showSurvey();
              }
            else if(activity!=null) {
                callback.onCall();
//                  trigger(activity, surveyParameters);
//                  new AliumSurveyLoader(activity, surveyParameters, surveyConfigMap)
//                          .showSurvey();
              }

            }
        }




}
