package com.dwao.alium.survey;

import java.io.Serializable;

public class CustomFreqSurveyData implements Serializable {
    public String freq;
    public String startOn;
    public String endOn;

    public CustomFreqSurveyData(String freq, String startOn, String endOn) {
        this.freq = freq;
        this.startOn = startOn;
        this.endOn = endOn;
    }

}
