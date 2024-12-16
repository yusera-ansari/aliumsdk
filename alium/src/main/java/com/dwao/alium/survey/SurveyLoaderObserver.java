package com.dwao.alium.survey;

import android.util.Log;

import com.dwao.alium.listeners.SurveyLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SurveyLoaderObserver implements SurveyLoader {
    final String TAG="SurveyLoader";
    private String screenName="";
    private volatile boolean isStopped=false;
    SurveyLoaderObserver(String screenName){
        this.screenName=screenName;
    }
@Override
public void setIsStopped(boolean stop){
        isStopped=stop;
}

    public synchronized List<AliumSurveyLoader> getList() {
        return list;
    }

    private List<AliumSurveyLoader> list=new ArrayList<>();
    @Override
    public synchronized void stop() {

        Log.d("stop-sync","before: "+ list);
//        if(list.size()>0){
//        Log.d(TAG, "stop was caled on the loader"+list.size());
        Iterator<AliumSurveyLoader> keys=getList().iterator();
        while(keys.hasNext()){
            AliumSurveyLoader loader=keys.next();
            loader.stop();
            keys.remove();
        }
        isStopped=true;
        //           for(AliumSurveyLoader loader: getList()){
//               Log.d(TAG, "stop was caled on the loader");
//               loader.stop();
//           }
//           for(int i=0; i<list.size(); i++){
//               getList().remove(i);
//           }
//        }
Log.d("stop-sync","after: "+ list);
    }

    @Override
    public synchronized void addSurveyLoader(AliumSurveyLoader loader) {
        if(isStopped()){
            Log.d(TAG, "stop caled before adding loader");
            loader.stop();
            this.stop();
        }
        else{
            getList().add(loader);
        }
        Log.d("add-sync",""+ list);
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
