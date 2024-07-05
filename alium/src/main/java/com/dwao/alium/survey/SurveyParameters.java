package com.dwao.alium.survey;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SurveyParameters implements Serializable {
    String screenName;
    Map<String, String> customerVariables;
    public SurveyParameters(String screenName, Map customerVariables){
        this.screenName=screenName;
        this.customerVariables=customerVariables;
    }
}