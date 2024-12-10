package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.RequestQueue;
import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.listeners.Observer;
import com.dwao.alium.listeners.SurveyLoader;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.CustomSurveyDetails;
import com.dwao.alium.models.Srv;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.ProcessLifecycleOwner;
public class AliumSurveyLoader implements Observer {
    private Fragment xfragment;
    private boolean activityInstanceCreated=false;

    AliumPreferences aliumPreferences;
    private static Map<String, SurveyConfig> surveyConfigMap;
    private List<Runnable> onGoingTasks=new ArrayList();
//    private  Context context;

    private Activity activity;
    private android.app.Fragment fragment;
    private static VolleyService volleyService;
    FragmentManager xfm;
    android.app.FragmentManager fm;
    private SurveyParameters surveyParameters;
ExecutorService executorService;
Handler mainHandler;
    private  AliumSurveyLoader(){}
    public AliumSurveyLoader(Activity activity,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){

        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        volleyService=new VolleyService();
        this.activity=activity;
        aliumPreferences= AliumPreferences.getInstance(activity);
        this.executorService= Executors.newSingleThreadExecutor();
        this.mainHandler=new Handler(Looper.getMainLooper());
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }


    }
    public AliumSurveyLoader(Fragment xfragment,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){

        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        volleyService=new VolleyService();
        this.xfragment=xfragment;
        aliumPreferences= AliumPreferences.getInstance(xfragment.getContext());
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }


    }
    public AliumSurveyLoader(android.app.Fragment fragment,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){

        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        volleyService=new VolleyService();
        this.fragment=fragment;
        aliumPreferences= AliumPreferences.getInstance(fragment.getActivity());
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }


    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public void showSurvey( ){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                findAndLoadSurveyForCurrentScr();

            }
        });
//        Log.d("Alium", "showing survey on :"+surveyParameters.screenName);

    }


    private void findAndLoadSurveyForCurrentScr() {
        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                SurveyConfig jsonObject = surveyConfigMap.get(key);
                Srv ppupsrvObject = jsonObject.getSrv();
                String screenName = ppupsrvObject.getUrl();
                if (surveyParameters.screenName.equals(screenName)){
//                    Log.d("ActiverSurveys", "before checking: "+Alium.activeSurveys);
                    //check if its already running
                    if(fragment!=null){
                         fm=fragment.getChildFragmentManager();
                        android.app.Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            Log.d("activeSurvey", "survey existes");
                            return;
                        }
                    } else
                        if(xfragment!=null){
                               xfm=xfragment.getChildFragmentManager();
                             Fragment fragment= xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                            if(fragment!=null){
                                Log.d("activeSurvey", "survey existes");
                                return;
                            }

                    }else if(activity !=null){
                            if (activity instanceof FragmentActivity) {
                                  xfm = ((FragmentActivity) activity).getSupportFragmentManager();
                                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("activeSurvey", "survey existes");
                                    return;
                                }
                            } else {

                                 fm = activity.getFragmentManager();
                                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("activeSurvey", "survey existes");
                                    return;
                                }
                            }
                        }
                   loadSurveyIfShouldBeLoaded(jsonObject, key);
                }
            } catch (Exception e) {
                Log.i("error", "inside catch block");
                e.printStackTrace();
            }
        }
    }

    private void loadSurveyIfShouldBeLoaded(SurveyConfig currentSurveyJson, String key)  {
       try{
           Srv ppupsrvObject = currentSurveyJson.getSrv();
           Uri spath=Uri.parse(currentSurveyJson.getSpath());
//           Log.d("URI", spath.toString());
           String srvshowfrq=ppupsrvObject.getSurveyShowFrequency();
           CustomFreqSurveyData customFreqSurveyData=null;
           if(ppupsrvObject.getCustomSurveyDetails()!=null){
               CustomSurveyDetails customSurveyDetails=ppupsrvObject.getCustomSurveyDetails();
               customFreqSurveyData=new CustomFreqSurveyData(
                       customSurveyDetails.getFreq(),
                       customSurveyDetails.getStartOn(),
                       customSurveyDetails.getEndOn()
               );
           }
//            srvshowfrq="custom";
//           customFreqSurveyData=new CustomFreqSurveyData(
//                  "2-min",
//                  "2024-07-07",
//                  "2024-09-15"
//          );

           String thankyouObj = ppupsrvObject.getThankYouMsg();
           if(   FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,key, srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad()){
               loadSurvey( new LoadableSurveySpecs(
                       key, srvshowfrq, spath.toString(), thankyouObj,
                       customFreqSurveyData
               ));
           }
       }catch (Exception e){
           Log.e("loadSurveyIfShouldLoad", e.toString());
       }
    }
    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        String surURL=loadableSurveySpecs.uri.toString();
        volleyService.callVolley(activity!=null?activity:fragment!=null?
                fragment.getActivity(): xfragment.getContext(), surURL,
                new LoadSurveyFromAPI(loadableSurveySpecs) );
    }

    @Override
    public void stop() {
        if(volleyService.getSurveyQueue()!=null){
            Log.d("stop", "observer needs to stop"+volleyService.getSurveyQueue());
            volleyService.getSurveyQueue().cancelAll(VolleyService.SURVEY_REQUEST_TAG);
        }
        executorService.shutdown();
//        handler.removeCallbacks();

        if(fm!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              for(android.app.Fragment fragment:fm.getFragments()){
                if(fragment.getTag().contains(surveyParameters.screenName)){
                    Log.d("dm", "removing fragment "+ fragment.getTag());
                    fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
              }
            }else{
                for(android.app.Fragment fragment:fm.getFragments()){
                    Log.d("dm", "removing fragment "+ fragment.getTag());
                    fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        }else if(xfm!=null){
                for(Fragment fragment:xfm.getFragments()){
                    if(fragment.getTag().contains(surveyParameters.screenName)){
                        Log.d("dm", "removing fragment "+ fragment.getTag());
                        xfm.beginTransaction().remove(fragment).commitAllowingStateLoss();
                    }
            }
        }

        Log.d("stop", "observer needs to stop");
    }


    class LoadSurveyFromAPI implements VolleyResponseListener{
        LoadableSurveySpecs loadableSurveySpecs;
        private LoadSurveyFromAPI(){}
        public LoadSurveyFromAPI(LoadableSurveySpecs loadableSurveySpecs) {
            this.loadableSurveySpecs=loadableSurveySpecs;
        }

        @Override
        public void onResponseReceived(JSONObject json) {
//            Log.d("Alium-survey loaded", json.toString());
      mainHandler.post(new Runnable() {
          @Override
          public void run() {
              loadSurveyFromDialogFragment(json, loadableSurveySpecs);
          }
      });
        }
    }
    private void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){
        Gson gson
                =new Gson();
        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                gson.fromJson(json.toString(), Survey.class)
                , loadableSurveySpecs);
        if(xfragment!=null){
              xfm=xfragment.getChildFragmentManager();
            if(!xfm.isStateSaved()){
                xfm.beginTransaction()
                        .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                surveyParameters, false), loadableSurveySpecs.key+"-"+surveyParameters.screenName)
                        .commit();
            }
        }
        else if(fragment!=null ){
             fm=fragment.getChildFragmentManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(!fm.isStateSaved()){
                    fm.beginTransaction()
                            .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                    surveyParameters, false), loadableSurveySpecs.key+"-"+surveyParameters.screenName)
                            .commit();
                }
            }else{
                fm.beginTransaction()
                        .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                surveyParameters, false), loadableSurveySpecs.key+"-"+surveyParameters.screenName)
                        .commitAllowingStateLoss();
            }
        }
        if(activity!=null){
            if (activity instanceof FragmentActivity) {
                  xfm = ((FragmentActivity) activity).getSupportFragmentManager();
                if (!xfm.isStateSaved()) {
                    xfm.beginTransaction()
                            .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                    surveyParameters, false), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                            .commit();
                }

            } else {
                 fm = activity.getFragmentManager();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!fm.isStateSaved()) {
                        fm.beginTransaction()
                                .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                        surveyParameters, false), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }
                } else {
                    fm.beginTransaction()
                            .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                    surveyParameters, false), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                            .commitAllowingStateLoss();
                }

            }
        }
    }
private void loadSurveyFromActivity(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){

    if(!AliumSurveyActivity.isActivityRunning  || !activityInstanceCreated) {

//        if(Alium.isAppInForeground() && !((Activity)context).isFinishing()){
//            Intent intent = new Intent(context, AliumSurveyActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//            );
////                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
////                            );
//           if (activity!=null) activity.startActivity(intent);
//           else fragment.startActivity(intent);
//            activityInstanceCreated = true;
//        }



    }

    Handler handler=new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            if(Alium.isAppInForeground()){
//                Intent intent = new Intent("survey_content_fetched");
//                intent.putExtra("surveyJson", json.toString());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                );
//                intent.putExtra("loadableSurveySpecs", loadableSurveySpecs);
//                intent.putExtra("surveyParameters", surveyParameters);
//                intent.putExtra("canonicalClassName", ((Activity) context).getClass().getCanonicalName());
////                        context.sendBroadcast(intent);
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
////                Log.d("alium-activity", "is running");
            }
        }
    }, 500);



}
private void loadSurveyFromDialog(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){

//    Gson gson
//            =new Gson();
//                ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
//                        gson.fromJson(json.toString(), Survey.class)
//                    , loadableSurveySpecs);
////                ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
////                  );
//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    new SurveyDialog(context, executableSurveySpecs,
//                            surveyParameters, false)
//                            .show();
//                }
//            });
}

}