package org.autojs.autojs.pluginclient;

import android.os.Build;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.autojs.autojs.tool.SimpleObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/5/10.
 */

public class DevPluginClient {

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

    private static final State STATE_CONNECTED = new State(State.CONNECTED);
    private static final State STATE_CONNECTING = new State(State.CONNECTING);

    private volatile Socket mSocket;
    private Handler mResponseHandler;
    private String host;
    private int port;
    private OutputStream mOutputStream;
    private final PublishSubject<State> mConnection;
    private int mState;

    public DevPluginClient(String host, int port, PublishSubject<State> connection) {
        this.host = host;
        this.port = port;
        mConnection = connection;
        mConnection.subscribe(state -> mState = state.getState());
    }

    public int getState() {
        return mState;
    }

    public void setResponseHandler(Handler handler) {
        mResponseHandler = handler;
    }

    public void connectToServer() {
        if (mState != State.DISCONNECTED) {
            throw new IllegalStateException("Connecting or Connected!");
        }
        new Thread(() -> {
            if (mState != State.DISCONNECTED) {
                return;
            }
            mConnection.onNext(STATE_CONNECTING);
            try {
                connect();
                if (mSocket != null)
                    readDataFromSocket();
                close();
            } catch (Exception e) {
                e.printStackTrace();
                close();
                mConnection.onNext(new State(State.DISCONNECTED, e));
            }
        }).start();
    }


    private void readDataFromSocket() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        JsonParser parser = new JsonParser();
        while (!Thread.currentThread().isInterrupted()) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (mResponseHandler != null) {
                handleData(parser, line);

            }
        }
    }

    private void handleData(JsonParser parser, String line) {
        try {
            JsonElement jsonElement = parser.parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            mResponseHandler.handle(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            //ignore exception thrown by data handler
        }
    }

    private void connect() throws IOException {
        mSocket = new Socket(host, port);
        mConnection.onNext(STATE_CONNECTED);
        mOutputStream = mSocket.getOutputStream();
        sendDeviceName();
    }

    private void sendDeviceName() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "device_name");
        object.addProperty("device_name", Build.BRAND + " " + Build.MODEL);
        send(object).subscribe();
    }


    public Observable<JsonObject> send(final JsonObject object) {
        if (mState != State.CONNECTED) {
            throw new IllegalStateException("Not connected!");
        }
        return Observable.fromCallable(() -> {
            mOutputStream.write(object.toString().getBytes());
            mOutputStream.write("\n".getBytes());
            mOutputStream.flush();
            return object;
        });
    }

    public Observable<JsonObject> send(Map<String, Object> data) {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Number) {
                object.addProperty(entry.getKey(), (Number) value);
            } else if (value instanceof Boolean) {
                object.addProperty(entry.getKey(), (Boolean) value);
            } else if (value instanceof Character) {
                object.addProperty(entry.getKey(), (Character) value);
            } else {
                object.addProperty(entry.getKey(), value.toString());
            }
        }
        return send(object);
    }

    public boolean close() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
            mOutputStream = null;
            return true;
        }
        return false;
    }


}
