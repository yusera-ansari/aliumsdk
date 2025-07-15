package com.dwao.alium.survey;



import android.util.Log;

import com.dwao.alium.models.TriggerRequest;
import com.dwao.alium.services.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class AliumRequestManager {
    private static boolean isAliumRequestExecuting=false;

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
                Logger.log(Logger.LogLevel.DEBUG, "exec-nex-req", "a request already in process: "+isAliumRequestExecuting
                        +" and queue is empty, so we return from here");
                isAliumRequestExecuting=false;
            }

            return;
        }
        isAliumRequestExecuting=true;
       
        AliumRequest request= aliumRequestQueue.poll();


    if (request != null){

            if(request.type.equals(AliumRequest.Request.TRIGGER)){
                    TriggerRequest triggerRequest = request.request;
                Logger.log(Logger.LogLevel.DEBUG, "exec-nex-req", "executing next request: TRIGGER "+triggerRequest.surveyParameters.screenName);

                SLQHandler slqHandler = surveyExecutingMap.get(triggerRequest.surveyParameters.screenName);
                if (slqHandler == null) {
                    slqHandler = new SLQHandler(triggerRequest.surveyParameters.screenName);
                    surveyExecutingMap.put(triggerRequest.surveyParameters.screenName, slqHandler);
                }
                if(slqHandler.loadedQueue.isEmpty()){ //limits the loader to one per screen
                    Logger.log(Logger.LogLevel.INFO, "exec-nex-req", "loaded queue is empty...proceeding");
                    slqHandler.offer(triggerRequest);
                }else {
                    Logger.log(Logger.LogLevel.INFO, "exec-nex-req", "loaded queue is not empty...not proceeding further with this request. A request with current key: "+triggerRequest.surveyParameters.screenName+ " is already in process");
                }
            }else{
                Logger.log(Logger.LogLevel.DEBUG, "exec-nex-req", "executing next request: STOP "+request.screenName);

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
        }
    }





}
