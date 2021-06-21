package com.pwj.bean;

import android.widget.CheckBox;

/**
 * Created by han on 2018/9/3.
 */

public class Home {
    private boolean isCheck;
    private int img;
    private int title;

    public Home(boolean isCheck, int img, int title) {
        this.isCheck = isCheck;
        this.img = img;
        this.title = title;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }
}
