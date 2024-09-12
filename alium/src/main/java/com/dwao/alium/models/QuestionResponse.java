package com.dwao.alium.models;

import java.io.Serializable;

public class QuestionResponse implements Serializable {
    int questionId, indexOfSelectedAnswer=0;

    public int getIndexOfSelectedAnswer() {
        return indexOfSelectedAnswer;
    }

    public void setIndexOfSelectedAnswer(int indexOfSelectedAnswer) {
        this.indexOfSelectedAnswer = indexOfSelectedAnswer;
    }

    String questionResponse="", ResponseType="";

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionResponse() {
        return questionResponse;
    }

    public void setQuestionResponse(String questionResponse) {
        this.questionResponse = questionResponse;
    }

    public String getResponseType() {
        return ResponseType;
    }

    public void setResponseType(String responseType) {
        ResponseType = responseType;
    }
}
