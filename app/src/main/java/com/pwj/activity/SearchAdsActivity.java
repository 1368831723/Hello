package com.pwj.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.jdbc.Insert;
import com.pwj.jdbc.Jdbc;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SearchUtil;

import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;


import java.sql.Connection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 13688 on 2019/5/9.
 */

public class SearchAdsActivity extends AppCompatActivity {
    @BindView(R.id.title_im)
    ImageView title_im;
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    private String keyWords;
    private String uuid;
    private Connection connection;
    private String page = "";
    private String brand = "";
    private String model = "";
    private String os_version = "";
    private String dates;
    private String locations = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ads);
        ButterKnife.bind(this);
        Keyboard.getInstance().openKeyBoard(et_search);
        initDate();
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    private void initDate() {
        page = "交易搜索";
        title_im.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        tv_search.setVisibility(View.GONE);
        img_issue.setVisibility(View.GONE);
        dates = SearchUtil.getInstance().getNowDate();
        brand = SearchUtil.getInstance().getSystemBrand();
        model = SearchUtil.getInstance().getSystemModel();
        os_version = SearchUtil.getInstance().getSystemVersion();
        initLocations();
//        locations = initLocations();
    }

    @OnClick({R.id.title_im, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_search:
                keyWords = et_search.getText().toString().trim();
                if (keyWords.equals("")) {
                    Util.showToast(SearchAdsActivity.this, "请输入关键词");
                } else {
                    insertData();
                    startActivity(SearchAdsResultActivity.class, keyWords);
                    finish();
                }

                break;
        }
    }

    private void insertData() {
        uuid = LoginInfo.getString(this, "uuid", "");
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void initLocations() {
        SearchUtil.getInstance().initLocation(SearchAdsActivity.this, new StringCallbackOne() {
            @Override
            public void stringOne(String str, double longitude, double latitude) {
                locations = str;
            }
        });
    }

    private void startActivity(Class<?> activity, String keyWords) {
        Intent intent = new Intent();
        intent.setClass(SearchAdsActivity.this, activity);
        intent.putExtra("keyWords", keyWords);
        startActivity(intent);
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
