package com.pwj.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class RegisterAlready extends BaseActivity{
    @BindView(R.id.register_result_linear)
    LinearLayout register_result_linear;
    @BindView(R.id.register_success)
    TextView register_success;
    @BindView(R.id.tv)
    TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_register_already);
        ButterKnife.bind(this);
        tv.setText(getString(R.string.register_already)+getIntent().getStringExtra("pwd"));
    }


    @OnClick({R.id.title_im,R.id.register_success})
    public void onViewClicked(View view) {
        RegisterAlready.this.finish();
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RegisterAlready"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RegisterAlready");
//        MobclickAgent.onPause(this); //统计时长
//    }

}
