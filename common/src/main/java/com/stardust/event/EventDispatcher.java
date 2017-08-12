package com.stardust.event;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/8/6.
 */

public class EventDispatcher<Listener> {

    public interface Event<Listener> {
        void notify(Listener l);
    }

    private CopyOnWriteArrayList<Listener> mListeners = new CopyOnWriteArrayList<>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }


    public boolean removeListener(Listener l) {
        return mListeners.remove(l);
    }

    public void dispatchEvent(Event<Listener> event) {
        for (Listener listener : mListeners) {
            event.notify(listener);
        }
    }

}
