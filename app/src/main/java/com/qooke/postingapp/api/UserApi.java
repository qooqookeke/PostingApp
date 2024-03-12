package com.qooke.postingapp.api;

import com.qooke.postingapp.model.Res;
import com.qooke.postingapp.model.User;
import com.qooke.postingapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    // 회원 가입 API
    @POST("/user/register")
    Call<UserRes> register(@Body User user);


    // 로그인 API
    @POST("/user/login")
    Call<UserRes> login(@Body User user);


    // 로그아웃 API
    @DELETE("/user/logout")
    Call<Res> logout(@Header("Authorization") String token);
}
