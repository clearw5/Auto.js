package com.stardust.view.accessibility;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/7/18.
 */

public interface OnKeyListener {

    void onKeyEvent(int keyCode, KeyEvent event);

    class Observer implements OnKeyListener {

        private static final String TAG = "OnKeyListenerObserver";

        private CopyOnWriteArrayList<OnKeyListener> mOnKeyListeners = new CopyOnWriteArrayList<>();

        @Override
        public void onKeyEvent(int keyCode, KeyEvent event) {
            for (OnKeyListener listener : mOnKeyListeners) {
                try {
                    listener.onKeyEvent(keyCode, event);
                } catch (Exception e) {
                    Log.e(TAG, "Error OnKeyEvent: " + event + " Listener: " + listener, e);
                }
            }
        }

        public void addListener(OnKeyListener listener) {
            mOnKeyListeners.add(listener);
        }

        public boolean removeListener(OnKeyListener listener) {
            return mOnKeyListeners.remove(listener);
        }
    }
}
