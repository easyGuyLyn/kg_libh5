package com.regus.base.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XLogUtil {


    static final boolean openLog = true;

    /**
     * log d
     *
     * @param obj
     */
    public static void log(Object obj) {
        if (openLog)
            Log.d("xposed debug  ", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  [" + obj.toString() + "]");
    }


    /**
     * log red log
     *
     * @param obj
     */
    public static void logE(Object obj) {
        if (openLog)
            Log.e("xposed red log  ", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  [" + obj.toString() + "]");
    }


}
