package com.dwao.alium.survey;

import static com.dwao.alium.survey.SurveyTracker.trackWithAlium;

import android.content.Context;
import android.util.Log;

import androidx.annotation.CallSuper;

import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class SurveyController {
    protected final String uuid;
    Survey survey;
    protected ExecutableSurveySpecs executableSurveySpecs;


    protected SurveyParameters surveyParameters;
    protected QuestionResponse currentQuestionResponse=new QuestionResponse();

    protected Context context;
    protected int currentIndx=0;
    protected  int previousIndx=-1;
    private boolean shouldUpdatePreferences;

    protected final LoadableSurveySpecs loadableSurveySpecs;
    private SurveyFrequencyManager surveyFrequencyManager;
    protected AliumPreferences aliumPreferences ;
    protected SurveyController( Context context,LoadableSurveySpecs loadableSurveySpecs,
                                boolean shouldUpdatePreferences){
        this.context=context;
        this.shouldUpdatePreferences=shouldUpdatePreferences;
        this.uuid= UUID.randomUUID().toString();
        this.loadableSurveySpecs=loadableSurveySpecs;
        this.aliumPreferences= AliumPreferences.getInstance(context);
        this.surveyFrequencyManager=  FrequencyManagerFactory
                .getFrequencyManager(aliumPreferences, loadableSurveySpecs.key,
                loadableSurveySpecs.surveyFreq,
                loadableSurveySpecs.customSurveyData);
    }

    abstract protected void generateQuestion(String responseType) throws JSONException;
    abstract protected Map<String, String > generateTrackingParameters();
    @CallSuper
    protected void  showCurrentQuestion( ) {
        updateCurrentQuestionResponse();
    }
    @CallSuper
    protected void handleNextQuestion() throws JSONException {
        submitResponse();
        //handle condition mapping, this updates the currentIndx
        handleConditionMapping(survey.getQuestions().get(currentIndx));

    };

    @CallSuper
    protected void show(){
        if(shouldUpdatePreferences){
            Log.i("shouldUpdatePreferences", ""+shouldUpdatePreferences);
            if (!loadableSurveySpecs.surveyFreq.equals("untilresponse"))
                surveyFrequencyManager.recordSurveyTriggerOnPreferences(
                );
            trackWithAlium(context, generateTrackingParameters());
        }
    }

    @CallSuper
    protected  void submitSurvey(){
        if(loadableSurveySpecs.surveyFreq.equals("untilresponse"))surveyFrequencyManager.recordSurveyTriggerOnPreferences(
        );
    };


    private void handleConditionMapping(Question question){
        try{
            if(question!=null && !question.getConditionMapping().isEmpty()){
                List<Integer> conditionMappingArray=question.getConditionMapping();
                Log.e("condition-index", conditionMappingArray.toString()+"" +currentQuestionResponse.getIndexOfSelectedAnswer() );
                int nextQuestIndx= conditionMappingArray.get(
                        currentQuestionResponse.getIndexOfSelectedAnswer()
                );
                previousIndx=currentIndx;
                if(nextQuestIndx==-2){
                    currentIndx++;//next question
                }else if(nextQuestIndx==-1){
                    currentIndx=survey.getQuestions().size();//thankyou
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


    private void updateCurrentQuestionResponse(){
        try{
            currentQuestionResponse.setQuestionId(survey.getQuestions().get(currentIndx)
                    .getId());
            currentQuestionResponse.setResponseType(survey.getQuestions().get(currentIndx)
                    .getResponseType());

            currentQuestionResponse.setIndexOfSelectedAnswer(0);
        }catch (Exception e){
            Log.d("updateQuestionResp", e.toString());
        }
    }


}
