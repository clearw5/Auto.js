package com.stardust.scriptdroid.network.api;

import com.stardust.scriptdroid.network.entity.VerifyResponse;
import com.stardust.scriptdroid.network.entity.TokenResponse;
import com.stardust.scriptdroid.network.entity.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Stardust on 2017/9/20.
 */

public interface UserApi {

    @GET("/api/me")
    Observable<User> me();

    @FormUrlEncoded
    @POST("/api/ns/login")
    Observable<VerifyResponse> verify(@Field("username") String userName, @Field("password") String password);


    @FormUrlEncoded
    @POST("/api/v2/{uid}/tokens")
    Observable<TokenResponse> generateToken(@Path("uid") String uid, @Field("password") String password);



}
