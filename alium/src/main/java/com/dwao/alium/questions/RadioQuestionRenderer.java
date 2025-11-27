package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dwao.alium.R;
import com.dwao.alium.adapters.RadioBtnAdapter;
import com.dwao.alium.listeners.RadioClickListener;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.services.Logger;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RadioQuestionRenderer implements QuestionRenderer {

    List responseOpt ;
    private RadioBtnAdapter adapter;
   ThemeColors themeColors;

    private boolean isRequired = false;

    private Question currentquestion;
    public Question getCurrentquestion() {
        return currentquestion;
    }

    public RadioQuestionRenderer setCurrentquestion(Question currentquestion) {
        this.currentquestion = currentquestion;
        return this.setOptions(this.currentquestion
                .getResponseOptions()).setRequired(this.currentquestion.getQuestionSetting().getRequired());

    }
    private RadioQuestionRenderer setRequired(boolean required) {
        isRequired = required;
        return this;
    }
    public RadioQuestionRenderer setTheme( ThemeColors themeColors){
        this.themeColors=themeColors;
        return this;
    }
    private RadioQuestionRenderer setOptions(List options){
        responseOpt =options;
        return this;

    }
    @Override
    public void renderQuestion(Context context, ViewGroup layout,
                               QuestionResponse currentQuestionResponse, View nextQuestionBtn) {


        View radioQues= LayoutInflater.from(context).inflate(R.layout.radio_ques, null);
//

        RecyclerView radioBtnRecyView=radioQues.findViewById(R.id.radio_btn_rec_view);
        radioBtnRecyView.setLayoutManager(new LinearLayoutManager(context));
        TextInputLayout textInputLayout = radioQues.findViewById(R.id.radio_text_input_layout);
        TextInputEditText textInputEditText = radioQues.findViewById(R.id.text_input_edit_text);
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.updateResponse(currentquestion.getQuestionSetting().getOtherOption(),
                        textInputEditText.getText()!=null?textInputEditText.getText().toString():"");
                if(currentquestion.getQuestionSetting().getOtherOption()) {
                    setCtaEnabled(nextQuestionBtn,
                            !textInputEditText.getText().toString().isEmpty());
                }else{
                    setCtaEnabled(nextQuestionBtn,
                            !currentQuestionResponse.getQuestionResponse().isEmpty());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(currentQuestionResponse.getQuestionResponse().isEmpty() && currentQuestionResponse.getIndexOfSelectedAnswer()==0){
            currentQuestionResponse.setIndexOfSelectedAnswer(-1);
        }
        if(currentquestion.getQuestionSetting().getOtherOption()){
            if ( currentQuestionResponse.getIndexOfSelectedAnswer() == responseOpt.size() - 1) {
                textInputLayout.setVisibility(View.VISIBLE);
                textInputEditText.requestFocus();
                setCtaEnabled(nextQuestionBtn,
                        !currentQuestionResponse.getQuestionResponse().isEmpty());
            } else  {
                textInputLayout.setVisibility(View.GONE);
                textInputEditText.clearFocus();
            }
        }

        RadioClickListener radioClickListener=new RadioClickListener() {
            @Override
            public void onClick(int position) {
                radioBtnRecyView.post(new Runnable() {
                    @Override
                    public void run() {

                        adapter.updateCheckedItem(position);

                        adapter.updateResponse(currentquestion.getQuestionSetting().getOtherOption(),
                                (textInputEditText.getText()!=null)?textInputEditText.getText().toString():"");
                        if(isRequired){
                         if(currentquestion.getQuestionSetting().getOtherOption() && position == responseOpt.size() - 1) {
                             setCtaEnabled(nextQuestionBtn,
                                     !textInputEditText.getText().toString().isEmpty());
                         }else{
                             setCtaEnabled(nextQuestionBtn,
                                     !currentQuestionResponse.getQuestionResponse().isEmpty());
                         }
                        }
                        if(currentquestion.getQuestionSetting().getOtherOption()){
                            if ( position == responseOpt.size() - 1) {
                                textInputLayout.setVisibility(View.VISIBLE);
                                textInputLayout.post(()->{
                                    textInputEditText.requestFocus();
                                    if (imm != null) {
                                        imm.showSoftInput(textInputEditText, InputMethodManager.SHOW_IMPLICIT);
                                    }
                                });
                            } else  {
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(textInputEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                }
                                textInputLayout.setVisibility(View.GONE);
                                textInputEditText.setText(null);
                                textInputEditText.clearFocus();

                            }
                        }
                    }
                });
            }
        };
        this.adapter=new RadioBtnAdapter(responseOpt,radioClickListener,
                currentQuestionResponse, themeColors );
        radioBtnRecyView.setAdapter(adapter);
        layout.addView(radioQues);
    }
}
