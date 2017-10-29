package com.stardust.scriptdroid.sublime;

import android.os.Build;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/5/10.
 */

public class SublimePluginClient {


    private volatile Socket mSocket;
    private Handler mResponseHandler;
    private String host;
    private int port;
    private OutputStream mOutputStream;
    private final PublishSubject<Boolean> mConnectionState;

    public SublimePluginClient(String host, int port, PublishSubject<Boolean> connectionState) {
        this.host = host;
        this.port = port;
        mConnectionState = connectionState;
    }

    public void setResponseHandler(Handler handler) {
        mResponseHandler = handler;
    }

    public Observable<Void> listen() {
        if (mSocket != null) {
            throw new IllegalStateException("Socket listening ");
        }
        return Observable.fromPublisher(new Publisher<Void>() {

            @Override
            public void subscribe(Subscriber<? super Void> s) {
                if (mSocket != null) {
                    s.onError(new IllegalStateException("Socket listening "));
                    return;
                }
                listenInternal(s);
            }
        });

    }

    private void listenInternal(final Subscriber<? super Void> s) {
        try {
            mSocket = new Socket(host, port);
            mConnectionState.onNext(true);
            mSocket.setTcpNoDelay(true);
            mOutputStream = mSocket.getOutputStream();
            s.onComplete();
            sendDeviceName();
        } catch (IOException e) {
            s.onError(e);
            tryClose();
            return;
        }
        try {
            readLoop(mSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            tryClose();
        }

    }

    private void sendDeviceName() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "device_name");
        object.addProperty("device_name", Build.MODEL);
        send(object).subscribe();
    }

    public PublishSubject<Boolean> getConnectionState() {
        return mConnectionState;
    }

    public boolean isListening() {
        return mSocket != null;
    }

    private void tryClose() {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Observable<JsonObject> send(final JsonObject object) {
        if (mSocket == null) {
            throw new IllegalStateException("Socket is not listening ");
        }
        return Observable.fromCallable(() -> {
            mOutputStream.write(object.toString().getBytes());
            mOutputStream.write("\n".getBytes());
            mOutputStream.flush();
            return object;
        });
    }

    public void close() throws IOException {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
            mOutputStream = null;
            mConnectionState.onNext(false);
        }
    }

    private void readLoop(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        JsonParser parser = new JsonParser();
        while (!Thread.currentThread().isInterrupted()) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (mResponseHandler != null) {
                JsonElement jsonElement = parser.parse(line);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                mResponseHandler.handle(jsonObject);
            }
        }
    }


}
