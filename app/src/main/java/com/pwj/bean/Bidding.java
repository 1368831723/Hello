package com.pwj.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by han on 2018/8/20.
 */

public class Bidding extends LitePalSupport {
    private String times;
    private String valid;
    private String title;
    private String url_pdf;
    private String link;
    private String content;
    private String company;
    private String contact;
    private String phone;
    private String email;
    private String location;
    private String requires;
    private String bank;
    private String account;
    private String longitude;
    private String latitude;
    private String count; //相同公司发布招标的数量

    public Bidding(String times, String valid, String title, String url_pdf, String link, String content, String company, String contact, String phone, String email, String location, String requires, String bank, String account) {
        this.times = times;
        this.valid = valid;
        this.title = title;
        this.url_pdf = url_pdf;
        this.link = link;
        this.content = content;
        this.company = company;
        this.contact = contact;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.requires = requires;
        this.bank = bank;
        this.account = account;
    }

    //同一个公司发布历史招标的数量，以及经纬度
    public Bidding(String company, String longitude, String latitude, String content, String count) {
        this.company = company;
        this.longitude = longitude;
        this.latitude = latitude;
        this.content = content;
        this.count = count;
    }

    public Bidding() {
    }

    public Bidding(String times, String title, String link, String company) {
        this.times = times;
        this.title = title;
        this.link = link;
        this.company = company;
    }

    public String getTimes() {
        return times;
    }

    public String getValid() {
        return valid;
    }


    public String getTitle() {
        return title;
    }

    public String getUrl_pdf() {
        return url_pdf;
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


    public String getContact() {
        return contact;
    }


    public String getPhone() {
        return phone;
    }


    public String getEmail() {
        return email;
    }


    public String getLocation() {
        return location;
    }


    public String getRequires() {
        return requires;
    }


    public String getBank() {
        return bank;
    }


    public String getAccount() {
        return account;
    }


    public String getLongitude() {
        return longitude;
    }


    public String getLatitude() {
        return latitude;
    }


    public String getCount() {
        return count;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setValid(String valid) {
        this.valid = valid;
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

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRequires(String requires) {
        this.requires = requires;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

