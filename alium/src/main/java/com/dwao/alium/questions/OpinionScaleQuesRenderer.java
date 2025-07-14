package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.dwao.alium.R;
import com.dwao.alium.adapters.OpinionScaleGridAdapter;
import com.dwao.alium.listeners.OpinionClickListener;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.ThemeColors;

import org.json.JSONArray;

import java.util.List;

public class OpinionScaleQuesRenderer implements QuestionRenderer{
    List<String> respOptions;
    ThemeColors themeColors;
    private boolean isRequired = false;
    private Question currentquestion;
    public Question getCurrentquestion() {
        return currentquestion;
    }

    public OpinionScaleQuesRenderer setCurrentquestion(Question currentquestion) {
        this.currentquestion = currentquestion;
        return this .setRequired(this.currentquestion.getQuestionSetting().getRequired())
                .setOptions(this.currentquestion.getResponseOptions());
    }

    public OpinionScaleQuesRenderer setRequired(boolean required) {
        isRequired = required;
        return this;
    }
    OpinionScaleGridAdapter adapter;
    public OpinionScaleQuesRenderer setTheme(ThemeColors themeColors){
        this.themeColors=themeColors;
        return this;
    }
    public OpinionScaleQuesRenderer setOptions(List<String> options){
        this.respOptions=options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout, QuestionResponse currentQuestionResponse, View nextQuestionBtn) {

        View opinionScaleQues= LayoutInflater.from(context).inflate(R.layout.opinion_scale_ques, null);
        GridView opinionScaleGrid=opinionScaleQues.findViewById(R.id.opinion_scale_ques);
        if(respOptions!=null)opinionScaleGrid.setNumColumns(respOptions.size());
        if(currentQuestionResponse.getQuestionResponse().isEmpty() && currentQuestionResponse.getIndexOfSelectedAnswer()==0){
            currentQuestionResponse.setIndexOfSelectedAnswer(-1);
        }
        OpinionClickListener listener=new OpinionClickListener() {
            @Override
            public void onClick(int position) {
                adapter.updatedSelectedOption(position);
                if(isRequired){
                    setCtaEnabled(nextQuestionBtn, !currentQuestionResponse
                            .getQuestionResponse().isEmpty());
                }
            }
        };
          adapter=new OpinionScaleGridAdapter(context, respOptions,currentQuestionResponse,listener,themeColors);
        opinionScaleGrid.setAdapter(adapter);
        layout.addView(opinionScaleQues);


    }
}
