package com.wavestech.yorubalock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;


public class LockScreenService extends Service {

    private BroadcastReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // Register for Lockscreen event intents
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new LockScreenIntentReceiver();
        registerReceiver(mReceiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        return START_STICKY;
    }

    // Run service in foreground so it is less likely to be killed by system
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.wavestech.yorubalock";
        String channelName = "LockScreenService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.face_icon_logo)
                .setContentTitle("Yoruba Locker is currently protecting you")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
//				.setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .build();
        startForeground(2, notification);
    }

    // Unregister receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
