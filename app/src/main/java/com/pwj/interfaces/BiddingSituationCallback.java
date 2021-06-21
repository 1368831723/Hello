package com.pwj.interfaces;
import com.pwj.bean.BiddingSituation;

import java.util.List;

/**
 * Created by han on 2018/7/25.
 */

public interface BiddingSituationCallback {
//    void threadStartLisener();
    void threadEndLisener(List<BiddingSituation> list);
}
