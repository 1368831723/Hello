package com.pwj.bean;

/**
 * Created by 13688 on 2018/11/16.
 */

public class Share {
    private int img ;
    private String name;

    public Share(int img, String name) {
        this.img = img;
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
