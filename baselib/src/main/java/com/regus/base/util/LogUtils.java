package com.regus.base.util;
/**
 * Created by benson on 17-12-24.
 */

import android.util.Log;

import com.regus.base.BuildConfig;

/**
 */
public class LogUtils {
    public static boolean debug = BuildConfig.DEBUG;

    public static void d(String o, String content) {
        logger("d", o, content);
    }

    public static void e(String o, String content) {
        logger("e", o, content);
    }

    public static void i(String o, String content) {
        logger("i", o, content);
    }

    public static void w(String o, String content) {
        logger("w", o, content);
    }

    /**
     */
    private static void logger(String type, String tag, String content) {
        if (!debug) {
            return;
        }
        switch (type) {
            case "i":
                Log.i(tag + "", content + "");
                break;
            case "d":
                Log.d(tag + "", content + "");
                break;
            case "e":
                Log.e(tag + "", content + "");
                break;
            case "w":
                Log.w(tag + "", content + "");
                break;
        }
    }

}
