package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dwao.alium.R;

class StarRating implements   RatingIconDrawable{
    Context context;
   private StarRating(){}
    public  StarRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
     return   ContextCompat.getDrawable(context, R.drawable.tabler_star_filled);
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.tabler_star);
    }
}
