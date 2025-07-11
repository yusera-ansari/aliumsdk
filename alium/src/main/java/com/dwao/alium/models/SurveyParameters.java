package com.dwao.alium.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SurveyParameters implements Serializable {
    public String screenName;
    public Map<String, String> customerVariables;
    public SurveyParameters(String screenName){
        this.screenName=screenName;
        this.customerVariables=new HashMap<>();
    }
    public SurveyParameters(String screenName, Map customerVariables){
        this.screenName=screenName;
        this.customerVariables=customerVariables;
    }
}