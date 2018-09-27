package org.autojs.autojs.external.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;

import org.autojs.autojs.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_PACKAGES_SUSPENDED;
import static android.content.Intent.ACTION_PACKAGES_UNSUSPENDED;
import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_CHANGED;
import static android.content.Intent.ACTION_PACKAGE_DATA_CLEARED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_RESTARTED;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static android.content.Intent.ACTION_SHUTDOWN;
import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static android.content.Intent.ACTION_TIME_TICK;
import static android.content.Intent.ACTION_UID_REMOVED;

public class DynamicBroadcastReceiver extends BaseBroadcastReceiver {

    private static final List<String> DEFAULT_ACTIONS = new ArrayList<>(Arrays.asList(
            ACTION_TIME_TICK,
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

    private boolean mRegistered = false;
    private final Set<String> mActions = new LinkedHashSet<>();
    private final Context mContext;


    public DynamicBroadcastReceiver(Context context) {
        mContext = context;
        register(DEFAULT_ACTIONS);
    }

    public void register(List<String> actions) {
        int oldSize = mActions.size();
        mActions.addAll(actions);
        if (oldSize == mActions.size()) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : mActions) {
            filter.addAction(action);
        }
        if(mRegistered){
            mContext.unregisterReceiver(this);
        }
        mContext.registerReceiver(this, filter);
        mRegistered = true;
    }

}
