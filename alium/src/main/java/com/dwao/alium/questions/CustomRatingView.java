package com.dwao.alium.questions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.view.ContextThemeWrapper;

import com.dwao.alium.listeners.RatingClickListener;
import com.dwao.alium.models.ThemeColors;
import com.dwao.alium.services.Logger;

public class CustomRatingView extends LinearLayout {
    private int iconCount = 5;
    private float currentRating = 0f;
    private View[] icon;
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
//        render( );


    }
    private final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int parentWidth = getWidth();
//                    if (parentWidth <= 0) return;

                    int minimumSizeDp = 38;
                    int minimumSizePx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, minimumSizeDp,
                            context.getResources().getDisplayMetrics());

                    int desiredSize = (int) (parentWidth * 0.56f / iconCount);  // use iconCount, not 5!
                    int size = Math.max(desiredSize, minimumSizePx);


                        for (int i = 0; i < iconCount; i++) {

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                            lp.setMargins(6, 8, 6, 8);
                            if(ratingType.equals(RatingType.buttons)){
                                Button btn=(Button) icon[i];
                                btn.setLayoutParams(lp);
                            }else{
                                ImageView img =(ImageView) icon[i];
                                img.setLayoutParams(lp);
                            }
                        }
                }
            };
    public void render(){
        removeAllViews();
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        if(ratingType.equals(RatingType.buttons)){
            icon = new Button[iconCount];
            for (int i = 0; i < iconCount; i++) {
                final int index = i;
                Button btn = new Button(context);
                btn.setText(String.valueOf(i + 1));
                btn.setAllCaps(false);
                btn.setOnClickListener(v -> setRating(index + 1));


                if(themeColors!=null){
                    btn.setBackgroundColor(Color.parseColor(themeColors.getColor19()));
                    btn.setTextColor(Color.parseColor(themeColors.getColor20()));
                }else{
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setTextColor(Color.BLACK);
                }

                addView(btn);
                icon[i]=btn;
            }
//            return;
        }
        else{

            RatingIconDrawable drawable = RatingIconDrawableFactory.getDrawable(this.ratingType, context);

            if (ratingType.equals(RatingType.emoji)) {
                filledIconList = drawable.getFilledIconList();
            }
            fullIcon = drawable.getFilledIcon();
            emptyIcon = drawable.getEmptyIcon();

            icon = new ImageView[iconCount];

            for (int i = 0; i < iconCount; i++) {
                final int index = i;

                ImageView img = new ImageView(context);
                img.setImageDrawable(emptyIcon);
                img.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                img.setClickable(true);
                LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(200, 200);

//            img.setLayoutParams(newParams);
                img.setAdjustViewBounds(true);                    // ← THIS IS KEY
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            img.requestLayout();
                img.setOnClickListener(v -> setRating(index + 1));

                if (themeColors != null) {
                    //		--color19 - #fff Rating Button Background Color
//		--color20 - #333 Rating Button Text Color
//		--color21 - #ffc100 Rating Button selected bg
//		--color22 - #333 Rating utton selected text color
                    img.setColorFilter(Color.parseColor(themeColors.getColor19()));
                }else{
                    img.setColorFilter(Color.WHITE);
                }

                addView(img);
                icon[i] = img;
            }
        }
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);


    }

    public CustomRatingView setRating(float rating) {
        try{

            currentRating = rating;
            for (int i = 0; i < iconCount; i++) {
                if(ratingType.equals(RatingType.buttons)){
                    Button btn = (Button) getChildAt(i);

                    if(themeColors!=null){
                        if (i+1 == rating) {

                            btn.setBackgroundColor(Color.parseColor(themeColors.getColor21()));
                            btn.setTextColor(Color.parseColor(themeColors.getColor22()));
                        }else{
                            btn.setBackgroundColor(Color.parseColor(themeColors.getColor19()));
                            btn.setTextColor(Color.parseColor(themeColors.getColor20()));
                        }
                    }else{
                        if (i+1 == rating) {

                            btn.setBackgroundColor(Color.DKGRAY);
                            btn.setTextColor(Color.WHITE);
                        }else{
                            btn.setBackgroundColor(Color.WHITE);
                            btn.setTextColor(Color.BLACK);
                        }
                    }
                }
                else{
                    ImageView icon = (ImageView) getChildAt(i);
                    if (i < rating) {

                        icon.setImageDrawable(
                                ratingType.equals(RatingType.emoji) ? filledIconList[i] : fullIcon
                        );
                        if (themeColors != null) {
                            //		--color21 - #ffc100 Rating Button selected bg
                            icon.setColorFilter(Color.parseColor(themeColors.getColor21()));
                        }else{
                            {
                                //		--color21 - #ffc100 Rating Button selected bg
                                icon.setColorFilter(Color.DKGRAY);
                            }
                        }
                    } else {

                        icon.setImageDrawable(emptyIcon);
                      if(themeColors!=null)
                      {
                          icon.setColorFilter(Color.parseColor(themeColors.getColor19()));
                      }else{
                          icon.setColorFilter(Color.WHITE);
                      }
                    }
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

