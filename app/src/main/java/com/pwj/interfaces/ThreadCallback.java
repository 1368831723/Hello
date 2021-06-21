package com.pwj.interfaces;


import com.pwj.bean.Product;

import java.util.List;

/**
 * Created by han on 2018/7/25.
 */

public interface ThreadCallback {
//    void threadStartLisener();
    void threadEndLisener(List<Product> list);
}
