package com.pwj.bean;

import java.io.Serializable;

/**
 * Created by jun on 2018/6/9.
 */

public class Customer implements Serializable{

    private int key;
    private String companyname;
    private String liaisons;
    private String www;
    private String date;
    private String address;
    private double longitude;
    private double latitude;
    private String lon;
    private String lat;
    private String description;
    private String main_products_using_pwj;
    private String related_sailer;
    private String used_pwjs;
    private String nation;
    private String province;
    private String prefectrue_level_city;
    private String county_level_city;
    private String town;
    private String village;
    private String count;

    public Customer(int key, String companyname, String liaisons, String www, String date, String address,
                    double longitude, double latitude, String description, String main_products_using_pwj,
                    String related_sailer, String used_pwjs, String nation, String province, String prefectrue_level_city,
                    String county_level_city, String town, String village){
        this.key = key;
        this.companyname = companyname;
        this.liaisons = liaisons;
        this.www = www;
        this.date = date;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.main_products_using_pwj = main_products_using_pwj;
        this.related_sailer = related_sailer;
        this.used_pwjs = used_pwjs;
        this.nation = nation;
        this.province = province;
        this.prefectrue_level_city = prefectrue_level_city;
        this.county_level_city = county_level_city;
        this.town = town;
        this.village = village;
    }

    //一级列表，省和数量
    public Customer(String province, String count) {
        this.province = province;
        this.count = count;
    }

    public Customer(String companyname, String liaisons, String www, String address, String description, String main_products_using_pwj, String count,String lon, String lat) {
        this.companyname = companyname;
        this.liaisons = liaisons;
        this.www = www;
        this.address = address;
        this.description = description;
        this.main_products_using_pwj = main_products_using_pwj;
        this.count = count;
        this.lon = lon;
        this.lat = lat;
    }


    public String getCompanyname(){
        return this.companyname;
    }
    public String getLiaisons() {
        return this.liaisons;
    }
    public String getWww(){
        return this.www;
    }
    public String getDate(){
        return this.date;
    }
    public String getAddress(){
        return this.address;
    }
    public String getDescription(){
        return this.description;
    }
    public String getMain_products_using_pwj(){
        return this.main_products_using_pwj;
    }
    public String getRelated_sailer(){
        return this.related_sailer;
    }
    public String getUsed_pwjs(){
        return this.used_pwjs;
    }
    public String getNation(){
        return this.nation;
    }
    public String getProvince(){
        return this.province;
    }
    public String getPrefectrue_level_city(){
        return this.prefectrue_level_city;
    }
    public String getCounty_level_city(){
        return this.county_level_city;
    }
    public String getTown(){
        return this.town;
    }
    public String getVillage(){
        return this.village;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public String getCount() {
        return count;
    }

    public String getLon() {
        return lon;
    }

    public String getLat() {
        return lat;
    }
}
