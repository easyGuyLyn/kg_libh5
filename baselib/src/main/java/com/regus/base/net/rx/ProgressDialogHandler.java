package com.regus.base.net.rx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog pd;

    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    private void initProgressDialog() {
        if (pd == null) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
                if (((Activity) context).isDestroyed()) {
                    return;
                }
                pd = new ProgressDialog(context);
                pd.show();
            }
        }
    }

    private void dismissProgressDialog() {
        if (pd != null && context != null && pd != null && pd.isShowing()) {
            if (context instanceof Activity) {

                if (((Activity) context).isFinishing()) {
                    pd = null;
                    return;
                }
                if (((Activity) context).isDestroyed()) {
                    pd = null;
                    return;
                }

                pd.dismiss();
                pd = null;

            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

}
