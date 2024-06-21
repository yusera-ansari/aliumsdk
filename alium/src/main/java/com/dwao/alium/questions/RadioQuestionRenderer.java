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
import com.dwao.alium.adapters.RadioBtnAdapter;
import com.dwao.alium.listeners.RadioClickListener;
import com.dwao.alium.models.QuestionResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RadioQuestionRenderer implements QuestionRenderer {

    JSONArray responseOptJSON;
    private RadioBtnAdapter adapter;
    JSONObject surveyUi;
    public RadioQuestionRenderer setSurveyUi(JSONObject surveyUi){
        this.surveyUi=surveyUi;
        return this;
    }
    public RadioQuestionRenderer setOptions(JSONArray options){
        responseOptJSON=options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout,
                               QuestionResponse currentQuestionResponse, View nextQuestionBtn) {

        List<String> responseOptions=new ArrayList<>();
       try{
           for (int i=0; i<responseOptJSON.length(); i++){
               responseOptions.add(responseOptJSON.getString(i));
           }
       }catch (Exception e){
           Log.e("RadioQuestionRenderer", "Radio options invalid");
           Log.e("RadioQuestionRenderer", e.toString());
       }
        View radioQues= LayoutInflater.from(context).inflate(R.layout.radio_ques, null);
//                if(surveyUi!=null)currentQuestion.setTextColor(Color.parseColor(surveyUi
//                        .getString("question")));

        RecyclerView radioBtnRecyView=radioQues.findViewById(R.id.radio_btn_rec_view);
        radioBtnRecyView.setLayoutManager(new LinearLayoutManager(context));

        RadioClickListener radioClickListener=new RadioClickListener() {
            @Override
            public void onClick(int position) {
                radioBtnRecyView.post(new Runnable() {
                    @Override
                    public void run() {

                        adapter.updateCheckedItem(position);
                        setCtaEnabled(nextQuestionBtn,
                                !currentQuestionResponse.getQuestionResponse().isEmpty());
                    }
                });
            }
        };
        this.adapter=new RadioBtnAdapter(responseOptions,radioClickListener,
                currentQuestionResponse, surveyUi );
        radioBtnRecyView.setAdapter(adapter);

        layout.addView(radioQues);
    }
}
