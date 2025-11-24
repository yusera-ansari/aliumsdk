package com.dwao.alium.survey;

import static android.view.View.GONE;
import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.os.Handler;
import android.os.Looper;

import com.dwao.alium.listeners.FollowUpCallback;
import com.dwao.alium.listeners.ResponseListener;
import com.dwao.alium.models.AiFollowup;
import com.dwao.alium.network.CustomNetworkService;
import com.dwao.alium.services.Logger;
import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FollowupRepository {
    void fetchFollowup(String url, Map<String, Object> data, FollowUpCallback callback){

        CustomNetworkService.getFollowUpQuestion(url, data, new ResponseListener() {
            @Override
            public void onResponseReceived(JSONObject jsonObject) {
               try
               {
                   AiFollowup aiFollowup = AliumJSONParser.getAiFollowupFromJson(jsonObject);
                   if(aiFollowup.getFollowupQuestion().trim().isEmpty()) {
                       callback.onError(new JSONException("No followup question"));
                       return;
                   }
                   callback.onSuccess(aiFollowup);

               }catch (Exception e){
                   callback.onError(e);
               }

            }

            @Override
            public void onRequestFailed(Exception e) {
                callback.onError(e);

            }
        });
    }
}
