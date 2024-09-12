package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dwao.alium.R;
import com.dwao.alium.adapters.CheckBoxRecyViewAdapter;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxQuestionRenderer implements QuestionRenderer {
    private CheckBoxRecyViewAdapter checkBoxRecyViewAdapter;
    List responseOpt ;
    Survey.SurveyUI surveyUi;
    public CheckBoxQuestionRenderer setSurveyUi(Survey.SurveyUI surveyUi){
        this.surveyUi=surveyUi;
        return this;
    }
    public CheckBoxQuestionRenderer setOptions(List options){
        responseOpt =options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout, QuestionResponse currentQuestionResponse, View nextQuestionBtn) {

        View checkBoxQues= LayoutInflater.from(context).inflate(R.layout.checkbox_type_ques, null);
        RecyclerView recyclerView=checkBoxQues.findViewById(R.id.checkbox_recy_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        CheckBoxClickListener checkBoxClickListener=new CheckBoxClickListener() {
            @Override
            public void onClick(int position, boolean selected) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        checkBoxRecyViewAdapter.updateCheckedItem(position, selected);
                        setCtaEnabled(nextQuestionBtn,
                                !currentQuestionResponse.getQuestionResponse().isEmpty());
                    }
                });
            }
        };
        checkBoxRecyViewAdapter=new CheckBoxRecyViewAdapter(responseOpt,
                checkBoxClickListener, currentQuestionResponse, surveyUi);
        recyclerView.setAdapter(checkBoxRecyViewAdapter);
        layout.addView(checkBoxQues);

    }
}
