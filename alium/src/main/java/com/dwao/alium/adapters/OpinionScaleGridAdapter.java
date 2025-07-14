package com.dwao.alium.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatButton;

import com.dwao.alium.R;
import com.dwao.alium.listeners.OpinionClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.ThemeColors;

import java.util.List;

public class OpinionScaleGridAdapter extends BaseAdapter {
    Context context;
    List<String> options;
    ThemeColors themeColors;
    QuestionResponse currentQuestionResponse;
    OpinionClickListener listener;
    int selectedOption=-1;
    public OpinionScaleGridAdapter(Context context, List<String> options, QuestionResponse currentQuestionResponse,
                                   OpinionClickListener listener,
                                   ThemeColors themeColors){
        this.context =context;
        this.themeColors=themeColors;
        this.options=options;
        this.currentQuestionResponse= currentQuestionResponse;
        this.listener=listener;
        this.selectedOption = currentQuestionResponse.getIndexOfSelectedAnswer();

    }

    public void updatedSelectedOption(int position){
        if(selectedOption==position){//if was already selected
            this.selectedOption=-1; //unselect
        }else{
            this.selectedOption=position;

        }
        if(selectedOption!=-1) {
            currentQuestionResponse.setIndexOfSelectedAnswer(selectedOption);
            currentQuestionResponse.setQuestionResponse(options.get(selectedOption));
        }else{
            currentQuestionResponse.setQuestionResponse("");
            currentQuestionResponse.setIndexOfSelectedAnswer(-1);
        };
        Log.d("selected Opt", ""+selectedOption);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view= LayoutInflater.from(context).inflate(R.layout.opnion_scale_option, null);
        AppCompatButton button = view.findViewById(R.id.opinion_scale_option);
        button.setText(options.get(position));
        Log.d("render", "opinion scale options: "+position);
        GradientDrawable d=(GradientDrawable) button.getBackground();
   if(d!=null){
       d.mutate();
   }
        if(themeColors!=null){
            //		--color15 - #fff Opinion Button Background Color
//		--color16 - #333 Opinion Button Text Color
//		--color17 - #ffc100 Opinion Button selected bg
//		--color18 - #333 Opinion Button selected text color
            if(d!=null){
                d.setColor(Color.parseColor(themeColors.getColor15()));
            }
            button.setTextColor(Color.parseColor(themeColors.getColor16()));
            if(position==selectedOption){
                if(d!=null){
                    d.setColor(Color.parseColor(themeColors.getColor17()));
                }
                button.setTextColor(Color.parseColor(themeColors.getColor18()));

            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });

        return view;
    }
}
