package com.dwao.alium.survey;


import static android.view.View.INVISIBLE;
import static com.dwao.alium.utils.DeviceInfo.getUserAgent;
import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.dwao.alium.R;
import com.dwao.alium.models.Survey;
import com.dwao.alium.questions.CheckBoxQuestionRenderer;
import com.dwao.alium.questions.LongTextQuestionRenderer;
import com.dwao.alium.questions.NPSQuestionRenderer;
import com.dwao.alium.questions.OpinionScaleQuesRenderer;
import com.dwao.alium.questions.QuestionRenderer;
import com.dwao.alium.questions.RadioQuestionRenderer;
import com.dwao.alium.questions.RatingQuestionRenderer;


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
//    LinearProgressIndicator bottomProgressBar;
    RelativeLayout layout;


   void cleanUp(){
       this.executableSurveySpecs=null;
       survey=null;
       this.surveyParameters=null;
       currentIndx= -1;
       dialog=null;
       context=null;
    }

    public Dialog getInstance(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(!survey.getQuestions().isEmpty() && currentIndx>=0) {
            showCurrentQuestion();
        }else{
           submitSurvey();
            return null;
        }
        super.show();
        return dialog;
    }

    public SurveyDialog(Context ctx, ExecutableSurveySpecs executableSurveySpecs,
                 SurveyParameters surveyParameters, boolean shouldUpdatePreferences)
    {

        super(ctx,executableSurveySpecs.getLoadableSurveySpecs(), shouldUpdatePreferences);
        Log.d("should--", "shouldupdate::: "+shouldUpdatePreferences);
        this.executableSurveySpecs=executableSurveySpecs;
        survey=executableSurveySpecs.survey;
        this.surveyParameters=surveyParameters;
        Log.e("cuurentIndex","currentIndex: "+executableSurveySpecs.getLoadableSurveySpecs().getCurrentIndex() );
        currentIndx= executableSurveySpecs.getLoadableSurveySpecs().getCurrentIndex();


    }
    @Override
    public void show(){
        initializeDialogUiElements(); //initializes elements and updates UI
        configureDialogWindow();
        if(survey.getQuestions().size()>0 && currentIndx>=0) {
            showCurrentQuestion();
        }else{
            submitSurvey();
            return;
        }
        dialog.show();
        super.show();
    }




    private void initializeDialogUiElements(){
        dialog=new Dialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.bottom_survey_layout);
        layout= dialog.findViewById(R.id.dialog_layout_content);

        currentQuestion=dialog.findViewById(R.id.survey_question_text);

        nextQuestionBtn=dialog.findViewById(R.id.btn_next);
        closeDialogBtn = dialog.findViewById(R.id.close_dialog_btn);
        setCtaEnabled(nextQuestionBtn, true);

        applySurveyUiColorScheme();
        addListenersToNextAndCloseBtn();
    }
    private void updateDialogUi(){

        GradientDrawable gradientDrawable=(GradientDrawable)  dialog
                .findViewById(R.id.dialog_layout).getBackground();
        gradientDrawable.setCornerRadius((int)(5* Resources.getSystem().getDisplayMetrics().density));
        gradientDrawable.setColor(Color.WHITE);

        try {
            if(survey.getSurveyInfo().getThemeColors()!=null) {
                gradientDrawable.setColor(
                        Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor1()));
            }
        }catch (Exception e){
            Log.e("surveyUI", e.toString());
        }
    }
    private void configureDialogWindow(){
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int maxWidthInPx =(int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                400f,
                context.getResources().getDisplayMetrics()
        );

        dialog.getWindow().setLayout(Math.min(screenWidth, maxWidthInPx), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        lp.horizontalMargin=0f;
        lp.verticalMargin=0.0f;
        lp.dimAmount=0.1f;
        dialog.getWindow().setAttributes(lp);
    }
    private void addListenersToNextAndCloseBtn(){

        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Alium-indx", ""+currentIndx);


                handleNextQuestion();
            }
        });
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitSurvey();


            }
        });
    }
    private void setNextAndCloseBtnUI(){
        GradientDrawable nxtQuesDrawable=(GradientDrawable) nextQuestionBtn.getBackground();
        try{
            if(survey.getSurveyInfo().getThemeColors()!=null) {
                nxtQuesDrawable.setColor(Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor3()
                ));
                nextQuestionBtn.setTextColor(Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor4()));
                closeDialogBtn.setColorFilter(Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor23())
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
         super.handleNextQuestion(); //updates index
         Log.d("next called", ""+currentIndx+" "+survey.getQuestions().size());
         super.submitSurvey();
            //check if to show next question or else show thank-you layout
            if( currentIndx< survey.getQuestions().size()){
                executableSurveySpecs.getLoadableSurveySpecs().setCurrentIndex(currentIndx);
                showCurrentQuestion();
                return;
            }

            //what else to do ?? if not show thankyou


        }catch(Exception e){
            Log.d("nextQuest",e.toString()+" "+currentIndx+" "+survey.getQuestions().size());
        }

    }

    private void clearDialogForThankYouLayout(){
        currentQuestion.setVisibility(View.GONE);
        nextQuestionBtn.setVisibility(View.GONE);
    }
    private void showThankYou() {
        View thankyou=LayoutInflater.from(context).inflate(R.layout.thankyou, null);
        AppCompatTextView thankYouText=thankyou.findViewById(R.id.thankyou_text);
        AppCompatTextView thankYouMsg=thankyou.findViewById(R.id.thankyou_msg);
        AppCompatImageView completedAnimation=thankyou.findViewById(R.id.completed_anim);
        if(survey.getSurveyInfo().getThemeColors()!=null){
               int color = Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor2());
                thankYouMsg.setTextColor(color);
                thankYouText.setTextColor(color);
                DrawableCompat.setTint(
                        DrawableCompat.wrap(completedAnimation.getDrawable()),
                        color
                );
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




    private void resetElementsForNextQuestion(){
        this.layout.removeAllViews();
        if(survey.getQuestions().get(currentIndx).getResponseType().equals("-1")
                ||survey.getQuestions().get(currentIndx).getResponseType().equals("0")) {
            Log.d("responseType", "response type is"+survey.getQuestions().get(currentIndx).getResponseType());
                  setCtaEnabled(nextQuestionBtn, true);
            LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams) nextQuestionBtn.getLayoutParams();

            lp.gravity = Gravity.CENTER_HORIZONTAL;
                nextQuestionBtn.setLayoutParams(lp);
              }else{
            Log.d("responseType", "response type is"+survey.getQuestions().get(currentIndx).getResponseType());

            setCtaEnabled(nextQuestionBtn, !survey.getQuestions()
                          .get(currentIndx).getQuestionSetting().getRequired());
            nextQuestionBtn.setText("Next");
            LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams) nextQuestionBtn.getLayoutParams();

            lp.gravity = Gravity.END;
            nextQuestionBtn.setLayoutParams(lp);
              }

    }
    private void applySurveyUiColorScheme(){
        try{
            updateDialogUi();
            setNextAndCloseBtnUI();

            if(survey.getSurveyInfo().getThemeColors()!=null) {
                int color=Color.parseColor(survey.getSurveyInfo().getThemeColors().getColor2());
                currentQuestion.setTextColor(color);



            }
        }catch (Exception e){
            Log.e("", e.toString());
        }
    }
    @Override
    protected void showCurrentQuestion( ) {
        int orientation = context.getResources().getConfiguration().orientation;
        Log.d("Orientation", orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" : "Portrait");

        Log.d("NextButton", "Next Button visibility: " + nextQuestionBtn.getVisibility());
        Log.d("NextButton", "Button width: " + nextQuestionBtn.getWidth() + " height: " + nextQuestionBtn.getHeight());

        super.showCurrentQuestion();
        LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams) layout.getLayoutParams();

        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics()
        );
        lp.bottomMargin = marginInPx;
        if(currentIndx==0 && currentQuestionResponse.getResponseType().equals("0")||(
                currentQuestionResponse.getResponseType().equals("-1")
                )){
            responseSubmitIndex = 2;
           lp.bottomMargin=0;
        }

        layout.setLayoutParams(lp);

        resetElementsForNextQuestion();
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
        Log.d("ResponseType", "response type i s "+responseType);

        switch (responseType) {
            case "0":
                Log.d("ResponseType", "response type i s "+1);
                if(survey.getQuestions().get(currentIndx).getId()==0)nextQuestionBtn.setText("Start");
                break;

            case "1": //long question
                Log.d("ResponseType", "response type i s "+1);

                LongTextQuestionRenderer longtextRenderer = new LongTextQuestionRenderer();
                longtextRenderer.setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                    .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "2": //radio
                Log.d("ResponseType", "response type i s "+2);

                RadioQuestionRenderer radioQuestionRenderer = new RadioQuestionRenderer();
                radioQuestionRenderer
                        .setOptions(survey.getQuestions().get(currentIndx)
                                .getResponseOptions())
                        .setTheme(survey.getSurveyInfo().getThemeColors())
                        .setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "3": //checkbox
                Log.d("ResponseType", "response type i s "+3);

                CheckBoxQuestionRenderer checkBoxQuestionRenderer = new CheckBoxQuestionRenderer();
                checkBoxQuestionRenderer
                         .setTheme(survey.getSurveyInfo().getThemeColors())
                        .setOptions(survey.getQuestions().get(currentIndx)
                                .getResponseOptions())
                        .setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "4"://nps
                Log.d("ResponseType", "response type i s "+4);

                NPSQuestionRenderer npsQuestionRenderer = new NPSQuestionRenderer();
                npsQuestionRenderer
                        .setTheme(survey.getSurveyInfo().getThemeColors())
                        .setOptions(survey.getQuestions().get(currentIndx).getResponseOptions())
                        .setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;

                case "5"://rating
                    RatingQuestionRenderer ratingQuestionRenderer=new RatingQuestionRenderer();
                    ratingQuestionRenderer.setRatingOptions(survey.getQuestions().get(currentIndx)
                            .getResponseOptions())
                            .setTheme(survey.getSurveyInfo().getThemeColors())
                            .setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                            .setQuestionSetting(survey.getQuestions().get(currentIndx).getQuestionSetting())
                                    .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);


                    break;
            case "6": //opinion
                Log.d("ResponseType", "response type i s  opinion scale"+6);
                OpinionScaleQuesRenderer opinionScaleQuesRenderer=new OpinionScaleQuesRenderer();
                opinionScaleQuesRenderer.setTheme(survey.getSurveyInfo().getThemeColors())
                        .setRequired(survey.getQuestions().get(currentIndx).getQuestionSetting().getRequired())
                        .setOptions(survey.getQuestions().get(currentIndx).getResponseOptions())
                        .renderQuestion(context, layout, currentQuestionResponse, nextQuestionBtn);
                break;
            case "-1": //Thank you
                Log.d("ResponseType", "response type i s "+7);
                setCtaEnabled(nextQuestionBtn, true);
                nextQuestionBtn.setText("Close");
                nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      submitSurvey();
                    }
                });
                break;
            default:
                //in case no question type matches
                handleNextQuestion();
                break;
        }
    }

    @Override
    protected Map<String, Object>  generateTrackingParameters(){
        Map  params=new HashMap<>(surveyParameters.customerVariables);
            params.put("surveyLoadId", uuid);

            params.put("surveyPath", surveyParameters.screenName);

        params.put("userId", "");
        params.put("custId", aliumPreferences.getCustomerId());
        params.put("userAgent", getUserAgent(context));
        params.put("eventType", "question response");
        params.put("language", "1");
        params.put("surveyType", 7);
        try{
            params.put("surveyId", survey.getSurveyInfo().getSurveyId());
            params.put("orgId",survey.getSurveyInfo().getOrgId());
        }catch (Exception e){
            Log.e("Generate Params Map", "Couldn't get srvid/orgId");
        }
        return params;
    }
    @Override
    protected void submitSurvey() {
        super.submitSurvey();
        dialog.dismiss();
        cleanUp();
    }
}