package com.pwj.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.helloya.R;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingAboutActivity extends BaseActivity {

    @BindView(R.id.setting_about)
    TextView setting_about;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_setting_about);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText(getResources().getString(R.string.setting_about));
        setting_about.setText(R.string.advertise);
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

}
