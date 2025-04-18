package com.dwao.alium.listeners;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface SurveyConfigResponseListener {
    @GET
    Call<JSONObject> configResponse(@Url String anEmptyString);

}
