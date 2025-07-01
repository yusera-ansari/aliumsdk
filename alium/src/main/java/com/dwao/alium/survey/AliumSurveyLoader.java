package com.dwao.alium.survey;

import static com.dwao.alium.survey.Alium.surveyConfig;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.listeners.Observer;
import com.dwao.alium.listeners.ResponseListener;
import com.dwao.alium.models.App;
import com.dwao.alium.models.CustomSurveyDetails;
import com.dwao.alium.models.Srv;
import com.dwao.alium.models.SurConf;
import com.dwao.alium.models.SurInfo;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TypeOfSur;
import com.dwao.alium.models.UrlMatch;
import com.dwao.alium.network.CustomNetworkService;

import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;
import com.dwao.alium.utils.preferences.AliumPreferences;


import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class AliumSurveyLoader implements Observer {
    private Queue<LoadableSurveySpecs> loadableSurveySpecsQueue=new LinkedList<>();
    private boolean isSurveyFragmentLoading=false;
    private AliumPreferences aliumPreferences;



    private volatile boolean threadShouldExecute=true;
    private WeakReference<Activity> activity;
    private WeakReference<Fragment> xfragment;
    private FragmentManager xfm;
    private android.app.FragmentManager fm;
    private WeakReference<android.app.Fragment >fragment;


    private SurveyParameters surveyParameters;
    private final UUID ID=UUID.randomUUID();
    private ExecutorService executorService;
    private Handler mainHandler;
    Callback callback;
    private Set<String> executingSurveys=new HashSet<>();

    private void cleanUp(){
         activity=null;
        xfragment=null;
         xfm=null;
          fm=null;
         fragment=null;

    }

    SurveyDialogCallback surveyDialogCallback
            =new SurveyDialogCallback() {
        @Override
        public void onStop(String key) {
            Log.d("executingSurveys", "on stop called executing-surveys:"+executingSurveys);
            if(executingSurveys.contains(key)) {
                Log.d("surveyDcallB", "on stop called and key was present: "+key);
                executingSurveys.remove(key);
                if (executingSurveys.isEmpty()) {
                    Log.d("ExecutingSurv", "Executing surveys is Empty()");
//                    Alium.updateExecLoaderData(getLoaderId(), surveyParameters.screenName);
                    callback.onQuitLoader(AliumSurveyLoader.this
                    );
                    callback.onAliumLoaderExcecuted();
                }
            }
        }
        @Override
        public void onCreate(String key){
            executingSurveys.add(key);
        }
    };
    public String getLoaderId(){
        return this.ID.toString();
    }
    private  AliumSurveyLoader(){}
    interface  SurveyDialogCallback{
        void onStop(String key);
        void onCreate(String key);
    }

    interface Callback{
        void  onAliumLoaderExcecuted();
        void onQuitLoader( AliumSurveyLoader loader);
    }

    public static AliumSurveyLoader createInstance(Object obj,SurveyParameters surveyParameters,
                                                    Callback callback){
        AliumSurveyLoader instance;
        //convert back to frag/activity
        if(obj instanceof Fragment){ //androidx
          instance=   new AliumSurveyLoader((Fragment) obj, surveyParameters);
        }else if(obj instanceof android.app.Fragment){
            instance= new AliumSurveyLoader((android.app.Fragment)obj, surveyParameters);
        }
        else{
            instance= new AliumSurveyLoader((Activity) obj, surveyParameters);
        }
        instance.callback=callback;

        List<SurInfo> svs=Alium.surveyConfig.getSvs();
       for(int i=0; i<svs.size(); i++){
            try {
                String screenName = svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    //check if its already running

                    if(instance.xfragment !=null){
                        instance.xfm=instance.xfragment.get().getChildFragmentManager();
                        Fragment fragment= instance.xfm.findFragmentByTag(svs.get(i).getId()+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            instance= null;
                        }
                    }

                    else if(instance.fragment!=null){
                        instance.fm=instance.fragment.get().getChildFragmentManager();
                        android.app.Fragment fragment= instance.fm.findFragmentByTag(svs.get(i).getId()+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            instance= null;
                        }

                    } else if(instance.activity  !=null){
                        if (instance.activity.get() instanceof FragmentActivity) {
                            instance.xfm = ((FragmentActivity) instance.activity.get()).getSupportFragmentManager();
                            Fragment fragment = instance.xfm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance= null;
                            }
                        } else {
                            instance.fm = instance.activity.get().getFragmentManager();
                            android.app.Fragment fragment = instance.fm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance= null;
                            }
                        }
                    }

                }

            } catch (Exception e) {

                e.printStackTrace();
                instance= null;

            }
        }

     return instance; //return final instance to the queue
    }

    private boolean checkIfSurveyAlreadyRunning(String key){
         if(xfragment!=null){
            xfm=xfragment.get().getChildFragmentManager();
            Fragment fragment= xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
            if(fragment!=null){
               surveyDialogCallback.onStop(key);
                return true;
            }
        }
        else if(fragment!=null){
            fm=fragment.get().getChildFragmentManager();
            android.app.Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
            if(fragment!=null){
                surveyDialogCallback.onStop(key);
                return true;
            }
        } else if(activity !=null){
            if (activity.get() instanceof FragmentActivity) {
                xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    surveyDialogCallback.onStop(key);
                    return true;
                }
            } else {
                fm = activity.get().getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    surveyDialogCallback.onStop(key);
                    Log.d("Already", "Survey is already there!! "+ this);
                    return true;
                }
            }
        }
        Log.d("checkIfAlready", "checkIfSurveyAlreadyRunning called on: "+key);
        Log.d("checkIfAlready", "not runnning called on: "+key);
        return false;
    }
    public AliumSurveyLoader(Activity activity,SurveyParameters surveyParameters){
        this.surveyParameters=surveyParameters;

        this.activity= new WeakReference<>( activity);
        aliumPreferences= AliumPreferences.getInstance();
        this.executorService= Executors.newSingleThreadExecutor();
        this.mainHandler=new Handler(Looper.getMainLooper());
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
    }
    public AliumSurveyLoader(Fragment xfragment,SurveyParameters surveyParameters){
        this.surveyParameters=surveyParameters;
        this.executorService= Executors.newSingleThreadExecutor();
        this.mainHandler=new Handler(Looper.getMainLooper());
        this.xfragment=new WeakReference<>(xfragment);
        aliumPreferences= AliumPreferences.getInstance( );
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
    }
    public AliumSurveyLoader(android.app.Fragment fragment,SurveyParameters surveyParameters){
        this.surveyParameters=surveyParameters;
        this.executorService= Executors.newSingleThreadExecutor();
        this.mainHandler=new Handler(Looper.getMainLooper());
        this.fragment=new WeakReference<>(fragment);
        aliumPreferences= AliumPreferences.getInstance( );
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public synchronized void showSurvey(){
        executorService.execute(()->{ //Runnable
                Log.d("showSurvey", "ShowSurvey called on: "+surveyParameters.screenName);
                Log.d("showSurvey", "Current THREAD: "+ Thread.currentThread().getName());
                findAndLoadSurveyForCurrentScr();
        });


        executorService.shutdown(); //very imp
    }

    private void findAndLoadSurveyForCurrentScr() {
        Log.d("findAndLoad", "findAndLoadSurveyForCurrentScr called on: "+surveyParameters.screenName);
        Log.d("findAndLoad", "Current THREAD: "+ Thread.currentThread().getName());
        List<SurInfo> svs=Alium.surveyConfig.getSvs();
        for(int i=0; i<svs.size(); i++){
            try {
                Log.d("inside for", "item: "+ svs.get(i).getTps().getApp().getUm().getU());
                String screenName =svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    executingSurveys.add(svs.get(i).getId());
                    //check if its already running
                    if(fragment!=null){
                         fm=fragment.get().getChildFragmentManager();
                        android.app.Fragment fragment= fm.findFragmentByTag(svs.get(i).getId()+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            surveyDialogCallback.onStop(svs.get(i).getId());
                            continue;
                        }
                    } else
                        if(xfragment!=null){
                               xfm=xfragment.get().getChildFragmentManager();
                             Fragment fragment= xfm.findFragmentByTag(svs.get(i).getId()+"-"+surveyParameters.screenName);
                            if(fragment!=null){
                                surveyDialogCallback.onStop(svs.get(i).getId());
                                continue;
                            }

                    }else if(activity!=null){
                            if (activity.get() instanceof FragmentActivity) {
                                  xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                                Fragment fragment = xfm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("findAndLoad", "and we return from here as survey fragment exists");
                                    surveyDialogCallback.onStop(svs.get(i).getId());
                                    continue;
                                }
                            } else {
                                fm = activity.get().getFragmentManager();
                                android.app.Fragment fragment = fm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    surveyDialogCallback.onStop(svs.get(i).getId());
                                    continue;
                                }
                            }
                        }else{
                            continue;
                        }
                    //survey is not already running...
                   loadSurveyIfShouldBeLoaded(svs.get(i));
                        return; // limits survey to one on each screen
                }
            } catch (Exception e) {
                Log.i("error", "inside catch block");
                e.printStackTrace();
            }
        }
    }


    //key is survey id
    private void loadSurveyIfShouldBeLoaded(SurInfo currentSurveyJson)  {
       try{
           Log.d("loadifshould", "loadSurveyIfShouldBeLoaded called on: "+surveyParameters.screenName+" key:"+currentSurveyJson.getId());
           Log.d("loadifshould", "Current THREAD: "+ Thread.currentThread().getName());
           App ppupsrvObject = currentSurveyJson.getTps().getApp();
           Uri spath=Uri.parse(currentSurveyJson.getSpath());
//           Log.d("URI", spath.toString());
           String srvshowfrq=ppupsrvObject.getVf();
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

//           String thankyouObj = ppupsrvObject.getThankYouMsg();
           if( FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,currentSurveyJson.getId(), srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad()){
               Log.d("loadIF", "survey should be loaded...offerring....");
               if(!checkIfSurveyAlreadyRunning(currentSurveyJson.getId())){
                   loadableSurveySpecsQueue.offer(new LoadableSurveySpecs(
                           currentSurveyJson.getId(), srvshowfrq, spath.toString(),
                           customFreqSurveyData
                   ));
                   Log.d("ExecureNext", "...calling execute next on survey");
                   executeNextSurvey();
               }
//               loadSurvey( new LoadableSurveySpecs(
//                       key, srvshowfrq, spath.toString(), thankyouObj,
//                       customFreqSurveyData
//               ));
           }else{
               surveyDialogCallback.onStop(currentSurveyJson.getId());
           }
       }catch (Exception e){
           Log.e("loadSurveyIfShouldLoad", e.toString());
       }
    }
    private synchronized void executeNextSurvey(){
        Log.d("ExecNext", "executeNextSurvey called on: "+surveyParameters.screenName);
        Log.d("ExecNext", "Current THREAD: "+ Thread.currentThread().getName());
        if(isSurveyFragmentLoading||loadableSurveySpecsQueue.isEmpty()){
            if(loadableSurveySpecsQueue.isEmpty()){
                callback.onAliumLoaderExcecuted();
//                cleanUp();

                Log.d("LoaderComplete", "Loader is complete loadable surveys called!!");
            }
            Log.d("ExecNext","A survey is loading..please wait...returning!!" );
            return;
        }
        isSurveyFragmentLoading=true;
        LoadableSurveySpecs currSpecs=loadableSurveySpecsQueue.poll();
       if(currSpecs!=null) loadSurvey(currSpecs);
    }
    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        Log.d("loadSurvey", "loadSurvey called on: "+surveyParameters.screenName);
        Log.d("loadSurvey", "loadSurvey THREAD: "+ Thread.currentThread().getName());
        String surURL=loadableSurveySpecs.uri.toString();
        if(threadShouldExecute) {
            CustomNetworkService.getNetworkData(  surURL,
                    new LoadSurveyFromAPI(loadableSurveySpecs));
        }
    }

    @Override
    public synchronized void stop() { //stop means remove eevry fragment and destry loader object too
        threadShouldExecute=false;

        executorService.shutdownNow();
        if(fm!=null ){
            if(executingSurveys.size()>0){
                for(String key: executingSurveys){
                    android.app.Fragment fragment=  fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                    if(fragment!=null)  fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        }else if(xfm!=null){
            if(executingSurveys.size()>0){
                for(String key: executingSurveys){
                    Fragment fragment=  xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                  if(fragment!=null)  xfm.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }

        }
        cleanUp();
    }


    class LoadSurveyFromAPI implements ResponseListener {
        LoadableSurveySpecs loadableSurveySpecs;
        private LoadSurveyFromAPI(){}
        public LoadSurveyFromAPI(LoadableSurveySpecs loadableSurveySpecs) {
            this.loadableSurveySpecs=loadableSurveySpecs;
            Log.d("loadSurveyAPI", "loadSurveyFROMAPI called on: "+surveyParameters.screenName);
            Log.d("loadSurveyAPI", "loadSurveyAPI THREAD: "+ Thread.currentThread().getName());
        }

        @Override
        public void onResponseReceived(JSONObject json) {
//        mainHandler.post(new Runnable() {
//          @Override
//          public void run() {
              Log.d("run()", "run() called on: "+surveyParameters.screenName);
              Log.d("run()", "run() THREAD: "+ Thread.currentThread().getName());
              if(threadShouldExecute) {
                  Log.d("run()", "threadShouldExecute called on: "+surveyParameters.screenName);
                  Log.d("run()", "threadShouldExecute THREAD: "+ Thread.currentThread().getName());
             mainHandler.post(new Runnable() {
                 @Override
                 public void run() {
                     Log.d("load()", "loadable: "+Thread.currentThread().getName());
                     loadSurveyFromDialogFragment(json, loadableSurveySpecs);
                 }
             });

              }
//          }
//      });
        }
    }
    private synchronized void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){
//        Gson gson
//                =new Gson();
//        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
//                gson.fromJson(json.toString(), Survey.class)
//                , loadableSurveySpecs);
        Log.d("Survey", "survey: "+json);
        Log.d("Survey", "survey: "+ AliumJSONParser.getSurveyFromJson(json));

        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                AliumJSONParser.getSurveyFromJson(json)
                , loadableSurveySpecs);

        if(!checkIfSurveyAlreadyRunning(loadableSurveySpecs.key)){
            Log.e("instance", "Of fragment activity activity??");

                Log.e("instance", "Of fragment activity not null...");
                if (xfragment != null) {
                    Log.e("instance", " xfragment!=null Of fragment activity");
                    xfm = xfragment.get().getChildFragmentManager();
                    if (!xfm.isStateSaved()) {
                        xfm.beginTransaction()
                                .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                                surveyParameters, false, getLoaderId()),
                                        loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }
                } else if (fragment != null) {
                    Log.e("instance", "fragment != null Of fragment activity");
                    fm = fragment.get().getChildFragmentManager();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!fm.isStateSaved()) {
                            fm.beginTransaction()
                                    .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                                    surveyParameters, false, getLoaderId()
                                            ),
                                            loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                    .commit();
                        }
                    } else {
                        fm.beginTransaction()
                                .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                        surveyParameters, false, getLoaderId()), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commitAllowingStateLoss();
                    }
                }
            if (activity != null) {
                if (activity.get() instanceof FragmentActivity) {
                    Log.e("instance", "Of fragment activity");
                    xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                    if (!xfm.isStateSaved()) {
                        Log.e("instance", "Of fragment activity begin transaction");
                        xfm.beginTransaction()
                                .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                                surveyParameters, false, getLoaderId()),
                                        loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }
                    Log.e("instance", "Of fragment activity outside !xfm");

                } else {
                    Log.e("instance", "else Of fragment activity");
                    fm = activity.get().getFragmentManager();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.e("instance", "Of else -1 fragment activity");
                        if (!fm.isStateSaved()) {
                            fm.beginTransaction()
                                    .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                            surveyParameters, false, getLoaderId()), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                    .commit();
                        }
                    } else {
                        Log.e("instance", "else else Of fragment activity");
                        fm.beginTransaction()
                                .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                        surveyParameters, false, getLoaderId()), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commitAllowingStateLoss();
                    }

                }
            }
        }
        Log.d("loadDial()", "loadSurveyFromDialogFragment called on: "+surveyParameters.screenName);
        Log.d("loadDial", "loadSurveyFromDialogFragmentTHREAD: "+ Thread.currentThread().getName());
        isSurveyFragmentLoading=false;
        executeNextSurvey();
    }

}