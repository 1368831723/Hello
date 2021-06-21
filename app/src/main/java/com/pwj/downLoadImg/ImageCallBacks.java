package com.pwj.downLoadImg;

/**
 * Created by CWJ on 2017/3/8.
 */

public interface ImageCallBacks {
    void onSuccess(int i, int j, String url);
    void onFailed();
}
