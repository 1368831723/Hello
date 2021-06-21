package com.pwj.bean;

/**
 * Created by 13688 on 2018/11/16.
 */

public class Countys {
    private String county;
    private String count;

    public Countys(String county, String count) {
        this.county = county;
        this.count = count;
    }

    public String getCounty(){
        return county;
    }

    public String getCount() {
        return count;
    }
}
