package com.pwj.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import com.pwj.BaseActivity;
import com.pwj.helloya.R;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class GeoCoderDemo extends BaseActivity {

    @BindView(R.id.mMapView)
    MapView mMapView;
    private BaiduMap mbaiduMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_test);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mbaiduMap = mMapView.getMap();
    }

    @OnClick()
    public void onViewClicked(View view) {
        finish();
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("GeoCoderDemo"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("GeoCoderDemo");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
