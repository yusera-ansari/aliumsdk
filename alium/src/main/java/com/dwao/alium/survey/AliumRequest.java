package com.dwao.alium.survey;

import com.dwao.alium.models.TriggerRequest;

public class AliumRequest {
    Request type;
    TriggerRequest request;

    String screenName;
    public enum Request{
        STOP, TRIGGER
    }
    public AliumRequest( TriggerRequest request){
        this.request=request;
        this.type=Request.TRIGGER;
    }
    AliumRequest( String screenName){
        this.type=Request.STOP;
        this.screenName=screenName;
    }
}
