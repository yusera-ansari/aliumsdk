package com.dwao.aliumandroidsdk.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.aliumandroidsdk.R;
import com.dwao.alium.survey.ShowSurvey;

public class TestActivity extends AppCompatActivity {
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        next=findViewById(R.id.next);
        String url= "";
        new ShowSurvey(this, url, this.getTitle().toString());
    }
}