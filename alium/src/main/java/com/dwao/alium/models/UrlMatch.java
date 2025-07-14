package com.dwao.alium.models;

public class UrlMatch{
    public UrlMatch(){}
    String u;

    @Override
    public String toString() {
        return "UrlMatch{" +
                "u='" + u + '\'' +
                '}';
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }
}
