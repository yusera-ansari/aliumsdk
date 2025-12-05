package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dwao.alium.R;
import com.dwao.alium.models.AiFollowup;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class FollowupTextQuestionRenderer {
    private JSONObject surveyUi;
    public FollowupTextQuestionRenderer setSurveyUi(JSONObject surveyUi){
        this.surveyUi=surveyUi;
        return this;
    }




    public void renderQuestion(Context context, ViewGroup layout, AiFollowup aiFollowup
    ,View nextQuestionBtn) {
        View longtextQues= LayoutInflater.from(context).inflate(R.layout.long_text_ques,
                null);
        TextInputLayout textInputLayout=longtextQues.findViewById(R.id.text_input_layout);

        TextInputEditText input=longtextQues.findViewById(R.id.text_input_edit_text);
        if(surveyUi!=null) {
//            int color=Color.parseColor(surveyUi
//                    .getString("question"));
        }


        GradientDrawable d= (GradientDrawable)textInputLayout.getBackground();
        d.mutate();
//        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if(hasFocus){
//
//                    d.setStroke(2, Color.BLUE);
//                }else{
//
//                    d.setStroke(2, Color.BLACK);
//                }
//            }
//        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                aiFollowup.setResponse(input.getText().toString().trim());
//                        .replace(" ", "%20"));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input.requestFocus();
        layout.addView(longtextQues);
    }
}
