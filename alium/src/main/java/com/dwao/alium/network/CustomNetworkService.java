package com.dwao.alium.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dwao.alium.listeners.NetworkCallback;
import com.dwao.alium.listeners.ResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CustomNetworkService {

    public static void postTrackRequest(String url, Map<String, Object> params){

       CustomNetworkClient.post(url, new JSONObject(params).toString(),
               new NetworkCallback(){

           @Override
           public void onSuccess(String response) {
               Log.d("track","track request successfull for paylaod: "+params.toString());
           }

           @Override
           public void onError(Exception e) {
               Log.e("track","track request failed with error: "+e.toString()+ "for paylaod: "+params.toString());


           }
       });
    }
    public static void getNetworkData(  String url, ResponseListener responseListener){
        Log.d("networkReq", "making network request to.."+url);
       CustomNetworkClient.get(url, new NetworkCallback() {
           @Override
           public void onSuccess(String response) {
              try{
                  Log.d("response", "retrived json "+ Thread.currentThread().getName() + response);
                  responseListener.onResponseReceived(new JSONObject(response));

              }catch (JSONException e){
                  Log.e("getNetworkData","couldn't parse json..");
              }
           }

           @Override
           public void onError(Exception e) {

           }
       });

    }
}
