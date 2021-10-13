package com.regus.mj.config;


import android.app.Application;

public class KGConfig {


    public  int app_splash_down_rec = 0;
    public  int app_layout_splash = 0;

    public  int app_view_root = 0;
    public  int app_view_progressbar = 0;
    public  int app_view_progressnum = 0;

    public  String app_aid = "";
    public  String app_sid = "";

    public int getApp_splash_down_rec() {
        return app_splash_down_rec;
    }

    public void setApp_splash_down_rec(int app_splash_down_rec) {
        this.app_splash_down_rec = app_splash_down_rec;
    }

    public int getApp_layout_splash() {
        return app_layout_splash;
    }

    public void setApp_layout_splash(int app_layout_splash) {
        this.app_layout_splash = app_layout_splash;
    }

    public int getApp_view_root() {
        return app_view_root;
    }

    public void setApp_view_root(int app_view_root) {
        this.app_view_root = app_view_root;
    }

    public int getApp_view_progressbar() {
        return app_view_progressbar;
    }

    public void setApp_view_progressbar(int app_view_progressbar) {
        this.app_view_progressbar = app_view_progressbar;
    }

    public int getApp_view_progressnum() {
        return app_view_progressnum;
    }

    public void setApp_view_progressnum(int app_view_progressnum) {
        this.app_view_progressnum = app_view_progressnum;
    }

    public String getApp_aid() {
        return app_aid;
    }

    public void setApp_aid(String app_aid) {
        this.app_aid = app_aid;
    }

    public String getApp_sid() {
        return app_sid;
    }

    public void setApp_sid(String app_sid) {
        this.app_sid = app_sid;
    }

    private static final String TAG = "KGConfig ";


    private static final KGConfig ourInstance = new KGConfig();

    public static KGConfig getInstance() {
        return ourInstance;
    }

    private Application application;


    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    private KGConfig() {
    }




}
