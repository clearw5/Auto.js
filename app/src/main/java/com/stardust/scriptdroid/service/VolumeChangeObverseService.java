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
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stardust on 2017/2/16.
 */

public class VolumeChangeObverseService extends Service {

    public interface OnVolumeChangeListener {

        void onVolumeChange();
    }

    private static final String ACTION_VOLUME_CHANGE = "android.media.VOLUME_CHANGED_ACTION";
    private static List<OnVolumeChangeListener> mOnVolumeChangeListenerList = new ArrayList<>();

    public static void addOnVolumeChangeListener(OnVolumeChangeListener listener) {
        mOnVolumeChangeListenerList.add(listener);
    }

    public static void removeOnVolumeChangeListener(OnVolumeChangeListener listener) {
        mOnVolumeChangeListenerList.remove(listener);
    }

    private VolumeChangeReceiver mVolumeChangeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
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
                for (OnVolumeChangeListener listener : mOnVolumeChangeListenerList) {
                    listener.onVolumeChange();
                }
            }
        }
    }

}
