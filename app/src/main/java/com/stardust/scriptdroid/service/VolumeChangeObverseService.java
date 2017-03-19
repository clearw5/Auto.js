package com.stardust.scriptdroid.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stardust.scriptdroid.external.notification.record.AccessibilityActionRecordNotification;
import com.stardust.scriptdroid.record.Recorder;
import com.stardust.scriptdroid.record.accessibility.AccessibilityActionRecorder;
import com.stardust.scriptdroid.Pref;

import java.lang.ref.WeakReference;


/**
 * Created by Stardust on 2017/2/16.
 */

public class VolumeChangeObverseService extends Service {

    private static final String ACTION_VOLUME_CHANGE = "android.media.VOLUME_CHANGED_ACTION";

    private VolumeChangeReceiver mVolumeChangeReceiver;
    private static VolumeChangeObverseService instance;

    public static void stopServiceIfNeeded() {
        if (instance != null) {
            instance.stopSelf();
            instance = null;
        }
    }

    public static void startServiceIfNeeded(Context context) {
        if (Pref.isRecordVolumeControlEnable() && instance == null) {
            context.startService(new Intent(context, VolumeChangeObverseService.class));
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (instance != null) {
            instance.stopSelf();
        }
        instance = this;
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
        instance = null;
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
                if (AccessibilityActionRecorder.getInstance().getState() == Recorder.STATE_STOPPED) {
                    AccessibilityActionRecordNotification.startRecord(VolumeChangeObverseService.this);
                } else {
                    AccessibilityActionRecordNotification.stopRecord(VolumeChangeObverseService.this);
                }
            }
        }
    }

}
