package com.regus.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by LiFuPing on 2017/9/5.
 */

public class ConnectionClient {

    public static final String TAG = "OKClient";

    private static final long cacheSize = 1024 * 1024 * 20;// 缓存文件最大限制大小20M


    private static ConnectionClient mInstace;

//    private static String cacheDirectory = App.getInstance().getCacheDir().getAbsolutePath(); // 设置缓存文件路径

//    private static Cache mCache = new Cache(new File(cacheDirectory, "Response"), cacheSize);  //


    private OkHttpClient mHttpClient;

    private CacheControl noCache = new CacheControl.Builder()
            .noCache()
            .noStore()
            .build();


    private ConnectionClient() {
//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                String url = request.url().url().toString();
//                Boolean tag = null;
//                if (url != null && (url.contains("statement/detaildata") || url.contains("bet/Data"))) {
//                    String[] a = url.split("&");
//                    String t = "";
//                    if (!CheckUtil.checkArrayIsEmpty(a)) {
//                        t = a[a.length - 1];
//                    }
//                    if ("true".equals(t)) {
//                        tag = true;
//                    } else {
//                        tag = false;
//                        url = url.substring(0, url.length() - 6);
//                    }
//                }
//                if (tag == null) {
//                    request = request.newBuilder()
//                            .cacheControl(noCache)
//                            .build();
//
//                } else if (tag) {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_CACHE)
//                            .url(url)
//                            .build();
//                    Log.i(TAG, "contains request = " + request.toString());
//                } else if (!tag) {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_NETWORK)
//                            .url(url)
//                            .build();
//                    Log.i(TAG, "contains request = " + request.toString());
//                }
//
//                Response response = chain.proceed(request);
////                int maxAge = 0 * 60; // 有网络时 设置缓存超时时间为0;
//                int maxAge; // 有网络时 设置缓存超时时间为0;
//                if (tag != null && tag) {
//                    maxAge = 60 * 60 * 24;
//                    response.newBuilder()
//                            .header("Cache-Control", "public, max-age=" + maxAge)
//                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
//                            .build();
//                    Log.i(TAG, " true response = " + response.toString());
//                } else {
////                    int maxStale = 1; // 无网络时，设置超时为1天
////                    Log.i(TAG, "has maxStale=" + maxStale);
////                    response.newBuilder()
////                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
////                            .removeHeader("Pragma")
////                            .build();
////                    Log.i(TAG, "false response = " + response.toString());
//                }
//                return response;
//            }
//        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.SECONDS)
//                .cookieJar(CookieJar.NO_COOKIES)
                .cookieJar(new CookieJar() {

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                        if (cookies != null) {
////                            cookieStore = cookies;
//                            for (Cookie cookie : cookies) {
////                                LogUtil.getLogger().e(MessageFormat.format("在Connect中保存Cookie{0} ", ObjectInfoUtil.toString(cookie)));
//                                cookieStore.put(cookie.name(), cookie);
//                            }
//                        }
                        CookieUtil.saveCookie(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        LogUtil.getLogger().e("请求Cookies的页面："+url.encodedPath());
//                        Iterator<String> Keys = cookieStore.keySet().iterator();
//                        ArrayLists<Cookie> mCookie = new ArrayList<>();
//                        while (Keys.hasNext()) {
//                            String name = Keys.next();
//                            mCookie.add(mCookies.get(name));
//                        }
//
//
//                        if ("/zh-cn/BetslipService/Add".equals(url.encodedPath())) {
//                            Cookie cookie = Cookie.parse(url, "settings=theme=betcmp");
//                            mCookie.add(cookie);
//                        }
//                        return cookies;
                        List<Cookie> cookies = CookieUtil.loadForRequest(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .addNetworkInterceptor(interceptor)
//                .cache(mCache)
                .followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
                .followSslRedirects(false)
                .build();
    }

    public static final ConnectionClient getInstace() {
        if (mInstace == null) {
            synchronized (ConnectionClient.class) {
                if (mInstace == null) {
                    mInstace = new ConnectionClient();
                }
            }
        }
        return mInstace;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }


}
