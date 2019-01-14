package org.autojs.autojs.pluginclient;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.stardust.app.GlobalAppContext;
import com.stardust.util.MapBuilder;

import org.autojs.autojs.BuildConfig;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Stardust on 2017/5/11.
 */

public class DevPluginService {

    private static final int CLIENT_VERSION = 2;
    private static final String LOG_TAG = "DevPluginService";
    private static final String TYPE_HELLO = "hello";
    private static final String TYPE_BYTES_COMMAND = "bytes_command";
    private static final long HANDSHAKE_TIMEOUT = 10 * 1000;

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
    private final DevPluginResponseHandler mResponseHandler;
    private final HashMap<String, JsonWebSocket.Bytes> mBytes = new HashMap<>();
    private final HashMap<String, JsonObject> mRequiredBytesCommands = new HashMap<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile JsonWebSocket mSocket;

    public static DevPluginService getInstance() {
        return sInstance;
    }

    public DevPluginService() {
        File cache = new File(GlobalAppContext.get().getCacheDir(), "remote_project");
        mResponseHandler = new DevPluginResponseHandler(cache);
    }

    @AnyThread
    public boolean isConnected() {
        return mSocket != null && !mSocket.isClosed();
    }

    @AnyThread
    public boolean isDisconnected() {
        return mSocket == null || mSocket.isClosed();
    }

    @AnyThread
    public void disconnectIfNeeded() {
        if (isDisconnected())
            return;
        disconnect();
    }

    @AnyThread
    public void disconnect() {
        mSocket.close();
        mSocket = null;
    }

    public Observable<State> connectionState() {
        return mConnectionState;
    }

    @AnyThread
    public Observable<JsonWebSocket> connectToServer(String host) {
        int port = PORT;
        String ip = host;
        int i = host.lastIndexOf(':');
        if (i > 0 && i < host.length() - 1) {
            port = Integer.parseInt(host.substring(i + 1));
            ip = host.substring(0, i);
        }
        mConnectionState.onNext(new State(State.CONNECTING));

        return socket(ip, port)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::onSocketError);
    }

    @AnyThread
    private Observable<JsonWebSocket> socket(String ip, int port) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        String url = ip + ":" + port;
        if (!url.startsWith("ws://") && !url.startsWith("wss://")) {
            url = "ws://" + url;
        }
        return Observable.just(new JsonWebSocket(client, new Request.Builder()
                .url(url)
                .build()))
                .doOnNext(socket -> {
                    mSocket = socket;
                    subscribeMessage(socket);
                    sayHelloToServer(socket);
                });
    }

    @SuppressLint("CheckResult")
    private void subscribeMessage(JsonWebSocket socket) {
        socket.data()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> mConnectionState.onNext(new State(State.DISCONNECTED)))
                .subscribe(data -> onSocketData(socket, data), this::onSocketError);
        socket.bytes()
                .doOnComplete(() -> mConnectionState.onNext(new State(State.DISCONNECTED)))
                .subscribe(data -> onSocketData(socket, data), this::onSocketError);
    }

    @MainThread
    private void onSocketError(Throwable e) {
        e.printStackTrace();
        if (mSocket != null) {
            mConnectionState.onNext(new State(State.DISCONNECTED, e));
            mSocket.close();
            mSocket = null;
        }
    }

    @MainThread
    private void onSocketData(JsonWebSocket jsonWebSocket, JsonElement element) {
        if (!element.isJsonObject()) {
            Log.w(LOG_TAG, "onSocketData: not json object: " + element);
            return;
        }
        try {
            JsonObject obj = element.getAsJsonObject();
            JsonElement typeElement = obj.get("type");
            if (typeElement == null || !typeElement.isJsonPrimitive()) {
                return;
            }
            String type = typeElement.getAsString();
            if (type.equals(TYPE_HELLO)) {
                onServerHello(jsonWebSocket, obj);
                return;
            }
            if (TYPE_BYTES_COMMAND.equals(type)) {
                String md5 = obj.get("md5").getAsString();
                JsonWebSocket.Bytes bytes = mBytes.remove(md5);
                if (bytes != null) {
                    handleBytes(obj, bytes);
                } else {
                    mRequiredBytesCommands.put(md5, obj);
                }
                return;
            }
            mResponseHandler.handle(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("CheckResult")
    private void handleBytes(JsonObject obj, JsonWebSocket.Bytes bytes) {
        mResponseHandler.handleBytes(obj, bytes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dir -> {
                    obj.get("data").getAsJsonObject().add("dir", new JsonPrimitive(dir.getPath()));
                    mResponseHandler.handle(obj);
                });

    }

    @WorkerThread
    private void onSocketData(JsonWebSocket jsonWebSocket, JsonWebSocket.Bytes bytes) {
        JsonObject command = mRequiredBytesCommands.remove(bytes.md5);
        if (command != null) {
            handleBytes(command, bytes);
        } else {
            mBytes.put(bytes.md5, bytes);
        }
    }

    @WorkerThread
    private void sayHelloToServer(JsonWebSocket socket) {
        writeMap(socket, TYPE_HELLO, new MapBuilder<String, Object>()
                .put("device_name", Build.BRAND + " " + Build.MODEL)
                .put("client_version", CLIENT_VERSION)
                .put("app_version", BuildConfig.VERSION_NAME)
                .put("app_version_code", BuildConfig.VERSION_CODE)
                .build());
        mHandler.postDelayed(() -> {
            if (mSocket != socket && !socket.isClosed()) {
                onHandshakeTimeout(socket);
            }
        }, HANDSHAKE_TIMEOUT);
    }

    @MainThread
    private void onHandshakeTimeout(JsonWebSocket socket) {
        Log.i(LOG_TAG, "onHandshakeTimeout");
        mConnectionState.onNext(new State(State.DISCONNECTED, new SocketTimeoutException("handshake timeout")));
        socket.close();
    }

    @MainThread
    private void onServerHello(JsonWebSocket jsonWebSocket, JsonObject message) {
        Log.i(LOG_TAG, "onServerHello: " + message);
        mSocket = jsonWebSocket;
        mConnectionState.onNext(new State(State.CONNECTED));
    }

    @AnyThread
    private static boolean write(JsonWebSocket socket, String type, JsonObject data) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.add("data", data);
        return socket.write(json);
    }

    @AnyThread
    private static boolean writePair(JsonWebSocket socket, String type, Pair<String, String> pair) {
        JsonObject data = new JsonObject();
        data.addProperty(pair.first, pair.second);
        return write(socket, type, data);
    }

    @AnyThread
    private static boolean writeMap(JsonWebSocket socket, String type, Map<String, ?> map) {
        JsonObject data = new JsonObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                data.addProperty(entry.getKey(), (String) value);
            } else if (value instanceof Character) {
                data.addProperty(entry.getKey(), (Character) value);
            } else if (value instanceof Number) {
                data.addProperty(entry.getKey(), (Number) value);
            } else if (value instanceof Boolean) {
                data.addProperty(entry.getKey(), (Boolean) value);
            } else if (value instanceof JsonElement) {
                data.add(entry.getKey(), (JsonElement) value);
            } else {
                throw new IllegalArgumentException("cannot put value " + value + " into json");
            }
        }
        return write(socket, type, data);
    }


    @SuppressLint("CheckResult")
    @AnyThread
    public void log(String log) {
        if (!isConnected())
            return;
        writePair(mSocket, "log", new Pair<>("log", log));
    }
}
