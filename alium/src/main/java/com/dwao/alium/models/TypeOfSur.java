package com.dwao.alium.models;

public class TypeOfSur{
    public TypeOfSur(){}

    App app;

    @Override
    public String toString() {
        return "TypeOfSur{" +
                "app=" + app +
                '}';
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
