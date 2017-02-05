package com.stardust.util;

import android.content.SharedPreferences;
import android.support.v7.widget.SwitchCompat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Stardust on 2017/2/3.
 */

public class StateObserver {

    public interface OnStateChangedListener<T> {

        void onStateChanged(T newState);

        void initState(T state);
    }


    public static abstract class SimpleOnStateChangedListener<T> implements OnStateChangedListener<T> {

        @Override
        public void initState(T state) {
            onStateChanged(state);
        }
    }

    public interface OnBooleanStateChangedListener extends OnStateChangedListener<Boolean> {

    }

    public static abstract class SimpleOnBooleanStateChangedListener implements OnBooleanStateChangedListener {

        @Override
        public void initState(Boolean state) {
            onStateChanged(state);
        }
    }


    private final Map<String, List<OnStateChangedListener>> mKeyStateListenersMap = new HashMap<>();
    private SharedPreferences mSharedPreferences;

    public StateObserver(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void register(final String key, SwitchCompat switchCompat) {
        final WeakReference<SwitchCompat> switchCompatWeakReference = new WeakReference<>(switchCompat);
        register(key, new SimpleOnBooleanStateChangedListener() {
            @Override
            public void onStateChanged(Boolean newState) {
                if (switchCompatWeakReference.get() != null) {
                    switchCompatWeakReference.get().setChecked(newState);
                } else {
                    unregister(key, this);
                }
            }
        });
    }

    public <T> void register(String key, OnStateChangedListener<T> listener) {
        T initialState = readState(key, listener);
        if (initialState != null)
            listener.initState(initialState);
        synchronized (mKeyStateListenersMap) {
            getListenerListOrCreateIfNotExists(key).add(listener);
        }
    }


    private <T> void unregister(String key, OnStateChangedListener<T> stateChangedListener) {
        synchronized (mKeyStateListenersMap) {
            List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
            if (listeners == null) {
                return;
            }
            listeners.remove(stateChangedListener);
        }
    }

    public <T> void setState(String key, T state) {
        synchronized (mKeyStateListenersMap) {
            List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
            if (listeners == null || listeners.isEmpty()) {
                return;
            }
            if (listeners.get(0) instanceof OnBooleanStateChangedListener) {
                mSharedPreferences.edit().putBoolean(key, (Boolean) state).apply();
                notifyBooleanStateChanged(listeners, (Boolean) state);
            }
        }
    }

    private void notifyBooleanStateChanged(List<OnStateChangedListener> listeners, boolean state) {
        for (OnStateChangedListener listener : listeners) {
            listener.onStateChanged(state);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T readState(String key, OnStateChangedListener<T> listener) {
        if (listener instanceof OnBooleanStateChangedListener) {
            return mSharedPreferences.contains(key) ? (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false)) : null;
        }
        return null;
    }

    private List<OnStateChangedListener> getListenerListOrCreateIfNotExists(String key) {
        List<OnStateChangedListener> listeners = mKeyStateListenersMap.get(key);
        if (listeners == null) {
            listeners = new Vector<>();
            mKeyStateListenersMap.put(key, listeners);
        }
        return listeners;
    }


}
