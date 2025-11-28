package com.dwao.aliumandroidsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.survey.Alium;
import com.dwao.aliumandroidsdk.FirstFragment;
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
//        Alium.trigger(this, new SurveyParameters("screen3"));
//        Alium.trigger(this,  new SurveyParameters("thirdscreen"));
//        Alium.trigger(this,  new SurveyParameters("thirdscreen"));
//        Alium.trigger(this,  new SurveyParameters("secondscreen", params));
      Alium.trigger(this,  new SurveyParameters("home", params));
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
//        Alium.stop("secondscreen");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new FirstFragment())
                .commit();

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