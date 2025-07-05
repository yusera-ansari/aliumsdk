package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dwao.alium.R;

class StarRating implements   RatingIconDrawable{
    Context context;
   private StarRating(){}
    public  StarRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
       Log.e("star", "is resource null: "+(ResourcesCompat.getDrawable(context.getResources(), R.drawable.tabler_star_filled,null)==null));
     return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.tabler_star_filled,null);
    }

    @Override
    public Drawable[] getFilledIconList() {
        return new Drawable[0];
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.tabler_star,null);
    }
}
