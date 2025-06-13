package com.dwao.alium.survey;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dwao.alium.listeners.RatingClickListener;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.questions.RatingIconDrawable;
import com.dwao.alium.questions.RatingIconDrawableFactory;
import com.dwao.alium.questions.RatingType;

public class CustomRatingView extends LinearLayout {
    private int iconCount = 5;
    private float currentRating = 0f;
    private ImageView[] stars;
    private Drawable fullIcon, emptyIcon;
    Context context;
    ThemeColors themeColors;

    private RatingType ratingType;
    private RatingClickListener listener;

    public CustomRatingView setIconCount(int count){
        this.iconCount=count;
        return this;
    }
    public CustomRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        setRatingType(RatingType.STARS);
        render( );


    }

    public void render(){
        removeAllViews();
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        RatingIconDrawable drawable=  RatingIconDrawableFactory.getDrawable(this.ratingType, context);
        fullIcon = drawable.getFilledIcon();
        emptyIcon = drawable.getEmptyIcon();

        stars = new ImageView[iconCount];

        for (int i = 0; i < iconCount; i++) {
            final int index = i;
            ImageView star = new ImageView(context);
            star.setImageDrawable(emptyIcon);
            LayoutParams params = new LayoutParams(120, 120); // Adjust size here
            params.setMargins(6, 8, 6, 8); // Spacing between icons
            star.setLayoutParams(params);
            star.setClickable(true);

            star.setOnClickListener(v -> setRating(index + 1));
            star.setColorFilter(Color.WHITE);
            if(themeColors!=null){
                //		--color19 - #fff Rating Button Background Color
//		--color20 - #333 Rating Button Text Color
//		--color21 - #ffc100 Rating Button selected bg
//		--color22 - #333 Rating utton selected text color
                star.setColorFilter(Color.parseColor(themeColors.getColor19()));
            }
            addView(star);
            stars[i] = star;
        }
    }

    public void setRating(float rating) {

        currentRating = rating;
        for (int i = 0; i < iconCount; i++) {
            if (i < rating) {
                stars[i].setImageDrawable(fullIcon);
                if(themeColors!=null){
                    //		--color21 - #ffc100 Rating Button selected bg
                    stars[i].setColorFilter(Color.parseColor(themeColors.getColor21()));
                }
            } else {
                stars[i].setImageDrawable(emptyIcon);
                stars[i].setColorFilter(Color.parseColor(themeColors.getColor19()));
            }
        }
        if(listener!=null){
            listener.onClick((int) (rating-1));
        }
    }

    public float getRating() {
        return currentRating;
    }
}

