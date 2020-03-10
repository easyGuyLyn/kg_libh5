package com.regus.base.net;

import android.content.Context;

import com.regus.base.ConstantValue;
import com.regus.base.HostManager;
import com.regus.base.R;

/**
 * 403：无权限访问
 * 404：请求链接或页面找不到
 * 500：代码错误
 * 502：运维服务问题

 */

public class CustomHttpException extends RuntimeException {

    public CustomHttpException(int code) {
        throw new RuntimeException(getCustomHttpException(code));
    }

    public static String getCustomHttpException(int code) {
        Context context = HostManager.getInstance().getContext();
        switch (code) {
            case 403:
                return context.getString(R.string.http_code_400);
            case 404:
                return context.getString(R.string.http_code_404);
            case 500:
                return context.getString(R.string.http_code_500);
                default:
        }
        return context.getString(R.string.http_code_default, code);
    }


}
