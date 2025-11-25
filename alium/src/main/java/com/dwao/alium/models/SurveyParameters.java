package com.dwao.alium.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SurveyParameters implements Serializable {

    public SurveyParameters(String screenName) {
        this.screenName = screenName;
        this.customerVariables = new HashMap<>();
    }

    public SurveyParameters(String screenName, Map customerVariables) {
        this.screenName = screenName;
        this.customerVariables = customerVariables;
    }


    public String screenName;
    public Map<String, String> customerVariables;

    private SurveyParameters(Builder builder) {
        this.screenName = builder.screenName;
        this.customerVariables = builder.customerVariables;
    }

    // ----------- BUILDER ---------------
    public static class Builder {
        private String screenName;
        private Map<String, String> customerVariables = new HashMap<String, String>();

        public Builder(String screenName) {
            this.screenName = screenName;
        }

        public Builder addDim(int number, String value) {
            customerVariables.put("dim" + number, value);
            return this;
        }

        public Builder addCustom(String key, String value) {
            customerVariables.put(key, value);
            return this;
        }

        // Remove empty values before final build
        private void clean() {
            Iterator<Map.Entry<String, String>> it = customerVariables.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String v = entry.getValue();

                if (v == null || v.trim().isEmpty()) {
                    it.remove();
                }
            }
        }

        public SurveyParameters build() {
            clean();
            return new SurveyParameters(this);
        }
    }

}