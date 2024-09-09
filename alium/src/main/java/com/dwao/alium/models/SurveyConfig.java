package com.dwao.alium.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SurveyConfig implements Serializable {
    SurveyConfig(){}
    int active;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

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
    public  Srv srv=new Srv();

}
