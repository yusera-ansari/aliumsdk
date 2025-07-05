package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dwao.alium.R;

class EmojiRating implements   RatingIconDrawable{
    Context context;
    private EmojiRating(){}
    public  EmojiRating(Context context){
        this.context=context;
    }
    @Override
    public Drawable getFilledIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_md_emoji_smile_filled, null);
    }

    @Override
    public Drawable[] getFilledIconList() {
        return new Drawable[]{
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_md_emoji_cry, null),
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_md_emoji_frown,null),
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.mingcute_emoji_smile,null),
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_emoji_emotions,null),
                ResourcesCompat.getDrawable(context.getResources(), R.drawable.fluent_emoji_laugh,null)
        };
    }

    @Override
    public Drawable getEmptyIcon() {
        return   ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_md_emoji_smile, null);
    }
}
