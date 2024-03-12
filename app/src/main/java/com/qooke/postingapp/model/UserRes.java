package com.qooke.postingapp.model;

public class UserRes {

    public String result;
    public String access_token;


    // 디폴트 생성자
    public UserRes() {

    }

    // 생성자
    public UserRes(String result, String access_token) {
        this.result = result;
        this.access_token = access_token;
    }
}
