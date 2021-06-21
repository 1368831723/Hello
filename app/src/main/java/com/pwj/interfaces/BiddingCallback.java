package com.pwj.interfaces;

import com.pwj.bean.Bidding;
import com.pwj.bean.BiddingCurrent;

import java.util.List;

/**
 * Created by han on 2018/7/25.
 */

public interface BiddingCallback {
    void threadEndLisener(List<Bidding> list);
}
