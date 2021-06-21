package com.pwj.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.eventbus.EventIssue;
import com.pwj.eventbus.EventRegister;
import com.pwj.helloya.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


/**
 * Created by delphi0127 on 2018/7/15.
 */

public class RegisterSuccess extends BaseActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_register_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

    }

    @OnClick({R.id.title_im,R.id.register_success})
    public void onViewClicked(View view) {
        RegisterSuccess.this.finish();
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
