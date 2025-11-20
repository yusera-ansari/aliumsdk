package com.dwao.alium.models;

import java.util.HashMap;
import java.util.Map;

public class FollowupHistory {

    private String ai_question;
    private String user_answer;

    public FollowupHistory(String ai_question, String user_answer) {
        this.ai_question = ai_question;
        this.user_answer = user_answer;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ai_question", ai_question);
        map.put("user_answer", user_answer);
        return map;
    }
}
