package com.stardust.scriptdroid.droid.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformService extends AccessibilityService {

    private static final String TAG = "ActionPerformService";
    public static final String KEY_ASSIST_MODE_ENABLE = "ASSIST_MODE_ENABLE";
    private static ActionPerformService instance;

    @SuppressWarnings("unchecked")
    public static final List<Action> NO_ACTION = Collections.EMPTY_LIST;
    public static final int STATE_INACTIVE = -1;
    public static final int STATE_PERFORM = 0;

    public static final List<Action> actions = new ArrayList<>();
    private static int state = STATE_INACTIVE;

    private static boolean assistModeEnable = false;


    public static void setActions(Collection<Action> collection) {
        synchronized (actions) {
            actions.clear();
            actions.addAll(collection);
            state = actions.size() > 0 ? STATE_PERFORM : STATE_INACTIVE;
        }
    }

    public static void setAction(Action action) {
        setActions(Collections.singletonList(action));
    }

    public static ActionPerformService getInstance() {
        return instance;
    }

    public static boolean isAssistModeEnable() {
        return assistModeEnable;
    }

    public static void setAssistModeEnable(boolean assistModeEnable) {
        ActionPerformService.assistModeEnable = assistModeEnable;
        App.getStateObserver().setState(KEY_ASSIST_MODE_ENABLE, assistModeEnable);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent: state=" + state + " event=" + event);
        performAssistance(event);
        if (state == STATE_INACTIVE)
            return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            Log.v(TAG, "root = null");
        }
        Action action = nextAction();
        if (action == null) {
            onActionPerformed(true);
        } else {
            Log.i(TAG, "perform action:" + action);
            if (action.perform(root)) {
                state++;
            } else if (!action.performUtilSucceed()) {
                onActionPerformed(false);
            }
        }

    }

    private void onActionPerformed(boolean succeed) {
        state = STATE_INACTIVE;
        synchronized (actions) {
            actions.clear();
        }
        DroidRuntime.getRuntime().notifyActionPerformed(succeed);
    }

    private Action nextAction() {
        synchronized (actions) {
            if (state >= actions.size()) {
                return null;
            }
            return actions.get(state);
        }
    }

    private void performAssistance(AccessibilityEvent event) {
        if (!assistModeEnable) {
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (nodeInfo == null) {
                return;
            }
            nodeInfo.refresh();
            Log.v(TAG, "click: " + nodeInfo);
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            String str = rect.toString().replace('-', ',').replace(" ", "").substring(4);
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("", str));
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            nodeInfo.recycle();
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");
        instance = this;
    }

    static {
        assistModeEnable = PreferenceManager.getDefaultSharedPreferences(App.getApp()).getBoolean(KEY_ASSIST_MODE_ENABLE, false);
    }

    public static void disable() {
        if (instance != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instance.disableSelf();
        }
    }

    public static boolean isEnable() {
        return AccessibilityServiceUtils.isAccessibilityServiceEnabled(App.getApp(), ActionPerformService.class);
    }
}
