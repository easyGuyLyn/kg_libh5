package com.regus.mj.view;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public abstract class BaseDialogFragment extends DialogFragment {
    //宽高比例 最高1 最低0
    protected double intScreenWProportion = 1; //宽比例
    protected double intScreenHProportion = 1; //高比例
    protected boolean mIsOutCanback = false; //是否点击 dialog外的地方dismiss
    protected boolean mIsKeyCanback = false; //是否点击 点击物理返回键可以取消


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected abstract int getViewId();

    protected abstract void initViews(View view);

    protected abstract void initData();

    public static void fullScreen(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {

                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        fullScreen(window);
                    }
                });

            }
        }


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(getViewId(), container, false);

        initViews(view);
        initData();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    getDialog().setCanceledOnTouchOutside(mIsKeyCanback);//键盘点击时是否可以取消--不需要设置了
                    return !mIsKeyCanback;//return true 不往上传递则关闭不了，默认是可以取消，即return false
                } else {
                    return false;
                }
            }
        });

        return view;
    }

    public void settingDialog() {
        Window mWindow = this.getDialog().getWindow();
        //状态栏
        //   mWindow.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //退出,进入动画
//        if (AnimationsStyle != -1) {
//            mWindow.setWindowAnimations(AnimationsStyle);
//        }
        //清理背景变暗
        //mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(mIsOutCanback);
        //是否可以取消,会影响上面那条属性
        setCancelable(false);
        //window外可以点击,不拦截窗口外的事件
        //mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        //设置背景颜色,只有设置了这个属性,宽度才能全屏MATCH_PARENT
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final Display defaultDisplay = getActivity().getWindow().getWindowManager()
                .getDefaultDisplay();
        int intScreenH = defaultDisplay.getHeight();
        int intScreenW = defaultDisplay.getWidth();

        WindowManager.LayoutParams mWindowAttributes = mWindow.getAttributes();
        if (intScreenWProportion < 0 || intScreenWProportion > 1) {
            mWindowAttributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            mWindowAttributes.width = (int) (intScreenW * intScreenWProportion);
        }
        if (intScreenHProportion < 0 || intScreenHProportion > 1) {
            mWindowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            mWindowAttributes.height = (int) (intScreenH * intScreenHProportion);
        }

        //gravity
        mWindowAttributes.gravity = Gravity.CENTER;
        mWindow.setAttributes(mWindowAttributes);
    }

    @Override
    public void onStart() {
        super.onStart();
        settingDialog();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("web", "横屏");
            // 横屏
            settingDialog();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("web", "竖屏");
            // 竖屏
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("Main", "b  onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Main", " b onPause");
    }


    @Override
    public void onDestroyView() {
        Log.e("Main", "onDestroyView");
        DialogFramentManager.getInstance().removeDialog(this);
        super.onDestroyView();
    }


}
