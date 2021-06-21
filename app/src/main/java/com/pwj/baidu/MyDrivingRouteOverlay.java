package com.pwj.baidu;

import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.pwj.helloya.R;

/**
 * Created by han on 2018/9/15.
 */

public class MyDrivingRouteOverlay extends DrivingRouteOverlay {

    public MyDrivingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }
    @Override
    public int getLineColor() {
        //红色的路径
        return Color.RED;
    }
    @Override
    public BitmapDescriptor getStartMarker() {
        //自定义的起点图标
        return BitmapDescriptorFactory.fromResource(R.drawable.location_pointer_24);
    }
    @Override
    public BitmapDescriptor getTerminalMarker() {
        //自定义的终点图标
        return BitmapDescriptorFactory.fromResource(R.drawable.location_pointer_24);
    }
}
