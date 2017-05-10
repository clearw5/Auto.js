package com.stardust.uiautomator;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Stardust on 2017/5/9.
 */

public class Test {


    private void runTests(Context context) {
        final String packageName = context.getPackageName();
        final List<InstrumentationInfo> list =
                context.getPackageManager().queryInstrumentation(packageName, 0);
        if (list.isEmpty()) {
            Toast.makeText(context, "Cannot find instrumentation for " + packageName,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final InstrumentationInfo instrumentationInfo = list.get(0);
        final ComponentName componentName =
                new ComponentName(instrumentationInfo.packageName,
                        instrumentationInfo.name);
        if (!context.startInstrumentation(componentName, null, null)) {
            Toast.makeText(context, "Cannot run instrumentation for " + packageName,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
