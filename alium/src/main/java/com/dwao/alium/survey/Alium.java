package com.dwao.alium.survey;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

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
     static List<SurveyDialog> activeSurveys=new ArrayList<>();
    static boolean isAppInForeground(){
        return appState;
    }
    static synchronized void  removeFromActiveSurveyList(SurveyDialog surveyDialog){
        Log.d("ActiverSurveys", "outside "+activeSurveys);
        if(!Alium.activeSurveys.isEmpty()){
            Log.d("ActiverSurveys", ""+activeSurveys);
            Iterator<SurveyDialog> keys= activeSurveys.iterator();
            while(keys.hasNext()){
                SurveyDialog dialog=keys.next();
                if(dialog.loadableSurveySpecs.key.equals( surveyDialog.loadableSurveySpecs.key)){
                    Log.d("activeSurvey", "survey existes");
                    keys.remove();
                    return;
                }

            }

        }
        activeSurveys.remove(surveyDialog);

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
        appLifeCycleListener=new AppLifeCycleListener() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {

                Log.d("LifeCycle", "oncraete listener"+owner.getLifecycle().getCurrentState().toString());
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                Log.d("LifeCycle", "ondestroy listener"+owner.getLifecycle().getCurrentState().toString());
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                Log.d("LifeCycle", "onResume listener"+owner.getLifecycle().getCurrentState().toString());
                appState=true;
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                Log.d("LifeCycle", "onPause listener"+owner.getLifecycle().getCurrentState().toString());
appState=false;
            }
        };
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifeCycleListener);
        }

        public static void trigger(Context ctx, SurveyParameters parameters){
//            ((Activity)ctx).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    ,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (configURL == null) {
                throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            }
            if(surveyConfigMap.isEmpty()) {
                volleyService.callVolley(ctx, configURL, new ConfigURLResponseListener(ctx, parameters));
                Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
            }else{
                Log.d("helllo", surveyConfigJSON.toString());
                new AliumSurveyLoader(ctx, parameters, surveyConfigMap)
                        .showSurvey();
            }

        }
        private static class ConfigURLResponseListener implements VolleyResponseListener{
            Context context;
            SurveyParameters surveyParameters;
            ConfigURLResponseListener(Context ctx,SurveyParameters parameters){
                context=ctx;
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
                new AliumSurveyLoader(context, surveyParameters, surveyConfigMap)
                        .showSurvey();

            }
        }




}
