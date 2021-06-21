package com.pwj.utils;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pwj.fragment.FragmentMe;
import com.pwj.interfaces.StringCallbackOne;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 13688 on 2019/5/18.
 */

public class SearchUtil {
    private static SearchUtil m_instance = null;
    private LocationClient mLocationClient = null;
    private MyLocationListener myListener;
    private String locations = "";
    private StringCallbackOne stringCallbackOne;
    public static SearchUtil getInstance() {
        if (m_instance == null)
            m_instance = new SearchUtil();
        return m_instance;
    }
    //获取当前系统号
    public  String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    //获取手机品牌
    public  String getSystemBrand() {
        return Build.BRAND;
    }

    //获取手机型号
    public  String getSystemModel() {
        return android.os.Build.MODEL;
    }

    //获取当前时间
    public  String getNowDate(){
        String temp_str="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        temp_str=sdf.format(dt);
        return temp_str;
    }
    //获取当前时间
    public  String getNowDate10(){
        String temp_str="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        temp_str=sdf.format(dt);
        return temp_str;
    }
    public void initLocation(Context context, StringCallbackOne stringCallbackOne) {
        this.stringCallbackOne = stringCallbackOne;
        mLocationClient = new LocationClient(context.getApplicationContext());
        myListener=new MyLocationListener();
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
            mLocationClient.start();
        }else {
            mLocationClient.start();
        }
    }
    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            locations = location.getProvince()+location.getCity()+location.getDistrict()+location.getStreet();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            if (longitude<1&&latitude<1){
                if (mLocationClient.isStarted()){
                    mLocationClient.stop();
                    mLocationClient.start();
                }else {
                    mLocationClient.start();
                }
            }
            stringCallbackOne.stringOne(locations , longitude,latitude);
        }
    }
}
