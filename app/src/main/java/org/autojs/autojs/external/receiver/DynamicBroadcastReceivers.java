package org.autojs.autojs.external.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static android.content.Intent.ACTION_PACKAGES_SUSPENDED;
import static android.content.Intent.ACTION_PACKAGES_UNSUSPENDED;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_TIME_TICK;

public class DynamicBroadcastReceivers {

    private static final List<String> DEFAULT_ACTIONS = new ArrayList<>(Arrays.asList(
            ACTION_TIME_TICK,
            ACTION_SCREEN_OFF,
            ACTION_SCREEN_ON,
            ACTION_BATTERY_CHANGED,
            ACTION_CONFIGURATION_CHANGED
    ));

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DEFAULT_ACTIONS.addAll(Arrays.asList(
                    ACTION_PACKAGES_SUSPENDED,
                    ACTION_PACKAGES_UNSUSPENDED
            ));
        }
    }

    private final Set<String> mActions = new LinkedHashSet<>();
    private final List<BroadcastReceiver> mReceivers = new ArrayList<>();
    private final Context mContext;


    public DynamicBroadcastReceivers(Context context) {
        mContext = context;
        register(DEFAULT_ACTIONS);
    }

    public void register(String action) {
        register(Collections.singletonList(action));
    }

    public void register(List<String> actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            if (!StaticBroadcastReceiver.ACTIONS.contains(action)
                    && !mActions.contains(action)) {
                mActions.add(action);
                filter.addAction(action);
            }
        }
        if (filter.countActions() == 0) {
            return;
        }
        BaseBroadcastReceiver receiver = new BaseBroadcastReceiver();
        mContext.registerReceiver(receiver, filter);
    }

    public void unregisterAll() {
        for (BroadcastReceiver receiver : mReceivers) {
            mContext.unregisterReceiver(receiver);
        }
        mReceivers.clear();
    }
}
