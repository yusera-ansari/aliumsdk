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
            Log.d("SLQ", "loaded queue: "+loadedQueue.toString());
            if(!loadedQueue.isEmpty()){
                Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                while( iterator.hasNext()){
                    AliumSurveyLoader loader=iterator.next();
                    Log.d("SLQ", "loaded queue: "+loader.getLoaderId()+" id: "+id);
                    if(loader.getLoaderId().equals(id)){
                        return loader.surveyDialogCallback;
                    }
                }
            }
        }
        return null;
    };

synchronized  void executeNextRequest(Queue<AliumRequest> aliumRequestQueue){

        Log.d("exec nex","execute next trigger");
        if(isAliumRequestExecuting||aliumRequestQueue.isEmpty()){
            Log.d("exec nex","execute next trigger "+isAliumRequestExecuting +" "+aliumRequestQueue.isEmpty());
            if(aliumRequestQueue.isEmpty()){
                Log.d("exec nex", "trigger request queue is empty");
                isAliumRequestExecuting=false;
            }
            return;
        }
        isAliumRequestExecuting=true;
        Log.d("exec nex","execute next trigger");
        AliumRequest request= aliumRequestQueue.poll();


        if (request != null){
            if(request.type.equals(AliumRequest.Request.TRIGGER)){
                Log.d("REQUEST", "request for trigger: "+request.request.surveyParameters.screenName);
                TriggerRequest triggerRequest = request.request;

                SLQHandler slqHandler = surveyExecutingMap.get(triggerRequest.surveyParameters.screenName);
                if (slqHandler == null) {
                    slqHandler = new SLQHandler(triggerRequest.surveyParameters.screenName);
                    surveyExecutingMap.put(triggerRequest.surveyParameters.screenName, slqHandler);
                }
                Log.d("slqman","checking loaded queue before making request..." );
                if(slqHandler.loadedQueue.isEmpty()){ //limits the loader to one per screen
                    Log.d("slqman", "loaded queue is empty....adding request...");
                    slqHandler.offer(triggerRequest);
                }
            }else{
                Log.d("REQUEST", "request for STOP executing...."+ request.screenName);
                stop(request.screenName);
            }

        }
        Log.d("exec nex","execute next trigger completed");
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
