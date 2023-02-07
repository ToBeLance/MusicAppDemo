package com.kun.musicappdemo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kun.musicappdemo.MainActivity;
import com.kun.musicappdemo.MusicService;
import com.kun.musicappdemo.R;

public class MusicNotificationUtil {
    private static String NOTIFICATION_CHANNEL_ID = "notification_channel_id_02";//android 8 后要必须加入通知渠道，让用户可以自行管理通知
    private static String NOTIFICATION_CHANNEL_NAME = "Music Notification";// 显示在手机上的通知渠道名称

    public static RemoteViews smallRemoteViews,bigRemoteViews;//RemoteViews 可自定义通知样式,public,为了其他地方可修改其视图

    //一下为 smallRemoteViews， bigRemoteViews 里面的控件点击之后的发出广播需要的 actionName
    public static String SMALL_REMOTEVIEWS_CONTROL = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_CONTROL"
            , SMALL_REMOTEVIEWS_NEXT_SONG = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_NEXT_SONG"
            , SMALL_REMOTEVIEWS_CANCEL = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_CANCEL";
    public static String BIG_REMOTEVIEWS_PRE_SONG = "com.kun.musicappdemo.BIG_REMOTEVIEWS_PRE_SONG"
            , BIG_REMOTEVIEWS_CONTROL = "com.kun.musicappdemo.BIG_REMOTEVIEWS_CONTROL"
            , BIG_REMOTEVIEWS_NEXT_SONG = "com.kun.musicappdemo.BIG_REMOTEVIEWS_NEXT_SONG"
            , BIG_REMOTEVIEWS_CANCEL = "com.kun.musicappdemo.BIG_REMOTEVIEWS_CANCEL";

    private static NotificationManager notificationManager;//管理通知，如展示刷新，删除等
    private static Notification notification;//通知
    private static int NOTIFICATION_ID = 300;//通知ID

    public static Notification createForegroundNotification(Context context) {//创建一个通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);//创建通知的点击后的跳转逻辑，这里跳转会MainActivity
        builder.setSmallIcon(R.mipmap.ic_launcher_round)//状态栏的小图标，这个必须要设置，需要的是alpha图片，这里没有素材，使用AS自带图标
                .setCustomContentView(getSmallContentView(context))//自定义的通知的大样式
                .setCustomBigContentView(getBigContentView(context))//自定义通知的小样式
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
        return builder.build();
    }

    public static void createNotificationChannel(Context context){//创建通知渠道，注册到系统上去
        ////Android8.0以上的系统，新建消息通道
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW);
        //可配置通知效果，震动，呼吸灯等；例子：notificationChannel.enableLights(true)，notificationChannel.lightColor = Color.RED，无需求不设置,在真机上调试
        //向系统注册
        notificationManager.createNotificationChannel(notificationChannel);
        notification = MusicNotificationUtil.createForegroundNotification(context);
    }

    public static void showNotification(Context context) {//首次展示通知

        notificationManager.notify(NOTIFICATION_ID,notification);
        //不理解这个的具体作用
        //((Service)context).startForeground(1,foregroundNotification);//Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，即 Context.startForegroundService()，以在前台启动新服务。
    }

    public static void cancelNotification() {//取消通知
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static void notifyNotification() {//刷新通知
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    //自定义通知栏会有大图样式和小图样式即普通样式和扩展样式，高度上边会有要求限制，普通样式高度不能超过64dp，扩展高度不能超过256dp；
    public static RemoteViews getSmallContentView(Context context) {
        smallRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_small_content_view);

        //为 smallRemoteViews 设置点击监听，点击后会发出广播
        smallRemoteViews.setOnClickPendingIntent(R.id.play_control,PendingIntent.getBroadcast(context, 1
                , new Intent(SMALL_REMOTEVIEWS_CONTROL), PendingIntent.FLAG_UPDATE_CURRENT));
        smallRemoteViews.setOnClickPendingIntent(R.id.next_song,PendingIntent.getBroadcast(context, 1
                , new Intent(SMALL_REMOTEVIEWS_NEXT_SONG), PendingIntent.FLAG_UPDATE_CURRENT));
        smallRemoteViews.setOnClickPendingIntent(R.id.cancel,PendingIntent.getBroadcast(context, 1
                , new Intent(SMALL_REMOTEVIEWS_CANCEL), PendingIntent.FLAG_UPDATE_CURRENT));
        return smallRemoteViews;
    }
    public static RemoteViews getBigContentView(Context context) {
        bigRemoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_big_content_view);

        //为 bigRemoteViews 设置点击监听，点击后会发出广播
        bigRemoteViews.setOnClickPendingIntent(R.id.pre_song,PendingIntent.getBroadcast(context, 1
                , new Intent(BIG_REMOTEVIEWS_PRE_SONG), PendingIntent.FLAG_UPDATE_CURRENT));
        bigRemoteViews.setOnClickPendingIntent(R.id.play_control,PendingIntent.getBroadcast(context, 1
                , new Intent(BIG_REMOTEVIEWS_CONTROL), PendingIntent.FLAG_UPDATE_CURRENT));
        bigRemoteViews.setOnClickPendingIntent(R.id.next_song,PendingIntent.getBroadcast(context, 1
                , new Intent(BIG_REMOTEVIEWS_NEXT_SONG), PendingIntent.FLAG_UPDATE_CURRENT));
        bigRemoteViews.setOnClickPendingIntent(R.id.cancel,PendingIntent.getBroadcast(context, 1
                , new Intent(BIG_REMOTEVIEWS_CANCEL), PendingIntent.FLAG_UPDATE_CURRENT));
        return bigRemoteViews;
    }
}
