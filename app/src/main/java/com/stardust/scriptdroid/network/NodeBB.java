package com.stardust.scriptdroid.network;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stardust.scriptdroid.network.util.WebkitCookieManagerProxy;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stardust on 2017/9/20.
 */

public class NodeBB {

    private static final NodeBB sInstance = new NodeBB();

    private Retrofit mRetrofit;

    NodeBB() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://www.autojs.org/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .cookieJar(new WebkitCookieManagerProxy())
                        .build())
                .build();
    }

    public static NodeBB getInstance() {
        return sInstance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
