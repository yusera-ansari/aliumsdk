package com.dwao.alium.survey;


import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.dwao.alium.R;
import com.dwao.alium.models.Survey;
import com.dwao.alium.questions.CheckBoxQuestionRenderer;
import com.dwao.alium.questions.LongTextQuestionRenderer;
import com.dwao.alium.questions.NPSQuestionRenderer;
import com.dwao.alium.questions.QuestionRenderer;
import com.dwao.alium.questions.RadioQuestionRenderer;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SurveyDialog extends SurveyController {
    Dialog dialog;
    View layoutView;
    AppCompatButton nextQuestionBtn;
    AppCompatImageView closeDialogBtn;
    AppCompatTextView currentQuestion,improveExpTxt, poweredByText,poweredByValue;
    LinearProgressIndicator bottomProgressBar;
    RelativeLayout layout;

    public Dialog getInstance(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(survey.getQuestions().size()>0 && currentIndx>=0) {
            showCurrentQuestion();
        }else{
            dialog.dismiss();
            return null;
        }
        super.show();
        return dialog;
    }

    public SurveyDialog(Context ctx, ExecutableSurveySpecs executableSurveySpecs,
                 SurveyParameters surveyParameters, boolean shouldUpdatePreferences)
    {
        super(ctx,executableSurveySpecs.getLoadableSurveySpecs(), shouldUpdatePreferences);
        this.executableSurveySpecs=executableSurveySpecs;
        survey=executableSurveySpecs.survey;
        this.surveyParameters=surveyParameters;
        currentIndx= executableSurveySpecs.getLoadableSurveySpecs().getCurrentIndex();


    }
    @Override
    public void show(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(survey.getQuestions().size()>0 && currentIndx>=0) {
            showCurrentQuestion();
        }else{
            dialog.dismiss();
            return;
        }
        dialog.show();
        super.show();
    }




    private void initializeDialogUiElements(){
        dialog=new Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.bottom_survey_layout);
        layout= dialog.findViewById(R.id.dialog_layout_content);
        ViewGroup questionContainer= dialog.findViewById(R.id.question_container);
        currentQuestion=dialog.findViewById(R.id.survey_question_text);
        bottomProgressBar=dialog.findViewById(R.id.horizontal_bottom_progressbar);
        bottomProgressBar.setMax(100*100);
        nextQuestionBtn=dialog.findViewById(R.id.btn_next);
        closeDialogBtn = dialog.findViewById(R.id.close_dialog_btn);

//        poweredByText=dialog.findViewById(R.id.powered_by_text);
//        poweredByValue=dialog.findViewById(R.id.powered_by_value);
        improveExpTxt=dialog.findViewById
                (R.id.help_improve_experience_textview);
        applySurveyUiColorScheme();
        addListenersToNextAndCloseBtn();
    }
    private void updateDialogUi(){
        GradientDrawable gradientDrawable=(GradientDrawable)  dialog
                .findViewById(R.id.dialog_layout).getBackground();
        gradientDrawable.setCornerRadius((int)(5* Resources.getSystem().getDisplayMetrics().density));
        gradientDrawable.setColor(Color.WHITE);

        try {
            if(survey.getSurveyUI()!=null)gradientDrawable.setColor(
                    Color.parseColor(survey.getSurveyUI().getBackgroundColor()));

            if(survey.getSurveyUI()!=null && !survey.getSurveyUI().getBorderColor().isEmpty()) {
                gradientDrawable.setStroke((int) (2 * Resources.getSystem()
                                .getDisplayMetrics().density),
                        Color.parseColor(survey.getSurveyUI().getBorderColor()));
            }
        }catch (Exception e){
            Log.e("surveyUI", e.toString());
        }
    }
    private void configureDialogWindow(){
//        dialog.setContentView(this.layoutView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.horizontalMargin=0f;
        lp.verticalMargin=0.0f;
        dialog.getWindow().setAttributes(lp);
    }
    private void addListenersToNextAndCloseBtn(){

        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Alium-indx", ""+currentIndx);
                setCtaEnabled(nextQuestionBtn,false);
                handleNextQuestion();

            }
        });
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alium.removeFromActiveSurveyList(SurveyDialog.this);

                dialog.dismiss();
//                ((AliumSurveyActivity)context).removeFromActiveSurveyList(SurveyDialog.this);

            }
        });
    }
    private void setNextAndCloseBtnUI(){
        GradientDrawable nxtQuesDrawable=(GradientDrawable) nextQuestionBtn.getBackground();
        try{
            if(survey.getSurveyUI()!=null) {
                nxtQuesDrawable.setColor(Color.parseColor(survey.getSurveyUI().getNextCta().getBackgroundColor()
                ));
                nextQuestionBtn.setTextColor(Color.parseColor(survey.getSurveyUI().getNextCta().getTextColor()));
                closeDialogBtn.setColorFilter(Color.parseColor(survey.getSurveyUI().getNextCta()
                                .getBackgroundColor())
                        ,
                        PorterDuff.Mode.MULTIPLY);
            }
        }catch (Exception e){
            Log.e("nextQues", e.toString());
        }

    }

    @Override
     protected void handleNextQuestion() {
        try{
         super.handleNextQuestion();
            resetElementsForNextQuestion();
            executableSurveySpecs.getLoadableSurveySpecs().setCurrentIndex(currentIndx);
            //check if to show next question or else show thank-you layout
            if( currentIndx< survey.getQuestions().size()){
                showCurrentQuestion();
                return;
            }
            showThankYouAndDismiss();

        }catch(Exception e){
            Log.d("nextQuest",e.toString());
        }

    }

    private void clearDialogForThankYouLayout(){
        currentQuestion.setVisibility(View.GONE);
        improveExpTxt.setVisibility(View.GONE);
        nextQuestionBtn.setVisibility(View.GONE);
    }
    private void showThankYou() {
        View thankyou=LayoutInflater.from(context).inflate(R.layout.thankyou, null);
        AppCompatTextView thankYouText=thankyou.findViewById(R.id.thankyou_text);
        AppCompatTextView thankYouMsg=thankyou.findViewById(R.id.thankyou_msg);
        AppCompatImageView completedAnimation=thankyou.findViewById(R.id.completed_anim);
        if(survey.getSurveyUI()!=null){
               int color = Color.parseColor(survey.getSurveyUI().getQuestion());
                thankYouMsg.setTextColor(color);
                thankYouText.setTextColor(color);
                DrawableCompat.setTint(
                        DrawableCompat.wrap(completedAnimation.getDrawable()),
                        color
                );
//                        completedAnimation.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
        thankYouMsg.setText(loadableSurveySpecs.thankYouMsg);
        AppCompatImageView imageView=thankyou.findViewById(R.id.completed_anim_container)
                .findViewById(R.id.completed_anim);
        addThankYouAnimation(imageView);
        this.layout.addView(thankyou);
    }

    private void addThankYouAnimation(AppCompatImageView imageView){
        imageView.setImageResource(R.drawable.avd_anim);
        Drawable drawable= imageView.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
//            Log.d("Alium-instance", "AnimatedVectorDrawableCompat");
            AnimatedVectorDrawableCompat avd=(AnimatedVectorDrawableCompat)drawable;
            avd.start();

        }else if(drawable instanceof AnimatedVectorDrawable){
            AnimatedVectorDrawable avd=(AnimatedVectorDrawable)drawable;
//            Log.d("Alium-instance2", "AnimatedVectorDrawableCompat");
            avd.start();

        }
    }
    private void showThankYouAndDismiss() throws JSONException {
        clearDialogForThankYouLayout();
        showThankYou();
        submitSurvey();
    }


    private void updateProgressIndicator(){
//        Log.d("index", ""+currentIndx+" "+previousIndx);
        int progress=(10000/(survey.getQuestions().size()+1))*(currentIndx-previousIndx);
        ObjectAnimator animator=ObjectAnimator.ofInt(bottomProgressBar,"progress"
        ,bottomProgressBar.getProgress(),(progress+bottomProgressBar.getProgress()));
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
//        bottomProgressBar.setProgress(bottomProgressBar.getProgress()+progress);
    }
    private void resetElementsForNextQuestion(){
        this.layout.removeAllViews();
        setCtaEnabled(nextQuestionBtn, false);
        updateProgressIndicator();
    }
    private void applySurveyUiColorScheme(){
        try{
            updateDialogUi();
            setNextAndCloseBtnUI();

            if(survey.getSurveyUI()!=null) {
                int color=Color.parseColor(survey.getSurveyUI().getQuestion()
                      );
                currentQuestion.setTextColor(color);
                improveExpTxt.setTextColor(color);
                bottomProgressBar.getProgressDrawable().setColorFilter(
                        color, android.graphics.PorterDuff.Mode.SRC_IN
                );

            }
        }catch (Exception e){
            Log.e("", e.toString());
        }
    }
    @Override
    protected void showCurrentQuestion( ) {
        super.showCurrentQuestion();
        updateProgressIndicator();
        Log.i("question", "going to next question " + currentIndx);


        try {
            currentQuestion.setText(survey.getQuestions().get(currentIndx)
                    .getQuestion());
            String responseType = survey.getQuestions().get(currentIndx).getResponseType();
            generateQuestion(responseType); //matches response type and generates corresponding ques
//            Log.d("surveyQuestion", "id: " + currentQuestionResponse.getQuestionId()
//                    + " type: " + currentQuestionResponse.getResponseType());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    protected void generateQuestion(String responseType) throws JSONException {
        switch (responseType) {
            case "1":
                QuestionRenderer longtextRenderer = new LongTextQuestionRenderer();
                longtextRenderer.renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "2":
                RadioQuestionRenderer radioQuestionRenderer = new RadioQuestionRenderer();
                radioQuestionRenderer
                        .setOptions(survey.getQuestions().get(currentIndx)
                                .getResponseOptions())
                        .setSurveyUi(survey.getSurveyUI())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "3":
                CheckBoxQuestionRenderer checkBoxQuestionRenderer = new CheckBoxQuestionRenderer();
                checkBoxQuestionRenderer.setSurveyUi(survey.getSurveyUI())
                        .setOptions(survey.getQuestions().get(currentIndx)
                                .getResponseOptions())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "4":
                NPSQuestionRenderer npsQuestionRenderer = new NPSQuestionRenderer();
                npsQuestionRenderer.setSurveyUi(survey.getSurveyUI())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
        }
    }

    @Override
    protected Map<String ,String> generateTrackingParameters(){
        Map<String, String> params=new HashMap<>(surveyParameters.customerVariables);
        params.put("srvtpid", "6");
        params.put("srvLng", "1");
        params.put("vstid", uuid);
        params.put("srvldid",uuid+"ppup"+ new Date().getTime()+"srv" );
        params.put("srvpt", surveyParameters.screenName);
        params.put("ran",""+new Date().getTime() );
        params.put("custSystemId", "NA");
        params.put("custId", aliumPreferences.getCustomerId());
        params.put("custEmail", "NA");
        params.put("custMobile", "NA");
        try{
            params.put("srvid", survey.getSurveyInfo().getSurveyId());
            params.put("orgId",survey.getSurveyInfo().getOrgId());
        }catch (Exception e){
            Log.e("Generate Params Map", "Couldn't get srvid/orgId");
        }
//        Log.d("MAP of MAP", params.toString());
        return params;
    }
    @Override
    protected void submitSurvey() {
        super.submitSurvey();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Alium.removeFromActiveSurveyList(SurveyDialog.this);
                dialog.dismiss();

//                ((AliumSurveyActivity)context).removeFromActiveSurveyList(SurveyDialog.this);

            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 2000);

    }
}