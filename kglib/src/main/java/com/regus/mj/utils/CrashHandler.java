package com.regus.mj.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";


    private String host;

    //设备相关

    private String macAddress = "";
    private String phoneNum = "";
    private String ip = "";
    private String sysInfo = "";

    private String aid = "";
    private String sid = "";


    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void setData(String host, String macAddress, String phoneNum, String ip, String sysInfo, String aid, String sid) {
        this.host = host;
        this.macAddress = macAddress;
        this.phoneNum = phoneNum;
        this.ip = ip;
        this.sysInfo = sysInfo;
        this.aid = aid;
        this.sid = sid;

        Log.e("regus", " crash SetData macAddress: " + macAddress + " phoneNum: " + phoneNum + " ip: " + ip + " sysInfo: " + sysInfo);
    }


    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {


        handleException(ex);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);


    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);

            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);


        //上传 crash

        new Thread(new CrashHandler.PostCrashInfoRunnable(sb.toString())).start();

        return null;
    }


    private class PostCrashInfoRunnable implements Runnable {


        String LogContent;

        public PostCrashInfoRunnable(String logContent) {
            LogContent = logContent;
        }


        @Override

        public void run() {


            String rootUrl = "http://" + host + "/AppShellService.svc/AddCrashLog";

            Log.e("regus_", "请求的crash 上传接口 " + rootUrl);


            try {

                JSONObject body = new JSONObject();
                body.put("Mac", macAddress);
                body.put("Ip", ip);
                body.put("OsVersion", sysInfo);
                body.put("Mobile", phoneNum);
                body.put("AppId", aid);
                body.put("StoreId", sid);
                body.put("LogContent", LogContent);

                Log.e("regus_", "闪退文本格式: " + LogContent);

                URL urll = new URL(rootUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                // 设置contentType
                urlConnection.setRequestProperty("Content-Type", "application/json");
                // 设置允许输出
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();


                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                String content = String.valueOf(body);
                os.writeBytes(content);
                os.flush();
                os.close();


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
                    Log.e("regus_crash接口返回", jsonStr + "");

                    JSONObject responseJson = new JSONObject(jsonStr.replace("\\", ""));

                    if (responseJson.has("Status") && responseJson.has("Data")) {

                        if (responseJson.getBoolean("Data")) {
                            Log.e("regus ", "闪退上传成功");
                        }

                    }

                }
            } catch (Exception e) {
                Log.e("reugs", "crash接口 " + e.getLocalizedMessage());
            }

        }


    }


}