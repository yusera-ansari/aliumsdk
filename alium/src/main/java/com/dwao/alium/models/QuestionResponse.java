package com.dwao.alium.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionResponse implements Serializable {
    int questionId, indexOfSelectedAnswer=0;
    List<Integer> indexOfSelectedAnswers= new ArrayList<>( );

    public int getIndexOfSelectedAnswer() {
        return indexOfSelectedAnswer;
    }

    public List<Integer> getIndexOfSelectedAnswers() {
        return indexOfSelectedAnswers;
    }

    @Override
    public String toString() {
        return "QuestionResponse{" +
                "questionId=" + questionId +
                ", indexOfSelectedAnswer=" + indexOfSelectedAnswer +
                ", indexOfSelectedAnswers=" + (indexOfSelectedAnswers) +
                ", questionResponse='" + questionResponse + '\'' +
                ", ResponseType='" + ResponseType + '\'' +
                '}';
    }

    public void setIndexOfSelectedAnswers(List<Integer> indexOfSelectedAnswers) {
        this.indexOfSelectedAnswers = indexOfSelectedAnswers;
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
