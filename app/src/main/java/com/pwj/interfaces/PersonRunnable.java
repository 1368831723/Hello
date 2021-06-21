package com.pwj.interfaces;



import com.pwj.bean.Product;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/8/11.
 */

public class PersonRunnable implements Runnable {
    private List<Product> data =new ArrayList<>();
    private ThreadCallback threadCallback;
    private int type;//表的type

    public PersonRunnable(ThreadCallback threadCallback, int type) {
        this.threadCallback = threadCallback;
        this.type = type;
    }

    @Override
    public void run() {
        switch (type) {
            case 0:
                data = Query.query_others(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 1:
                data = Query.query_purchase(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 2:
                data = Query.query_repair(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 3:
                //查询租场需求表
                data = Query.query_rent(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 4:
                //查询租场需求表
                data = Query.query_logistics(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 5:
                //查询招聘应聘表
                data = Query.query_recruit(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
//            case 3:
//                //查询租场需求表
//                data = Query.query_rent(Jdbc.getConnection("root", "1q23lyc45j"));
//                threadCallback.threadEndLisener(data);
//                break;

        }
    }
}
