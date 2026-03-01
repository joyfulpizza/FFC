package com.example.floatingfps;

import android.app.*;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.widget.TextView;

public class FPSService extends Service {

    private WindowManager windowManager;
    private TextView fpsText;
    private int frames = 0;
    private long lastTime;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(1, createNotification());

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        fpsText = new TextView(this);
        fpsText.setTextColor(0xFF00FF00);
        fpsText.setTextSize(18);
        fpsText.setBackgroundColor(0x88000000);
        fpsText.setPadding(20, 10, 20, 10);

        int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        windowManager.addView(fpsText, params);

        lastTime = System.currentTimeMillis();
        startFPSCounter();
    }

    private void startFPSCounter() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                frames++;
                long now = System.currentTimeMillis();

                if (now - lastTime >= 1000) {
                    fpsText.setText("FPS: " + frames);
                    frames = 0;
                    lastTime = now;
                }

                handler.postDelayed(this, 16);
            }
        });
    }

    private Notification createNotification() {
        String channelId = "fps_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "FPS Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }

        return new Notification.Builder(this, channelId)
                .setContentTitle("Floating FPS Running")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
