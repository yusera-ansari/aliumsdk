package com.dwao.alium.survey;

import org.json.JSONException;

import java.util.Date;
import java.util.UUID;

public class SurveyTracker {
    private static final String BASE_URL="https://tracker.alium.co.in/tracker?";
    public static String getUrl(String srvId, String uuid, String currentScreen,String srvOrgId,String customerId ){
            String surveyId="srvid="+srvId+"&";
            String srvpid="srvtpid=6&";
            String srvLng="srvLng=1&";
            String vstid="vstid="+uuid+"&"  ;
            String srvldid="srvldid="+uuid+"ppup"+ new Date().getTime()+"srv"+"&";
            String srvpt="srvpt="+currentScreen+"&";
            String ua= "ua=Mozilla/5.0%20(Windows%20NT%2010.0%3B%20Win64%3B%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/122.0.0.0%20Safari/537.36&";
            String ran="ran="+new Date().getTime()+"&";
            String orgId="orgId="+srvOrgId+"&";
            String cutomerData= "custSystemId=NA&custId="+customerId+"&custEmail=NA&custMobile=NA";
        return BASE_URL+surveyId+srvpid+srvLng+srvldid+srvpt+ua+vstid+ran+orgId+
                    cutomerData;

    }
}
