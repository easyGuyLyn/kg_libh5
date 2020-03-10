package com.regus.entrance.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.regus.base.util.SingleToast;
import com.regus.entrance.BoxApplication;
import com.regus.entrance.BuildConfig;
import com.regus.base.util.LogUtils;
import com.regus.entrance.R;
import com.regus.entrance.activity.MaJiaActivity;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import static com.regus.entrance.utils.LoadJsUtil.getGame1;
import static com.regus.entrance.utils.LoadJsUtil.getGame2;
import static com.regus.entrance.utils.LoadJsUtil.getGame3;
import static com.regus.entrance.utils.LoadJsUtil.getGame4;
import static com.regus.entrance.utils.LoadJsUtil.getKaiJiangJs;
import static com.regus.entrance.utils.LoadJsUtil.getShengXiaoJs0;

public class AimWebViewFragment extends Fragment {


    public static final String URL = "url";
    private FrameLayout mFrameLayout;
    private ProgressDialog mProgressDialog;
    private WebView mWebview;

    private ImageView iv_back;
    private RelativeLayout ll_toolbar;
    private TextView tv_titile;
    private View cover;

    private ImageView finsh_cover;

    private boolean isWebViewInit;
    private String url;

    private boolean isNeedToCoverDoudizhu;
    private boolean isNeedTOCOverFinsh;

    public boolean isCanGoBack() {
        if (mWebview == null) {
            return false;
        }
        return mWebview.canGoBack();
    }


    public void backWebView() {
        mWebview.goBack();
    }


    public AimWebViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_main_content, container, false);
        mFrameLayout = view.findViewById(R.id.content);
        ll_toolbar = view.findViewById(R.id.ll_toolbar);
        tv_titile = view.findViewById(R.id.tv_titile);
        iv_back = view.findViewById(R.id.iv_back);
        cover = view.findViewById(R.id.v_cover);
        finsh_cover = view.findViewById(R.id.v_cover_fish);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load();
    }


    private void load() {


        url = ((MaJiaActivity) getActivity()).mAimUrl;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("加载中，请稍后...");
        mProgressDialog.show();

        createWebView();
        mWebview.loadUrl(url);
    }

    private void createWebView() {
        mWebview = new WebView(getActivity());
        initWebSetting();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mWebview.setLayoutParams(layoutParams);
        mFrameLayout.addView(mWebview);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                }
            }
        });
    }

    /**
     * 处理是否展现标题
     */

    private void setShowTitle() {
//


        if (!BuildConfig.is_Game) {
            if (BuildConfig.h5_mj_url.equals("http://mb03.bctx365.com/")
                    || BuildConfig.h5_mj_url.equals("http://cp.gsjyjd.com/")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/mb02_lm1.html")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/b.html")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/mb03_1.html")) {
                //彩票模板  暂时有自己的toolbar
                return;
            }
            ll_toolbar.setVisibility(View.VISIBLE);
            tv_titile.setText(R.string.app_name);
        } else {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (url.equals("https://shengxiao.911cha.com/")
                    || url.contains("https://m.buyiju.com/cha/shengxiao.php")
                    || url.contains("https://m.xingzuo360.cn/")) {
                // mRoot.setBackgroundColor(getResources().getColor(R.color.white));
                ll_toolbar.setVisibility(View.VISIBLE);
                tv_titile.setText(R.string.app_name);
            }

        }


    }


    public void initWebSetting() {
        WebSettings webSettings = mWebview.getSettings();

        //支持缩放，默认为true。
        webSettings.setSupportZoom(true);
        //调整图片至适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //设置默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //支持插件
        webSettings.setPluginsEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        //多窗口
        webSettings.supportMultipleWindows();
        //获取触摸焦点
        mWebview.requestFocusFromTouch();
        //允许访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        webSettings.setAllowContentAccess(true);
        //开启javascript
        webSettings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //提高渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        DocumentBuilderFactory.newInstance().setExpandEntityReferences(false);

        webSettings.setDomStorageEnabled(true);        //设置支持DomStorage
        //图片先不加载最后再加载
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setAppCacheEnabled(true);          // 启用缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //修改硬件加速导致页面渲染闪烁问题
        // mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebview.requestFocus();
        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        /**
         * MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的；
         * MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
         * MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格。
         **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebview.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //    mWebview.addJavascriptInterface(new InJavaScriptCommon(), "gamebox");

        //  CookieManager.getInstance().setAcceptCookie(true);
        if (BuildConfig.h5_mj_url.equals("http://fish.bbjoy.xyz/agent/rest/game/CNBYTest")) {
            mWebview.setBackgroundColor(Color.parseColor("#003cce"));
        }


        mWebview.setWebViewClient(new CommonWebViewClient());
        mWebview.setWebChromeClient(new CommonWebChromeClient());
        mWebview.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        mWebview.setDownloadListener(new MjDownloadListener());

    }

    private class MjDownloadListener implements com.tencent.smtt.sdk.DownloadListener {

        @Override
        public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimetype, long contentLength) {

            Log.e("onDurl-->", url);
            Log.e("onD userAgent->", userAgent);
            Log.e("onD contentLength->", contentLength + "");

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);

        }

    }

    private class CommonWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            setProgressBar(newProgress);
        }
    }


    private class CommonWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView webView, String s) {
            super.onLoadResource(webView, s);
            LogUtils.e("onPageLoadResource", s);
            if (BuildConfig.h5_mj_url.equals("http://mb03.bctx365.com/")
                    || BuildConfig.h5_mj_url.equals("http://cp.gsjyjd.com/")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/mb02_lm1.html")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/b.html")
                    || BuildConfig.h5_mj_url.equals("file:///android_asset/mb03_1.html")) {
                getKaiJiangJs(webView);
            } else if (BuildConfig.h5_mj_url.equals("https://m.xzw.com/")) {
                getShengXiaoJs0(webView);
            } else if (BuildConfig.h5_mj_url.equals("https://m.aidiao.com/baike/")) {
                getGame1(webView);
            } else if (BuildConfig.h5_mj_url.equals("http://m.bangrong.com/article/cid/11")) {
                getGame2(webView);
            } else if (BuildConfig.h5_mj_url.equals("https://m.52zzl.com/")) {
                getGame3(webView);
            } else if (BuildConfig.h5_mj_url.equals("https://m.6688.com/")) {
                getGame4(webView);
            }

            if (isNeedToCoverDoudizhu) {
                if (s.equals("http://res.12317wan.com/gameModule/h5/jsddz/365you/201908142110/res/import/0e/0e6cf9dcd.json")) {
                    cover.setVisibility(View.GONE);
                }
            }

            if (isNeedTOCOverFinsh) {
                if (s.contains("user/login")) {
                    BoxApplication.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finsh_cover.setVisibility(View.GONE);
                        }
                    }, 1000);
                }
            }

        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("onPageStarted", url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("onPageFinished", url);
            mWebview.setVisibility(View.VISIBLE);
            hideWebViewLoadDialog();

            if (BuildConfig.h5_mj_url.equals("http://res.12317wan.com/gameModule/h5/jsddz/365you/201908142110/index.html")) {
                isNeedToCoverDoudizhu = true;
                cover.setVisibility(View.VISIBLE);
            } else if (BuildConfig.h5_mj_url.equals("http://fish.bbjoy.xyz/agent/rest/game/CNBYTest")) {
                isNeedTOCOverFinsh = true;
                finsh_cover.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
        }


        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.cancel();
            super.onReceivedSslError(webView, sslErrorHandler, sslError);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("onPageShoudOver", url);
//            if (url.equals("http://www.google.com/")) {
//                Log.e("====google====", "国内不能访问google,拦截该url");
//                return true;
//            }
//
//
//            if (url.equals("https://shengxiao.911cha.com/")) {
//                if (url.equals("https://shengxiao.911cha.com/yuncheng.html")
//                        || !url.contains("shengxiao")) {//911生肖
//                    return true;
//                }
//            }
            if (url.startsWith("tel:")) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(url)));
                return true;
            }

            mWebview.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            switch (errorCode) {
                case ERROR_CONNECT:
                    //  mWebview.loadUrl("file:///android_asset/html/unNet.html");
                    break;
            }
        }


        /**
         * 拦截WebView网络请求（Android API < 21）
         * 只能拦截网络请求的URL，请求方法、请求内容等无法拦截
         */
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        /**
         * 拦截WebView网络请求（Android API >= 21）
         * 通过解析WebResourceRequest对象获取网络请求相关信息
         */
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            Log.e("OnWebResourceRequest  ", "url: " + request.getUrl() + " \n Method: " + request.getMethod() + "  \n Headers: " + request.getRequestHeaders().toString() + "\n");

            if (request.getUrl().toString().contains("baidu.com")) {

                WebResourceResponse response = null;
                try {
                    InputStream localCopy = getActivity().getAssets().open("icon_empty.png");
                    response = new WebResourceResponse("image/png", "UTF-8", localCopy);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            if (request.getUrl().toString().contains("recharge/order")) {

                SingleToast.showMsg("金币免费派送中~");

            }





            return super.shouldInterceptRequest(view, request);
        }
    }


    /**
     * 设置进度条
     *
     * @param progress
     */
    private void setProgressBar(int progress) {

        if (mWebview.canGoBack()) {
            iv_back.setVisibility(View.VISIBLE);
        } else {
            iv_back.setVisibility(View.GONE);
        }

        showLoadDialogProgress(progress);
    }

    private void showLoadDialogProgress(int progress) {

        if (isWebViewInit) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (mProgressDialog != null && !getActivity().isDestroyed()) {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
                mProgressDialog.setProgress(progress);
            }
        } else {
            if (mProgressDialog != null) {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
                mProgressDialog.setProgress(progress);
            }
        }
    }

    private void hideWebViewLoadDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (mProgressDialog != null && !getActivity().isDestroyed()) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } else {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
        isWebViewInit = true;
        setShowTitle();
    }


}
