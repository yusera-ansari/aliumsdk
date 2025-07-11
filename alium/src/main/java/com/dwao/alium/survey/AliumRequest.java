package com.dwao.alium.survey;

import com.dwao.alium.models.TriggerRequest;
 class AliumRequest {
    Request type;
    TriggerRequest request;

    String screenName;
      enum Request{
        STOP, TRIGGER
    }
      AliumRequest( TriggerRequest request){
        this.request=request;
        this.type=Request.TRIGGER;
    }
      AliumRequest( String screenName){
        this.type=Request.STOP;
        this.screenName=screenName;
    }
}
