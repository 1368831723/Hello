package com.pwj.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import com.pwj.BaseActivity;
import com.pwj.baidu.MyDrivingRouteOverlay;
import com.pwj.helloya.R;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class RoutePlanningActivity extends BaseActivity implements OnGetRoutePlanResultListener {

    @BindView(R.id.about_phone)
    TextView about_phone;
    @BindView(R.id.mMapView)
    MapView mMapView;
    private RoutePlanSearch mSearch;
    private BaiduMap mBaiduMap;
    private double my_longitude = 0.0;
    private double my_latitude = 0.0;
    private double com_longitude = 0.0;
    private double com_latitude = 0.0;
    private LatLng start;
    private LatLng end;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planning);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        my_latitude = getIntent().getDoubleExtra("my_latitude", 0.0);
        my_longitude = getIntent().getDoubleExtra("my_longitude", 0.0);
        com_latitude = getIntent().getDoubleExtra("com_latitude", 0.0);
        com_longitude = getIntent().getDoubleExtra("com_longitude", 0.0);
    }

    private void initData() {
        start = new LatLng(my_latitude, my_longitude);
        end = new LatLng(com_latitude, com_longitude);

        //???????????????????????????????????????
        mSearch = RoutePlanSearch.newInstance();
        //2?????????????????????????????????????????? //3??????????????????????????????????????????
        mSearch.setOnGetRoutePlanResultListener(this);
        //4?????????????????????????????????
        PlanNode stNode = PlanNode.withLocation(start);

        PlanNode enNode = PlanNode.withLocation(end);
        //5?????????????????????????????????
        mSearch.drivingSearch((new DrivingRoutePlanOption())

                .from(stNode)
                .to(enNode));

    }

    private void startSearch(LatLng start,LatLng end) {
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
    }
//    private void startSearch() {
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("??????", "??????");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("??????", "??????");
//        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //6?????????????????????
        mSearch.destroy();
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        //TODO: ??????????????????????????????
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //?????????????????????????????????????????????????????????????????????????????????
            //result.getSuggestAddrInfo()
            Log.d("baiduMap", "????????????????????????????????????");
            return;
        }
        if (result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
            //????????????????????????????????????
            Log.d("baiduMap", "?????????????????????,????????????");
            startSearch(start,end);
            return;
        }
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanningActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
//            startSearch(start,end);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteLine route = result.getRouteLines().get(0);
            MyDrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(route);
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RoutePlanningActivity"); //????????????("MainScreen"??????????????????????????????)
//        MobclickAgent.onResume(this); //????????????
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RoutePlanningActivity");
//        MobclickAgent.onPause(this); //????????????
//    }
}
