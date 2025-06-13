package com.dwao.alium.models;

import java.io.Serializable;

public class QuestionSetting implements Serializable {
    Boolean required = true; //req
    String ratingType="star";

    public String getRatingType() {
        return ratingType;
    }

    @Override
    public String toString() {
        return "QuestionSetting{" +
                "required=" + required +
                ", ratingType='" + ratingType + '\'' +
                '}';
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
