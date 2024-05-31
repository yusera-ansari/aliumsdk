package com.dwao.alium.survey;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SurveyTracker {
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
            double diagonalinch = Math.sqrt(xinch * xinch + yinch * yinch);
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
    private static final String BASE_URL="https://tracker.alium.co.in/tracker?";
    private static Uri.Builder getUriBuilder(){
        return new Uri.Builder().scheme("https"
                ).authority("tracker.alium.co.in")
                .path("tracker");
    }
    private static String getUserAgent(Context context){
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


//            appId=context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();

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
    public static String getAppendableCustomerVariables(Map parameters){

        String appendableTrackString="";
        Iterator<String> keys=parameters.keySet().iterator();
        while(keys.hasNext()){
            String key= keys.next();
            String temp=key+"="+parameters.get(key);
            appendableTrackString=appendableTrackString+"&"+temp;
        }
        return appendableTrackString.replace(" ", "%20");
    }
    @NonNull
    public static String getUrl(Context context, String srvId, String uuid, String currentScreen, String srvOrgId, String customerId ){
        Uri.Builder builder=getUriBuilder();
        builder.appendQueryParameter("ua", getUserAgent(context));

        String surveyId="srvid="+srvId+"&";
            String srvpid="srvtpid=6&";
            String srvLng="srvLng=1&";
            String vstid="vstid="+uuid+"&"  ;
            String srvldid="srvldid="+uuid+"ppup"+ new Date().getTime()+"srv"+"&";
            String srvpt="srvpt="+currentScreen+"&";
//            String ua= "ua=Mozilla/5.0%20(Windows%20NT%2010.0%3B%20Win64%3B%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/122.0.0.0%20Safari/537.36&";
            String ua="ua="+getUserAgent(context).replace(" ", "%20")+"&";
        String ran="ran="+new Date().getTime()+"&";
            String orgId="orgId="+srvOrgId+"&";
            String cutomerData= "custSystemId=NA&custId="+customerId+"&custEmail=NA&custMobile=NA";
        Log.d("builder", builder.build().toString()+
                surveyId+srvpid+srvLng+srvldid+srvpt+vstid+ran+orgId+
                cutomerData);
        return builder.build().toString()+"&"+
                surveyId+srvpid+srvLng+srvldid+srvpt+vstid+ran+orgId+
                cutomerData;
//            return BASE_URL+surveyId+srvpid+srvLng+srvldid+srvpt+ua+vstid+ran+orgId+
//                    cutomerData;

    }
}
