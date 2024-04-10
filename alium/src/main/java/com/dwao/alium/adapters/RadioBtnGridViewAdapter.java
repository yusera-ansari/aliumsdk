package com.dwao.alium.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.dwao.alium.R;
import com.dwao.alium.listeners.CheckBoxClickListener;
import com.dwao.alium.listeners.RadioClickListener;

import java.util.List;

public class RadioBtnGridViewAdapter extends BaseAdapter {
    int selectedPosition;
    List<String> radioBtnList;
    RadioClickListener radioClickListener;
    Context context;
    public RadioBtnGridViewAdapter(Context context, List<String> checkBoxList,
                                   CheckBoxClickListener listener){
        this.context=context;
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
        view= LayoutInflater.from(context).inflate(R.layout.radio_btn, null);
        RadioButton radioButton=view.findViewById(R.id.radio_btn);
     radioButton.setText(radioBtnList.get(position));
     radioButton.setChecked(position==selectedPosition);
        Log.d("pos"+position, "pos: "+radioBtnList.get(position));
     radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    radioClickListener.onClick(position);
                }
            }
        });
        return view;
    }
}
