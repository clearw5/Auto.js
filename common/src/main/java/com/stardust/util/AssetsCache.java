package com.stardust.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.app.FragmentActivity;


import com.stardust.pio.PFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/14.
 */

public class AssetsCache {

    private static final long PERSIST_TIME = 5 * 60 * 1000;

    private static SimpleCache<String> cache = new SimpleCache<>(PERSIST_TIME, 5, 30 * 1000);

    public static String get(final AssetManager assetManager, final String path) {
        return cache.get(path, new SimpleCache.Supplier<String>() {
            @Override
            public String get(String key) {
                return PFile.readAsset(assetManager, path);
            }
        });
    }

    public static String get(final Activity activity, final String path) {
        return get(activity.getAssets(), path);
    }
}
