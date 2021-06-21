package com.pwj.interfaces;
import com.pwj.bean.BiddingHistory;

import java.util.List;

/**
 * Created by han on 2018/7/25.
 */

public interface BiddingHistoryCallback {
//    void threadStartLisener();
    void threadEndLisener(List<BiddingHistory> list);
}
