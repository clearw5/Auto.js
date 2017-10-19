package com.stardust.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/4/4.
 */

public class VolumeChangeObserver extends BroadcastReceiver {

    public interface OnVolumeChangeListener {

        void onVolumeChange();
    }

    public static final String ACTION_VOLUME_CHANGE = "android.media.VOLUME_CHANGED_ACTION";

    private long mLastChangeMillis;
    private List<OnVolumeChangeListener> mOnVolumeChangeListenerList = new ArrayList<>();

    public void addOnVolumeChangeListener(OnVolumeChangeListener listener) {
        mOnVolumeChangeListenerList.add(listener);
    }

    public void removeOnVolumeChangeListener(OnVolumeChangeListener listener) {
        mOnVolumeChangeListenerList.remove(listener);
    }

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
