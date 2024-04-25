package com.dwao.alium.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Survey {
    @SerializedName("surveyQuestions")
    List<Question> questions;

    @Override
    public String toString() {
        return "Survey {" +
                "questions=" + questions +
                ", surveyInfo=" + surveyInfo +
                '}';
    }

    SurveyInfo surveyInfo=new SurveyInfo();
    SurveyUI surveyUI=new SurveyUI();

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public SurveyInfo getSurveyInfo() {
        return surveyInfo;
    }

    public void setSurveyInfo(SurveyInfo surveyInfo) {
        this.surveyInfo = surveyInfo;
    }

    private class SurveyUI{
        String backgroundColor, borderColor, question, options;
        public NextCta nextCta=new NextCta();

        @Override
        public String toString() {
            return "SurveyUI{" +
                    "backgroundColor='" + backgroundColor + '\'' +
                    ", borderColor='" + borderColor + '\'' +
                    ", question='" + question + '\'' +
                    ", options='" + options + '\'' +
                    ", nextCta=" + nextCta +
                    '}';
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public NextCta getNextCta() {
            return nextCta;
        }

        public void setNextCta(NextCta nextCta) {
            this.nextCta = nextCta;
        }

        private class NextCta{
            String textColor;
            String backgroundColor;

            public String getTextColor() {
                return textColor;
            }

            public void setTextColor(String textColor) {
                this.textColor = textColor;
            }

            public String getBackgroundColor() {
                return backgroundColor;
            }

            public void setBackgroundColor(String backgroundColor) {
                this.backgroundColor = backgroundColor;
            }



        }
    }
    private class SurveyInfo {
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
}
