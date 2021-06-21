package com.pwj.callBack;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import com.pwj.utils.Util;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.IProgressDialog;

/**
 * Created by 13688 on 2019/6/25.
 */

public abstract class SuccessCallBack<T> extends CallBack<T> {
    private Activity activity = null;
    private ProgressBar progressBar = null;
    //默认不用回调失败方法
    public SuccessCallBack() {

    }
    //回调失败方法提醒用户
    public SuccessCallBack(Activity activity , ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onError(ApiException e) {
        if (activity!=null){
            Util.showToast(activity,"对不起,网络异常,请检查网络连接");
        }
        if (progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
    }

}
