package com.dwao.alium.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dwao.alium.R;
import com.dwao.alium.listeners.NpsOptionClickListener;
import com.dwao.alium.listeners.RatingOptionListener;
import com.dwao.alium.models.QuestionResponse;
import com.dwao.alium.models.Survey;

import java.util.List;

public class RatingAdapter extends BaseAdapter {
    Context ctx;
    RatingOptionListener ratingOptionListener;
    QuestionResponse currentQuestionResponse;
    List<String> ratingOptions;
    public RatingAdapter(Context ctx,  RatingOptionListener ratingOptionListener,
                         QuestionResponse currentQuestionResponse, List<String> ratingOptions){
        this.ctx=ctx;
        this.ratingOptionListener=ratingOptionListener;
        this.currentQuestionResponse=currentQuestionResponse;
        this.ratingOptions=ratingOptions;
    }
    @Override
    public int getCount() {
        return ratingOptions!=null?ratingOptions.size():5;
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
//        view = LayoutInflater.from(this).inflate(R.layout.)
        return null;
    }
}
