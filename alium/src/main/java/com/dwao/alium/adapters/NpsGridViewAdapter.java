package com.dwao.alium.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatButton;

import com.dwao.alium.R;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;

import org.json.JSONObject;


public class NpsGridViewAdapter extends BaseAdapter{
    Context ctx;
    int selectedOption=-1;
    NpsOptionClickListener npsOptionClickListener;
   ThemeColors themeColors;
    QuestionResponse currentQuestionResponse;
    public NpsGridViewAdapter(Context ctx, NpsOptionClickListener npsOptionClickListener,
                              QuestionResponse currentQuestionResponse,ThemeColors themeColors){
    this.ctx=ctx;
    this.npsOptionClickListener=npsOptionClickListener;
    this.currentQuestionResponse=currentQuestionResponse;
    this.themeColors = themeColors;
}

    public void updatedSelectedOption(int position){
        if(selectedOption==position){//if was already selected
            this.selectedOption=-1; //unselect
        }else{
            this.selectedOption=position;

//            if(position>5){
//                currentQuestionResponse.setIndexOfSelectedAnswer(2);
//            }else if(position<5){
//                currentQuestionResponse.setIndexOfSelectedAnswer(0);
//            }else {
//                currentQuestionResponse.setIndexOfSelectedAnswer(1);
//            }

        }
       if(selectedOption!=-1) {
           currentQuestionResponse.setIndexOfSelectedAnswer(selectedOption);
           currentQuestionResponse.setQuestionResponse(String.valueOf(selectedOption));
       }else{
           currentQuestionResponse.setQuestionResponse("");
           currentQuestionResponse.setIndexOfSelectedAnswer(0);
       };
       Log.d("selected Opt", ""+selectedOption);
       notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view=LayoutInflater.from(ctx).inflate(R.layout.nps_option, null);
        AppCompatButton  npsOption=view.findViewById(R.id.nps_option);
        npsOption.setText(String.valueOf(position));

        GradientDrawable d=(GradientDrawable) npsOption.getBackground();
        d.mutate();

        //		--color11 - #fff NPS Button Background Color
//		--color12 - #333 NPS Button Text Color
//		--color13 - #ffc100 NPS Button selected bg
//		--color14 - #333 NPS Button selected Text Color
        if(themeColors!=null) {
            try {
                    npsOption.setTextColor(Color.parseColor(themeColors.getColor12()
                           ));
            } catch (Exception e) {
                Log.e("surveyUICheckBox", e.toString());
            }
        }
        if(selectedOption==position){
            try{
//                assert surveyUi != null; //this will throw assertion error

                d.setColor(Color.parseColor(themeColors.getColor13() ));
                npsOption.setTextColor(Color.parseColor(themeColors.getColor14()));

                //uncomment
//
//                d.setColorFilter(Color.parseColor(themeColors.getColor6()),PorterDuff.Mode.SRC_IN);
//                d.setStroke(5,Color.parseColor(themeColors.getColor6()));
            }catch(Exception e){
                d.setColor(Color.parseColor(themeColors.getColor11()));
//                d.setColorFilter(Color.parseColor("#00ff00" ),PorterDuff.Mode.SRC_IN);
            }

        }else{
            try{
                d.setColor(Color.parseColor(themeColors.getColor11()));
//               d.setStroke(2,Color.parseColor(themeColors.getColor6()));
            }
            catch (Exception e){
                d.setStroke(2, Color.BLACK);
            }
        }
//        npsOption.setBackgroundDrawable(d);
//        npsOption.setBackgroundColor(selectedOption!=position? Color.parseColor("#808080")
//                :Color.parseColor("#00ff00"));
        npsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                npsOptionClickListener.onClick(position);
            }
        });
        return view;

    }
}