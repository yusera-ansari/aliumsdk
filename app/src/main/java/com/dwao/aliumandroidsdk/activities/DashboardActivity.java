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

public class DashboardActivity extends Activity {
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        next=findViewById(R.id.sec_next);
        Intent intent=new Intent(this, TestActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        Map params=new HashMap();
        params.put("dim1", "alium_app"); //appName
        params.put("dim2", "mobile"); //surveyOn
        params.put("dim3", "android"); //os
        Alium.trigger(this,  new SurveyParameters("thirdscreen", params));
    }
}