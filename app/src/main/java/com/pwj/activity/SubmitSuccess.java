package com.pwj.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pwj.BaseActivity;
import com.pwj.helloya.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by delphi0127 on 2018/7/15.
 */

public class SubmitSuccess extends BaseActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_submit_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

    }

    @OnClick({R.id.title_im,R.id.update_success})
    public void onViewClicked(View view) {
        SubmitSuccess.this.finish();
    }


//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RegisterSuccess"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RegisterSuccess");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
