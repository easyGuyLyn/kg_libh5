package com.regus.mj.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class YsdkSignUtil {


    public static String getFinalSign(String burl, String appKey, Map<String, String> param, String reqType) {

        String result = "";

        try {
            String enCodeBurl = URLEncoder.encode(burl, "UTF-8");

            String enCodecontent = URLEncoder.encode(formatUrlParam(param, "UTF-8", false), "UTF-8");

            String enCodeOrignal = reqType + "&" + enCodeBurl + "&" + enCodecontent;

            byte[] hmacResult = hmacSHA1Encrypt(enCodeOrignal, appKey + "&", "UTF-8");

            String sign = new String(Base64.encode(hmacResult, Base64.DEFAULT));

            result = URLEncoder.encode(sign.trim(), "UTF-8");


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sign", " " + e.getLocalizedMessage());
        }

        return result;

    }


    /**
     * @param param   参数
     * @param encode  编码
     * @param isLower 是否小写
     * @return
     */
    private static String formatUrlParam(Map<String, String> param, String encode, boolean isLower) {
        String params = "";
        Map<String, String> map = param;

        try {
            List<Map.Entry<String, String>> itmes = new ArrayList<Map.Entry<String, String>>(map.entrySet());

            //对所有传入的参数按照字段名从小到大排序
            //Collections.sort(items); 默认正序
            //可通过实现Comparator接口的compare方法来完成自定义排序
            Collections.sort(itmes, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    // TODO Auto-generated method stub
                    return (o1.getKey().toString().compareTo(o2.getKey()));
                }
            });

            //构造URL 键值对的形式
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> item : itmes) {
                if (!TextUtils.isEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    // val = URLEncoder.encode(val, encode);
                    if (isLower) {
                        sb.append(key.toLowerCase() + "=" + val);
                    } else {
                        sb.append(key + "=" + val);
                    }
                    sb.append("&");
                }
            }

            params = sb.toString();
            if (!params.isEmpty()) {
                params = params.substring(0, params.length() - 1);
            }
        } catch (Exception e) {
            return "";
        }
        return params;
    }


    private static final String MAC_NAME = "HmacSHA1";

    private static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey, String encode) throws Exception {
        byte[] data = encryptKey.getBytes(encode);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(encode);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }


}
