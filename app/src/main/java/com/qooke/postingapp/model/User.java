package com.qooke.postingapp.model;

public class User {

//    {
//            "email": "rrr@naver.com",
//            "password": "1234"
//    }

    public String email;
    public String password;


    // 디폴트 생성자 만들기
    public User() {

    }

    // 생성자 만들기
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
