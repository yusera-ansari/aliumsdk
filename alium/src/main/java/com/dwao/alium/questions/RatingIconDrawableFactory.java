package com.dwao.alium.questions;

import android.content.Context;


public class RatingIconDrawableFactory {
    public static RatingIconDrawable getDrawable(RatingType type, Context context){
        switch (type){

            case hearts:
                return new HeartRating(context);
            case emoji:
                return new EmojiRating(context);
            case ticks:
                return new TickRating(context);
            default:
                return new StarRating(context);
        }
    };
}
