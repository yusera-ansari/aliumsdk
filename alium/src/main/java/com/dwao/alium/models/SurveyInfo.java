package com.dwao.alium.models;

import com.google.gson.annotations.SerializedName;

public class SurveyInfo {
    String orgId, customerId, surveyId, language, position, background, uniqueidentifier, theme, branding;

    public String getOrgId() {
        return orgId;
    }

    @Override
    public String toString() {
        return "SurveyInfo{" +
                "orgId='" + orgId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", surveyId='" + surveyId + '\'' +
                ", language='" + language + '\'' +
                ", position='" + position + '\'' +
                ", background='" + background + '\'' +
                ", uniqueidentifier='" + uniqueidentifier + '\'' +
                ", theme='" + theme + '\'' +
                ", branding='" + branding + '\'' +
                '}';
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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
