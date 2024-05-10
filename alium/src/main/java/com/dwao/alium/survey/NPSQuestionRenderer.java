package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.dwao.alium.R;
import com.dwao.alium.adapters.CheckBoxRecyViewAdapter;
import com.dwao.alium.adapters.NpsGridViewAdapter;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.models.QuestionResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class NPSQuestionRenderer implements QuestionRenderer{

    private NpsGridViewAdapter npsGridViewAdapter;
    JSONArray responseOptJSON;
    JSONObject surveyUi;
    public NPSQuestionRenderer setSurveyUi(JSONObject surveyUi){
        this.surveyUi=surveyUi;
        return this;
    }
    public NPSQuestionRenderer setOptions(JSONArray options){
        responseOptJSON=options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout,
                               QuestionResponse currentQuestionResponse, View nextQuestionBtn) {
        View npsQues= LayoutInflater.from(context).inflate(R.layout.nps_ques, null);
        GridView npsRecView=npsQues.findViewById(R.id.nps_recy_view);
        NpsOptionClickListener listener=new NpsOptionClickListener() {
            @Override
            public void onClick(int position) {
                npsRecView.post(new Runnable() {
                    @Override
                    public void run() {
                        npsGridViewAdapter.updatedSelectedOption(position);
                        setCtaEnabled(nextQuestionBtn, !currentQuestionResponse
                                .getQuestionResponse().isEmpty());
                    }
                });
            }
        };
        npsGridViewAdapter=new NpsGridViewAdapter(context, listener, currentQuestionResponse, surveyUi);
        npsRecView.setAdapter( npsGridViewAdapter);
        layout.addView(npsQues);

    }
}
