package org.autojs.autojs.pluginclient;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class JsonWebSocket extends WebSocketListener {

    private static final String LOG_TAG = "JsonWebSocket";

    private final WebSocket mWebSocket;
    private final JsonParser mJsonParser = new JsonParser();
    private final PublishSubject<JsonElement> mJsonElementPublishSubject = PublishSubject.create();
    private volatile boolean mClosed = false;

    public JsonWebSocket(OkHttpClient client, Request request) {
        mWebSocket = client.newWebSocket(request, this);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(LOG_TAG, "onMessage: " + text);
        dispatchJson(text);
    }

    public Observable<JsonElement> data() {
        return mJsonElementPublishSubject;
    }

    public boolean write(JsonElement element) {
        String json = element.toString();
        Log.d(LOG_TAG, "write: length = " + json.length() + ", json = " + element);
        return mWebSocket.send(json);
    }

    public void close() {
        mJsonElementPublishSubject.onComplete();
        mClosed = true;
        mWebSocket.close(1000, "close");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(LOG_TAG, "onFailure: code = " + code + ", reason = " + reason);
        close();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        Log.d(LOG_TAG, "onFailure: response = " + response, t);
        close(t);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(LOG_TAG, "onOpen: response = " + response);
    }


    private void close(Throwable e) {
        if (mClosed) {
            return;
        }
        mJsonElementPublishSubject.onError(e);
        mClosed = true;
        mWebSocket.close(1011, "remote exception: " + e.getMessage());
    }

    private void dispatchJson(String json) {
        try {
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            JsonElement element = mJsonParser.parse(reader);
            mJsonElementPublishSubject.onNext(element);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

    }

    public boolean isClosed() {
        return mClosed;
    }

}
