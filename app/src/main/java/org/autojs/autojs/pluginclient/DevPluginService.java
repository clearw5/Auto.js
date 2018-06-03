package org.autojs.autojs.pluginclient;

import com.google.gson.JsonObject;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/5/11.
 */

public class DevPluginService {

    private static DevPluginService sInstance = new DevPluginService();
    private DevPluginClient mClient;
    private final PublishSubject<DevPluginClient.State> mConnection = PublishSubject.create();

    public static DevPluginService getInstance() {
        return sInstance;
    }

    public boolean isConnected() {
        return mClient != null && mClient.getState() == DevPluginClient.State.CONNECTED;
    }

    public boolean isDisconnected() {
        return mClient == null || mClient.getState() == DevPluginClient.State.DISCONNECTED;
    }

    public void disconnectIfNeeded() {
        if (isDisconnected())
            return;
        disconnect();
    }

    public void disconnect() {
        mClient.close();
        mClient = null;
        mConnection.onNext(new DevPluginClient.State(DevPluginClient.State.DISCONNECTED));
    }

    public PublishSubject<DevPluginClient.State> getConnection() {
        return mConnection;
    }

    public void connectToServer(String host) {
        int port = 1209;
        String ip = host;
        int i = host.lastIndexOf(':');
        if (i > 0 && i < host.length() - 1) {
            port = Integer.parseInt(host.substring(i + 1));
            ip = host.substring(0, i);
        }
        mClient = new DevPluginClient(ip, port, mConnection);
        mClient.setResponseHandler(new DevPluginResponseHandler());
        mClient.connectToServer();
    }

    public void log(String log) {
        if (!isConnected())
            return;
        JsonObject object = new JsonObject();
        object.addProperty("type", "log");
        object.addProperty("log", log);
        mClient.send(object)
                .subscribeOn(Schedulers.io())
                .subscribe(ignored -> {
                }, Throwable::printStackTrace);
    }
}
