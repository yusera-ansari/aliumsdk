package com.dwao.aliumandroidsdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dwao.alium.survey.Alium;

public class MainApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        Alium.config(this,Config.BASE_URL);
        Log.d("Application", "initialized");
       registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
           @Override
           public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
           }

           @Override
           public void onActivityStarted(@NonNull Activity activity) {

           }

           @Override
           public void onActivityResumed(@NonNull Activity activity) {
           }

           @Override
           public void onActivityPaused(@NonNull Activity activity) {

           }

           @Override
           public void onActivityStopped(@NonNull Activity activity) {

           }

           @Override
           public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

           }

           @Override
           public void onActivityDestroyed(@NonNull Activity activity) {

           }
       });
    }
}
