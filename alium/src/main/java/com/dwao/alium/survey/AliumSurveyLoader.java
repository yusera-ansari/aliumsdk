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
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.CustomSurveyDetails;
import com.dwao.alium.models.Srv;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

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

import androidx.lifecycle.ProcessLifecycleOwner;
public class AliumSurveyLoader implements Observer {
    private Queue<LoadableSurveySpecs> loadableSurveySpecsQueue=new LinkedList<>();
    private boolean activityInstanceCreated=false;
    private boolean isSurveyFragmentLoading=false;
    private AliumPreferences aliumPreferences;
    private static Map<String, SurveyConfig> surveyConfigMap;
//    private  Context context;
    private volatile boolean threadShouldExecute=true;
    private WeakReference<Activity> activity;
    private WeakReference<Fragment> xfragment;
    private FragmentManager xfm;
    private android.app.FragmentManager fm;
    private WeakReference<android.app.Fragment >fragment;

    private static VolleyService volleyService=VolleyService.getInstance();
    private SurveyParameters surveyParameters;
    private final UUID ID=UUID.randomUUID();
    ExecutorService executorService;
    Handler mainHandler;
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
            Log.d("surveyDcallB", "on stop called "+executingSurveys);
            if(executingSurveys.contains(key)) {
                Log.d("surveyDcallB", "on stop called and key was present: "+key);
                executingSurveys.remove(key);
                if (executingSurveys.isEmpty()) {
                    Log.d("ExecutingSurv", "Executing surveys is Empty()");
                    Alium.updateExecLoaderData(getLoaderId(), surveyParameters.screenName);
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
                                                   Map<String, SurveyConfig> surveyConf,Callback callback){
        AliumSurveyLoader instance;
        if(obj instanceof Fragment){
          instance=   new AliumSurveyLoader((Fragment) obj, surveyParameters,surveyConf);
        }else if(obj instanceof android.app.Fragment){
            instance= new AliumSurveyLoader((android.app.Fragment)obj, surveyParameters,surveyConf);
        }
        else{
            instance= new AliumSurveyLoader((Activity) obj, surveyParameters,surveyConf);
        }
        instance.callback=callback;

        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                SurveyConfig jsonObject = surveyConfigMap.get(key);
                Srv ppupsrvObject = jsonObject.getSrv();
                String screenName = ppupsrvObject.getUrl();
                if (surveyParameters.screenName.equals(screenName)){
                    //check if its already running

                    if(instance.xfragment !=null){
                        instance.xfm=instance.xfragment.get().getChildFragmentManager();
                        Fragment fragment= instance.xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            instance= null;
                        }
                    }

                    else if(instance.fragment!=null){
                        instance.fm=instance.fragment.get().getChildFragmentManager();
                        android.app.Fragment fragment= instance.fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            instance= null;
                        }

                    } else if(instance.activity  !=null){
                        if (instance.activity.get() instanceof FragmentActivity) {
                            instance.xfm = ((FragmentActivity) instance.activity.get()).getSupportFragmentManager();
                            Fragment fragment = instance.xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance= null;
                            }
                        } else {
                            instance.fm = instance.activity.get().getFragmentManager();
                            android.app.Fragment fragment = instance.fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance= null;
                            }
                        }
                    }

                }

            } catch (Exception e) {
                Log.i("error", "inside catch block");
                e.printStackTrace();
                instance= null;

            }
        }
        Log.d("createLoader", "LOADER being returned is: "+instance);

     return instance;
    }

    private boolean checkIfSurveyAlreadyRunning(String key){
        Log.d("checkIfAlready", "checkIfSurveyAlreadyRunning called on: "+key);
        Log.d("checkIfAlready", "checkIfSurveyAlreadyRunning THREAD: "+ Thread.currentThread().getName());
        if(xfragment!=null){
            xfm=xfragment.get().getChildFragmentManager();
            Fragment fragment= xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
            if(fragment!=null){
               surveyDialogCallback.onStop(key);
//                callback.onQuitLoader(this);
//                callback.onAliumLoaderExcecuted();
                return true;
            }
        }
        else if(fragment!=null){
            fm=fragment.get().getChildFragmentManager();
            android.app.Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
            if(fragment!=null){
                surveyDialogCallback.onStop(key);
//                callback.onQuitLoader(this);
//                callback.onAliumLoaderExcecuted();
                return true;
            }
        } else if(activity !=null){
            if (activity.get() instanceof FragmentActivity) {
                xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    surveyDialogCallback.onStop(key);
//                    callback.onQuitLoader(this);
//                    callback.onAliumLoaderExcecuted();
                    return true;
                }
            } else {
                fm = activity.get().getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    surveyDialogCallback.onStop(key);
//                    callback.onQuitLoader(this);
//                    callback.onAliumLoaderExcecuted();
                    Log.d("Already", "Survey is already there!! "+ this);
                    return true;
                }
            }
        }
        Log.d("checkIfAlready", "checkIfSurveyAlreadyRunning called on: "+key);
        Log.d("checkIfAlready", "not runnning called on: "+key);
        return false;
    }
    public AliumSurveyLoader(Activity activity,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){
        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        this.activity= new WeakReference<>( activity);
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

        this.xfragment=new WeakReference<>(xfragment);
        aliumPreferences= AliumPreferences.getInstance(xfragment.getContext());
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }


    }
    public AliumSurveyLoader(android.app.Fragment fragment,SurveyParameters surveyParameters, Map<String, SurveyConfig> surveyConf){

        this.surveyParameters=surveyParameters;
        surveyConfigMap=surveyConf;
        this.fragment=new WeakReference<>(fragment);
        aliumPreferences= AliumPreferences.getInstance(fragment.getActivity());
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
        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                SurveyConfig jsonObject = surveyConfigMap.get(key);
                Srv ppupsrvObject = jsonObject.getSrv();
                String screenName = ppupsrvObject.getUrl();
                if (surveyParameters.screenName.equals(screenName)){
                    executingSurveys.add(key);
                    //check if its already running
                    if(fragment!=null){
                         fm=fragment.get().getChildFragmentManager();
                        android.app.Fragment fragment= fm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                        if(fragment!=null){
                            surveyDialogCallback.onStop(key);
//                            callback.onQuitLoader(this);
//                            callback.onAliumLoaderExcecuted();
                            continue;
                        }
                    } else
                        if(xfragment!=null){
                               xfm=xfragment.get().getChildFragmentManager();
                             Fragment fragment= xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
                            if(fragment!=null){
                                surveyDialogCallback.onStop(key);
//                                callback.onQuitLoader(this);
//                                callback.onAliumLoaderExcecuted();
                                continue;
                            }

                    }else if(activity!=null){
                            if (activity.get() instanceof FragmentActivity) {
                                  xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("findAndLoad", "and we return from here as survey fragment exists");
//                                    callback.onQuitLoader(this);
                                    surveyDialogCallback.onStop(key);
//                                    callback.onAliumLoaderExcecuted();
                                    continue;
                                }
                            } else {
                                fm = activity.get().getFragmentManager();
                                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    surveyDialogCallback.onStop(key);
//                                    callback.onQuitLoader(this);
//                                    callback.onAliumLoaderExcecuted();
                                    continue;
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
           Log.d("loadifshould", "loadSurveyIfShouldBeLoaded called on: "+surveyParameters.screenName+" key:"+key);
           Log.d("loadifshould", "Current THREAD: "+ Thread.currentThread().getName());
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
           if( FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,key, srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad()){
               Log.d("loadIF", "survey should be loaded...offerring....");
               if(!checkIfSurveyAlreadyRunning(key)){
                   loadableSurveySpecsQueue.offer(new LoadableSurveySpecs(
                           key, srvshowfrq, spath.toString(), thankyouObj,
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
//               callback.onQuitLoader(this);
               surveyDialogCallback.onStop(key);
//               callback.onAliumLoaderExcecuted();
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
                cleanUp();

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
            volleyService.callVolley(  surURL,
                    new LoadSurveyFromAPI(loadableSurveySpecs));
        }
    }

    @Override
    public synchronized void stop() { //stop means remove eevry fragment and destry loader object too
        threadShouldExecute=false;
        if(volleyService.getSurveyQueue()!=null){
            volleyService.getSurveyQueue().cancelAll(VolleyService.SURVEY_REQUEST_TAG);
        }
        executorService.shutdownNow();
//        handler.removeCallbacks();

//        if(fm!=null ){
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//              for(android.app.Fragment fragment:fm.getFragments()){
//
//                if(fragment.getTag()!=null &&fragment.getTag().contains(surveyParameters.screenName)){
//                    fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
//                }
//              }
//            }else{
//                for(android.app.Fragment fragment:fm.getFragments()){
//                    fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
//                }
//            }
//        }else if(xfm!=null){
//            if(executingSurveys.size()>0){
//                for(String key: executingSurveys){
//                    Fragment fragment=  xfm.findFragmentByTag(key+"-"+surveyParameters.screenName);
//                  if(fragment!=null)  xfm.beginTransaction().remove(fragment).commitAllowingStateLoss();
//                }
//            }
////                for(Fragment fragment:xfm.getFragments()){
////                    if(fragment.getTag().contains(surveyParameters.screenName)){
////                        xfm.beginTransaction().remove(fragment).commitAllowingStateLoss();
////                    }
////            }
//        }
        cleanUp();
    }


    class LoadSurveyFromAPI implements VolleyResponseListener{
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
                  loadSurveyFromDialogFragment(json, loadableSurveySpecs);
              }
//          }
//      });
        }
    }
    private synchronized void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){
        Gson gson
                =new Gson();
        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                gson.fromJson(json.toString(), Survey.class)
                , loadableSurveySpecs);

        if(!checkIfSurveyAlreadyRunning(loadableSurveySpecs.key)){
            if (activity != null) {
                if (xfragment != null) {
                    xfm = xfragment.get().getChildFragmentManager();
                    if (!xfm.isStateSaved()) {
                        xfm.beginTransaction()
                                .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                                surveyParameters, false, getLoaderId()),
                                        loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }
                } else if (fragment != null) {
                    fm = fragment.get().getChildFragmentManager();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!fm.isStateSaved()) {
                            fm.beginTransaction()
                                    .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                                    surveyParameters, false
                                            ),
                                            loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                    .commit();
                        }
                    } else {
                        fm.beginTransaction()
                                .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                        surveyParameters, false), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commitAllowingStateLoss();
                    }
                }
                if (activity.get() instanceof FragmentActivity) {
                    xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                    if (!xfm.isStateSaved()) {
                        xfm.beginTransaction()
                                .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                                surveyParameters, false, getLoaderId()),
                                        loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }

                } else {
                    fm = activity.get().getFragmentManager();
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
        }Log.d("loadDial()", "loadSurveyFromDialogFragment called on: "+surveyParameters.screenName);
        Log.d("loadDial", "loadSurveyFromDialogFragmentTHREAD: "+ Thread.currentThread().getName());
        isSurveyFragmentLoading=false;
        executeNextSurvey();
    }

}