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
    public static String NOTIFICATION_CHANNEL_ID = "notification_channel_id_02";
    public static String NOTIFICATION_CHANNEL_NAME = "Music Notification";
    public static RemoteViews smallRemoteViews,bigRemoteViews;

    public static String SMALL_REMOTEVIEWS_CONTROL = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_CONTROL"
            , SMALL_REMOTEVIEWS_NEXT_SONG = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_NEXT_SONG"
            , SMALL_REMOTEVIEWS_CANCEL = "com.kun.musicappdemo.SMALL_REMOTEVIEWS_CANCEL";
    public static String BIG_REMOTEVIEWS_PRE_SONG = "com.kun.musicappdemo.BIG_REMOTEVIEWS_PRE_SONG"
            , BIG_REMOTEVIEWS_CONTROL = "com.kun.musicappdemo.BIG_REMOTEVIEWS_CONTROL"
            , BIG_REMOTEVIEWS_NEXT_SONG = "com.kun.musicappdemo.BIG_REMOTEVIEWS_NEXT_SONG"
            , BIG_REMOTEVIEWS_CANCEL = "com.kun.musicappdemo.BIG_REMOTEVIEWS_CANCEL";

    public static NotificationManager notificationManager;
    public static Notification notification;
    public static int NOTIFICATION_ID = 300;

    public static Notification createForegroundNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.mipmap.music_demo_app_icon)
                .setCustomContentView(getSmallContentView(context))
                .setCustomBigContentView(getBigContentView(context))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
        return builder.build();
    }

    public static void showNotification(Context context) {
        ////Android8.0以上的系统，新建消息通道
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
        //可配置通知效果，震动，呼吸灯等；例子：notificationChannel.enableLights(true)，notificationChannel.lightColor = Color.RED，无需求不设置
        //向系统注册
        notificationManager.createNotificationChannel(notificationChannel);

        notification = MusicNotificationUtil.createForegroundNotification(context);
        notificationManager.notify(NOTIFICATION_ID,notification);
        //不理解这个的具体作用
        //((Service)context).startForeground(1,foregroundNotification);//Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，即 Context.startForegroundService()，以在前台启动新服务。
    }
    public static void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static void notifyNotification() {
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    //自定义通知栏会有大图样式和小图样式即普通样式和扩展样式，高度上边会有要求限制，普通样式高度不能超过64dp，扩展高度不能超过256dp；
    public static RemoteViews getSmallContentView(Context context) {
        smallRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_small_content_view);
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
