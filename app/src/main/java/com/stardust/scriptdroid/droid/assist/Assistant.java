package com.stardust.scriptdroid.droid.assist;

import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.stardust.scriptdroid.App;


/**
 * Created by Stardust on 2017/2/4.
 */

public class Assistant {
    public static final String KEY_ASSIST_MODE_ENABLE = "ASSIST_MODE_ENABLE";


    private static boolean assistModeEnable;
    private static AssistClipList assistClipList = SharedPrefAssistClipList.getInstance();

    public static boolean isAssistModeEnable() {
        return assistModeEnable;
    }

    public static void setAssistModeEnable(boolean assistModeEnable) {
        Assistant.assistModeEnable = assistModeEnable;
        App.getStateObserver().setState(KEY_ASSIST_MODE_ENABLE, assistModeEnable);
    }

    public static void performAssistance(AccessibilityEvent event) {
        if (!assistModeEnable) {
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (nodeInfo == null) {
                return;
            }
            nodeInfo.refresh();
            Rect rect = getBoundsInScreen(nodeInfo);
            saveAndAlertBounds(rect);
            nodeInfo.recycle();
        }
    }

    private static Rect getBoundsInScreen(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return rect;
    }

    private static void saveAndAlertBounds(Rect rect) {
        String str = rect.toString().replace('-', ',').replace(" ", "").substring(4);
        assistClipList.add(str);
        Toast.makeText(App.getApp(), str, Toast.LENGTH_SHORT).show();
    }


    static {
        assistModeEnable = PreferenceManager.getDefaultSharedPreferences(App.getApp()).getBoolean(KEY_ASSIST_MODE_ENABLE, false);
    }

}
