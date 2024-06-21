package com.dwao.alium.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceInfo {
    private DeviceInfo(){};
    enum DeviceType {
        PHONE,
        TABLET,
        WATCH,
        UNKNOWN;

        public String toString(){
            switch (this){
                case PHONE:
                    return "phone";

                case WATCH:
                    return "watch";
                case TABLET:
                    return "tablet";
                case UNKNOWN:
                    return "unknown";
                default:
                    return "unknown";
            }
        };
    }
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    public static String getUserAgent(Context context){
        String appId="UNKNOWN";

        try{
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            String versionName= packageInfo.versionName;;

            int versionCode=packageInfo.versionCode;
            ApplicationInfo applicationInfo=context.getPackageManager().getApplicationInfo(
                    context.getPackageName(),0
            );
            appId= context.getPackageManager().getApplicationLabel(
                    applicationInfo

            )+ " "+versionName+" "+versionCode;


//      appId=context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();

        }catch (Exception e){
            Log.d("User-Agent", e.toString());
        }
        String osVersion= "Android "+ Build.VERSION.RELEASE;
        String deviceName=Build.MODEL;
        String deviceType=getDeviceType(context).toString();
        Log.i("UA", "Android|"+deviceName+"|"+appId+"|"+osVersion+"|"+deviceType
        );

        return "Android|"+deviceName+"|"+appId+"|"+osVersion+"|"+deviceType;
    };
    private static DeviceType getDeviceType(Context context){
        if(context.getResources()==null)return DeviceType.UNKNOWN;
        if(context.getResources().getConfiguration()==null)return  DeviceType.UNKNOWN;
        int uiMode=context.getResources().getConfiguration().uiMode;
        if((uiMode & Configuration.UI_MODE_TYPE_MASK)==Configuration.UI_MODE_TYPE_WATCH){
            Log.d("isWatch-DeviceType", DeviceType.PHONE.toString());
            return DeviceType.WATCH;
        }
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        final double Max_Mobile_Inches=7.0d;

        if(isTablet(context)){
            float yinch=displayMetrics.heightPixels/ displayMetrics.ydpi;
            float xinch=displayMetrics.widthPixels/ displayMetrics.xdpi;
            Log.d("HEIGHT/WIDTH", " "+yinch+" "+xinch);
            double diagonalinch = Math. sqrt(xinch * xinch + yinch * yinch);
            if(diagonalinch<=Max_Mobile_Inches){
                Log.d("isTab-if-DeviceType", DeviceType.PHONE.toString());
                return DeviceType.PHONE;
            }else {
                Log.d("isTab-else-DeviceType", DeviceType.TABLET.toString());
                return DeviceType.TABLET;
            }
        }else {
            Log.d("DeviceType-else", DeviceType.PHONE.toString());
            return DeviceType.PHONE;
        }

    }
}
