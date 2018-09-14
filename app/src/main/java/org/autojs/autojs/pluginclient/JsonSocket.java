package org.autojs.autojs.pluginclient;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class JsonSocket {

    private static final char DELIMITER = '#';
    private static final String DELIMITER_STRING = "#";
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
        String json = element.toString();
        byte[] bytes = json.getBytes();
        String length = json.length() + DELIMITER_STRING;
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

    private static class CharQueue {
        char[] data;
        int offset = 0;
        int size = 0;

        public CharQueue(int initialCapacity) {
            data = new char[initialCapacity];
        }

        int read(InputStreamReader reader) throws IOException {
            if (size >= data.length) {
                resize();
            }
            int end = offset + size;
            int n;
            if (end >= data.length) {
                n = reader.read(data, 0, offset);
            } else {
                n = reader.read(data, end, data.length - end);
            }
            size += n;
            return n;
        }


        void pop(int len) {
            if (len > size) {
                throw new IllegalArgumentException("pop " + len + " but current length is " + size);
            }
            offset += len;
            if (offset >= data.length) {
                offset -= data.length;
            }
            size -= len;
        }

        String popAsString(int len) {
            if (len > size) {
                throw new IllegalArgumentException("popAsString " + len + " but current length is " + size);
            }
            int end = offset + len;
            String str;
            if (end < data.length) {
                str = new String(data, offset, len);
            } else {
                char[] bytes = new char[len];
                int firstPartLength = data.length - offset;
                int secondPartLength = len - firstPartLength;
                System.arraycopy(data, offset, bytes, 0, firstPartLength);
                System.arraycopy(data, 0, bytes, firstPartLength, secondPartLength);
                str = new String(bytes);
            }
            pop(len);
            return str;
        }

        private void resize() {
            char[] newData = new char[data.length * 2];
            int end = offset + size;
            if (end < data.length) {
                System.arraycopy(data, offset, newData, 0, size);
            } else {
                int firstPartLength = data.length - offset;
                int secondPartLength = offset + size - data.length;
                System.arraycopy(data, offset, newData, 0, firstPartLength);
                System.arraycopy(data, 0, newData, firstPartLength, secondPartLength);
            }
            offset = 0;
            data = newData;
        }


    }

    private class SocketReader implements Runnable {

        private final Socket mSocket;
        private final InputStreamReader mInputStreamReader;
        private int mJsonDataLength = -1;
        private CharQueue mCharQueue = new CharQueue(4096);

        private SocketReader(Socket socket) throws IOException {
            mSocket = socket;
            mInputStreamReader = new InputStreamReader(mSocket.getInputStream());
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
            while ((n = mCharQueue.read(mInputStreamReader)) > 0) {
                onChunk(mCharQueue, n);
            }
        }

        private void onChunk(CharQueue charQueue, int chunkSize) {
            if (mJsonDataLength <= 0) {
                tryReadingJsonDataLength(charQueue, chunkSize);
            }
            if (mJsonDataLength <= 0) {
                return;
            }
            if (charQueue.size < mJsonDataLength) {
                return;
            }
            String json = charQueue.popAsString(mJsonDataLength);
            Log.d(LOG_TAG, "json = " + json);
            mJsonDataLength = -1;
            dispatchJson(json);

        }


        private void tryReadingJsonDataLength(CharQueue charQueue, int chunkSize) {
            int end = charQueue.offset + charQueue.size;
            int start = end - chunkSize;
            for (int i = start; i < end; i++) {
                if (charQueue.data[i] == DELIMITER) {
                    String jsonDataLength = new String(charQueue.data, charQueue.offset, i - charQueue.offset);
                    Log.d(LOG_TAG, "json data length = " + jsonDataLength);
                    charQueue.pop(i - charQueue.offset + 1);
                    receiveJsonDataLength(jsonDataLength);
                    break;
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
