package com.tool.statusbarblock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class BlockerService extends Service {

    private WindowManager windowManager;
    private View blockView;
    private static final String CHANNEL_ID = "sbblock";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("SBBlock")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (blockView != null) return START_STICKY;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        blockView = new View(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.TOP;

        windowManager.addView(blockView, params);

        return START_STICKY;
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier(
            "status_bar_height", "dimen", "android");
        return resourceId > 0
            ? getResources().getDimensionPixelSize(resourceId)
            : 30;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, "StatusBar Blocker", NotificationManager.IMPORTANCE_MIN);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (blockView != null) {
            windowManager.removeView(blockView);
            blockView = null;
        }
    }
}
