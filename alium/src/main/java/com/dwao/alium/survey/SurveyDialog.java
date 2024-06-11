package com.dwao.alium.survey;


import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.dwao.alium.R;
import com.dwao.alium.adapters.CheckBoxRecyViewAdapter;
import com.dwao.alium.adapters.NpsGridViewAdapter;
import com.dwao.alium.adapters.RadioBtnAdapter;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.listeners.RadioClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SurveyDialog extends SurveyDialogCreator {
    private final String uuid;
    ExecutableSurveySpecs executableSurveySpecs;
    private AppCompatButton nextQuestionBtn;
    LinearProgressIndicator bottomProgressBar;
    VolleyService volleyService=new VolleyService();
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
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.bottom_survey_layout);
        dialog.show();
        ViewGroup questionContainer= dialog.findViewById(R.id.question_container);
        currentQuestion=dialog.findViewById(R.id.survey_question_text);
        layout= dialog.findViewById(R.id.dialog_layout_content);
        bottomProgressBar=dialog.findViewById(R.id.horizontal_bottom_progressbar);
        bottomProgressBar.setMax(100*100);

//        poweredByText=dialog.findViewById(R.id.powered_by_text);
//        poweredByValue=dialog.findViewById(R.id.powered_by_value);
        improveExpTxt=dialog.findViewById
                (R.id.help_improve_experience_textview);
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
        AppCompatImageView closeDialogBtn = dialog.findViewById(R.id.close_dialog_btn);
        Log.d("Alium-showSurvey", surveyParameters.screenName);
        nextQuestionBtn=dialog.findViewById(R.id.btn_next);
        GradientDrawable nxtQuesDrawable=(GradientDrawable) nextQuestionBtn.getBackground();
        try{
            if(surveyUi!=null) {
                nxtQuesDrawable.setColor(Color.parseColor(surveyUi
                        .getJSONObject("nextCta").getString("backgroundColor")));
                nextQuestionBtn.setTextColor(Color.parseColor(surveyUi
                        .getJSONObject("nextCta").getString("textColor")));
                closeDialogBtn.setColorFilter(Color.parseColor(surveyUi
                                .getJSONObject("nextCta").getString("backgroundColor"))
                        ,
                        PorterDuff.Mode.MULTIPLY);




            }
        }catch (Exception e){
            Log.e("nextQues", e.toString());
        }
        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                currentIndx++;
                Log.d("Alium-indx", ""+currentIndx);
                setCtaEnabled(nextQuestionBtn,false);
                handleNextQuestion();

            }
        });

        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                List<SurveyDialog>  activeSurveyList= ((AliumSurveyActivity)context).removeSurveyFromActiveList(SurveyDialog.this);
                if(activeSurveyList.isEmpty()){
                    ((Activity)context).finish();
                }
            }
        });


//        dialog.setContentView(this.layoutView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.horizontalMargin=0f;
        lp.verticalMargin=0.0f;
        dialog.getWindow().setAttributes(lp);
        if(surveyQuestions.length()>0 && currentIndx==0) showCurrentQuestion();
        trackWithAlium(); //convert to tracker class
    }
    protected void trackWithAlium() {
        try{
            volleyService.loadRequestWithVolley(context, SurveyTracker.getUrl(
                    surveyInfo.getString("surveyId"),uuid, surveyParameters.screenName,
                    surveyInfo.getString("orgId"),
                    aliumPreferences.getCustomerId()
            ) +SurveyTracker.getAppendableCustomerVariables(surveyParameters.customerVariables));
        }catch(Exception e){
            Log.e("track", e.toString());
        }
    }
    private void checkForConditionMapping(JSONObject jsonObject){
        try{
            if(jsonObject!=null && jsonObject.has("conditionMapping")){
                JSONArray conditionMappingArray=jsonObject.getJSONArray("conditionMapping");
                int nextQuestIndx=conditionMappingArray.getInt(0);
                previousIndx=currentIndx;
                if(nextQuestIndx==-2){
                    currentIndx++;//next question
                }else if(nextQuestIndx==-1){

                    currentIndx=surveyQuestions.length();//thankyou
                }else {
                    currentIndx=nextQuestIndx;//set nextQuestIndx as currentIndx
                }
                Log.e("condition",Integer.toString(conditionMappingArray.getInt(0)) );
            }
        }catch (Exception e){
            Log.e("Condition Map", e.toString());
        }
    }
    private void handleNextQuestion(){
        try{
            String url=SurveyTracker.getUrl(
                    surveyInfo.getString("surveyId"),
                    uuid,
                    surveyParameters.screenName,
                    surveyInfo.getString("orgId"),
                    aliumPreferences.getCustomerId()
            )        +
                    SurveyTracker.getAppendableCustomerVariables(surveyParameters.customerVariables)+
                    "&"+
                    "qusid="+
                    (currentQuestionResponse.getQuestionId()+1)+
                    "&"+
                    "qusrs="+currentQuestionResponse.getQuestionResponse()+
                    "&"+
                    "restp="+currentQuestionResponse.getResponseType();

            volleyService.loadRequestWithVolley(context,url );
            //check for condition mapping, this updates the currentIndx
            checkForConditionMapping(surveyQuestions.getJSONObject(currentIndx));

            //check if to show next question or thank-you layout
            if(surveyQuestions.length()>0) {
                this.layout.removeAllViews();


                if( currentIndx< surveyQuestions.length()){
                    showCurrentQuestion();
                }else if(currentIndx==surveyQuestions.length()){
                    updateProgressIndicator();
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
                }}
        }catch(Exception e){
            Log.d("nextQuest",e.toString());
        }

    }
    private void submitSurvey(){
        if(loadableSurveySpecs.surveyFreq.equals("untilresponse")){

            aliumPreferences.addToAliumPreferences(loadableSurveySpecs.key,
                    loadableSurveySpecs.surveyFreq);
        }
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
                List<SurveyDialog>  activeSurveyList= ((AliumSurveyActivity)context).removeSurveyFromActiveList(SurveyDialog.this);
               if(activeSurveyList.size()==0){
                   ((Activity)context).finish();
               }
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
       }catch (Exception e){
           Log.d("updateQuestionResp", e.toString());
       }
    }
    private void applySurveyUiColorScheme(){
        try{
            if(surveyUi!=null) {
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
        setCtaEnabled(nextQuestionBtn, false);
        updateProgressIndicator();
        Log.i("question", "going to next question "+currentIndx);
        updateCurrentQuestionResponse();
        applySurveyUiColorScheme();
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

}