package com.dwao.alium.models;

//Survey info inside app config also called svs
public class SurInfo{
    public  SurInfo(){}
    String id;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SurInfo{" +
                "id='" + id + '\'' +
                ", nm='" + nm + '\'' +
                ", spath='" + spath + '\'' +
                ", tps=" + tps +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getSpath() {
        return spath;
    }

    public void setSpath(String spath) {
        this.spath = spath;
    }

    public TypeOfSur getTps() {
        return tps;
    }

    public void setTps(TypeOfSur tps) {
        this.tps = tps;
    }

    String nm;

    String spath;

    TypeOfSur tps;

}
