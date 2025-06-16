package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.dwao.alium.R;
import com.dwao.alium.adapters.NpsGridViewAdapter;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NPSQuestionRenderer implements QuestionRenderer {

    private NpsGridViewAdapter npsGridViewAdapter;
    List<String> responseOpt;
    ThemeColors themeColors;
    private boolean isRequired = false;


    public NPSQuestionRenderer setRequired(boolean required) {
        isRequired = required;
        return this;
    }

    public NPSQuestionRenderer setTheme(ThemeColors themeColors){
        this.themeColors=themeColors;
        return this;
    }
    public NPSQuestionRenderer setOptions(List<String> options){
        responseOpt=options;
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
                        if(isRequired){
                            setCtaEnabled(nextQuestionBtn, !currentQuestionResponse
                                    .getQuestionResponse().isEmpty());
                        }
                    }
                });
            }
        };
        npsGridViewAdapter=new NpsGridViewAdapter(context,responseOpt ,listener, currentQuestionResponse, themeColors);
        npsRecView.setAdapter( npsGridViewAdapter);
        layout.addView(npsQues);

    }
}
