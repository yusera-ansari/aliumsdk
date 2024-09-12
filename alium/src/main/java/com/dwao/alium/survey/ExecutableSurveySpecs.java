package com.dwao.alium.survey;

import android.util.Log;

import com.dwao.alium.models.Survey;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExecutableSurveySpecs {



    private LoadableSurveySpecs loadableSurveySpecs;

    public Survey survey=null;

    public LoadableSurveySpecs getLoadableSurveySpecs() {
        return loadableSurveySpecs;
    }

    public void setLoadableSurveySpecs(LoadableSurveySpecs loadableSurveySpecs) {
        this.loadableSurveySpecs = loadableSurveySpecs;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public ExecutableSurveySpecs(Survey survey, LoadableSurveySpecs loadableSurveySpecs) {
        this.survey=survey;
        this.loadableSurveySpecs = loadableSurveySpecs;
    }
}