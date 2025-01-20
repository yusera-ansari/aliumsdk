package com.dwao.aliumandroidsdk.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.survey.Alium;
import com.dwao.alium.survey.SurveyParameters;
import com.dwao.aliumandroidsdk.Config;
import com.dwao.aliumandroidsdk.R;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    TextView next;
    @Override
    protected void onResume(){
        super.onResume();
        Map params=new HashMap();
        params.put("dim1", "alium_app"); //appName
        params.put("dim2", "mobile"); //surveyOn
        params.put("dim3", "android"); //os
        Alium.trigger(this,  new SurveyParameters("thirdscreen"));
        Alium.trigger(this,  new SurveyParameters("secondscreen", params));
//      Alium.trigger(this,  new SurveyParameters("secondscreen", params));
//       new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Alium.trigger(DashboardActivity.this,  new SurveyParameters("thirdscreen"));
//                Alium.trigger(DashboardActivity.this , new SurveyParameters("secondscreen", params));
//            }
//        }, "thread-second").start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Alium.trigger(DashboardActivity.this , new SurveyParameters("secondscreen", params));
//            }
//        } ).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Alium.trigger(DashboardActivity.this , new SurveyParameters("secondscreen", params));
//            }
//        } ).start();

    }
    protected  void onPause(){
        super.onPause();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        next=findViewById(R.id.sec_next);
        Intent intent=new Intent(this, TestActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                surveyLoader.stop();
                startActivity(intent);
//                Alium.stop("secondscreen");
            }
        });
      }
}