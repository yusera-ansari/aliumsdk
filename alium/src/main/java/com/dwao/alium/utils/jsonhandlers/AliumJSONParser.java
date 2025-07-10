package com.dwao.alium.utils.jsonhandlers;

import android.util.Log;

import com.dwao.alium.models.App;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionSetting;
import com.dwao.alium.models.SurConf;
import com.dwao.alium.models.SurInfo;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.SurveyInfo;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.models.TypeOfSur;
import com.dwao.alium.models.UrlMatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AliumJSONParser {

    public static SurConf getSurConfFromJSON(JSONObject jsonObject){
        try{
            SurConf surveyConfi=new SurConf();

            if(jsonObject.has("oid")){
                surveyConfi.setOid(jsonObject.getString("oid"));
            }
            if(jsonObject.has("svs")){
                JSONArray svs = jsonObject.getJSONArray("svs");
                List<SurInfo> infoList=new ArrayList<>();
                for(int i=0; i<svs.length(); i++){
                    JSONObject currentJObj=svs.getJSONObject(i);
                    SurInfo surInfo = new SurInfo();

                    surInfo.setNm(currentJObj.getString("nm"));
                    surInfo.setId(currentJObj.getString("id"));
                    JSONObject tps = currentJObj.getJSONObject("tps");//type of survey
                    JSONObject ap=tps.getJSONObject("ap");

                    JSONObject um=ap.getJSONObject("um");


                    UrlMatch urlMatch =new UrlMatch();
                    urlMatch.setU(um.getString("u"));

                    App app=new App();
                    app.setUm(urlMatch);
                    app.setVf(ap.getString("vf"));
                    surInfo.setSpath(ap.getString("spt"));
                    TypeOfSur typeOfSur=new TypeOfSur();
                    typeOfSur.setApp(app);

                    surInfo.setTps(typeOfSur);
                    infoList.add(surInfo);
                }
                surveyConfi.setSvs(infoList);
            }
           Log.d("survey",  surveyConfi.toString());
            return  surveyConfi;
        }catch (Exception e){
            Log.e("SurveyConf", "Error parsing Survey Config"+e.toString());
            return null;
        }
    }

    public static Survey getSurveyFromJson(JSONObject jsonObject){
//        survey questions - sq
//        question - qs
//        responseType - rt
//        responseOptions - ro
//        conditionMapping - cm
//        settings - st (not implemented)
//        surveyInfo - si
//        orgId - oid
//        surveyId - sid
//        type - stp
//        themeColors - thc
//        color1,color2,color3,...,colorn - c1, c2, c3,...,cn
//        question settings - st
//        required - req

    try{
        Survey survey =new Survey();
        if(jsonObject.has("sq")){
            JSONArray surveyQuestions=jsonObject.getJSONArray("sq");
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
                    question.setQuestion( currentQuest.has("qs")?
                           currentQuest.getString("qs"):"");

                    //response type
                   question.setResponseType(currentQuest.getString("rt"));

                   //response options
                   List<String> responseOptions= new ArrayList<>();
                   JSONArray responseOptArr= currentQuest.getJSONArray("ro");
                   if(responseOptArr.length()>0){
                       for(int res=0; res<responseOptArr.length();res++){
                           responseOptions.add(responseOptArr.getString(res));
                       }
                   }
                   question.setResponseOptions(responseOptions);

                   //condition Mapping
                    List<Integer> conditionMappingList=new ArrayList<>();
                    JSONArray conditionMappingArr=currentQuest.getJSONArray("cm");
                    if(conditionMappingArr.length()>0){
                        for(int cm=0; cm<conditionMappingArr.length();cm++){
                            conditionMappingList.add(conditionMappingArr.getInt(cm));
                        }
                    }
                    question.setConditionMapping(conditionMappingList);

                    //question settings
                    QuestionSetting questionSetting=new QuestionSetting();
                  if(currentQuest.has("st")){ //no settings when rt is 0 or -1
                      JSONObject questionSettingObj= currentQuest.getJSONObject("st");
                      if(questionSettingObj.has("req")){
                          questionSetting.setRequired(questionSettingObj.getBoolean("req"));
                      }
                      if(questionSettingObj.has("rtp")){
                          questionSetting.setRatingType(questionSettingObj.getString("rtp"));
                      }
                      if(questionSettingObj.has("oo")) {//other option
                            questionSetting.setOtherOption(questionSettingObj.getBoolean("oo"));
                      }
                  }
                    question.setQuestionSetting(questionSetting);

                    questions.add(question);
                }
                survey.setQuestions(questions);
            }
        }
        if(jsonObject.has("si")){
            JSONObject surveyInfoObj= jsonObject.getJSONObject("si");
            SurveyInfo surveyInfo = new SurveyInfo();
            surveyInfo.setOrgId(surveyInfoObj.getString("oid"));
            surveyInfo.setSurveyId(surveyInfoObj.getString("sid"));
             surveyInfo.setType(surveyInfoObj.getString("stp"));
//             surveyInfo.setViewFrequency(surveyInfoObj.getString("vf"));

             JSONObject themeColorObj= surveyInfoObj.getJSONObject("thc");
            ThemeColors  themeColors=new ThemeColors();
            themeColors.setColor1("#"+themeColorObj.getString("c1"));
            themeColors.setColor2("#"+themeColorObj.getString("c2"));
            themeColors.setColor3("#"+themeColorObj.getString("c3"));
            themeColors.setColor4("#"+themeColorObj.getString("c4"));
            themeColors.setColor5("#"+themeColorObj.getString("c5"));
            themeColors.setColor6("#"+themeColorObj.getString("c6"));
            themeColors.setColor7("#"+themeColorObj.getString("c7"));
            themeColors.setColor8("#"+themeColorObj.getString("c8"));
            themeColors.setColor9("#"+themeColorObj.getString("c9"));
            themeColors.setColor10("#"+themeColorObj.getString("c10"));
            themeColors.setColor11("#"+themeColorObj.getString("c11"));
            themeColors.setColor12("#"+themeColorObj.getString("c12"));
            themeColors.setColor13("#"+themeColorObj.getString("c13"));
            themeColors.setColor14("#"+themeColorObj.getString("c14"));
            themeColors.setColor15("#"+themeColorObj.getString("c15"));
            themeColors.setColor16("#"+themeColorObj.getString("c16"));
            themeColors.setColor17("#"+themeColorObj.getString("c17"));
            themeColors.setColor18("#"+themeColorObj.getString("c18"));
            themeColors.setColor19("#"+themeColorObj.getString("c19"));
            themeColors.setColor20("#"+themeColorObj.getString("c20"));
            themeColors.setColor21("#"+themeColorObj.getString("c21"));
            themeColors.setColor22("#"+themeColorObj.getString("c22"));
            themeColors.setColor23("#"+themeColorObj.getString("c23"));
            surveyInfo.setThemeColors(themeColors);
            survey.setSurveyInfo(surveyInfo);

        }
        return  survey;
    }catch (Exception e){
        Log.e("getSurveyFromJSON", e.toString());
        return null;
    }
    };
}
