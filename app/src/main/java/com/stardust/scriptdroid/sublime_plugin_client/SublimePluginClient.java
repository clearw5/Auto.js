package com.stardust.scriptdroid.sublime_plugin_client;

import android.os.Looper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardust.util.UiHandler;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/5/10.
 */

public class SublimePluginClient {

    public static class ConnectionStateChangeEvent {

        private boolean mConnected;

        public ConnectionStateChangeEvent(boolean connected) {
            mConnected = connected;
        }

        public boolean isConnected() {
            return mConnected;
        }
    }

    private Socket mSocket;
    private Handler mResponseHandler;
    private String host;
    private int port;
    private OutputStream mOutputStream;
    private Executor mExecutor;

    public SublimePluginClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setResponseHandler(Handler handler) {
        mResponseHandler = handler;
    }

    public void listen() {
        if (mSocket != null) {
            throw new IllegalStateException("Socket listening ");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(host, port);
                    EventBus.getDefault().post(new ConnectionStateChangeEvent(true));
                    mSocket.setTcpNoDelay(true);
                    mOutputStream = mSocket.getOutputStream();
                    startReadLoop(mSocket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    tryClose();
                }
            }
        }).start();
    }

    private void tryClose() {
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(final JsonObject object) {
        if (mSocket == null) {
            throw new IllegalStateException("Socket is not listening ");
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    send(object);
                }
            });
        }
        try {
            mOutputStream.write(object.toString().getBytes());
            mOutputStream.write("\n".getBytes());
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            tryClose();
        }

    }

    public void close() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
            mOutputStream = null;
            EventBus.getDefault().post(new ConnectionStateChangeEvent(false));
        }
    }

    private void startReadLoop(InputStream stream) throws IOException {
        byte[] buffer = new byte[8192];
        while (!Thread.currentThread().isInterrupted()) {
            int len = stream.read(buffer);
            if (len <= 0) {
                return;
            }
            if (mResponseHandler != null) {
                String str = new String(buffer, 0, len);
                JsonElement jsonElement = new JsonParser().parse(str);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                mResponseHandler.handle(jsonObject);
            }
        }
    }


}
