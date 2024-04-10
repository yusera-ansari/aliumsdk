package com.dwao.alium.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class FetchJson {
    private FetchJson(){}
        public static JSONObject getFromLocal(InputStream stream){
            try {
                InputStream inputStream=stream;
                int size=inputStream.available();
                byte[] buffer=new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                //fetch json
                String json;
                int max;
                json = new String(buffer, StandardCharsets.UTF_8);
                JSONObject jsonObject=new JSONObject(json);
                return  jsonObject;

            }catch (Exception e){
                Log.e("log from json", ""+e);
                return null;
            }
    }
}
