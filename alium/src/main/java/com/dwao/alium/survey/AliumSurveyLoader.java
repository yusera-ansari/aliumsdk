package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.CustomSurveyDetails;
import com.dwao.alium.models.Srv;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import androidx.lifecycle.ProcessLifecycleOwner;
public class AliumSurveyLoader {
    private boolean activityInstanceCreated=false;
    AliumPreferences aliumPreferences;
    private static Map<String, SurveyConfig> surveyConfigMap;

    private  Context context;

    private static VolleyService volleyService;

    private SurveyParameters surveyParameters;


    private  AliumSurveyLoader(){}
    public AliumSurveyLoader(Context context,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){

        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        volleyService=new VolleyService();
        this.context=context;
        aliumPreferences= AliumPreferences.getInstance(context);
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }


    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public void showSurvey( ){
//        Log.d("Alium", "showing survey on :"+surveyParameters.screenName);
        findAndLoadSurveyForCurrentScr();
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
                        if(context instanceof FragmentActivity) {
                            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                            Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                            if(fragment!=null){
                                Log.d("activeSurvey", "survey existes");
                                return;
                            }
                        }else{

                            android.app.FragmentManager fm = ((Activity) context).getFragmentManager();
                            android.app.Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                            if(fragment!=null){
                                Log.d("activeSurvey", "survey existes");
                                return;
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
        volleyService.callVolley(context, surURL,new LoadSurveyFromAPI(loadableSurveySpecs) );
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
           loadSurveyFromDialogFragment(json, loadableSurveySpecs);
        }
    }
    private void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){
        Gson gson
                =new Gson();
        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                gson.fromJson(json.toString(), Survey.class)
                , loadableSurveySpecs);

        if(context instanceof FragmentActivity){
            FragmentManager fm=((FragmentActivity)context).getSupportFragmentManager();
            if(!fm.isStateSaved()){
                fm.beginTransaction()
                        .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                surveyParameters, false), loadableSurveySpecs.key+"-"+surveyParameters.screenName)
                        .commit();
            }

        }else{
            android.app.FragmentManager fm=((Activity)context).getFragmentManager();
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
    }
private void loadSurveyFromActivity(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){

    if(!AliumSurveyActivity.isActivityRunning  || !activityInstanceCreated) {

        if(Alium.isAppInForeground() && !((Activity)context).isFinishing()){
            Intent intent = new Intent(context, AliumSurveyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            );
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                            );
            context.startActivity(intent);
            activityInstanceCreated = true;
        }



    }

    Handler handler=new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            if(Alium.isAppInForeground()){
                Intent intent = new Intent("survey_content_fetched");
                intent.putExtra("surveyJson", json.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                );
                intent.putExtra("loadableSurveySpecs", loadableSurveySpecs);
                intent.putExtra("surveyParameters", surveyParameters);
                intent.putExtra("canonicalClassName", ((Activity) context).getClass().getCanonicalName());
//                        context.sendBroadcast(intent);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                Log.d("alium-activity", "is running");
            }
        }
    }, 500);



}
private void loadSurveyFromDialog(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){

    Gson gson
            =new Gson();
                ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                        gson.fromJson(json.toString(), Survey.class)
                    , loadableSurveySpecs);
//                ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                  );
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    new SurveyDialog(context, executableSurveySpecs,
                            surveyParameters, false)
                            .show();
                }
            });
}

}