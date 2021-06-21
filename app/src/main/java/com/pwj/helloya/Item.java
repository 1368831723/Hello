package com.pwj.helloya;

/**
 * Created by jun on 2018/6/3.
 */

public class Item {
    private int imgId;
    private int textId;

    public Item(int imgId, int textId){
        this.imgId = imgId;
        this.textId = textId;
    }

    public void setImgId(int imgId){
        this.imgId = imgId;
    }

    public void setText(int text){
        this.textId = text;
    }

    public int getImgId(){
        return imgId;
    }

    public int getTextId(){
        return textId;
    }
}
