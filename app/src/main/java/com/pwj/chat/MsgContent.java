package com.pwj.chat;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 13688 on 2019/7/19.
 */

public class MsgContent extends LitePalSupport{
    private int status;     // 1是已读，2是未读
    private String my_phone;
    private String other_phone;
    private String other_name;
    private String date;
    private String url3;
    private String content;
    private int type;      // 1是图片，2是纯文字
    private String path;

    public MsgContent() {

    }

    public MsgContent(String my_phone, String other_phone, String date, String url3, String content, int type) {
        this.my_phone = my_phone;
        this.other_phone = other_phone;
        this.date = date;
        this.url3 = url3;
        this.content = content;
        this.type = type;
    }

    public MsgContent(String my_phone,String other_phone,String other_name, String date, String url3, String content, int type) {
        this.my_phone = my_phone;
        this.other_phone = other_phone;
        this.other_name = other_name;
        this.date = date;
        this.url3 = url3;
        this.content = content;
        this.type = type;
    }
    public MsgContent(int status,String my_phone,String other_phone,String other_name, String date, String url3, String content, int type) {
        this.status = status;
        this.my_phone = my_phone;
        this.other_phone = other_phone;
        this.other_name = other_name;
        this.date = date;
        this.url3 = url3;
        this.content = content;
        this.type = type;
    }
    public MsgContent(String my_phone,String other_phone, String date, String url3, String content, int type, String path) {
        this.my_phone = my_phone;
        this.other_phone = other_phone;
        this.date = date;
        this.url3 = url3;
        this.content = content;
        this.type = type;
        this.path = path;
    }
    public MsgContent(String my_phone,String other_phone,String other_name, String date, String url3, String content, int type, String path) {
        this.my_phone = my_phone;
        this.other_phone = other_phone;
        this.other_name = other_name;
        this.date = date;
        this.url3 = url3;
        this.content = content;
        this.type = type;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public String getMy_phone() {
        return my_phone;
    }

    public String getOther_phone() {
        return other_phone;
    }

    public String getOther_name() {
        return other_name;
    }

    public String getDate() {
        return date;
    }

    public String getUrl3() {
        return url3;
    }

    public String getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    public int getType() {
        return type;
    }
}
