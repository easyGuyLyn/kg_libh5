package com.regus.qipai.net;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regus.base.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by benson on 17-12-20.
 */

public class RetrofitHelper {
    public static final int DEFAULT_TIMEOUT_SECONDS = 15;
    public static final int DEFAULT_READ_TIMEOUT_SECONDS = 20;
    public static final int DEFAULT_WRITE_TIMEOUT_SECONDS = 20;
    private Retrofit mRetrofit;
    private static RetrofitHelper mInstance = new RetrofitHelper();

    private RetrofitHelper() {
        String baseUrl = "http://route.showapi.com/";

        OkHttpClient.Builder builder = initOkHttpBuilder();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson)) //添加Gson支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加RxJava支持
                .client(builder.build()) //关联okhttp
                .build();
    }

    private OkHttpClient.Builder initOkHttpBuilder() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//失败重连
                .addInterceptor(new LoggingInterceptor());

        //   日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        builder.addInterceptor(interceptor);  // 添加httplog
        return builder;
    }



    public static RetrofitHelper getInstance() {
        return mInstance;
    }


    /**
     * 获取服务对象   Rxjava+Retrofit建立在接口对象的基础上的
     * 泛型避免强制转换
     */
    public <T> T getService(Class<T> classz) {
        return mRetrofit.create(classz);
    }


    private class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            request = builder.build();
            return processResponse(chain.proceed(request));
        }

        //访问网络之后，处理Response(这里没有做特别处理)
        private Response processResponse(Response response) {
            int statusCode = response.code();
            if (statusCode != 200) {
                throw new CustomHttpException(statusCode);
            }
            return response;
        }


    }


}
