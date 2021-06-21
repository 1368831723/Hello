package com.pwj.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageMap implements Serializable {
    private HashMap<String, Object> map;

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }
}
