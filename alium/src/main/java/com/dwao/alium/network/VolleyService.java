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
    private RequestQueue queue;
    public static final String SURVEY_REQUEST_TAG="SURVEY_REQ";
    private static VolleyService instance;
    private  Context context;
    public static VolleyService getInstance(){
        return instance;
    }
    public synchronized static VolleyService getInstance(Context context) {
        if(instance==null){
            synchronized (VolleyService.class){
                if(instance==null){
                    instance=new VolleyService(context);
                }
            }
        }
        return instance;
    }
    private VolleyService(Context context){
        this.context=context;
    }
    private VolleyService(){

    }

     public RequestQueue getSurveyQueue(){
         return this.queue;
     }

    public void loadRequestWithVolley(  String url){
        Log.d("post_url", url);
        // Instantiate the RequestQueue.
        RequestQueue config_queue = Volley.newRequestQueue(context);
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
        config_queue.add(stringRequest);

    }
   public  void callVolley(  String url, VolleyResponseListener volleyResponseListener){
       // Instantiate the RequestQueue.
         queue = Volley.newRequestQueue(context);
    Log.d("fetch-main", url);
       // Request a string response from the provided URL.
       StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       Log.d("response-survey", response);
                       try {
                           JSONObject jsonObject=new JSONObject(response);
                           volleyResponseListener.onResponseReceived(jsonObject);

                       } catch (JSONException e) {
                        Log.i("surveyResponseExc", e.toString());
                       }

                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.d("error", error.toString());
           }
       });
stringRequest.addMarker("SURVEY_REQ");
// Add the request to the RequestQueue.
       queue.add(stringRequest);

   }
}
