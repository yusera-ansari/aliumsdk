package com.dwao.alium.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.dwao.alium.R;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.services.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxRecyViewAdapter extends RecyclerView.Adapter<CheckBoxRecyViewAdapter.ViewHolder> {

    List<String> checkBoxList;
    CheckBoxClickListener listener;
    List<Integer> selectedItems;
    QuestionResponse currentQuestionResponse;
   ThemeColors themeColors;
    public CheckBoxRecyViewAdapter(List<String> checkBoxList,
                                   CheckBoxClickListener listener,
                                   QuestionResponse currentQuestionResponse, ThemeColors themeColors){
        this.checkBoxList=checkBoxList;
        this.listener=listener;
        selectedItems=currentQuestionResponse.getIndexOfSelectedAnswers();
        this.currentQuestionResponse=currentQuestionResponse;
      this.themeColors=themeColors;
    }
      public void updateResponseString(Boolean otherOptionEnabled, String otherResponse){
        String resp="";
        if(otherOptionEnabled){
            for(int i=0; i< selectedItems.size(); i++){
                if(i==selectedItems.size()-1){ //if last item
                    //(no need of "," in last item)
                    //if last item(pos) is other option
                    if(selectedItems.get(i)== checkBoxList.size()-1){
                        //response for other option
                        resp+= checkBoxList.get(selectedItems.get(i))+"|"+otherResponse;
                        break;
                    }
                    resp+= checkBoxList.get(selectedItems.get(i));
                    break;
                }
                //if current item is other option
                if(selectedItems.get(i)== checkBoxList.size()-1){
                    resp+= checkBoxList.get(selectedItems.get(i))+"|"+otherResponse+",";
                }
                //normal response
                else{
                    resp += checkBoxList.get(selectedItems.get(i)) + ",";
                }
            }
        }else{
            for(int i=0; i< selectedItems.size(); i++){
                if(i==selectedItems.size()-1){
                    resp+= checkBoxList.get(selectedItems.get(i));
                    break;
                }
                resp+= checkBoxList.get(selectedItems.get(i))+",";
            }
        }
        currentQuestionResponse.setQuestionResponse(resp);
        currentQuestionResponse.setIndexOfSelectedAnswers(selectedItems);
    }



    public void updateCheckedItem(int pos, boolean selected){
      if(selected){
          this.selectedItems.add(pos);

      }else {
          this.selectedItems.remove(Integer.valueOf(pos));
      }

        notifyItemChanged(pos);

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_btn, parent, false);
        return  new ViewHolder(view);
    }
    //		--color5 - #ffffff  Multiple Choice Background Color
//		--color6 - #00C764  Multiple Choice Icon Color
//		--color7 - #333 Multiple Choice Text Color
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setText(checkBoxList.get(position));
        if(themeColors!=null){
           try{
                holder.checkBox.setTextColor(Color.parseColor(themeColors.getColor7()

                        ));
               GradientDrawable background= (GradientDrawable) holder.checkBox.getBackground();
             background.mutate();

               background.setColor(Color.parseColor(themeColors.getColor5()
               ));
               holder.checkBox.setButtonTintList(new ColorStateList(new int[][]{
                       new int[]{-android.R.attr.state_checked},
                       new int[]{android.R.attr.state_checked}
               }, new int[]{ Color.parseColor(themeColors.getColor6()),
                       Color.parseColor(themeColors.getColor6())}));
           }catch (Exception e){
               Logger.log(Logger.LogLevel.ERROR, "surveyUICheckBox", e.toString());
           }
        }
        // Remove listener temporarily
//        When notifyItemChanged(pos) is called, it triggers
//        onBindViewHolder(), which includes setChecked(). This in turn would trigger OnCheckedChangeListener, causing your logic to re-fire unintentionally — potentially leading to a mismatch between UI and selectedItems.
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedItems.contains(position));
//    holder.checkBox.setButtonDrawable(R.drawable.ic_check_circle);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//           if(selectedItems.contains(Integer.valueOf(holder.getAdapterPosition())))
               listener.onClick(holder.getAdapterPosition(), b, selectedItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.checkBoxList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        AppCompatCheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.checkbox_btn);
        }
    }
}
