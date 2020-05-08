package com.regus.qipai;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.regus.base.HostManager;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.regus.qipai.net.RetrofitHelper.DEFAULT_READ_TIMEOUT_SECONDS;
import static com.regus.qipai.net.RetrofitHelper.DEFAULT_TIMEOUT_SECONDS;
import static com.regus.qipai.net.RetrofitHelper.DEFAULT_WRITE_TIMEOUT_SECONDS;

/**
 *
 */

public class BoxApplication extends Application {

    public static Handler handler = new Handler();


    //兼容 4.5版本以下 添加MultiDex分包，但未初始化的问题
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        HostManager.getInstance().init(
                this,
                handler,
                getString(R.string.app_name),
                getString(R.string.chanel),
                BuildConfig.APPLICATION_ID
        );


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

        initUm();
    }


    void regesterJPush() {
        if (BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }

        JPushInterface.init(this);
    }



    void initUm(){

        //足球圈  最右app
        UMConfigure.init(this, BuildConfig.um_key, getString(R.string.chanel), UMConfigure.DEVICE_TYPE_PHONE,
                BuildConfig.um_push_key);

        UMConfigure.setLogEnabled(true);


        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("UM", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("UM", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

    }




}
