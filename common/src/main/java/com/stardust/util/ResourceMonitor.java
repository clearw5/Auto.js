package com.stardust.util;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.stardust.BuildConfig;

import java.util.concurrent.ConcurrentHashMap;


public final class ResourceMonitor {

    private static final String LOG_TAG = "ResourceMonitor";

    private static final ConcurrentHashMap<Class<?>, SparseArray<UnclosedResourceException>> mResources;
    private static Handler mHandler;
    private static boolean mEnabled;

    public static void onOpen(ResourceMonitor.Resource resource) {
        if (!mEnabled) {
            return;
        }
        SparseArray<UnclosedResourceException> map = mResources.get(resource.getClass());
        if (map == null) {
            map = new SparseArray<>();
            mResources.put(resource.getClass(), map);
        }
        int resourceId = resource.getResourceId();
        ResourceMonitor.UnclosedResourceException exception = new ResourceMonitor.UnclosedResourceException("id = " + resourceId + ", resource = " + resource);
        exception.fillInStackTrace();
        map.put(resourceId, exception);
    }

    public static void onClose(ResourceMonitor.Resource resource) {
        if (!mEnabled) {
            return;
        }
        SparseArray map = mResources.get(resource.getClass());
        if (map != null) {
            map.remove(resource.getResourceId());
        }
    }

    public static void onFinalize(ResourceMonitor.Resource resource) {
        if (!mEnabled) {
            return;
        }
        SparseArray<UnclosedResourceException> map = mResources.get(resource.getClass());
        if (map != null) {
            int indexOfKey = map.indexOfKey(resource.getResourceId());
            if (indexOfKey >= 0) {
                final ResourceMonitor.UnclosedResourceException unclosedResourceException = map.valueAt(indexOfKey);
                map.removeAt(indexOfKey);
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
                mHandler.post(new Runnable() {
                    public final void run() {
                        throw new UnclosedResourceDetectedException(unclosedResourceException);
                    }
                });
            }
        }
    }

    static {
        mResources = new ConcurrentHashMap<>();
        mEnabled = BuildConfig.DEBUG;
    }

    public static boolean isEnabled() {
        return mEnabled;
    }

    public static void setEnabled(boolean mEnabled) {
        ResourceMonitor.mEnabled = mEnabled;
    }

    public static final class UnclosedResourceException extends RuntimeException {
        public UnclosedResourceException(String message) {
            super(message);
        }
    }


    public static final class UnclosedResourceDetectedException extends RuntimeException {
        public UnclosedResourceDetectedException(ResourceMonitor.UnclosedResourceException cause) {
            super(cause);
        }
    }

    public interface Resource {
        int getResourceId();
    }
}
