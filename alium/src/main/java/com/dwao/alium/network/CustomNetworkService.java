package com.dwao.alium.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dwao.alium.listeners.NetworkCallback;
import com.dwao.alium.listeners.VolleyResponseListener;

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
    public static void getNetworkData(  String url, VolleyResponseListener volleyResponseListener){
        Log.d("networkReq", "making network request to.."+url);
       CustomNetworkClient.get(url, new NetworkCallback() {
           @Override
           public void onSuccess(String response) {
              try{
                  Log.d("response", "retrived json "+ response);
                  volleyResponseListener.onResponseReceived(new JSONObject(response));
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
