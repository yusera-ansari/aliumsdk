package com.dwao.alium.survey;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
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
        setRatingType(RatingType.stars);
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
                    float density= context.getResources().getDisplayMetrics().density;
                    int minimumSize = Math.round(48*density);
                    int width = (parentwidth/5) - 12 *4 ;
                        if(width<minimumSize){
                            width=minimumSize;
                        }
                    LayoutParams params = new LayoutParams(width, width); // Adjust size here
                    params.setMargins(6, 8, 6, 8); // Spacing between icons
                    img.setLayoutParams(params);
                }
            });
            addView(img);
            icon[i] = img;
        }
    }

    public void setRating(float rating) {

        currentRating = rating;
        for (int i = 0; i < iconCount; i++) {


            if (i < rating) {
                icon[i].setImageDrawable(ratingType.equals(RatingType.emoji)?
             filledIconList[i]
            : fullIcon);
                if(themeColors!=null){
                    //		--color21 - #ffc100 Rating Button selected bg
                    icon[i].setColorFilter(Color.parseColor(themeColors.getColor21()));
                }
            } else {
                icon[i].setImageDrawable(emptyIcon);
                icon[i].setColorFilter(Color.parseColor(themeColors.getColor19()));
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

