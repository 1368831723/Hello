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

public class MenuProductActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    private BaseRcyAdapter adapter;
    private List<Item_purchase> data = new ArrayList<>();
    //    private String[] title = {
//            "二手抛丸机", "羊毛毡", "耐磨皮", "海绵纸", "落沙管",
//            "门条", "专用电机", "护罩", "电机结合盘", "密封圈",
//            "提升带",  "厂房出租", "物流提供", "耐磨铸造件", "抛丸机",
//            "履带", "漏沙板", "其他"};
    private String[] title = {
            "二手抛丸机", "办事处", "履带式抛丸机", "吊钩式抛丸机",
            "转台式抛丸机", "网带通过式抛丸机", "履带通过式抛丸机", "滚道通过式抛丸机",
            "悬链通过式抛丸机", "环保除尘设备", "喷砂机", "碾砂机",
            "空压机", "电机", "耐磨铸件", "合金钢丸",
            "不锈钢丸", "钢砂", "密封材料", "橡胶件",
            "气动元件", "电器", "方头螺丝", "叶轮",
            "铁水包", "链条", "出租厂房", "物流提供",
            "轴承", "钢材", "其他二手", "其他"};
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
                button.setOnClickListener(MenuProductActivity.this);
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
//                Intent intent2 = new Intent(MenuProductActivity.this, QueryProduct.class);
//                intent2.putExtra("title", title[tag]);
//                intent2.putExtra("type", tag);
//                MenuProductActivity.this.finish();
//                startActivity(intent2);
        }
    }

    @OnClick(R.id.share_tv)
    public void onViewClicked(View view) {
        MenuProductActivity.this.finish();
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(MenuProductActivity.this, activity);
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
