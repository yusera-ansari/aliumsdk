package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dwao.alium.R;

class TickRating implements   RatingIconDrawable{
    Context context;
    private TickRating(){}
    public  TickRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.hugeicons_tick_double);
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.hugeicons_tick_double);
    }
}
