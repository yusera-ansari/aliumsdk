package com.dwao.alium.survey;

import static com.dwao.alium.utils.DeviceInfo.getUserAgent;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dwao.alium.network.CustomNetworkService;
import com.dwao.alium.services.Logger;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class SurveyTracker {
private static final String url = "https://demo.dwao.in/tracker";
//    private static final String BASE_URL="https://tracker.alium.co.in/tracker?";
//
//    private static Uri.Builder getUriBuilder(){
//        return new Uri.Builder().scheme("https"
//                ).authority("demo.dwao.in")
//                .path("tracker");
//    }

//    private static String getAppendableVariables(Map<String, String> parameters){
//        String appendableTrackString="";
//        Iterator<String> keys=parameters.keySet().iterator();
//        while(keys.hasNext()){
//            String key= keys.next();
//            String temp=key+"="+parameters.get(key);
//            appendableTrackString=appendableTrackString+"&"+temp;
//        }
//        return appendableTrackString.replace(" ", "%20");
//    }
    public static void trackWithAlium(Context context,  Map<String, Object> parameters ) {
        try{
            Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                Object value = entry.getValue();

                if (value == null ||
                        (value instanceof String && ((String) value).trim().isEmpty()) ||
                        (value instanceof Collection && ((Collection<?>) value).isEmpty()) ||
                        (value instanceof Map && ((Map<?, ?>) value).isEmpty())) {

                    iterator.remove(); // Safe removal for API 21
                }
            }
            CustomNetworkService.postTrackRequest(url, parameters);
//            volleyService.loadRequestWithVolley(  getUrl(context, parameters) );

        }catch(Exception e){
            Logger.log(Logger.LogLevel.ERROR,"tracker", e.toString());
        }
    }
//    @NonNull
//    private static String getUrl(Context context, Map<String, String> parameters ){
//        Uri.Builder builder=getUriBuilder();
//        builder.appendQueryParameter("userAgent", getUserAgent(context));
//        return builder.build().toString()+getAppendableVariables(parameters);
//    }
}
