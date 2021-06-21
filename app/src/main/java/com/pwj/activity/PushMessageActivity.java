package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.base.BaseActivityComment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.classes.CircleImageView;

import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;

import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PushMessageActivity extends BaseActivityComment implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    RelativeLayout title_linear;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    private List<Product> data = new ArrayList<>();
    private BaseRcyAdapter adapter;
    private String title = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }

    private void initData() {
        initProgress(this);
        phone = LoginInfo.getString(this, "phone", "");
        title_tv.setText(getResources().getString(R.string.me_push));
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    private void initAdapter() {
        recycle.setNestedScrollingEnabled(false);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name,push_background.* FROM login,push_background WHERE push_background.phone = login.phone ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data = GsonUtils.getGsonToList(json, Product.class);
                        if (data.size() == 0) {
                            return;
                        }
                        adapter = new BaseRcyAdapter(data, R.layout.item_push_message) {
                            @Override
                            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                                CircleImageView img = holder.getView(R.id.img);
                                TextView tv_product_name = holder.getView(R.id.tv_product_name);
                                TextView tv_name = holder.getView(R.id.tv_name);
                                TextView tv_time = holder.getView(R.id.tv_time);
                                TextView tv_content = holder.getView(R.id.tv_content);
//                                AssNineGridView gridView = holder.getView(R.id.gridView);

                                Glide.with(PushMessageActivity.this).load(data.get(position).getUrl3()).into(img);
                                tv_product_name.setText(data.get(position).getProduct_name());
                                tv_name.setText(data.get(position).getUser_name());
                                tv_time.setText(data.get(position).getDate());
                                tv_content.setText(data.get(position).getContent());
                                img.setOnClickListener(PushMessageActivity.this);
                                img.setTag(position);
                            }
                        };
                        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
//                                int id = data.get(position).getId();
//                                String table_name = data.get(position).getTable_name();
//                                String url3 = data.get(position).getUrl3();
//                                String user_name = data.get(position).getUser_name();
//                                String other_phone = data.get(position).getPhone();
                                title = data.get(position).getProduct_name();
                                String contents = data.get(position).getContent();
                                intent = new Intent(PushMessageActivity.this, PushBackgroundActivity.class);
//                                intent.putExtra("table_id", id);
//                                intent.putExtra("table_name", table_name);
//                                intent.putExtra("user_img", url3);
//                                intent.putExtra("user_name", user_name);
//                                intent.putExtra("other_phone", other_phone);
                                intent.putExtra("title", title);
                                intent.putExtra("content", contents);
                                startActivity(intent);
                            }
                        });
                        recycle.setLayoutManager(new LinearLayoutManager(PushMessageActivity.this));
                        recycle.setAdapter(adapter);

                    }
                });
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(PushMessageActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;

        }

    }
}
