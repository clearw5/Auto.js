package com.stardust.scriptdroid.network;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stardust.scriptdroid.network.api.UserApi;
import com.stardust.scriptdroid.network.entity.TokenResponse;
import com.stardust.scriptdroid.network.entity.VerifyResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stardust on 2017/9/20.
 */

public class UserService {

    private static final UserService sInstance = new UserService();
    private final Retrofit mRetrofit;

    UserService() {
        mRetrofit = NodeBB.getInstance().getRetrofit();
    }


    public Observable<TokenResponse> login(String userName, final String password) {
        final UserApi userApi = mRetrofit.create(UserApi.class);
        return userApi.verify(userName, password)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<VerifyResponse, ObservableSource<TokenResponse>>() {
                    @Override
                    public ObservableSource<TokenResponse> apply(@NonNull VerifyResponse verifyResponse) throws Exception {
                        if (verifyResponse.isSuccessful()) {
                            return userApi.generateToken(verifyResponse.getUid(), password);
                        } else {
                            return Observable.error(new Exception(verifyResponse.getMessage()));
                        }
                    }
                });
    }
}
