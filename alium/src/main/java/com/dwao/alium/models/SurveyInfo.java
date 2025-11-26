package com.dwao.alium.models;

import java.io.Serializable;

//survey specific info within individual survey json file
public class SurveyInfo implements Serializable {
    //          "orgId": "128",
//                  "orgName": "ASDF",
//                  "customerId": null,
//                  "surveyId": "956",
//                  "language": "",
//                  "position": "",
//                  "background": "",
//                  "uniqueidentifier": "",
//                  "elmsltr": "",
//                  "theme": "",
//                  "branding": "",
//                  "type": 1,
//                  "thanksMessage": "",
//                  "vf": "overandover"
    String  orgName, customerId, surveyId, language, position, background, uniqueidentifier, theme, branding,
            type;
    int orgId;

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getOrgId() {
        return orgId;
    }

    @Override
    public String toString() {
        return "SurveyInfo{" +
                "orgName='" + orgName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", surveyId='" + surveyId + '\'' +
                ", language='" + language + '\'' +
                ", position='" + position + '\'' +
                ", background='" + background + '\'' +
                ", uniqueidentifier='" + uniqueidentifier + '\'' +
                ", theme='" + theme + '\'' +
                ", branding='" + branding + '\'' +
                ", type='" + type + '\'' +
                ", orgId=" + orgId +
                ", themeColors=" + themeColors +
                '}';
    }

    //    viewFrequency;
    public ThemeColors themeColors;

    public ThemeColors getThemeColors() {
        return themeColors;
    }

    public void setThemeColors(ThemeColors themeColors) {
        this.themeColors = themeColors;
    }

//    public String getViewFrequency() {
//        return viewFrequency;
//    }
//
//    public void setViewFrequency(String viewFrequency) {
//        this.viewFrequency = viewFrequency;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }



    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getUniqueidentifier() {
        return uniqueidentifier;
    }

    public void setUniqueidentifier(String uniqueidentifier) {
        this.uniqueidentifier = uniqueidentifier;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getBranding() {
        return branding;
    }

    public void setBranding(String branding) {
        this.branding = branding;
    }
}
