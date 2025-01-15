package com.dwao.alium.survey;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TriggerRequest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

//one for each screen and all the loaders for a screen stays here
public class ExecSurLoaderDM {
        Queue<AliumSurveyLoader> loadedQueue=new LinkedList<>();
        Queue<AliumSurveyLoader> aliumSurveyLoaderQueue=new LinkedList<>();
        String screenName="";
        AliumSurveyLoader currentLoader=null;
        private ExecSurLoaderDM(){};
        private boolean isStopped=false;
        private boolean isAliumLoaderExecuting=false;

        public ExecSurLoaderDM(String screenName){
            this.screenName=screenName;
        }


    synchronized void  offer(TriggerRequest request, Map<String, SurveyConfig> surveyConfigMap){
            Log.d("called", "offer is executing...");
            AliumSurveyLoader loader= AliumSurveyLoader.createInstance(request.object, request.surveyParameters, surveyConfigMap,

                                    new AliumSurveyLoader.Callback() {
                                        @Override
                                        public synchronized void onAliumLoaderExcecuted() {
                                            isAliumLoaderExecuting=false;
                                            executeNextLoader();
                                        }

                                        @Override
                                        public synchronized void onQuitLoader(AliumSurveyLoader loader) {
                                            currentLoader=null;
//                                            Iterator<AliumSurveyLoader> loaderIterator= loadedQueue.iterator();
//                                            while(loaderIterator.hasNext()){
//                                                AliumSurveyLoader ele=loaderIterator.next();
//                                                if(ele.getLoaderId()==)
//                                            }
////                                        emptyLoadedQueue();
                                        }
                                    });
                            if(loader!=null)  aliumSurveyLoaderQueue.offer(  loader    );
                            executeNextLoader();
//
        }

    private synchronized void executeNextLoader(){
            Log.d("ExecNLoader", "loader list for: "+screenName+ " conatins: loadedQueue"+loadedQueue
            +" loading:  "+aliumSurveyLoaderQueue);
            if(currentLoader!=null){
                Log.d("CURRENT", "Current LOADER is not NULL");
                loadedQueue.offer(currentLoader); //it should be added to loaded-queue
                currentLoader=null;
            }

            if(aliumSurveyLoaderQueue.isEmpty()||isAliumLoaderExecuting){
                if(aliumSurveyLoaderQueue.isEmpty()){
                    isAliumLoaderExecuting=false;
                }
                return;
            }

            isAliumLoaderExecuting=true;
            AliumSurveyLoader aliumSurveyLoader= aliumSurveyLoaderQueue.poll();

            if(aliumSurveyLoader!=null){
                currentLoader=aliumSurveyLoader;
                aliumSurveyLoader.showSurvey();
            }
    }
    synchronized void stop(){ //Alium calls stop on a screen
        Log.d("stop", "offer stop is executing...");
            isStopped=true;
            isAliumLoaderExecuting=false;
            if(currentLoader!=null) currentLoader.stop();
            aliumSurveyLoaderQueue.clear();
           emptyLoadedQueue();
        currentLoader=null;
    }
    private synchronized void emptyLoadedQueue(){
        while(!loadedQueue.isEmpty()){
          AliumSurveyLoader loader=  loadedQueue.poll(); //why poll?
          loader.stop();
        }
    }
    }
