package com.pwj.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.adapter.SearchResultAdapter;
import com.pwj.bean.Product;
import com.pwj.helloya.R;
import com.pwj.interfaces.QueryRunnable;
import com.pwj.interfaces.ThreadCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by han on 2018/9/4.
 */

public class TypeDisplay extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.recycle_type)
    RecyclerView recycle_type;
    @BindView(R.id.type_display_pro)
    ProgressBar type_display_pro;
    private List<Product> data;
    private SearchResultAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_display);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycle_type.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        data = new ArrayList<>();
        int position = getIntent().getIntExtra("position", 1);
        position = position + 1;
        queryType(position);
        title_tv.setText(getIntent().getStringExtra("type_name"));
    }

    @OnClick(R.id.title_im)
    protected void onViewClicked() {
        TypeDisplay.this.finish();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            data = (List<Product>) msg.obj;
            myAdapter = new SearchResultAdapter(TypeDisplay.this, data);

            recycle_type.setAdapter(myAdapter);
            type_display_pro.setVisibility(View.GONE);
        }
    };

    private void queryType(int type) {
        new Thread(new QueryRunnable(new ThreadCallback() {
            @Override
            public void threadEndLisener(List<Product> list) {
                data.clear();
                data.addAll(list);
                Message msg = Message.obtain();
                msg.obj = data;
                msg.what = 2;
                handler.sendMessageDelayed(msg, 0);
            }
        }, type,0)).start();

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("TypeDisplay"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("TypeDisplay");
//        MobclickAgent.onPause(this); //统计时长
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


}
