package com.pwj.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
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
import com.pwj.bean.Login;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogCheckId;

import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.dialog.DialogChoosePicture;
import com.pwj.helloya.R;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class RepairActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.issue_et_name)
    EditText issue_et_name;
    @BindView(R.id.issue_tv_phone)
    TextView issue_tv_phone;
    @BindView(R.id.img_location)
    ImageView img_location;
    @BindView(R.id.edt_lcn_remarks)
    EditText edt_lcn_remarks;
    @BindView(R.id.edt_remarks)
    EditText edt_remarks;
    //???????????????????????????????????????
    //??????????????????-?????????
    private static final int PHOTO_CODE = 10;
    //??????????????????-?????????
    private static final int CAMERA_CODE = 11;
    //??????????????????-????????????-?????????

    private DialogChoosePicture dialogChoosePicture;
    private int require = 1; //???????????????????????????
    @BindView(R.id.require_rgp)
    RadioGroup require_rgp;
    @BindView(R.id.require_rbn_01)
    RadioButton require_rbn_01;
    @BindView(R.id.require_rbn_02)
    RadioButton require_rbn_02;
    @BindView(R.id.linear_type1)
    LinearLayout linear_type1;
    @BindView(R.id.linear_name)
    LinearLayout linear_name;
    @BindView(R.id.et0_01)
    EditText et0_01;
    @BindView(R.id.et1_01)
    EditText et1_01;
    //    @BindView(R.id.name_tv_01)
//    TextView name_tv_01;
    @BindView(R.id.linear_rent)
    LinearLayout linear_rent;
    @BindView(R.id.rent_et_01)
    EditText rent_et_01;
    @BindView(R.id.linear_logistics)
    LinearLayout linear_logistics;
    @BindView(R.id.logistics_et_01)
    EditText logistics_et_01;
    @BindView(R.id.linear_lcn)
    LinearLayout linear_lcn;
    @BindView(R.id.edt_location)
    EditText edt_location;
    //    @BindView(R.id.purchase_tv)
//    TextView purchase_tv;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private String data_name;   //??????
    private String data_phone;  //??????
    private String location;    //??????
    private String produc_name; //?????? 1 ?????????,//??????4???????????????
    private String lcn_remark;  //?????? 2 ?????????,????????????
    private String rent;        //?????? 3 ?????????
    private String destination;        //?????? 4 ?????????
    private String remarks;     //????????????
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private Connection connection = null;
    private GridImageAdapter adapter;
    private DialogChoosePicture dialog;
    private final int CODE_LOCATION = 100;
    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String[] mUrls;
    private String url = "";
    private long time;
    private String sql = "";
    private List<Login> data = new ArrayList<>();
    private DialogCheckId dialogCheckId;
    private String content;
    private String date = "";
    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_repair);
        ButterKnife.bind(this);
        initView();
        initData();
        initLayout();
        initWidget();
    }

    private void initView() {
        title_tv.setText("????????????");
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

    private void initData() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = System.currentTimeMillis();
        data_name = LoginInfo.getString(this, "name", "");
        data_phone = LoginInfo.getString(this, "phone", "");
//        cheIdentify();
        issue_et_name.setText(data_name);
        issue_et_name.setSelection(data_name.length());
        issue_tv_phone.setText(data_phone);
    }

    private void cheIdentify() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT user_name,phone,id_card FROM login WHERE phone = " + data_phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data = GsonUtils.getGsonToList(json, Login.class);
                        if ("".equals(data.get(0).getId_card())) {
                            dialogCheckId = new DialogCheckId(RepairActivity.this, RepairActivity.this, new DialogCheckId.ICustomDialogEventListener() {
                                @Override
                                public void customDialogEvent(int id) {
                                    switch (id) {
                                        case R.id.dia_no:
                                            RepairActivity.this.finish();
                                            break;
                                        case R.id.dia_yes:
                                            startActivity(PersonalData.class);
                                            RepairActivity.this.finish();
                                            break;
                                    }
                                }
                            }, R.style.dialog_choose);
                            dialogCheckId.show();
                            full_dialog_identify();
                        }
                    }
                });
    }

    //??????????????????
    private void full_dialog_identify() {
        WindowManager windowManager = RepairActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogCheckId.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //????????????
        dialogCheckId.getWindow().setAttributes(lp);
    }

    private void initLayout() {
        require_rbn_01.setChecked(true);
        require_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.require_rbn_00:
                        linear_type1.setVisibility(View.VISIBLE);
                        linear_name.setVisibility(View.GONE);    //??????1
//                        name_tv_01.setVisibility(View.VISIBLE);
                        linear_lcn.setVisibility(View.GONE);         //??????2
                        linear_rent.setVisibility(View.GONE);        //??????3
                        linear_logistics.setVisibility(View.GONE);   //??????4
                        require = 0;
                        break;
                    case R.id.require_rbn_01:
                        linear_type1.setVisibility(View.GONE);
                        linear_name.setVisibility(View.VISIBLE);    //??????1
//                        name_tv_01.setVisibility(View.VISIBLE);
                        linear_lcn.setVisibility(View.GONE);         //??????2
                        linear_rent.setVisibility(View.GONE);        //??????3
                        linear_logistics.setVisibility(View.GONE);   //??????4
                        require = 1;
                        break;
                    case R.id.require_rbn_02:
                        linear_type1.setVisibility(View.GONE);
                        linear_name.setVisibility(View.GONE);   //??????1
//                        name_tv_01.setVisibility(View.GONE);
                        linear_lcn.setVisibility(View.VISIBLE); //??????2
                        linear_rent.setVisibility(View.GONE);   //??????3
                        linear_logistics.setVisibility(View.GONE);   //??????4
                        require = 2;
                        break;
                    case R.id.require_rbn_03:
                        linear_type1.setVisibility(View.GONE);
                        linear_name.setVisibility(View.GONE);   //??????1
//                        name_tv_01.setVisibility(View.GONE);
                        linear_lcn.setVisibility(View.GONE);    //??????2
                        linear_rent.setVisibility(View.VISIBLE);//??????3
//                        name_tv_01.setVisibility(View.VISIBLE);
                        linear_logistics.setVisibility(View.GONE);   //??????4
                        require = 3;
                        break;
                    case R.id.require_rbn_04:
                        linear_type1.setVisibility(View.GONE);
                        linear_name.setVisibility(View.VISIBLE);   //??????1
//                        name_tv_01.setVisibility(View.VISIBLE);
                        linear_lcn.setVisibility(View.GONE);    //??????2
                        linear_rent.setVisibility(View.GONE);   //??????3
                        linear_logistics.setVisibility(View.VISIBLE);   //??????4
                        require = 4;
                        break;
                }
            }
        });
    }

    @OnClick({R.id.title_im, R.id.img_location, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                RepairActivity.this.finish();
                break;
            case R.id.img_location:
                Intent intent = new Intent(RepairActivity.this, BaseMapActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.submit:
                getData();
                break;
        }
    }

    private void addUrls() {
        mUrls = null;
        mUrls = new String[9];
        for (int i = 0; i < selectList.size(); i++) {
            mUrls[i] = selectList.get(i).getPath();
        }
    }

    public void getData() {
        data_name = issue_et_name.getText().toString().trim();
        location = edt_location.getText().toString().trim();
        lcn_remark = edt_lcn_remarks.getText().toString().trim();
        remarks = edt_remarks.getText().toString().trim();
        if (!TextUtils.isEmpty(data_name)) {
            if (!TextUtils.isEmpty(location)) {
                if (selectList.size() > 0) {
                    addUrls();
                    data_name = "?????????:" + data_name;
                    location = "??????:" + location;
                    remarks = "??????:" + remarks;
                    date = sdf.format(System.currentTimeMillis());
                    switch (require) {
                        case 0:
                            produc_name = et0_01.getText().toString().trim();
                            if (TextUtils.isEmpty(produc_name)) {
                                Util.showToast(RepairActivity.this, "????????????????????????");
                            } else {
                                produc_name = "????????????:" + produc_name;
                                content = produc_name+","+location+","+data_name+","+data_phone+","+remarks;
                                for (int i = 0; i < mUrls.length; i++) {
                                    if (!TextUtils.isEmpty(mUrls[i])) {
                                        insertIf(i);
                                    } else {
                                        sql = "insert into require_0_others(`url`,`contact`,`phone`,`location`,`name`, `remarks`, `content`, `date`)values('" + url + "','" + data_name + "','" + data_phone + "','" + location + "','" + produc_name + "','" + remarks + "','" + content + "','" + date + "')";
                                        insertElse("require_0_others");
                                        break;
                                    }
                                }
                            }
                            break;
                        case 1:
                            produc_name = et1_01.getText().toString().trim();
                            if (TextUtils.isEmpty(produc_name)) {
                                Util.showToast(RepairActivity.this, "????????????????????????");
                            } else {
                                produc_name = "????????????:" + produc_name;
                                content = produc_name+","+location+","+data_name+","+data_phone+","+remarks;
                                for (int i = 0; i < mUrls.length; i++) {
                                    if (!TextUtils.isEmpty(mUrls[i])) {
                                        insertIf(i);
                                    } else {
                                        sql = "insert into require_1_purchase(`url`,`contact`,`phone`,`location`,`name`, `remarks`, `content`, `date`)values('" + url + "','" + data_name + "','" + data_phone + "','" + location + "','" + produc_name + "','" + remarks + "','" + content + "','" + date + "')";
                                        insertElse("require_1_purchase");
                                        break;
                                    }
                                }
                            }
                            break;
                        case 2:
                            LoginInfo.setString(this, "name", data_name.substring(4));//????????????
                            content = location+","+lcn_remark+","+data_name+","+data_phone+","+remarks;
                            for (int i = 0; i < mUrls.length; i++) {
                                if (!TextUtils.isEmpty(mUrls[i])) {
                                    insertIf(i);
                                } else {
                                    sql = "insert into require_2_repair(`url`,`contact`, `phone`,`location`,`lcn_remark`, `remarks`, `content`, `date`)values('" + url + "','" + data_name + "','" + data_phone + "','" + location + "','" + lcn_remark + "','" + remarks + "','" + content + "','" + date + "')";
                                    insertElse("require_2_repair");
                                    break;
                                }
                            }
                            break;
                        case 3:
                            rent = rent_et_01.getText().toString().trim();
                            if (TextUtils.isEmpty(rent)) {
                                Util.showToast(RepairActivity.this, "????????????????????????");
                            } else {
                                rent = "????????????:" + rent + "??????";
                                content = rent+","+location+","+data_name+","+data_phone+","+remarks;
                                for (int i = 0; i < mUrls.length; i++) {
                                    if (!TextUtils.isEmpty(mUrls[i])) {
                                        insertIf(i);
                                    } else {
                                        sql = "insert into require_3_rent(`url`,`contact`,`phone`,`location`,`duration`,`remarks`, `content`, `date`)values('" + url + "','" + data_name + "','" + data_phone + "','" + location + "','" + rent + "','" + remarks + "','" + content + "','" + date + "')";
                                        insertElse("require_3_rent");
                                        break;
                                    }
                                }
                            }
                            break;
                        case 4:
                            produc_name = et1_01.getText().toString().trim();
                            destination = logistics_et_01.getText().toString().trim();
                            if (TextUtils.isEmpty(produc_name)) {
                                Util.showToast(RepairActivity.this, "????????????????????????");
                            } else {
                                if (TextUtils.isEmpty(destination)) {
                                    Util.showToast(RepairActivity.this, "???????????????????????????");
                                } else {
                                    produc_name = "????????????:" + produc_name;
                                    destination = "?????????:" + destination;
                                    content = produc_name+","+location+","+destination+","+data_name+","+data_phone+","+remarks;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into require_4_logistics(`url`,`contact`,`phone`,`location`,`name`,`destination`, `remarks`, `content`, `date`)values('" + url + "','" + data_name + "','" + data_phone + "','" + location + "','" + produc_name + "','" + destination + "','" + remarks + "','" + content + "','" + date + "')";
                                            insertElse("require_4_logistics");
                                            break;
                                        }
                                    }
                                }
                            }

                            break;
                    }
                } else {
                    Util.showToast(RepairActivity.this, "???????????????");
                }
            } else {
                Util.showToast(RepairActivity.this, "??????????????????");
            }

        } else {
            Util.showToast(RepairActivity.this, "??????????????????");
        }
    }

    private void insertIf(int i) {
        File file = new File(BitmapUtil.compressImage(mUrls[i]));
        String filename = file.getName();
        String postfix = filename.substring(filename.lastIndexOf("."));
        String names = String.valueOf(time + i) + postfix;
        url = url + IpConfig.BASE_URL + names + ",";
        EasyHttp.post(IpConfig.URL_IMG + names)
                .params("uploadfile", file, names, null)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                    }
                });
    }

    private void insertElse(String table_name) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

//                        startActivity(IssueSuccess.class);//????????????????????????
//                        finish();
                        insertIssue(data_phone, table_name);
                    }
                });
    }
    private void insertIssue(String phone, String table_name) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM " + table_name + " WHERE phone = " + phone + " ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        int table_id = GsonUtils.getGsonToList(str, Product.class).get(0).getId();
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "insert into comment_1_issue(`phone`, `table_id`, `table_name`)values('" + phone + "'," + table_id + ",'" + table_name + "')")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        finish();
                                        startActivity(IssueSuccess.class);//????????????????????????
                                    }
                                });
                    }
                });
    }
    private void initWidget() {
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
                            // ???????????? ???????????????????????????
                            PictureSelector.create(RepairActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // ????????????
                            PictureSelector.create(RepairActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // ????????????
                            PictureSelector.create(RepairActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            select();
        }
    };

    public void select() {

        dialog = new DialogChoosePicture(RepairActivity.this, "", new DialogChoosePicture.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                if (id == R.id.tv_camera) {
                    //??????
                    PictureSelector.create(RepairActivity.this)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_photo) {
                    //??????
                    PictureSelector.create(RepairActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(maxSelectNum - selectList.size())
                            .minSelectNum(1)
                            .imageSpanCount(4)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }
            }
        }, R.style.dialog_choose);
        dialog.show();
        full(this, dialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????????????????
                    images = PictureSelector.obtainMultipleResult(data);
                    selectList.addAll(images);
                    selectList.get(0).getCutPath();
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        } else if (resultCode == CODE_LOCATION) {
            location = data.getStringExtra("location");
            edt_location.setText(location);
            edt_location.setSelection(location.length());
        }
    }

    //??????????????????
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //????????????
        dialog.getWindow().setAttributes(lp);
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //?????????BDLocation?????????????????????????????????????????????get??????????????????????????????????????????
            //??????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????BDLocation???????????????

            String addr = location.getAddrStr();    //????????????????????????
            String country = location.getCountry();    //????????????
            String province = location.getProvince();    //????????????
            String city = location.getCity();    //????????????
            String district = location.getDistrict();    //????????????
            String street = location.getStreet();    //??????????????????
            edt_location.setText(addr);
            edt_location.setSelection(addr.length());

        }
    }


//    private void startActivity(Class<?> activity) {
//        Intent intent = new Intent();
//        intent.setClass(RepairActivity.this, activity);
//        startActivity(intent);
//    }

    /**
     * ??????????????????
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode = 10)

    public void startLocation() {
        //????????????
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();

    }

    /**
     * ???????????????
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
        finish();
    }

    /**
     * ???????????????
     */
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(this, "????????????????????????app???????????????");
    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RepairActivity"); //????????????("MainScreen"??????????????????????????????)
//        MobclickAgent.onResume(this); //????????????
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RepairActivity");
//        MobclickAgent.onPause(this); //????????????
//    }

}
