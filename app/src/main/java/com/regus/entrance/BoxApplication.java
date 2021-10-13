package com.regus.entrance;

import android.app.Application;

import com.regus.mj.config.KGConfig;

/**
 *
 */

public class BoxApplication extends Application {




    @Override
    public void onCreate() {
        super.onCreate();

        KGConfig.getInstance().app_splash_down_rec = R.mipmap.splash_down;
        KGConfig.getInstance().app_layout_splash = R.layout.mj_regus_splash;
        KGConfig.getInstance().app_view_root = R.id.mj_root_view;
        KGConfig.getInstance().app_view_progressbar = R.id.mj_progressBar;
        KGConfig.getInstance().app_view_progressnum = R.id.mj_progress_num;

        KGConfig.getInstance().app_aid = "T20210818135713029817";//appid
        KGConfig.getInstance().app_sid = "P2021052516051007259";//渠道id

        KGConfig.getInstance().setApplication(this);




    }




}
