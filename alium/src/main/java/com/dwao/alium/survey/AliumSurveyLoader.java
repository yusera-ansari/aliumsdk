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

import com.dwao.alium.services.Logger;
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
//    Callback callback;
    private Set<String> executingSurveys=new HashSet<>();

    private void cleanUp(){
         activity=null;
         xfm=null;
          fm=null;
        executingSurveys.clear();
        executorService.shutdownNow();
        mainHandler.removeCallbacksAndMessages(null);

    }

    void handleExecutingKeys(String key){
                    if(executingSurveys.contains(key)) {
                executingSurveys.remove(key);
                if (executingSurveys.isEmpty()) {
                    Alium.stop(surveyParameters.screenName);
//                     AliumRequestManager.getManager().stop(surveyParameters.screenName);
                }
            }
    }

//    SurveyDialogCallback surveyDialogCallback
//            =new SurveyDialogCallback() {
//        @Override
//        public void onStop(String key) {
//            if(executingSurveys.contains(key)) {
//                executingSurveys.remove(key);
//                if (executingSurveys.isEmpty()) {
//                    callback.onQuitLoader(AliumSurveyLoader.this
//                    );
//                    callback.onAliumLoaderExcecuted();
//                }
//            }
//        }
//        @Override
//        public void onCreate(String key){
//        executingSurveys.add(key);
//        }
//    };
    public String getLoaderId(){
        return this.ID.toString();
    }
    private  AliumSurveyLoader(){}
//    interface  SurveyDialogCallback{
//        void onStop(String key);
//        void onCreate(String key);
//    }

    interface Callback{
        void  onAliumLoaderExcecuted();
        void onQuitLoader( AliumSurveyLoader loader);
    }

    public static AliumSurveyLoader createInstance(Object obj,SurveyParameters surveyParameters,
                                                    Callback callback){
        AliumSurveyLoader instance;

        instance= new AliumSurveyLoader((Activity) obj, surveyParameters);

//        instance.callback=callback;

        List<SurInfo> svs=Alium.surveyConfig.getSvs();
       for(int i=0; i<svs.size(); i++){
            try {
                String screenName = svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    //check if its already running, set instance to null if already present
                    if (instance != null ) {
                        Activity act = instance.activity != null ? instance.activity.get() : null;
                        if(act==null) {
                            instance=null;
                            Logger.log(Logger.LogLevel.INFO, "create-instance", "activity reference is null, returning...");
                            break;
                        };
                        if (act instanceof FragmentActivity) {
                            instance.xfm = ((FragmentActivity) act).getSupportFragmentManager();
                            Fragment fragment = instance.xfm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance = null;
                            }
                        } else {
                            instance.fm = act.getFragmentManager();
                            android.app.Fragment fragment = instance.fm.findFragmentByTag(svs.get(i).getId() + "-" + surveyParameters.screenName);
                            if (fragment != null) {
                                instance = null;
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

        Activity act = activity != null ? activity.get() : null;
            if(act !=null){
            if (act instanceof FragmentActivity) {
                xfm = ((FragmentActivity) act).getSupportFragmentManager();
                Fragment fragment = xfm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {

//                    surveyDialogCallback.onStop(key);
                    handleExecutingKeys(key);
                    Logger.log(Logger.LogLevel.DEBUG, "Loader.checkIfRunning", "survey already running, cancelling the request");
                    return true;
                }
            } else {
                fm = act.getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    Logger.log(Logger.LogLevel.DEBUG, "Loader.checkIfRunning", "survey already running, cancelling the request");
//                    surveyDialogCallback.onStop(key);
                    handleExecutingKeys(key);
                    return true;
                }
            }
        }else{
                Logger.log(Logger.LogLevel.ERROR, "Loader.checkIfRunning", "activity instance is null, returning...");
                return true;
            }
        Logger.log(Logger.LogLevel.DEBUG, "Loader.checkIfRunning", "survey not running, proceeding the request");

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
        //Runnable
        executorService.execute(this::findAndLoadSurveyForCurrentScr);
        executorService.shutdown(); //very imp
    }

    private void findAndLoadSurveyForCurrentScr() {
          List<SurInfo> svs=Alium.surveyConfig.getSvs();
        for(int i=0; i<svs.size(); i++){
                  String screenName =svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    //survey is not already running...
                   loadSurveyIfShouldBeLoaded(svs.get(i));
                   return; // limits survey to one on each screen
                }

        }
//        remove the instance of the loader from manager
        Logger.log(Logger.LogLevel.INFO,"INFO", "survey not found in config...stopping...");
    AliumRequestManager.getManager().stop(surveyParameters.screenName);
    }



    private void loadSurveyIfShouldBeLoaded(SurInfo currentSurveyJson)  {
       try{
           App ppupsrvObject = currentSurveyJson.getTps().getApp();
           Uri spath=Uri.parse(currentSurveyJson.getSpath());
           String srvshowfrq=ppupsrvObject.getVf();
           CustomFreqSurveyData customFreqSurveyData=null;

           //this feature isn't available yet
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


           if( FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,currentSurveyJson.getId(), srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad())
           {
                Logger.log(Logger.LogLevel.DEBUG, "shouldSurLoad", "survey frequency allows loading...");

                   loadSurvey( new LoadableSurveySpecs(
                           currentSurveyJson.getId(), srvshowfrq, spath.toString(),
                           customFreqSurveyData
                   ));

           }else{
               Logger.log(Logger.LogLevel.DEBUG, "shouldSurLoad", "survey frequency setting restricts loading...stopping....");
//               surveyDialogCallback.onStop(currentSurveyJson.getId());
               handleExecutingKeys(currentSurveyJson.getId());
           }
       }catch (Exception e){
           Logger.log(Logger.LogLevel.ERROR,"shouldSurLoad", e.toString());
       }
    }

    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        if(threadShouldExecute) {
            CustomNetworkService.getNetworkData(  loadableSurveySpecs.uri ,
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
        }

        @Override
        public void onResponseReceived(JSONObject json) {

              if(threadShouldExecute) {
             mainHandler.post(new Runnable() {
                 @Override
                 public void run() {

                     ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                             AliumJSONParser.getSurveyFromJson(json)
                             , loadableSurveySpecs);
                     loadSurveyFromDialogFragment(executableSurveySpecs);
                 }
             });

              }

        }

        @Override
        public void onRequestFailed(Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "LoaderNetReqFail", "Network request to survey load failed "+ e.toString());
            stop();
//            callback.onQuitLoader(AliumSurveyLoader.this);
            AliumRequestManager.manager.stop(surveyParameters.screenName);
        }
    }
    private synchronized void loadSurveyFromDialogFragment(ExecutableSurveySpecs executableSurveySpecs){
        LoadableSurveySpecs loadableSurveySpecs = executableSurveySpecs.getLoadableSurveySpecs();
        Logger.log(Logger.LogLevel.DEBUG, "Loader.Dial", "checking if a dialog exists...");

        executingSurveys.add(loadableSurveySpecs.key);

        if(!checkIfSurveyAlreadyRunning(loadableSurveySpecs.key)){
            Logger.log(Logger.LogLevel.DEBUG, "Loader.Dial", "creating a survey dialog...");
            Activity act = activity != null ? activity.get() : null;
            if (act != null) {
                if (act instanceof FragmentActivity) {
                    xfm = ((FragmentActivity) act).getSupportFragmentManager();
                    if (!xfm.isStateSaved()) {
                        xfm.beginTransaction()
                                .add(SurveyDialogFragment.newInstance(executableSurveySpecs,
                                                surveyParameters, false, getLoaderId()),
                                        loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commit();
                    }

                } else {
                    fm = act.getFragmentManager();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        if (!fm.isStateSaved()) {
                            fm.beginTransaction()
                                    .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                            surveyParameters, false, getLoaderId()), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                    .commit();
                        }
                    } else {

                        fm.beginTransaction()
                                .add(LegacySurveyDialogFragment.newInstance(executableSurveySpecs,
                                        surveyParameters, false, getLoaderId()), loadableSurveySpecs.key + "-" + surveyParameters.screenName)
                                .commitAllowingStateLoss();
                    }

                }
            }
        }

    }

}