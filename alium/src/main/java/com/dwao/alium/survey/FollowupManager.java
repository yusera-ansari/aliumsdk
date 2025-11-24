package com.dwao.alium.survey;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.os.Handler;
import android.os.Looper;

import com.dwao.alium.listeners.FollowUpCallback;
import com.dwao.alium.listeners.FollowupHandlerCallback;
import com.dwao.alium.listeners.ResponseListener;
import com.dwao.alium.models.AiFollowup;
import com.dwao.alium.models.FollowupHistory;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.Survey;
import com.dwao.alium.network.CustomNetworkService;
import com.dwao.alium.questions.FollowupTextQuestionRenderer;
import com.dwao.alium.services.Logger;
import com.dwao.alium.utils.jsonhandlers.AliumJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowupManager {

    int followUpIndex = -1;
    private final String url = "https://api.aliumsurvey.com/api/v1/public/surveys/ai-followup";
    List<FollowupHistory> followupHistoryList = new ArrayList<>();
    AiFollowup aiFollowup;

    FollowupRepository repository = new FollowupRepository();
    Survey survey;

    FollowupManager(
            Survey survey
    ) {
        this.survey = survey;


    }

    protected void storePreviousFollowUp() {
        if (aiFollowup != null) {
            if (followUpIndex > -1 && aiFollowup != null) {
                followupHistoryList.add(new FollowupHistory(aiFollowup.getFollowupQuestion(), aiFollowup.getResponse()));
            }
        }
    }

    protected void incrementFollowupIndex() {
        followUpIndex++;
    }

    protected boolean shouldStop(int freq) {
        incrementFollowupIndex();
        if (followUpIndex >= freq || (  aiFollowup!=null &&!aiFollowup.isShouldFollowup())) {
            Logger.log(Logger.LogLevel.DEBUG, "foll-up", "freq reached");
            aiFollowup = null;
            followUpIndex = -1;
            followupHistoryList = new ArrayList<>();
            return true;
        }
        return false;
    }

    protected void getFollowUpQuestion(int maxFollowups, int currentIndx, String originalResponse, FollowUpCallback callback) {
        Map<String, Object> data = new HashMap<>();
        Question curQues = survey.getQuestions().get(currentIndx);
        data.put("survey_id", survey.getSurveyInfo().getSurveyId());
        data.put("org_id", survey.getSurveyInfo().getOrgId());
        data.put("question_id", curQues.getId());
        data.put("question_text", curQues.getQuestion());
//        data.put("original_response", currentQuestionResponse.getQuestionResponse());
        data.put("original_response", originalResponse);
        data.put("survey_context", new ArrayList<>());        // empty list
        data.put("current_followup_count", followUpIndex);
        data.put("max_followups", maxFollowups);

        List<Map<String, Object>> historyMapList = new ArrayList<>();

        for (FollowupHistory item : followupHistoryList) {
            historyMapList.add(item.toMap());
        }

        data.put("conversation_history", historyMapList);  // empty list
        Logger.log(Logger.LogLevel.DEBUG, "foll-up", "sending followup requesr");
        repository.fetchFollowup(
                url,
                data,
                new FollowUpCallback() {
                    @Override
                    public void onSuccess(AiFollowup response) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                aiFollowup = response;
                                Logger.log(Logger.LogLevel.DEBUG, "FollowUp", aiFollowup.toString());
                                callback.onSuccess(response);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                });
    }
}
