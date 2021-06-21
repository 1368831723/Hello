package com.pwj.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.pwj.adapter.DetailAdapter;

import com.pwj.bean.Customer;
import com.pwj.bean.WordModel;
import com.pwj.helloya.InfoPathActivity;
import com.pwj.helloya.R;
import com.pwj.helloya.databinding.ActivityCityDetailListBinding;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.jdbc.Insert;
import com.pwj.jdbc.Jdbc;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SearchUtil;
import com.umeng.analytics.MobclickAgent;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CityDetailList extends AppCompatActivity implements TextWatcher, SortedListAdapter.Callback {
    private static final Comparator<WordModel> COMPARATOR = new SortedListAdapter.ComparatorBuilder<WordModel>()
            .setOrderForModel(WordModel.class, (a, b) -> Integer.signum(a.getRank() - b.getRank()))
            .build();

    private DetailAdapter mAdapter;
    private ActivityCityDetailListBinding mBinding;
    private Animator mAnimator;
    private List<WordModel> mModels;
    private ArrayList<Customer> customerList;
    @BindView(R.id.title_im)
    ImageView title_im;
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    private String keyWords;
    private String uuid;
    private Connection connection;
    private String page = "";
    private String brand = "";
    private String model = "";
    private String os_version = "";
    private String dates;
    private String locations = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_city_detail_list);
        ButterKnife.bind(this);
        title_im.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        tv_search.setVisibility(View.GONE);
        img_issue.setVisibility(View.GONE);
        et_search.addTextChangedListener(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        mAdapter = new DetailAdapter(this, COMPARATOR, model -> {
            int type = getIntent().getIntExtra("type", 1);
            if (type == 2) {
                String longitude = customerList.get(model.getRank()).getLon();
                String latitude = customerList.get(model.getRank()).getLat();
                LoginInfo.setString(this, "com_longitude", longitude);
                LoginInfo.setString(this, "com_latitude", latitude);
            }
            final String message = getString(R.string.model_clicked_pattern, model.getRank(), model.getWord());
            //点击后的操作在这里写
            Intent intent = new Intent(CityDetailList.this, InfoPathActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer_info", customerList.get(model.getRank()));
            intent.putExtras(bundle);
            startActivity(intent);
        });

        mAdapter.addCallback(this);
        initData();
        mBinding.cityRecycle.setLayoutManager(new LinearLayoutManager(this));
        mBinding.cityRecycle.setAdapter(mAdapter);
        initDate();
    }

    private void initData() {
        mModels = new ArrayList<>();
        customerList = (ArrayList<Customer>) getIntent().getSerializableExtra("resultlist");
        for (int i = 0; i < customerList.size(); i++) {
            mModels.add(new WordModel(1, i, customerList.get(i).getCompanyname()));
        }
        mAdapter.edit()
                .replaceAll(mModels)
                .commit();
    }


    private static List<WordModel> filter(List<WordModel> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<WordModel> filteredModelList = new ArrayList<>();
        for (WordModel model : models) {
            final String text = model.getWord().toLowerCase();  //name的的过滤规则
            final String rank = String.valueOf(model.getRank());//id的过滤规则
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void initDate() {
        page = "客户搜索";
        dates = SearchUtil.getInstance().getNowDate();
        brand = SearchUtil.getInstance().getSystemBrand();
        model = SearchUtil.getInstance().getSystemModel();
        os_version = SearchUtil.getInstance().getSystemVersion();
        initLocations();
    }
    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void initLocations() {
        SearchUtil.getInstance().initLocation(CityDetailList.this, new StringCallbackOne() {
            @Override
            public void stringOne(String str ,double longitude ,double latitude) {
                locations= str;
            }
        });
    }

    @OnClick({R.id.title_im, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.title_im:
                finish();
                break;
            case R.id.img_search:
                keyWords = et_search.getText().toString().trim();
                final List<WordModel> filteredModelList = filter(mModels,keyWords);
                mAdapter.edit()
                        .replaceAll(filteredModelList)
                        .commit();
                uuid = LoginInfo.getString(this,"uuid","");
                if("".equals(keyWords)){
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connection = Jdbc.getConnection("root", "1q23lyc45j");
                            Insert.insert_keyWords(connection,uuid,brand,model,os_version,dates,page,keyWords,locations);
                        }
                    }).start();
                }
                break;
        }

    }

    @Override
    public void onEditStarted() {
        if (mBinding.cityProgressBar.getVisibility() != View.VISIBLE) {
            mBinding.cityProgressBar.setVisibility(View.VISIBLE);
            mBinding.cityProgressBar.setAlpha(0.0f);
        }

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(mBinding.cityProgressBar, View.ALPHA, 1.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.start();

        mBinding.cityRecycle.animate().alpha(0.5f);

    }

    @Override
    public void onEditFinished() {
        mBinding.cityRecycle.scrollToPosition(0);
        mBinding.cityRecycle.animate().alpha(1.0f);

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(mBinding.cityProgressBar, View.ALPHA, 0.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {

            private boolean mCanceled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!mCanceled) {
                    mBinding.cityProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mAnimator.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ("".equals(s.toString())) {  //如果输入框为空，返回原有数据
            final List<WordModel> filteredModelList = filter(mModels, "");
            mAdapter.edit()
                    .replaceAll(filteredModelList)
                    .commit();
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
