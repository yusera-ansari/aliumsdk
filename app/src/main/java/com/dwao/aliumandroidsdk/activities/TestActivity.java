package com.dwao.aliumandroidsdk.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.survey.Alium;
import com.dwao.alium.survey.SurveyParameters;
import com.dwao.aliumandroidsdk.Config;
import com.dwao.aliumandroidsdk.R;

import java.util.HashMap;

public class TestActivity extends AppCompatActivity {
    TextView next;

    @Override
    protected void onResume(){
        super.onResume();
        Alium.trigger(this,  new SurveyParameters("thirdscreen"));
        //Alium.trigger(this,  new SurveyParameters("thirdscreen"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen"));
            }
        }, "thread-second").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen" ));
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen" ));
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("SavedState", "Instance state saved");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("StateRestored","Instance state restored");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        next=findViewById(R.id.test_next);
        Log.d("onCreate", "Instance state- oncreate");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("OnconfigChange", "Instance state - onconfig changed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Alium.stop("thirdscreen");
        Alium.stop("thirdscreen");
    }
}