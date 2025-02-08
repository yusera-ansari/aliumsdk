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
import java.util.concurrent.ConcurrentLinkedQueue;

//one for each screen and all the loaders for a screen stays here
public class ExecSurLoaderDM {
        Queue<AliumSurveyLoader> loadedQueue=new ConcurrentLinkedQueue<>();
        Queue<AliumSurveyLoader> aliumSurveyLoaderQueue=new ConcurrentLinkedQueue<>();
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
                                            if(currentLoader !=null &&currentLoader.getLoaderId().equals(loader.getLoaderId())){
                                                isAliumLoaderExecuting=false;
                                                currentLoader=null;

                                            }
                                        }
                                    });
                            if(loader!=null ) {
                                //limiting the loader to one  && aliumSurveyLoaderQueue.size()==0 && loadedQueue.size()==0
                                aliumSurveyLoaderQueue.add(loader);

                            }
                            executeNextLoader();
        }

    private synchronized void executeNextLoader(){
            Log.d("ExecNLoader", "loader list for: "+screenName+ " conatins: loadedQueue"+loadedQueue
            +" loading:  "+aliumSurveyLoaderQueue);
//            if(currentLoader!=null){
//                Log.d("CURRENT", "Current LOADER is not NULL");
//                loadedQueue.offer(currentLoader); //it should be added to loaded-queue
//                currentLoader=null;
//            }

            if(aliumSurveyLoaderQueue.isEmpty()||isAliumLoaderExecuting){
                if(aliumSurveyLoaderQueue.isEmpty()){
                    Log.d("ISEMPTY", "ALIUM SURVEY lOSADER Queue is empty");
                    isAliumLoaderExecuting=false; //reset the value
                }
                Log.d("isAliumLoaderExecuting", "ALIUM SURVEY lOSADER Queue isAliumLoaderExecuting " +isAliumLoaderExecuting);
                return;
            }

            isAliumLoaderExecuting=true;
            AliumSurveyLoader aliumSurveyLoader= aliumSurveyLoaderQueue.poll();
            if(aliumSurveyLoader!=null){
                loadedQueue.offer(aliumSurveyLoader);
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
            Iterator<AliumSurveyLoader> iterator= loadedQueue.iterator();
            while (iterator.hasNext()){
                AliumSurveyLoader loader=iterator.next();
                if(loader!=null)loader.stop();
                iterator.remove();
            }
//        while(!loadedQueue.isEmpty()){
//          AliumSurveyLoader loader=  loadedQueue.poll(); //why poll?
//        if(loader!=null)  loader.stop();
//        }
    }
    }
