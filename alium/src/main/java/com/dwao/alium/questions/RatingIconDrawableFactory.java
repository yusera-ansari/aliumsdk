package com.dwao.alium.questions;

import android.content.Context;


public class RatingIconDrawableFactory {
    public static RatingIconDrawable getDrawable(RatingType type, Context context){
        switch (type){

            case HEART:
                return new HeartRating(context);
            case EMOJI:
                return new EmojiRating(context);
            case TICKS:
                return new TickRating(context);
            default:
                return new StarRating(context);
        }
    };
}
