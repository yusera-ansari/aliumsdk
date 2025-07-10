package com.dwao.aliumandroidsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import java.util.Map;

public class MainActivity extends Activity {
    JSONObject json=null;
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
            Map<String, String> params = new HashMap();
            params.put("dim1", "mumbai");//city
        params.put("dim2", "32");//age
        params.put("dim3", "male");//gender
        params.put("dim4", "IN");//country
        params.put("dim5", "45678987654");//last login
        params.put("dim6", "savings");//account type
        params.put("dim7", "45678987");//customer id
        params.put("dim8", "otp"); //authentication type
        params.put("dim9", "opted_in");//consent for feedback
        params.put("dim10", "BRCH0921MUM");//branch id
        params.put("custEmail", "test@gmail.co");//branch id
        params.put("custMobile", "9090909090");//branch id
        params.put("custSystemId", "0jdu07");//systemId id
         Alium.trigger(this, new SurveyParameters("screen4", params));
         Alium.trigger(this, new SurveyParameters("screen4", params));
//         Alium.stop("screen4");

//          Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));

//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//              Alium.stop("screen4");
////              surveyLoader2= Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));
//
//          }
//      }, 5000);
          }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //disable night mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LegacyFragment fragment = new LegacyFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        Log.d("Activity", ""+this.getClass().getSimpleName());
        next=findViewById(R.id.main_next);
        Intent intent=new Intent(this, DashboardActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }
}