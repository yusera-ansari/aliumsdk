package com.dwao.alium.network;

import android.util.Log;

import com.dwao.alium.listeners.NetworkCallback;
import com.dwao.alium.utils.NetworkUtils;

public class CustomNetworkClient {


        public static void get(String urlString, NetworkCallback callback) {
            new Thread(() -> {
                try {
                    String response = NetworkUtils.makeGetRequest(urlString);
                    if (response != null) {
                        callback.onSuccess(response);
                    } else {
                        callback.onError(new Exception("Empty response"));
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }).start();
        }

        public static void post(String urlString, String jsonBody, NetworkCallback callback) {
            new Thread(() -> {
                try {
                    String response = NetworkUtils.makePostRequest(urlString, jsonBody);
                    if (response != null) {
                        callback.onSuccess(response);
                    } else {
                        callback.onError(new Exception("Empty response"));
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }

            }).start();
        }
    }


