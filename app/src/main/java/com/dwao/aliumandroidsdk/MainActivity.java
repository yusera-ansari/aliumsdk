package com.dwao.aliumandroidsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dwao.alium.listeners.SurveyLoader;
import com.dwao.alium.survey.Alium;
import com.dwao.alium.survey.SurveyParameters;
import com.dwao.aliumandroidsdk.activities.DashboardActivity;


import com.dwao.aliumandroidsdk.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends Activity {
    JSONObject json=null;
    SurveyLoader surveyLoader, surveyLoader2;
    TextView next;
    @Override
    protected  void onPause(){
        super.onPause();
//        surveyLoader.stop();
//        surveyLoader2.stop();
        Log.d("Pause", "MainActivity Paused");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("OnResume", "resumed main activity");
       surveyLoader= Alium.trigger(this, new SurveyParameters("firstscreen"));
        surveyLoader2= Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));

//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//              surveyLoader2= Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));
//
//          }
//      }, 5000);
          }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //disable night mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Activity", ""+this.getClass().getSimpleName());
        next=findViewById(R.id.main_next);
        Intent intent=new Intent(this, DashboardActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyLoader.stop();
                surveyLoader2.stop();
                startActivity(intent);
            }
        });
    }
}