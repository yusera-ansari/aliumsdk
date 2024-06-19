package com.dwao.alium.survey;


import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.animation.ObjectAnimator;
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
import com.dwao.alium.utils.preferences.AliumPreferences;
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

    public SurveyDialog(Context ctx, ExecutableSurveySpecs executableSurveySpecs,
                 SurveyParameters surveyParameters)
    {
        super(ctx,executableSurveySpecs.getLoadableSurveySpecs());
        this.executableSurveySpecs=executableSurveySpecs;
        surveyQuestions=executableSurveySpecs.getSurveyQuestions();
        surveyUi=executableSurveySpecs.getSurveyUi();
        surveyInfo=executableSurveySpecs.getSurveyInfo();
        this.surveyParameters=surveyParameters;


    }
    @Override
    public void show(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(surveyQuestions.length()>0 && currentIndx==0) {
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
            if(surveyUi!=null)gradientDrawable.setColor(Color.parseColor(surveyUi.getString("backgroundColor")));

            if(surveyUi!=null && surveyUi.has("borderColor")) gradientDrawable.setStroke((int)(2* Resources.getSystem()
                            .getDisplayMetrics().density),
                    Color.parseColor(surveyUi.getString("borderColor")));
        }catch (Exception e){
            Log.d("surveyUI", e.toString());
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
                dialog.dismiss();
            }
        });
    }
    private void setNextAndCloseBtnUI(){
        GradientDrawable nxtQuesDrawable=(GradientDrawable) nextQuestionBtn.getBackground();
        try{
            if(surveyUi!=null) {
                nxtQuesDrawable.setColor(Color.parseColor(surveyUi
                        .getJSONObject("nextCta").getString("backgroundColor")));
                nextQuestionBtn.setTextColor(Color.parseColor(surveyUi
                        .getJSONObject("nextCta").getString("textColor")));
                closeDialogBtn.setColorFilter(Color.parseColor(surveyUi
                                .getJSONObject("nextCta")
                                .getString("backgroundColor"))
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
            //check if to show next question or else show thank-you layout
            if( currentIndx< surveyQuestions.length()){
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
        if(surveyUi!=null){

            try {
               int color = Color.parseColor(surveyUi
                        .getString("question"));
                thankYouMsg.setTextColor(color);
                thankYouText.setTextColor(color);
                DrawableCompat.setTint(
                        DrawableCompat.wrap(completedAnimation.getDrawable()),
                        color
                );
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

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
            Log.d("Alium-instance", "AnimatedVectorDrawableCompat");
            AnimatedVectorDrawableCompat avd=(AnimatedVectorDrawableCompat)drawable;
            avd.start();

        }else if(drawable instanceof AnimatedVectorDrawable){
            AnimatedVectorDrawable avd=(AnimatedVectorDrawable)drawable;
            Log.d("Alium-instance2", "AnimatedVectorDrawableCompat");
            avd.start();

        }
    }
    private void showThankYouAndDismiss() throws JSONException {
        clearDialogForThankYouLayout();
        showThankYou();
        submitSurvey();
    }


    private void updateProgressIndicator(){
        Log.d("index", ""+currentIndx+" "+previousIndx);
        int progress=(10000/(surveyQuestions.length()+1))*(currentIndx-previousIndx);
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

            if(surveyUi!=null && surveyUi
                    .has("question")) {
                int color=Color.parseColor(surveyUi
                        .getString("question"));
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
            currentQuestion.setText(surveyQuestions.getJSONObject(currentIndx)
                    .getString("question"));
            String responseType = surveyQuestions.getJSONObject(currentIndx).getString("responseType");
            generateQuestion(responseType); //matches response type and generates corresponding ques
            Log.d("surveyQuestion", "id: " + currentQuestionResponse.getQuestionId()
                    + " type: " + currentQuestionResponse.getResponseType());
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
                        .setOptions(surveyQuestions.getJSONObject(currentIndx)
                                .getJSONArray("responseOptions"))
                        .setSurveyUi(surveyUi)
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "3":
                CheckBoxQuestionRenderer checkBoxQuestionRenderer = new CheckBoxQuestionRenderer();
                checkBoxQuestionRenderer.setSurveyUi(surveyUi)
                        .setOptions(surveyQuestions.getJSONObject(currentIndx)
                                .getJSONArray("responseOptions"))
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "4":
                NPSQuestionRenderer npsQuestionRenderer = new NPSQuestionRenderer();
                npsQuestionRenderer.setSurveyUi(surveyUi)
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
            params.put("srvid", surveyInfo.getString("surveyId"));
            params.put("orgId",surveyInfo.getString("orgId"));
        }catch (Exception e){
            Log.e("Generate Params Map", "Couldn't get srvid/orgId");
        }
        Log.d("MAP of MAP", params.toString());
        return params;
    }
    @Override
    protected void submitSurvey() {
        super.submitSurvey();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
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