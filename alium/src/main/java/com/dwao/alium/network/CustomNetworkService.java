package com.dwao.alium.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dwao.alium.listeners.NetworkCallback;
import com.dwao.alium.listeners.ResponseListener;
import com.dwao.alium.services.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CustomNetworkService {

    public static void postTrackRequest(String url, Map<String, Object> params){

       CustomNetworkClient.post(url, new JSONObject(params).toString(),
               new NetworkCallback(){

           @Override
           public void onSuccess(String response) {
               Logger.log(Logger.LogLevel.INFO,"track","track request successfull for paylaod: "+params.toString());
           }

           @Override
           public void onError(Exception e) {
               Logger.log(Logger.LogLevel.INFO,"track","track request failed with error: "+e.toString()+ "for paylaod: "+params.toString());


           }
       });
    }
    public static void getFollowUpQuestion(String url, Map<String, Object> params, ResponseListener listener ){
        CustomNetworkClient.post(url, new JSONObject(params).toString(),
                new NetworkCallback(){

                    @Override
                    public void onSuccess(String response) {
                        Logger.log(Logger.LogLevel.INFO,"followUp","followUp request successfull for paylaod: "+params.toString());
                        try{
                            listener.onResponseReceived(new JSONObject(response));

                        }catch (JSONException e){
                            listener.onRequestFailed(e);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.log(Logger.LogLevel.INFO,"followUp","followUp request failed with error: "+e.toString()+ "for paylaod: "+params.toString());
                        listener.onRequestFailed(e);

                    }
                });
    }
    public static void getNetworkData(  String url, ResponseListener responseListener){
        Log.d("networkReq", "making network request to.."+url);
       CustomNetworkClient.get(url, new NetworkCallback() {
           @Override
           public void onSuccess(String response) {
              try{
                  responseListener.onResponseReceived(new JSONObject(response));

              }catch (JSONException e){
                  responseListener.onRequestFailed(e);
              }
           }

           @Override
           public void onError(Exception e) {
            responseListener.onRequestFailed(e);

           }
       });

    }
}
