package com.pwj.interfaces;



import com.pwj.bean.Product;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/8/11.
 */

public class FormRunnable implements Runnable {
    private List<Product> data = new ArrayList<>();
    private ThreadCallback threadCallback;
    private int type;//表的type
    private String phone;
    public FormRunnable(ThreadCallback threadCallback, int type, String phone) {
        this.threadCallback = threadCallback;
        this.type = type;
        this.phone = phone;
    }

    @Override
    public void run() {
        switch (type) {
            case 0:
                data = Query.query_purchase_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
            case 1:
                data = Query.query_repair_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
            case 2:
                //查询招聘应聘表
                data = Query.query_recruit_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
            case 3:
                //查询租房需求聘表
                data = Query.query_rent_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
            case 4:
                //查询物流需求聘表
                data = Query.query_logistics_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
            case 5:
                //查询物流需求聘表
                data = Query.query_others_form(Jdbc.getConnection("root", "1q23lyc45j"),phone);
                threadCallback.threadEndLisener(data);
                break;
        }
    }
}
