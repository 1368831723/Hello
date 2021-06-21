package com.pwj.activity;




import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gyf.immersionbar.ImmersionBar;

import com.pwj.BaseActivity;
import com.pwj.dialog.DialogChooseLocation;
import com.pwj.fragment.BaseFragment;
import com.pwj.helloya.R;
import com.pwj.utils.Util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by han on 2018/8/17.
 */

public class BaseMapActivity extends BaseActivity implements View.OnClickListener, OnGetPoiSearchResultListener {

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.tv_location)
    TextView tv_location;

    @BindView(R.id.mMapView)
    MapView mMapView;
    @BindView(R.id.register_type1)
    LinearLayout register_type1;

    @BindView(R.id.liner_type2_2)
    LinearLayout liner_type2_2;
    private int type;

    public LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private MyLocationListener myListener;

    private String addr;

    private Connection connection = null;
    private double longitude;
    private double latitude;
    private LatLng point;
    private List<PoiInfo> poiInfos;
    private String city;
    private PoiSearch poiSearch;
    private PoiCitySearchOption poiCitySearchOption;
    private DialogChooseLocation dialog;
    private MapStatusUpdate mMapStatusUpdate;
    private Listener listener;
    private Intent intent;

    public interface Listener{
        void getLocation(String location,double longitude,double latitude);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_base_map);
        ButterKnife.bind(this);
        checkGPS();
    }

    private void checkGPS() {
        LocationManager manager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if(manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            //GPS已打开
            poiInfos = new ArrayList<>();
            initType();
            initListener();
            initMap();
        }else{
            //GPS未打开，跳转到GPS设置界面
            Util.showToast(this,"请打开GPS后在操作");
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            finish();
            //Toast.makeText(getActivity(), "请先打开GPS", 1).show();
        }
    }


    private void initListener() {
        register_type1.setOnClickListener(this);
//      tv_location.setOnClickListener(this);
        // 创建PoiSearch实例
        poiSearch = PoiSearch.newInstance();
        poiCitySearchOption = new PoiCitySearchOption();
        poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        myListener=new MyLocationListener();
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option1 = new LocationClientOption();
        option1.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option1);
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
            mLocationClient.start();
        }else {
            mLocationClient.start();
        }
    }

    private void initType() {
        title_tv.setText("选择位置");
        type = getIntent().getIntExtra("type", 2);
        switch (type) {
            case 1:

                break;
            case 2:

                break;
        }
    }

    @OnClick({R.id.title_im,R.id.confirm})
    public void onViewClicked(View view) {
        intent = new Intent();
        switch (view.getId()) {
            case R.id.title_im:
                BaseMapActivity.this.finish();
                break;
            case R.id.confirm:
                intent.putExtra("location",tv_location.getText().toString());
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                setResult(100,intent);
                BaseMapActivity.this.finish();
                break;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //设置poi检索监听者
        poiInfos = poiResult.getAllPoi();

        if (poiInfos != null && poiInfos.size() > 1) {

            dialog = new DialogChooseLocation(BaseMapActivity.this, city, R.style.dialog_choose, poiInfos, new DialogChooseLocation.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(String location) {
                    tv_location.setText(location);
                }
            });
            dialog.show();
            full_choose();
        } else {
            if (dialog != null) {
                dialog.show();
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
    }

    @Override
    public void onClick(View view) {
        String et = tv_location.getText().toString();

        poiCitySearchOption.keyword(et);
        poiCitySearchOption.city(city);
        poiCitySearchOption.pageCapacity(15);
        // 分页编号
        poiCitySearchOption.pageNum(3);
        poiSearch.searchInCity(poiCitySearchOption);
    }



    //设置弹窗宽度
    private void full_choose() {
        WindowManager windowManager = BaseMapActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取详细地址信息
            addr = location.getAddrStr();
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            //获取城市
            city = location.getCity();
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            tv_location.setText(addr);


            if (latitude < 1 || longitude < 1) {

            } else {
                //定义Maker坐标点
                point = new LatLng(latitude, longitude);
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.clear);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option2 = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .zIndex(9)
                        .draggable(true);
                //在地图上添加Marker，并显示
                final Marker marker = (Marker) mBaiduMap.addOverlay(option2);
                marker.setDraggable(false);
                MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                        .target(point)
                        .zoom(19)       //缩放级别
                        .build();  //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//            MapStatusUpdate mMapStatusUpdate=MapStatusUpdateFactory.newLatLngZoom(point,17.0f);
                mBaiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态
                mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {

                        marker.setPosition(mapStatus.target);

                        GeoCoder geocoder = GeoCoder.newInstance();
                        geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                        geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                            @Override
                            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                                //不调用
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                            poiInfos = reverseGeoCodeResult.getPoiList();
//                            Log.e(TAG, "onGetReverseGeoCodeResult周边城市: "+poiInfos+"--"+poiInfos.size());
                                String address = reverseGeoCodeResult.getAddress();

                                tv_location.setText(address);
                                if (!TextUtils.isEmpty(address)) {
                                    longitude = reverseGeoCodeResult.getLocation().longitude;
                                    latitude = reverseGeoCodeResult.getLocation().latitude;

                                    return;
                                }
                                Util.showToast(BaseMapActivity.this, "获取地理位置失败");
                            }
                        });
                        geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                    }//结束

                });
            }

        }

    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("BaseMapActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("BaseMapActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
