package com.liutianjiao.smartvideosurveillance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.HeartbeatData;
import com.liutianjiao.smartvideosurveillance.data.NotificationData;

import java.util.ArrayList;
import java.util.List;

public class HeartbeatService extends Service {
    private boolean isWork = true;
    private List<NotificationData> notificationList = new ArrayList<NotificationData>();
    private Context context = this;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isWork) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    HeartbeatData heartbeatData = new HeartbeatData(Config.WEB_ADDRESS + "userheart.php?username="
                            + Config.USER_NAME);
                    notificationList = heartbeatData.GetResult();
                    System.out.println(notificationList.size());
                    if (notificationList.size() > 0) {
                        showNotification();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWork = false;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification() {
        Notification notification;
        notification = new Notification.Builder(context)
                .setContentTitle("智能监控系统检测到异常消息")
                .setContentText("1个联系人发来1条异常消息")
                .setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
