package org.autojs.autojs.tool;

/**
 * Created by Stardust on 2017/2/2.
 */


import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.BuildConfig;
import org.mozilla.javascript.RhinoException;

import com.stardust.view.accessibility.AccessibilityService;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

public class CrashHandler extends CrashReport.CrashHandleCallback implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static int crashCount = 0;
    private static long firstCrashMillis = 0;
    private final Class<?> mErrorReportClass;
    private UncaughtExceptionHandler mBuglyHandler;
    private UncaughtExceptionHandler mSystemHandler;

    public CrashHandler(Class<?> errorReportClass) {
        this.mErrorReportClass = errorReportClass;
        mSystemHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void setBuglyHandler(UncaughtExceptionHandler buglyHandler) {
        mBuglyHandler = buglyHandler;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "Uncaught Exception", ex);
        if (thread != Looper.getMainLooper().getThread()) {
            if(!(ex instanceof RhinoException)){
                CrashReport.postCatchedException(ex, thread);
            }
            return;
        }
        AccessibilityService service = AccessibilityService.Companion.getInstance();
        if (service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "disable service: " + service);
            service.disableSelf();
        } else {
            BuglyLog.d(TAG, "cannot disable service: " + service);
        }
        if (BuildConfig.DEBUG) {
            mSystemHandler.uncaughtException(thread, ex);
        } else {
            mBuglyHandler.uncaughtException(thread, ex);
        }
    }

    @Override
    public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                               String errorMessage, String errorStack) {
        Log.d(TAG, "onCrashHandleStart: crashType = " + crashType + ", errorType = " + errorType + ", msg = "
                + errorMessage + ", stack = " + errorStack);
        try {
            if (crashTooManyTimes())
                return super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack);
            String msg = errorType + ": " + errorMessage;
            startErrorReportActivity(msg, errorStack);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack);
    }

    private void startErrorReportActivity(String msg, String detail) {
        Intent intent = new Intent(GlobalAppContext.get(), this.mErrorReportClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", msg);
        intent.putExtra("error", detail);
        GlobalAppContext.get().startActivity(intent);
    }

    private boolean crashTooManyTimes() {
        if (crashIntervalTooLong()) {
            resetCrashCount();
            return false;
        }
        crashCount++;
        return crashCount >= 5;
    }

    private void resetCrashCount() {
        firstCrashMillis = System.currentTimeMillis();
        crashCount = 0;
    }

    private boolean crashIntervalTooLong() {
        return System.currentTimeMillis() - firstCrashMillis > 3000;
    }


}