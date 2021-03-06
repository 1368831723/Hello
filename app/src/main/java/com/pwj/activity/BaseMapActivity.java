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
            //GPS?????????
            poiInfos = new ArrayList<>();
            initType();
            initListener();
            initMap();
        }else{
            //GPS?????????????????????GPS????????????
            Util.showToast(this,"?????????GPS????????????");
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            finish();
            //Toast.makeText(getActivity(), "????????????GPS", 1).show();
        }
    }


    private void initListener() {
        register_type1.setOnClickListener(this);
//      tv_location.setOnClickListener(this);
        // ??????PoiSearch??????
        poiSearch = PoiSearch.newInstance();
        poiCitySearchOption = new PoiCitySearchOption();
        poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        myListener=new MyLocationListener();
        //??????LocationClient???
        mLocationClient.registerLocationListener(myListener);
        //??????????????????
        LocationClientOption option1 = new LocationClientOption();
        option1.setIsNeedAddress(true);
        //?????????????????????????????????????????????????????????????????????false
        //?????????????????????????????????????????????????????????????????????true
        mLocationClient.setLocOption(option1);
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
            mLocationClient.start();
        }else {
            mLocationClient.start();
        }
    }

    private void initType() {
        title_tv.setText("????????????");
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
        //??????poi???????????????
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
        // ????????????
        poiCitySearchOption.pageNum(3);
        poiSearch.searchInCity(poiCitySearchOption);
    }



    //??????????????????
    private void full_choose() {
        WindowManager windowManager = BaseMapActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //????????????
        dialog.getWindow().setAttributes(lp);
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //?????????BDLocation?????????????????????????????????????????????get??????????????????????????????????????????
            //??????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????BDLocation???????????????

            //????????????????????????
            addr = location.getAddrStr();
            String country = location.getCountry();    //????????????
            String province = location.getProvince();    //????????????
            //????????????
            city = location.getCity();
            String district = location.getDistrict();    //????????????
            String street = location.getStreet();    //??????????????????
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            tv_location.setText(addr);


            if (latitude < 1 || longitude < 1) {

            } else {
                //??????Maker?????????
                point = new LatLng(latitude, longitude);
                //??????Marker??????
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.clear);
                //??????MarkerOption???????????????????????????Marker
                OverlayOptions option2 = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .zIndex(9)
                        .draggable(true);
                //??????????????????Marker????????????
                final Marker marker = (Marker) mBaiduMap.addOverlay(option2);
                marker.setDraggable(false);
                MapStatus mMapStatus = new MapStatus.Builder()//??????????????????
                        .target(point)
                        .zoom(19)       //????????????
                        .build();  //??????MapStatusUpdate??????????????????????????????????????????????????????
                mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//            MapStatusUpdate mMapStatusUpdate=MapStatusUpdateFactory.newLatLngZoom(point,17.0f);
                mBaiduMap.setMapStatus(mMapStatusUpdate);//??????????????????
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
                                //?????????
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                            poiInfos = reverseGeoCodeResult.getPoiList();
//                            Log.e(TAG, "onGetReverseGeoCodeResult????????????: "+poiInfos+"--"+poiInfos.size());
                                String address = reverseGeoCodeResult.getAddress();

                                tv_location.setText(address);
                                if (!TextUtils.isEmpty(address)) {
                                    longitude = reverseGeoCodeResult.getLocation().longitude;
                                    latitude = reverseGeoCodeResult.getLocation().latitude;

                                    return;
                                }
                                Util.showToast(BaseMapActivity.this, "????????????????????????");
                            }
                        });
                        geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                    }//??????

                });
            }

        }

    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("BaseMapActivity"); //????????????("MainScreen"??????????????????????????????)
//        MobclickAgent.onResume(this); //????????????
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("BaseMapActivity");
//        MobclickAgent.onPause(this); //????????????
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
