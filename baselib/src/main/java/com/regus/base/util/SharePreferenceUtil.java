package com.regus.base.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 */

public class SharePreferenceUtil {


    /**
     * 保存　if   first install
     */
    public static void saveIsFirstInstall(Context context, boolean isFirstInstall) {
        SharedPreferences sp = context.getSharedPreferences("Box_Base_Info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("install", isFirstInstall);
        editor.apply();
    }

    /**
     * * get　if   first install
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstInstall(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Box_Base_Info", Context.MODE_PRIVATE);
        return sp.getBoolean("install", false);
    }








}
