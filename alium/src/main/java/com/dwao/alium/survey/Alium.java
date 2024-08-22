package com.dwao.alium.survey;
import android.content.Context;
import android.util.Log;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.network.VolleyService;
import org.json.JSONObject;


public class Alium {
     private static JSONObject surveyConfigJSON;

     private static  Alium instance;

     private static VolleyService volleyService;
     private static String configURL;
     private  Alium(){
         volleyService=new VolleyService();
         surveyConfigJSON=new JSONObject();
     }

    public static void config(String url){
            if(instance==null){
                instance=new Alium();
            }
            if(configURL==null){
                configURL=url;
            }
            if(!configURL.equals(url)) {
                configURL = url;
                surveyConfigJSON=new JSONObject();
            }
        }

        public static void trigger(Context ctx, SurveyParameters parameters){
            if (configURL == null) {
                throw new IllegalStateException("Configuration URL not set. Call configure() method first.");
            }
            if(surveyConfigJSON.length()==0) {
                volleyService.callVolley(ctx, configURL, new ConfigURLResponseListener(ctx, parameters));
                Log.d("Alium-initialized", "calling survey on" + parameters.screenName);
            }else{
                Log.d("helllo", surveyConfigJSON.toString());
                new AliumSurveyLoader(ctx, parameters, surveyConfigJSON)
                        .showSurvey();
            }

        }
        private static class ConfigURLResponseListener implements VolleyResponseListener{
            Context context;
            SurveyParameters surveyParameters;
            ConfigURLResponseListener(Context ctx,SurveyParameters parameters){
                context=ctx;
                surveyParameters=parameters;
            }
            @Override
            public void onResponseReceived(JSONObject jsonObject) {
                surveyConfigJSON=jsonObject;
                Log.d("Alium-Config", jsonObject.toString());
                new AliumSurveyLoader(context, surveyParameters, surveyConfigJSON)
                        .showSurvey();

            }
        }




}
