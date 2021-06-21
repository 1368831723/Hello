package com.pwj.activity;


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
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UpdateNameActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.define)
    Button define;
    private String phone;
    private String user_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_update_name);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText("修改用户名");
        user_name = LoginInfo.getString(this, "user_name", "");
        phone = LoginInfo.getString(this, "phone", "");
        et_name.setText(user_name);
        et_name.setSelection(user_name.length());
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
        user_name = et_name.getText().toString().trim();
        if (!"".equals(user_name)) {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "update login set user_name = '" + user_name + "' where phone = " + phone + "")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Util.showToast(UpdateNameActivity.this, "修改成功");
                            LoginInfo.setString(UpdateNameActivity.this, "user_name", user_name);
                            finish();
                        }
                    });
        } else {
            Util.showToast(UpdateNameActivity.this, "用户名不能为空");
        }
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
