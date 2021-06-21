package com.pwj.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.BiddingSituation;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchUserActivity extends AppCompatActivity {

    @BindView(R.id.title_linear)
    RelativeLayout title_linear;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_no_user)
    TextView tv_no_user;
    List<Product> data = new ArrayList<>();
    private BaseRcyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchUserActivity.this);
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new BaseRcyAdapter(data, R.layout.item_chat_message) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                CircleImageView imageView = holder.getView(R.id.img);
                TextView name_tv = holder.getView(R.id.name_tv);
                TextView count_tv = holder.getView(R.id.count_tv);
                Glide.with(SearchUserActivity.this).load(data.get(position).getUrl3()).into(imageView);
                name_tv.setText(data.get(position).getUser_name());
                count_tv.setVisibility(View.GONE);
            }
        };
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SearchUserActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
            }
        });
        recycler.setAdapter(adapter);
    }

    private void initData() {
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();

    }

    @OnClick({R.id.title_im, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.tv_search:
                String phone = et_search.getText().toString().trim();
                if ("".equals(phone)) {
                    Util.showToast(SearchUserActivity.this, "手机号不能为空");
                } else {
                    data.clear();
                    recycler.setVisibility(View.VISIBLE);
                    tv_no_user.setVisibility(View.GONE);
                    EasyHttp.post(IpConfig.URL_SQL)
                            .params("query", "SELECT username FROM ofuser WHERE username LIKE '%" + phone + "%'")
                            .timeStamp(true)
                            .execute(new SuccessCallBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    List<UserMessage> data_users = GsonUtils.getGsonToList(s, UserMessage.class);
                                    if (data_users.size() == 0) {
                                        adapter.notifyDataSetChanged();
                                        recycler.setVisibility(View.GONE);
                                        tv_no_user.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                    for (int i = 0; i < data_users.size(); i++) {
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "SELECT url3,user_name,phone FROM login WHERE phone = " + data_users.get(i).getUsername() + "")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        List<Product> data_user = GsonUtils.getGsonToList(s, Product.class);
                                                        if (data_user != null && data_user.size() > 0) {
                                                            data.addAll(data_user);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
                break;
        }
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
