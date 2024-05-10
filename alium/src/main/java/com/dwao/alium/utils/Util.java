package com.dwao.alium.utils;

import static java.lang.Math.random;

import android.view.View;

import java.util.Date;
import java.util.Random;

public class Util {
    static long OFFSET=100000000L;
    public static String generateCustomerId(){

        long ct=new Date().getTime()% OFFSET;
        long rn=Math.abs(new Random().nextLong()) % OFFSET;
        return String.valueOf((ct+rn));
    }
    public static void setCtaEnabled(View Cta, boolean enabled){

        if(enabled){
            Cta.setEnabled(true);
            Cta.setAlpha(1f);
        }else {
            Cta.setEnabled(false);
            Cta.setAlpha(0.5f);
        }
    }
}
