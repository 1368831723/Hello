package com.pwj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Item_purchase;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class SelectLoveActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.tv)
    TextView tv;
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
    private String phone = "";
    private String love  = "";
    private String love_type = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_love);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(tv)  //可以为任意view
                .statusBarColor(R.color.white)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        initData();
    }

    private void initData() {
        phone = getIntent().getStringExtra("phone");
        for (int i = 0; i < title.length; i++) {
            Item_purchase item_purchase = new Item_purchase(title[i],0);
            data.add(item_purchase);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recycle.setLayoutManager(gridLayoutManager);
        adapter = new BaseRcyAdapter(data, R.layout.item_select_love_tv) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                TextView tv_title = holder.getView(R.id.tv_title);
                tv_title.setText(data.get(position).getTitle());
                tv_title.setOnClickListener(SelectLoveActivity.this);
                tv_title.setTag(position);
                if (data.get(position).getLove_tag()==0){
                    tv_title.setBackground(getResources().getDrawable(R.drawable.shape_select_love_tv_normal));
                }else {
                    tv_title.setBackground(getResources().getDrawable(R.drawable.shape_select_love_tv_pressed));
                }
            }
        };

        recycle.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_title:
                int position = (int) view.getTag();
                if (data.get(position).getLove_tag()==0){
                    data.get(position).setLove_tag(1);
                }else {
                    data.get(position).setLove_tag(0);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @OnClick(R.id.define)
    public void onViewClicked(View view) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getLove_tag()==1){
                love = love+title[i]+",";
                love_type = love_type+String.valueOf(i)+",";
            }
        }
        if (love.length()>1){
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "update login set love = '" + love + "', love_type = '" + love_type + "'where phone = " + phone + "")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            super.onSuccess(s);
                            startActivity(new Intent(SelectLoveActivity.this, MainActivity.class));
                            SelectLoveActivity.this.finish();
                        }
                    });
        }else{
            startActivity(new Intent(SelectLoveActivity.this, MainActivity.class));
            SelectLoveActivity.this.finish();
        }

    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(SelectLoveActivity.this, activity);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
}
