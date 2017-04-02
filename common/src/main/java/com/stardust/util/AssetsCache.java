package com.stardust.util;

import android.content.res.AssetManager;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/3/14.
 */

public class AssetsCache {

    private Map<String, String> mCache = new HashMap<>();


    public String read(AssetManager manager, String path) throws IOException {
        String str = mCache.get(path);
        if (str == null) {
            str = FileUtils.readString(manager.open(path));
            mCache.put(path, str);
        }
        return str;
    }
}
