package com.stardust.notification;

import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcel;
import android.support.annotation.RequiresApi;

/**
 * Created by Stardust on 2017/10/30.
 */

public class Notification extends android.app.Notification {

    private String mPackageName;

    private Notification(String packageName) {
        mPackageName = packageName;
    }

    public static Notification create(android.app.Notification n, String packageName) {
        Notification notification = new Notification(packageName);
        clone(n, notification);
        return notification;
    }

    public String getPackageName() {
        return mPackageName;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getText() {
        return extras.getString(EXTRA_TEXT);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getTitle() {
        return extras.getString(EXTRA_TITLE);
    }

    public void click() {
        try {
            this.contentIntent.send();
        } catch (PendingIntent.CanceledException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        try {
            this.deleteIntent.send();
        } catch (PendingIntent.CanceledException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "Notification{" +
                    "packageName='" + mPackageName + "', " +
                    "title='" + getTitle() + ", " +
                    "text='" + getText() + "'" +
                    "} ";
        }
        return super.toString();
    }

    public static void clone(android.app.Notification from, android.app.Notification to) {
        to.when = from.when;
        to.icon = from.icon;
        to.iconLevel = from.iconLevel;
        to.number = from.number;
        to.contentIntent = from.contentIntent;
        to.deleteIntent = from.deleteIntent;
        to.fullScreenIntent = from.fullScreenIntent;
        to.tickerText = from.tickerText;
        to.tickerView = from.tickerView;
        to.contentView = from.contentView;
        to.bigContentView = from.bigContentView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            to.headsUpContentView = from.headsUpContentView;
            to.audioAttributes = from.audioAttributes;
            to.color = from.color;
            to.visibility = from.visibility;
            to.category = from.category;
            to.publicVersion = from.publicVersion;
        }
        to.largeIcon = from.largeIcon;
        to.sound = from.sound;
        to.audioStreamType = from.audioStreamType;
        to.vibrate = from.vibrate;
        to.ledARGB = from.ledARGB;
        to.ledOnMS = from.ledOnMS;
        to.ledOffMS = from.ledOffMS;
        to.defaults = from.defaults;
        to.flags = from.flags;
        to.priority = from.priority;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            to.extras = from.extras;
            to.actions = from.actions;
        }
    }

}
