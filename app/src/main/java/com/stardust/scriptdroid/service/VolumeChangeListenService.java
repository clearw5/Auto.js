package com.stardust.scriptdroid.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchHandleService;
import com.stardust.scriptdroid.record.AccessibilityRecorderDelegate;
import com.stardust.scriptdroid.Pref;

import java.lang.ref.WeakReference;

import static com.stardust.scriptdroid.external.notification.record.ActionRecordSwitchView.STOPPED;

/**
 * Created by Stardust on 2017/2/16.
 */

public class VolumeChangeListenService extends Service {

    private static final String ACTION_VOLUME_CHANGE = "android.media.VOLUME_CHANGED_ACTION";

    private VolumeChangeReceiver mVolumeChangeReceiver;
    private static WeakReference<VolumeChangeListenService> instance;

    public static void stopServiceIfNeeded() {
        if (instance != null && instance.get() != null) {
            instance.get().stopSelf();
        }
    }

    public static void startServiceIfNeeded(Context context) {
        if (Pref.isRecordVolumeControlEnable() && (instance == null || instance.get() == null)) {
            context.startService(new Intent(context, VolumeChangeListenService.class));
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (instance != null && instance.get() != null) {
            instance.get().stopSelf();
        }
        instance = new WeakReference<>(this);
        mVolumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(mVolumeChangeReceiver, new IntentFilter(ACTION_VOLUME_CHANGE));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mVolumeChangeReceiver);
    }

    private class VolumeChangeReceiver extends BroadcastReceiver {

        private long mLastChangeMillis;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_VOLUME_CHANGE)) {
                if (System.currentTimeMillis() - mLastChangeMillis < 400) {
                    return;
                }
                mLastChangeMillis = System.currentTimeMillis();
                if (AccessibilityRecorderDelegate.getInstance().getState() == STOPPED) {
                    ActionRecordSwitchHandleService.startRecord(VolumeChangeListenService.this);
                } else {
                    ActionRecordSwitchHandleService.stopRecord(VolumeChangeListenService.this);
                }
            }
        }
    }

}
