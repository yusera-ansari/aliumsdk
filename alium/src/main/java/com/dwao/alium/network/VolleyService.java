package com.dwao.alium.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dwao.alium.listeners.VolleyResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyService {
    public void loadRequestWithVolley(Context context, String url){
        Log.d("post_url", url);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("trackWithAlium", "Success: "+response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("trackWithAlium", "error: "+error.toString());
            }
        }) ;
        queue.add(stringRequest);
    }
   public  void callVolley(Context context, String url, VolleyResponseListener volleyResponseListener){
       // Instantiate the RequestQueue.
       RequestQueue queue = Volley.newRequestQueue(context);

       // Request a string response from the provided URL.
       StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       try {
                           JSONObject jsonObject=new JSONObject(response);
                           volleyResponseListener.onResponseReceived(jsonObject);

                       } catch (JSONException e) {
                           throw new RuntimeException(e);
                       }
                       Log.d("response", response);
                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.d("error", error.toString());
           }
       });

// Add the request to the RequestQueue.
       queue.add(stringRequest);
   }
}
