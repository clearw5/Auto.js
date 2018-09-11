package org.autojs.autojs.pluginclient;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class JsonSocket {

    private static final byte DELIMITER = '#';
    private static final String LOG_TAG = "JsonSocket";

    private final Socket mSocket;
    private final JsonParser mJsonParser = new JsonParser();
    private OutputStream mOutputStream;
    private final PublishSubject<JsonElement> mJsonElementPublishSubject = PublishSubject.create();
    private volatile boolean mClosed = false;

    public JsonSocket(Socket socket) throws IOException {
        mSocket = socket;
        mOutputStream = socket.getOutputStream();
        new Thread(new SocketReader(socket)).start();
    }

    public Observable<JsonElement> data() {
        return mJsonElementPublishSubject;
    }

    public int write(JsonElement element) throws IOException {
        byte[] bytes = element.toString().getBytes();
        String length = String.valueOf(bytes.length) + DELIMITER;
        mOutputStream.write(length.getBytes());
        mOutputStream.write(bytes);
        Log.d(LOG_TAG, "write: length = " + bytes.length + ", json = " + element);
        return bytes.length;
    }

    public void close() {
        mJsonElementPublishSubject.onComplete();
        mClosed = true;
        try {
            mSocket.close();
        } catch (IOException ignored) {
        }
    }


    private void close(Exception e) {
        if (mClosed) {
            return;
        }
        mJsonElementPublishSubject.onError(e);
        mClosed = true;
        try {
            mSocket.close();
        } catch (IOException ignored) {
        }
    }

    private void dispatchJson(String json) {
        try {
            JsonElement element = mJsonParser.parse(json);
            mJsonElementPublishSubject.onNext(element);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

    }

    public boolean isClosed() {
        return mClosed;
    }

    private static class ByteQueue {
        byte[] data;
        int offset = 0;
        int length = 0;

        public ByteQueue(int initialCapacity) {
            data = new byte[initialCapacity];
        }

        int read(InputStream stream) throws IOException {
            if (length >= data.length) {
                resize();
            }
            int end = offset + length;
            int n;
            if (end >= data.length) {
                n = stream.read(data, 0, offset);
            } else {
                n = stream.read(data, end, data.length - end);
            }
            length += n;
            return n;
        }


        void pop(int len) {
            if (len > length) {
                throw new IllegalArgumentException("pop " + len + " but current length is " + length);
            }
            offset += len;
            if (offset >= data.length) {
                offset -= data.length;
            }
        }

        String popAsString(int len) {
            if (len > length) {
                throw new IllegalArgumentException("popAsString " + len + " but current length is " + length);
            }
            int end = offset + len;
            String str;
            if (end < data.length) {
                str = new String(data, offset, len);
            } else {
                byte[] bytes = new byte[len];
                System.arraycopy(data, offset, bytes, 0, data.length - offset);
                System.arraycopy(data, 0, bytes, data.length - offset, len - (data.length - offset));
                str = new String(bytes);
            }
            pop(len);
            return str;
        }

        private void resize() {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }


    }

    private class SocketReader implements Runnable {

        private final Socket mSocket;
        private final InputStream mInputStream;
        private int mJsonDataLength = -1;
        private ByteQueue mByteQueue = new ByteQueue(4096);

        private SocketReader(Socket socket) throws IOException {
            mSocket = socket;
            mInputStream = mSocket.getInputStream();
        }


        @Override
        public void run() {
            try {
                readLoop();
                close();
            } catch (Exception e) {
                e.printStackTrace();
                close(e);
            }
        }


        private void readLoop() throws Exception {
            int n;
            while ((n = mByteQueue.read(mInputStream)) > 0) {
                onChunk(mByteQueue, n);
            }
        }

        private void onChunk(ByteQueue byteQueue, int chunkSize) {
            if (mJsonDataLength <= 0) {
                tryReadingJsonDataLength(byteQueue, chunkSize);
                return;
            }
            if (byteQueue.length < mJsonDataLength) {
                return;
            }
            String json = byteQueue.popAsString(mJsonDataLength);
            Log.d(LOG_TAG, "json = " + json);
            mJsonDataLength = -1;
            dispatchJson(json);

        }


        private void tryReadingJsonDataLength(ByteQueue byteQueue, int chunkSize) {
            int end = byteQueue.offset + byteQueue.length;
            for (int i = 1; i <= chunkSize; i++) {
                if (byteQueue.data[end - i] == DELIMITER) {
                    String jsonDataLength = new String(byteQueue.data, byteQueue.offset, end - i);
                    Log.d(LOG_TAG, "json data length = " + jsonDataLength);
                    byteQueue.pop(end - i + 1);
                    receiveJsonDataLength(jsonDataLength);
                }
            }
        }

        private void receiveJsonDataLength(String jsonDataLength) {
            try {
                mJsonDataLength = Integer.parseInt(jsonDataLength);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
