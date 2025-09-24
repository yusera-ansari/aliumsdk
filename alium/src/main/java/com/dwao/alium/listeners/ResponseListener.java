package com.dwao.alium.listeners;

import org.json.JSONObject;

public interface ResponseListener {
    public void onResponseReceived(JSONObject jsonObject);
    public void onRequestFailed(Exception e);
}
