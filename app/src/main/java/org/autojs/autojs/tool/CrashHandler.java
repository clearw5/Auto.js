package org.autojs.autojs.tool;

/**
 * Created by Stardust on 2017/2/2.
 */


import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stardust.app.GlobalAppContext;
import org.autojs.autojs.App;
import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.R;
import com.stardust.util.IntentUtil;
import com.stardust.view.accessibility.AccessibilityService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static int crashCount = 0;
    private static long firstCrashMillis = 0;
    private final Class<?> mErrorReportClass;
    private UncaughtExceptionHandler mDefaultHandler;

    public CrashHandler(Class<?> errorReportClass) {
        this.mErrorReportClass = errorReportClass;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (thread != Looper.getMainLooper().getThread()) {
            Log.e(TAG, "Uncaught Exception", ex);
            return;
        }
        AccessibilityService service = AccessibilityService.getInstance();
        if (service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "disable service: " + service);
            service.disableSelf();
        } else {
            Log.d(TAG, "cannot disable service: " + service);

        }
        if (BuildConfig.DEBUG) {
            mDefaultHandler.uncaughtException(thread, ex);
            return;
        }
        if (causedByBadWindowToken(ex)) {
            Toast.makeText(GlobalAppContext.get(), R.string.text_no_floating_window_permission, Toast.LENGTH_SHORT).show();
            IntentUtil.goToAppDetailSettings(GlobalAppContext.get());
        } else {
            try {
                Log.e(TAG, "Uncaught Exception", ex);
                if (crashTooManyTimes())
                    return;
                String msg = GlobalAppContext.getString(R.string.sorry_for_crash) + ex.toString();
                startErrorReportActivity(msg, throwableToString(ex));
                System.exit(1);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static boolean causedByBadWindowToken(Throwable e) {
        while (e != null) {
            if (e instanceof WindowManager.BadTokenException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
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

    public static String throwableToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace();
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}