package com.dwao.aliumandroidsdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dwao.alium.survey.Alium;

public class MainApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        String url= "";
        Alium.configure(this, url);
       registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
           @Override
           public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
//               new ShowSurvey(activity, url);

//                     new  ShowSurvey(activity, url, activity.getTitle().toString());
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
