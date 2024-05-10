package com.dwao.alium.survey;

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
import com.dwao.alium.adapters.RadioBtnAdapter;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.models.QuestionResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxQuestionRenderer implements  QuestionRenderer{
    private CheckBoxRecyViewAdapter checkBoxRecyViewAdapter;
    JSONArray responseOptJSON;
    JSONObject surveyUi;
    public CheckBoxQuestionRenderer setSurveyUi(JSONObject surveyUi){
        this.surveyUi=surveyUi;
        return this;
    }
    public CheckBoxQuestionRenderer setOptions(JSONArray options){
        responseOptJSON=options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout, QuestionResponse currentQuestionResponse, View nextQuestionBtn) {
        List<String> responseOptions=new ArrayList<>();
        try{
        for (int i=0; i<responseOptJSON.length(); i++){
            responseOptions.add(responseOptJSON.getString(i));
        }
        }catch (Exception e){
            Log.e("CheckBoxQuestRenderer", "Radio options invalid");
            Log.e("CheckBoxQuestRenderer", e.toString());
        }
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
        checkBoxRecyViewAdapter=new CheckBoxRecyViewAdapter(responseOptions,
                checkBoxClickListener, currentQuestionResponse, surveyUi);
        recyclerView.setAdapter(checkBoxRecyViewAdapter);
        layout.addView(checkBoxQues);

    }
}
