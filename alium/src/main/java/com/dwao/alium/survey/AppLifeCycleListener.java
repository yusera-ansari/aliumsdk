package com.dwao.alium.survey;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

public class AppLifeCycleListener implements Application.ActivityLifecycleCallbacks {

    private Activity currentActivity;

    public Activity getCurrentActivity(){
        return currentActivity;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d("onActivityCreated","onActivityCreated "+activity.getClass().getCanonicalName());
      if(!activity.getClass().getCanonicalName().equals("com.dwao.alium.survey.AliumSurveyActivity"))  currentActivity=activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d("onActivityStarted","onActivityStarted "+activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d("onActivityResumed","onActivityResumed "+activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d("onActivityPaused","onActivityPaused "+activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("onActivityStopped","onActivityStopped "+activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d("ontySaveInstanceState","onAtySaveInstanceState "+activity.getClass().getCanonicalName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d("onActivityDestroyed","onActivityDestroyed "+activity.getClass().getCanonicalName());
    }
}
