package com.stardust.autojs.core.boardcast;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2018/4/1.
 */

public class Broadcast {

    private static CopyOnWriteArrayList<BroadcastEmitter> sEventEmitters = new CopyOnWriteArrayList<>();

    public static void registerListener(BroadcastEmitter eventEmitter) {
        sEventEmitters.add(eventEmitter);
    }

    public static boolean unregisterListener(BroadcastEmitter eventEmitter) {
        return sEventEmitters.remove(eventEmitter);
    }

    public static void send(String eventName, Object[] args) {
        for (BroadcastEmitter emitter : sEventEmitters) {
            emitter.onBroadcast(eventName, args);
        }
    }

}
