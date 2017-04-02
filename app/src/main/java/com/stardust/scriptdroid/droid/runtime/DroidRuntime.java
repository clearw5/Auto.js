package com.stardust.scriptdroid.droid.runtime;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jraska.console.timber.ConsoleTree;
import com.stardust.automator.AccessibilityEventCommandHost;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.accessibility.AccessibilityInfoProvider;
import com.stardust.scriptdroid.droid.runtime.action.ActionTarget;
import com.stardust.scriptdroid.droid.runtime.api.UiSelector;
import com.stardust.scriptdroid.tool.AccessibilityServiceTool;
import com.stardust.scriptdroid.service.AccessibilityWatchDogService;
import com.stardust.scriptdroid.tool.IntentTool;
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

    static {
        Timber.plant(new ConsoleTree.Builder()
                .minPriority(Log.VERBOSE)
                .verboseColor(0xff909090)
                .debugColor(0xffc88b48)
                .infoColor(0xffc9c9c9)
                .warnColor(0xffa97db6)
                .errorColor(0xffff534e)
                .assertColor(0xffff5540)
                .build());
    }

    private static class PerformGlobalActionCommand implements AccessibilityEventCommandHost.Command {

        boolean result;
        private int mGlobalAction;

        PerformGlobalActionCommand(int globalAction) {
            mGlobalAction = globalAction;
        }

        @Override
        public void execute(AccessibilityService service, AccessibilityEvent event) {
            result = service.performGlobalAction(mGlobalAction);
        }
    }

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

    public boolean launchPackage(String packageName) {
        try {
            PackageManager packageManager = App.getApp().getPackageManager();
            App.getApp().startActivity(packageManager.getLaunchIntentForPackage(packageName));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean launchApp(String appName) {
        return launchPackage(getPackageName(appName));
    }

    public String getPackageName(String appName) {
        PackageManager packageManager = App.getApp().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString().equals(appName)) {
                return applicationInfo.processName;
            }
        }
        return "";
    }

    public boolean openAppSetting(String packageName) {
        return IntentTool.goToAppSetting(App.getApp(), packageName);
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

    public boolean back() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public boolean home() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public boolean powerDialog() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
    }

    public boolean notifications() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    public boolean quickSettings() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
    }

    public boolean recents() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    public boolean splitScreen() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
    }

    public boolean swipeDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN);
    }

    public boolean swipeDownLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT);
    }

    public boolean swipeDownRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT);
    }

    public boolean swipeDownUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_DOWN_AND_UP);
    }

    public boolean swipeUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP);
    }

    public boolean swipeUpLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT);
    }

    public boolean swipeUpRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT);
    }

    public boolean swipeUpDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN);
    }

    public boolean swipeLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);
    }

    public boolean swipeLeftRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_RIGHT);
    }

    public boolean swipeLeftUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP);
    }

    public boolean swipeLeftDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT_AND_DOWN);
    }

    public boolean swipeRight() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT);
    }

    public boolean swipeRightLeft() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_LEFT);
    }

    public boolean swipeRightUp() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP);
    }

    public boolean swipeRightDown() {
        return performGlobalAction(AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN);
    }

    private boolean performGlobalAction(final int action) {
        ensureAccessibilityServiceEnabled();
        PerformGlobalActionCommand command = new PerformGlobalActionCommand(action);
        AccessibilityEventCommandHost.getInstance().executeAndWaitForEvent(command);
        return command.result;
    }

    public boolean paste(ActionTarget target) {
        return performAction(target.createAction(AccessibilityNodeInfo.ACTION_PASTE));
    }

    public void log(@Nullable Object o) {
        String str = o + "";
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Console.writeLine(spannableString);
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
        ensureAccessibilityServiceEnabled();
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

    public void ensureAccessibilityServiceEnabled() {
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

    public String currentPackage() {
        ensureAccessibilityServiceEnabled();
        return AccessibilityInfoProvider.getInstance().getLatestPackage();
    }

    public String currentActivity() {
        ensureAccessibilityServiceEnabled();
        return AccessibilityInfoProvider.getInstance().getLatestActivity();
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
