package com.stardust.scriptdroid.droid.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.assist.BoundsAssistant;
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
    private static ActionPerformService instance;

    @SuppressWarnings("unchecked")
    public static final List<Action> NO_ACTION = Collections.EMPTY_LIST;
    public static final int STATE_INACTIVE = -1;
    public static final int STATE_PERFORM = 0;

    public static final List<Action> actions = new ArrayList<>();
    private static int state = STATE_INACTIVE;

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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent: state=" + state + " event=" + event);
        BoundsAssistant.performAssistance(event);
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

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");
        instance = this;
    }

    public static void disable() {
        if (instance != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                instance.disableSelf();
            } else {
                AccessibilityServiceUtils.goToAccessibilitySetting(App.getApp());
            }
        }
    }

    public static boolean isEnable() {
        return AccessibilityServiceUtils.isAccessibilityServiceEnabled(App.getApp(), ActionPerformService.class);
    }
}
