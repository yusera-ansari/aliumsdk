package com.dwao.alium.survey;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExecutableSurveySpecs {
    private JSONArray surveyQuestions=new JSONArray();
    private JSONObject surveyUi=new JSONObject(), surveyInfo=new JSONObject();



    private LoadableSurveySpecs loadableSurveySpecs;

    public JSONArray getSurveyQuestions() {
        return surveyQuestions;
    }

    public JSONObject getSurveyUi() {
        return surveyUi;
    }

    public JSONObject getSurveyInfo() {
        return surveyInfo;
    }



    public LoadableSurveySpecs getLoadableSurveySpecs() {
        return loadableSurveySpecs;
    }

    public ExecutableSurveySpecs(JSONObject survey, LoadableSurveySpecs loadableSurveySpecs) {
      try {
          if(survey.has("surveyQuestions")){
              surveyQuestions=survey.getJSONArray("surveyQuestions");
          }
//
          if(survey.has("surveyUI")){
              surveyUi=survey.getJSONObject("surveyUI");
          }
          if(survey.has("surveyInfo")){
              surveyInfo=survey.getJSONObject("surveyInfo");
          }
      }catch (Exception e){
          Log.e("ExecutableSurveySpecs", e.toString());
      }
        this.loadableSurveySpecs = loadableSurveySpecs;
    }
}
