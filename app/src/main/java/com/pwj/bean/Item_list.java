package com.pwj.bean;

/**
 * Created by han on 2018/9/3.
 */

public class Item_list {
    private String title;
    private int img;

    public Item_list(String title, int img) {
        this.title = title;
        this.img = img;
    }
    public Item_list( int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
