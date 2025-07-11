package com.dwao.alium.survey;



import android.util.Log;

import com.dwao.alium.models.TriggerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class AliumRequestManager {
    private static boolean isAliumRequestExecuting=false;
    private final Set<String> pendingStops = Collections.synchronizedSet(new HashSet<>());

    private volatile static Map<String, SLQHandler> surveyExecutingMap=new HashMap<>();
    public static synchronized AliumSurveyLoader.SurveyDialogCallback reAttachCallback(String id, String screenName){
        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null){
            Queue<AliumSurveyLoader> loadedQueue=execSurLoaderDM.loadedQueue;
            
            if(!loadedQueue.isEmpty()){
                Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                while( iterator.hasNext()){
                    AliumSurveyLoader loader=iterator.next();
                    
                    if(loader.getLoaderId().equals(id)){
                        return loader.surveyDialogCallback;
                    }
                }
            }
        }
        return null;
    };

synchronized  void executeNextRequest(Queue<AliumRequest> aliumRequestQueue){

       
        if(isAliumRequestExecuting||aliumRequestQueue.isEmpty()){
            
            if(aliumRequestQueue.isEmpty()){
                
                isAliumRequestExecuting=false;
            }
            return;
        }
        isAliumRequestExecuting=true;
       
        AliumRequest request= aliumRequestQueue.poll();


        if (request != null){
            if(request.type.equals(AliumRequest.Request.TRIGGER)){
                    TriggerRequest triggerRequest = request.request;

                SLQHandler slqHandler = surveyExecutingMap.get(triggerRequest.surveyParameters.screenName);
                if (slqHandler == null) {
                    slqHandler = new SLQHandler(triggerRequest.surveyParameters.screenName);
                    surveyExecutingMap.put(triggerRequest.surveyParameters.screenName, slqHandler);
                }
                if(slqHandler.loadedQueue.isEmpty()){ //limits the loader to one per screen
                    slqHandler.offer(triggerRequest);
                }
            }else{
                stop(request.screenName);
            }

        }
        isAliumRequestExecuting=false;
        executeNextRequest(aliumRequestQueue);
}

    public void stop(String screenName){
        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null ){
            execSurLoaderDM.stop();
        }else{
            pendingStops.add(screenName);
        }
    }





}
