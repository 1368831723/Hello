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
        showInfo = "??????????????? " + customer.getCompanyname() + "\n" +
                "??????????????? " + customer.getLiaisons() + "\n" +
                "????????? " + customer.getWww() + "\n" +
                "????????? " + customer.getAddress() + "\n" +
                "??????????????? " + customer.getDescription() + "\n" +
                "????????????????????????????????? " + customer.getMain_products_using_pwj() + "\n" +
                "????????????????????? " + customer.getRelated_sailer() + "\n" +
                "???????????????????????? " + customer.getUsed_pwjs() + "\n";

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
//        //??????LocationClient???
//        mLocationClient.registerLocationListener(myListener);
//        //??????????????????
//        LocationClientOption option = new LocationClientOption();
//
//        option.setIsNeedAddress(true);
//        //?????????????????????????????????????????????????????????????????????false
//        //?????????????????????????????????????????????????????????????????????true
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

        //??????Maker?????????
        LatLng point = new LatLng(com_latitude, com_longitude);
        //??????Marker??????
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_pointer_24);
        //??????MarkerOption???????????????????????????Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //??????????????????Marker????????????
        mBaiduMap.addOverlay(option);
        MapStatus mMapStatus = new MapStatus.Builder()//??????????????????
                .target(point)
                .zoom(11)       //????????????
                .build();  //??????MapStatusUpdate??????????????????????????????????????????????????????
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);//??????????????????
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //?????????BDLocation?????????????????????????????????????????????get??????????????????????????????????????????
            //??????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????BDLocation???????????????

            //????????????????????????
            String country = location.getCountry();    //????????????
            String province = location.getProvince();    //????????????
            String city = location.getCity();    //????????????
            String district = location.getDistrict();    //????????????
            String street = location.getStreet();    //??????????????????
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
                                    intent1 = Intent.getIntent("intent://map/direction?origin=latlng:" + my_latitude + "," + my_longitude + "|name:" + "????????????" + "&destination=latlng:" + com_latitude + "," + com_longitude + "|name:" + customer.getCompanyname() + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
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

    //??????????????????
    private void full_choose_map() {
        WindowManager windowManager = InfoPathActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //????????????
        dialogChooseMap.getWindow().setAttributes(lp);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("InfoPathActivity"); //????????????("MainScreen"??????????????????????????????)
//        MobclickAgent.onResume(this); //????????????
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("InfoPathActivity");
//        MobclickAgent.onPause(this); //????????????
//    }
}
