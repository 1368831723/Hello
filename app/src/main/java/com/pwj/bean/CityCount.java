package com.pwj.bean;

/**
 * Created by han on 2018/11/8.
 */

public class CityCount {
    private String province;
    private String count;

    public CityCount(String province, String count) {
        this.province = province;
        this.count = count;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
