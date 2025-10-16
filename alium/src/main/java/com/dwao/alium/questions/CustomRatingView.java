package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.view.ContextThemeWrapper;

import com.dwao.alium.listeners.RatingClickListener;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.services.Logger;

public class CustomRatingView extends LinearLayout {
    private int iconCount = 5;
    private float currentRating = 0f;
    private ImageView[] icon;
    private Drawable fullIcon, emptyIcon;
    private Drawable[] filledIconList;
    Context context;
    ThemeColors themeColors;

    private RatingType ratingType;
    private RatingClickListener listener;

    public CustomRatingView setIconCount(int count){
        this.iconCount=count;
        return this;
    }
    public CustomRatingView(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar), attrs);
        this.context=context;
        init( );

    }

    public CustomRatingView setThemeColors(ThemeColors themeColors) {
        this.themeColors = themeColors;
        return this;
    }

    public CustomRatingView setListener(RatingClickListener listener){
        this.listener=listener;
        return  this;
    }
    public CustomRatingView setRatingType(RatingType type){
            this.ratingType=type;
            return this;
    }


    private void init(   ) {
        setOrientation(HORIZONTAL);
        setRatingType(RatingType.star);
        render( );


    }

    public void render(){
        removeAllViews();
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        RatingIconDrawable drawable=  RatingIconDrawableFactory.getDrawable(this.ratingType, context);

        if(ratingType.equals(RatingType.emoji)){
            filledIconList = drawable.getFilledIconList();
        }
            fullIcon = drawable.getFilledIcon();
            emptyIcon = drawable.getEmptyIcon();

        icon = new ImageView[iconCount];

        for (int i = 0; i < iconCount; i++) {
            final int index = i;

            ImageView img = new ImageView(context);
            img.setImageDrawable(emptyIcon);

            img.setClickable(true);

            img.setOnClickListener(v -> setRating(index + 1));
            img.setColorFilter(Color.WHITE);
            if(themeColors!=null){
                //		--color19 - #fff Rating Button Background Color
//		--color20 - #333 Rating Button Text Color
//		--color21 - #ffc100 Rating Button selected bg
//		--color22 - #333 Rating utton selected text color
                img.setColorFilter(Color.parseColor(themeColors.getColor19()));
            }
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int parentwidth = getWidth();

                    int minimumSize =((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            28,context.getResources().getDisplayMetrics() ));
                    int size = (int)((parentwidth * 0.65 )/ 5 ) ;

                        if(size<minimumSize){
                            size=minimumSize;
                        }
                    LayoutParams params = new LayoutParams(size, size); // Adjust size here
                    params.setMargins(6, 8, 6, 8); // Spacing between icons
                    img.setLayoutParams(params);
                }
            });
            addView(img);
            icon[i] = img;
        }
    }

    public CustomRatingView setRating(float rating) {
        try{

            currentRating = rating;
            for (int i = 0; i < iconCount; i++) {

                if (i < rating) {
                    icon[i].setImageDrawable(ratingType.equals(RatingType.emoji) ?
                            filledIconList[i]
                            : fullIcon);
                    if (themeColors != null) {
                        //		--color21 - #ffc100 Rating Button selected bg
                        icon[i].setColorFilter(Color.parseColor(themeColors.getColor21()));
                    }
                } else {
                    icon[i].setImageDrawable(emptyIcon);
                    icon[i].setColorFilter(Color.parseColor(themeColors.getColor19()));
                }
            }
            if (listener != null) {
                listener.onClick((int) (rating -1));
            }
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR,"CustomRating", "error: "+e);
        }
        return this;
    }

    public float getRating() {
        return currentRating;
    }
}

