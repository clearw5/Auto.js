package com.stardust.view.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.service.notification.StatusBarNotification;
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
 * Created by Stardust on 2017/8/1.
 */

public interface NotificationListener {


    void onNotification(Notification notification);


}
