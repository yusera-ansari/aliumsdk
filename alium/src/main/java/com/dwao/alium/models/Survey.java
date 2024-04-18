package com.dwao.alium.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Survey {
    @SerializedName("surveyQuestions")
    List<Question> questions;

    @Override
    public String toString() {
        return "Survey {" +
                "questions=" + questions +
                ", surveyInfo=" + surveyInfo +
                '}';
    }

    SurveyInfo surveyInfo;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public SurveyInfo getSurveyInfo() {
        return surveyInfo;
    }

    public void setSurveyInfo(SurveyInfo surveyInfo) {
        this.surveyInfo = surveyInfo;
    }
}
