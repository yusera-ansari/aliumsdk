package com.dwao.alium.listeners;

import com.dwao.alium.models.AiFollowup;

public interface FollowUpCallback {
    void onSuccess(AiFollowup response);
    void onError(Exception e);
}

