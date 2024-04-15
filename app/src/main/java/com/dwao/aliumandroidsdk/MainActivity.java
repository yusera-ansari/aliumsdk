package com.dwao.aliumandroidsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dwao.aliumandroidsdk.activities.DashboardActivity;


import com.dwao.alium.survey.ShowSurvey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    JSONObject json=null;
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //disable night mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Activity", ""+this.getClass().getSimpleName());
        next=findViewById(R.id.next);
        Intent intent=new Intent(this, DashboardActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
//        VolleyService.callVolley(this);
//        try {
//            VolleyService volleyService=new VolleyService();
//            String url= "https://assets.alium.co.in/cmmn/cstjn/cstjn_1038.json";
//            volleyService.callVolley(this,url );
//           json= FetchJson.getFromLocal(getAssets().open("survey_details.json"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if(json!=null){
//        String url= "https://assets.alium.co.in/cmmn/cstjn/cstjn_1038.json";
//           new ShowSurvey(this, url);

//        }
        new ShowSurvey(this, Config.BASE_URL, this.getTitle().toString());
    }
}