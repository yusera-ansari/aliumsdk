package com.dwao.alium.survey;

import static com.dwao.alium.survey.Alium.surveyConfigMap;

import android.util.Log;

import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TriggerRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

class SLQHandlerManager {
    private static boolean isTriggerExecuting=false;

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


      synchronized void executeNextTrigger(Queue<TriggerRequest> triggerRequestQueue){
//          if(isTriggerExecuting||configURL==null||isConfigFetching||triggerRequestQueue.isEmpty()){
//              return;
//          }
        if(isTriggerExecuting||triggerRequestQueue.isEmpty()){
            return;
        }
        isTriggerExecuting=true;
        TriggerRequest request= triggerRequestQueue.poll();
        SLQHandler slqHandler= surveyExecutingMap.get(request.surveyParameters.screenName);
        if(slqHandler==null){
            Log.d("TRIGGER", "CALLING FROM ELSE-BLOCK!! "+request.surveyParameters.screenName);
            Log.d("THREAD", Thread.currentThread().getName());
            slqHandler=new SLQHandler(request.surveyParameters.screenName);
            surveyExecutingMap.put(request.surveyParameters.screenName, slqHandler);
        }



        slqHandler.offer(request , surveyConfigMap);

        isTriggerExecuting=false;
        executeNextTrigger(triggerRequestQueue);
    }
    public void stop(String screenName){
        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null){
            execSurLoaderDM.stop();
        }
    }
      synchronized void updateExecLoaderData(String id, String screenName){
        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null){ //should try with iterator
            Queue<AliumSurveyLoader> loadedQueue=execSurLoaderDM.loadedQueue;
            if(!loadedQueue.isEmpty()){
                Log.d("LoadedQueue", "Loaded queue is mot empty we will update it!");
                Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
                while( iterator.hasNext()) {
                    AliumSurveyLoader loader = iterator.next();
                    Log.d("Loaded-", "loaded Loader: "+loader);
                    if(loader.getLoaderId().equals(id)){
                        Log.d("Loaded-", "removing it loaded Loader: "+loader);
                        loader.callback.onQuitLoader(loader);
                        iterator.remove();

                    }
                }
                Log.d("LOadd"," loaded queue: "+loadedQueue+" "+execSurLoaderDM.aliumSurveyLoaderQueue);
            }
        }
    }

}
