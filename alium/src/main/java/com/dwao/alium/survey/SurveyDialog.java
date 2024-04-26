package com.dwao.alium.survey;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SurveyDialog {
    private AppCompatButton nextQuestionBtn;
    VolleyService volleyService=new VolleyService();
    String currentScreen;
    JSONObject surveyUi;
    JSONObject surveyInfo;
    private CheckBoxRecyViewAdapter checkBoxRecyViewAdapter;
    private NpsGridViewAdapter npsGridViewAdapter;
    private RadioBtnAdapter adapter;
    Alium alium;
    private RelativeLayout layout;
    private AppCompatImageView closeDialogBtn;
    QuestionResponse currentQuestionResponse=new QuestionResponse();
    Dialog dialog;
    Context context;
    private int currentIndx=0;
    JSONArray surveyQuestions;
    private View layoutView;
    private AppCompatTextView currentQuestion;
    private void setCtaEnabled(View Cta, boolean enabled){

        if(enabled){
            Cta.setEnabled(true);
            Cta.setAlpha(1f);
        }else {
            Cta.setEnabled(false);
            Cta.setAlpha(0.5f);
        }
    }
    SurveyDialog(Context ctx, Alium instance){
        alium=instance;
        surveyQuestions=alium.getSurveyQuestions();
        surveyUi=alium.getSurveyUi();
        surveyInfo=alium.getSurveyInfo();
        this.context=ctx;
        currentScreen= alium.getCurrentScreen();
        layoutView= LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
        ViewGroup questionContainer= layoutView.findViewById(R.id.question_container);
        currentQuestion=questionContainer.findViewById(R.id.question);
        layout= layoutView.findViewById(R.id.dialog_layout_content);

        GradientDrawable gradientDrawable=(GradientDrawable)  layoutView
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
    protected void show(){

        dialog=new Dialog(context);
        Log.d("Alium-showSurvey", currentScreen);
        nextQuestionBtn=layoutView.findViewById(R.id.btn_next);

        GradientDrawable nxtQuesDrawable=(GradientDrawable) nextQuestionBtn.getBackground();
        try{
            if(surveyUi!=null)nxtQuesDrawable.setColor(Color.parseColor(surveyUi
                    .getJSONObject("nextCta").getString("backgroundColor")));
            if(surveyUi!=null)nextQuestionBtn.setTextColor(Color.parseColor(surveyUi
                    .getJSONObject("nextCta").getString("textColor")));
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
        closeDialogBtn=layoutView.findViewById(R.id.close_dialog_btn);
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if(surveyQuestions.length()>0 && currentIndx==0) showCurrentQuestion();
        dialog.setContentView(this.layoutView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.horizontalMargin=0f;
        lp.verticalMargin=0.0f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        alium.trackWithAlium();
    }
    private void checkForConditionMapping(JSONObject jsonObject){
        try{
            if(jsonObject!=null && jsonObject.has("conditionMapping") ){
                JSONArray conditionMappingArray=jsonObject.getJSONArray("conditionMapping");
                int nextQuestIndx=conditionMappingArray.getInt(0);
                if(nextQuestIndx==-2){
                    currentIndx++;
                    //next question
                }else if(nextQuestIndx==-1){
                    //thankyou
                    currentIndx=surveyQuestions.length();
                }else {
                    //set currentIndx as nextQuestIndx
                    currentIndx=nextQuestIndx;
                }
                Log.e("condition",Integer.toString(conditionMappingArray.getInt(0)) );
            }
        }catch (Exception e){
            Log.e("checkForConditnMapping", e.toString());
        }
    }
    private void handleNextQuestion(){
        try{
            String url=SurveyTracker.getUrl(
                    surveyInfo.getString("surveyId"),
                    alium.getUuid(),
                    currentScreen,
                    surveyInfo.getString("orgId"),
                    surveyInfo.getString("customerId")
                    )+
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
                AppCompatTextView improveExpTxt=layoutView.findViewById
                        (R.id.help_improve_experience_textview);

                if( currentIndx< surveyQuestions.length()){
                    showCurrentQuestion();
                }else if(currentIndx==surveyQuestions.length()){
                    currentQuestion.setVisibility(View.GONE);

                    improveExpTxt.setVisibility(View.GONE);
                    nextQuestionBtn.setVisibility(View.GONE);
                    View thankyou=LayoutInflater.from(context).inflate(R.layout.thankyou, null);
                    AppCompatTextView thankyouTxt=thankyou.findViewById(R.id.thankyou_msg);
                    thankyouTxt.setText(alium.getThankyouObj());
//                    thankyouTxt.setText(Alium.getSurveyConfi gMap().get(alium.getCurrentSurveyIndx()).srv.getThankYouMsg());
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
                    alium.submitSurvey(dialog);
                }}
        }catch(Exception e){
            Log.d("nextQuest",e.toString());
        }

    }
    private void showCurrentQuestion( ){
            setCtaEnabled(nextQuestionBtn, false);
        try {
            Log.i("question", "going to next question "+currentIndx);
            currentQuestionResponse.setQuestionId(surveyQuestions.getJSONObject(currentIndx)
                    .getInt("id"));
            currentQuestionResponse.setResponseType(surveyQuestions
                    .getJSONObject(currentIndx).getString("responseType"));
            currentQuestion.setText(surveyQuestions.getJSONObject(currentIndx).getString("question"));
            //long text question
            if(surveyQuestions.getJSONObject(currentIndx).getString("responseType").equals("1")){

                View longtextQues= LayoutInflater.from(context).inflate(R.layout.long_text_ques,
                        null);
                TextInputLayout textInputLayout=longtextQues.findViewById(R.id.text_input_layout);

                TextInputEditText input=longtextQues.findViewById(R.id.text_input_edit_text);
                GradientDrawable d= (GradientDrawable)input.getBackground();
                d.mutate();
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if(hasFocus){

                            d.setStroke(2, Color.BLUE);
                        }else{

                            d.setStroke(2, Color.BLACK);
                        }
                    }
                });
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        currentQuestionResponse.setQuestionResponse(input.getText().toString().trim()
                                .replace(" ", "%20"));
                        if(currentQuestionResponse.getQuestionResponse().length()>0){
                            setCtaEnabled(nextQuestionBtn,true);
                        }else{
                            setCtaEnabled(nextQuestionBtn,false);
                        }
                        Log.d("Alium-input", currentQuestionResponse.getQuestionResponse());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                if(surveyUi!=null)currentQuestion.setTextColor(Color.parseColor(surveyUi
                        .getString("question")));
                this.layout.addView(longtextQues);
            }

            else if(surveyQuestions.getJSONObject(currentIndx).getString("responseType")
                    .equals("2")){

                JSONArray responseOptJSON=surveyQuestions.getJSONObject(currentIndx)
                        .getJSONArray("responseOptions");
                List<String> responseOptions=new ArrayList<>();
                for (int i=0; i<responseOptJSON.length(); i++){
                    responseOptions.add(responseOptJSON.getString(i));
                }
                View radioQues= LayoutInflater.from(context).inflate(R.layout.radio_ques, null);
                if(surveyUi!=null)currentQuestion.setTextColor(Color.parseColor(surveyUi
                        .getString("question")));

                RecyclerView radioBtnRecyView=radioQues.findViewById(R.id.radio_btn_rec_view);
                radioBtnRecyView.setLayoutManager(new LinearLayoutManager(context));

                RadioClickListener radioClickListener=new RadioClickListener() {
                    @Override
                    public void onClick(int position) {
                        radioBtnRecyView.post(new Runnable() {
                            @Override
                            public void run() {

                                adapter.updateCheckedItem(position);
                                if(currentQuestionResponse.getQuestionResponse().length()>0){
                                    setCtaEnabled(nextQuestionBtn,true);
                                }else{
                                    setCtaEnabled(nextQuestionBtn,false);
                                }
                            }
                        });
                    }
                };
                this.adapter=new RadioBtnAdapter(responseOptions,radioClickListener,
                        currentQuestionResponse, surveyUi );
                radioBtnRecyView.setAdapter(adapter);

                this.layout.addView(radioQues);
            }
            else
            if(surveyQuestions.getJSONObject(currentIndx).getString("responseType").equals("3")){
                JSONArray responseOptJSON=surveyQuestions.getJSONObject(currentIndx).getJSONArray("responseOptions");
                List<String> responseOptions=new ArrayList<>();
                for (int i=0; i<responseOptJSON.length(); i++){
                    responseOptions.add(responseOptJSON.getString(i));
                }
                View checkBoxQues= LayoutInflater.from(context).inflate(R.layout.checkbox_type_ques, null);
                if(surveyUi!=null)currentQuestion.setTextColor(Color.parseColor(surveyUi
                        .getString("question")));
                RecyclerView recyclerView=checkBoxQues.findViewById(R.id.checkbox_recy_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                CheckBoxClickListener checkBoxClickListener=new CheckBoxClickListener() {
                    @Override
                    public void onClick(int position, boolean selected) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                checkBoxRecyViewAdapter.updateCheckedItem(position, selected);

                                if(currentQuestionResponse.getQuestionResponse().length()>0){
                                    setCtaEnabled(nextQuestionBtn,true);
                                }else{
                                    setCtaEnabled(nextQuestionBtn,false);
                                }
                            }
                        });
                    }
                };
                checkBoxRecyViewAdapter=new CheckBoxRecyViewAdapter(responseOptions,
                        checkBoxClickListener, currentQuestionResponse, surveyUi);
                recyclerView.setAdapter(checkBoxRecyViewAdapter);
                this.layout.addView(checkBoxQues);
            }else  if(surveyQuestions.getJSONObject(currentIndx).getString("responseType")
                    .equals("4")){
                View npsQues= LayoutInflater.from(context).inflate(R.layout.nps_ques, null);
                if(surveyUi!=null)currentQuestion.setTextColor(Color.parseColor(surveyUi
                        .getString("question")));

                GridView npsRecView=npsQues.findViewById(R.id.nps_recy_view);
                NpsOptionClickListener listener=new NpsOptionClickListener() {
                    @Override
                    public void onClick(int position) {
                        npsRecView.post(new Runnable() {
                            @Override
                            public void run() {
                                npsGridViewAdapter.updatedSelectedOption(position);
                                if( currentQuestionResponse.getQuestionResponse().length()>0){
                                    setCtaEnabled(nextQuestionBtn,true);
                                }else{
                                    setCtaEnabled(nextQuestionBtn,false);
                                }
//                                    npsOptionsAdapter.updatedSelectedOption(position);
                            }
                        });
                    }
                };
                npsGridViewAdapter=new NpsGridViewAdapter(context, listener, currentQuestionResponse, surveyUi);
                npsRecView.setAdapter( npsGridViewAdapter);
                this.layout.addView(npsQues);

            }
            Log.d("surveyQuestion", "id: "+currentQuestionResponse.getQuestionId()
                    +" type: "+currentQuestionResponse.getResponseType());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
