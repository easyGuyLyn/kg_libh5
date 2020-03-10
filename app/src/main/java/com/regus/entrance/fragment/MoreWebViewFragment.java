package com.regus.entrance.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.regus.entrance.BuildConfig;
import com.regus.entrance.utils.LoadJsUtil;
import com.regus.base.util.LogUtils;
import com.regus.entrance.R;
import com.regus.entrance.utils.LoadingDialog;
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

import javax.xml.parsers.DocumentBuilderFactory;

public class MoreWebViewFragment extends Fragment {

    private FrameLayout mFrameLayout;
    private LoadingDialog mProgressDialog;
    private WebView mWebview;

    private ImageView iv_back;
    private RelativeLayout ll_toolbar;
    private TextView tv_titile;

    private boolean isWebViewInit;
    private String url = BuildConfig.h5_mj_url_2;


    public MoreWebViewFragment() {
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load();
    }


    private void load() {

        mProgressDialog = new LoadingDialog(getActivity(), "读取中..", R.mipmap.ic_dialog_loading);

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
        ll_toolbar.setVisibility(View.VISIBLE);
        tv_titile.setText(R.string.app_name);
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


        mWebview.setWebViewClient(new CommonWebViewClient());
        mWebview.setWebChromeClient(new CommonWebChromeClient());
        mWebview.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));

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
            LoadJsUtil.loadThirdJs(webView);
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
            if (url.equals("http://www.google.com/")) {
                Log.e("====google====", "国内不能访问google,拦截该url");
                return true;
            }


            if (url.equals("https://shengxiao.911cha.com/")) {
                if (!url.contains("shengxiao")) {
                    return true;
                }

                if (url.equals("https://shengxiao.911cha.com/yuncheng.html")) {
                    return true;
                }
            }


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

            Log.e("WebResourceRequest  ", "url: " + request.getUrl() + " \n Method: " + request.getMethod() + "  \n Headers: " + request.getRequestHeaders().toString() + "\n");

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
