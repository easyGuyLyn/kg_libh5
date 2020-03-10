package com.regus.base.view.Progress;


/**
 * Created by archar on 15-4-23.
 * SpashAcitivity的线路任务UI回调器
 */
public interface LineTaskProgressListener extends LineTaskBaseListener {

    void onProgressBarChange(int current, int max);

}
