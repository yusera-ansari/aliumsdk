package com.dwao.alium.survey;



import com.dwao.alium.models.AliumRequest;
import com.dwao.alium.models.TriggerRequest;
import com.dwao.alium.services.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

final class AliumRequestManager {
    private static boolean isAliumRequestExecuting=false;

//    stores all the SLQHandlers for each screen
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
                Logger.log(Logger.LogLevel.DEBUG, "req-manager", "a request is already in process: "+isAliumRequestExecuting
                        +" and queue is empty, so we return from here");
                //    since queue is empty reset the AliumRequestManager.isAliumRequestExecuting
                isAliumRequestExecuting=false;
                return;
            }

            Logger.log(Logger.LogLevel.DEBUG, "req-manager", "a request is already in process:..."
                  );
            return;
        }
        // set it to true to defer incoming requests
        isAliumRequestExecuting=true;
       
        AliumRequest request= aliumRequestQueue.poll();


    if (request != null){

            if(request.type.equals(AliumRequest.Request.TRIGGER)){
                TriggerRequest triggerRequest = request.request;
                Logger.log(Logger.LogLevel.DEBUG, ".ReqManager.next", "executing next request: TRIGGER for: "+triggerRequest.surveyParameters.screenName);

                SLQHandler slqHandler = surveyExecutingMap.get(triggerRequest.surveyParameters.screenName);
                if (slqHandler == null) {
                    slqHandler = new SLQHandler(triggerRequest.surveyParameters.screenName);
                    surveyExecutingMap.put(triggerRequest.surveyParameters.screenName, slqHandler);
                }
//                to limit one request per screen/survey we check if the queue is empty
                if(slqHandler.loadedQueue.isEmpty()){ //limits the loader to one per screen
//                    Logger.log(Logger.LogLevel.INFO, "exec-nex-req", "loaded queue is empty...proceeding");
                    slqHandler.offer(triggerRequest);
                }else {
                    Logger.log(Logger.LogLevel.INFO, ".ReqManager.next", "loaded queue is not empty...not proceeding further with this request. A request with current key: "+triggerRequest.surveyParameters.screenName+ " is already in process");
                }
            }else{
                Logger.log(Logger.LogLevel.DEBUG, ".ReqManager.next", "executing next request: STOP for: "+request.screenName);
                stop(request.screenName);
            }

        }

        isAliumRequestExecuting=false;
        executeNextRequest(aliumRequestQueue);
    }

    public void stop(String screenName){
        SLQHandler slqHandler= surveyExecutingMap.get(screenName);
        if(slqHandler!=null ){
            slqHandler.stop();
        }
    }


}
