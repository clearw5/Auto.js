package com.stardust.scriptdroid.network;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stardust.scriptdroid.network.api.ConfigApi;
import com.stardust.scriptdroid.network.entity.config.Config;
import com.stardust.scriptdroid.network.util.WebkitCookieManagerProxy;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stardust on 2017/9/20.
 */

public class NodeBB {

    public static final String BASE_URL = "http://www.autojs.org/";
    private static final NodeBB sInstance = new NodeBB();
    private Config mConfig;


    private Retrofit mRetrofit;

    NodeBB() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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

    public Observable<Config> getConfig() {
        if (mConfig == null) {
            return mRetrofit.create(ConfigApi.class)
                    .getConfig()
                    .doOnNext(config -> mConfig = config);
        }
        return Observable.just(mConfig);

    }

    public static String url(String relativePath) {
        return BASE_URL + relativePath;
    }
}
