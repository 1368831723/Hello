package com.pwj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.BaseActivity;

import com.pwj.base.BaseActivityPermission;

import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogChooseLocation;
import com.pwj.dialog.DialogChoosePicture;


import com.pwj.helloya.R;

import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PhoneFormatCheckUtils;

import com.pwj.utils.Util;

import com.zhouyou.http.EasyHttp;

import java.io.File;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.content.ContentValues.TAG;

/**
 * Created by han on 2018/8/17.
 */

public class RegisterType2 extends BaseActivity implements View.OnClickListener, OnGetPoiSearchResultListener {
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.user_name)
    EditText user_name;
    @BindView(R.id.user_pwd)
    EditText user_pwd;
    @BindView(R.id.confirm_pwd)
    EditText confirm_pwd;
    @BindView(R.id.user_phone)
    EditText user_phone;
    @BindView(R.id.user_code_et)
    EditText user_code_et;
    @BindView(R.id.user_code_btn)
    TextView user_code_btn;
    @BindView(R.id.others_remarks)
    EditText et_others_remarks;

    @BindView(R.id.user_code_tv)
    TextView user_code_tv;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.register)
    Button register;

    @BindView(R.id.tv_location)
    TextView tv_location;
    @BindView(R.id.mMapView)
    MapView mMapView;
    @BindView(R.id.register_type1)
    LinearLayout register_type1;
    @BindView(R.id.liner_type2_1)
    ScrollView liner_type2_1;
    @BindView(R.id.liner_type2_2)
    LinearLayout liner_type2_2;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private String name_str;
    private String pwd_str;
    private String confirm_str;
    private String phone_str;
    private String location;
    //    private String remarks;
    private String others_remarks = "";
    private String type;
    private int typ;
    private String code_str;
    //百度定位
    //调用相机相册-请求权限-请求码
    public LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private MyLocationListener myListener = new MyLocationListener();
    private static final int PERMISSION_LOCATION_CODE = 100;
    private static final int PERMISSION_CAMERA_CODE = 101;
    private String addr;
    private String detail;
    private Connection connection = null;
    private double longitude;
    private double latitude;
    private LatLng point;
    private List<PoiInfo> poiInfos;
    private String city;
    private PoiSearch poiSearch;
    private PoiCitySearchOption poiCitySearchOption;
    private DialogChoosePicture dialogChoosePicture;
    private DialogChooseLocation dialogChooseLocation;
    private MapStatusUpdate mMapStatusUpdate;
    private int maxSelectNum = 1;
    public List<LocalMedia> selectList = new ArrayList<>();
    private String[] mUrls = new String[1];
    private GridImageAdapter adapter;
    private String uuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_register_type2);
        ButterKnife.bind(this);
        poiInfos = new ArrayList<>();
        initType();
        initData();
        initListener();
        initView();
        initWidget();
        user_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    }


    private void initListener() {
        register_type1.setOnClickListener(this);
//      tv_location.setOnClickListener(this);
        // 创建PoiSearch实例
        poiSearch = PoiSearch.newInstance();
        poiCitySearchOption = new PoiCitySearchOption();
        poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        initLocations();
    }

    private void initData() {
    }

    private void initType() {
        title_tv.setText("用户注册");
        uuid = LoginInfo.getString(this, "uuid", "");
        typ = getIntent().getIntExtra("typ", 2);
        switch (typ) {
            case 2:
                type = "生产商";
                break;
            case 3:
                type = "办事处";
                break;
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    private int recLen;

    private void recLenCode() {
        recLen = 60;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        user_code_btn.setVisibility(View.GONE);
                        user_code_tv.setVisibility(View.VISIBLE);
                        user_code_tv.setText(recLen + "s");
                        if (recLen < 1) {
                            timer.cancel();
                            user_code_btn.setVisibility(View.VISIBLE);
                            user_code_tv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);       // timeTask
    }

    @OnClick({R.id.title_im, R.id.user_code_btn, R.id.next, R.id.back, R.id.register})
    public void onViewClicked(View view) {
        name_str = user_name.getText().toString().trim();
        pwd_str = user_pwd.getText().toString().trim();
        confirm_str = confirm_pwd.getText().toString().trim();
        phone_str = user_phone.getText().toString().trim();
        code_str = user_code_et.getText().toString().trim();
        location = tv_location.getText().toString().trim();
        others_remarks = et_others_remarks.getText().toString().trim();
//        remarks = edt_remarks.getText().toString().trim();
        switch (view.getId()) {
            case R.id.title_im:
                RegisterType2.this.finish();
                break;
            case R.id.user_code_btn:
                judgeCode();
                break;
            case R.id.next:
                initLocations();
                liner_type2_1.setVisibility(View.GONE);
                liner_type2_2.setVisibility(View.VISIBLE);
                break;
            case R.id.back:
                liner_type2_1.setVisibility(View.VISIBLE);
                liner_type2_2.setVisibility(View.GONE);
                break;
            case R.id.register:
                judgeName();
                break;

        }
    }

    protected void initLocation() {
        //申请成功
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();

        if (latitude < 1 || longitude < 1) {
            initLocations();
        } else {
            //定义Maker坐标点
            point = new LatLng(latitude, longitude);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.clear);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap)
                    .zIndex(9)
                    .draggable(true);
            //在地图上添加Marker，并显示
            final Marker marker = (Marker) mBaiduMap.addOverlay(option);
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
                            Util.showToast(RegisterType2.this, "获取地理位置失败");
                        }
                    });
                    geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                }//结束

            });
        }
    }

    private boolean first = true;

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //设置poi检索监听者
        poiInfos = poiResult.getAllPoi();

        if (poiInfos != null && poiInfos.size() > 1) {

            dialogChooseLocation = new DialogChooseLocation(RegisterType2.this, city, R.style.dialog_choose, poiInfos, new DialogChooseLocation.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(String location) {
                    tv_location.setText(location);
                }
            });
            dialogChooseLocation.show();
            full(RegisterType2.this, dialogChooseLocation);
        } else {
            if (dialogChooseLocation != null) {
                dialogChooseLocation.show();
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

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})

    public void initLocations() {
        initLocation();
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
        finish();
    }

    /**
     * 权限被取消
     */
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        finish();
        Util.showToast(this, "禁止权限会影响到app的正常使用");
    }


    //设置弹窗宽度
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
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

        }

    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:             //验证码验证成功
                    insert();
                    //执行bmob注册
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            connection = Jdbc.getConnection("root", "1q23lyc45j");
//                            boolean flag = Query.query_register(connection, phone_str);
//                            if (flag) {
//                                handler.sendEmptyMessageDelayed(1, 0);
//                            } else {//没有账号
//                                handler.sendEmptyMessageDelayed(2, 0);
//                                connection = Jdbc.getConnection("root", "1q23lyc45j");
//                                String url = Test2.uploadFile(IpConfig.ip, mUrls);
//                                Insert.insert_phone2(connection, url, name_str, pwd_str, phone_str, location, type, others_remarks, longitude, latitude);
//                            }
//                        }
//                    }).start();
                    break;
                case 2:             //获取验证码成功,注意查看
                    Util.showToast(RegisterType2.this, "获取验证码成功,注意查看");
                    break;
                case 3:
                    //获取验证码失败,请填写正确的手机号码
                    Util.showToast(RegisterType2.this, "获取验证码失败,请填写正确的手机号");
                    break;
                case 4:             //验证码验证错误
                    Util.showToast(RegisterType2.this, "验证码错误");
                    break;
            }
        }
    };

    private void insert() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT pwd FROM login where phone = '" + phone_str + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                        if (s.length() > 12) {  //账号存在

                            Intent intent = new Intent(RegisterType2.this, RegisterAlready.class);
                            intent.putExtra("pwd", s.substring(9, s.length() - 3));
                            startActivity(intent);
                            finish();
                        } else {                  //没有账号
                            if (selectList.size() > 0) {
                                File file = new File(BitmapUtil.compressImage(mUrls[0]));
                                String filename = file.getName();
                                String postfix = filename.substring(filename.lastIndexOf("."));
                                String names = String.valueOf(System.currentTimeMillis()) + postfix;
                                EasyHttp.post(IpConfig.URL_IMG + names)
                                        .params("uploadfile", file, names, null)
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                insert2(names);
                                            }
                                        });
                            }else {
                                insert2("");
                            }
                        }
                    }
                });
    }
    private void insert2(String names) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "update login set `url1`= '" + (names) + "',`user_name`= '" + name_str + "',`pwd`='" + pwd_str + "' ,`phone`='" + phone_str + "',`location`='" + location + "',`type`='" + type + "',`remarks`='" + others_remarks + "',`longitude`='" + longitude + "',`latitude`='" + latitude + "' where `uid`='" + uuid + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Intent intent = new Intent(RegisterType2.this, RegisterSuccess.class);
                        startActivity(intent);//点击按钮立马跳转
                        finish();
                    }
                });
    }
    //1 注册回调接口 初始化短信接送监听器【注意：回掉接口在非UI线程中so要用到Handler来通知用户】在onCreate()方法里init
    private EventHandler eventHandler;
    public int smsFlage = 0;//0:设置为初始化值 1：请求获取验证码 2：提交用户输入的验证码判断是否正确

    private void callBack() {
        this.eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        myHandler.sendEmptyMessageDelayed(1, 0);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        myHandler.sendEmptyMessageDelayed(2, 0);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    //此语句代表接口返回失败
                    //获取验证码失败。短信验证码验证失败（用flage标记来判断）
//                    if (smsFlage == 0) {
//                        myHandler.sendEmptyMessage(4);
//                    } else
                    if (smsFlage == 1) {
                        myHandler.sendEmptyMessageDelayed(3, 0);

                    } else {
                        myHandler.sendEmptyMessageDelayed(4, 0);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);//注册短信回调
    }

    //2 获取短信验证码 请求获取短信验证码，在监听中返回
    private void getSmsCode(String country, String phone) {
        SMSSDK.getVerificationCode(country, phone);//请求获取短信验证码，在监听中返回
    }

    //3 提交验证码
    private void submitCode(String country, String phone, String code) {
        SMSSDK.submitVerificationCode(country, phone, code);//提交短信验证码，在监听中返回
    }

    //4 注销回调接口 registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
    private void unregisterHandler() {
        SMSSDK.unregisterEventHandler(eventHandler);
        //    Log.v("TAG", "注销回调接口");
    }

    private void judgeCode() {
        if (!TextUtils.isEmpty(name_str)) {                                    //用户名不为空
            if (!TextUtils.isEmpty(pwd_str)) {                                //密码不为空
                if (pwd_str.equals(confirm_str)) {                            //密码一致
                    if (!TextUtils.isEmpty(location)) {
                        if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
                            getSmsCode("86", phone_str);  //发送验证码
                            recLenCode();
                            Util.showToast(RegisterType2.this, "获取验证码成功,注意查看");
                        } else {
                            Util.showToast(RegisterType2.this, "请输入正确的手机号");
                        }
                    } else {
                        Util.showToast(RegisterType2.this, "该服务需要提供位置信息");
                    }
                } else {
                    Util.showToast(RegisterType2.this, "两次输入的密码不一致");
                }
            } else {
                Util.showToast(RegisterType2.this, "密码不能为空");
            }
        } else {
            Util.showToast(RegisterType2.this, "用户名不能为空");
        }
    }

    private void judgeName() {
        if (!TextUtils.isEmpty(name_str)) {                                    //用户名不为空
            if (!TextUtils.isEmpty(pwd_str)) {                                //密码不为空
                if (pwd_str.equals(confirm_str)) {                            //密码一致
                    if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
                        submitCode("86", phone_str, code_str);
                        callBack();
//                        if (selectList.size() > 0) {
//                            submitCode("86", phone_str, code_str);
//                            callBack();
//                        } else {
//                            Util.showToast(RegisterType2.this, "请上传营业执照");
//                        }
                    } else {
                        Util.showToast(RegisterType2.this, "请输入正确的手机号");
                    }
                } else {
                    Util.showToast(RegisterType2.this, "两次输入的密码不一致");
                }
            } else {
                Util.showToast(RegisterType2.this, "密码不能为空");
            }
        } else {
            Util.showToast(RegisterType2.this, "用户名不能为空");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            images = PictureSelector.obtainMultipleResult(data);
            selectList.addAll(images);
//                    selectList = PictureSelector.obtainMultipleResult(data);
            // 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
            if (selectList.size() > 0) {
                mUrls[0] = selectList.get(0).getPath();
            }
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {

            select();

        }
    };

    private void initWidget() {
//        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(RegisterType2.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(RegisterType2.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(RegisterType2.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    public void select() {

        dialogChoosePicture = new DialogChoosePicture(RegisterType2.this, "", new DialogChoosePicture.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                if (id == R.id.tv_camera) {
                    //拍照
                    PictureSelector.create(RegisterType2.this)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_photo) {
                    //相册
                    PictureSelector.create(RegisterType2.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(maxSelectNum)
                            .minSelectNum(1)
                            .imageSpanCount(4)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }
            }
        }, R.style.dialog_choose);
        dialogChoosePicture.show();
        full(this, dialogChoosePicture);
    }

    //    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RegisterType2"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RegisterType2");
//        MobclickAgent.onPause(this); //统计时长
//    }
    public void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(RegisterType2.this, activity);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHandler();
        myHandler.removeCallbacksAndMessages(null);
//        Jdbc.releaseConnection(Jdbc.getConnection("root", "1q23lyc45j"));
    }
}
