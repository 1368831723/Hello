package com.pwj.bean;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/8/11.
 */

public class Product implements Serializable {
    private String url3;      //每张表的用户图像
    private String user_name; //每张表的用户名
    private String profile; //个人简介
    private String job; //职务
    private int id;          //表的主键
    private String table_name;
    private String table;          //表的中文类别
    private String url;
    private int url_type;
    private String contact; //联系人名字
    private String phone; //联系人电话
    private String phone_address; //联系人电话
    private String consign; //托运
    private String postage; //邮费
    private String location;    //位置
    private String province;
    private String city;
    private String county;
    private String street;
    private String specific;
    private String product_name;    //产品中文名
    private String brand;    //品牌
    private String type;    //类型
    private String spec;     //规格
    private String price;    //价格
    private String description;  //描述
    private String content;   //表中的内容，包含所以字段的值
    private String date;    //发布日期
    private int number;
    private String lon;
    private String goods;     //产品名称
    @SerializedName("long") //长
    private String longs;
    private String wide;    //宽
    private String thickness;  //厚
    private String diameter;  //直径

    private String name;        //需求物品名称
    private String duration;    //租赁时长
    private String destination;    //目的地
    private String area;
    private String location_start;
    private String location_end;
    private String start_time;
    private String lcn_remark;  //位置补充
    private String remarks;     //需求补充
    private String status;  //订单状态
    private int comment;    //评论数
    private int like;    //点赞数
    private int like_person;
    private List<String> uri_list = new ArrayList<>();    //图片集合
    private double longitude;
    private double latitude;
    private String issue_date; //发布日期
    private String valid_date; //发布日期
    private String identity;//招聘应聘的身份
    //查询商品种类的表 n张图片的集合
    public Product(List<String> uri_list, String spec, String lon, String wide, String thickness, String price, String contact, String phone, String consign, String postage, int type) {
        this.uri_list = uri_list;
        this.spec = spec;
        this.lon = lon;
        this.wide = wide;
        this.thickness = thickness;
        this.price = price;
        this.contact = contact;
        this.phone = phone;
        this.consign = consign;
        this.postage = postage;
        this.number = type;
    }


    //查询租厂需求的表
    public Product(List<String> uri_list, String contact, String phone, String area, String price, String location) {
        this.uri_list = uri_list;
        this.contact = contact;
        this.phone = phone;
        this.area = area;
        this.price = price;
        this.location = location;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable() {
        return table;
    }

    public String getUrl3() {
        return url3;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getProfile() {
        return profile;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getUrl_type() {
        return url_type;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getBrand() {
        return brand;
    }

    public int getNumber() {
        return number;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public String getValid_date() {
        return valid_date;
    }

    public String getSpec() {
        return spec;
    }

    public String getLon() {
        return lon;
    }

    public String getGoods() {
        return goods;
    }

    public String getLongs() {
        return longs;
    }


    public String getWide() {
        return wide;
    }

    public String getThickness() {
        return thickness;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhone_address() {
        return phone_address;
    }

    public String getConsign() {
        return consign;
    }

    public String getPostage() {
        return postage;
    }



    //8个参数，1个int，8个String
    //查询采购需求，维修需求的表 ，求租需求的表    //共用的
    public Product(int id, List<String> uri_list, String contact, String phone, String location, String lcn_remark, String remarks, String status, int type) {
        this.id = id;
        this.uri_list = uri_list;
        this.contact = contact;
        this.phone = phone;
        this.location = location;
        this.lcn_remark = lcn_remark;
        this.remarks = remarks;
        this.status = status;
        this.number = type;
    }

    //10个参数，1个int，9个String
    //查询物流需求的表
    public Product(int id, List<String> uri_list, String contact, String phone, String location, String name, String destination, String remarks, String status, int type) {
        this.id = id;
        this.uri_list = uri_list;
        this.contact = contact;
        this.phone = phone;
        this.location = location;
        this.name = name;
        this.destination = destination;
        this.remarks = remarks;
        this.status = status;
        this.number = type;
    }


    //查询招聘、应聘的表
    public Product(int id, String contact, String phone, String issue_date, String valid_date, String location, String identity, String remarks, String status, double longitude, double latitude, int type) {
        this.id = id;
        this.contact = contact;
        this.phone = phone;
        this.issue_date = issue_date;
        this.valid_date = valid_date;
        this.location = location;
        this.identity = identity;
        this.remarks = remarks;
        this.status = status;
        this.longitude = longitude;
        this.latitude = latitude;
        this.number = type;
    }


    public String getArea() {
        return area;
    }

    public String getIdentity() {
        return identity;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getStreet() {
        return street;
    }

    public String getSpecific() {
        return specific;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getDestination() {
        return destination;
    }

    public String getLocation_start() {
        return location_start;
    }

    public String getLocation_end() {
        return location_end;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getLcn_remark() {
        return lcn_remark;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStatus() {
        return status;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public List<String> getUri_list() {
        return uri_list;
    }

    public String getType() {
        return type;
    }

    public int getComment() {
        return comment;
    }

    public int getLike() {
        return like;
    }

    public int getLike_person() {
        return like_person;
    }

    public void setLike_person(int like_person) {
        this.like_person = like_person;
    }

    public String getJob() {
        return job;
    }
}
