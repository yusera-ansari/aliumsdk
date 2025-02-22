package com.dwao.alium.survey;

import static com.dwao.alium.utils.DeviceInfo.getUserAgent;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dwao.alium.network.VolleyService;

import java.util.Iterator;
import java.util.Map;

public class SurveyTracker {

    private static final String BASE_URL="https://tracker.alium.co.in/tracker?";
    private static Uri.Builder getUriBuilder(){
        return new Uri.Builder().scheme("https"
                ).authority("tracker.alium.co.in")
                .path("tracker");
    }

    private static String getAppendableVariables(Map<String, String> parameters){
        String appendableTrackString="";
        Iterator<String> keys=parameters.keySet().iterator();
        while(keys.hasNext()){
            String key= keys.next();
            String temp=key+"="+parameters.get(key);
            appendableTrackString=appendableTrackString+"&"+temp;
        }
        return appendableTrackString.replace(" ", "%20");
    }
    public static void trackWithAlium(Context context,  Map<String, String> parameters ) {
        try{
            VolleyService volleyService=  VolleyService.getInstance();
            volleyService.loadRequestWithVolley(  getUrl(context, parameters) );
        }catch(Exception e){
            Log.e("track", e.toString());
        }
    }
    @NonNull
    private static String getUrl(Context context, Map<String, String> parameters ){
        Uri.Builder builder=getUriBuilder();
        builder.appendQueryParameter("ua", getUserAgent(context));
        return builder.build().toString()+getAppendableVariables(parameters);
    }
}
