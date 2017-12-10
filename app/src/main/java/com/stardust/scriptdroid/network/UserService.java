package com.stardust.scriptdroid.network;

import android.util.Log;

import com.stardust.scriptdroid.network.api.ConfigApi;
import com.stardust.scriptdroid.network.api.UserApi;

import java.util.Collections;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Stardust on 2017/9/20.
 */

public class UserService {

    private static final UserService sInstance = new UserService();
    private final Retrofit mRetrofit;

    UserService() {
        mRetrofit = NodeBB.getInstance().getRetrofit();
    }

    public static UserService getInstance() {
        return sInstance;
    }

    public Observable<ResponseBody> login(String userName, final String password) {
        return NodeBB.getInstance()
                .getConfig()
                .flatMap(config -> {
                    return mRetrofit.create(UserApi.class)
                            .login(Collections.singletonMap("x-csrf-token", config.getCsrfToken()),
                                    userName, password);
                });

    }

    public Observable<ResponseBody> register(String email, String userName, String password) {
        return NodeBB.getInstance()
                .getConfig()
                .flatMap(config -> mRetrofit.create(UserApi.class)
                        .register(Collections.singletonMap("x-csrf-token", config.getCsrfToken()),
                                email, userName, password, password));
    }
}
