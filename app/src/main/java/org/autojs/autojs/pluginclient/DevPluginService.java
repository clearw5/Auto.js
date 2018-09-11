package org.autojs.autojs.pluginclient;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Pair;

import com.google.gson.JsonObject;

import org.autojs.autojs.tool.EmptyObservers;

import java.io.IOException;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/5/11.
 */

public class DevPluginService {

    public static class State {

        public static final int DISCONNECTED = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;

        private final int mState;
        private final Throwable mException;

        public State(int state, Throwable exception) {
            mState = state;
            mException = exception;
        }

        public State(int state) {
            this(state, null);
        }

        public int getState() {
            return mState;
        }

        public Throwable getException() {
            return mException;
        }
    }

    private static final int PORT = 9317;
    private static DevPluginService sInstance = new DevPluginService();
    private final PublishSubject<State> mConnectionState = PublishSubject.create();
    private volatile JsonSocket mSocket;

    public static DevPluginService getInstance() {
        return sInstance;
    }

    public boolean isConnected() {
        return mSocket != null && !mSocket.isClosed();
    }

    public boolean isDisconnected() {
        return mSocket == null || mSocket.isClosed();
    }

    public void disconnectIfNeeded() {
        if (isDisconnected())
            return;
        disconnect();
    }

    public void disconnect() {
        mSocket.close();
        mSocket = null;
    }

    public Observable<State> connectionState() {
        return mConnectionState;
    }

    public Observable<JsonSocket> connectToServer(String host) {
        int port = PORT;
        String ip = host;
        int i = host.lastIndexOf(':');
        if (i > 0 && i < host.length() - 1) {
            port = Integer.parseInt(host.substring(i + 1));
            ip = host.substring(0, i);
        }
        mConnectionState.onNext(new State(State.CONNECTING));
        return createSocket(ip, port)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(socket -> {
                    mSocket = socket;
                    mConnectionState.onNext(new State(State.CONNECTED));
                })
                .doOnError(e -> {
                    mConnectionState.onNext(new State(State.DISCONNECTED));
                    e.printStackTrace();
                });
    }

    private Observable<JsonSocket> createSocket(String ip, int port) {
        return Observable.fromCallable(() -> {
            JsonSocket jsonSocket = new JsonSocket(new Socket(ip, port));
            DevPluginResponseHandler handler = new DevPluginResponseHandler();
            jsonSocket.data()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> mConnectionState.onNext(new State(State.DISCONNECTED)))
                    .subscribe(data -> handler.handle(data.getAsJsonObject()), e -> {
                        e.printStackTrace();
                        mConnectionState.onNext(new State(State.DISCONNECTED));
                    });

            writePair(jsonSocket, "device_name", new Pair<>("device_name", Build.BRAND + " " + Build.MODEL));
            return jsonSocket;
        })
                .subscribeOn(Schedulers.io());
    }

    private static int write(JsonSocket socket, String type, JsonObject data) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.add("data", data);
        return socket.write(json);
    }

    private static int writePair(JsonSocket socket, String type, Pair<String, String> pair) throws IOException {
        JsonObject data = new JsonObject();
        data.addProperty(pair.first, pair.second);
        return write(socket, type, data);
    }


    @SuppressLint("CheckResult")
    public void log(String log) {
        if (!isConnected())
            return;
        Observable.fromCallable(() ->
                writePair(mSocket, "log", new Pair<>("log", log)))
                .subscribeOn(Schedulers.io())
                .subscribe(EmptyObservers.consumer(), Throwable::printStackTrace);
    }
}
