package com.regus.entrance.utils;

import android.app.Application;

import com.regus.entrance.BuildConfig;

import cn.jpush.android.api.JPushInterface;

public class Test extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        regesterJPush();
    }

    void regesterJPush() {
        if (BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }

        JPushInterface.init(this);
    }



}
