package com.stardust.net;

import com.stardust.pio.UncheckedIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Stardust on 2017/4/10.
 */

public class AutoHttpURLConnection extends HttpURLConnection implements AutoCloseable {

    private HttpURLConnection mHttpURLConnection;
    private InputStream mInputStream;
    private OutputStream mOutputStream;


    public AutoHttpURLConnection(URL url) throws IOException {
        super(url);
        mHttpURLConnection = (HttpURLConnection) url.openConnection();
        connect();
    }

    public AutoHttpURLConnection(String url) throws IOException {
        this(new URL(url));
    }

    @Override
    public InputStream getInputStream() {
        try {
            mInputStream = super.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return mInputStream;
    }

    @Override
    public void close() {
        disconnect();
    }

    @Override
    public void disconnect() {
        mHttpURLConnection.disconnect();
    }

    @Override
    public boolean usingProxy() {
        return mHttpURLConnection.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        mHttpURLConnection.connect();
    }
}
