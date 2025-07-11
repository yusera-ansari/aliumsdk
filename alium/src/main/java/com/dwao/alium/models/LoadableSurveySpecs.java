package com.dwao.alium.models;


import java.io.Serializable;

public class LoadableSurveySpecs  implements Serializable {
    public String key, surveyFreq, thankYouMsg;
    public String uri;
    public int currentIndex=0;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
    public CustomFreqSurveyData customSurveyData;

    public LoadableSurveySpecs(String key, String surveyFreq, String uri,  CustomFreqSurveyData customSurveyData) {
        this.key = key;
        this.surveyFreq = surveyFreq;
        this.uri = uri;

        this.customSurveyData=customSurveyData;
    }

}
