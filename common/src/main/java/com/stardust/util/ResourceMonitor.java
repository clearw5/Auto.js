package com.stardust.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.stardust.BuildConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public final class ResourceMonitor {

    private static final String LOG_TAG = "ResourceMonitor";

    private static class LockedResource {
        private ReentrantLock lock;
        private SparseArray<Exception> resource;

        public LockedResource() {
            this.resource = new SparseArray<>();
            this.lock = new ReentrantLock();
        }

        public LockedResource(SparseArray<Exception> resource) {
            this.resource = resource;
            this.lock = new ReentrantLock();
        }

        public ReentrantLock getLock() {
            return lock;
        }

        public void setLock(ReentrantLock lock) {
            this.lock = lock;
        }

        public SparseArray<Exception> getResource() {
            return resource;
        }

        public void setResource(SparseArray<Exception> resource) {
            this.resource = resource;
        }
    }

    private static final ConcurrentHashMap<Class<?>, LockedResource> mResources = new ConcurrentHashMap<>();
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
        LockedResource map = mResources.get(resource.getClass());
        if (map == null) {
            map = new LockedResource();
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
        map.getLock().lock();
        map.getResource().put(resourceId, exception);
        map.getLock().unlock();
    }

    public static void onClose(ResourceMonitor.Resource resource) {
        if (!sEnabled) {
            return;
        }
        LockedResource map = mResources.get(resource.getClass());
        if (map != null) {
            map.getLock().lock();
            map.getResource().remove(resource.getResourceId());
            map.getLock().unlock();
        }
    }

    public static void onFinalize(ResourceMonitor.Resource resource) {
        if (!sEnabled) {
            return;
        }
        LockedResource map = mResources.get(resource.getClass());
        if (map != null) {
            map.getLock().lock();
            int indexOfKey = map.getResource().indexOfKey(resource.getResourceId());
            if (indexOfKey >= 0) {
                final Exception exception = map.getResource().valueAt(indexOfKey);
                map.getResource().removeAt(indexOfKey);
                map.getLock().unlock();
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
            } else {
                map.getLock().unlock();
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
