package com.dwao.alium.models;


import java.io.Serializable;
import java.util.List;

public class Survey implements Serializable {
    List<Question> questions;

    @Override
    public String toString() {
        return "Survey {" +
                "questions=" + questions +
                ", surveyInfo=" + surveyInfo +
                '}';
    }

    SurveyInfo surveyInfo=new SurveyInfo();





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

    public class SurveyUI implements Serializable{
        String backgroundColor="#ffffff", borderColor="#ffffff", question="#000000", options="#000000";
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

        public class NextCta implements Serializable{
            String textColor="#000000";
            String backgroundColor="#ffffff";

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

}
