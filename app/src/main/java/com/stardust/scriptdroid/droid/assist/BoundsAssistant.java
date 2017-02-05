package com.stardust.scriptdroid.droid.assist;

import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;


/**
 * Created by Stardust on 2017/2/4.
 */

public class BoundsAssistant {
    public static final String KEY_ASSIST_MODE_ENABLE = "ASSIST_MODE_ENABLE";


    private static boolean assistModeEnable;
    private static BoundsAssistClipList boundsAssistClipList = SharedPrefBoundsAssistClipList.getInstance();

    public static boolean isAssistModeEnable() {
        return assistModeEnable;
    }

    public static void setAssistModeEnable(boolean assistModeEnable) {
        if (assistModeEnable && Pref.isFirstEnableAssistMode()) {
            showAssistModeInfoDialog();
        }
        BoundsAssistant.assistModeEnable = assistModeEnable;
        App.getStateObserver().setState(KEY_ASSIST_MODE_ENABLE, assistModeEnable);
    }

    private static void showAssistModeInfoDialog() {
        new MaterialDialog.Builder(App.currentActivity())
                .title(R.string.text_alert)
                .content(R.string.assist_mode_notice)
                .positiveText(R.string.ok)
                .show();
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
        boundsAssistClipList.add(str);
        Toast.makeText(App.getApp(), str, Toast.LENGTH_SHORT).show();
    }


    static {
        assistModeEnable = PreferenceManager.getDefaultSharedPreferences(App.getApp()).getBoolean(KEY_ASSIST_MODE_ENABLE, false);
    }

}
