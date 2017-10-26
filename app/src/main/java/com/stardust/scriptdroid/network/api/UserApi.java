package com.stardust.scriptdroid.network.api;

import com.stardust.scriptdroid.network.entity.user.User;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Stardust on 2017/9/20.
 */

public interface UserApi {

    @GET("/api/me")
    Observable<User> me();

    @FormUrlEncoded
    @POST("/login")
    Observable<ResponseBody> login(@HeaderMap Map<String, String> csrfToken, @Field("username") String userName, @Field("password") String password);


}
