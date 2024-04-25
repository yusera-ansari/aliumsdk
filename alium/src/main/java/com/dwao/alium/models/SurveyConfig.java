package com.dwao.alium.models;

import com.google.gson.annotations.SerializedName;

public class SurveyConfig {
    SurveyConfig(){}
    int active;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getActstyp() {
        return actstyp;
    }

    public void setActstyp(String actstyp) {
        this.actstyp = actstyp;
    }

    String actstyp;
    String orgId="";
    String spath="";

    @Override
    public String toString() {
        return "SurveyConfig{" +
                "orgId='" + orgId + '\'' +
                ", spath='" + spath + '\'' +
                ", srv=" + srv +
                '}';
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSpath() {
        return spath;
    }

    public void setSpath(String spath) {
        this.spath = spath;
    }

    public Srv getSrv() {
        return srv;
    }

    public void setSrv(Srv srv) {
        this.srv = srv;
    }

    @SerializedName("appsrv")
      Srv srv=new Srv();

     private class Srv{
        @SerializedName("urlmatch")
        String urlMatch;
        String url="";
        int active;
        String elmsltr,interval,device,trigger, theme, cstclr1,cstclr2,cstclr3,cstclr4,cstclr5,
                uniqueidentifier;

        public String getUrlMatch() {
            return urlMatch;
        }

        @Override
        public String toString() {
            return "srv{" +
                    "urlMatch='" + urlMatch + '\'' +
                    ", url='" + url + '\'' +
                    ", active=" + active +
                    ", elmsltr='" + elmsltr + '\'' +
                    ", interval='" + interval + '\'' +
                    ", device='" + device + '\'' +
                    ", trigger='" + trigger + '\'' +
                    ", theme='" + theme + '\'' +
                    ", cstclr1='" + cstclr1 + '\'' +
                    ", cstclr2='" + cstclr2 + '\'' +
                    ", cstclr3='" + cstclr3 + '\'' +
                    ", cstclr4='" + cstclr4 + '\'' +
                    ", cstclr5='" + cstclr5 + '\'' +
                    ", uniqueidentifier='" + uniqueidentifier + '\'' +
                    ", surveyShowFrequency='" + surveyShowFrequency + '\'' +
                    ", thankYouMsg='" + thankYouMsg + '\'' +
                    '}';
        }

        public void setUrlMatch(String urlMatch) {
            this.urlMatch = urlMatch;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public String getElmsltr() {
            return elmsltr;
        }

        public void setElmsltr(String elmsltr) {
            this.elmsltr = elmsltr;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getTrigger() {
            return trigger;
        }

        public void setTrigger(String trigger) {
            this.trigger = trigger;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public String getCstclr1() {
            return cstclr1;
        }

        public void setCstclr1(String cstclr1) {
            this.cstclr1 = cstclr1;
        }

        public String getCstclr2() {
            return cstclr2;
        }

        public void setCstclr2(String cstclr2) {
            this.cstclr2 = cstclr2;
        }

        public String getCstclr3() {
            return cstclr3;
        }

        public void setCstclr3(String cstclr3) {
            this.cstclr3 = cstclr3;
        }

        public String getCstclr4() {
            return cstclr4;
        }

        public void setCstclr4(String cstclr4) {
            this.cstclr4 = cstclr4;
        }

        public String getCstclr5() {
            return cstclr5;
        }

        public void setCstclr5(String cstclr5) {
            this.cstclr5 = cstclr5;
        }

        public String getUniqueidentifier() {
            return uniqueidentifier;
        }

        public void setUniqueidentifier(String uniqueidentifier) {
            this.uniqueidentifier = uniqueidentifier;
        }

        public String getSurveyShowFrequency() {
            return surveyShowFrequency;
        }

        public void setSurveyShowFrequency(String surveyShowFrequency) {
            this.surveyShowFrequency = surveyShowFrequency;
        }

        public String getThankYouMsg() {
            return thankYouMsg;
        }

        public void setThankYouMsg(String thankYouMsg) {
            this.thankYouMsg = thankYouMsg;
        }

        @SerializedName("srvshowfrq")
        String surveyShowFrequency;
        @SerializedName("thnkMsg")
        String thankYouMsg="";

}
}
