package com.pwj.interfaces;

import com.pwj.bean.Bidding;
import com.pwj.bean.BiddingCurrent;
import com.pwj.bean.BiddingHistory;
import com.pwj.bean.BiddingSituation;
import com.pwj.bean.Product;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/8/11.
 */

public class QueryRunnable implements Runnable {
    private List<Product> data = new ArrayList<>();
    private List<Bidding>list_bidding;
    private List<BiddingCurrent>list_current;
    private List<BiddingSituation>list_situation;
    private List<BiddingHistory>list_history;
    private ThreadCallback threadCallback;
    private BiddingCallback biddingCallback;
    private BiddingCurrentCallback biddingCurrentCallback;
    private BiddingHistoryCallback biddingHistoryCallback;
    private BiddingSituationCallback biddingSituationCallback;
    private int type;//表的type
    private int field=0;//表的type
    private String company;//Situation态势图页面需要按照公司名字查询表格
    public QueryRunnable(ThreadCallback threadCallback, int type, int field) {
        this.threadCallback = threadCallback;
        this.type = type;
        this.field = field;
    }
    public QueryRunnable(BiddingCallback biddingCallback, int type) {
        list_bidding = new ArrayList<>();
        this.biddingCallback = biddingCallback;
        this.type = type;
    }
    public QueryRunnable(BiddingCurrentCallback biddingCurrentCallback, int type) {
        list_current = new ArrayList<>();
        this.biddingCurrentCallback = biddingCurrentCallback;
        this.type = type;
    }

    public QueryRunnable(BiddingSituationCallback biddingSituationCallback, int type) {
        list_situation = new ArrayList<>();
        this.biddingSituationCallback = biddingSituationCallback;
        this.type = type;
    }
    public QueryRunnable(BiddingHistoryCallback biddingHistoryCallback, int type) {
        list_history = new ArrayList<>();
        this.biddingHistoryCallback = biddingHistoryCallback;
        this.type = type;
    }

    public QueryRunnable(BiddingCurrentCallback biddingCurrentCallback, int type, String company) {
        list_current = new ArrayList<>();
        this.biddingCurrentCallback = biddingCurrentCallback;
        this.type = type;
        this.company = company;
    }

    @Override
    public void run() {
        switch (type) {
            case 0:
                data = Query.query0(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 1:
                data = Query.query1(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 2:
                data = Query.query2(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 3:
                data = Query.query3(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 4:
                data = Query.query4(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 5:
                data = Query.query5(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 6:
                data = Query.query6(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 7:
                data = Query.query7(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 8:
                data = Query.query8(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 9:
                data = Query.query9(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 10:
                data = Query.query10(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 11:
                data = Query.query11(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 12:
                data = Query.query12(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 13:
                data = Query.query13(Jdbc.getConnection("root", "1q23lyc45j"));
                threadCallback.threadEndLisener(data);
                break;
            case 14:
                data = Query.query14(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 15:
                data = Query.query15(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 16:
                data = Query.query16(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 17:
                data = Query.query17(Jdbc.getConnection("root", "1q23lyc45j"),field);
                threadCallback.threadEndLisener(data);
                break;
            case 20:
                list_current = Query.query_current(Jdbc.getConnection("root", "1q23lyc45j"));
                biddingCurrentCallback.threadEndLisener(list_current);
                break;
            case 21:
                list_situation = Query.query_situation(Jdbc.getConnection("root", "1q23lyc45j"));
//                biddingSituationCallback.threadEndLisener(list_situation);
                break;
            case 22:
                list_history = Query.query_history(Jdbc.getConnection("root", "1q23lyc45j"));
                biddingHistoryCallback.threadEndLisener(list_history);
                break;
        }
    }
}
