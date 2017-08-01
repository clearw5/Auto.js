package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/8/1.
 */

public interface NotificationListener {

    void onNotification(AccessibilityEvent event, String[] notification);

    void onNotification(AccessibilityEvent event, Notification notification);

    class Observer implements NotificationListener, AccessibilityDelegate {

        private static final Set<Integer> EVENT_TYPES = Collections.singleton(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);

        private static final String TAG = "NotificationObserver";
        private static final String[] EMPTY = new String[0];

        private Context mContext;
        private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();

        public Observer(Context context) {
            mContext = context;
        }

        @Override
        public void onNotification(AccessibilityEvent event, String[] notification) {
            for (NotificationListener listener : mNotificationListeners) {
                try {
                    listener.onNotification(event, notification);
                } catch (Exception e) {
                    Log.e(TAG, "Error onNotification: " + Arrays.toString(notification) + " Listener: " + listener, e);
                }
            }
        }

        @Override
        public void onNotification(AccessibilityEvent event, Notification notification) {
            for (NotificationListener listener : mNotificationListeners) {
                try {
                    listener.onNotification(event, notification);
                } catch (Exception e) {
                    Log.e(TAG, "Error onNotification: " + notification + " Listener: " + listener, e);
                }
            }
        }

        public void addListener(NotificationListener listener) {
            mNotificationListeners.add(listener);
        }

        public boolean removeListener(NotificationListener listener) {
            return mNotificationListeners.remove(listener);
        }

        @Override
        public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
            if (event.getParcelableData() instanceof Notification) {
                Notification notification = (Notification) event.getParcelableData();
                Log.d(TAG, "onNotification: " + notification + "; " + event);
                onNotification(event, notification);
            } else {
                List<CharSequence> list = event.getText();
                Log.d(TAG, "onNotification: " + list + "; " + event);
                if (event.getPackageName().equals(mContext.getPackageName())) {
                    return false;
                }
                if (list != null) {
                    onNotification(event, ArrayUtils.toStringArray(list));
                }
            }
            return false;
        }

        @Nullable
        @Override
        public Set<Integer> getEventTypes() {
            return EVENT_TYPES;
        }
    }
}
