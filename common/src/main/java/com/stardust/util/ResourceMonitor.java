package com.stardust.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.stardust.BuildConfig;

import java.util.concurrent.ConcurrentHashMap;


public final class ResourceMonitor {

    private static final String LOG_TAG = "ResourceMonitor";

    private static final ConcurrentHashMap<Class<?>, SparseArray<Exception>> mResources = new ConcurrentHashMap<>();
    private static Handler sHandler;
    private static boolean sEnabled = BuildConfig.DEBUG;
    private static ExceptionCreator sExceptionCreator;
    private static UnclosedResourceDetectedHandler sUnclosedResourceDetectedHandler;

    public static void setExceptionCreator(ExceptionCreator exceptionCreator) {
        sExceptionCreator = exceptionCreator;
    }

    public static void setUnclosedResourceDetectedHandler(UnclosedResourceDetectedHandler unclosedResourceDetectedHandler) {
        sUnclosedResourceDetectedHandler = unclosedResourceDetectedHandler;
    }

    public static void onOpen(ResourceMonitor.Resource resource) {
        if (!sEnabled) {
            return;
        }
        SparseArray<Exception> map = mResources.get(resource.getClass());
        if (map == null) {
            map = new SparseArray<>();
            mResources.put(resource.getClass(), map);
        }
        int resourceId = resource.getResourceId();
        Exception exception;
        if (sExceptionCreator == null) {
            exception = new ResourceMonitor.UnclosedResourceException(resource);
            exception.fillInStackTrace();
        } else {
            exception = sExceptionCreator.create(resource);
        }
        map.put(resourceId, exception);
    }

    public static void onClose(ResourceMonitor.Resource resource) {
        if (!sEnabled) {
            return;
        }
        SparseArray map = mResources.get(resource.getClass());
        if (map != null) {
            map.remove(resource.getResourceId());
        }
    }

    public static void onFinalize(ResourceMonitor.Resource resource) {
        if (!sEnabled) {
            return;
        }
        SparseArray<Exception> map = mResources.get(resource.getClass());
        if (map != null) {
            int indexOfKey = map.indexOfKey(resource.getResourceId());
            if (indexOfKey >= 0) {
                final Exception exception = map.valueAt(indexOfKey);
                map.removeAt(indexOfKey);
                if (sHandler == null) {
                    sHandler = new Handler(Looper.getMainLooper());
                }
                sHandler.post(new Runnable() {
                    public final void run() {
                        UnclosedResourceDetectedException detectedException = new UnclosedResourceDetectedException(exception);
                        detectedException.fillInStackTrace();
                        Log.w(LOG_TAG, "UnclosedResourceDetected", detectedException);
                        if (sUnclosedResourceDetectedHandler != null) {
                            sUnclosedResourceDetectedHandler.onUnclosedResourceDetected(detectedException);
                        } else {
                            throw detectedException;
                        }
                    }
                });
            }
        }
    }

    public static boolean isEnabled() {
        return sEnabled;
    }

    public static void setEnabled(boolean mEnabled) {
        ResourceMonitor.sEnabled = mEnabled;
    }

    public static final class UnclosedResourceException extends RuntimeException {
        public UnclosedResourceException(Resource resource) {
            super("id = " + resource.getResourceId() + ", resource = " + resource);
        }

    }


    public static final class UnclosedResourceDetectedException extends RuntimeException {
        public UnclosedResourceDetectedException(Throwable cause) {
            super(cause);
        }
    }

    public interface Resource {
        int getResourceId();
    }

    public interface ExceptionCreator {
        Exception create(Resource resource);
    }

    public interface UnclosedResourceDetectedHandler {

        void onUnclosedResourceDetected(UnclosedResourceDetectedException detectedException);
    }
}
