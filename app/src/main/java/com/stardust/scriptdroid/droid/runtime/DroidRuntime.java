package com.stardust.scriptdroid.droid.runtime;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jraska.console.Console;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.console.ConsoleActivity;
import com.stardust.scriptdroid.droid.runtime.action.Action;
import com.stardust.scriptdroid.droid.runtime.action.ActionFactory;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.scriptdroid.droid.runtime.action.ActionTarget;
import com.stardust.scriptdroid.droid.runtime.action.GetTextAction;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.Shell;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DroidRuntime implements IDroidRuntime {

    private static final String TAG = "DroidRuntime";
    private static DroidRuntime runtime = new DroidRuntime();

    private final Object mActionPerformLock = new Object();
    private boolean mActionPerformResult;
    private Handler mUIHandler;

    public static DroidRuntime getRuntime() {
        return runtime;
    }

    protected DroidRuntime() {
        mUIHandler = new Handler(App.getApp().getMainLooper());
    }

    @Override
    public void launchPackage(String packageName) {
        PackageManager packageManager = App.getApp().getPackageManager();
        App.getApp().startActivity(packageManager.getLaunchIntentForPackage(packageName));
    }

    @Override
    public void launch(String packageName, String className) {
        App.getApp().startActivity(new Intent().setClassName(packageName, className).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void launchApp(String appName) {
        PackageManager packageManager = App.getApp().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString().equals(appName)) {
                launchPackage(applicationInfo.packageName);
                break;
            }
        }
    }

    @Override
    public String[] getPackageName(String appName) {
        return new String[0];
    }

    @Override
    public ActionTarget text(String text) {
        return new ActionTarget.TextActionTarget(text);
    }

    @Override
    public ActionTarget bounds(int left, int top, int right, int bottom) {
        return new ActionTarget.BoundsActionTarget(new Rect(left, top, right, bottom));
    }

    @Override
    public ActionTarget editable(int i) {
        return new ActionTarget.EditableActionTarget(i);
    }

    @Override
    public boolean click(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_CLICK));
    }

    @Override
    public boolean longClick(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_LONG_CLICK));
    }

    @Override
    public boolean scrollUp(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    @Override
    public boolean scrollDown(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    @Override
    public boolean scrollUp(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, i));
    }

    @Override
    public boolean scrollDown(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, i));
    }

    public boolean scrollAllUp() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollAllDown() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    @Override
    public boolean focus(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_FOCUS));
    }

    @Override
    public boolean select(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SELECT));
    }

    @Override
    public boolean setText(ActionTarget target, String text) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text));
    }

    @Override
    public boolean paste(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE));
    }

    public void log(@Nullable Object str) {
        Timber.i("" + str);
    }

    public void err(@Nullable Object o) {
        Timber.e("" + o);
    }

    public void console() {
        App.getApp().startActivity(new Intent(App.getApp(), ConsoleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void clearConsole() {
        Console.clear();
    }

    private boolean performAction(Action action) {
        if (AccessibilityWatchDogService.getInstance() == null) {
            toast(App.getApp().getString(R.string.text_no_accessibility_permission));
            throw new ScriptStopException(App.getApp().getString(R.string.text_no_accessibility_permission));
        }
        ActionPerformAccessibilityDelegate.setAction(action);
        synchronized (mActionPerformLock) {
            try {
                mActionPerformLock.wait();
            } catch (InterruptedException e) {
                ActionPerformAccessibilityDelegate.setAction(ActionPerformAccessibilityDelegate.NO_ACTION);
                throw new ScriptStopException(App.getApp().getString(R.string.text_script_stopped), e);
            }
        }
        return mActionPerformResult;
    }

    public List<String> getTexts() {
        if (AccessibilityWatchDogService.getInstance() == null) {
            toast(App.getApp().getString(R.string.text_no_accessibility_permission));
            throw new ScriptStopException(App.getApp().getString(R.string.text_no_accessibility_permission));
        }
        GetTextAction.result = Collections.EMPTY_LIST;
        ActionPerformAccessibilityDelegate.setAction(new GetTextAction());
        synchronized (mActionPerformLock) {
            try {
                mActionPerformLock.wait();
                return GetTextAction.result;
            } catch (InterruptedException e) {
                ActionPerformAccessibilityDelegate.setAction(ActionPerformAccessibilityDelegate.NO_ACTION);
                throw new ScriptStopException(App.getApp().getString(R.string.text_script_stopped), e);
            }
        }
    }

    @Override
    public void toast(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getApp(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ScriptStopException(e);
        }
    }

    public void shell(String cmd, int root) {
        Shell.execCommand(cmd, root != 0);
    }

    @Override
    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    @Override
    public void stop() {
        Thread.interrupted();
    }

    @Override
    public MaterialDialog.Builder dialog() {
        if (App.currentActivity() == null) {
            Toast.makeText(App.getApp(), R.string.text_cannot_create_dialog_when_app_invisible, Toast.LENGTH_SHORT).show();
            throw new ScriptStopException(App.getApp().getString(R.string.text_cannot_create_dialog_when_app_invisible));
        }
        return new MaterialDialog.Builder(App.currentActivity()) {

            public MaterialDialog show() {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        superShow();
                    }
                });
                return null;
            }

            private void superShow() {
                super.show();
            }
        };
    }

    public void notifyActionPerformed(boolean succeed) {
        mActionPerformResult = succeed;
        synchronized (mActionPerformLock) {
            mActionPerformLock.notify();
        }
    }

}
