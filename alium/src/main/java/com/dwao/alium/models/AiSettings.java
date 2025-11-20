package com.dwao.alium.models;

import java.io.Serializable;

public class AiSettings implements Serializable {
    boolean enabled = false;
    int maxFrequency = 0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "AiSettings{" +
                "enabled=" + enabled +
                ", maxFrequency=" + maxFrequency +
                '}';
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }
}
