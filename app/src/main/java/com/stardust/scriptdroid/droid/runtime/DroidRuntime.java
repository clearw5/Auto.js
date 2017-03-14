package com.stardust.scriptdroid.droid.runtime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.droid.runtime.action.ActionTarget;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.jraska.console.Console;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.runtime.action.Action;
import com.stardust.scriptdroid.droid.runtime.action.ActionFactory;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformAccessibilityDelegate;
import com.stardust.scriptdroid.tool.FileUtils;
import com.stardust.scriptdroid.tool.Shell;
import com.stardust.scriptdroid.ui.console.ConsoleActivity;
import com.stardust.view.accessibility.AccessibilityServiceUtils;

import java.io.File;
import java.util.List;

import timber.log.Timber;

;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DroidRuntime {

    private static final String TAG = "DroidRuntime";
    private static DroidRuntime runtime = new DroidRuntime();

    private final Object mActionPerformLock = new Object();
    private Handler mUIHandler;

    public static DroidRuntime getRuntime() {
        return runtime;
    }

    protected DroidRuntime() {
        mUIHandler = new Handler(App.getApp().getMainLooper());
    }

    public void launchPackage(String packageName) {
        PackageManager packageManager = App.getApp().getPackageManager();
        App.getApp().startActivity(packageManager.getLaunchIntentForPackage(packageName));
    }

    public void launch(String packageName, String className) {
        App.getApp().startActivity(new Intent().setClassName(packageName, className).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

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

    public String[] getPackageName(String appName) {
        return new String[0];
    }

    public ActionTarget text(String text, int i) {
        return new ActionTarget.TextActionTarget(text, i);
    }

    public ActionTarget bounds(int left, int top, int right, int bottom) {
        return new ActionTarget.BoundsActionTarget(new Rect(left, top, right, bottom));
    }

    public ActionTarget editable(int i) {
        return new ActionTarget.EditableActionTarget(i);
    }

    public ActionTarget id(String id) {
        return new ActionTarget.IdActionTarget(id);
    }

    public boolean click(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_CLICK));
    }

    public boolean longClick(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_LONG_CLICK));
    }

    public boolean scrollUp(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollDown(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    public boolean scrollUp(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, i));
    }

    public boolean scrollDown(int i) {
        return performAction(ActionFactory.createScrollAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, i));
    }

    public boolean scrollAllUp() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollAllDown() {
        return performAction(ActionFactory.createScrollMaxAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
    }

    public boolean focus(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_FOCUS));
    }

    public boolean select(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SELECT));
    }

    public boolean setText(ActionTarget target, String text) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text));
    }

    public void setClip(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                ((ClipboardManager) App.getApp().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TAG, text));
            }
        });
    }

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

    private <T> T performAction(Action action) {
        ensureAccessibilityServiceEnable();
        ActionPerformAccessibilityDelegate.setAction(action);
        synchronized (mActionPerformLock) {
            try {
                mActionPerformLock.wait();
            } catch (InterruptedException e) {
                ActionPerformAccessibilityDelegate.setAction(ActionPerformAccessibilityDelegate.NO_ACTION);
                throw new ScriptStopException(App.getApp().getString(R.string.text_script_stopped), e);
            }
        }
        return (T) action.getResult();
    }

    private void ensureAccessibilityServiceEnable() {
        if (AccessibilityWatchDogService.getInstance() == null) {
            String errorMessage = null;
            if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(App.getApp(), AccessibilityWatchDogService.class)) {
                errorMessage = App.getApp().getString(R.string.text_auto_operate_service_enabled_but_not_running);
            } else {
                if (Pref.def().getBoolean(App.getApp().getString(R.string.key_enable_accessibility_service_by_root), false)) {
                    if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(AccessibilityWatchDogService.class, 3000)) {
                        errorMessage = App.getApp().getString(R.string.text_enable_accessibility_service_by_root_timeout);
                    }
                } else {
                    errorMessage = App.getApp().getString(R.string.text_no_accessibility_permission);
                }
            }
            if (errorMessage != null) {
                toast(errorMessage);
                throw new ScriptStopException(errorMessage);
            }
        }
    }

    public void toast(final String text) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getApp(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ScriptStopException(e);
        }
    }

    public void addAccessibilityEventListener(final Object delegate) {
        if (delegate == null)
            return;

    }

    public Shell.CommandResult shell(String cmd, int root) {
        return Shell.execCommand(cmd, root != 0);
    }

    public String getPackageName() {
        ensureAccessibilityServiceEnable();
        return ActionPerformAccessibilityDelegate.getLatestPackage();
    }

    public String getActivityName() {
        ensureAccessibilityServiceEnable();
        return ActionPerformAccessibilityDelegate.getLatestActivity();
    }

    public UiSelector selector() {
        return new UiSelector();
    }

    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    public void stop() {
        Thread.interrupted();
    }

    public String readFile(String path) {
        return FileUtils.readString(new File(Environment.getExternalStorageDirectory() + "/" + path));
    }

    public ThemeColorMaterialDialogBuilder dialog() {
        if (App.currentActivity() == null) {
            Toast.makeText(App.getApp(), R.string.text_cannot_create_dialog_when_app_invisible, Toast.LENGTH_SHORT).show();
            throw new ScriptStopException(App.getApp().getString(R.string.text_cannot_create_dialog_when_app_invisible));
        }
        return new ThemeColorMaterialDialogBuilder(App.currentActivity()) {

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

    public void notifyActionPerformed() {
        synchronized (mActionPerformLock) {
            mActionPerformLock.notify();
        }
    }

}
