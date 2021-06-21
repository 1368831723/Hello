package com.pwj.bean;

/**
 * Created by 13688 on 2019/7/5.
 */

public class User {
    private String id;      //聊天时候的id (包括用户呢成和ip地址)
    private String phone;   //聊天时候的昵称
    private String url3;
    private String user_name;
    private int count;

    public User(String id, String phone,String user_name, String url3) {
        this.id = id;
        this.phone = phone;
        this.user_name = user_name;
        this.url3 = url3;
    }

    //我的消息这个页面展示最近消息列表
    public User(String phone,String user_name, String url3) {
        this.phone = phone;
        this.user_name = user_name;
        this.url3 = url3;
    }
    //我的消息这个页面展示最近消息列表
    public User(String phone, String user_name,String url3 , int count) {
        this.phone = phone;
        this.user_name = user_name;
        this.url3 = url3;
        this.count = count;
    }
    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrl3() {
        return url3;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getCount() {
        return count;
    }
}
