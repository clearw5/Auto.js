package com.stardust.scriptdroid.sublime;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/5/11.
 */

public class SublimePluginService {

    private static SublimePluginClient client;
    private static final PublishSubject<Boolean> CONNECTION_STATE = PublishSubject.create();

    public static boolean isConnected() {
        return client != null && client.isListening();
    }

    public static void disconnectIfNeeded() {
        if (!isConnected())
            return;
        disconnect();
    }

    public static void disconnect() {
        try {
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    public static PublishSubject<Boolean> getConnectionState() {
        return CONNECTION_STATE;
    }

    public static Observable<Void> connect(String host) {
        client = new SublimePluginClient(host, 1209, CONNECTION_STATE);
        client.setResponseHandler(new SublimeResponseHandler());
        return client.listen();
    }

    public static void log(String log) {
        if (!isConnected())
            return;
        JsonObject object = new JsonObject();
        object.addProperty("type", "log");
        object.addProperty("log", log);
        try {
            client.send(object);
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }
}
