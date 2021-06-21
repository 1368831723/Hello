package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pwj.BaseActivity;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.R;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingProfile extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.data_img)
    CircleImageView data_img;
    @BindView(R.id.data_name)
    TextView data_name;
    @BindView(R.id.tv_profile)
    TextView tv_profile;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String path;
    private long time;
    private String phone;
    private final int CODE_PROFILE = 100;
    private List<Product>data = new ArrayList<>();
    private String profile = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_data);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText("个人信息");
        String user_img = LoginInfo.getString(this, "user_img", "");
        phone = LoginInfo.getString(this, "phone", "");
        Glide.with(this).load(user_img).into(data_img);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone,profile FROM login WHERE phone = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                       data = GsonUtils.getGsonToList(str,Product.class);
                        profile = data.get(0).getProfile();
                        tv_profile.setText(profile);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
        String user_name = LoginInfo.getString(this, "user_name", "");
        data_name.setText(user_name);
    }

    @OnClick({R.id.title_im, R.id.relative_img, R.id.relative_name, R.id.relative_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.relative_img:
                PictureSelector.create(SettingProfile.this)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.relative_name:
                startActivity(UpdateNameActivity.class);
                break;
            case R.id.relative_profile:
                Intent intent = new Intent(SettingProfile.this, ProfileActivity.class);
                intent.putExtra("profile",profile);
                startActivityForResult(intent, 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            images = PictureSelector.obtainMultipleResult(data);
            selectList.clear();
            selectList.addAll(images);
            path = selectList.get(0).getPath();
            path = BitmapUtil.compressImage(path);
            Glide.with(this).load(path).into(data_img);
            File file = new File(BitmapUtil.compressImage(path));
            String filename = file.getName();
            String postfix = filename.substring(filename.lastIndexOf("."));
            time = System.currentTimeMillis();
            String names = String.valueOf(time) + postfix;
            String url = IpConfig.BASE_URL + names;
            EasyHttp.post(IpConfig.URL_IMG + names)
                    .params("uploadfile", file, names, null)
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "update login set url3 = '" + url + "' where phone = '" + phone + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {

                                            LoginInfo.setString(SettingProfile.this, "user_img", url);
                                        }
                                    });
                        }
                    });
        } else if (resultCode == CODE_PROFILE) {
            profile = data.getStringExtra("profile");
            tv_profile.setText(profile);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
}
