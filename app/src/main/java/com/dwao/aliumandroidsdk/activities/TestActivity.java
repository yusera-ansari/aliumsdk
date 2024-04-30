package com.dwao.aliumandroidsdk.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.survey.Alium;
import com.dwao.aliumandroidsdk.Config;
import com.dwao.aliumandroidsdk.R;

public class TestActivity extends AppCompatActivity {
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        next=findViewById(R.id.test_next);
        Alium.loadAliumSurvey(this,  this.getTitle().toString());
    }
}