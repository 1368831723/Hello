package com.pwj;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.ImageInfo;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.activity.LoginActivity;
import com.pwj.bean.Product;
import com.pwj.helloya.R;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SoftHideKeyBoardUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 13688 on 2019/2/25.
 */

public class BaseActivity extends AppCompatActivity{
    private FrameLayout frm_content;
    private View view;
    public LinearLayout title_linear;
    public TextView title_tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initView();
    }

    private void initView() {
        SoftHideKeyBoardUtil.assistActivity(this);
        frm_content = findViewById(R.id.frm_content);
        title_linear = findViewById(R.id.title_linear);
        title_tv = findViewById(R.id.title_tv);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    //  设置要显示的布局方法
    protected void initLayout(int layoutID) {
        //		获得inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //		把继承该BaseAcitivyt的layoutID放进来 显示
        view = inflater.inflate(layoutID, null);
        frm_content.addView(view);
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivity.this, activity);
        startActivity(intent);
    }
    protected void startActivity(Class<?> activity, int page) {
        Intent intent = new Intent();
        intent.setClass(BaseActivity.this, activity);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    public List<ImageInfo> getImageInfos(List<Product>data,int position) {
        List<ImageInfo> imageInfos = new ArrayList<>();
        List<String> uri_list = Arrays.asList(data.get(position).getUrl().split(","));
        for (String url : uri_list) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setBigImageUrl(url);
            imageInfo.setThumbnailUrl(url);
            imageInfos.add(imageInfo);
        }
        return imageInfos;
    }

    protected boolean checkPhone(Context mContext) {
       String phone = LoginInfo.getString(mContext, "phone", "");
        if (phone.equals("")) {
            startActivity(LoginActivity.class);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
