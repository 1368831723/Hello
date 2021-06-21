package com.pwj.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.gyf.immersionbar.ImmersionBar;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddressActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.title_fm)
    FrameLayout title_fm;
    @BindView(R.id.address_add)
    TextView address_add;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    private BaseRcyAdapter baseRcyAdapter;
    private List<Product> data = new ArrayList();
    private boolean flag = false;//如果是其他页面点进来，就可以选择地址
    private String phone;
    private String location = "";
    private String province;
    private String city;
    private String county;
    private String street = "";
    private String specific = "";
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }


    private void initData() {
        phone = LoginInfo.getString(this, "phone", "");
        flag = getIntent().getBooleanExtra("flag", false);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_fm)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycle.setLayoutManager(linearLayoutManager);
    }

    private void initAdapter() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM address WHERE phone = " + phone + " ORDER BY number DESC")
                .timeStamp(true)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data.addAll(GsonUtils.getGsonToList(s, Product.class));
                        if (baseRcyAdapter != null) {
                            baseRcyAdapter.notifyDataSetChanged();
                        } else {
                            baseRcyAdapter = new BaseRcyAdapter(data, R.layout.item_address) {
                                @Override
                                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                                    LinearLayout linearLayout = holder.getView(R.id.linear);
                                    TextView tv_contact = holder.getView(R.id.tv_contact);
                                    TextView tv_phone_address = holder.getView(R.id.tv_phone_address);
                                    TextView tv_content = holder.getView(R.id.tv_content);
                                    TextView tv_edit = holder.getView(R.id.tv_edit);
                                    TextView tv_default = holder.getView(R.id.tv_default);
                                    if (position == 0 && data.get(0).getNumber() == 1) {
                                        tv_default.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_default.setVisibility(View.GONE);
                                    }
                                    tv_contact.setText(data.get(position).getContact());
                                    tv_phone_address.setText(data.get(position).getPhone_address());
                                    tv_content.setText(data.get(position).getLocation());

                                    linearLayout.setOnClickListener(AddressActivity.this);
                                    linearLayout.setTag(position);
                                    tv_content.setOnClickListener(AddressActivity.this);
                                    tv_content.setTag(position);
                                    tv_edit.setOnClickListener(AddressActivity.this);
                                    tv_edit.setTag(position);
                                }
                            };
                            recycle.setAdapter(baseRcyAdapter);
                        }
                    }
                });
    }

    @OnClick({R.id.title_im, R.id.address_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                if (data.size() > 0 && data.get(0).getNumber() == 0) {
                    Util.showToast(AddressActivity.this, "你还没有设置默认地址哦");
                }
                finish();
                break;
            case R.id.address_add:
                Intent intent = new Intent(AddressActivity.this, AddressAddActivity.class);
                intent.putExtra("flag", false);
                startActivityForResult(intent, 10);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()) {
            case R.id.tv_edit:
                intent = new Intent(AddressActivity.this, AddressAddActivity.class);
                intent.putExtra("flag", true);
                intent.putExtra("id", data.get(position).getId());
                intent.putExtra("contact", data.get(position).getContact());
                intent.putExtra("phone_address", data.get(position).getPhone_address());
                intent.putExtra("location", data.get(position).getLocation());
                intent.putExtra("province", data.get(position).getProvince());
                intent.putExtra("city", data.get(position).getCity());
                intent.putExtra("county", data.get(position).getCounty());
                intent.putExtra("street", data.get(position).getStreet());
                intent.putExtra("longitude", data.get(position).getLongitude());
                intent.putExtra("latitude", data.get(position).getLatitude());
                intent.putExtra("specific", data.get(position).getSpecific());
                intent.putExtra("number", data.get(position).getNumber());
                startActivityForResult(intent, 10);

                break;
            case R.id.linear:
                start(position);
                break;
            case R.id.tv_content:
                start(position);
                break;
        }
    }

    private void start(int position) {
        if (flag) {
            intent = new Intent();
            intent.putExtra("contact", data.get(position).getContact());
            intent.putExtra("phone_address", data.get(position).getPhone_address());
            intent.putExtra("location", data.get(position).getLocation());
            intent.putExtra("province", data.get(position).getProvince());
            intent.putExtra("city", data.get(position).getCity());
            intent.putExtra("county", data.get(position).getCounty());
            intent.putExtra("street", data.get(position).getStreet());
            intent.putExtra("specific", data.get(position).getSpecific());
            intent.putExtra("longitude", data.get(position).getLongitude());
            intent.putExtra("latitude", data.get(position).getLatitude());
            setResult(10, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 10) {
            initAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (data.size() > 0 && data.get(0).getNumber() == 0) {
            Util.showToast(AddressActivity.this, "你还没有设置默认地址哦");
        }
        AddressActivity.this.finish();
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
