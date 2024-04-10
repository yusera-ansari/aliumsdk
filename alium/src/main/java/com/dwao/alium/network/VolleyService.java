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

//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;

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
// Add the request to the RequestQueue.
        queue.add(stringRequest);

//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("text/plain");
////        RequestBody body = RequestBody.create("",mediaType);
//        RequestBody body = RequestBody.create("",mediaType);
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .addHeader("Content-Type", "text/plain")
//                .build();
//        okhttp3.Response response = null;
//       Thread runnable=new Thread(new Runnable() {
//           @Override
//           public void run() {
//               try {
//                   okhttp3.Response response  = client.newCall(request).execute();
//                   Log.d("OKHTTP", response.toString());
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           }
//       });
//      runnable.start();

    }
   public  void callVolley(Context context, String url, VolleyResponseListener volleyResponseListener){
       // Instantiate the RequestQueue.
       RequestQueue queue = Volley.newRequestQueue(context);
//       String url = "https://assets.alium.co.in/cmmn/cstjn/cstjn_1038.json";

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
                       // Display the first 500 characters of the response string.
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
