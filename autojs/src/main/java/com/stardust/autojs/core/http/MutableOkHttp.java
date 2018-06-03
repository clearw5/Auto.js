package com.stardust.autojs.core.http;

import android.widget.AdapterView;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Stardust on 2018/4/11.
 */

public class MutableOkHttp extends OkHttpClient {

    private OkHttpClient mOkHttpClient;
    private int mMaxRetries = 3;
    private long mTimeout = 30 * 1000;
    private Interceptor mRetryInterceptor = chain -> {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int tryCount = 0;
        while (!response.isSuccessful() && tryCount < getMaxRetries()) {
            tryCount++;
            response = chain.proceed(request);
        }
        return response;
    };

    public MutableOkHttp() {
        mOkHttpClient = newClient(new OkHttpClient.Builder());
    }

    public OkHttpClient client() {
        return mOkHttpClient;
    }

    protected OkHttpClient newClient(Builder builder) {
        builder.readTimeout(getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(getTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(getTimeout(), TimeUnit.MILLISECONDS);
        for (Interceptor interceptor : getInterceptors()) {
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    public Iterable<? extends Interceptor> getInterceptors() {
        return Collections.singletonList(mRetryInterceptor);
    }

    public int getMaxRetries() {
        return mMaxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        mMaxRetries = maxRetries;
    }

    public long getTimeout() {
        return mTimeout;
    }


    public void setTimeout(long timeout) {
        mTimeout = timeout;
        muteClient();
    }

    protected synchronized void muteClient() {
        mOkHttpClient = newClient(mOkHttpClient.newBuilder());
    }
}
