package com.dwao.alium.models;

import java.io.Serializable;

public class QuestionSetting implements Serializable {
    Boolean required = true; //req
    String ratingType="star";
    Boolean otherOption = false;

    @Override
    public String toString() {
        return "QuestionSetting{" +
                "required=" + required +
                ", ratingType='" + ratingType + '\'' +
                ", otherOption=" + otherOption +
                '}';
    }

    public String getRatingType() {
        return ratingType;
    }

    public Boolean getOtherOption() {
        return otherOption;
    }

    public void setOtherOption(Boolean otherOption) {
        this.otherOption = otherOption;
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
