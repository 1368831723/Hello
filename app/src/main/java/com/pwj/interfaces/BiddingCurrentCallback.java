package com.pwj.interfaces;

import com.pwj.bean.BiddingCurrent;
import java.util.List;

/**
 * Created by han on 2018/7/25.
 */

public interface BiddingCurrentCallback {
//    void threadStartLisener();
    void threadEndLisener(List<BiddingCurrent> list);
}
