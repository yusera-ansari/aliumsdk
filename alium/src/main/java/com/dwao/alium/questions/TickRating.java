package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dwao.alium.R;

class TickRating implements   RatingIconDrawable{
    Context context;
    private TickRating(){}
    public  TickRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.hugeicons_tick_double, null);
    }

    @Override
    public Drawable[] getFilledIconList() {
        return new Drawable[0];
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.hugeicons_tick_double, null);
    }
}
