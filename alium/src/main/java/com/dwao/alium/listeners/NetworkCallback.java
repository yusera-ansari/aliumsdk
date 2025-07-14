package com.dwao.alium.listeners;

public interface NetworkCallback {
    void onSuccess(String response);
    void onError(Exception e);
}
