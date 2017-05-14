package com.stardust.scriptdroid.sublime_plugin_client;

import com.google.gson.JsonObject;
import com.stardust.scriptdroid.autojs.AutoJs;

import java.io.IOException;

/**
 * Created by Stardust on 2017/5/11.
 */

public class SublimePluginService {

    private static SublimePluginClient client;

    public static boolean isConnected() {
        return client != null;
    }

    public static void disconnectIfNeeded() {
        if (!isConnected())
            return;
        disconnect();
    }

    public static void disconnect() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    public static void connect(String host) {
        client = new SublimePluginClient(host, 1209);
        client.setResponseHandler(new SublimeResponseHandler());
        client.listen();
    }

    public static void log(String log) {
        if (!isConnected())
            return;
        JsonObject object = new JsonObject();
        object.addProperty("type", "log");
        object.addProperty("log", log);
        client.send(object);
    }
}
