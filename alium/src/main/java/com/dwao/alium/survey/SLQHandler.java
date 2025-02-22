package com.dwao.alium.survey;

import android.util.Log;

import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TriggerRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//SURVEY-LOADER-QUEUE-HANDLER
// one for each screen and all the loaders for a screen stays here

public class SLQHandler {
       volatile Queue<AliumSurveyLoader> loadedQueue=new ConcurrentLinkedQueue<>();
        volatile  Queue<AliumSurveyLoader> aliumSurveyLoaderQueue=new ConcurrentLinkedQueue<>();
        String screenName="";
        AliumSurveyLoader currentLoader=null;
        private SLQHandler(){};
        private volatile boolean isStopped=false;
        private volatile boolean isAliumLoaderExecuting=false;

        public SLQHandler(String screenName){
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
//                                            if(currentLoader !=null && currentLoader.getLoaderId().equals(loader.getLoaderId())){
                                                if(!loadedQueue.isEmpty()){
                                                    Log.d("LoadedQueue", "Loaded queue is mot empty we will update it!");
                                                    Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                                                    while( iterator.hasNext()) {
                                                        AliumSurveyLoader loadertmp = iterator.next();
                                                        Log.d("Loaded-", "loaded Loader: "+loadertmp);
                                                        if(loadertmp.getLoaderId().equals(loader.getLoaderId())){
                                                            Log.d("Loaded-", "removing it loaded Loader: "+loader);
//                                                            loader.callback.onQuitLoader(loader);
                                                            iterator.remove();

                                                        }
                                                    }
                                                }
                                                isAliumLoaderExecuting=false;
                                                currentLoader=null;

//                                            }
                                        }
                                    });
                            if(loader!=null ) {
                                //limiting the loader to one:-  && aliumSurveyLoaderQueue.size()==0 && loadedQueue.size()==0
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
                    Log.d("Loader", "loading queue empty: loaded queue: "+loadedQueue);
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

    }
    }
