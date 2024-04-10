package com.dwao.aliumandroidsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.aliumandroidsdk.R;
import com.dwao.alium.survey.ShowSurvey;

public class DashboardActivity extends AppCompatActivity {
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        next=findViewById(R.id.next);
        Intent intent=new Intent(this, TestActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        String url= "https://assets.alium.co.in/cmmn/cstjn/cstjn_1038.json";
        new ShowSurvey(this, url, this.getTitle().toString());
    }
}