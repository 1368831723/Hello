package com.pwj.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pwj.BaseActivity;

import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;

import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.exception.ApiException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BusinessActivity extends BaseActivity {

    @BindView(R.id.about_phone)
    TextView about_phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_business);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

    }

    @OnClick({R.id.title_im, R.id.about_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.about_phone:
                EasyHttp.post("http://119.18.207.203:80/server_mariadb_ios_use/sendSMS.php")
                        .params("phone", "15337232763")
                        .params("smsCode", "666666")
                        .timeStamp(true)
                        .execute(new SuccessCallBack<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Log.e("发送成功",""+s);
                            }

                            @Override
                            public void onError(ApiException e) {
                                Log.e("发送失败",""+e.getMessage());
                            }
                        });
                break;
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
