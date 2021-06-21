package com.pwj.bean;

/**
 * Created by 13688 on 2018/11/16.
 */

public class Provinces {
    private String province;
    private String count;

    public Provinces(String province, String count) {
        this.province = province;
        this.count = count;
    }

    public String getProvince() {
        return province;
    }

    public String getCount() {
        return count;
    }
}
