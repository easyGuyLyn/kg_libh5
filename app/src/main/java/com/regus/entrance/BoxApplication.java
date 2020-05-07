package com.regus.entrance;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.regus.base.HostManager;
import com.regus.mj.config.KGConfig;
import com.regus.mj.utils.YsdkSignUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.regus.base.net.RetrofitHelper.DEFAULT_READ_TIMEOUT_SECONDS;
import static com.regus.base.net.RetrofitHelper.DEFAULT_TIMEOUT_SECONDS;
import static com.regus.base.net.RetrofitHelper.DEFAULT_WRITE_TIMEOUT_SECONDS;
import static com.regus.entrance.BuildConfig.isWaiBao;

/**
 *
 */

public class BoxApplication extends Application {

    public static Handler handler = new Handler();


    public static String SmPostDetailUrl;


    public static boolean isOneTabMJ = false;


    //兼容 4.5版本以下 添加MultiDex分包，但未初始化的问题
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isWaiBao) {
            SmPostDetailUrl = "http://majia.132996.com:8010/AppShellService.svc/GetAppInfo";
        } else {
            SmPostDetailUrl = "http://majia.132996.com:8010/AppShellService.svc/GetAppInfo";
        }

        if (TextUtils.isEmpty(BuildConfig.h5_mj_url_1)) {
            isOneTabMJ = true;
        }


        HostManager.getInstance().init(
                this,
                handler,
                BuildConfig.h5_mj_url,
                BuildConfig.appId,
                getString(R.string.chanel),
                getString(R.string.app_name),
                getString(R.string.chanel),
                BuildConfig.APPLICATION_ID
        );


        KGConfig.UIConfig.app_icon_rec = R.mipmap.app_icon;
        KGConfig.UIConfig.app_splash_rec = R.mipmap.splash;
        KGConfig.UIConfig.app_splash_down_rec = R.mipmap.splash_down;
        KGConfig.UIConfig.app_layout_splash = R.layout.mj_regus_splash;
        KGConfig.UIConfig.app_view_root = R.id.mj_root_view;
        KGConfig.UIConfig.app_view_progressbar = R.id.mj_progressBar;
        KGConfig.UIConfig.app_view_progressnum = R.id.mj_progress_num;

        KGConfig.AppInfoConfig.app_main_path = "com.regus.entrance.activity.MaJiaActivity";
        KGConfig.AppInfoConfig.app_aid = BuildConfig.appId;
        KGConfig.AppInfoConfig.app_sid = getString(R.string.chanel);

        KGConfig.getInstance().setApplication(this);

//        if (BuildConfig.DEBUG) {
//            AVOSCloud.setDebugLogEnabled(true);
//        }


//        AVOSCloud.initialize(this
//                , "67x9AFJW4h2aT78GEWVVQGWN-gzGzoHsz"
//                , "tbeMiKA9yCmFQXtPDgb8mGsg");

//        for (ChanelStoreEnum specialSiteEnum : ChanelStoreEnum.values()) {
//        AVObject avObject = new AVObject("UpVersion");
//        avObject.put("name", getString(R.string.app_name));
//        avObject.put("url", BuildConfig.aim_url);
//        avObject.put("show", 1);
//        avObject.put("chanel", "三星");
//        avObject.saveInBackground();
//        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//失败重连
                .followSslRedirects(true)
                .followRedirects(true)
                .addInterceptor(interceptor)
                .build();

        OkHttpUtils.initClient(client);

        regesterJPush();


        //测试   appid，format，openid，openkey，pf，pfkey,ts,userip,zoneid
//        Map<String, String> map = new HashMap<>();
//        map.put("appid", "15499");
//        map.put("format", "json");
//        map.put("openid", "aa");
//        map.put("openkey", "ab");
//        map.put("pf", "ac");
//        map.put("pfkey", "ad");
//        map.put("ts", "1340880299");
//        map.put("userip", "112.90.139.30");
//        map.put("zoneid", "1");


       // YsdkSignUtil.getFinalSign("/v3/r/mpay/get_balance_m", "56abfbcd12fe46f5ad85ad9f12345678", map, "GET");


    }


    void regesterJPush() {
        if (BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }

        JPushInterface.init(this);
    }


}
