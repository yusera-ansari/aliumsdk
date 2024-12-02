package com.dwao.aliumandroidsdk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwao.alium.survey.Alium;
import com.dwao.alium.survey.SurveyParameters;
import com.dwao.aliumandroidsdk.Config;
import com.dwao.aliumandroidsdk.R;

import java.util.HashMap;

public class TestActivity extends Activity {
    TextView next;
    @Override
    protected void onResume(){
        super.onResume();
        Alium.trigger(this,  new SurveyParameters("thirdscreen"));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        next=findViewById(R.id.test_next);

    }
}