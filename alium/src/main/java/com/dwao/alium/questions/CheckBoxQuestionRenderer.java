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
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxQuestionRenderer implements QuestionRenderer {
    private CheckBoxRecyViewAdapter checkBoxRecyViewAdapter;
    List responseOpt ;

   ThemeColors themeColors;
    private Question currentquestion;
    public Question getCurrentquestion() {
        return currentquestion;
    }

    public CheckBoxQuestionRenderer setCurrentquestion(Question currentquestion) {
        this.currentquestion = currentquestion;
        return this.setOptions(this.currentquestion
                .getResponseOptions())
                .setRequired(this.currentquestion.getQuestionSetting().getRequired());

    }

    private boolean isRequired = false;


    public CheckBoxQuestionRenderer setRequired(boolean required) {
        isRequired = required;
        return this;
    }
    public CheckBoxQuestionRenderer setTheme(ThemeColors themeColors){
        this.themeColors = themeColors;
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
        TextInputLayout textInputLayout = checkBoxQues.findViewById(R.id.checkbox_text_input_layout);
        CheckBoxClickListener checkBoxClickListener=new CheckBoxClickListener() {
            @Override
            public void onClick(int position, boolean selected, List<Integer> selectedIndex) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        checkBoxRecyViewAdapter.updateCheckedItem(position, selected);
                      if(isRequired){
                          setCtaEnabled(nextQuestionBtn,
                                  !currentQuestionResponse.getQuestionResponse().isEmpty());
                      }
                        if(currentquestion.getQuestionSetting().getOtherOption()){
                            if (selected && position == responseOpt.size() - 1) {
                                textInputLayout.setVisibility(View.VISIBLE);
                            } else if (position == responseOpt.size() - 1) {
                                textInputLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
            }
        };
        checkBoxRecyViewAdapter=new CheckBoxRecyViewAdapter(responseOpt,
                checkBoxClickListener, currentQuestionResponse, themeColors);
        recyclerView.setAdapter(checkBoxRecyViewAdapter);
        layout.addView(checkBoxQues);

    }
}
