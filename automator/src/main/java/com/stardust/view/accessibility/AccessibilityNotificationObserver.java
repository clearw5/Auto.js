package com.stardust.view.accessibility;

import android.accessibilityservice.*;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.notification.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/11/3.
 */

public class AccessibilityNotificationObserver implements NotificationListener, AccessibilityDelegate {

    public class Toast {
        public final List<String> texts;
        public final String packageName;

        public Toast(String packageName, List<CharSequence> texts) {
            this.texts = new ArrayList<>(texts.size());
            for (CharSequence t : texts) {
                if (t != null) {
                    this.texts.add(t.toString());
                }
            }
            this.packageName = packageName;
        }

        public String getText() {
            if (texts.isEmpty()) {
                return null;
            }
            CharSequence text = texts.get(0);
            if (text == null) {
                return null;
            }
            return text.toString();
        }

        public List<String> getTexts() {
            return texts;
        }

        public String getPackageName() {
            return packageName;
        }

        @Override
        public String toString() {
            return "Toast{" +
                    "texts=" + texts +
                    ", packageName='" + packageName + '\'' +
                    '}';
        }
    }

    public interface ToastListener {
        void onToast(Toast toast);
    }

    private static final Set<Integer> EVENT_TYPES = Collections.singleton(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);

    private static final String TAG = "NotificationObserver";
    private static final String[] EMPTY = new String[0];

    private Context mContext;
    private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ToastListener> mToastListeners = new CopyOnWriteArrayList<>();

    public AccessibilityNotificationObserver(Context context) {
        mContext = context;
    }


    public void addNotificationListener(NotificationListener listener) {
        mNotificationListeners.add(listener);
    }

    public boolean removeNotificationListener(NotificationListener listener) {
        return mNotificationListeners.remove(listener);
    }


    public void addToastListener(ToastListener listener) {
        mToastListeners.add(listener);
    }

    public boolean removeToastListener(ToastListener listener) {
        return mToastListeners.remove(listener);
    }

    @Override
    public boolean onAccessibilityEvent(android.accessibilityservice.AccessibilityService service, AccessibilityEvent event) {
        if (event.getParcelableData() instanceof Notification) {
            android.app.Notification notification = (android.app.Notification) event.getParcelableData();
            Log.d(TAG, "onNotification: " + notification + "; " + event);
            onNotification(Notification.create(notification, event.getPackageName().toString()));
        } else {
            List<CharSequence> list = event.getText();
            Log.d(TAG, "onNotification: " + list + "; " + event);
            if (event.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
            if (list != null) {
                onToast(event, new Toast(event.getPackageName().toString(), list));
            }
        }

        return false;
    }

    private void onToast(AccessibilityEvent event, Toast toast) {
        for (ToastListener listener : mToastListeners) {
            try {
                listener.onToast(toast);
            } catch (Exception e) {
                Log.e(TAG, "Error onNotification: " + toast + " Listener: " + listener, e);
            }
        }
    }

    @Nullable
    @Override
    public Set<Integer> getEventTypes() {
        return EVENT_TYPES;
    }

    @Override
    public void onNotification(Notification notification) {
        for (NotificationListener listener : mNotificationListeners) {
            try {
                listener.onNotification(notification);
            } catch (Exception e) {
                Log.e(TAG, "Error onNotification: " + notification + " Listener: " + listener, e);
            }
        }
    }
}
