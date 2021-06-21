package com.pwj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.pwj.BaseActivity;

import com.pwj.helloya.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



/**
 * Created by delphi0127 on 2018/7/15.
 */

public class IssueSuccess extends BaseActivity{
    @BindView(R.id.issue_result_linear)
    LinearLayout issue_result_linear;
    @BindView(R.id.issue_success)
    TextView issue_success;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_issue_success);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.title_im,R.id.issue_success})
    public void onViewClicked(View view) {
        setResult(15);
        IssueSuccess.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(15);
            IssueSuccess.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("IssueSuccess"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("IssueSuccess");
//        MobclickAgent.onPause(this); //统计时长
//    }

}
