package com.regus.mj.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.regus.mj.MJRegusDialogFragment;
import com.regus.mj.config.KGConfig;
import com.regus.mj.config.PostBean;
import com.regus.mj.view.DialogFramentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;

public class KgUtils {

    public static String[] httpHost = {"http://ieo.titiyul.com"};


    public static void thinkJump(AppCompatActivity activity){


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.e("regus_getHost ", "https://gitee.com/mannyymm1/cqcc/raw/master/README.md");

                    URL urll = new URL("https://gitee.com/mannyymm1/cqcc/raw/master/README.md");
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

                            if(jsonArray.length()>0){
                                httpHost[0] = jsonArray.getString(0);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("regus getHost", e.getLocalizedMessage() + "");

                        }
                    } else {
                        Log.e("regus getHost", "code 不是200 或304");

                    }
                } catch (Exception e) {
                    //   allHostSize = 1;
                    Log.e("regus getHost", e.getLocalizedMessage() + "");

                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String aid = KGConfig.getInstance().getApp_aid();
                        String sid = KGConfig.getInstance().getApp_sid();

                        if (TextUtils.isEmpty(aid)) {
                            return;
                        }

                        String url = httpHost[0] + "/Inbound/QueryAppConfig";

                        Log.e("regus 跳转请求url", url+"");

                        String time = System.currentTimeMillis()+"";

                        PostBean postBean = new PostBean();
                        postBean.setClientSource(0);
                        postBean.setPartnerKey("b82cc1515cd64869beefe697cce16aad");
                        postBean.setDate(time);
                        postBean.setToken("");

                        PostBean.ParamBean paramBean = new PostBean.ParamBean();
                        paramBean.setMac(AssistUtils.getMacAddress(activity));
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

                                solveLines(true, jsonStr,activity);

                            } else {
                                Log.e("regus",   "不是200");
                                solveLines(false, null,activity);
                            }
                        } catch (Exception e) {
                            Log.e("regus",   "请求开关错误 "+e.getLocalizedMessage());
                            solveLines(false, null,activity);
                        }


                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL urll = new URL("https://qnl4eqoe.api.lncld.net/1.1/classes/UpVersion/61665006ec1d407bb2450e59");
                            HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                            urlConnection.setRequestProperty("X-LC-Id", "QnL4eqOeVFvxKnwF1gLDJywM-gzGzoHsz");
                            urlConnection.setRequestProperty("X-LC-Key", "8gEvCsJUQAcw2RJHpfoXknLQ");
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

                                  //  Log.e("avo", "s  " + show + " i  " + isStop);

                                    if (isStop) {
                                        if (show == 2) {
                                            DialogFramentManager.getInstance().showDialog(activity.getSupportFragmentManager(),new MJRegusDialogFragment());
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }).start();

            }
        }).start();


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


    private static volatile boolean isGetKgRepInfoAlready = false;


    /**
     * 处理几个ping 域名的请求
     */
    private static synchronized void solveLines(boolean isOK, String buJson,AppCompatActivity activity) {

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
                            Log.e("regus_", " IsLimit true");
                            return;
                        }


                        if (dataJsonObject.has("IsOpenFuse") && dataJsonObject.getBoolean("IsOpenFuse")) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFramentManager.getInstance().showDialog(activity.getSupportFragmentManager(),new MJRegusDialogFragment());
                                }
                            });

                            return;
                        }


                        if (dataJsonObject.getBoolean("IsOpenAdvert")) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFramentManager.getInstance().showDialog(activity.getSupportFragmentManager(),new MJRegusDialogFragment());
                                }
                            });
                            return;

                        }


                        if (dataJsonObject.getBoolean("IsOpenDown")) {
                            Log.e("regus_", " IsOpenDown true");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFramentManager.getInstance().showDialog(activity.getSupportFragmentManager(),new MJRegusDialogFragment());
                                }
                            });
                            return;
                        }


                        if (dataJsonObject.getBoolean("IsOpenJump")) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFramentManager.getInstance().showDialog(activity.getSupportFragmentManager(),new MJRegusDialogFragment());
                                }
                            });
                            return;
                        }

                    }else {
                        Log.e("regus_", " error 13");
                    }
                }else {
                    Log.e("regus_", " error 12");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            Log.e("regus_", " error 1");
        }

    }



}
