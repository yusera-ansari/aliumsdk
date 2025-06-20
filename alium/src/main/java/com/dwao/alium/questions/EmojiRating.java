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
    public Drawable[] getFilledIconList() {
        return new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.line_md_emoji_cry),
                ContextCompat.getDrawable(context, R.drawable.line_md_emoji_frown),
                ContextCompat.getDrawable(context, R.drawable.mingcute_emoji_smile),
                ContextCompat.getDrawable(context, R.drawable.ic_baseline_emoji_emotions),
                ContextCompat.getDrawable(context, R.drawable.fluent_emoji_laugh)
        };
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ContextCompat.getDrawable(context, R.drawable.line_md_emoji_smile);
    }
}
