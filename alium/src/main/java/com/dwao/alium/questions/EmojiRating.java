package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dwao.alium.R;

class EmojiRating implements   RatingIconDrawable{
    Context context;
    private EmojiRating(){}
    public  EmojiRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.line_md_emoji_smile_filled);
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.line_md_emoji_smile);
    }
}
