package com.dwao.alium.models;

import java.io.Serializable;

public class QuestionSetting implements Serializable {
    Boolean required = true; //req

    @Override
    public String toString() {
        return "QuestionSetting{" +
                "required=" + required +
                '}';
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
