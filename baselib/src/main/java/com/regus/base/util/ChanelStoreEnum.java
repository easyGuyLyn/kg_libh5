package com.regus.base.util;


import android.text.TextUtils;

//特殊站点　设置引导页　不需要显示ｌｏｇｏ
public enum ChanelStoreEnum {
    app_三星应用商店(" ", "三星"),
    app_腾讯应用宝(" ", "应用宝"),

//    app_OPPO应用市场(" ", "app_OPPO应用市场"),
//    app_vivo应用商店(" ", "app_vivo应用商店"),
//    app_360手机助手(" ", "app_360手机助手"),
//    app_百度手机助手(" ", "app_百度手机助手"),
//    app_华为应用市场(" ", "app_华为应用市场"),
//    app_小米应用商店(" ", "app_小米应用商店"),
//    app_PC6(" ", "app_PC6"),
//    app_安粉网(" ", "app_安粉网"),
//    app_豌豆荚(" ", "app_豌豆荚"),
//    app_安智应用市场(" ", "app_安智应用市场"),
//    app_锤子手机应用商店(" ", "app_锤子手机应用商店"),
//    app_金立(" ", "app_金立"),
//    app_乐视助手(" ", "app_乐视助手"),
//    app_厉趣市场(" ", "app_厉趣市场"),
//    app_魅族应用商店(" ", "app_魅族应用商店"),
//    app_联想开发平台(" ", "app_联想开发平台"),
//    app_搜狗手机助手(" ", "app_搜狗手机助手"),
//    app_应用汇安卓市场(" ", "app_应用汇安卓市场")
    ;


    private String codeName;
    private String code;

    private ChanelStoreEnum(String code, String codeName) {
        this.codeName = codeName;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public static String getCodeByCodeName(String codeName) {
        String code = "";
        if (!TextUtils.isEmpty(codeName)) {
            for (ChanelStoreEnum specialSiteEnum : ChanelStoreEnum.values()) {
                if (codeName.equals(specialSiteEnum.getCodeName())) {
                    code = specialSiteEnum.code;
                    break;
                }
            }
        }
        return code;
    }
}
