package com.pwj.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;


import com.pwj.BaseActivity;
import com.pwj.helloya.BuildConfig;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingSaveActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText(getResources().getString(R.string.setting_save));
        //调用系统文件管理器打开指定路径目录
        //获取到指定文件夹，这里为：/storage/emulated/0/Android/data/你的包	名/files/Download
//        File file = new File(Environment.getExternalStorageDirectory() + IpConfig.PATH_DATA);
        File file = new File("/storage/emulated/0/Android/data/com.pwj.helloya/files/pwj");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
        Uri uri = FileProvider.getUriForFile(this, "com.pwj.helloya.fileprovider",file);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,200);
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        finish();
    }

}
