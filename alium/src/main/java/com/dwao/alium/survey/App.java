package com.dwao.alium.survey;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App implements Application.ActivityLifecycleCallbacks
{
    private static String TAG="Alium-App";

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    Log.d(TAG, "OnActivityCreated "+activity.getLocalClassName());
    if(activity.getLocalClassName().equals("com.dwao.alium.survey.AliumSurveyActivity")
        &&!Alium.isAppInForeground()){
        activity.finish();
    }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStarted "+activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d(TAG, "onActivityResumed "+activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d(TAG, "onActivityPaused "+activity.getLocalClassName());
        if(activity.getLocalClassName().equals("MainActivity") && AliumSurveyActivity.isActivityRunning){
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStopped "+activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState "+activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d(TAG, "onActivityDestroyed");
    }
}
