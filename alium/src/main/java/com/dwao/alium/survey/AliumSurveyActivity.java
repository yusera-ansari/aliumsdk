package com.dwao.alium.survey;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AliumSurveyActivity extends AppCompatActivity {
    public static boolean isActivityRunning = false;
    private static String canonicalClassName="";
//    private static List<String> activeSurveyList;
//    private static List<SurveyDialog> activeSurveyDialogs;
    private static Map<String, List<SurveyDialog>> activeSurveyDetails;

    protected List<SurveyDialog> removeSurveyFromActiveList(SurveyDialog surveyDialog){
//        activeSurveyList.remove(surveyDialog.executableSurveySpecs.getLoadableSurveySpecs().key);
//        activeSurveyDialogs.remove(surveyDialog);
       List<SurveyDialog> list= activeSurveyDetails.get(canonicalClassName);
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
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("Instance", "on save state");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("Instance", "on restore state");
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && !isDestroyed() && !isFinishing()){
                        SurveyParameters surveyParameters=(SurveyParameters) intent
                .getSerializableExtra("surveyParameters");
                        canonicalClassName=intent.getStringExtra("canonicalClassName");
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
//                activeSurveyList.add(loadableSurveySpecs.key);
//                Log.i("activesurveylist", activeSurveyList.toString());
                SurveyDialog surveyDialog=new SurveyDialog(context, executableSurveySpecs,
                            surveyParameters)
                            ;
               if(!canonicalClassName.isEmpty()){
                  if(!(activeSurveyDetails.get(canonicalClassName) ==null)){
                      List<SurveyDialog> surveyDialogList=activeSurveyDetails
                              .get(canonicalClassName);
                      surveyDialogList.add(surveyDialog);
                  }else{
                      List<SurveyDialog > list=new ArrayList<>();
                      list.add(surveyDialog);
                      activeSurveyDetails.put(canonicalClassName, list);
                  }
               }
Log.d("canonicalName", canonicalClassName+" "+
        Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName());

               if(Alium.appLifeCycleListener.getCurrentActivity().getClass().getCanonicalName()
                       .equals(canonicalClassName)){
                   surveyDialog.show();
//                   activeSurveyDialogs.add(surveyDialog);
               }
            }
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
        activeSurveyDetails=new HashMap();
        IntentFilter intentFilter=new IntentFilter("survey_content_fetched");
        registerReceiver(broadcastReceiver, intentFilter);
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
        if(!activeSurveyDetails.isEmpty()){
            List<SurveyDialog> list= activeSurveyDetails.get(canonicalClassName);
            Iterator iterator= list.iterator();
            while(iterator.hasNext()){
                SurveyDialog survey= (SurveyDialog) iterator.next();
                survey.dialog.dismiss();

            }
        }
    }
    @Override
    protected  void onResume(){
        super.onResume();

    }
    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;

        if(!activeSurveyDetails.isEmpty()){
            List<SurveyDialog> list= activeSurveyDetails.get(canonicalClassName);
            Iterator iterator= list.iterator();
            while(iterator.hasNext()){
                SurveyDialog survey= (SurveyDialog) iterator.next();
                survey.dialog.dismiss();

            }
        }
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
        unregisterReceiver(broadcastReceiver);

    }
}
