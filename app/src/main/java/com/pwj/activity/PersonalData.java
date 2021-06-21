package com.pwj.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pwj.BaseActivity;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.dialog.DialogChoosePicture;
import com.pwj.helloya.R;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;

import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;


import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


/**
 * Created by delphi0127 on 2018/7/13.
 */

public class PersonalData extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.person_data_pro)
    ProgressBar person_data_pro;

    @BindView(R.id.linear_yes)
    LinearLayout linear_yes;
    @BindView(R.id.data_submit)
    Button data_submit;
    @BindView(R.id.data_name)
    EditText data_name;
    @BindView(R.id.data_id_card)
    EditText data_id_card;
    @BindView(R.id.data_tv_phone)
    TextView data_tv_phone;

    private String phone;
    private String name;
    private String number;

    @BindView(R.id.linear_no)
    LinearLayout linear_no;
    @BindView(R.id.data_already_name)
    TextView ddata_already_name;
    @BindView(R.id.data_already_id)
    TextView data_already_id;
    @BindView(R.id.data_already_phone)
    TextView data_already_phone;

    //选择相机还是相册里面的图片
    //调用系统相册-请求码
    private static final int PHOTO_CODE = 10;
    //调用系统相机-请求码
    private static final int CAMERA_CODE = 11;
    //调用相机相册-请求权限-请求码
    private static final int PERMISSION_CODE = 100;
    private DialogChoosePicture dialog;
    private Map<String, String> map;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private int maxSelectNum = 2;
    public List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private String[] mUrls;
    private String url = "";
    private long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_person_data);
        ButterKnife.bind(this);
        phone = LoginInfo.getString(this, "phone", "");
        title_tv.setText("实名信息");
        initWidget();
        initView();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            map = (Map<String, String>) msg.obj;
            number = map.get("id_card");
            name = map.get("name");
            person_data_pro.setVisibility(View.GONE);
            if (TextUtils.isEmpty(number)) {
                linear_no.setVisibility(View.VISIBLE);
                linear_yes.setVisibility(View.GONE);
                data_tv_phone.setText(phone);

            } else {
                linear_no.setVisibility(View.GONE);
                linear_yes.setVisibility(View.VISIBLE);
                data_already_phone.setText(phone);
                data_already_id.setText(number);
                ddata_already_name.setText(name);



            }
        }
    };

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = Jdbc.getConnection("root", "1q23lyc45j");
                map = new HashMap<String, String>();
                map = Query.query_id_card(connection, phone);
                Message msg = Message.obtain();
                msg.obj = map;
                handler.sendMessageDelayed(msg, 0);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    //几种选择图片的点击事件判断
    @OnClick({R.id.title_im, R.id.data_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                PersonalData.this.finish();
                break;
            case R.id.data_submit:
                congfirmIdCard();
                break;
        }
    }

    private void addUrls() {
        mUrls = null;
        mUrls = new String[9];
        if (selectList.size() > 0) {
            mUrls[0] = selectList.get(0).getPath();
        }
        if (selectList.size() > 1) {
            mUrls[1] = selectList.get(1).getPath();
        }
    }

    private void congfirmIdCard() {
        time = System.currentTimeMillis();
        name = data_name.getText().toString().trim();
        number = data_id_card.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            if (selectList.size() == 2) {
                addUrls();
                for (int i = 0; i < mUrls.length; i++) {
                    if (!TextUtils.isEmpty(mUrls[i])) {
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
                    } else {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "update login set url2 ='" + url + "',name ='" + name + "',id_card = '" + number + "'where phone = '" + phone + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        startActivity(SubmitSuccess.class);//点击按钮立马跳转
                                        finish();
                                    }
                                });
                        break;
                    }
                }
            } else {
                Util.showToast(PersonalData.this, "请上传身份证正反面");
            }
        } else {
            Util.showToast(PersonalData.this, "请填写姓名");
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
                            PictureSelector.create(PersonalData.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PersonalData.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PersonalData.this).externalPictureAudio(media.getPath());
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
        }
    }

    public void select() {

        dialog = new DialogChoosePicture(PersonalData.this, "", new DialogChoosePicture.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                if (id == R.id.tv_camera) {
                    //拍照
                    PictureSelector.create(PersonalData.this)
                            .openCamera(PictureMimeType.ofImage())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (id == R.id.tv_photo) {
                    //相册
                    PictureSelector.create(PersonalData.this)
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

    //设置弹窗宽度
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("PersonalData"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("PersonalData");
//        MobclickAgent.onPause(this); //统计时长
//    }


}
