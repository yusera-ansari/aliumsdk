package com.dwao.alium.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AliumSurveyActivity extends AppCompatActivity {
    public static boolean isActivityRunning = false;
    private static List<String> activeSurveyList;
    protected List<String> removeSurveyFromActiveList(String key){
        activeSurveyList.remove(key);
        return activeSurveyList;
    }
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                        SurveyParameters surveyParameters=(SurveyParameters) intent
                .getSerializableExtra("surveyParameters");
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
                activeSurveyList.add(loadableSurveySpecs.key);
                Log.i("activesurveylist", activeSurveyList.toString());
                new SurveyDialog(context, executableSurveySpecs,
                            surveyParameters)
                            .show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alium_survey_activity);
        isActivityRunning = true;
        activeSurveyList=new ArrayList<>();
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
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
        unregisterReceiver(broadcastReceiver);
    }
}
