package org.autojs.autojs.external.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import org.autojs.autojs.timing.IntentTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DynamicBroadcastReceivers {

    public static final String ACTION_STARTUP = "org.autojs.autojs.action.startup";

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

    public void register(IntentTask task) {
        register(Collections.singletonList(task.getAction()), task.isLocal());
    }

    public synchronized void register(List<String> actions, boolean local) {
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
        ReceiverRegistry receiverRegistry = new ReceiverRegistry(newActions, local);
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
        boolean local;

        ReceiverRegistry(LinkedHashSet<String> actions, boolean local) {
            this.actions = actions;
            this.local = local;
            receiver = new BaseBroadcastReceiver();
        }

        void unregister() {
            if (local) {
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
                broadcastManager.unregisterReceiver(receiver);
            } else {
                mContext.unregisterReceiver(receiver);
            }
        }

        boolean register() {
            if (actions.isEmpty())
                return false;
            IntentFilter intentFilter = createIntentFilter(actions);
            if (local) {
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
                broadcastManager.registerReceiver(receiver, intentFilter);
            } else {
                mContext.registerReceiver(receiver, intentFilter);
            }
            Log.d(LOG_TAG, "register: " + actions);
            return true;
        }
    }
}
