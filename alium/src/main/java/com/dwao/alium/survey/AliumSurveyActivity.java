package com.dwao.alium.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dwao.alium.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AliumSurveyActivity extends AppCompatActivity {
    public static boolean isActivityRunning=false;
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
        if (intent != null ) {
            Log.d("intent", "intent received in activity");
            renderSurvey(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alium_survey);

        IntentFilter intentFilter=new IntentFilter("survey_content_fetched");
        registerReceiver(surveyContentReceiver, intentFilter);
        isActivityRunning=true;
        Intent intent=getIntent();
        if (intent != null ) {
            Log.d("intent", "intent received in activity");
           renderSurvey(intent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        isActivityRunning=false;
    }
    private void renderSurvey(Intent intent){
        SurveyParameters surveyParameters = (SurveyParameters) intent
                .getSerializableExtra("surveyParameters");
        String canonicalClassName = intent.getStringExtra("canonicalClassName");
        LoadableSurveySpecs loadableSurveySpecs =
                (LoadableSurveySpecs) intent.getSerializableExtra("loadableSurveySpecs");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(intent.getStringExtra("surveyJson"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ExecutableSurveySpecs executableSurveySpecs = new ExecutableSurveySpecs(jsonObject
                , loadableSurveySpecs);

        SurveyDialog surveyDialog = new SurveyDialog(this, executableSurveySpecs,
                surveyParameters);
        activeSurveys.add(surveyDialog);
        surveyDialog.show();
    }
}