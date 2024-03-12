package com.qooke.postingapp.model;

public class Posting {

//    {
//        "photoId": 13,
//            "imgUrl": "https://qooke-posting-server.s3.ap-northeast-2.amazonaws.com/2023-12-19T11_17_14.9341574jpeg",
//            "content": "자연",
//            "userId": 4,
//            "email": "ccc@naver.com",
//            "createdAt": "2023-12-19T02:17:20",
//            "likeCnt": 0,
//            "isLike": 0
//    },


    public int photoId;
    public String imgUrl;
    public String content;
    public int userId;
    public String email;
    public String createdAt;
    public int likeCnt;
    public int isLike;


    // 디폴트 생성자
    public Posting() {

    }

    // 생성자


}
