package com.dwao.alium.models;

public class AliumRequest {
    public Request type;
    public TriggerRequest request;

    public String screenName;
      public enum Request{
        STOP, TRIGGER
    }
      public AliumRequest( TriggerRequest request){
        this.request=request;
        this.type=Request.TRIGGER;
    }
      public AliumRequest(String screenName){
        this.type=Request.STOP;
        this.screenName=screenName;
    }
}
