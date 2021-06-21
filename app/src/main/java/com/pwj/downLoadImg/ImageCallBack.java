package com.pwj.downLoadImg;

/**
 * Created by CWJ on 2017/3/8.
 */

public interface ImageCallBack {
    void onSuccess(int i,String url);
    void onFailed();
}
