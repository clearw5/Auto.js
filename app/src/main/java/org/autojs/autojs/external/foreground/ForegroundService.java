package org.autojs.autojs.external.foreground;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import org.autojs.autojs.R;

public class ForegroundService extends Service {
    private static final int NOTIFICATION_ID = 117;
    private static final String CHANEL_ID = "foreground";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return START_STICKY;
    }

    private void startForeground(){
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        return new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(getString(R.string.foreground_notification_title))
                .setContentText(getString(R.string.foreground_notification_text))
                .setSmallIcon(R.drawable.autojs_material)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        CharSequence name = getString(R.string.foreground_notification_channel_name);
        String description = getString(R.string.foreground_notification_channel_name);
        NotificationChannel channel = new NotificationChannel(CHANEL_ID, name,  NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        manager.createNotificationChannel(channel);
    }
}
