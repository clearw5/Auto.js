package com.stardust.util;

import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/2/3.
 */

public class StateObserver {

    public interface OnStateChangedListener {

        void onStateChanged(boolean newState);

        void initState(boolean state);
    }


    public static abstract class SimpleOnStateChangedListener<T> implements OnStateChangedListener {

        @Override
        public void initState(boolean state) {
            onStateChanged(state);
        }
    }


    private final Map<String, List<OnStateChangedListener>> mKeyStateListenersMap = new HashMap<>();
    private SharedPreferences mSharedPreferences;

    public StateObserver(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }


    public void register(String key, OnStateChangedListener listener) {
        initState(key, listener);
        synchronized (mKeyStateListenersMap) {
            List<OnStateChangedListener> listeners = getListenerListOrCreateIfNotExists(key);
            listeners.add(listener);
        }
    }


    private void unregister(String key, OnStateChangedListener stateChangedListener) {
        synchronized (mKeyStateListenersMap) {
            List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
            if (listeners == null) {
                return;
            }
            listeners.remove(stateChangedListener);
        }
    }

    public void setState(String key, boolean state) {
        synchronized (mKeyStateListenersMap) {
            List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
            if (listeners == null || listeners.isEmpty())
                return;
            mSharedPreferences.edit().putBoolean(key, state).apply();
            notifyBooleanStateChanged(listeners, state);
        }
    }

    private void notifyBooleanStateChanged(List<OnStateChangedListener> listeners, boolean state) {
        for (OnStateChangedListener listener : listeners) {
            listener.onStateChanged(state);
        }
    }

    private void initState(String key, OnStateChangedListener listener) {
        if (mSharedPreferences.contains(key)) {
            listener.initState(mSharedPreferences.getBoolean(key, false));
        }
    }

    private List<OnStateChangedListener> getListenerListOrCreateIfNotExists(String key) {
        List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            mKeyStateListenersMap.put(key, listeners);
        }
        return listeners;
    }


}
