package com.dwao.alium.models;

import java.io.Serializable;
import java.util.List;


public class Question implements Serializable {
    int id;
    String question;
    String responseType;
    List<String> responseOptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public List<String> getResponseOptions() {
        return responseOptions;
    }

    public void setResponseOptions(List<String> responseOptions) {
        this.responseOptions = responseOptions;
    }

    public List<Integer> getConditionMapping() {
        return conditionMapping;
    }

    public void setConditionMapping(List<Integer> conditionMapping) {
        this.conditionMapping = conditionMapping;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", responseType='" + responseType + '\'' +
                ", responseOptions=" + responseOptions +
                ", conditionMapping=" + conditionMapping +
                '}';
    }

    List<Integer> conditionMapping;
}
