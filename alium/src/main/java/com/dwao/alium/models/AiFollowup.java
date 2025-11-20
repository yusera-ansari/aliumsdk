package com.dwao.alium.models;

public class AiFollowup {
    boolean shouldFollowup=true;
    String followupQuestion;
    int remainingFollowups=0;
    String response ="";

    @Override
    public String toString() {
        return "AiFollowup{" +
                "shouldFollowup=" + shouldFollowup +
                ", followupQuestion='" + followupQuestion + '\'' +
                ", remainingFollowups=" + remainingFollowups +
                ", response='" + response + '\'' +
                '}';
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isShouldFollowup() {
        return shouldFollowup;
    }

    public void setShouldFollowup(boolean shouldFollowup) {
        this.shouldFollowup = shouldFollowup;
    }

    public String getFollowupQuestion() {
        return followupQuestion;
    }

    public void setFollowupQuestion(String followupQuestion) {
        this.followupQuestion = followupQuestion;
    }

    public int getRemainingFollowups() {
        return remainingFollowups;
    }

    public void setRemainingFollowups(int remainingFollowups) {
        this.remainingFollowups = remainingFollowups;
    }
}
