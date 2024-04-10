package com.dwao.alium.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.dwao.alium.R;
import com.dwao.alium.listeners.CheckBoxClickListener;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxGridViewAdapter extends BaseAdapter {
    Context context;
    List<String> checkBoxList;
    CheckBoxClickListener listener;
    List<Integer> selectedItems;
    public CheckBoxGridViewAdapter(Context context,List<String> checkBoxList,
                                   CheckBoxClickListener listener){
        this.context=context;
        this.checkBoxList=checkBoxList;
        this.listener=listener;
        selectedItems=new ArrayList<>();
    }
    public void updateCheckedItem(int pos, boolean selected){
        if(selected){
            this.selectedItems.add(pos);

        }else {
            this.selectedItems.remove(Integer.valueOf(pos));
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return 0;
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
       view = LayoutInflater.from(context).inflate(R.layout.checkbox_btn,null);
        AppCompatCheckBox checkBox=view.findViewById(R.id.checkbox_btn);
        checkBox.setText(checkBoxList.get(position));
        checkBox.setChecked(selectedItems.contains(position));
        Log.d("pos"+position, "pos: "+checkBoxList.get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(selectedItems.contains(Integer.valueOf(position)))
                    listener.onClick(position, b);
            }
        });


       return  view;
    }
}
