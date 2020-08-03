package org.autojs.autojs.pluginclient;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

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

    public static class Bytes {
        public final String md5;
        public final ByteString byteString;
        public final long timestamp;

        public Bytes(String md5, ByteString byteString) {
            this.md5 = md5;
            this.byteString = byteString;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static final String LOG_TAG = "JsonWebSocket";

    private final WebSocket mWebSocket;
    private final JsonParser mJsonParser = new JsonParser();
    private final PublishSubject<JsonElement> mJsonElementPublishSubject = PublishSubject.create();
    private final PublishSubject<Bytes> mBytesPublishSubject = PublishSubject.create();
    private volatile boolean mClosed = false;

    public JsonWebSocket(OkHttpClient client, Request request) {
        mWebSocket = client.newWebSocket(request, this);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(LOG_TAG, "onMessage: text = " + text);
        dispatchJson(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(LOG_TAG, "onMessage: ByteString = " + bytes.toString());
        mBytesPublishSubject.onNext(new Bytes(bytes.md5().hex(), bytes));
    }

    public Observable<JsonElement> data() {
        return mJsonElementPublishSubject;
    }

    public Observable<Bytes> bytes(){
        return mBytesPublishSubject;
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
