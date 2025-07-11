package com.dwao.alium.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dwao.alium.R;
import com.dwao.alium.listeners.RadioClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;

import org.json.JSONObject;

import java.util.List;

public class RadioBtnAdapter extends RecyclerView.Adapter<RadioBtnAdapter.ViewHolder> {
     int selectedPosition;
    List<String> radioBtnList;
    RadioClickListener radioClickListener;
    QuestionResponse currentQuestionResponse;
 ThemeColors themeColors;
     public RadioBtnAdapter(List<String> radioBtnList, RadioClickListener radioClickListener,
                            QuestionResponse currentQuestionResponse, ThemeColors themeColors){
         this.currentQuestionResponse=currentQuestionResponse;
        this.radioBtnList=radioBtnList;
        this.radioClickListener=radioClickListener;
        this.selectedPosition=-1;
        this.themeColors=themeColors;
    }
    public void updateCheckedItem(int selectedPosition){
        this.selectedPosition=selectedPosition;

        notifyDataSetChanged();
    }
    public void updateResponse(boolean isOtherOptionEnabled,
                                 String otherResponse){
        currentQuestionResponse.setQuestionResponse(radioBtnList.get(selectedPosition));
    if(isOtherOptionEnabled){
       if(selectedPosition==radioBtnList.size()-1) currentQuestionResponse.setQuestionResponse(radioBtnList.get(selectedPosition)+"|"+otherResponse);
    }
        currentQuestionResponse.setIndexOfSelectedAnswer(selectedPosition);
    Log.d("response", "response of the selected item is: "+ currentQuestionResponse.getQuestionResponse());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_btn, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.radioButton.setText(radioBtnList.get(position));
        //		--color8 -  #ffffff Single Choice Background Color
//		--color9 -  #00C764 Single Choice Icon Color
//		--color10 - #333 Single Choice Text Color
        if(themeColors!=null){
            try{
                    holder.radioButton.setTextColor(Color.parseColor(themeColors
                            .getColor10()
                            ));
                GradientDrawable background =(GradientDrawable) holder.radioButton.getBackground();
                background.mutate();
                   background.setColor(Color.parseColor(themeColors.getColor8()));
            }catch (Exception e){
                Log.e("surveyUICheckBox", e.toString());
            }}
        holder.radioButton.setChecked(position==selectedPosition);
        holder.radioButton.setButtonTintList(new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        }, new int[]{
               Color.parseColor(themeColors.getColor9()),
                Color.parseColor(themeColors.getColor9())
        }));
        Log.d("pos"+position, "pos: "+radioBtnList.get(position));
        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   radioClickListener.onClick(holder.getAdapterPosition());
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        return radioBtnList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        RadioButton radioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton=itemView.findViewById(R.id.radio_btn);
        }
    }
}
