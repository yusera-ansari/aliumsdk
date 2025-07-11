package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
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
import com.dwao.alium.models.CustomFreqSurveyData;
import com.dwao.alium.models.CustomSurveyDetails;
import com.dwao.alium.models.ExecutableSurveySpecs;
import com.dwao.alium.models.LoadableSurveySpecs;
import com.dwao.alium.models.SurInfo;
import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.network.CustomNetworkService;

import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;
import com.dwao.alium.utils.preferences.AliumPreferences;


import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class AliumSurveyLoader implements Observer {

    private AliumPreferences aliumPreferences;



    private volatile boolean threadShouldExecute=true;
    private WeakReference<Activity> activity;

    private FragmentManager xfm;
    private android.app.FragmentManager fm;


    private SurveyParameters surveyParameters;
    private final UUID ID=UUID.randomUUID();
    private ExecutorService executorService;
    private Handler mainHandler;
    Callback callback;
    private Set<String> executingSurveys=new HashSet<>();

    private void cleanUp(){
         activity=null;
         xfm=null;
          fm=null;

    }

    SurveyDialogCallback surveyDialogCallback
            =new SurveyDialogCallback() {
        @Override
        public void onStop(String key) {
            if(executingSurveys.contains(key)) {
                executingSurveys.remove(key);
                if (executingSurveys.isEmpty()) {
                    callback.onQuitLoader(AliumSurveyLoader.this
                    );
                    callback.onAliumLoaderExcecuted();
                }
            }
            Log.d("ExecutingSurv", "Executing surveys is "+executingSurveys);
        }
        @Override
        public void onCreate(String key){
            Log.d("oncreate", "oncreaye callback called");
        executingSurveys.add(key);
            Log.d("oncreate", "oncreaye callback called"+executingSurveys.toString());
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

        instance= new AliumSurveyLoader((Activity) obj, surveyParameters);

        instance.callback=callback;

        List<SurInfo> svs=Alium.surveyConfig.getSvs();
       for(int i=0; i<svs.size(); i++){
            try {
                String screenName = svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    //check if its already running

                        if(instance.activity  !=null){
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

            if(activity !=null){

            if (activity.get() instanceof FragmentActivity) {
                xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    Log.d("onStop", "survey already running...calling stop");
                    surveyDialogCallback.onStop(key);
                    return true;
                }
            } else {
                fm = activity.get().getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    Log.d("onStop", "survey already running...calling stop");
                    surveyDialogCallback.onStop(key);
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
        this.aliumPreferences= AliumPreferences.getInstance();
        this.executorService= Executors.newSingleThreadExecutor();
        this.mainHandler=new Handler(Looper.getMainLooper());
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
                findAndLoadSurveyForCurrentScr();
        });


        executorService.shutdown(); //very imp
    }

    private void findAndLoadSurveyForCurrentScr() {
        Log.d("findAndLoad", "findAndLoadSurveyForCurrentScr called on: "+surveyParameters.screenName);
        List<SurInfo> svs=Alium.surveyConfig.getSvs();
        for(int i=0; i<svs.size(); i++){
            try {
                Log.d("inside for", "item: "+ svs.get(i).getTps().getApp().getUm().getU());
                String screenName =svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    executingSurveys.add(svs.get(i).getId());
                    //check if its already running

                        if(activity!=null){
                            if (activity.get() instanceof FragmentActivity) {
                                  xfm = ((FragmentActivity) activity.get()).getSupportFragmentManager();
                                Fragment fragment = xfm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("onStop", "survey already running...calling stop");
                                    Log.d("findAndLoad", "and we return from here as survey fragment exists");
                                    surveyDialogCallback.onStop(svs.get(i).getId());
                                    return;
                                }
                            } else {
                                fm = activity.get().getFragmentManager();
                                android.app.Fragment fragment = fm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                                if (fragment != null) {
                                    Log.d("onStop", "survey already running...calling stop");
                                    surveyDialogCallback.onStop(svs.get(i).getId());
                                    return;
                                }
                            }
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

               if(!checkIfSurveyAlreadyRunning(currentSurveyJson.getId())){
//                   loadableSurveySpecsQueue.offer(new LoadableSurveySpecs(
//                           currentSurveyJson.getId(), srvshowfrq, spath.toString(),
//                           customFreqSurveyData
//                   ));

                                  loadSurvey( new LoadableSurveySpecs(
                           currentSurveyJson.getId(), srvshowfrq, spath.toString(),
                           customFreqSurveyData
                   ));

               }

           }else{
               surveyDialogCallback.onStop(currentSurveyJson.getId());
           }
       }catch (Exception e){
           Log.e("loadSurveyIfShouldLoad", e.toString());
       }
    }

    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        Log.d("loadSurvey", "loadSurvey called on: "+surveyParameters.screenName);
        String surURL=loadableSurveySpecs.uri.toString();
        if(threadShouldExecute) {
            CustomNetworkService.getNetworkData(  surURL ,
                    new LoadSurveyFromAPI(loadableSurveySpecs));
        }
    }

    @Override
    public synchronized void stop() { //stop means remove every fragment and destroy loader object too
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
        }

        @Override
        public void onResponseReceived(JSONObject json) {

              Log.d("run()", "run() called on: "+surveyParameters.screenName);
              if(threadShouldExecute) {
                  Log.d("run()", "threadShouldExecute called on: "+surveyParameters.screenName);
             mainHandler.post(new Runnable() {
                 @Override
                 public void run() {
                     Log.d("load()", "loadable: "+Thread.currentThread().getName());
                     loadSurveyFromDialogFragment(json, loadableSurveySpecs);
                 }
             });

              }

        }
    }
    private synchronized void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){
        Log.d("Survey", "survey: "+json);
        Log.d("Survey", "survey: "+ AliumJSONParser.getSurveyFromJson(json));

        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                AliumJSONParser.getSurveyFromJson(json)
                , loadableSurveySpecs);

        if(!checkIfSurveyAlreadyRunning(loadableSurveySpecs.key)){
            if (activity != null) {
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
//        isSurveyFragmentLoading=false;
//        executeNextSurvey();
    }

}