package com.pwj.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.Home;

import com.pwj.utils.LoginInfo;
import com.pwj.activity.CityListActivity;
import com.pwj.helloya.R;
import com.pwj.helloya.RequestActivity;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by leon on 3/7/18.
 */

public class FragmentCustomer extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.recycle_location)
    RecyclerView recycle_location;
    @BindView(R.id.recycle_type)
    RecyclerView recycle_type;
    @BindView(R.id.go_ahead)
    Button go_ahead;
    @BindView(R.id.checked_all)
    CheckBox checked_all;
    public static final String ARG_PAGE = "ARG_PAGE_HOME";
    private Activity mActivity;
    private Unbinder unbinder;
    private List<Home> data_location;
    private List<Home> data_type;
    //    private RadioButton[] radioButtons= {radioButton1,radioButton2};
    private int[] product_img = {R.mipmap.gang, R.mipmap.tie, R.mipmap.gangtie, R.mipmap.lv,
            R.mipmap.lunchuan, R.mipmap.maolian, R.mipmap.kuagnshanjixie,};
    private int[] product_title = {R.string.gang, R.string.tie, R.string.gangtie, R.string.lv,
            R.string.lunchuan, R.string.maolian, R.string.kuangshanjixie};
    private CheckBox checkBox;
    private BaseRcyAdapter adapter_type;
    private BaseRcyAdapter adapter_location;
    private int position_radio = 0;
    private int position_check = 8;
    //定位
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private static final int PERMISSION_LOCATION_CODE = 100;
    private double latitude;
    private double longitude;

    public static FragmentCustomer newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentCustomer pageFragment = new FragmentCustomer();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        unbinder = ButterKnife.bind(this, view);
        mActivity = getActivity();
        initChoiceType();
        initLocation();
        initData();
        return view;
    }

    private static String[] productType = {"钢", "铁", "钢铁", "铝", "轮船", "锚链", "矿山机械"};
    private HashMap<Integer, Boolean> typeSelected;
    private int typesize = 0;
    private String choice = "";

    private void initChoiceType() {

    }
    private void initLocation() {
        mLocationClient = new LocationClient(mActivity.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
    }

    private void initData() {
        checked_all.setChecked(true);
        data_location = new ArrayList<>();
        data_type = new ArrayList<>();
        //客户选择位置的list集合
        Home home_Location1 = new Home(true, R.drawable.nearby, R.string.fujin);
        Home home_Location2 = new Home(false, R.drawable.city, R.string.chengshi);
        data_location.add(home_Location1);
        data_location.add(home_Location2);

        //客户选择商品类型的list集合
        for (int i = 0; i < product_img.length; i++) {
            Home home_type = new Home(false, product_img[i], product_title[i]);
            data_type.add(home_type);
        }
        initAdapter();
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recycle_location.setLayoutManager(linearLayoutManager);
        adapter_location = new BaseRcyAdapter(data_location, R.layout.item_home_radio) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                RadioButton radioButton = holder.getView(R.id.home_radioButton);
                radioButton.setOnClickListener(FragmentCustomer.this);
                radioButton.setTag(position);
                ImageView imageView = holder.getView(R.id.home_img);
                TextView textView = holder.getView(R.id.home_tv);
                //赋值给相应的控件
                radioButton.setChecked(data_location.get(position).isCheck());
                imageView.setImageResource(data_location.get(position).getImg());
                textView.setText(data_location.get(position).getTitle());
            }
        };
        recycle_location.setAdapter(adapter_location);
        //第二个recycleView，商品类型
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recycle_type.setLayoutManager(linearLayoutManager1);
        adapter_type = new BaseRcyAdapter(data_type, R.layout.item_home_check) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                checkBox = holder.getView(R.id.home_checkBox);
                checkBox.setOnClickListener(FragmentCustomer.this);
                checkBox.setTag(position);
                ImageView imageView = holder.getView(R.id.home_img);
                TextView textView = holder.getView(R.id.home_tv);
                //赋值给相应的控件
                checkBox.setChecked(data_type.get(position).isCheck());
                imageView.setImageResource(data_type.get(position).getImg());
                textView.setText(data_type.get(position).getTitle());
            }
        };
//        recycle_type.setLayoutManager(linearLayoutManager);
        recycle_type.setAdapter(adapter_type);

        for (int i = 0; i < product_img.length; i++) {
            data_type.get(i).setCheck(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_radioButton:
                position_radio = (int) view.getTag();
                switch (position_radio) {
                    case 0:                         //用户选择了城市列表
                        data_location.get(0).setCheck(true);
                        data_location.get(1).setCheck(false);
                        adapter_location.notifyDataSetChanged();
                        break;
                    case 1:                         //用户选择了附近列表
                        data_location.get(0).setCheck(false);
                        data_location.get(1).setCheck(true);
                        adapter_location.notifyDataSetChanged();
                        break;
                }
                break;
            case R.id.home_checkBox:
                position_check = (int) view.getTag();
                if (data_type.get(position_check).isCheck()) {
                    data_type.get(position_check).setCheck(false);
                } else {
                    data_type.get(position_check).setCheck(true);
                }
                break;
        }
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = { Manifest.permission.ACCESS_FINE_LOCATION})
    public void requestPermission() {
        //申请成功
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
        goAhead();
    }
    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
    }
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(mActivity, "禁止权限会影响到app的正常使用");
    }

    private void goAhead() {
        choice = "";
        switch (position_radio) {
            case 0:
                for (int i = 0; i < product_img.length; i++) {
                    if (data_type.get(i).isCheck()) {
                        choice = choice + productType[i] + ",";
                    }
                }
                if (choice.equals("")) {
                    Util.showToast(mActivity, "请至少选择一种客户类型");
                } else {
                    if (longitude > 1 && latitude > 1) {
                        Intent intent = new Intent(mActivity, RequestActivity.class);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("latitude", latitude);
                        LoginInfo.setString(getActivity(), "longitude", String.valueOf(longitude));
                        LoginInfo.setString(getActivity(), "latitude", String.valueOf(latitude));
                        intent.putExtra("choice", choice);
                        startActivity(intent);

                    } else {

                    }
                }
                break;
            case 1:
                for (int i = 0; i < product_img.length; i++) {
                    if (data_type.get(i).isCheck()) {
                        choice = choice + productType[i] + ",";
                    }
                }
                if (choice.equals("")) {
                    Util.showToast(mActivity, "请至少选择一种客户类型");
                } else {
                    if (longitude > 1 && latitude > 1) {
                        Intent intent = new Intent(mActivity, CityListActivity.class);
                        LoginInfo.setString(getActivity(), "longitude", String.valueOf(longitude));
                        LoginInfo.setString(getActivity(), "latitude", String.valueOf(latitude));
                        LoginInfo.setString(mActivity, "choice", choice);
//                    intent.putExtra("choice", choice);
                        startActivity(intent);

                    } else {
//                        Util.showToast(mActivity, "请打开GPS后在操作");
                    }
                }
        }
    }

    @OnClick({R.id.go_ahead, R.id.checked_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_ahead:
                checkGPS();
                break;
            case R.id.checked_all:
                if (checked_all.isChecked()) {
                    for (int i = 0; i < product_img.length; i++) {
                        data_type.get(i).setCheck(true);
                    }
                } else {
                    for (int i = 0; i < product_img.length; i++) {
                        data_type.get(i).setCheck(false);
                        position_check = 8;
                    }
                }
                adapter_type.notifyDataSetChanged();
                break;
        }

    }
    private void checkGPS() {
        LocationManager manager = (LocationManager) mActivity
                .getSystemService(Context.LOCATION_SERVICE);
        if(manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            //GPS已打开
           requestPermission();
        }else{
            //GPS未打开，跳转到GPS设置界面
            Util.showToast(mActivity,"请打开GPS后在操作");
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            //Toast.makeText(getActivity(), "请先打开GPS", 1).show();
        }
    }
    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryChat();
        MobclickAgent.onPageStart("FragmentCustomer");
        MobclickAgent.onResume(mActivity); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentCustomer");
        MobclickAgent.onPause(mActivity); //统计时长
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
