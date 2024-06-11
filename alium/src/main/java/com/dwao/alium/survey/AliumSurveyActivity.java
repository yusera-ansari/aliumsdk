package com.dwao.alium.survey;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.R;
import com.dwao.alium.models.Survey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AliumSurveyActivity extends AppCompatActivity {
    public static boolean isActivityRunning = false;
//    private static String canonicalClassName="";
//    private static List<String> activeSurveyList;
//    private static List<SurveyDialog> activeSurveyDialogs;
    private static Map<String, List<SurveyDialog>> activeSurveyDetails=new HashMap<>();

    protected List<SurveyDialog> removeSurveyFromActiveList(SurveyDialog surveyDialog){
//        activeSurveyList.remove(surveyDialog.executableSurveySpecs.getLoadableSurveySpecs().key);
//        activeSurveyDialogs.remove(surveyDialog);
        Log.d("removeFromActiveList", Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
       List<SurveyDialog> list= activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
       list.remove(surveyDialog);
//
        return list;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Instance", "on config changed");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("activeSurveys111", activeSurveyDetails.toString());
        Log.d("Instance", "on save state");
       if((activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName())!=null)&&
               !activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()).isEmpty()){
           List<SurveyDialog> list= activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
           Iterator surveyDialogIterator= list.iterator();
           while (surveyDialogIterator.hasNext()){
               SurveyDialog surveyDialog= (SurveyDialog) surveyDialogIterator.next();
               Map<String, Serializable> map=new HashMap<>();
               map.put("surveyParameters", surveyDialog.surveyParameters);
               map.put("loadableSurveySpecs", surveyDialog.executableSurveySpecs.getLoadableSurveySpecs());
               map.put("surveyJson", surveyDialog.
                       executableSurveySpecs.getJson().toString());

               outState.putSerializable(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName(), (Serializable) map);
               surveyDialog.dialog.dismiss();
               surveyDialogIterator.remove();

           }
       }

        Log.d("activeSurveys111", activeSurveyDetails.toString());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("Instance", "on restore state"+Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()+
                savedInstanceState.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()));
        Map<String, Serializable> map= (Map<String, Serializable>) savedInstanceState.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
        SurveyParameters surveyParameters=(SurveyParameters) map.get("surveyParameters");
        LoadableSurveySpecs loadableSurveySpecs=
                (LoadableSurveySpecs)map.get("loadableSurveySpecs");
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(map.get("surveyJson").toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(jsonObject
                , loadableSurveySpecs);
//                activeSurveyList.add(loadableSurveySpecs.key);
//                Log.i("activesurveylist", activeSurveyList.toString());
        SurveyDialog surveyDialog=new SurveyDialog(this, executableSurveySpecs,
                surveyParameters)
                ;
        Log.d("activeSurveys", activeSurveyDetails.toString());
        if(!activeSurveyDetails.isEmpty()){
            if(!Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName().isEmpty()){
                if(!(activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()) ==null)){
                    Log.d("restore", "activeList is null "+Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
                    List<SurveyDialog> surveyDialogList=activeSurveyDetails
                            .get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
                    surveyDialogList.add(surveyDialog);
                }else{
                    Log.d("restore", "activeList is noy null "+Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
                    List<SurveyDialog > list=new ArrayList<>();
                    list.add(surveyDialog);
                    activeSurveyDetails.put(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName(), list);
                }
            }
        }
          Log.d("show", "activeList is show "+Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
            surveyDialog.show();
//                   activeSurveyDialogs.add(surveyDialog);

    }

    private BroadcastReceiver activityResumedBoradcast=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("activityResudBoradcast", ""+intent+" "+isFinishing()+" "+isDestroyed());
            if(intent!=null && !isDestroyed() && !isFinishing()) {
                Log.d("activy_resumed", "insideif");

                String contextActivityCanonicalName=intent.getStringExtra("canonicalName");
                Log.d("context", context.toString());
               if(activeSurveyDetails!=null && activeSurveyDetails.get(contextActivityCanonicalName)
                       !=null){
                   List<SurveyDialog> list=activeSurveyDetails.get(contextActivityCanonicalName);
                   if(!list.isEmpty() && !isActivityRunning){
                      Iterator<SurveyDialog> iterator=list.iterator();
                      List<SurveyDialog> newList=new ArrayList<>();
                      while(iterator.hasNext()){
                          SurveyDialog next=iterator.next();
                          SurveyDialog surveyDialog=new SurveyDialog(context,
                                  next.executableSurveySpecs,
                                  next.surveyParameters
                                  );
                          iterator.remove();
                          newList.add(surveyDialog);
                          surveyDialog.show();
                      }
                      activeSurveyDetails.put(contextActivityCanonicalName,
                              newList);
                   }
               }
            }
        }
    };
    static void checkForExistingSurveys(Activity currentActivity){
            String contextActivityCanonicalName=(
                    currentActivity

            ).getClass().getCanonicalName();

            if(activeSurveyDetails!=null && activeSurveyDetails.get(contextActivityCanonicalName)
                    !=null){
                List<SurveyDialog> list=activeSurveyDetails.get(contextActivityCanonicalName);
                if(!list.isEmpty() && !isActivityRunning){
                  try{
                      Intent intent1=new Intent(currentActivity, AliumSurveyActivity.class);
                      intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      currentActivity.startActivity(intent1);
                  }catch(Exception e){
                      Log.i("Exception", "CheckForExisting ACtivities, couldn't pass intent");
                  }

                }
            }
    }
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && !isDestroyed() && !isFinishing()){
                        SurveyParameters surveyParameters=(SurveyParameters) intent
                .getSerializableExtra("surveyParameters");
                      String  canonicalClassName=intent.getStringExtra("canonicalClassName");
                LoadableSurveySpecs loadableSurveySpecs=
                        (LoadableSurveySpecs) intent.getSerializableExtra("loadableSurveySpecs");
                JSONObject jsonObject;
                try {
                      jsonObject=new JSONObject(intent.getStringExtra("surveyJson"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(jsonObject
                        , loadableSurveySpecs);

                    if(!canonicalClassName.isEmpty()){
                        if( !(activeSurveyDetails
                                .get(canonicalClassName)==null) ) {
                            List<SurveyDialog> surveyDialogList=activeSurveyDetails
                                    .get(canonicalClassName);
                            Iterator<SurveyDialog> iterator=surveyDialogList.iterator();
                            while (iterator.hasNext()){
                                SurveyDialog surveyDialog=iterator.next();

                                if(surveyDialog.executableSurveySpecs.getLoadableSurveySpecs()
                                        .key.equals(loadableSurveySpecs.key)){
                                    if (Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()
                                            .equals(canonicalClassName)) {

                                        iterator.remove();
                                    }
                                    Log.d("activeSurveys333", activeSurveyDetails.toString());

                                    //                activeSurveyList.add(loadableSurveySpecs.key);
//                Log.i("activesurveylist", activeSurveyList.toString());
                                    SurveyDialog surveyDialog2 = new SurveyDialog(context, executableSurveySpecs,
                                            surveyParameters);

                                    surveyDialogList.add(surveyDialog2);

                                    Log.d("canonicalName", canonicalClassName + " " +
                                            Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());

                                    if (Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()
                                            .equals(canonicalClassName)) {
                                        surveyDialog2.show();
//                   activeSurveyDialogs.add(surveyDialog);
                                    }
                                }
                            }
                        } else {
                            SurveyDialog surveyDialog2 = new SurveyDialog(context, executableSurveySpecs,
                                    surveyParameters);
                            List<SurveyDialog> list = new ArrayList<>();
                            list.add(surveyDialog2);
                            activeSurveyDetails.put(canonicalClassName, list);
                            if (Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()
                                    .equals(canonicalClassName)) {
                                surveyDialog2.show();
//                   activeSurveyDialogs.add(surveyDialog);
                            }
                        }
                    }

            }
            Log.d("activeSurveys222", activeSurveyDetails.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alium_survey_activity);
        isActivityRunning = true;
//        activeSurveyList=new ArrayList<>();
//        activeSurveyDialogs=new ArrayList<>();
        IntentFilter intentFilter=new IntentFilter("survey_content_fetched");
        registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter intentFilter2=new IntentFilter("activity_resumed");
        registerReceiver(activityResumedBoradcast, intentFilter2);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            Log.d("WindowAttributes", "SoftInputMode: " + params.softInputMode);
            Log.d("WindowAttributes", "Flags: " + params.flags);
        }
//        Intent intent=getIntent();
//        SurveyParameters surveyParameters=(SurveyParameters) intent
//                .getSerializableExtra("surveyParameters");
//        JSONObject surveyConfigJSON;
//        try {
//             surveyConfigJSON=new JSONObject(
//                    intent.getStringExtra("surveyConfigJSON")
//            );
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        new AliumSurveyLoader(this, surveyParameters, surveyConfigJSON)
//                        .showSurvey();

    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d("Instance", "on pause");
        if(!activeSurveyDetails.isEmpty()){
            List<SurveyDialog> list= activeSurveyDetails.get(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());
            Iterator iterator= list.iterator();
            while(iterator.hasNext()){
               try{
                   SurveyDialog survey= (SurveyDialog) iterator.next();
                   survey.dialog.dismiss();
               }catch (Exception e){
                   Log.d("onPause", e.toString());
               }

            }
        }
    }
    @Override
    protected  void onResume(){
        super.onResume();

        Log.d("Instance", "on resume");

    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Instance", "on stop");
        isActivityRunning = false;
//        if(!activeSurveyDetails.isEmpty()){
//            List<SurveyDialog> list= activeSurveyDetails.get(canonicalClassName);
//            Iterator iterator= list.iterator();
//            while(iterator.hasNext()){
//                SurveyDialog survey= (SurveyDialog) iterator.next();
//                survey.dialog.dismiss();
//
//
//            }
//        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Instance", "on destroy");
        isActivityRunning = false;


//        if(!activeSurveyDialogs.isEmpty()){
//            Iterator iterator=activeSurveyDialogs.iterator();
//           while(iterator.hasNext()){
//               SurveyDialog survey= (SurveyDialog) iterator.next();
//               survey.dialog.dismiss();
//               List<SurveyDialog> list= activeSurveyDetails.get(canonicalClassName);
//               list.remove(survey);
////               activeSurveyList.remove(survey.executableSurveySpecs.getLoadableSurveySpecs().key);
//               iterator.remove();
//           }
//        }
        unregisterReceiver(activityResumedBoradcast);
        unregisterReceiver(broadcastReceiver);

    }
}
