package com.stardust.scriptdroid.action;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformService extends AccessibilityService {

    private static final String TAG = "SettingRunningServiceSS";
    private static ActionPerformService instance;

    public static void goToPermissionSettingIfDisabled(final Context context) {
        if (!isAccessibilityServiceEnabled(context, ActionPerformService.class)) {
            new MaterialDialog.Builder(context)
                    .content(R.string.explain_accessibility_permission)
                    .positiveText(R.string.text_go_to_setting)
                    .negativeText(R.string.text_cancel)
                    .onPositive((dialog, which) -> context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))).show();
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public static final int STATE_WAIT = 0;
    @SuppressWarnings("unchecked")
    public static final List<Action> NO_ACTION = Collections.EMPTY_LIST;

    public static final List<Action> actions = new ArrayList<>();
    private static int state = STATE_WAIT;

    public static boolean assistModeEnable = true;
    private AccessibilityNodeInfo mLastFocus;

    public static void setActions(Collection<Action> collection) {
        synchronized (actions) {
            actions.clear();
            actions.addAll(collection);
            state = STATE_WAIT;
        }
    }

    public static ActionPerformService getInstance() {
        return instance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "event type=" + event.getEventType());
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        performAssistance(event, event.getSource());
        Log.v(TAG, "rootInActiveWindow = " + nodeInfo);
        if (nodeInfo == null)
            return;
        Log.v(TAG, "state = " + state);
        Action action = nextAction();
        if (action == null) {
            reset();
        } else if (action.perform(nodeInfo)) {
            state++;
        }
    }

    private void performAssistance(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo) {
        if (!assistModeEnable || nodeInfo == null)
            return;
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            nodeInfo.refresh();
            Log.v(TAG, "click: " + nodeInfo);
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            String str = rect.toString().replace('-', ',').replace(" ", "");
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("", str));
            Toast.makeText(this, "id=" + nodeInfo.getViewIdResourceName() + " bounds=" + str, Toast.LENGTH_SHORT).show();
        }
    }

    private int[] findAddress(AccessibilityNodeInfo nodeInfo) {
        List<Integer> address = new ArrayList<>();
        AccessibilityNodeInfo lastNodeInfo = null;
        while (nodeInfo.getParent() != null) {
            address.add(findPositionInParent(nodeInfo));
            if (lastNodeInfo != null)
                lastNodeInfo.recycle();
            lastNodeInfo = nodeInfo;
            nodeInfo = nodeInfo.getParent();
        }
        int[] array = new int[address.size()];
        for (int i = 0; i < address.size(); i++) {
            array[i] = address.get(address.size() - i - 1);
        }
        return array;
    }



    private int findPositionInParent(AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo parent = nodeInfo.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child != null && child.equals(nodeInfo)) {
                parent.recycle();
                child.recycle();
                return i;
            }
            if (child != null) {
                child.recycle();
            }
        }
        parent.recycle();
        return -1;
    }

    private void reset() {
        state = STATE_WAIT;
        synchronized (actions) {
            actions.clear();
        }
    }

    private Action nextAction() {
        synchronized (actions) {
            if (state >= actions.size()) {
                return null;
            }
            return actions.get(state);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");
        Action.setActionContext(this);
        instance = this;
    }

}
