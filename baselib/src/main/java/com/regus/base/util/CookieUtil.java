package com.regus.base.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class CookieUtil {

    public static final String SP_NAME_COOKIE = "SP_NAME_COOKIE";
    public static final String Key_LocalCookie = "LocalCookie";

    private static final Map<String, List<Cookie>> mCookieStore = new HashMap<>();



    public static void saveCookie(HttpUrl url, List<Cookie> cookies) {
        if (!CheckUtil.checkListIsEmpty(cookies)) {
            if (url.host().equals("xj-sb-asia-manx.prdasbbwla1.com")) {
                boolean saveTag = false;
                for (Cookie c : cookies) {
                    if (c != null && c.name().contains("SessionId")) {
                        saveTag = true;
                        break;
                    }
                }
//                if (saveTag) mCookieStore.put(url.host(), cookies);
                if (saveTag) {
                    mCookieStore.put(url.host(), cookies);
                    saveSessionId(cookies);
                }
            } else {
                mCookieStore.put(url.host(), cookies);
                saveSessionId(cookies);
            }
        }
    }

    public static List<Cookie> loadForRequest(HttpUrl url) {
//                        LogUtil.getLogger().e("请求Cookies的页面："+url.encodedPath());
//                        Iterator<String> Keys = cookieStore.keySet().iterator();
//                        ArrayList<Cookie> mCookie = new ArrayList<>();
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
        List<Cookie> cookies = mCookieStore.get(url.host());
        if (cookies == null) {
            System.out.println("没加载到cookie");
        }
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

    public static String changeCookieListToString(List<Cookie> cookies) {
        if (CheckUtil.checkListIsEmpty(cookies)) return null;
        //获取需要提交的CookieStr
        StringBuilder cookieStr = new StringBuilder();
        //将Cookie数据弄成一行
        for (Cookie cookie : cookies) {
            cookieStr.append(cookie.name()).append("=").append(cookie.value() + "; ");
        }
        return cookieStr.toString();
    }

    public static String getCookieString(Request request) {
        return getCookieString(request.url());
    }

    public static String getCookieString(HttpUrl url) {
        if (url == null || url.host() == null) return null;
        //从缓存中获取Cookie
        List<Cookie> cookies = loadForRequest(url);
        return changeCookieListToString(cookies);
    }

    public static String getCookieString(String url) {
        if (TextUtils.isEmpty(url)) return null;
        return getCookieString(HttpUrl.parse(url));
    }

    public static List<Cookie> getCookieList(HttpUrl url) {
        if (url == null || url.host() == null) return null;
        List<Cookie> cookies = loadForRequest(url);
        return cookies;
    }

    @Nullable
    public static List<Cookie> getCookieList(String url) {
        if (TextUtils.isEmpty(url)) return null;
        HttpUrl h = HttpUrl.parse(url);
        return getCookieList(h);
    }

    public static boolean checkSessionCookieIsExit(String host) {
        if (!TextUtils.isEmpty(host)) {
            List<Cookie> cookies = getCookieList(host);
            if (!CheckUtil.checkListIsEmpty(cookies)) {
                for (Cookie c : cookies) {
                    if (c != null && host.contains(c.domain()) && c.name().equals("ASP.NET_SessionId")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkSessionCookieIsExit(HttpUrl url) {
        if (url == null || url.host() == null)
            throw new NullPointerException("The HttpUrl not be null");
        return checkSessionCookieIsExit(url.host());
    }

    public static void deleteCookieByHost(HttpUrl url) {
        if (url == null || url.host() == null)
            throw new NullPointerException("The HttpUrl not be null");
        deleteCookieByHost(url.host());
    }

    public static void deleteCookieByHost(String host) {
        if (!TextUtils.isEmpty(host)) {
            List<Cookie> cookies = getCookieList(host);
            if (!CheckUtil.checkListIsEmpty(cookies)) {
                for (Cookie c : cookies) {
                    if (c != null && host.contains(c.domain())) {
                        //DataCacheUtil.getInstace(App.getInstance()).getSPF(c.domain()).edit().remove(c.name()).apply();
                    }
                }
            }
        }
    }

    public synchronized static void saveSessionId(List<Cookie> cookies) {
        if (CheckUtil.checkListIsEmpty(cookies)) return;
        for (Cookie c : cookies) {
            if (c.name().contains("SessionId")) {
                XLogUtil.logE("SessionId " + c.domain() + " / " + c.value());
                // LoginAndUserUtil.setSessionId(c.domain(), c.value());
            }
        }
    }

//    public synchronized static void saveCookiesByHost(HttpUrl url, List<Cookie> cookies) {
//        if (CheckUtil.checkListIsEmpty(cookies) || url == null || url.host() == null) return;
//        try {
//            Map<String, List<Cookie>> map = new HashMap<String, List<Cookie>>();
//            if (getCookieStore() != null) {
//                map = getCookieStore();
//            }
//            map.put(url.host(), cookies);
//            saveCookieStore(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public synchronized static void saveCookieStore() {
        saveCookieStore(mCookieStore);
    }

    private synchronized static void saveCookieStore(Map<String, List<Cookie>> mCookieStore) {
        if (CheckUtil.checkMapIsEmpty(mCookieStore)) return;
        Map<String, List<Cookie>> copyMap = new HashMap<>(mCookieStore);
        if (copyMap.get("xj-sb-asia-manx.prdasbbwla1.com") != null) {
            copyMap.remove("xj-sb-asia-manx.prdasbbwla1.com");
        }
//        JSONObject json = MapUtil.mapToJsonObject(copyMap);
//        if (json != null) {
//          //  DataCacheUtil.getInstace(App.getInstance()).getSPF(SP_NAME_COOKIE).edit().putString(Key_LocalCookie, json.toString()).apply();
//        }
    }

//    public synchronized static Map<String, List<Cookie>> getCookieStore() {
//        String json = DataCacheUtil.getInstace(App.getInstance()).getSPF(SP_NAME_COOKIE).getString(Key_LocalCookie, null);
//        Map<String, List<Cookie>> map = new HashMap<>();
//        try {
//            if (json != null) {
//                map = new Gson().fromJson(json, new TypeToken<Map<String, List<Cookie>>>() {
//                }.getType());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return map;
//    }
}
