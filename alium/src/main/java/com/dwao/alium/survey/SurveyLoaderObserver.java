package com.dwao.alium.survey;

import android.util.Log;

import com.dwao.alium.listeners.SurveyLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyLoaderObserver implements SurveyLoader {
    final String TAG="SurveyLoader";
    private String screenName="";
    private static boolean isStopped=false;
    SurveyLoaderObserver(String screenName){
        this.screenName=screenName;
    }

 List<AliumSurveyLoader> list=new ArrayList<>();
    @Override
    public  void stop() {
        isStopped=true;
//        if(list.size()>0){

           for(AliumSurveyLoader loader: list){
               Log.d(TAG, "stop was caled on the loader");

               loader.stop();
           }
//        }

    }

    @Override
    public synchronized void addSurveyLoader(AliumSurveyLoader loader) {
        if(isStopped()){
            Log.d(TAG, "stop caled before adding loader");
            loader.stop();
        }
        else{
            list.add(loader);
        }
    }

    @Override
    public boolean isStopped() {
        return isStopped;
    }

    @Override
    public String getScreenName() {
        return screenName;
    }
}
