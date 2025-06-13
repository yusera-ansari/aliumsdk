package com.dwao.alium.models;

public class App{
    public App(){}

    UrlMatch um;

    public String getVf() {
        return vf;
    }

    public void setVf(String vf) {
        this.vf = vf;
    }

    public UrlMatch getUm() {
        return um;
    }

    public void setUm(UrlMatch um) {
        this.um = um;
    }

    @Override
    public String toString() {
        return "App{" +
                "um=" + um +
                ", vf='" + vf + '\'' +
                ", customSurveyDetails=" + customSurveyDetails +
                '}';
    }

    String vf; //View Frequency
    CustomSurveyDetails customSurveyDetails;

    public CustomSurveyDetails getCustomSurveyDetails() {
        return customSurveyDetails;
    }

    public void setCustomSurveyDetails(CustomSurveyDetails customSurveyDetails) {
        this.customSurveyDetails = customSurveyDetails;
    }
}
