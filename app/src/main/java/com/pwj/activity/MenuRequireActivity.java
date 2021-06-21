package com.pwj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Item_purchase;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class MenuRequireActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    private BaseRcyAdapter adapter;
    private List<Item_purchase> data = new ArrayList<>();
    private String[] title = {"求购需求", "维修需求", "租厂需求", "物流需求", "其他需求"};
    private int tag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_product);
        ButterKnife.bind(this);
        WindowManager windowManager = this.getWindowManager();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        Display display = windowManager.getDefaultDisplay();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.gravity = Gravity.TOP;
        this.getWindow().setAttributes(lp);
        initData();
    }

    private void initData() {
        tag = getIntent().getIntExtra("tag", 0);
        for (int i = 0; i < title.length; i++) {
            Item_purchase item_purchase = new Item_purchase(title[i]);
            data.add(item_purchase);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recycle.setLayoutManager(gridLayoutManager);
        adapter = new BaseRcyAdapter(data, R.layout.item_menu_purchase) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                Button button = holder.getView(R.id.btn_title);
                button.setText(data.get(position).getTitle());
                button.setOnClickListener(MenuRequireActivity.this);
                button.setTag(position);
                if (position == tag) {
                    button.setTextColor(getResources().getColor(R.color.red_all));
                } else {
                    button.setTextColor(getResources().getColor(R.color.deep_black));
                }
            }
        };
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.btn_title:
                        tag = position;
                        adapter.notifyDataSetChanged();
                }
            }
        });
        recycle.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title:
                tag = (int) view.getTag();
                adapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("tag", tag);
                setResult(100, intent);
                finish();
//                Intent intent2 = new Intent(MenuRequireActivity.this, QueryRequireActivity.class);
//                intent2.putExtra("title", title[tag]);
//                intent2.putExtra("type", tag);
//                MenuRequireActivity.this.finish();
//                startActivity(intent2);
        }
    }

    @OnClick(R.id.share_tv)
    public void onViewClicked(View view) {
        MenuRequireActivity.this.finish();
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(MenuRequireActivity.this, activity);
        startActivity(intent);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("ShareActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("ShareActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
