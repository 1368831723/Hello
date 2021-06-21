package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pwj.BaseActivity;

import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.photos.FullyGridLayoutManager;
import com.pwj.photos.GridImageAdapter;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingSuggestionActivity extends BaseActivity {
    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private int maxSelectNum = 9;
    public List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private List<String> mUrls = new ArrayList<>();
    private long time;
    private String url = "";
    private String title = "";
    private String phone = "";
    private String content = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_setting_suggestion);
        ButterKnife.bind(this);
        initWidget();
    }

    private void initWidget() {
        phone = LoginInfo.getString(this, "phone", "");
        time = System.currentTimeMillis();
        title_tv.setText(getResources().getString(R.string.setting_suggestion));
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
                            PictureSelector.create(SettingSuggestionActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(SettingSuggestionActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(SettingSuggestionActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            //相册
            PictureSelector.create(SettingSuggestionActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(maxSelectNum - selectList.size())
                    .minSelectNum(1)
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .forResult(PictureConfig.CHOOSE_REQUEST);

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

    @OnClick({R.id.title_im, R.id.submit})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        url = "";
        title = et1.getText().toString().trim();
        content = et2.getText().toString().trim();
        mUrls.clear();
        for (int i = 0; i < selectList.size(); i++) {
            mUrls.add(selectList.get(i).getPath());
        }
        if (!TextUtils.isEmpty(title)){
            if (!TextUtils.isEmpty(content)){
                if (mUrls.size()>0){
                    insert1();
                }else {
                    insert2();
                }
            }else {
                Util.showToast(SettingSuggestionActivity.this,"问题描述不能为空");
            }
        }else {
            Util.showToast(SettingSuggestionActivity.this,"问题名称不能为空");
        }
    }

    private void insert1() {
        for (int i = 0; i < mUrls.size(); i++) {
            File file = new File(BitmapUtil.compressImage(mUrls.get(i)));
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
            if (i == mUrls.size()-1){
                insert2();
            }
        }


    }
    private void insert2() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into suggestion(`url`,`title`, `phone`, `content`)values('" + url + "','" + title + "','" + phone + "','" + content + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        startActivity(IssueSuccess.class);//点击按钮立马跳转
                        finish();
                    }
                });
    }
    public void onResume() {
        super.onResume();

    }

}
