package com.pwj.bean;

/**
 * Created by 13688 on 2019/8/29.
 */

public class Item_purchase {
    private String title;
    private int love_tag;

    public Item_purchase(String title) {
        this.title = title;
    }

    public Item_purchase(String title, int love_tag) {
        this.title = title;
        this.love_tag = love_tag;
    }

    public String getTitle() {
        return title;
    }

    public int getLove_tag() {
        return love_tag;
    }

    public void setLove_tag(int love_tag) {
        this.love_tag = love_tag;
    }
}
