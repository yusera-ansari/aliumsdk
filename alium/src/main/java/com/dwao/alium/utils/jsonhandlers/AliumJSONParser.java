package com.dwao.alium.utils.jsonhandlers;

import android.util.Log;

import com.dwao.alium.models.Question;
import com.dwao.alium.models.Survey;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AliumJSONParser {
    public static void getSurveyFromJson(JSONObject jsonObject, Survey survey){

    try{
        if(jsonObject.has("surveyQuestions")){
            JSONArray surveyQuestions=jsonObject.getJSONArray("surveyQuestions");
            if(surveyQuestions.length()>0){
                List<Question> questions=new ArrayList<>();
                for(int i=0; i<surveyQuestions.length(); i++){
                    //current question
                    JSONObject currentQuest=surveyQuestions.getJSONObject(i);

                    //question obj
                    Question question=new Question();

                    //id
                    if(currentQuest.has("id"))question.setId(currentQuest.getInt("id"));

                    //question
                    question.setQuestion( currentQuest.has("question")?
                           currentQuest.getString("question"):"");

                    //response type
                   question.setResponseType(currentQuest.getString("responseType"));

                   //response options
                   List<String> responseOptions= new ArrayList<>();
                   JSONArray responseOptArr= currentQuest.getJSONArray("responseOptions");
                   if(responseOptArr.length()>0){
                       for(int res=0; res<responseOptArr.length();res++){
                           responseOptions.add(responseOptArr.getString(res));
                       }
                   }
                   question.setResponseOptions(responseOptions);

                   //condition Mapping
                    List<Integer> conditionMappingList=new ArrayList<>();
                    JSONArray conditionMappingArr=currentQuest.getJSONArray("conditionMapping");
                    if(conditionMappingArr.length()>0){
                        for(int cm=0; cm<conditionMappingArr.length();cm++){
                            conditionMappingList.add(conditionMappingArr.getInt(cm));
                        }
                    }
                    question.setConditionMapping(conditionMappingList);

                    questions.add(question);
                }
            }
        }
    }catch (Exception e){
        Log.e("getSurveyFromJSON", e.toString());
    }
    };
}
