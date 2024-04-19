package com.dwao.alium.survey;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
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
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyConfig;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utilities.JSONConverter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Alium {
    private SharedPreferences sharedPreferences;
    private static final String ALIUM_PREFS="AliumPrefs";
    private SharedPreferences.Editor editor;
    private static JSONObject surveyConfigJSON;
    private String thankyouObj;
        private View layoutView;
        private RelativeLayout layout;
        private RadioBtnAdapter adapter;
        private AppCompatTextView currentQuestion;
        private NpsGridViewAdapter npsGridViewAdapter;
        private Dialog dialog;
        private String currentSurveyFrequency;
        private  String currentSurveyKey;
        private CheckBoxRecyViewAdapter checkBoxRecyViewAdapter;
        //    private static ShowSurvey instance;
        private AppCompatButton nextQuestionBtn;
        private AppCompatImageView closeDialogBtn;
        private static  Alium instance;
        private int currentIndx=0;
        private  Context context;
        private JSONArray surveyQuestions;
        private JSONObject surveyUi;
        private JSONObject surveyInfo;
        private static VolleyService volleyService;
        String uuid, currentScreen;
        private QuestionResponse currentQuestionResponse;
        private static String configURL;
        private  Alium(){}

        public static void configure( String url){
//            if(instance==null){
//                instance=new Alium();
//            }
            configURL=url;
            volleyService=new VolleyService();
            surveyConfigJSON=new JSONObject();


        }

        public static void loadAliumSurvey(Context ctx, String currentScreen){

//          if(instance!=null) {
              VolleyResponseListener ConfigJSONListener=new VolleyResponseListener() {
                  @Override
                  public void onResponseReceived(JSONObject jsonObject) {
                      surveyConfigJSON=jsonObject;
                      Log.d("Alium-Config", jsonObject.toString());
                      new Alium().showSurvey(ctx, currentScreen);

                  }
              };
              volleyService.callVolley(ctx,configURL ,ConfigJSONListener );
              Log.d("Alium-initialized","calling survey on"+ currentScreen);

//          };
        }
        private void showSurvey(Context ctx, String currentScreen){
            Log.d("Alium", "showing survey on :"+currentScreen);
            context=ctx;
            sharedPreferences=context.getSharedPreferences(ALIUM_PREFS, Context.MODE_PRIVATE);
            editor= sharedPreferences.edit();
            this.currentScreen=currentScreen;
            currentQuestionResponse=new QuestionResponse();
            uuid=UUID.randomUUID().toString();
            surveyResponse(surveyConfigJSON, this.currentScreen);
        }



    private void surveyResponse(JSONObject response, String checkURL) {
        Log.d("Alium-Target2", checkURL);
        Iterator<String> keys = response.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject jsonObject = response.getJSONObject(key);
                JSONObject ppupsrvObject = jsonObject.getJSONObject("ppupsrv");
//                JSONObject ppupsrvObject = jsonObject.getJSONObject("appsrv");
                Uri spath=Uri.parse(jsonObject.getString("spath"));
                Log.d("URI", spath.toString());
                String urlValue = ppupsrvObject.getString("url");
                Log.d("Alium-Target2", "Key: " + key + ", URL: " + urlValue);

                if (checkURL.equals(urlValue)){
                    String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
                    thankyouObj=ppupsrvObject.getString("thnkMsg");
                    Log.e("Alium-True","True");
                            Log.d("Alium-url-match",""+true);
                            Log.d("srvshowfrq-sharedpref",key+" "+"prefs "
                                    +sharedPreferences.getString(key, "")
                                    +" "+sharedPreferences.getString(key, "").isEmpty() );
                          if(!sharedPreferences.getString(key, "").isEmpty()){
                            if(!sharedPreferences.getString(key,"").equals(srvshowfrq)){
                                Log.i("srvshowfrq-changed", "updating stored preferences data");
                                editor.remove(key);
                                editor.commit();
                            }else{
                                return;
                            }
                          }
                              loadSurvey(key, srvshowfrq, spath);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadSurvey(String key, String srvshowfrq, Uri uri) {
            currentSurveyFrequency=srvshowfrq;
            currentSurveyKey=key;
        String surURL=uri.toString();
        VolleyResponseListener volleyResponseListener2=new VolleyResponseListener() {
            @Override
            public void onResponseReceived(JSONObject json) {
                Log.d("Alium-survey loaded", json.toString());
//                List<Question> questions= null;
//                Survey survey=JSONConverter.mapToObject(Survey.class, json.toString());
//                Log.d("jsonLoaded",survey.toString());
//                try {
//                    questions = JSONConverter.mapToListObject(Question.class,
//                            json.getJSONArray("surveyQuestions").toString());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                Log.d("Questions", questions.get(0).toString());
                try {
                    surveyQuestions=json.getJSONArray("surveyQuestions");
                    if(json.has("surveyUI")){
                        surveyUi=json.getJSONObject("surveyUI");
                    }
                    if(json.has("surveyInfo")){
                        surveyInfo=json.getJSONObject("surveyInfo");
                    }
                    layoutView= LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
//                    Drawable dialogBg=context.getDrawable(R.drawable.dialog_bg);
//
//                    layoutView.setBackgroundResource(R.drawable.dialog_bg);
                    ViewGroup questionContainer= layoutView.findViewById(R.id.question_container);
                    currentQuestion=questionContainer.findViewById(R.id.question);
                    layout= layoutView.findViewById(R.id.dialog_layout_content);

                    GradientDrawable gradientDrawable=(GradientDrawable)  layoutView
                            .findViewById(R.id.dialog_layout).getBackground();

                    gradientDrawable.setCornerRadius((int)(5* Resources.getSystem().getDisplayMetrics().density));
                    gradientDrawable.setColor(Color.WHITE);
                    if(surveyUi!=null)gradientDrawable.setColor(Color.parseColor(surveyUi.getString("backgroundColor")));
//                    if(surveyUi!=null)gradientDrawable.setColor(Color.argb(255,
//                            Integer.parseInt(surveyUi.getString("rBackground")),
//                            Integer.parseInt(surveyUi.getString("gBackground")),
//                            Integer.parseInt(surveyUi.getString("bBackground"))));

                    if(surveyUi!=null && surveyUi.has("borderColor")) gradientDrawable.setStroke((int)(2* Resources.getSystem()
                                    .getDisplayMetrics().density),
                            Color.parseColor(surveyUi.getString("borderColor")));
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show();
                        }
                    });
//                    trackWithAlium();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
            if(currentSurveyFrequency.equals("onlyonce")){
                Log.i("srvshowfrq", "show survey frequency: onlyonce");
                volleyService.callVolley(context, surURL,volleyResponseListener2 );
                editor.putString(key,srvshowfrq);
                editor.commit();
            }else if(currentSurveyFrequency.equals("overandover")){
                Log.i("srvshowfrq", "show survey frequency: overandover");
                volleyService.callVolley(context, surURL,volleyResponseListener2 );
            }else if(currentSurveyFrequency.equals("untilresponse")){
                Log.i("srvshowfrq", "show survey frequency: untilresponse");
                volleyService.callVolley(context, surURL,volleyResponseListener2 );
            }


    }
    private void setCtaEnabled(View Cta, boolean enabled){
        if(enabled){
            Cta.setEnabled(true);
            Cta.setAlpha(1f);
        }else {
            Cta.setEnabled(false);
            Cta.setAlpha(0.5f);
        }
    }

    private void show(){
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
                currentIndx++;
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
        trackWithAlium();
    }
    private void trackWithAlium() {
      try{
          Log.d("track- first hit", SurveyTracker.getUrl(
                  surveyInfo.getString("surveyId"),uuid, currentScreen,
                  surveyInfo.getString("orgId"),
                  surveyInfo.getString("customerId")
          ) );
          volleyService.loadRequestWithVolley(context, SurveyTracker.getUrl(
                  surveyInfo.getString("surveyId"),uuid, currentScreen,
                  surveyInfo.getString("orgId"),
                  surveyInfo.getString("customerId")
          ));
      }catch(Exception e){
          Log.d("trackWithAlium()", e.toString());
      }
    }
    private void handleNextQuestion(){
      try{
          String url=SurveyTracker.getUrl(
                  surveyInfo.getString("surveyId"),uuid, currentScreen,
                  surveyInfo.getString("orgId"),
                  surveyInfo.getString("customerId"))+"&"+"qusid="+(currentQuestionResponse.getQuestionId()+1)+"&"+
                          "qusrs="+currentQuestionResponse.getQuestionResponse()+"&"+
                          "restp="+currentQuestionResponse.getResponseType();
          volleyService.loadRequestWithVolley(context,url );
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
                  thankyouTxt.setText(thankyouObj);

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

                  //show thank you msg
//                Toast.makeText(context, "Your response has been submitted!!", Toast.LENGTH_LONG)
//                        .show();
                  submitSurvey();
              }}
          }catch(Exception e){
              Log.d("nextQuest",e.toString());
          }

    }
    private void submitSurvey(){
            if(currentSurveyFrequency.equals("untilresponse")){
                editor.putString(currentSurveyKey, currentSurveyFrequency);
                editor.commit();
            }
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


    private void showCurrentQuestion(){

        try {
            Log.d("indx@", ""+currentIndx);
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
