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
import com.pwj.adapter.SearchAdapter;
import com.pwj.bean.Bidding;

import com.pwj.bean.BiddingSituation;
import com.pwj.bean.WordModel;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;

import com.pwj.helloya.databinding.ActivitySearchBinding;
import com.pwj.interfaces.BiddingCallback;
import com.pwj.interfaces.BiddingCurrentCallback;
import com.pwj.interfaces.QueryRunnable;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.jdbc.Insert;
import com.pwj.jdbc.Jdbc;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements TextWatcher, SortedListAdapter.Callback{
    private static final Comparator<WordModel> COMPARATOR = new SortedListAdapter.ComparatorBuilder<WordModel>()
            .setOrderForModel(WordModel.class, (a, b) -> Integer.signum(a.getRank() - b.getRank()))
            .build();

    private SearchAdapter mAdapter;
    private ActivitySearchBinding mBinding;
    private Animator mAnimator;
    private List<WordModel> mModels;
    private List<BiddingSituation> data = new ArrayList<>();
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
//    private ImageView title_im;
//    private EditText et_search;
//    private TextView tv_search;

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        View view = mBinding.getRoot();
        ButterKnife.bind(this,view);
//        title_im = view.findViewById(R.id.title_im);
//        et_search = view.findViewById(R.id.et_search);
//        tv_search = view.findViewById(R.id.tv_search);
        title_im.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        tv_search.setVisibility(View.GONE);
        img_issue.setVisibility(View.GONE);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
//        title_im.setOnClickListener(this);
//        tv_search.setOnClickListener(this);
        mAdapter = new SearchAdapter(this, COMPARATOR, model -> {

            final String message = getString(R.string.model_clicked_pattern, model.getRank(), model.getWord());
            //点击后的操作在这里写
            Intent intent = new Intent(SearchActivity.this, CompanyActivity.class);
            intent.putExtra("company", data.get(model.getRank()).getCompany());
            startActivity(intent);

        });

        mAdapter.addCallback(this);
        initData();
        mBinding.searchRecycle.setLayoutManager(new LinearLayoutManager(this));
        mBinding.searchRecycle.setAdapter(mAdapter);
        et_search.addTextChangedListener(this);
        initDate();
    }

    private void initData() {
        mModels = new ArrayList<>();
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT company,longitude,latitude,content,COUNT(*) AS COUNT FROM bid_pwj GROUP BY company HAVING COUNT>0 ORDER BY COUNT DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data = GsonUtils.getGsonToList(s, BiddingSituation.class);
                        for (int i = 0; i < data.size(); i++) {
                            mModels.add(new WordModel(1, i, data.get(i).getCompany(), data.get(i).getCOUNT(), data.get(i).getContent()));
                        }
                        mAdapter.edit()
                                .replaceAll(mModels)
                                .commit();
                    }
                });

    }

    private void initDate() {
        page = "招标搜索";
        dates = SearchUtil.getInstance().getNowDate();
        brand = SearchUtil.getInstance().getSystemBrand();
        model = SearchUtil.getInstance().getSystemModel();
        os_version = SearchUtil.getInstance().getSystemVersion();
        initLocations();
//        locations = initLocations();
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void initLocations() {
        SearchUtil.getInstance().initLocation(SearchActivity.this, new StringCallbackOne() {
            @Override
            public void stringOne(String str, double longitude, double latitude) {
                locations = str;
            }
        });
    }

    private static List<WordModel> filter(List<WordModel> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<WordModel> filteredModelList = new ArrayList<>();
        for (WordModel model : models) {
            if (model.getWord() != null) {
                final String text = model.getWord().toLowerCase();  //name的的过滤规则
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            } else {

            }
            if (model.getContent() != null) {
                final String content = model.getContent().toLowerCase();//id的过滤规则
                if (content.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            } else {
            }
            final String rank = String.valueOf(model.getRank());//id的过滤规则
        }
        return filteredModelList;
    }


    @Override
    public void onEditStarted() {
        if (mBinding.searchProgressBar.getVisibility() != View.VISIBLE) {
            mBinding.searchProgressBar.setVisibility(View.VISIBLE);
            mBinding.searchProgressBar.setAlpha(0.0f);
        }

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(mBinding.searchProgressBar, View.ALPHA, 1.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.start();

        mBinding.searchRecycle.animate().alpha(0.5f);

    }

    @Override
    public void onEditFinished() {
        mBinding.searchRecycle.scrollToPosition(0);
        mBinding.searchRecycle.animate().alpha(1.0f);

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(mBinding.searchProgressBar, View.ALPHA, 0.0f);
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
                    mBinding.searchProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mAnimator.start();
    }

    @OnClick({R.id.title_im, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_search:
                keyWords = et_search.getText().toString().trim();
                final List<WordModel> filteredModelList = filter(mModels, keyWords);
                mAdapter.edit()
                        .replaceAll(filteredModelList)
                        .commit();
                uuid = LoginInfo.getString(this, "uuid", "");
                if ("".equals(keyWords)) {
                    Util.showToast(SearchActivity.this, "请输入关键词");
                } else {

                    EasyHttp.post(IpConfig.URL_SQL)
                            .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
                            .timeStamp(true)
                            .execute(new SuccessCallBack<String>() {
                                @Override
                                public void onSuccess(String s) {

                                }
                            });
                }
                break;
        }
    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.title_im:
//                finish();
//                break;
//            case R.id.img_search:
//                keyWords = et_search.getText().toString().trim();
//                final List<WordModel> filteredModelList = filter(mModels, keyWords);
//                mAdapter.edit()
//                        .replaceAll(filteredModelList)
//                        .commit();
//                uuid = LoginInfo.getString(this, "uuid", "");
//                if ("".equals(keyWords)) {
//
//                } else {
//                    EasyHttp.post(IpConfig.URL_SQL)
//                            .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
//                            .timeStamp(true)
//                            .execute(new SuccessCallBack<String>() {
//                                @Override
//                                public void onSuccess(String s) {
//
//                                }
//                            });
//                }
//                break;
//        }
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ("".equals(s.toString())) {
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
