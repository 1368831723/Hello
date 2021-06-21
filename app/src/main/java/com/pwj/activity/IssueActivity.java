package com.pwj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.pwj.base.BaseActivityIssue;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogCheckId;
import com.pwj.dialog.DialogChoosePicture;
import com.pwj.helloya.R;
import com.pwj.jdbc.Insert;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;
import com.pwj.jdbc.Test2;
import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PhoneFormatCheckUtils;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


/**
 * Created by delphi0127 on 2018/7/4.
 */

public class IssueActivity extends BaseActivity {
    public static IssueActivity issueActivity;
    private final int CODE_LOCATION = 100;
    @BindView(R.id.title_tv)
    TextView title_tv;

    private DialogCheckId dialogCheckId;
    //姓名，手机号
    @BindView(R.id.issue_et_name)
    EditText issue_et_name;
    //    @BindView(R.id.issue_et_phone)
//    EditText issue_et_phone;
    @BindView(R.id.issue_tv_phone)
    TextView issue_tv_phone;
    @BindView(R.id.issue_submit)
    Button issue_submit;

    //是否能托运，包邮
    private String consign = "可托运";
    private String postage = "运费另计";
    @BindView(R.id.consign_rgp)
    RadioGroup consign_rgp;
    @BindView(R.id.consign_yes)
    RadioButton consign_yes;
    @BindView(R.id.consign_no)
    RadioButton consign_no;
    @BindView(R.id.postage_rgp)
    RadioGroup postage_rgp;
    @BindView(R.id.postage_yes)
    RadioButton postage_yes;
    @BindView(R.id.postage_no)
    RadioButton postage_no;
    @BindView(R.id.et_location)
    EditText et_location;
    //发布产品的类型的radiogroup
    private int issue = 1;
    @BindView(R.id.issue_rgp)
    RadioGroup issue_rgp;
    @BindView(R.id.issue_rb1)
    RadioButton issue_rb1;
    @BindView(R.id.issue_rb2)
    RadioButton issue_rb2;
    @BindView(R.id.issue_rb3)
    RadioButton issue_rb3;
    @BindView(R.id.issue_rb4)
    RadioButton issue_rb4;
    @BindView(R.id.issue_rb5)
    RadioButton issue_rb5;
    @BindView(R.id.issue_rb6)
    RadioButton issue_rb6;
    //发布产品的类型的的布局页面,以及用户填写的参数
    @BindView(R.id.liner_type1)
    LinearLayout liner_type1;
    @BindView(R.id.liner_type2)
    LinearLayout liner_type2;
    @BindView(R.id.liner_type3)
    LinearLayout liner_type3;
    @BindView(R.id.liner_type4)
    LinearLayout liner_type4;
    @BindView(R.id.liner_type5)
    LinearLayout liner_type5;
    @BindView(R.id.liner_type6)
    LinearLayout liner_type6;
    @BindView(R.id.liner_type7)
    LinearLayout liner_type7;
    @BindView(R.id.liner_type8)
    LinearLayout liner_type8;
    @BindView(R.id.liner_type9)
    LinearLayout liner_type9;
    @BindView(R.id.liner_type11)
    LinearLayout liner_type11;
    @BindView(R.id.liner_type12)
    LinearLayout liner_type12;
    @BindView(R.id.liner_type13)
    LinearLayout liner_type13;
    @BindView(R.id.liner_type14)
    LinearLayout liner_type14;
    @BindView(R.id.liner_type16)
    LinearLayout liner_type16;
    private LinearLayout[] linear;
    @BindView(R.id.et_description)
    EditText et_description;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.et1_01)
    EditText et1_01;
    @BindView(R.id.et1_02)
    EditText et1_02;
    @BindView(R.id.et1_03)
    EditText et1_03;
    @BindView(R.id.et1_04)
    EditText et1_04;
    @BindView(R.id.et2_01)
    EditText et2_01;
    @BindView(R.id.et2_02)
    EditText et2_02;
    @BindView(R.id.et3_01)
    EditText et3_01;
    @BindView(R.id.et3_02)
    EditText et3_02;
    @BindView(R.id.et3_03)
    EditText et3_03;
    @BindView(R.id.et4_01)
    EditText et4_01;
    @BindView(R.id.et4_02)
    EditText et4_02;
    @BindView(R.id.et4_03)
    EditText et4_03;
    @BindView(R.id.et4_04)
    EditText et4_04;
    @BindView(R.id.et5_01)
    EditText et5_01;
    @BindView(R.id.et5_02)
    EditText et5_02;
    @BindView(R.id.et5_03)
    EditText et5_03;
    @BindView(R.id.et6_01)
    EditText et6_01;
    @BindView(R.id.et6_02)
    EditText et6_02;
    @BindView(R.id.et6_03)
    EditText et6_03;
    @BindView(R.id.et7_01)
    EditText et7_01;
    @BindView(R.id.et7_02)
    EditText et7_02;
    @BindView(R.id.et7_03)
    EditText et7_03;
    @BindView(R.id.et8_01)
    EditText et8_01;
    @BindView(R.id.et8_02)
    EditText et8_02;
    @BindView(R.id.et9_01)
    EditText et9_01;
    @BindView(R.id.et9_02)
    EditText et9_02;
    @BindView(R.id.et9_03)
    EditText et9_03;
    @BindView(R.id.et11_01)
    EditText et11_01;
    @BindView(R.id.et11_02)
    EditText et11_02;
    @BindView(R.id.et11_03)
    EditText et11_03;
    @BindView(R.id.tv_location)
    TextView tv_location;
    @BindView(R.id.ll_consign)
    LinearLayout ll_consign;
    @BindView(R.id.ll_postage)
    LinearLayout ll_postage;
    @BindView(R.id.et12_01)
    EditText et12_01;
    @BindView(R.id.et12_02)
    EditText et12_02;
    @BindView(R.id.et13_01)
    EditText et13_01;
    @BindView(R.id.et13_02)
    EditText et13_02;
    @BindView(R.id.et14_01)
    EditText et14_01;
    @BindView(R.id.et14_02)
    EditText et14_02;
    @BindView(R.id.et14_03)
    EditText et14_03;
    @BindView(R.id.et14_04)
    EditText et14_04;
    @BindView(R.id.et14_05)
    EditText et14_05;
    @BindView(R.id.et16_01)
    EditText et16_01;
    @BindView(R.id.et16_02)
    EditText et16_02;
    @BindView(R.id.et16_03)
    EditText et16_03;
    @BindView(R.id.et16_04)
    EditText et16_04;
    private String et1_str = "";
    private String et2_str = "";
    private String et3_str = "";
    private String et4_str = "";
    private String et5_str = "";
    private String description = "";
    private String sql = "";
    private String name;   //发布消息时候的名字
    private String phone;  //发布消息时候的电话号码
    private String data_id_card;   //发布消息时候的身份证号码
    //选择相机还是相册里面的图片
    //调用系统相册-请求码
    private static final int PHOTO_CODE = 10;
    //调用系统相机-请求码
    private static final int CAMERA_CODE = 11;
    //调用相机相册-请求权限-请求码
    private static final int PERMISSION_CODE = 100;
    private DialogChoosePicture dialogChoosePicture;
    //    @BindView(R.id.issue_add)
//    TextView issue_add;
//    @BindView(R.id.issue_add_reselect)
//    TextView issue_add_reselect;
//    @BindView(R.id.issue_img)
//    ImageView issue_img;
    private Uri imageUri_take;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private DialogChoosePicture dialog;
    public String[] mUrls;
    private String url = "";
    public long time;
    private String location = "";
    private SimpleDateFormat sdf;
    private String content = "";
    private String date = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_issue);    //activity_issue
        issueActivity = this;
        ButterKnife.bind(this);
        initArray();
        initData();
        initWidget();
        consign_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.consign_yes:
                        consign = "可托运";

                        break;
                    case R.id.consign_no:
                        consign = "不可托运";

                        break;
                }
            }
        });
        postage_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.postage_yes:
                        postage = "免运费";

                        break;
                    case R.id.postage_no:
                        postage = "运费另计";

                        break;
                }
            }
        });
        issue_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.issue_rb0:
                        issue = 0;
                        break;
                    case R.id.issue_rb1:
                        issue = 1;
                        break;
                    case R.id.issue_rb2:
                        issue = 2;
                        break;
                    case R.id.issue_rb3:
                        issue = 3;
                        break;
                    case R.id.issue_rb4:
                        issue = 4;
                        break;
                    case R.id.issue_rb5:
                        issue = 5;
                        break;
                    case R.id.issue_rb6:
                        issue = 6;
                        break;
                    case R.id.issue_rb7:
                        issue = 7;
                        break;
                    case R.id.issue_rb8:
                        issue = 8;
                        break;
                    case R.id.issue_rb9:
                        issue = 9;
                        break;
                    case R.id.issue_rb10:
                        issue = 10;
                        break;
                    case R.id.issue_rb11:
                        issue = 11;
                        break;
                    case R.id.issue_rb12:
                        issue = 12;
                        break;
                    case R.id.issue_rb13:
                        issue = 13;
                        break;
                    case R.id.issue_rb14:
                        issue = 14;
                        break;
                    case R.id.issue_rb15:
                        issue = 15;
                        break;
                    case R.id.issue_rb16:
                        issue = 16;
                        break;
                    case R.id.issue_rb17:
                        issue = 17;
                        break;
                }
                if (issue == 12) {
                    tv_location.setText("厂房的位置:");
                    ll_consign.setVisibility(View.GONE);
                    ll_postage.setVisibility(View.GONE);
                } else if (issue == 13) {
                    tv_location.setText("起始位置:");
                    ll_consign.setVisibility(View.GONE);
                    ll_postage.setVisibility(View.GONE);
                } else {
                    tv_location.setText("产品的位置:");
                    ll_consign.setVisibility(View.VISIBLE);
                    ll_postage.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < linear.length; i++) {
                    if (i == issue) {
                        linear[i].setVisibility(View.VISIBLE);
                    } else {
                        linear[i].setVisibility(View.GONE);
                        if (issue == 0 || issue == 14) {
                            linear[0].setVisibility(View.VISIBLE);
                        }
                        if (issue == 8 || issue == 17) {
                            linear[8].setVisibility(View.VISIBLE);
                        }
                        if (issue == 9 || issue == 10) {
                            linear[9].setVisibility(View.VISIBLE);
                        }
                        if (issue == 1 || issue == 15) {
                            linear[1].setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void initArray() {
        linear = new LinearLayout[]{liner_type14, liner_type1, liner_type2, liner_type3, liner_type4, liner_type5, liner_type6,
                liner_type7, liner_type8, liner_type9, liner_type9, liner_type11, liner_type12, liner_type13, liner_type14, liner_type1, liner_type16, liner_type8};
    }

//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            data_id_card = (String) msg.obj;
//            if (!TextUtils.isEmpty(data_id_card)) {
//
//            } else {
//
//                dialogCheckId = new DialogCheckId(IssueActivity.this, IssueActivity.this, new DialogCheckId.ICustomDialogEventListener() {
//                    @Override
//                    public void customDialogEvent(int id) {
//                        switch (id) {
//                            case R.id.dia_no:
//                                IssueActivity.this.finish();
//                                break;
//                            case R.id.dia_yes:
//                                startActivity(PersonalData.class);
//                                IssueActivity.this.finish();
//                                break;
//                        }
//                    }
//                }, R.style.dialog_choose);
//                dialogCheckId.show();
////                full_check_id();
//            }
//        }
//    };

    private void initData() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        title_tv.setText("发布消息");
        time = System.currentTimeMillis();
        consign_yes.setChecked(true);
        postage_no.setChecked(true);
        name = LoginInfo.getString(this, "name", "");
        phone = LoginInfo.getString(this, "phone", "");
        if (phone.equals("")) {
            startActivity(LoginActivity.class, 0);
            finish();
        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Connection connection = Jdbc.getConnection("root", "1q23lyc45j");
//                    Map<String, String> map = new HashMap<String, String>();
//                    map = Query.query_id_card(connection, phone);
//                    data_id_card = map.get("id_card");
//                    Message msg = Message.obtain();
//                    msg.obj = data_id_card;
//                    handler.sendMessageDelayed(msg, 0);
//
//                }
//            }).start();
            issue_et_name.setText(name);
            issue_et_name.setSelection(name.length());
            issue_tv_phone.setText(phone);
        }
    }

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
                            PictureSelector.create(IssueActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(IssueActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(IssueActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            seller();

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
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
                    break;
            }
        } else if (resultCode == CODE_LOCATION) {
            location = data.getStringExtra("location");
            et_location.setText(location);
            et_location.setSelection(location.length());
        }
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION}, requestCode = 10)
    public void seller() {

        dialog = new DialogChoosePicture(IssueActivity.this, "", new DialogChoosePicture.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                if (id == R.id.tv_camera) {
                    //拍照
                    PictureSelector.create(IssueActivity.this)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_photo) {
                    //相册
                    PictureSelector.create(IssueActivity.this)
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
        full(IssueActivity.this, dialog);
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
    }

    /**
     * 权限被取消
     */
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(this, "禁止权限会影响到app的正常使用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
    }

    private String path = "";

    //几种选择图片的点击事件判断
    @OnClick({R.id.title_im, R.id.issue_submit, R.id.img_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                IssueActivity.this.finish();
                break;
            case R.id.issue_submit:
                checkPhoneNumber();
                break;
            case R.id.img_location:
                Intent intent = new Intent(IssueActivity.this, BaseMapActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private void addUrls() {
        mUrls = null;
        mUrls = new String[9];
        for (int i = 0; i < selectList.size(); i++) {
            mUrls[i] = selectList.get(i).getPath();
        }
//        if (selectList.size() > 0) {
//            mUrls[0] = selectList.get(0).getPath();
//        }
//        if (selectList.size() > 1) {
//            mUrls[1] = selectList.get(1).getPath();
//        }
//        if (selectList.size() > 2) {
//            mUrls[2] = selectList.get(2).getPath();
//        }
//        if (selectList.size() > 3) {
//            mUrls[3] = selectList.get(3).getPath();
//        }
//        if (selectList.size() > 4) {
//            mUrls[4] = selectList.get(4).getPath();
//        }
//        if (selectList.size() > 5) {
//            mUrls[5] = selectList.get(5).getPath();
//        }
//        if (selectList.size() > 6) {
//            mUrls[6] = selectList.get(6).getPath();
//        }
//        if (selectList.size() > 7) {
//            mUrls[7] = selectList.get(7).getPath();
//        }
//        if (selectList.size() > 8) {
//            mUrls[8] = selectList.get(8).getPath();
//        }
    }

    //提交之前检查姓名 手机号不为空，产品类型
    private void checkPhoneNumber() {
        date = sdf.format(System.currentTimeMillis());
        description = et_description.getText().toString().trim();
        name = issue_et_name.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {  //联系人姓名
            LoginInfo.setString(IssueActivity.this, "name", name);
            if (!TextUtils.isEmpty(location)) {  //位置不是空的
                if (!TextUtils.isEmpty(description)) { //描述不为空
                    if (selectList.size() > 0) {          //图片不为空
                        addUrls();
                        name = "联系人:" + name;
                        location = "位置:" + location;
                        description = "描述:" + description;
                        switch (issue) {
                            case 0:
                                et1_str = et14_01.getText().toString().trim();
                                et2_str = et14_02.getText().toString().trim();
                                et3_str = et14_03.getText().toString().trim();
                                et4_str = et14_04.getText().toString().trim();
                                et5_str = et14_05.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str) || TextUtils.isEmpty(et5_str)) {
                                    Util.showToast(IssueActivity.this, "品牌以外的参数不能为空");
                                } else {
                                    et1_str ="名称:" + et1_str;
                                    et2_str ="品牌:" + et2_str;
                                    et3_str ="型号:" + et3_str;
                                    et4_str ="规格:" + et4_str;
                                    et5_str ="单价:" + et5_str;
                                    content = et1_str+","+et2_str+","+et3_str+","+et4_str+","+et5_str+","+consign+","+postage+","+location+","+name+","+phone+","+description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_0_others(`url`,`goods`, `long`,`wide`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`, `location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + et5_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_0_others");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 1:
                                et1_str = et1_01.getText().toString().trim();
                                et2_str = et1_02.getText().toString().trim();
                                et3_str = et1_03.getText().toString().trim();
                                et4_str = et1_04.getText().toString().trim();
                                if (TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str)) {
                                    Util.showToast(IssueActivity.this, "品牌除外其他参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = et3_str + "kg";
                                    et4_str = et4_str + "元";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + et4_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_1_second_hand(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_1_second_hand");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 2:
                                et1_str = et2_01.getText().toString().trim();
                                et2_str = et2_02.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + "公厘米";
                                    et2_str = et2_str + "元/张";
                                    content = et1_str + "," + et2_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_2_wool(`url`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`, `location`,`description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_2_wool");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 3:
                                et1_str = et3_01.getText().toString().trim();
                                et2_str = et3_02.getText().toString().trim();
                                et3_str = et3_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + getString(R.string.wide);
                                    et2_str = et2_str + getString(R.string.thick);
                                    et3_str = et3_str + "元/kg";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_3_resist(`url`,`wide`, `thickness`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_3_resist");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 4:
                                et1_str = et4_01.getText().toString().trim();
                                et2_str = et4_02.getText().toString().trim();
                                et3_str = et4_03.getText().toString().trim();
                                et4_str = et4_04.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + getString(R.string.lon);
                                    et2_str = et2_str + getString(R.string.wide);
                                    et3_str = et3_str + getString(R.string.thick);
                                    et4_str = et4_str + "元/张";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + et4_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_4_sponge(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_4_sponge");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 5:
                                et1_str = et5_01.getText().toString().trim();
                                et2_str = et5_02.getText().toString().trim();
                                et3_str = et5_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + getString(R.string.diameter);
                                    et2_str = et2_str + getString(R.string.lon);
                                    et3_str = et3_str + "元/米";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_5_tube(`url`,`diameter`, `long`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_5_tube");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 6:
                                et1_str = et6_01.getText().toString().trim();
                                et2_str = et6_02.getText().toString().trim();
                                et3_str = et6_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + getString(R.string.lon);
                                    et2_str = et2_str + getString(R.string.wide);
                                    et3_str = et3_str + "元/根";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_6_door(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_6_door");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 7:
                                et1_str = et7_01.getText().toString().trim();
                                et2_str = et7_02.getText().toString().trim();
                                et3_str = et7_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = et3_str + "元/台";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_7_elect_machine(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_7_elect_machine");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 8:
                                et1_str = et8_01.getText().toString().trim();
                                et2_str = et8_02.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = "规格:" + et1_str;
                                    et2_str = et2_str + "元/个";
                                    content = et1_str + "," + et2_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_8_shield(`url`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_8_shield");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 9:
                                et1_str = et9_01.getText().toString().trim();
                                et2_str = et9_02.getText().toString().trim();
                                et3_str = et9_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = et3_str + "元/个";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product_9_plate(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product_9_plate");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 10:
                                et1_str = et9_01.getText().toString().trim();
                                et2_str = et9_02.getText().toString().trim();
                                et3_str = et9_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = et3_str + "元/个";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product10_lock_ring(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`, `location`,`description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product10_lock_ring");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 11:
                                et1_str = et11_01.getText().toString().trim();
                                et2_str = et11_02.getText().toString().trim();
                                et3_str = et11_03.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = et1_str + getString(R.string.lon);
                                    et2_str = et2_str + getString(R.string.wide);
                                    et3_str = et3_str + "元/米";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product11_transporter(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`, `location`,`description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product11_transporter");
                                            break;
                                        }
                                    }
                                }

                                break;
                            case 12:
                                et1_str = et12_01.getText().toString().trim();
                                et2_str = et12_02.getText().toString().trim();
                                if (!TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
                                    et1_str = et1_str + "㎡";
                                    et2_str = et2_str + "元/月";
                                    content = et1_str + "," + et2_str + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product12_factory(`url`,`area`, `price`,`location`, `contact`,`phone`,  `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + location + "','" + name + "','" + phone + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product12_factory");
                                            break;
                                        }
                                    }
                                } else {
                                    Util.showToast(IssueActivity.this, "厂房参数不能为空");
                                }
                                break;
                            case 13:
                                et1_str = et13_01.getText().toString().trim();
                                et2_str = et13_02.getText().toString().trim();
                                if (!TextUtils.isEmpty(et1_str)) {
                                    if (!TextUtils.isEmpty(et2_str)) {
                                        et1_str ="终点位置:"+et1_str;
                                        et2_str ="最晚时间:"+et2_str;
                                        content = et2_str + "," + "起始" + location + "," + et1_str + "," + name + "," + phone + "," + description;
                                        for (int i = 0; i < mUrls.length; i++) {
                                            if (!TextUtils.isEmpty(mUrls[i])) {
                                                insertIf(i);
                                            } else {
                                                sql = "insert into product13_logistics(`url`,`start_time`,`location_start`,`location_end`, `contact`,`phone`, `description`, `content`, `date`)values('" + url + "','" + et2_str + "','" + location + "','" + et1_str + "','" + name + "','" + phone + "','" + description + "','" + content + "','" + date + "')";
                                                insertElse("product13_logistics");
                                                break;
                                            }
                                        }

                                    } else {
                                        Util.showToast(IssueActivity.this, "请填写行程的起始位置");
                                    }
                                } else {
                                    Util.showToast(IssueActivity.this, "请填写行程的开始时间");
                                }
                                break;
                            case 14:
                                et1_str = et14_01.getText().toString().trim();
                                et2_str = et14_02.getText().toString().trim();
                                et3_str = et14_03.getText().toString().trim();
                                et4_str = et14_04.getText().toString().trim();
                                et5_str = et14_05.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str) || TextUtils.isEmpty(et5_str)) {
                                    Util.showToast(IssueActivity.this, "品牌以外的参数不能为空");
                                } else {
                                    et1_str = "名称:" + et1_str;
                                    et2_str = "品牌:" + et2_str;
                                    et3_str = "型号:" + et3_str;
                                    et4_str = "规格:" + et4_str;
                                    et5_str = "单价:" + et5_str;
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + et4_str + "," + et5_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product14_casting(`url`,`goods`, `long`,`wide`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + et5_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product14_casting");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 15:
                                et1_str = et1_01.getText().toString().trim();
                                et2_str = et1_02.getText().toString().trim();
                                et3_str = et1_03.getText().toString().trim();
                                et4_str = et1_04.getText().toString().trim();
                                if (TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str)) {
                                    Util.showToast(IssueActivity.this, "品牌除外其他参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = et3_str + "kg";
                                    et4_str = et4_str + "元";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + et4_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product15_pwj(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`, `location`,`description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product15_pwj");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 16:
                                et1_str = et16_01.getText().toString().trim();
                                et2_str = et16_02.getText().toString().trim();
                                et3_str = et16_03.getText().toString().trim();
                                et4_str = et16_04.getText().toString().trim();
                                if (TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str)) {
                                    Util.showToast(IssueActivity.this, "品牌除外其他参数不能为空");
                                } else {
                                    et1_str = "品牌:" + et1_str;
                                    et2_str = "型号:" + et2_str;
                                    et3_str = "孔数:" + et3_str + "个";
                                    et4_str = et4_str + "元";
                                    content = et1_str + "," + et2_str + "," + et3_str + "," + et4_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product16_track(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product16_track");
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 17:
                                et1_str = et8_01.getText().toString().trim();
                                et2_str = et8_02.getText().toString().trim();
                                if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
                                    Util.showToast(IssueActivity.this, "产品的参数不能为空");
                                } else {
                                    et1_str = "规格:" + et1_str;
                                    et2_str = et2_str + "元/个";
                                    content = et1_str + "," + et2_str + "," + consign + "," + postage + "," + location + "," + name + "," + phone + "," + description;
                                    for (int i = 0; i < mUrls.length; i++) {
                                        if (!TextUtils.isEmpty(mUrls[i])) {
                                            insertIf(i);
                                        } else {
                                            sql = "insert into product17_sand(`url`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`,`location`, `description`, `content`, `date`)values('" + url + "','" + et1_str + "','" + et2_str + "','" + name + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + description + "','" + content + "','" + date + "')";
                                            insertElse("product17_sand");
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                    } else {
                        Util.showToast(IssueActivity.this, "请选择图片");
                    }
                } else {//手机号错误
                    Util.showToast(IssueActivity.this, "描述不能为空");
                }
            } else {  //描述为空
                Util.showToast(IssueActivity.this, "位置不能为空");
            }
        } else {     //用户名为空
            Util.showToast(IssueActivity.this, "姓名不能为空");
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
//                        Log.e(TAG, ":" + sql + "--" + url);
//                        startActivity(IssueSuccess.class);//点击按钮立马跳转
//                        finish();
                        insertIssue(phone, table_name);
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
                                        startActivity(IssueSuccess.class);//点击按钮立马跳转
                                    }
                                });
                    }
                });
    }
//    protected void startActivity(Class<?> activity) {
//        Intent intent = new Intent();
//        intent.setClass(IssueActivity.this, activity);
//        startActivity(intent);
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IssueActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置弹窗宽度
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    //设置弹窗宽度
    private void full_check_id() {
        WindowManager windowManager = IssueActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogCheckId.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogCheckId.getWindow().setAttributes(lp);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("IssueActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("IssueActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
