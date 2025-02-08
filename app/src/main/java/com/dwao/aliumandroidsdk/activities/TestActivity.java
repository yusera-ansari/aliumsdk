package com.dwao.aliumandroidsdk.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
//        ThreadGroup threadGroup
//                = Thread.currentThread().getThreadGroup();
//
//        // getting the total active count of the threads
//        int threadCount = threadGroup.activeCount();
//
//        Thread threadList[] = new Thread[threadCount];
//        // enumerating over the thread list
//        threadGroup.enumerate(threadList);

//        Log.d("TestActivity::","Active threads are:");

        // iterating over the for loop to get the names of
        // all the active threads.
//        for (int i = 0; i < threadCount; i++) {
//            Log.d("TestActivity::", threadList[i].getName()) ;
//            for(StackTraceElement ele: threadList[i].getStackTrace()){
//                Log.d("StackTraceEle: ","   "+ele);
//            }
//
//        }




//        Alium.trigger(this,  new SurveyParameters("thirdscreen"));
//        Alium.trigger(this,  new SurveyParameters("secondscreen"));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen"));
//            }
//        }, "thread-second").start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen" ));
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Alium.trigger(TestActivity.this , new SurveyParameters("thirdscreen" ));
//            }
//        }).start();
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
        Intent intent=new Intent(this, DashboardActivity.class);
//        next.setOnClickListener((View v)->{
//            Alium.stop("thirdscreen");
//            startActivity(intent);
//        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("OnconfigChange", "Instance state - onconfig changed");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Alium.stop("thirdscreen");
//        Alium.stop("thirdscreen");
    }
}