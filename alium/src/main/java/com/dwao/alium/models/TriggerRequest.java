package com.dwao.alium.models;

import com.dwao.alium.survey.SurveyParameters;

public class TriggerRequest{
    public Object object;
    public SurveyParameters surveyParameters;
    public TriggerRequest(Object object, SurveyParameters surveyParameters){
        this.surveyParameters=surveyParameters;
        this.object=object;
    }
}