package org.autojs.autojs.external.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static android.content.Intent.ACTION_PACKAGES_SUSPENDED;
import static android.content.Intent.ACTION_PACKAGES_UNSUSPENDED;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;

public class DynamicBroadcastReceivers {

    private static final String LOG_TAG = "DynBroadcastReceivers";

    private final Set<String> mActions = new LinkedHashSet<>();
    private final List<ReceiverRegistry> mReceiverRegistries = new ArrayList<>();
    private final BaseBroadcastReceiver mDefaultActionReceiver = new BaseBroadcastReceiver();
    private final BaseBroadcastReceiver mPackageActionReceiver = new BaseBroadcastReceiver();
    private final Context mContext;


    public DynamicBroadcastReceivers(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.registerReceiver(mDefaultActionReceiver, createIntentFilter(StaticBroadcastReceiver.ACTIONS));
            IntentFilter filter = createIntentFilter(StaticBroadcastReceiver.PACKAGE_ACTIONS);
            filter.addDataScheme("package");
            mContext.registerReceiver(mPackageActionReceiver, filter);
        }


    }

    public void register(String action) {
        register(Collections.singletonList(action));
    }

    public synchronized void register(List<String> actions) {
        LinkedHashSet<String> newActions = new LinkedHashSet<>();
        for (String action : actions) {
            if (!StaticBroadcastReceiver.ACTIONS.contains(action)
                    && !StaticBroadcastReceiver.PACKAGE_ACTIONS.contains(action)
                    && !mActions.contains(action)) {
                newActions.add(action);
            }
        }
        if (newActions.isEmpty()) {
            return;
        }
        ReceiverRegistry receiverRegistry = new ReceiverRegistry(newActions);
        receiverRegistry.register();
        mReceiverRegistries.add(receiverRegistry);
    }

    public synchronized void unregister(String action) {
        if (!mActions.contains(action)) {
            return;
        }
        mActions.remove(action);
        Iterator<ReceiverRegistry> iterator = mReceiverRegistries.iterator();
        while (iterator.hasNext()) {
            ReceiverRegistry receiverRegistry = iterator.next();
            if (!receiverRegistry.actions.contains(action)) {
                continue;
            }
            receiverRegistry.actions.remove(action);
            receiverRegistry.unregister();
            if (!receiverRegistry.register()) {
                iterator.remove();
            }
            break;
        }
    }

    public synchronized void unregisterAll() {
        for (ReceiverRegistry registry : mReceiverRegistries) {
            registry.unregister();
        }
        mReceiverRegistries.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.unregisterReceiver(mDefaultActionReceiver);
            mContext.unregisterReceiver(mPackageActionReceiver);
        }
    }

    static IntentFilter createIntentFilter(Collection<String> actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        return filter;
    }

    private class ReceiverRegistry {
        BroadcastReceiver receiver;
        LinkedHashSet<String> actions;

        ReceiverRegistry(LinkedHashSet<String> actions) {
            this.actions = actions;
            receiver = new BaseBroadcastReceiver();
        }

        void unregister() {
            mContext.unregisterReceiver(receiver);
        }

        boolean register() {
            if (actions.isEmpty())
                return false;
            IntentFilter intentFilter = createIntentFilter(actions);
            mContext.registerReceiver(receiver, intentFilter);
            Log.d(LOG_TAG, "register: " + actions);
            return true;
        }
    }
}
