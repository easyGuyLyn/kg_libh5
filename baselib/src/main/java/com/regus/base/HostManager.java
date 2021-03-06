package com.regus.base;

import android.content.Context;
import android.os.Handler;

import com.regus.base.util.LogUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.regus.base.net.RetrofitHelper.DEFAULT_READ_TIMEOUT_SECONDS;
import static com.regus.base.net.RetrofitHelper.DEFAULT_TIMEOUT_SECONDS;
import static com.regus.base.net.RetrofitHelper.DEFAULT_WRITE_TIMEOUT_SECONDS;


public class HostManager {


    private static final String TAG = "HostManager ";

    private Context context;


    private String mAppId;
    private String mSid;

    private Handler mHandler; //主线程handler

    private String mH5MjURL;//h5的url


    private String application_id;


    private static final HostManager ourInstance = new HostManager();

    public static HostManager getInstance() {
        return ourInstance;
    }

    public String getmSid() {
        return mSid;
    }

    public void setmSid(String mSid) {
        this.mSid = mSid;
    }

    private HostManager() {
    }


    public String getAppId() {
        return mAppId;
    }


    public Context getContext() {
        return context;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public String getH5MJURl() {
        return mH5MjURL;
    }


    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public void init(
            Context context,
            Handler handler,
            String H5MJUrl,
            String appId,
            String sid,
            String app_name,
            String um_chanel,
            String application) {

        this.context = context;

        mHandler = handler;

        mH5MjURL = H5MJUrl;

        mAppId = appId;
        mSid = sid;
        application_id = application;

        initOKHttpUtils();

        loadX5();






    }


    void initOKHttpUtils() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//失败重连
                .addInterceptor(interceptor)
                .build();

        OkHttpUtils.initClient(client);
    }


    /**
     * 加载x5
     */
    void loadX5() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.e(TAG, " load x5   onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb);
    }

}
