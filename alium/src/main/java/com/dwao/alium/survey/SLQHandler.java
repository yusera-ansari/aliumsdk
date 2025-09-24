package com.dwao.alium.survey;

import android.util.Log;

import com.dwao.alium.models.TriggerRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//SURVEY-LOADER-QUEUE-HANDLER
// one for each screen and all the loaders for a screen stays here

class SLQHandler {
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



    synchronized void  offer(TriggerRequest request){
            AliumSurveyLoader loader= AliumSurveyLoader.createInstance(request.object, request.surveyParameters,

                                    new AliumSurveyLoader.Callback() {
                                        @Override
                                        public synchronized void onAliumLoaderExcecuted() {
                                            //a loader has finished its tasks
                                            isAliumLoaderExecuting=false;
                                            executeNextLoader();
                                        }

                                        @Override
                                        public synchronized void onQuitLoader(AliumSurveyLoader loader) {
                                            //loader quit : run cleanup
                                            if(!loadedQueue.isEmpty()){
                                                    Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                                                    while( iterator.hasNext()) {
                                                        AliumSurveyLoader loadertmp = iterator.next();
                                                        if(loadertmp.getLoaderId().equals(loader.getLoaderId())){
//                                                            loader.callback.onQuitLoader(loader);
                                                            iterator.remove();

                                                        }
                                                    }

                                            }
                                                isAliumLoaderExecuting=false;
                                                currentLoader=null;

                                        }
                                    });
                            //create instance will retrun a loader to be added to the queue
                            //only move forward if its not null

                            if(loader!=null ) {
                                //limiting the loader to one:-  && aliumSurveyLoaderQueue.size()==0 && loadedQueue.size()==0
                                aliumSurveyLoaderQueue.add(loader);

                            }
                            //always execute next
                            executeNextLoader();
        }

    private synchronized void executeNextLoader(){

            if(aliumSurveyLoaderQueue.isEmpty()||isAliumLoaderExecuting){
                if(aliumSurveyLoaderQueue.isEmpty()){
                    isAliumLoaderExecuting=false; //reset the value
                }
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
