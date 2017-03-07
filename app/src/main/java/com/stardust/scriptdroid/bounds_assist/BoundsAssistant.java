package com.stardust.scriptdroid.bounds_assist;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.service.AccessibilityDelegate;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;


/**
 * Created by Stardust on 2017/2/4.
 */

public class BoundsAssistant implements AccessibilityDelegate {

    public static final String KEY_BOUNDS_ASSIST_ENABLE = "ASSIST_MODE_ENABLE";

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
        App.getStateObserver().setState(KEY_BOUNDS_ASSIST_ENABLE, assistModeEnable);
    }

    private static void showAssistModeInfoDialog() {
        new ThemeColorMaterialDialogBuilder(App.currentActivity())
                .title(R.string.text_alert)
                .content(R.string.assist_mode_notice)
                .positiveText(R.string.ok)
                .show();
    }

    private static void performAssistance(AccessibilityEvent event) {
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

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        performAssistance(event);
        return false;
    }

    public static Rect getBoundsInScreen(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return rect;
    }

    private static void saveAndAlertBounds(Rect rect) {
        String str = boundsToString(rect);
        boundsAssistClipList.add(str);
        Toast.makeText(App.getApp(), str, Toast.LENGTH_SHORT).show();
    }

    public static String boundsToString(Rect rect) {
        return rect.toString().replace('-', ',').replace(" ", "").substring(4);
    }


    static {
        assistModeEnable = PreferenceManager.getDefaultSharedPreferences(App.getApp()).getBoolean(KEY_BOUNDS_ASSIST_ENABLE, false);
    }


}
