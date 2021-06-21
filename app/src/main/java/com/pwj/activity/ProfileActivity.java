package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.et_profile)
    EditText et_profile;
    @BindView(R.id.define)
    Button define;
    private String phone;
    private String profile = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_profile);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText("修改用户名");
        phone = LoginInfo.getString(this, "phone", "");
        profile = getIntent().getStringExtra("profile");
        et_profile.setText(profile);
        if (profile!=null){
            et_profile.setSelection(profile.length());
        }
    }

    @OnClick({R.id.title_im, R.id.define})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.define:
                upDate();
                break;
        }
    }

    private void upDate() {
        profile = et_profile.getText().toString().trim();
        if (!"".equals(profile)) {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "update login set profile = '" + profile + "' where phone = " + phone + "")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Util.showToast(ProfileActivity.this, "修改成功");
                            Intent intent = new Intent();
                            intent.putExtra("profile",profile);
                            setResult(100,intent);
                            finish();
                        }
                    });
        } else {
            Util.showToast(ProfileActivity.this, "用户名不能为空");
        }
    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SettingAboutActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SettingAboutActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
