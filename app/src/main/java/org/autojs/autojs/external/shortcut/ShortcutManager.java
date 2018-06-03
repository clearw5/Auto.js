package org.autojs.autojs.external.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Collections;
import java.util.List;

/**
 * Created by Stardust on 2017/10/25.
 */

public class ShortcutManager {

    private static ShortcutManager sInstance;
    private Context mContext;
    private android.content.pm.ShortcutManager mShortcutManager;


    public ShortcutManager(Context context) {
        mContext = context;
        mShortcutManager = (android.content.pm.ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
    }

    public static ShortcutManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShortcutManager(context);
        }
        return sInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void addDynamicShortcut(CharSequence label, String id, Icon icon, Intent intent) {
        ShortcutInfo shortcut = new ShortcutInfo.Builder(mContext, id)
                .setIntent(intent)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(icon)
                .build();
        try {
            addDynamicShortcutUnchecked(shortcut);
        } catch (IllegalArgumentException shortcutsExceeded) {
            removeTheFirstShortcut();
            addDynamicShortcutUnchecked(shortcut);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void removeTheFirstShortcut() {
        List<ShortcutInfo> dynamicShortcuts = mShortcutManager.getDynamicShortcuts();
        mShortcutManager.removeDynamicShortcuts(Collections.singletonList(dynamicShortcuts.get(0).getId()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void addDynamicShortcutUnchecked(ShortcutInfo shortcut) {
        mShortcutManager.addDynamicShortcuts(Collections.singletonList(shortcut));
    }

}
