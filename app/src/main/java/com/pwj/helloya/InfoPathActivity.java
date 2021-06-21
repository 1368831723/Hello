package com.pwj.helloya;

import android.content.Intent;
import android.os.Bundle;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.pwj.BaseActivity;
import com.pwj.bean.Customer;
import com.pwj.utils.LoginInfo;
import com.pwj.dialog.DialogChooseMap;

import java.io.File;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InfoPathActivity extends BaseActivity{
    @BindView(R.id.title_im)
    LinearLayout title_im;
    @BindView(R.id.info_go)
    TextView info_go;
    TextView textView;
    BaiduMap mBaiduMap;
    MapView mMapView;
    String showInfo;
    private double my_longitude = 0.0;
    private double my_latitude = 0.0;
    private double com_longitude = 0.0;
    private double com_latitude = 0.0;
    private Customer customer;
    private boolean baiDu;
    private boolean gaoDe;
    private DialogChooseMap dialogChooseMap;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_info_path);
        ButterKnife.bind(this);
        textView = (TextView) findViewById(R.id.info_path_textView);
        mMapView = (MapView) findViewById(R.id.path_bmapView);
        mBaiduMap = mMapView.getMap();
        customer = (Customer) getIntent().getSerializableExtra("customer_info");
        showInfo = "公司名称： " + customer.getCompanyname() + "\n" +
                "联系方式： " + customer.getLiaisons() + "\n" +
                "网址： " + customer.getWww() + "\n" +
                "地址： " + customer.getAddress() + "\n" +
                "公司介绍： " + customer.getDescription() + "\n" +
                "主要使用抛丸机产品为： " + customer.getMain_products_using_pwj() + "\n" +
                "有关销售人员： " + customer.getRelated_sailer() + "\n" +
                "已有抛丸机销售： " + customer.getUsed_pwjs() + "\n";

        textView.setText(showInfo);
        initLocation();
        initView();
    }

    private void initLocation() {
        String latitude = LoginInfo.getString(InfoPathActivity.this, "latitude", "");
        String longitude = LoginInfo.getString(InfoPathActivity.this, "longitude", "");
        my_latitude = Double.parseDouble(latitude);
        my_longitude = Double.parseDouble(longitude);
//        mLocationClient = new LocationClient(getApplicationContext());
//        //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);
//        //注册监听函数
//        LocationClientOption option = new LocationClientOption();
//
//        option.setIsNeedAddress(true);
//        //可选，是否需要地址信息，默认为不需要，即参数为false
//        //如果开发者需要获得当前点的地址信息，此处必须为true
//
//        mLocationClient.setLocOption(option);
    }

    private void initView() {
        com_latitude = customer.getLatitude();
        com_longitude = customer.getLongitude();
        if (com_longitude < 1 && com_latitude < 1) {
            com_latitude = Double.parseDouble(LoginInfo.getString(this, "com_latitude", ""));
            com_longitude = Double.parseDouble(LoginInfo.getString(this, "com_longitude", ""));
        }

        //定义Maker坐标点
        LatLng point = new LatLng(com_latitude, com_longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_pointer_24);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                .target(point)
                .zoom(11)       //缩放级别
                .build();  //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
//            my_latitude = location.getLatitude();
//            my_longitude = location.getLongitude();
        }
    }

    @OnClick({R.id.title_im, R.id.info_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                InfoPathActivity.this.finish();
                break;
            case R.id.info_go:
                checkApp();
                dialogChooseMap = new DialogChooseMap(InfoPathActivity.this, baiDu, gaoDe, R.style.dialog_choose, new DialogChooseMap.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int id) {
                        switch (id) {
                            case R.id.dialog_bai_du:
                                Intent intent1 = null;
                                try {
                                    intent1 = Intent.getIntent("intent://map/direction?origin=latlng:" + my_latitude + "," + my_longitude + "|name:" + "我的位置" + "&destination=latlng:" + com_latitude + "," + com_longitude + "|name:" + customer.getCompanyname() + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent1);

                                break;
                            case R.id.dialog_gao_de:
                                Intent intent2 = null;
                                try {
                                    intent2 = Intent.getIntent("androidamap://navi?sourceApplication=appname&poiname="+customer.getCompanyname()+"&lat=" +
                                            com_latitude + "&lon=" + com_longitude + "&dev=1&style=2");
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent2);
                                break;
                        }
                    }
                });
                dialogChooseMap.show();
                full_choose_map();
                break;
        }

    }

    private void checkApp() {
        if (isInstallByread("com.baidu.BaiduMap")) {
            baiDu = true;
        } else {
            baiDu = false;
        }
        if (isInstallByread("com.autonavi.minimap")) {
            gaoDe = true;
        } else {
            gaoDe = false;
        }
    }

    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    //设置弹窗宽度
    private void full_choose_map() {
        WindowManager windowManager = InfoPathActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogChooseMap.getWindow().setAttributes(lp);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("InfoPathActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("InfoPathActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
