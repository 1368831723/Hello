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


public class SettingPolicyActivity extends BaseActivity {

    @BindView(R.id.setting_policy)
    TextView setting_policy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_setting_policy);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText(getResources().getString(R.string.setting_policy));
        setting_policy.setText(R.string.policy);
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
