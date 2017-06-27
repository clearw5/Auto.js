package com.stardust.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

/**
 * Created by Stardust on 2017/6/27.
 */

public class MessageIntent extends Intent {

    private HashMap<String, Object> mObjectExtras;

    public MessageIntent() {
    }

    public MessageIntent(Intent o) {
        super(o);
    }

    public MessageIntent(String action) {
        super(action);
    }

    public MessageIntent(String action, Uri uri) {
        super(action, uri);
    }

    public MessageIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public MessageIntent(String action, Uri uri, Context packageContext, Class<?> cls) {
        super(action, uri, packageContext, cls);
    }

    public MessageIntent putExtra(String key, Object value) {
        if (mObjectExtras == null) {
            mObjectExtras = new HashMap<>();
        }
        mObjectExtras.put(key, value);
        return this;
    }

    public Object getObjectExtra(String key) {
        return mObjectExtras.get(key);
    }
}
