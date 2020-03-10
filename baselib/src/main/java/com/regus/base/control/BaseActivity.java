package com.regus.base.control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.regus.base.util.ActivityStackManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;

/**
 * activity的基本类
 */

public abstract class BaseActivity extends AppCompatActivity {
    // private ImmersionBar mImmersionBar;
    protected Context mContext;
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.getInstance().addActivity(this);
        createLayoutView();
        mContext = this;
        mBind = ButterKnife.bind(this); // 初始化ButterKnife
        initViews();
        initData();
    }

    protected abstract void createLayoutView();


    protected abstract void initViews();

    protected abstract void initData();

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
        ActivityStackManager.getInstance().removeActivity(this);
    }

}
