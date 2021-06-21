package com.pwj.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.BaseActivity;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.base.BaseActivityPermission;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogForm;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.subsciber.IProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chihane.jdaddressselector.BottomDialog;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;
import mlxy.utils.T;


public class AddressAddActivity extends Activity {

    @BindView(R.id.title_fm)
    FrameLayout title_fm;
    @BindView(R.id.et_contact)
    EditText et_contact;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.tv_local)
    TextView tv_local;
    @BindView(R.id.et_specific)
    EditText et_specific;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.address_cb)
    CheckBox address_cb;
    @BindView(R.id.relative_delete)
    RelativeLayout relative_delete;
    private BottomDialog dialog;
    private boolean flag = false;
    private String phone;
    private String phone_address = "";
    private String contact = "";
    private String location = "";
    private String province;
    private String city;
    private String county;
    private String street = "";
    private String specific = "";
    private ProgressDialog myDialog; // 分享进度框
    private String sql;
    private int number = 0;
    private int id;
    private DialogForm dialogForm;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        ButterKnife.bind(this);
        initLocation();
        initData();
    }

    private void initData() {
        phone = LoginInfo.getString(this, "phone", "");
        flag = getIntent().getBooleanExtra("flag", false);
        if (flag) {
            id = getIntent().getIntExtra("id", 0);
            contact = getIntent().getStringExtra("contact");
            phone_address = getIntent().getStringExtra("phone_address");
            location = getIntent().getStringExtra("location");
            specific = getIntent().getStringExtra("specific");
            province = getIntent().getStringExtra("province");
            city = getIntent().getStringExtra("city");
            county = getIntent().getStringExtra("county");
            street = getIntent().getStringExtra("street");
            longitude = getIntent().getDoubleExtra("longitude", 0);
            latitude = getIntent().getDoubleExtra("latitude", 0);
            number = getIntent().getIntExtra("number", 0);
            if (number == 1) {
                address_cb.setChecked(true);
            }
            et_contact.setText(contact);
            et_phone.setText(phone_address);
            tv_local.setText(location.substring(0, location.length() - specific.length()));
            et_specific.setText(specific);
            relative_delete.setVisibility(View.VISIBLE);
        }
        ImmersionBar.with(this)
                .titleBarMarginTop(title_fm)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        SpannableString s1 = new SpannableString(getString(R.string.address_hint_contact));
        SpannableString s2 = new SpannableString(getString(R.string.address_hint_phone));
        SpannableString s3 = new SpannableString(getString(R.string.address_hint_specific));
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(14, true);
        s1.setSpan(textSize, 0, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s2.setSpan(textSize, 0, s2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s3.setSpan(textSize, 0, s3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        et_contact.setHint(s1);
//        et_phone.setHint(s2);
        et_specific.setHint(s3);
        address_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    number = 1;
                } else {
                    number = 0;
                }
            }
        });


    }

    private void initLocation() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(false);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
    }

    @OnClick({R.id.title_im, R.id.img_phone, R.id.relative_local, R.id.save, R.id.relative_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_phone:
                requestPermission1();
                break;
            case R.id.relative_local:
                checkGPS();
                break;
            case R.id.save:
                submit();
                break;
            case R.id.relative_delete:
                dialogForm = new DialogForm(AddressAddActivity.this, "是否删除该地址？", new DialogForm.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int view) {
                        switch (view) {
                            case R.id.dia_yes:
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "DELETE FROM address WHERE id = " + id + "")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                setResult(10);
                                                finish();
                                            }
                                        });
                                break;
                        }
                    }
                }, R.style.dialog_choose);
                dialogForm.show();
                break;
        }
    }
    private void startContacts(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 0);
    }
    private void checkGPS() {
        LocationManager manager = (LocationManager) AddressAddActivity.this
                .getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            //GPS已打开
            requestPermission2();
        } else {
            //GPS未打开，跳转到GPS设置界面
            Util.showToast(AddressAddActivity.this, "请打开GPS后在操作");
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
        }
    }

    private void showDialog() {
        if (longitude < 0.1) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
            mLocationClient.start();
        }
        if (dialog != null) {
            dialog.show();
        } else {
            dialog = new BottomDialog(this);
            dialog.setOnAddressSelectedListener(new OnAddressSelectedListener() {
                @Override
                public void onAddressSelected(Province provinces, City citys, County countys, Street streets) {
                    province = provinces.name;
                    city = citys.name;
                    county = countys.name;
                    location = province + city + county;
                    if (streets != null) {
                        street = streets.name;
                        location = location + street;
                    } else {
                        street = "";
                    }
                    tv_local.setText(location);
                    dialog.dismiss();
                }
            });
            dialog.show();
            full_dialog();
        }
    }

    //设置弹窗宽度
    private void full_dialog() {
        WindowManager windowManager = AddressAddActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogForm.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()) - 150; //设置宽度
        dialogForm.getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            Uri uri=data.getData();
            String[] contacts=getPhoneContacts(uri);
            assert contacts != null;
            et_phone.setText(contacts[1]);
        }
    }

    private void submit() {
        contact = et_contact.getText().toString().trim();
        phone_address = et_phone.getText().toString().trim();
        specific = et_specific.getText().toString().trim();
        if (!"".equals(contact)) {
            if (!"".equals(phone_address)) {
                if (!"".equals(location)) {
                    if (!"".equals(specific)) {
                        progressbar.setVisibility(View.VISIBLE);
                        location = tv_local.getText().toString() + specific;
                        if (number == 1) {
                            if (flag) {  //编辑地址
                                sql = "update address set `number` = 1 ,`contact` = '" + contact + "',`phone_address` = '" + phone_address + "',`location` = '" + location + "',`province` = '" + province + "',`city` = '" + city + "',`county` = '" + county + "',`street` = '" + street + "',`specific` = '" + specific + "' where id = " + id + "";
                            } else {     //插入新地址
                                sql = "insert into address (`phone`,`contact`,`phone_address`,`location`,`province`,`city`,`county`,`street`,`specific`,`longitude`,`latitude`,`number`) values ('" + phone + "','" + contact + "','" + phone_address + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + longitude + "','" + latitude + "','" + number + "')";
                            }
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "update address set `number` = 0 WHERE phone = " + phone + " AND id<>" + id + "")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {

                                        }
                                    });
                        } else {
                            if (flag) {  //编辑地址
                                sql = "update address set `number` = 0 ,`contact` = '" + contact + "',`phone_address` = '" + phone_address + "',`location` = '" + location + "',`province` = '" + province + "',`city` = '" + city + "',`county` = '" + county + "',`street` = '" + street + "',`specific` = '" + specific + "' where id = " + id + "";
                            } else {     //插入新地址
                                sql = "insert into address (`phone`,`contact`,`phone_address`,`location`,`province`,`city`,`county`,`street`,`specific`,`longitude`,`latitude`,`number`) values ('" + phone + "','" + contact + "','" + phone_address + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + longitude + "','" + latitude + "','" + number + "')";
                            }
                        }
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", sql)
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>(AddressAddActivity.this, progressbar) {
                                    @Override
                                    public void onSuccess(String s) {
                                        progressbar.setVisibility(View.GONE);
                                        setResult(10);
                                        finish();
                                        Log.e("submit: ", "sql: " + sql);
                                    }
                                });
                    } else {
                        Util.showToast(AddressAddActivity.this, "详细地址不能为空");
                    }
                } else {
                    Util.showToast(AddressAddActivity.this, "所在区域不能为空");
                }
            } else {
                Util.showToast(AddressAddActivity.this, "联系电话不能为空");
            }
        } else {
            Util.showToast(AddressAddActivity.this, "联系人不能为空");
        }
    }

    public IProgressDialog mProgressDialog = new IProgressDialog() {
        @Override
        public Dialog getDialog() {
            ProgressDialog dialog = new ProgressDialog(AddressAddActivity.this);
            dialog.setMessage("正在保存中...");
            return dialog;
        }
    };
    private String[] getPhoneContacts(Uri uri){
        String[] contact=new String[2];
        //得到ContentResolver对象**
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标**
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            //取得联系人姓名**
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0]=cursor.getString(nameFieldColumnIndex);
            //取得电话号码**
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if(phone != null){
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phone.close();
            cursor.close();
        }
        else
        {
            return null;
        }
        return contact;
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.e("onReceiveLocation:", "" + longitude);
        }
    }
    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE})
    public void requestPermission1() {
        startContacts();
    }
    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void requestPermission2() {
        showDialog();
        //申请成功
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(new Intent(AddressAddActivity.this, BaseActivityPermission.class));
    }

    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(AddressAddActivity.this, "禁止权限会影响到app的正常使用");
    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SettingAboutActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SettingAboutActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
