package com.pwj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Share;
import com.pwj.helloya.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class ShareActivity extends Activity{

    @BindView(R.id.share_recycle)
    RecyclerView recycle;
    private BaseRcyAdapter adapter;
    private List<Share> data = new ArrayList<>();
    private int[] img = {R.drawable.wechat, R.drawable.wechatmoments, R.drawable.qq1, R.drawable.sinaweibo};
    private String[] name = {"微信", "朋友圈", "QQ", "新浪"};
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        WindowManager windowManager = this.getWindowManager();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        Display display = windowManager.getDefaultDisplay();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.gravity = Gravity.BOTTOM;
        this.getWindow().setAttributes(lp);
        initData();
    }

    private void initData() {
        intent = new Intent();
        for (int i = 0; i < img.length; i++) {
            Share share = new Share(img[i], name[i]);
            data.add(share);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycle.setLayoutManager(linearLayoutManager);
        adapter = new BaseRcyAdapter(data, R.layout.item_share) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                ImageView imageView = holder.getView(R.id.img);
                TextView tv = holder.getView(R.id.tv);
                imageView.setImageResource(data.get(position).getImg());
                tv.setText(data.get(position).getName());
            }
        };
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                intent.putExtra("share",position+1);
                setResult(12,intent);
                ShareActivity.this.finish();
            }
        });
        recycle.setAdapter(adapter);
    }

    @OnClick(R.id.share_tv)
    public void onViewClicked(View view) {
        ShareActivity.this.finish();
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
