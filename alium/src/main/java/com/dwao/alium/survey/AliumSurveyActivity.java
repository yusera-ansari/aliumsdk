package com.dwao.alium.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dwao.alium.R;
import com.dwao.alium.models.Survey;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AliumSurveyActivity extends AppCompatActivity {
    public static boolean isActivityRunning=false;
    private static boolean stateRestored=false;

    private static List<SurveyDialog> activeSurveys=new ArrayList<>();
    BroadcastReceiver surveyContentReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && !isDestroyed() && !isFinishing()) {
                Log.d("broadcast", "broadcast received in activity");
                renderSurvey(intent);
            }
        }

    };
    protected void removeFromActiveSurveyList(SurveyDialog surveyDialog){
        activeSurveys.remove(surveyDialog);
        if(activeSurveys.isEmpty()){
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NewIntent", "newIntent received"+intent);
        if (intent != null && intent.hasExtra("surveyParameters")) {
            Log.d("new intent", "new intent received in activity");
//            renderSurvey(intent);
        }
    }
    @Override
    protected  void onPause(){
        super.onPause();
        Log.d("Pause", "AliumActivity Paused");
    }
    @Override
    protected  void onResume(){
        super.onResume();
        Log.d("onResume", "AliumActivity onResume");
        Log.d("onResume",activeSurveys.toString());
        if(!stateRestored){
            Log.d("onresume", "state not restored");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alium_survey);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
        window.setDimAmount(0.25f);
        IntentFilter intentFilter=new IntentFilter("survey_content_fetched");
//        registerReceiver(surveyContentReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(surveyContentReceiver,intentFilter);
        isActivityRunning=true;
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("activeSurveys", (Serializable) activeSurveys);
        Log.d("Instance", "on save state");

            Iterator surveyDialogIterator= activeSurveys.iterator();
            List<Map<String, Serializable>> activeSurveyMaps=new ArrayList();
            while (surveyDialogIterator.hasNext()){
                SurveyDialog surveyDialog= (SurveyDialog) surveyDialogIterator.next();
                Map<String, Serializable> map=new HashMap<>();
                map.put("surveyParameters", surveyDialog.surveyParameters);
                map.put("loadableSurveySpecs", surveyDialog.executableSurveySpecs.getLoadableSurveySpecs());
                map.put("surveyJson", surveyDialog.
                        executableSurveySpecs.getSurvey());

                activeSurveyMaps.add(map);
                surveyDialog.dialog.dismiss();
                surveyDialogIterator.remove();

            }
        outState.putSerializable("activeSurveyLists", (Serializable) activeSurveyMaps);



        Log.d("onSaveInstanceState", "On saved Instance state"+ activeSurveyMaps.size());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d("onSaveInstanceState", "On restore Instance state");
        List<Map<String, Serializable>> activeSurveyMapLists
                = (List<Map<String, Serializable>>) savedInstanceState.get("activeSurveyLists");
       if(activeSurveyMapLists.size()>0){
           Log.d("surveyList", "active survey list is >0");
           Iterator<Map<String, Serializable>> keys= activeSurveyMapLists.listIterator();
           while(keys.hasNext()) {
               Map<String, Serializable> map=keys.next();
               SurveyParameters surveyParameters = (SurveyParameters) map.get("surveyParameters");
               LoadableSurveySpecs loadableSurveySpecs =
                       (LoadableSurveySpecs) map.get("loadableSurveySpecs");
               String json;
               Gson gson=new Gson();
               Survey survey=new Survey();
               try {
                   survey=(Survey) map.get("surveyJson");
//                   json = map.get("surveyJson").toString();
//                   survey=gson.fromJson(json, Survey.class);
               } catch (Exception e) {
//                   throw new RuntimeException(e);
                   Log.d("onstateRestore",map.get("surveyJson").toString());
                   Log.e("onstateRestore", e.toString());
                   continue;
               }
               ExecutableSurveySpecs executableSurveySpecs = new ExecutableSurveySpecs(survey
                       , loadableSurveySpecs);

//                Log.i("activesurveylist", activeSurveyList.toString());
               SurveyDialog surveyDialog = new SurveyDialog(this, executableSurveySpecs,
                       surveyParameters, false);
               activeSurveys.add(surveyDialog);
               surveyDialog.show();
           }
       }
       stateRestored=true;
       Log.d("onrestoresate", activeSurveys.toString());

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("onDestroy", "on activity destroy");
        isActivityRunning=false;
        if(!activeSurveys.isEmpty()){
            Iterator<SurveyDialog> keys=activeSurveys.iterator();
            while(keys.hasNext()){
                SurveyDialog key=keys.next();
                key.dialog.dismiss();
                keys.remove();
            }
        }
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(surveyContentReceiver);
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.d("Instance", "on config changed");
    }

    private void renderSurvey(Intent intent){
        SurveyParameters surveyParameters = (SurveyParameters) intent
                .getSerializableExtra("surveyParameters");
        String canonicalClassName = intent.getStringExtra("canonicalClassName");
        LoadableSurveySpecs loadableSurveySpecs =
                (LoadableSurveySpecs) intent.getSerializableExtra("loadableSurveySpecs");
        if(!activeSurveys.isEmpty()){
            Iterator<SurveyDialog> keys=activeSurveys.iterator();
            while(keys.hasNext()){
                SurveyDialog dialog=keys.next();
                if(dialog.loadableSurveySpecs.key.equals(loadableSurveySpecs.key)){
                    Log.d("activeSurvey", "survey existes");
                    return;
                }

            }

        }
        Gson gson=new Gson();
        Survey survey=new Survey();
        String json="";
        try {
            json = intent.getStringExtra("surveyJson");
            survey=gson.fromJson(json, Survey.class);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            Log.d("renderSurvey", e.toString());
            return;
        }
        ExecutableSurveySpecs executableSurveySpecs = new ExecutableSurveySpecs(survey
                , loadableSurveySpecs);

        SurveyDialog surveyDialog = new SurveyDialog(this, executableSurveySpecs,
                surveyParameters, true);
        activeSurveys.add(surveyDialog);
        surveyDialog.show();
    }
}