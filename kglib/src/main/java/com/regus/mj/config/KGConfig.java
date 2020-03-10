package com.regus.mj.config;


import android.app.Application;

public class KGConfig {


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


    public static class UIConfig {

        public static int app_icon_rec = 0;
        public static int app_splash_rec = 0;
        public static int app_splash_down_rec = 0;
        public static int app_layout_splash = 0;

        public static int app_view_root = 0;
        public static int app_view_progressbar = 0;
        public static int app_view_progressnum = 0;

    }

    public static class AppInfoConfig {

        public static String app_main_path = "";

        public static String app_aid = "";
        public static String app_sid = "";

    }


    public static class ServerConfig {

        public static String welcomeUrl = "http://woaizggcdws.com:48581/shellapi/welcome";

        public static String bussnessUrl = "/AppShellService.svc";

        public static String ip = "47.103.218.210";

    }


}
