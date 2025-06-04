package com.dwao.aliumandroidsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity {
    Dialog dialog;
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
        Alium.stop("screename");
//         Alium.trigger(this, new SurveyParameters("firstscreen"));
//          Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));

//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//              surveyLoader2= Alium.trigger(MainActivity.this, new SurveyParameters("firstscreen"));
//
//          }
//      }, 5000);
          }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //disable night mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        showSurveyDialog(this);
        Log.d("Activity", ""+this.getClass().getSimpleName());
        next=findViewById(R.id.main_next);
        Intent intent=new Intent(this, DashboardActivity.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                surveyLoader.stop();
//                surveyLoader2.stop();
                startActivity(intent);
            }
        });
    }
    public   void showSurveyDialog(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View dialogView = inflater.inflate(R.layout.dialog_survey, null);
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog_Material3); // use 'context', not 'this' if it's not Activity
        dialog.setContentView(dialogView); // ✅ Correct

        WebView webView = dialog.findViewById(R.id.dialogWebView);
        webView.setBackgroundColor(Color.TRANSPARENT);
//        dialogView.findViewById(R.id.dialogRoot).setBackgroundColor(Color.GREEN);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        GradientDrawable gradientDrawable=(GradientDrawable)  dialog
//                .findViewById(R.id.dialogRoot).getBackground();
//        gradientDrawable.setCornerRadius((int)(5* Resources.getSystem().getDisplayMetrics().density));
//        gradientDrawable.setColor(Color.WHITE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new Object() {
        @JavascriptInterface
        public void resize(final int height, final int width) {
//            new Handler(Looper.getMainLooper()).post(() -> {
//                float density = context.getResources().getDisplayMetrics().density;
//
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) webView.getLayoutParams();
//                params.height = (int) (height * density);
//                params.width = (int) (width * density);
//                webView.setLayoutParams(params);
//
//                if (dialog.getWindow() != null) {
//                    dialog.getWindow().setLayout(params.width + 32, params.height ); // Optional padding
//                }
//            });
        }
    }, "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
//            webView.evaluateJavascript(
//                    "javascript:(function() {" +
//                            "var height = document.documentElement.scrollHeight;" +
//                            "var width = document.documentElement.scrollWidth;" +
//                            "AndroidBridge.resize(height, width);" +
//                            "})()", null
//            );
        }
    });

        webView.loadUrl("file:///android_asset/test-survey.html");
        dialog.show();
}
}