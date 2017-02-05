package com.stardust.scriptdroid.droid.runtime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.action.Action;
import com.stardust.scriptdroid.droid.runtime.action.ActionFactory;
import com.stardust.scriptdroid.droid.runtime.action.ActionPerformService;
import com.stardust.scriptdroid.droid.runtime.action.ActionTarget;
import com.stardust.scriptdroid.droid.runtime.api.IDroidRuntime;

import java.util.List;

/**
 * Created by Stardust on 2017/1/27.
 */

public class DroidRuntime implements IDroidRuntime {

    private static final String TAG = "DroidRuntime";
    private static DroidRuntime runtime = new DroidRuntime();
    private static Context context;

    private final Object mLock = new Object();
    private boolean mActionPerformResult;
    private boolean mActionPerformResultUpToDate;
    private Handler mUIHandler;

    public static DroidRuntime getRuntime() {
        return runtime;
    }

    public static void setContext(Context context) {
        DroidRuntime.context = context;
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
        App.getApp().startActivity(new Intent().setClassName(packageName, className));
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

    public boolean scrollAllUp() {
        return performAction(ActionFactory.createScrollAllAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD));
    }

    public boolean scrollAllDown() {
        return performAction(ActionFactory.createScrollAllAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD));
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


    private boolean performAction(Action action) {
        if (ActionPerformService.getInstance() == null) {
            toast(App.getApp().getString(R.string.text_no_accessibility_permission));
            throw new PermissionDeniedException(App.getApp().getString(R.string.text_no_accessibility_permission));
        }
        ensureNotStopped();
        ActionPerformService.setAction(action);
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                throw new PermissionDeniedException("已停止运行");
            }
        }
        return mActionPerformResult;
    }

    private void ensureNotStopped() {
        Droid.getInstance().ensureNotStopped();
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
            e.printStackTrace();
        }
    }

    @Override
    public MaterialDialog.Builder dialog() {
        return new Builder();
    }

    public void notifyActionPerformed(boolean succeed) {
        mActionPerformResult = succeed;
        synchronized (mLock) {
            mLock.notify();
        }
    }

    public class Builder extends MaterialDialog.Builder {

        public Builder() {
            super(DroidRuntime.context);
        }

        public MaterialDialog show(final MaterialDialog.Builder dialog) {

            return null;
        }
    }

}
