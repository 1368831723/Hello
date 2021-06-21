package com.pwj.bean;

import com.pwj.bean.Customer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jun on 2018/6/29.
 */

public class ProvinceCityCustomer implements Serializable{
    String province;
    String prefecture_level_city;
    String county_level_city;
    int count;
    public ArrayList<Customer> listCustomers;

    public ProvinceCityCustomer(){
        this.province = null;
        this.prefecture_level_city = null;
        this.county_level_city = null;
        count = 0;
        listCustomers = new ArrayList<Customer>();
    }

    public String getProvince(){
        return this.province;
    }

    public String getPrefecture_level_city(){
        return this.prefecture_level_city;
    }

    public String getCounty_level_city(){
        return this.county_level_city;
    }

    public int getCount(){
        return this.count;
    }

    public ArrayList<Customer> getListCustomers(){
        return this.listCustomers;
    }

    public void setProvince(String s){
        this.province = s;
    }

    public void setPrefecture_level_city(String s){
        this.prefecture_level_city = s;
    }

    public void setCounty_level_city(String s){
        this.county_level_city = s;
    }

    public void setCount(int count){
        this.count = count;
    }

    public void setListCustomers(ArrayList<Customer> arrayList){
        this.listCustomers = arrayList;
    }
}
