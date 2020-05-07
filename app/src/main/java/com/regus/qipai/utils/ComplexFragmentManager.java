package com.regus.qipai.utils;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.hwangjr.rxbus.RxBus;
import com.regus.qipai.R;

import java.util.TreeSet;

/**
 * Created by archar on 2018.
 */
public class ComplexFragmentManager {

    public static final String TOP_FRGMENT = "top_fragment";

    private static ComplexFragmentManager mInstance = new ComplexFragmentManager();

    public static ComplexFragmentManager getInstance() {
        return mInstance;
    }

    private TreeSet<String> mStringTreeSet = new TreeSet<>();//保存的tag

    private Handler mHandler = new Handler();

    private SwichFragmentRunnable mSwichFragmentRunnable;
    private FragmentManager mFragmentManager;

    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    //获取fragment
    public Fragment getFragment(Class<? extends Fragment> clazz) {
        if (clazz == null) {
            return null;
        }
        String tag = clazz.getSimpleName();
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            synchronized (ComplexFragmentManager.class) {
                try {
                    fragment = clazz.newInstance();
                    mStringTreeSet.add(tag);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return fragment;
    }

    //总清理
    public void clear() {
        clearAllFragmentQuote();
    }

    //清除所有fragment被栈的引用
    //再清除map对fragment的引用  很快就会释放掉所有的fragment
    private void clearAllFragmentQuote() {
        if (mFragmentManager == null) return;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (String tag : mStringTreeSet) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
        mStringTreeSet.clear();
        mFragmentManager = null;//不然会造成整个activity泄露，因为是传进来的
        mSwichFragmentRunnable = null;
    }


    /**
     * 选中一个目标fragment   展现。
     */
    public void switchFragment(FragmentManager fragmentManager, Class<? extends Fragment> clazz) {
        setFragmentManager(fragmentManager);
        if (mSwichFragmentRunnable == null) {
            mSwichFragmentRunnable = new SwichFragmentRunnable();
        }
        mHandler.removeCallbacks(mSwichFragmentRunnable);
        mSwichFragmentRunnable.setClazz(clazz);
        mHandler.postDelayed(mSwichFragmentRunnable, 50);
    }


    /**
     * 要切到相应fragment的意图
     */

    private class SwichFragmentRunnable implements Runnable {

        Class<? extends Fragment> clazz;


        public void setClazz(Class<? extends Fragment> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void run() {
            Fragment toFragment = getFragment(clazz);
            if (toFragment == null) return;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //   transaction.setCustomAnimations(R.anim.slide_in_bottom_b, R.anim.slide_out_top_b);
            //防止极限情况下的  会有fragment hide()不掉,导致fragment重叠的情况  (比如activity的异常重启;fragment的被系统异常回收)
            for (String tag : mStringTreeSet) {
                if (!tag.equals(clazz.getSimpleName())) {
                    Fragment hideFragment = mFragmentManager.findFragmentByTag(tag);
                    if (hideFragment != null) {
                        transaction.hide(hideFragment);
                    }
                }
            }
            if (!toFragment.isAdded()) {
                transaction.add(R.id.fragment_content, toFragment, clazz.getSimpleName());
            } else {
                transaction.show(toFragment);
            }
            transaction.commitAllowingStateLoss();
            RxBus.get().post(TOP_FRGMENT, toFragment.getId() + "");
        }
    }
}
