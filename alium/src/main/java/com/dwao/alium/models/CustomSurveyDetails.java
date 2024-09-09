package com.dwao.alium.models;

import java.io.Serializable;

public class CustomSurveyDetails implements Serializable {
    CustomSurveyDetails(){}
    String freq, startOn, endOn;

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getStartOn() {
        return startOn;
    }

    public void setStartOn(String startOn) {
        this.startOn = startOn;
    }

    public String getEndOn() {
        return endOn;
    }

    public void setEndOn(String endOn) {
        this.endOn = endOn;
    }
}
