package com.stardust.scriptdroid.sublime_plugin_client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardust.util.UiHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Stardust on 2017/5/10.
 */

public class SublimePluginClient {


    private Socket mSocket;
    private Handler mResponseHandler;
    private String host;
    private int port;
    private OutputStream mOutputStream;
    private UiHandler mUiHandler;

    public SublimePluginClient(UiHandler handler, String host, int port) {
        this.host = host;
        this.port = port;
        mUiHandler = handler;
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
                    mSocket.setTcpNoDelay(true);
                    mUiHandler.toast("Connected");
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

    public void send(JsonObject object) throws IOException {
        if (mSocket == null) {
            throw new IllegalStateException("Socket is not listening ");
        }
        mOutputStream.write(object.toString().getBytes());
        mOutputStream.write("\n".getBytes());
        mOutputStream.flush();
    }

    public void close() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
            mOutputStream = null;
            mUiHandler.toast("Disconnected");
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
