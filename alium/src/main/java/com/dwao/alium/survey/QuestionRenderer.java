package com.dwao.alium.survey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dwao.alium.models.QuestionResponse;

public interface QuestionRenderer {

    public void renderQuestion(Context context, ViewGroup layout, QuestionResponse currentQuestionResponse,
                               View nextQuestionBtn);
}
