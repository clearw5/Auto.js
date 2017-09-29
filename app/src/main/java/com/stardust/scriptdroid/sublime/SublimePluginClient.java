package com.stardust.scriptdroid.sublime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(host, port);
                    mConnectionState.onNext(true);
                    mSocket.setTcpNoDelay(true);
                    mOutputStream = mSocket.getOutputStream();
                    s.onComplete();
                    readLoop(mSocket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    s.onError(e);
                } finally {
                    tryClose();
                }
            }
        }).start();
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

    public Observable<Void> send(final JsonObject object) {
        if (mSocket == null) {
            throw new IllegalStateException("Socket is not listening ");
        }
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mOutputStream.write(object.toString().getBytes());
                mOutputStream.write("\n".getBytes());
                mOutputStream.flush();
                return null;
            }
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
