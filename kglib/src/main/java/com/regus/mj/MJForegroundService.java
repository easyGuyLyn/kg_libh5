package com.regus.mj;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.regus.mj.config.KGConfig;
import com.regus.mj.utils.AssistUtils;


public class MJForegroundService extends Service {

    private static final int SERVICE_ID = 1;
    private int SMAll_ICON = KGConfig.UIConfig.app_icon_rec;  //应用通知小图标

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ForegroundServiceNew", "开启ForegroundService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ForegroundServiceNew", "销毁ForegroundService");
        AssistUtils.test();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //小于8.0
                //将Service设置为前台服务，可以取消通知栏消息

                Intent intent1 = null;
                try {
                    intent1 = new Intent(this, Class.forName("com.regus.mj.MJRegusActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                builder.setSmallIcon(SMAll_ICON).
//                        setContentText("应用更新中...")
//                        .setContentIntent(pendingIntent)
//                        .setContentTitle("应用通知")
//                        .setAutoCancel(false)
//                        .setWhen(System.currentTimeMillis());


                Notification notification = new Notification.Builder(this)
                        .setContentTitle("应用通知")
                        .setContentText("应用更新中...")
                        .setSmallIcon(SMAll_ICON)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(false)
                        .setContentIntent(pendingIntent)
                        .build();


                assert manager != null;
                manager.notify(0, notification);

            } else {//Android 8.0以上
                if (manager != null) {

                    Intent intent1 = null;
                    try {
                        intent1 = new Intent(this, Class.forName("com.regus.mj.MJRegusActivity"));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


                    //设置通知的重要程度
                    int importance = NotificationManager.IMPORTANCE_MAX;

                    @SuppressLint("WrongConstant")
                    NotificationChannel channel = new NotificationChannel("mjchannel", "mj", importance);
                    manager.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(this, "mjchannel")
                            .setContentTitle("应用通知")
                            .setContentText("应用更新中...")
                            .setSmallIcon(SMAll_ICON)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(false)
                            .setContentIntent(pendingIntent)
                            .build();

//                    NotificationCompat.Builder builder = new Notification.Builder(this, "mjchannel");
//                    builder.setSmallIcon(SMAll_ICON).
//                            setContentText("应用更新中...")
//                            .setContentTitle("应用通知")
//                            .setAutoCancel(false)
//                            .setContentIntent(pendingIntent)
//                            .setWhen(System.currentTimeMillis());
//
//                    Notification notification = builder.build();
                    notification.flags |= Notification.FLAG_NO_CLEAR;

                    //将Service设置为前台服务,Android 8.0 App启动不会弹出通知栏消息，退出后台会弹出通知消息
                    //Android9.0启动时候会立刻弹出通知栏消息
                    startForeground(SERVICE_ID, notification);

                }
            }
        } catch (Exception e) {
            Log.e("regus_", "MJForegroundService 前台服务异常 " + e.getLocalizedMessage());
        }

        return START_STICKY;
    }



}