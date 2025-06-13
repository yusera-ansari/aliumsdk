package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dwao.alium.R;

class HeartRating implements   RatingIconDrawable{
    Context context;
    private HeartRating(){}
    public  HeartRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.ph_heart_fill);
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.ph_heart);
    }
}
