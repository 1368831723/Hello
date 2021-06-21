package com.pwj.base;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.helloya.R;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 13688 on 2019/3/7.
 */

public abstract class BaseActivityQueryRequire extends Activity implements View.OnClickListener {
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.title_tv)
    public TextView title_tv;
    @BindView(R.id.title_im)
    LinearLayout title_im;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.progressbar)
    public ProgressBar progressbar;
    public List<Product> data;
    public List<User> data_user;
    public List<User> data_users;
    public String phone;
    public String url = "";
    public List<Comment> data_comment;
    public int key ;
    public Map<Integer,Integer> like_map = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_query_require);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        title_im.setOnClickListener(this);
        data_user = new ArrayList<>();
        data_users = new ArrayList<>();
        data = new ArrayList<>();
        data_comment = new ArrayList<>();
        initData();
    }

    public abstract void initData();//子页面初始化方法


    @Override
    public void onClick(View view) {
        finish();
    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivityQueryRequire.this, activity);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivityQueryRequire.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }
}
