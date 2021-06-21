package com.pwj.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 13688 on 2019/7/12.
 */

public class BiddingSituation extends LitePalSupport {
    private String company;
    private String COUNT;   //相同公司发布招标的数量
    private String longitude;
    private String latitude;
    private String content; //为了检索内容的关键词
    public void setCompany(String company) {
        this.company = company;
    }


    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getCompany() {
        return company;
    }


    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(String COUNT) {
        this.COUNT = COUNT;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
