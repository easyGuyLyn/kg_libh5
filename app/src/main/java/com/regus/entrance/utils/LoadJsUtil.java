package com.regus.entrance.utils;

import android.util.Log;

import com.regus.base.HostManager;
import com.regus.base.R;
import com.regus.base.util.XLogUtil;
import com.regus.entrance.BuildConfig;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

public class LoadJsUtil {


    public static void loadFirstJs(WebView webView) {

        String js = "";

        if (BuildConfig.h5_mj_url.equals("https://3g.d1xz.net/sx/")
                || BuildConfig.h5_mj_url.equals("https://3g.d1xz.net/sx/shu/")) {
            js = getShengXiaoJs1();
        } else if (BuildConfig.h5_mj_url.equals("https://shengxiao.911cha.com/")) {
            js = getShengXiaoJs2();
        } else if (BuildConfig.h5_mj_url.equals("https://m.buyiju.com/cha/shengxiao.php")) {
            js = getShengXiaoJs3();
        } else if (BuildConfig.h5_mj_url.equals("https://m.xingzuo360.cn/")) {
            js = getShengXiaoJs4();
        } else if (BuildConfig.h5_mj_url.equals("http://m.xzhome.com.cn/")
                || BuildConfig.h5_mj_url.equals("http://m.xzhome.com.cn/fortune/")) {
            js = getShengXiaoJs5();
        } else if (BuildConfig.h5_mj_url.equals("http://m.isyacht.com/news/")) {
            js = getXyft();
        }


        XLogUtil.logE("load js " + js);
        webView.evaluateJavascript(js, new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

    }


    public static void loadSecond(WebView webView) {

        String js = "";

        if (BuildConfig.h5_mj_url_1.equals("https://m.xingzuo360.cn/shiershengxiao/zishu/")) {
            js = getShengXiaoJs4();
            StringBuilder stringBuilder = new StringBuilder();//footer
            stringBuilder.append("javascript:if (document.getElementsByClassName('m_column_title bgf9')[0]){document.getElementsByClassName('m_column_title bgf9')[0].style.display ='none';}void(0);");
            stringBuilder.append("javascript:if (document.getElementsByClassName('m_class_col3 bgf9')[0]){document.getElementsByClassName('m_class_col3 bgf9')[0].style.display ='none';}void(0);");
            js = js + stringBuilder.toString();
        } else if (BuildConfig.h5_mj_url_1.equals("https://3g.d1xz.net/rili/")) {
            js = getShengXiaoJs1();
        } else if (BuildConfig.h5_mj_url_1.equals("http://m.xzhome.com.cn/fortune/")) {
            js = getShengXiaoJs5();
        }else if (BuildConfig.h5_mj_url_1.equals("http://m.isyacht.com/brands/")) {
            js = getXyft();
        }



        XLogUtil.logE("load js " + js);
        webView.evaluateJavascript(js, new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });
    }


    public static void loadThirdJs(WebView webView) {

        String js = null;
        if (BuildConfig.h5_mj_url_2.equals("https://3g.d1xz.net/about/sitemap.aspx")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getShengXiaoJs1());

            js = stringBuilder.toString();
        } else if (BuildConfig.h5_mj_url_2.equals("https://m.buyiju.com/site/sitemap.html")) {
            js = getShengXiaoJs3();
        } else if (BuildConfig.h5_mj_url_2.equals("http://m.isyacht.com/yachtclub/")) {
            js = getXyft();
        }


        XLogUtil.logE("load js " + js);
        webView.evaluateJavascript(js, new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

    }


    public static void loadFourthJs(WebView webView) {
        String js = null;

        if (BuildConfig.h5_mj_url_3.equals("https://m.xingzuo360.cn/zhongguominsu/")
                || BuildConfig.h5_mj_url_3.equals("https://m.xingzuo360.cn/zongjiaoxinyang/")) {
            js = getShengXiaoJs4();
            StringBuilder stringBuilder = new StringBuilder();//footer
            stringBuilder.append("javascript:if (document.getElementsByClassName('m_column_title bgf9')[0]){document.getElementsByClassName('m_column_title bgf9')[0].style.display ='none';}void(0);");
            stringBuilder.append("javascript:if (document.getElementsByClassName('m_class_col3 bgf9')[0]){document.getElementsByClassName('m_class_col3 bgf9')[0].style.display ='none';}void(0);");
            js = js + stringBuilder.toString();
        }else if(BuildConfig.h5_mj_url_3.equals("http://m.isyacht.com/gouting/")){
            js = getXyft();
        }


        XLogUtil.logE("load js " + js);
        webView.evaluateJavascript(js, new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

    }


    /**
     * 开奖类js模板
     * <p>
     * @return
     */

    public static String getKaiJiangJs(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("javascript:loadjs('");
        stringBuilder.append(HostManager.getInstance().getContext().getResources().getString(R.string.app_name).trim());
        stringBuilder.append("','");
        stringBuilder.append(HostManager.getInstance().getContext().getResources().getString(R.string.urlBanner1).trim());
        stringBuilder.append("','");
        stringBuilder.append(HostManager.getInstance().getContext().getResources().getString(R.string.urlColor).trim());
        stringBuilder.append("','");
        stringBuilder.append(HostManager.getInstance().getContext().getResources().getString(R.string.urlBody).trim());
        stringBuilder.append("','");
        stringBuilder.append(HostManager.getInstance().getContext().getResources().getString(R.string.urlBanner2).trim());
        stringBuilder.append("')");


        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();

    }


    /**
     * <p>
     *
     * @return
     */

    public static String getShengXiaoJs0(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:if (document.getElementsByClassName('login_logo')[0]){document.getElementsByClassName('login_logo')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('logo')[0]){document.getElementsByClassName('logo')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('qq')[0]){document.getElementsByClassName('qq')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('sina')[0]){document.getElementsByClassName('sina')[0].style.display ='none';}void(0);");

        //bdsharebuttonbox
        stringBuilder.append("javascript:if (document.getElementsByClassName('bdsharebuttonbox')[0]){document.getElementsByClassName('bdsharebuttonbox')[0].style.display ='none';}void(0);");

        //artool clear swiper-wrapper
        stringBuilder.append("javascript:if (document.getElementsByClassName('artool clear swiper-wrapper')[0]){document.getElementsByClassName('artool clear swiper-wrapper')[0].style.display ='none';}void(0);");

        //copyright
        stringBuilder.append("javascript:if (document.getElementsByClassName('copyright')[0]){document.getElementsByClassName('copyright')[0].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('login')[0]){document.getElementsByClassName('login')[0].style.display ='none';}void(0);");


        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();
    }


    /**
     * 生肖类js模板1
     *
     * @return
     */

    private static String getShengXiaoJs1() {
        String appName = HostManager.getInstance().getContext().getResources().getString(R.string.app_name);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_h_home')[0]){document.getElementsByClassName('public_h_home')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_h_menu')[0]){document.getElementsByClassName('public_h_menu')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_hot_test')[0]){document.getElementsByClassName('public_hot_test')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_footer_servers')[0]){document.getElementsByClassName('public_footer_servers')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_test_fixed')[0]){document.getElementsByClassName('public_test_fixed')[0].style.display ='none';}void(0);");


        stringBuilder.append("javascript:if (document.getElementsByClassName('zt_title_block')[0]){document.getElementsByClassName('zt_title_block')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('zt_title_block')[1]){document.getElementsByClassName('zt_title_block')[1].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_ffsm_list')[0]){document.getElementsByClassName('public_ffsm_list')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('zt_recommend_list')[0]){document.getElementsByClassName('zt_recommend_list')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_bt_more')[0]){document.getElementsByClassName('public_bt_more')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_bt_more')[1]){document.getElementsByClassName('public_bt_more')[1].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_bt_more')[2]){document.getElementsByClassName('public_bt_more')[2].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_bt_more')[3]){document.getElementsByClassName('public_bt_more')[3].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('public_col_4')[0]){document.getElementsByClassName('public_col_4')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('sx_tit')[0]){document.getElementsByClassName('sx_tit')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_route')[0]){document.getElementsByClassName('public_route')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_footer')[0]){document.getElementsByClassName('public_footer')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_h_logo')[0]){document.getElementsByClassName('public_h_logo')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_h_more')[0]){document.getElementsByClassName('public_h_more')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_h_txt')[0]){document.getElementsByClassName('public_h_txt')[0].text ='" + appName + "';}void(0);");

        return stringBuilder.toString();
    }


    /**
     * 生肖类js模板2
     *
     * @return
     */

    private static String getShengXiaoJs2() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:if (document.getElementsByClassName('pop inlink')[0]){document.getElementsByClassName('pop inlink')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('dingdan right')[0]){document.getElementsByClassName('dingdan right')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('header ftop')[0]){document.getElementsByClassName('header ftop')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('hl_head')[0]){document.getElementsByClassName('hl_head')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('foot')[0]){document.getElementsByClassName('foot')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('footer center')[0]){document.getElementsByClassName('footer center')[0].style.display ='none';}void(0);");


        stringBuilder.append("javascript:if (document.getElementsByClassName('cont bb center')[0]){document.getElementsByClassName('cont bb center')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('plr center')[0]){document.getElementsByClassName('plr center')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('plr pb')[0]){document.getElementsByClassName('plr pb')[0].style.display ='none';}void(0);");


        stringBuilder.append("javascript:if (document.getElementsByClassName('cont bb green center')[0]){document.getElementsByClassName('cont bb green center')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('txt4 center')[0]){document.getElementsByClassName('txt4 center')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('txt2 center bt')[0]){document.getElementsByClassName('txt2 center bt')[0].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('hl_mod hl_zhouyi hl_scrol')[0]){document.getElementsByClassName('hl_mod hl_zhouyi hl_scrol')[0].style.display ='none';}void(0);");


        return stringBuilder.toString();
    }


    /**
     * 生肖类js模板3
     *
     * @return
     */

    private static String getShengXiaoJs3() {
        String appName = HostManager.getInstance().getContext().getResources().getString(R.string.app_name);

        StringBuilder stringBuilder = new StringBuilder();//footer
        stringBuilder.append("javascript:if (document.getElementsByClassName('logo_m')[0]){document.getElementsByClassName('logo_m')[0].style.display ='none';}void(0);");
        //place
        stringBuilder.append("javascript:if (document.getElementsByClassName('place')[0]){document.getElementsByClassName('place')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('footer')[0]){document.getElementsByClassName('footer')[0].style.display ='none';}void(0);");


        return stringBuilder.toString();
    }


    /**
     * 生肖类js模板4
     *
     * @return
     */

    private static String getShengXiaoJs4() {
        String appName = HostManager.getInstance().getContext().getResources().getString(R.string.app_name);

        StringBuilder stringBuilder = new StringBuilder();//footer

        stringBuilder.append("javascript:if (document.getElementsByClassName('m_top_fixed')[0]){document.getElementsByClassName('m_top_fixed')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('m_route_mod bgf9 ')[0]){document.getElementsByClassName('m_route_mod bgf9 ')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('m_channel_tipimg')[0]){document.getElementsByClassName('m_channel_tipimg')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('m_footer_mod bgf9 mt10')[0]){document.getElementsByClassName('m_footer_mod bgf9 mt10')[0].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('public_header')[0]){document.getElementsByClassName('public_header')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_hot_test')[0]){document.getElementsByClassName('public_hot_test')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_footer_servers')[0]){document.getElementsByClassName('public_footer_servers')[0].style.display ='none';}void(0);");

        //info_box     public_agreement     public_btn_s    tac_words J_testFixedShow   public_test_fixed  screen screen_7

        stringBuilder.append("javascript:if (document.getElementsByClassName('public_agreement')[0]){document.getElementsByClassName('public_agreement')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_btn_s')[0]){document.getElementsByClassName('public_btn_s')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('tac_words J_testFixedShow')[0]){document.getElementsByClassName('tac_words J_testFixedShow')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('public_test_fixed')[0]){document.getElementsByClassName('public_test_fixed')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('screen screen_7')[0]){document.getElementsByClassName('screen screen_70')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('comm_ul')[0]){document.getElementsByClassName('comm_ul')[0].style.display ='none';}void(0);");
        // J_ajaxForm  J_ajaxForm J_testFixedTop  J_ajaxForm J_testFixedShow   index_form  m_myOrder

        stringBuilder.append("javascript:if (document.getElementsByClassName('index_form')[0]){document.getElementsByClassName('index_form')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('J_ajaxForm')[0]){document.getElementsByClassName('J_ajaxForm')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('J_ajaxForm J_testFixedTop')[0]){document.getElementsByClassName('J_ajaxForm J_testFixedTop')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('J_ajaxForm J_testFixedShow')[0]){document.getElementsByClassName('J_ajaxForm J_testFixedShow')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('m_order_fixed')[0]){document.getElementsByClassName('m_order_fixed')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('J_ajaxForm J_testFixedTop')[0]){document.getElementsByClassName('J_ajaxForm J_testFixedTop')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('m_myOrder')[0]){document.getElementsByClassName('m_myOrder')[0].style.display ='none';}void(0);");

        //index_firend mt10
        stringBuilder.append("javascript:if (document.getElementsByClassName('index_firend mt10')[0]){document.getElementsByClassName('index_firend mt10')[0].style.display ='none';}void(0);");


        return stringBuilder.toString();
    }


    /**
     * 生肖类js模板5
     *
     * @return
     */

    private static String getShengXiaoJs5() {
        String appName = HostManager.getInstance().getContext().getResources().getString(R.string.app_name);

        StringBuilder stringBuilder = new StringBuilder();//footer
        stringBuilder.append("javascript:if (document.getElementsByClassName('h_module')[0]){document.getElementsByClassName('h_module')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('f_module')[0]){document.getElementsByClassName('f_module')[0].style.display ='none';}void(0);");

        //footer
        stringBuilder.append("javascript:if (document.getElementsByClassName('footer')[0]){document.getElementsByClassName('footer')[0].style.display ='none';}void(0);");

        //fubiao-dialog
        stringBuilder.append("javascript:if (document.getElementsByClassName('fubiao-dialog')[0]){document.getElementsByClassName('fubiao-dialog')[0].style.display ='none';}void(0);");

        //ta-img
        stringBuilder.append("javascript:if (document.getElementsByClassName('ta-img')[0]){document.getElementsByClassName('ta-img')[0].style.display ='none';}void(0);");


        return stringBuilder.toString();
    }

    /**
     * 幸运飞艇模板
     * <p>
     *
     * @return
     */

    public static String getXyft() {


        StringBuilder stringBuilder = new StringBuilder();//footer
        //searchBox
        stringBuilder.append("javascript:if (document.getElementsByClassName('header clearfix')[0]){document.getElementsByClassName('header clearfix')[0].style.display ='none';}void(0);");
        stringBuilder.append("javascript:if (document.getElementsByClassName('footer marT20')[0]){document.getElementsByClassName('footer marT20')[0].style.display ='none';}void(0);");


        return stringBuilder.toString();


    }


    /**
     * 捕鱼模板
     * <p>
     *
     * @return
     */

    public static String getGame1(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();

        //section
        stringBuilder.append("javascript:if (document.getElementsByTagName('section')[0]){document.getElementsByTagName('section')[0].style.display ='none';}void(0);");
        //searchBox
        stringBuilder.append("javascript:if (document.getElementsByClassName('searchBox')[0]){document.getElementsByClassName('searchBox')[0].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('footer')[0]){document.getElementsByClassName('footer')[0].style.display ='none';}void(0);");

        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();

    }


    /**
     * 信用贷款模板
     * <p>
     *
     * @return
     */

    public static String getGame2(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("javascript:if (document.getElementsByClassName('nheader')[0]){document.getElementsByClassName('nheader')[0].style.display ='none';}void(0);");

        stringBuilder.append("javascript:if (document.getElementsByClassName('btn')[0]){document.getElementsByClassName('btn')[0].style.display ='none';}void(0);");


        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();

    }


    /**
     * 金花模板
     * <p>
     *
     * @return
     */

    public static String getGame3(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();

        //hua mbhd

        stringBuilder.append("javascript:if (document.getElementsByClassName('hua mbhd')[0]){document.getElementsByClassName('hua mbhd')[0].style.display ='none';}void(0);");

        //footer

        stringBuilder.append("javascript:if (document.getElementsByClassName('footer')[0]){document.getElementsByClassName('footer')[0].style.display ='none';}void(0);");


        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();

    }


    //商城

    public static String getGame4(WebView webView) {

        StringBuilder stringBuilder = new StringBuilder();

        //hua mbhd

        stringBuilder.append("javascript:if (document.getElementsByClassName('top logo')[0]){document.getElementsByClassName('top logo')[0].style.display ='none';}void(0);");


        XLogUtil.logE("load js " + stringBuilder.toString());
        webView.evaluateJavascript(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.e("====js result====", str);
            }
        });

        return stringBuilder.toString();

    }


}
