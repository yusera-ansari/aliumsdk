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


/**
 * AliumSurveyLoader is responsible for loading and displaying surveys within an Android application.
 * It observes changes and manages the lifecycle of survey dialogs.
 *
 * <p>Key functionalities include:
 * <ul>
 *   <li>Creating and managing instances of survey loaders for specific screens.</li>
 *   <li>Checking if a survey is already running to prevent duplicates.</li>
 *   <li>Fetching survey data from a network source.</li>
 *   <li>Determining if a survey should be loaded based on frequency settings.</li>
 *   <li>Displaying surveys using either {@link androidx.fragment.app.DialogFragment} (for {@link FragmentActivity})
 *       or {@link android.app.DialogFragment} (for older Activities).</li>
 *   <li>Handling the stopping and cleanup of survey loaders and their associated dialogs.</li>
 *   <li>Utilizing an {@link ExecutorService} for background operations like network requests.</li>
 *   <li>Communicating results and lifecycle events back to a {@link Callback}.</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * SurveyParameters params = new SurveyParameters("your_screen_name");
 * AliumSurveyLoader.Callback callback = new AliumSurveyLoader.Callback() {
 *     @Override
 *     public void onAliumLoaderExcecuted() {
 *         // Called when the loader has finished its execution (e.g., survey shown or decided not to show)
 *     }
 *
 *     @Override
 *     public void onQuitLoader(AliumSurveyLoader loader) {
 *         // Called when the loader is being stopped or cleaned up
 *     }
 * };
 *
 * // 'this' should be an Activity or FragmentActivity instance
 * AliumSurveyLoader loader = AliumSurveyLoader.createInstance(this, params, callback);
 * if (loader != null) {
 *     loader.showSurvey();
 * }
 * }</pre>
 *
 * <p>The loader ensures that only one survey is displayed per screen at a time. It uses
 * {@link AliumPreferences} to store and retrieve user-specific data like customer ID and
 * survey display frequencies.</p>
 *
 */
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

                    surveyDialogCallback.onStop(key);
                    Logger.log(Logger.LogLevel.DEBUG, "already-running", "survey already running, cancelling the request");
                    return true;
                }
            } else {
                fm = activity.get().getFragmentManager();
                android.app.Fragment fragment = fm.findFragmentByTag(key + "-" + surveyParameters.screenName);
                if (fragment != null) {
                    Logger.log(Logger.LogLevel.DEBUG, "already-running", "survey already running, cancelling the request");
                    surveyDialogCallback.onStop(key);
                    return true;
                }
            }
        }else{
                Logger.log(Logger.LogLevel.ERROR, "already-running", "activity instance is null, returning...");
                return true;
            }
        Logger.log(Logger.LogLevel.DEBUG, "already-running", "survey not running, proceeding the request");

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
            Logger.log(Logger.LogLevel.DEBUG, "show-surv", "find and load survey");
                    findAndLoadSurveyForCurrentScr();
        });


        executorService.shutdown(); //very imp
    }

    private void findAndLoadSurveyForCurrentScr() {
          List<SurInfo> svs=Alium.surveyConfig.getSvs();
        for(int i=0; i<svs.size(); i++){
            try {
                  String screenName =svs.get(i).getTps().getApp().getUm().getU();
                if (surveyParameters.screenName.equals(screenName)){
                    executingSurveys.add(svs.get(i).getId());

                    //check if its already running
//                    boolean alreadyRunning= checkIfSurveyAlreadyRunning(svs.get(i).getId());
//                    if(alreadyRunning){
//                        Logger.log(Logger.LogLevel.DEBUG, "find-&-load", "already running...returning");
//                        return;
//                    }
                    Logger.log(Logger.LogLevel.DEBUG, "find-&-load", "not running...proceeding");



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
                   .shouldSurveyLoad()){
                Logger.log(Logger.LogLevel.DEBUG, "should-load", "survey frequency handled..survey should load");
//               if(!checkIfSurveyAlreadyRunning(currentSurveyJson.getId())){
//                   Logger.log(Logger.LogLevel.DEBUG, "should-load", "survet not running...proceeding");

                   loadSurvey( new LoadableSurveySpecs(
                           currentSurveyJson.getId(), srvshowfrq, spath.toString(),
                           customFreqSurveyData
                   ));

//               }

           }else{
               Logger.log(Logger.LogLevel.DEBUG, "should-load", "survey frequency setting restricts loading...stopping....");
               surveyDialogCallback.onStop(currentSurveyJson.getId());
           }
       }catch (Exception e){
           Logger.log(Logger.LogLevel.ERROR,"should-load", e.toString());
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
                     loadSurveyFromDialogFragment(json, loadableSurveySpecs);
                 }
             });

              }

        }
    }
    private synchronized void loadSurveyFromDialogFragment(JSONObject json, LoadableSurveySpecs loadableSurveySpecs){

        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(
                AliumJSONParser.getSurveyFromJson(json)
                , loadableSurveySpecs);
            Logger.log(Logger.LogLevel.DEBUG, "load-dial", "checking if a dialog exists...");
        if(!checkIfSurveyAlreadyRunning(loadableSurveySpecs.key)){
            Logger.log(Logger.LogLevel.DEBUG, "load-dial", "creating a survey dialog...");

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