package com.dwao.alium.survey;



import android.util.Log;

import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.models.TriggerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class SLQHandlerManager {
    private static boolean isTriggerExecuting=false;
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


      synchronized void executeNextTrigger(Queue<TriggerRequest> triggerRequestQueue){
//          if(isTriggerExecuting||configURL==null||isConfigFetching||triggerRequestQueue.isEmpty()){
//              return;
//          }
          Log.d("exec nex","execute next trigger");
        if(isTriggerExecuting||triggerRequestQueue.isEmpty()){
            Log.d("exec nex","execute next trigger "+isTriggerExecuting +" "+triggerRequestQueue.isEmpty());
            if(triggerRequestQueue.isEmpty()){
                isTriggerExecuting=false;
            }
            return;
        }
        isTriggerExecuting=true;
        Log.d("exec nex","execute next trigger");
        TriggerRequest request= triggerRequestQueue.poll();


        if (request != null){
            if (pendingStops.contains(request.surveyParameters.screenName)) {
                      pendingStops.remove(request.surveyParameters.screenName);
                  }

                      SLQHandler slqHandler = surveyExecutingMap.get(request.surveyParameters.screenName);
                      if (slqHandler == null) {
                          slqHandler = new SLQHandler(request.surveyParameters.screenName);
                          surveyExecutingMap.put(request.surveyParameters.screenName, slqHandler);
                      }

                      slqHandler.offer(request);

          }
          Log.d("exec nex","execute next trigger completed");
        isTriggerExecuting=false;
        executeNextTrigger(triggerRequestQueue);
    }
    public void stop(String screenName){
        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
        if(execSurLoaderDM!=null ){

            execSurLoaderDM.stop();
        }else{
            pendingStops.add(screenName);
        }
    }
//      synchronized void updateExecLoaderData(String id, String screenName){
//        SLQHandler execSurLoaderDM= surveyExecutingMap.get(screenName);
//        if(execSurLoaderDM!=null){ //should try with iterator
//            Queue<AliumSurveyLoader> loadedQueue=execSurLoaderDM.loadedQueue;
//            if(!loadedQueue.isEmpty()){
//                Log.d("LoadedQueue", "Loaded queue is mot empty we will update it!");
//                Iterator<AliumSurveyLoader> iterator=loadedQueue.iterator();
//                while( iterator.hasNext()) {
//                    AliumSurveyLoader loader = iterator.next();
//                    Log.d("Loaded-", "loaded Loader: "+loader);
//                    if(loader.getLoaderId().equals(id)){
//                        Log.d("Loaded-", "removing it loaded Loader: "+loader);
//                        loader.callback.onQuitLoader(loader);
//                        iterator.remove();
//
//                    }
//                }
//                Log.d("LOadd"," loaded queue: "+loadedQueue+" "+execSurLoaderDM.aliumSurveyLoaderQueue);
//            }
//        }
//    }

}
