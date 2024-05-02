package com.dwao.alium.survey;

import java.util.HashMap;
import java.util.Map;

public class SurveyParameters {
    String screenName;
    Map<String, String> customerVariables;
    public SurveyParameters(String screenName, Map customerVariables){
        this.screenName=screenName;
        this.customerVariables=customerVariables;
    }
}