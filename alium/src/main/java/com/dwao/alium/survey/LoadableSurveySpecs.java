package com.dwao.alium.survey;


import android.net.Uri;

import java.io.Serializable;

public class LoadableSurveySpecs implements Serializable {
    String key, surveyFreq, thankYouMsg;
    String uri;



    public LoadableSurveySpecs(String key, String surveyFreq, String uri, String thankYouMsg) {
        this.key = key;
        this.surveyFreq = surveyFreq;
        this.uri = uri;
        this.thankYouMsg=thankYouMsg;
    }
}
