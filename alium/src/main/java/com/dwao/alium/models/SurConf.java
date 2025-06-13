package com.dwao.alium.models;

import java.util.ArrayList;
import java.util.List;

public class SurConf {
    public SurConf(){};

    String oid="";

    @Override
    public String toString() {
        return "SurConf{" +
                "oid='" + oid + '\'' +
                ", svs=" + svs +
                '}';
    }

    public List<SurInfo> getSvs() {
        return svs;
    }

    public void setSvs(List<SurInfo> svs) {
        this.svs = svs;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    List<SurInfo> svs=new ArrayList<>();

}


