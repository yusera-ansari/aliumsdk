package com.dwao.alium.survey;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.dwao.alium.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

class CustomDialog extends Dialog {
    Dialog dialog;
    View layoutView;
    AppCompatButton nextQuestionBtn;
    AppCompatImageView closeDialogBtn;
    AppCompatTextView currentQuestion,improveExpTxt, poweredByText,poweredByValue;
    LinearProgressIndicator bottomProgressBar;
    RelativeLayout layout;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }
    private void initializeDialogUiElements(){
        this.setContentView(R.layout.bottom_survey_layout);

    }




}
