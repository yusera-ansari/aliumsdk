package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dwao.alium.R;

class HeartRating implements   RatingIconDrawable{
    Context context;
    private HeartRating(){}
    public  HeartRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(),R.drawable.ph_heart_fill,null) ;
    }

    @Override
    public Drawable[] getFilledIconList() {
        return new Drawable[0];
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.ph_heart,null);
    }
}
