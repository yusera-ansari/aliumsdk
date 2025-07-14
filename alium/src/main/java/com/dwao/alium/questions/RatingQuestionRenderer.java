package com.dwao.alium.questions;

import static com.dwao.alium.utils.Util.setCtaEnabled;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dwao.alium.R;
import com.dwao.alium.listeners.RatingClickListener;
import com.dwao.alium.models.Question;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.QuestionSetting;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.services.Logger;

import java.util.List;

public class RatingQuestionRenderer implements QuestionRenderer{

List<String> ratingOptions;
    ThemeColors themeColors;
    QuestionSetting questionSetting;
    private boolean isRequired = false;

    private Question currentquestion;
    public Question getCurrentquestion() {
        return currentquestion;
    }

    public RatingQuestionRenderer setCurrentquestion(Question currentquestion) {
        this.currentquestion = currentquestion;
        return  this.setRatingOptions(this.currentquestion.getResponseOptions())
                .setRequired(this.currentquestion.getQuestionSetting().getRequired())
                .setQuestionSetting(this.currentquestion.getQuestionSetting());

    }
    public RatingQuestionRenderer setRequired(boolean required) {
        isRequired = required;
        return this;
    }

    public RatingQuestionRenderer setQuestionSetting(QuestionSetting questionSetting) {
        this.questionSetting = questionSetting;
        return  this;
    }

    public RatingQuestionRenderer setRatingOptions(List<String> options){
    this.ratingOptions=options;
    return  this;
}


    public RatingQuestionRenderer setTheme(ThemeColors themeColors){
          this.themeColors=themeColors;
        return  this;
    }

    //		--color19 - #fff Rating Button Background Color
//		--color20 - #333 Rating Button Text Color
//		--color21 - #ffc100 Rating Button selected bg
//		--color22 - #333 Rating utton selected text color
    @Override
    public void renderQuestion(Context context, ViewGroup layout, QuestionResponse currentQuestionResponse, View nextQuestionBtn) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_question, null);
        CustomRatingView customRatingView=view.findViewById(R.id.custom_rating_view);
        if(currentQuestionResponse.getQuestionResponse().isEmpty() && currentQuestionResponse.getIndexOfSelectedAnswer()==0){
            currentQuestionResponse.setIndexOfSelectedAnswer(-1);
        }

        RatingClickListener listener=new RatingClickListener() {
            @Override
            public void onClick(int position) {
               if(position>=0){
                   currentQuestionResponse.setQuestionResponse(ratingOptions.get(position));
               }else{
                   currentQuestionResponse.setQuestionResponse( "");
               }
                currentQuestionResponse.setIndexOfSelectedAnswer(position);

                if(isRequired){
                    setCtaEnabled(nextQuestionBtn, !currentQuestionResponse
                            .getQuestionResponse().isEmpty());
                }
            }
        };

        RatingType ratingType=RatingType.stars;
        try{
            if(questionSetting!=null){
                ratingType=RatingType.valueOf(questionSetting.getRatingType().toLowerCase());
            }
        }catch (Exception e){
                Logger.log(Logger.LogLevel.ERROR,"RatingType", "RatingType Doesn't exist"+questionSetting.getRatingType());
        }
            customRatingView.setRatingType(ratingType)
                            .setIconCount(ratingOptions.size())
                    .setThemeColors(themeColors)
                    .setListener(listener)
                    .render( );
            customRatingView.setRating(currentQuestionResponse.getIndexOfSelectedAnswer()+1);
        layout.addView(view);

    }
}
