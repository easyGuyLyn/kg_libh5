package com.regus.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by owner on 2018/3/26.
 */

public class CheckUtil {

    public static <T> void checkNullThrowException(T t) {
        if (t == null) {
            throw new NullPointerException("CheckUtil's checkNullThrowException params t is null");
        }
    }

    public static void checkAndNullThrowException(Object... t) {
        if (t == null) {
            throw new NullPointerException("CheckUtil's checkAndNullThrowException params[] t is null");
        } else {
            for (Object o : t) {
                if (o == null) {
                    throw new NullPointerException("CheckUtil's checkAndNullThrowException params[] someone element object is null");
                }
            }
        }
    }

    public static <T> boolean checkListIsEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <T> boolean checkArrayIsEmpty(T[] arrays) {
        if (arrays == null || arrays.length == 0) {
            return true;
        }
        return false;
    }

    public static <K, V> boolean checkMapIsEmpty(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <T> boolean checkIteratorIsEmpty(Iterator<T> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            return true;
        }
        return false;
    }

    public static <T> boolean checkSpareArrayIsEmpty(SparseArray<T> array) {
        if (array == null || array.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean checkAllIsParamBoolean(boolean paramBoolean, Boolean[] booleans) {
        if (booleans == null) return false;
        return checkAllIsParamBoolean(paramBoolean, Arrays.asList(booleans));
    }

    public static boolean checkAllIsParamBoolean(boolean paramBoolean, List<Boolean> list) {
        if (checkListIsEmpty(list)) return false;
        for (Boolean b : list) {
            if (b != paramBoolean) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断有没有安装该apk
     *
     * @param packageName
     * @param context
     * @return
     */
    public static boolean isAvilible(String packageName, Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

}
