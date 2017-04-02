package autojs.runtime.action;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.view.accessibility.AccessibilityDelegate;


/**
 * Created by Stardust on 2017/1/21.
 */

public class ActionPerformAccessibilityDelegate implements AccessibilityDelegate {

    private static final String TAG = "ActionPerformDelegate";

    public static final Action NO_ACTION = null;

    private static final Object ACTION_LOCK = new Object();

    private static Action action;

    //private Executor mExecutor = Executors.newFixedThreadPool(5);

    public static void setAction(Action action) {
        synchronized (ACTION_LOCK) {
            ActionPerformAccessibilityDelegate.action = action;
        }
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        synchronized (ACTION_LOCK) {
            if (action == NO_ACTION)
                return false;
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                Log.v(TAG, "root = null");
                return false;
            }
            performAction(root, action);
            return false;
        }
    }

    private void performAction(final AccessibilityNodeInfo root, final Action action) {
        new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "perform action:" + action);
                if (action.perform(root)) {
                    action.setResult(true);
                    onActionPerformed();
                } else if (!action.performUtilSucceed()) {
                    action.setResult(false);
                    onActionPerformed();
                }
            }
        }.run();
    }


    private void onActionPerformed() {
        synchronized (ACTION_LOCK) {
            action = NO_ACTION;
            DroidRuntime.getRuntime().notifyActionPerformed();
        }
    }


}

