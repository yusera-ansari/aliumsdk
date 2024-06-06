package com.dwao.alium.survey;


import static com.dwao.alium.survey.SurveyTracker.trackWithAlium;
import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SurveyDialog extends SurveyDialogCreator {
    private final String uuid;
    ExecutableSurveySpecs executableSurveySpecs;
    private AppCompatButton nextQuestionBtn;
    private AppCompatImageView closeDialogBtn;
    LinearProgressIndicator bottomProgressBar;
    SurveyParameters surveyParameters;
    JSONObject surveyUi;
    JSONObject surveyInfo;
    private RelativeLayout layout;
    QuestionResponse currentQuestionResponse=new QuestionResponse();
    Dialog dialog;
    Context context;
    private int currentIndx=0;
    private int previousIndx=-1;
    JSONArray surveyQuestions;
    private View layoutView;
    private AppCompatTextView currentQuestion,improveExpTxt, poweredByText,poweredByValue;
    private final LoadableSurveySpecs loadableSurveySpecs;
    AliumPreferences aliumPreferences ;

    public SurveyDialog(Context ctx, ExecutableSurveySpecs executableSurveySpecs,
                 SurveyParameters surveyParameters)
    {
        this.executableSurveySpecs=executableSurveySpecs;
        this.uuid= UUID.randomUUID().toString();
        surveyQuestions=executableSurveySpecs.getSurveyQuestions();
        surveyUi=executableSurveySpecs.getSurveyUi();
        surveyInfo=executableSurveySpecs.getSurveyInfo();
        this.context=ctx;
        this.surveyParameters=surveyParameters;
        this.loadableSurveySpecs=executableSurveySpecs.getLoadableSurveySpecs();
        this.aliumPreferences= AliumPreferences.getInstance(context);




    }
    public void show(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(surveyQuestions.length()>0 && currentIndx==0) {
            showCurrentQuestion();
        }else{
            dialog.dismiss();
        }
        dialog.show();
        if(!loadableSurveySpecs.surveyFreq.equals("untilresponse"))recordSurveyTriggerOnPreferences();
        trackWithAlium(context, generateTrackingParameters());
    }
    private void recordSurveyTriggerOnPreferences(){
        switch (loadableSurveySpecs.surveyFreq) {
            case "onlyonce":
                Log.i("srvshowfrq", "show survey frequency: onlyonce");
                aliumPreferences.addToAliumPreferences(loadableSurveySpecs.key,
                        loadableSurveySpecs.surveyFreq);
                break;
            case "overandover":
                Log.i("srvshowfrq", "show survey frequency: overandover");
                break;
            case "untilresponse":
                Log.i("srvshowfrq", "show survey frequency: untilresponse");
                aliumPreferences.addToAliumPreferences(loadableSurveySpecs.key,
                        loadableSurveySpecs.surveyFreq);
                break;
            default:
                   checkForFrequencyCount();


        }

    }
    private void checkForFrequencyCount(){
            try{
                int freq=Integer.parseInt(loadableSurveySpecs.surveyFreq);
                //        int  freq=  Integer.parseInt("1");
            aliumPreferences.handleFrequencyCounter(freq, loadableSurveySpecs.key);
            }catch (Exception e){
            Log.e("SurveyFrequency", "Invalid Survey Frequency Provided");
        }
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


    private void handleConditionMapping(JSONObject jsonObject){
        try{
            if(jsonObject!=null && jsonObject.has("conditionMapping")){
                JSONArray conditionMappingArray=jsonObject.getJSONArray("conditionMapping");
                Log.e("condition-index", conditionMappingArray.toString()+"" +currentQuestionResponse.getIndexOfSelectedAnswer() );
                int nextQuestIndx= conditionMappingArray.getInt(
                        currentQuestionResponse.getIndexOfSelectedAnswer()
                );
                previousIndx=currentIndx;
                if(nextQuestIndx==-2){
                    currentIndx++;//next question
                }else if(nextQuestIndx==-1){
                    currentIndx=surveyQuestions.length();//thankyou
                }else {
                    currentIndx=nextQuestIndx;//set currentIndx as nextQuestIndx
                }
                Log.e("condition", "" +currentQuestionResponse.getIndexOfSelectedAnswer() );
            }
        }catch (Exception e){
            Log.e("Condition Map", e.toString());
        }
    }
    private void submitResponse() {
        Map<String, String > responseMap=new HashMap<>(generateTrackingParameters());
        responseMap.put("qusid",""+(currentQuestionResponse.getQuestionId()+1));
        responseMap.put("qusrs",currentQuestionResponse.getQuestionResponse());
        responseMap.put("restp",currentQuestionResponse.getResponseType());
        trackWithAlium(context,responseMap );
    }
    private void handleNextQuestion(){
        try{
            submitResponse();
            //handle condition mapping, this updates the currentIndx
            handleConditionMapping(surveyQuestions.getJSONObject(currentIndx));

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
    private void resetElementsForNextQuestion(){
        this.layout.removeAllViews();
        setCtaEnabled(nextQuestionBtn, false);
        updateProgressIndicator();
    }
    private void showThankYouAndDismiss() throws JSONException {
        currentQuestion.setVisibility(View.GONE);
        improveExpTxt.setVisibility(View.GONE);
        nextQuestionBtn.setVisibility(View.GONE);
        View thankyou=LayoutInflater.from(context).inflate(R.layout.thankyou, null);
        AppCompatTextView thankYouText=thankyou.findViewById(R.id.thankyou_text);
        AppCompatTextView thankYouMsg=thankyou.findViewById(R.id.thankyou_msg);
        AppCompatImageView completedAnimation=thankyou.findViewById(R.id.completed_anim);
        if(surveyUi!=null){
            int color=Color.parseColor(surveyUi
                    .getString("question"));
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
        this.layout.addView(thankyou);
        submitSurvey();
    }
    private void submitSurvey(){
        recordSurveyTriggerOnPreferences();
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
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
    private void updateCurrentQuestionResponse(){
       try{
           currentQuestionResponse.setQuestionId(surveyQuestions.getJSONObject(currentIndx)
                   .getInt("id"));
           currentQuestionResponse.setResponseType(surveyQuestions
                   .getJSONObject(currentIndx).getString("responseType"));
           currentQuestion.setText(surveyQuestions.getJSONObject(currentIndx)
                   .getString("question"));
           currentQuestionResponse.setIndexOfSelectedAnswer(0);
       }catch (Exception e){
           Log.d("updateQuestionResp", e.toString());
       }
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
    private void showCurrentQuestion( ){
        updateProgressIndicator();
        Log.i("question", "going to next question "+currentIndx);
        updateCurrentQuestionResponse();

        try {

            String responseType=surveyQuestions.getJSONObject(currentIndx).getString("responseType");
            generateQuestion(responseType); //matches response type and generates corresponding ques
            Log.d("surveyQuestion", "id: "+currentQuestionResponse.getQuestionId()
                    +" type: "+currentQuestionResponse.getResponseType());
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
    private Map<String ,String> generateTrackingParameters(){
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
}