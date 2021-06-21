package com.pwj.activity;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.bean.AppSpec;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.BuildConfig;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingUpdateActivity extends BaseActivity {

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    Button tv2;
    private List<AppSpec>data = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_setting_update);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText(getResources().getString(R.string.setting_update));
        tv1.setText("v "+versionName());
    }

    private int versionCode() {
        int versionCode = BuildConfig.VERSION_CODE;
        return versionCode;
    }

    private String versionName() {
        String versionName = BuildConfig.VERSION_NAME;
        return versionName;
    }

    @OnClick({R.id.title_im, R.id.tv2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;

            case R.id.tv2:
                queryVersionCode();
                break;
        }
    }

    private void queryVersionCode() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT versionCode FROM app")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data = GsonUtils.getGsonToList(str,AppSpec.class);
                        if (data.get(0).getVersionCode()>versionCode()){
                            jumpToMarket();
                        }else {
                            Util.showToast(SettingUpdateActivity.this,"您使用的版本已经是最新的了哦！");
                        }
                    }
                });
    }

    public void jumpToMarket() {
        String mAddress = "market://details?id=" + getPackageName();
        Intent marketIntent = new Intent("android.intent.action.VIEW");
        marketIntent.setData(Uri.parse(mAddress));
        if (marketIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(marketIntent);
        } else {
            //要调起的应用不存在时的处理
            Util.showToast(SettingUpdateActivity.this,"你要打开的软件不存在，请先下载");
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
