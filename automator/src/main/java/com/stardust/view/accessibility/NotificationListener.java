package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/8/1.
 */

public interface NotificationListener {

    class NotificationInfo {

        private String mPackageName;
        private String mText;
        private List<String> mTexts;

        public NotificationInfo(String packageName, List<String> texts) {
            mPackageName = packageName;
            mTexts = texts;
            if (mTexts.size() > 0) {
                mText = mTexts.get(0);
            }
        }

        public NotificationInfo(CharSequence packageName, List<CharSequence> list) {
            mPackageName = packageName == null ? "" : packageName.toString();
            mTexts = new ArrayList<>(list.size());
            for (CharSequence text : list) {
                if (text != null) {
                    mTexts.add(text.toString());
                }
            }
            if (mTexts.size() > 0) {
                mText = mTexts.get(0);
            }
        }


        public static NotificationInfo fromEvent(AccessibilityEvent event) {
            return new NotificationInfo(event.getPackageName(), event.getText());
        }

        public String getPackageName() {
            return mPackageName;
        }

        public String getText() {
            return mText;
        }

        public List<String> getTexts() {
            return mTexts;
        }

        @Override
        public String toString() {
            return "NotificationInfo{" +
                    "packageName='" + mPackageName + '\'' +
                    ", text='" + mText + '\'' +
                    ", texts=" + mTexts +
                    '}';
        }
    }

    void onNotification(AccessibilityEvent event, NotificationInfo notification);

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
        public void onNotification(AccessibilityEvent event, NotificationInfo notification) {
            for (NotificationListener listener : mNotificationListeners) {
                try {
                    listener.onNotification(event, notification);
                } catch (Exception e) {
                    Log.e(TAG, "Error onNotification: " + notification + " Listener: " + listener, e);
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
                    onNotification(event, new NotificationInfo(event.getPackageName(), list));
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
