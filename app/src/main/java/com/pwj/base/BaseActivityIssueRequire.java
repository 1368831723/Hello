package com.pwj.base;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;


import com.baidu.location.LocationClient;
import com.gyf.immersionbar.ImmersionBar;
import com.iceteck.silicompressorr.SiliCompressor;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import com.pwj.activity.AddressActivity;

import com.pwj.activity.IssueSuccess;
import com.pwj.activity.LoginActivity;
import com.pwj.activity.PersonalData;

import com.pwj.bean.Comment;
import com.pwj.bean.Login;
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogCheckId;
import com.pwj.dialog.DialogChoosePicture;
import com.pwj.helloya.R;
import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.FullStopUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SoftHideKeyBoardUtil;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.subsciber.IProgressDialog;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.ButterKnife;


import static android.content.ContentValues.TAG;

/**
 * Created by 13688 on 2019/3/7.
 */

public abstract class BaseActivityIssueRequire extends Activity implements View.OnClickListener {
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.title_tv)
    public TextView title_tv;
    @BindView(R.id.title_im)
    LinearLayout title_im;
    @BindView(R.id.tv_location)
    public TextView tv_location;
    private int position;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private int maxSelectNum = 9;
    public List<LocalMedia> selectList = new ArrayList<>();
    public String[] mUrls;
    private GridImageAdapter adapter;
    private ProgressDialog dialog;
    @BindView(R.id.edt_location)
    EditText edt_location;
    @BindView(R.id.img_location)
    ImageView img_location;
    @BindView(R.id.edt_remarks)
    EditText edt_remarks;
    private static final int PERMISSION_LOCATION_CODE = 100;
    private String data_id_card;   //发布消息时候的身份证号码
    public String phone;  //注册时候的电话号码
    public String contact;   //发布消息时候的名字
    public String phone_address;  //注册时候的电话号码
    public String content = "";
    private final int CODE_LOCATION = 10;
    public String location = "";
    public String province;
    public String city;
    public String county;
    public String street = "";
    public String specific = "";
    public String remarks = "";     //需求补充
    public String date = "";
    private SimpleDateFormat sdf;
    @BindView(R.id.frm_content)
    FrameLayout frm_content;
    public View view;
    public LocationClient mLocationClient = null;
    public long time;
    public String url = "";
    public int url_type = 1;
    private String url_type_str = "";
    private List<Login> data = new ArrayList<>();
    private DialogCheckId dialogCheckId;
    public String title = "";
    public int type = 0;
    private List<Comment> data_concern = new ArrayList<>();
    private List<Comment> data_collect = new ArrayList<>();
    private List<Comment> data_love = new ArrayList<>();
    private String phone_all = "";
    private String concern = "";
    private String collect = "";
    private String user_name;
    private List<User> data_user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_issue_require);
        ButterKnife.bind(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        initData();
        initChild();
        initWidget();
    }

    public abstract void initChild();

    //  设置要显示的布局方法
    public void initLayout(int layoutID) {
//		获得inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		把继承该BaseAcitivyt的layoutID放进来 显示
        view = inflater.inflate(layoutID, null);
//		addview
        frm_content.addView(view);
    }


    private void initData() {
        type = getIntent().getIntExtra("type", 0);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        title_im.setOnClickListener(this);
        img_location.setOnClickListener(this);
        time = System.currentTimeMillis();
        phone = LoginInfo.getString(this, "phone", "");
        user_name = LoginInfo.getString(this, "user_name", "");
        if (phone.equals("")) {
            startActivity(LoginActivity.class, 0);
            finish();
        } else {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT * FROM address WHERE phone = " + phone + " AND number = 1")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            List<Product> data = GsonUtils.getGsonToList(s, Product.class);
                            if (data.size() > 0) {
                                contact = data.get(0).getContact();
                                phone_address = data.get(0).getPhone_address();
                                location = data.get(0).getLocation();
                                province = data.get(0).getProvince();
                                city = data.get(0).getCity();
                                county = data.get(0).getCounty();
                                street = data.get(0).getStreet();
                                specific = data.get(0).getSpecific();
                                edt_location.setText(contact + " " + phone_address + "\n" + location);
                            }
                        }
                    });
            type = getIntent().getIntExtra("type", 0);
            title = getIntent().getStringExtra("title");
        }
    }

    private void cheIdentify() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT user_name,phone,id_card FROM login WHERE phone = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data = GsonUtils.getGsonToList(json, Login.class);
                        if ("".equals(data.get(0).getId_card())) {
                            dialogCheckId = new DialogCheckId(BaseActivityIssueRequire.this, BaseActivityIssueRequire.this, new DialogCheckId.ICustomDialogEventListener() {
                                @Override
                                public void customDialogEvent(int id) {
                                    switch (id) {
                                        case R.id.dia_no:
                                            BaseActivityIssueRequire.this.finish();
                                            break;
                                        case R.id.dia_yes:
                                            startActivity(PersonalData.class);
                                            BaseActivityIssueRequire.this.finish();
                                            break;
                                    }
                                }
                            }, R.style.dialog_choose);
                            dialogCheckId.show();
                            full(BaseActivityIssueRequire.this, dialogCheckId);
                        }
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
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(BaseActivityIssueRequire.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(BaseActivityIssueRequire.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(BaseActivityIssueRequire.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    public boolean submit() {
        date = sdf.format(System.currentTimeMillis());
        remarks = edt_remarks.getText().toString().trim();
        url = "";
        mUrls = null;
        mUrls = new String[selectList.size()];
//        for (int i = 0; i < selectList.size(); i++) {
//            mUrls[i] = selectList.get(i).getPath();
//        }
        if (url_type == 1) {
            for (int i = 0; i < selectList.size(); i++) {
                mUrls[i] = BitmapUtil.compressImage(selectList.get(i).getPath());
            }
        } else {
            mUrls[0] = selectList.get(0).getPath();
        }
        if (!TextUtils.isEmpty(location)) {
            if (!"".equals(remarks)) {
                remarks = FullStopUtil.getFullStop(remarks);
                return true;
            } else {
                Util.showToast(this, "备注不能为空");
            }
        } else {
            Util.showToast(this, "位置不能为空");
        }
        return false;
    }

    protected void compress() {
        final String outputVideoPath = "/storage/emulated/0/pwj/video";
//            String prefix = mUrls[0].substring(0, mUrls[0].lastIndexOf("."));
//            String postfix = mUrls[0].substring(mUrls[0].lastIndexOf("."));
//            String outputVideoPath = prefix + "1" + postfix;

        File file = null;
        file = new File(mUrls[0]);
        if (file.length() <= 8000000) {
            mProgressDialog.getDialog().show();
            try {
                file = new File(outputVideoPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        if (mUrls[0].indexOf("DCIM") != -1) {
                        if (mUrls[0].length() < 1) {
                            try {
                                mUrls[0] = SiliCompressor.with(BaseActivityIssueRequire.this).compressVideo(mUrls[0], outputVideoPath);
                                insert1();
                            } catch (URISyntaxException e) {
                                mUrls[0] = selectList.get(0).getPath();
                                insert1();
                                e.printStackTrace();
                            }
                        } else {
                            MediaMetadataRetriever retr = new MediaMetadataRetriever();
                            retr.setDataSource(mUrls[0]);
                            Bitmap bm = retr.getFrameAtTime();
                            String mediaWidth = String.valueOf(bm.getWidth());
                            String mediaHeight = String.valueOf(bm.getHeight());
                            try {
                                mUrls[0] = SiliCompressor.with(BaseActivityIssueRequire.this).compressVideo(mUrls[0], outputVideoPath, Integer.parseInt(mediaWidth), Integer.parseInt(mediaHeight), 450000);
                                insert1();
                                Log.e("insert1: ", "insert1: 2");
                            } catch (URISyntaxException e) {
                                mUrls[0] = selectList.get(0).getPath();
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mProgressDialog.getDialog().dismiss();
            }
        } else {
            Util.showToast(BaseActivityIssueRequire.this, "对不起，请上传8M以内的视频");
        }
    }
    public abstract void insert1();

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            select();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            images = PictureSelector.obtainMultipleResult(data);
            selectList.addAll(images);
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
        } else if (resultCode == CODE_LOCATION) {
            contact = data.getStringExtra("contact");
            phone_address = data.getStringExtra("phone_address");
            location = data.getStringExtra("location");
            province = data.getStringExtra("province");
            city = data.getStringExtra("city");
            county = data.getStringExtra("county");
            street = data.getStringExtra("street");
            specific = data.getStringExtra("specific");
            edt_location.setText(contact + " " + phone_address + "\n" + location);
        }
    }

    public void select() {
        if (url_type == 2 && selectList.size() > 0) {
            Util.showToast(BaseActivityIssueRequire.this, "对不起，最多只能上传一个视频");
        } else {
            DialogChoosePicture dialog = new DialogChoosePicture(BaseActivityIssueRequire.this, "", new DialogChoosePicture.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(int id) {
                    if (id == R.id.tv_camera) {
                        //拍照
                        url_type = 1;
                        url_type_str = "上传图片中";
                        PictureSelector.create(BaseActivityIssueRequire.this)
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                    } else if (id == R.id.tv_photo) {
                        //相册
                        url_type = 1;
                        url_type_str = "上传图片中";
                        PictureSelector.create(BaseActivityIssueRequire.this)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(maxSelectNum - selectList.size())
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                    } else if (id == R.id.tv_video) {
                        //相册
                        if (url_type == 1 && selectList.size() > 0) {
                            Util.showToast(BaseActivityIssueRequire.this, "对不起，不能同时选择图片和视频");
                        } else {
                            url_type = 2;
                            url_type_str = "视频压缩上传中";
                            PictureSelector.create(BaseActivityIssueRequire.this)
                                    .openGallery(PictureMimeType.ofVideo())
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(4)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .forResult(PictureConfig.CHOOSE_REQUEST);
                        }
                    }
                }
            }, R.style.dialog_choose_picture);
            dialog.show();
            full(this, dialog);
        }
    }

    public void insertIssue(String phone, String table_name) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone FROM comment_5_concern WHERE phone_concern = '" + phone + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_concern = GsonUtils.getGsonToList(str, Comment.class);
                        for (int i = 0; i < data_concern.size(); i++) {
                            concern = concern + data_concern.get(i).getPhone() + ",";
                            if (i == data_concern.size() - 1) {
                                concern = concern.substring(0, concern.length() - 1);
                            }
                        }
                    }
                });
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone FROM comment_6_collect WHERE table_name = '" + table_name + "' GROUP BY phone DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_collect = GsonUtils.getGsonToList(str, Comment.class);
                    }
                });
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone FROM login WHERE love  LIKE '%"+title+"%' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_love = GsonUtils.getGsonToList(str, Comment.class);
                    }
                });
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
//                                        for (int i = 0; i < data_collect.size(); i++) {
//                                            if (!concern.contains(data_collect.get(i).getPhone())) {
//                                                collect = collect + data_collect.get(i).getPhone() + ",";
//                                            }
//                                        }
//                                        for (int j = 0; j < data_love.size(); j++) {
//                                            if (!concern.contains(data_love.get(j).getPhone())){
//                                                if(!collect.contains(data_love.get(j).getPhone())){
//                                                    collect = collect+data_love.get(j).getPhone() + ",";
//                                                }
//                                            }
//                                        }
//                                        if (collect.length()>5){
//                                            collect = collect.substring(0, collect.length() - 1);
//                                        }
//                                        if (concern.length()>5){
//                                            pushConcern(table_name,table_id);
//                                        }
//                                        if (collect.length()>5){
//                                            pushCollect(table_name,table_id);
//                                        }
                                        pushAll(table_name, table_id);

                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                        finish();
                                        startActivity(IssueSuccess.class);//点击按钮立马跳转
                                    }
                                });
                    }
                });
    }

    private void pushAll(String table_name, int table_id) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone FROM login ORDER BY phone DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data_user = GsonUtils.getGsonToList(s, User.class);
                        for (int i = 0; i < data_user.size(); i++) {
                            if (data_user.get(i).getPhone()!=null){
                                phone_all = phone_all + data_user.get(i).getPhone()+",";
                            }else {
                                phone_all = phone_all.substring(0, phone_all.length() - 1);
                                break;
                            }
                        }
                        EasyHttp.post(IpConfig.BASE + "server_mariadb_ios_use/umpush.php")
                                .params("alias", phone_all)
                                .params("title", "新需求提醒")
                                .params("text", user_name + "发布了" + title)
                                .params("from", phone)
                                .params("table_name", table_name)
                                .params("table_id", String.valueOf(table_id))
                                .params("type_class", "1")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {

                                });
                    }
                });
    }

    private void pushConcern(String table_name,int table_id ){
        EasyHttp.post("http://119.18.207.203:80/server_mariadb_ios_use/umpush.php")
                .params("alias", concern)
                .params("title", "关注提醒")
                .params("text", user_name+"发布了"+title)
                .params("from", phone)
                .params("table_name", table_name)
                .params("table_id", String.valueOf(table_id))
                .params("type_class", "1")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {

                });
    }
    private void pushCollect(String table_name,int table_id ){
        EasyHttp.post("http://119.18.207.203:80/server_mariadb_ios_use/umpush.php")
                .params("alias", collect)
                .params("title", "收藏提醒")
                .params("text", user_name+"发布了"+title)
                .params("from", phone)
                .params("table_name", table_name)
                .params("table_id", String.valueOf(table_id))
                .params("type_class", "1")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {

                });
    }
    public IProgressDialog mProgressDialog = new IProgressDialog() {
        @Override
        public Dialog getDialog() {
            dialog = new ProgressDialog(BaseActivityIssueRequire.this);
            dialog.setMessage(url_type_str + "...");
            return dialog;
        }
    };

    //设置弹窗宽度
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivityIssueRequire.this, activity);
        startActivity(intent);
    }

    protected void startActivity(Class<?> activity, int page) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivityIssueRequire.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_location:
                Intent intent = new Intent(BaseActivityIssueRequire.this, AddressActivity.class);
                intent.putExtra("flag", true);
                startActivityForResult(intent, 10);
                break;
        }
    }
}
