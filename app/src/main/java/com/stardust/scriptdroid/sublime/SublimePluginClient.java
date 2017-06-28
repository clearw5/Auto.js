package com.stardust.scriptdroid.sublime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
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
    private ExecutorService mExecutor;

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
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mOutputStream.write(object.toString().getBytes());
                    mOutputStream.write("\n".getBytes());
                    mOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    tryClose();
                }
            }
        });
    }

    public void close() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mExecutor.shutdownNow();
            mSocket = null;
            mOutputStream = null;
            mExecutor = null;
            EventBus.getDefault().post(new ConnectionStateChangeEvent(false));
        }
    }

    private void startReadLoop(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (!Thread.currentThread().isInterrupted()) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (mResponseHandler != null) {
                JsonElement jsonElement = new JsonParser().parse(line);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                mResponseHandler.handle(jsonObject);
            }
        }
    }


}
