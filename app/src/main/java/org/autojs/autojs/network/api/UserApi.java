package org.autojs.autojs.network.api;

import org.autojs.autojs.network.entity.notification.NotificationResponse;
import org.autojs.autojs.network.entity.user.User;

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

    @FormUrlEncoded
    @POST("/register")
    Observable<ResponseBody> register(@HeaderMap Map<String, String> csrfToken, @Field("email") String email,
                                      @Field("username") String userName, @Field("password") String password, @Field("password-confirm") String repeatPassword);


    @POST("/logout")
    Observable<ResponseBody> logout(@HeaderMap Map<String, String> csrfToken);

    @GET("/api/notifications")
    Observable<NotificationResponse> getNotifitions();
}
