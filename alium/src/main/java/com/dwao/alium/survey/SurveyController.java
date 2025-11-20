package com.dwao.alium.survey;

import static com.dwao.alium.survey.SurveyTracker.trackWithAlium;

import android.content.Context;
import android.util.Log;

import androidx.annotation.CallSuper;

import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.models.ExecutableSurveySpecs;
import com.dwao.alium.models.LoadableSurveySpecs;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyParameters;
import com.dwao.alium.services.Logger;
import com.dwao.alium.utils.preferences.AliumPreferences;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class SurveyController {
    protected final String uuid;
    Survey survey;
    protected ExecutableSurveySpecs executableSurveySpecs;

    int responseSubmitIndex=1;
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
        this.aliumPreferences= AliumPreferences.getInstance();
        this.surveyFrequencyManager=  FrequencyManagerFactory
                .getFrequencyManager(aliumPreferences, loadableSurveySpecs.key,
                loadableSurveySpecs.surveyFreq,
                loadableSurveySpecs.customSurveyData);
    }

    abstract protected void generateQuestion(String responseType) throws JSONException;
    abstract protected Map<String, Object > generateTrackingParameters();
    @CallSuper
    protected void  showCurrentQuestion( ) {
      if(shouldUpdatePreferences)  updateCurrentQuestionResponse();
    }
    @CallSuper
    protected void handleNextQuestion() throws JSONException {
//        submitResponse();
        //handle condition mapping, this updates the currentIndx
        handleConditionMapping(survey.getQuestions().get(currentIndx));

    };
    //os: Once per Submit -untilresponse
    //o: Once - onlyonce
    //rp: Repeatedly -overandover
    @CallSuper
    protected void show(){
        if(shouldUpdatePreferences){

            if (!loadableSurveySpecs.surveyFreq.equals("os")) {//untilresponse
                if(currentIndx>=responseSubmitIndex){
                    surveyFrequencyManager.recordSurveyTriggerOnPreferences(
                    );
                }

            }
            Map<String, Object> params = generateTrackingParameters();
            params.put("eventType","load");
            trackWithAlium(context, params);
        }
    shouldUpdatePreferences=true;
    }

    @CallSuper
    protected  void submitSurvey(){
        if(loadableSurveySpecs.surveyFreq.equals("os")
        && !currentQuestionResponse.getResponseType().equals("0")
        ){
            if(currentIndx>=responseSubmitIndex){
                surveyFrequencyManager.recordSurveyTriggerOnPreferences(
                );
            }
        }
    };


    private void handleConditionMapping(Question question){
        try{
            if(question!=null && !question.getConditionMapping().isEmpty()){
                List<Integer> conditionMappingArray=question.getConditionMapping();

                int nextQuestIndx= conditionMappingArray.get(
                        // this is -1 if no answer is selected
                        // for radio and checkbox thus throws an error
                        // and moves to next index
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
            }else{
                currentIndx++;  //in some cases condition map might be empty, in that case next question
            }
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR,"Condition-Map", e.toString());
            currentIndx++;
        }
    }
    protected void submitResponse() {
//        if thankyou or cover page or no response
        if(survey.getQuestions().get(currentIndx).getResponseType().equals("-1")
                ||survey.getQuestions().get(currentIndx).getResponseType().equals("0")||
        currentQuestionResponse.getQuestionResponse().isEmpty()
        ){
            Logger.log(Logger.LogLevel.DEBUG, "submit", "no response");
            return;
        }
        Map<String, Object > responseMap=new HashMap<>(generateTrackingParameters());
        responseMap.put("questionId",(currentQuestionResponse.getQuestionId()));
//        responseMap.put("response",currentQuestionResponse.getQuestionResponse());
//        responseMap.put("respType",currentQuestionResponse.getResponseType());
        String resp = (String)responseMap.get("response");
        if(resp==null|| resp.isEmpty())return;
        trackWithAlium(context,responseMap );
    }


    private void updateCurrentQuestionResponse(){
        try{
            currentQuestionResponse.setQuestionId(survey.getQuestions().get(currentIndx)
                    .getId());
            currentQuestionResponse.setResponseType(survey.getQuestions().get(currentIndx)
                    .getResponseType());
            currentQuestionResponse.setQuestionResponse("");

            currentQuestionResponse.setIndexOfSelectedAnswer(0);
        }catch (Exception e){
            Logger.log(Logger.LogLevel.ERROR,"updateQuestionResp", e.toString());
        }
    }


}
