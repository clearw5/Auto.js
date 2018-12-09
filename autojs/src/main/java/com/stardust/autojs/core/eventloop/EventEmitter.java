package com.stardust.autojs.core.eventloop;

import androidx.annotation.NonNull;


import com.stardust.autojs.core.looper.Timer;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.exception.ScriptException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/7/19.
 */

public class EventEmitter {

    private static class ListenerWrapper {
        Object listener;
        boolean isOnce;

        public ListenerWrapper(Object listener, boolean isOnce) {
            this.listener = listener;
            this.isOnce = isOnce;
        }
    }


    private class Listeners {
        private CopyOnWriteArrayList<ListenerWrapper> mListenerWrappers = new CopyOnWriteArrayList<>();

        void add(Object listener, boolean once) {
            ensureListenersNotAtLimit();
            mListenerWrappers.add(new ListenerWrapper(listener, once));
        }

        private void ensureListenersNotAtLimit() {
            if (mMaxListeners != 0 && mListenerWrappers.size() >= mMaxListeners) {
                throw new ScriptException(new TooManyListenersException("max = " + mMaxListeners));
            }
        }

        boolean empty() {
            return mListenerWrappers.isEmpty();
        }

        void emit(Object[] args) {
            Iterator<ListenerWrapper> listenerIterator = mListenerWrappers.iterator();
            while (listenerIterator.hasNext()) {
                ListenerWrapper listenerWrapper = listenerIterator.next();
                if (mTimer != null) {
                    mTimer.setImmediate(listenerWrapper.listener, args);
                } else {
                    mBridges.callFunction(listenerWrapper.listener, EventEmitter.this, args);
                }
                if (listenerWrapper.isOnce) {
                    mListenerWrappers.remove(listenerWrapper);
                }
            }
        }

        int count() {
            return mListenerWrappers.size();
        }

        Object[] toArray() {
            Iterator<ListenerWrapper> listenerIterator = mListenerWrappers.iterator();
            ArrayList<Object> listeners = new ArrayList<>(mListenerWrappers.size());
            while (listenerIterator.hasNext()) {
                listeners.add(listenerIterator.next().listener);
            }
            return listeners.toArray(new Object[listeners.size()]);
        }

        void prepend(Object listener, boolean once) {
            ensureListenersNotAtLimit();
            mListenerWrappers.add(0, new ListenerWrapper(listener, once));
        }

        void remove(Object listener) {
            Iterator<ListenerWrapper> listenerIterator = mListenerWrappers.iterator();
            while (listenerIterator.hasNext()) {
                ListenerWrapper l = listenerIterator.next();
                if (l.listener == listener) {
                    listenerIterator.remove();
                    break;
                }
            }
        }
    }

    private Map<String, Listeners> mListenersMap = new HashMap<>();
    public static int defaultMaxListeners = 10;
    private int mMaxListeners = defaultMaxListeners;
    protected ScriptBridges mBridges;
    private Timer mTimer;

    public EventEmitter(ScriptBridges bridges) {
        mBridges = bridges;
    }

    public EventEmitter(ScriptBridges bridges, Timer timer) {
        mTimer = timer;
        mBridges = bridges;
    }

    public EventEmitter once(String eventName, Object listener) {
        getListeners(eventName).add(listener, true);
        return this;
    }
    @NonNull
    private Listeners getListeners(String eventName) {
        Listeners listeners = mListenersMap.get(eventName);
        if (listeners == null) {
            listeners = new Listeners();
            mListenersMap.put(eventName, listeners);
        }
        return listeners;
    }


    public EventEmitter on(String eventName, Object listener) {
        getListeners(eventName).add(listener, false);
        return this;
    }

    public EventEmitter addListener(String eventName, Object listener) {
        return on(eventName, listener);
    }

    public boolean emit(String eventName, Object... args) {
        Listeners listeners = mListenersMap.get(eventName);
        if (listeners == null || listeners.empty())
            return false;
        listeners.emit(args);
        return true;
    }

    public String[] eventNames() {
        return mListenersMap.keySet().toArray(new String[0]);
    }

    public int listenerCount(String eventName) {
        Listeners listeners = mListenersMap.get(eventName);
        if (listeners == null)
            return 0;
        return listeners.count();
    }

    public Object[] listeners(String eventName) {
        return getListeners(eventName).toArray();
    }

    public EventEmitter prependListener(String eventName, Object listener) {
        getListeners(eventName).prepend(listener, false);
        return this;
    }

    public EventEmitter prependOnceListener(String eventName, Object listener) {
        getListeners(eventName).prepend(listener, true);
        return this;
    }

    public EventEmitter removeAllListeners() {
        mListenersMap.clear();
        return this;
    }

    public EventEmitter removeAllListeners(String eventName) {
        mListenersMap.remove(eventName);
        return this;
    }

    public EventEmitter removeListener(String eventName, Object listener) {
        Listeners listeners = mListenersMap.get(eventName);
        if (listeners != null)
            listeners.remove(listener);
        return this;
    }

    public EventEmitter setMaxListeners(int n) {
        mMaxListeners = n;
        return this;
    }

    public int getMaxListeners() {
        return mMaxListeners;
    }

    public static int defaultMaxListeners() {
        return defaultMaxListeners;
    }


}
