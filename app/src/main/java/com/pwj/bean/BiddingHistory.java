package com.pwj.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 13688 on 2019/7/12.
 */

public class BiddingHistory extends LitePalSupport {
    private String times;
    private String title;
    private String link;
    private String content;
    private String company;

    public void setTimes(String times) {
        this.times = times;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTimes() {
        return times;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    public String getCompany() {
        return company;
    }

}
