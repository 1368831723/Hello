package com.pwj.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;
import android.util.SparseIntArray;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;

import com.pwj.activity.RecruitActivity;
import com.pwj.adapter.BaseRcyAdapter;

import com.pwj.bean.Product;

import com.pwj.callBack.SuccessCallBack;

import com.pwj.dialog.DialogChooseMap;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.pages.RecruitViewOne;
import com.pwj.pages.RecruitViewTwo;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.ProductTableName;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.sql.Connection;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by leon on 3/7/18.
 */

public class FragmentRecruit extends BaseFragment implements TextWatcher{
    private List<Product> data = new ArrayList<>();
    private List<Product> data_one = new ArrayList<>();
    private List<Product> data_one_final = new ArrayList<>();
    private List<Product> data_two = new ArrayList<>();
    private List<Product> data_two_final = new ArrayList<>();
    private List<Product> data_final = new ArrayList<>();
    private Activity mActivity;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_search)
    ImageView img_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    @BindView(R.id.tv_no_result)
    TextView tv_no_result;
    @BindView(R.id.tl_title)
    TabLayout mTitle;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    private View[] pages = new View[2];
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    private Unbinder unbinder;
    private String keyWords;
    private String uuid;
    private Connection connection;
    private String page = "";
    private String brand = "";
    private String model = "";
    private String os_version = "";
    private String dates;
    private String locations = "";
    private BaseRcyAdapter adapter;
    private int position;
    private DownImageUtil mDownImageUtil;
    private ProgressDialog myDialog; // 分享进度框
    private String title_pdf;
    private String path;
    private PdfItextUtil pdfItextUtil;
    private String content_search;
    private List<Integer> count_keyWord = new ArrayList<>();
    private SparseIntArray map = new SparseIntArray();
    private int tag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recruit, container, false);
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        et_search.addTextChangedListener(this);
        Keyboard.getInstance().hideKeyBoard(et_search);
        initDate();
        return view;
    }

    private boolean baiDu;
    private boolean gaoDe;
    private DialogChooseMap dialogChooseMap;


    private void initDate() {
        initProgress(mActivity);
        page = "招聘搜索";
        tv_search.setVisibility(View.GONE);
        et_search.setVisibility(View.VISIBLE);
        uuid = LoginInfo.getString(mActivity, "uuid", "");
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
        SearchUtil.getInstance().initLocation(mActivity, new StringCallbackOne() {
            @Override
            public void stringOne(String str , double longitude , double latitude) {
                locations = str;
            }
        });
    }


    private void initView() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                et_search.setText("");
                onRefreshing();
            }
        });
        swipe.setSize(SwipeRefreshLayout.LARGE);
        swipe.setColorSchemeResources(R.color.orange, R.color.blue_bg, R.color.green);
        mTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tag = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void onRefreshing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EasyHttp.post(IpConfig.URL_SQL)
                        .params("query", "SELECT login.url3,login.user_name,login.job," + ProductTableName.require6 + ".* FROM login," + ProductTableName.require6 + " WHERE login.phone = " + ProductTableName.require6 + ".phone ORDER BY id DESC")
                        .timeStamp(true)
                        .execute(new SuccessCallBack<String>() {
                            @Override
                            public void onSuccess(String s) {
                                data.clear();
                                data_final.clear();
                                data.addAll(GsonUtils.getGsonToList(s, Product.class));
                                data_final.addAll(data);
                                adapter.notifyDataSetChanged();
                                swipe.setRefreshing(false);
                                Util.showToast(mActivity, "数据更新完成");
                            }
                        });
            }
        }, 4000);
    }


    private void initData() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name,login.job," + ProductTableName.require6 + ".* FROM login," + ProductTableName.require6 + " WHERE login.phone = " + ProductTableName.require6 + ".phone AND product_name = '招聘需求' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                    @Override
                    public void onSuccess(String s) {
                        progressBar.setVisibility(View.GONE);
                        data_one = GsonUtils.getGsonToList(s, Product.class);
                        data_one_final.addAll(data_one);
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "SELECT login.url3,login.user_name,login.job," + ProductTableName.require6 + ".* FROM login," + ProductTableName.require6 + " WHERE login.phone = " + ProductTableName.require6 + ".phone AND product_name = '应聘需求' ORDER BY id DESC")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                                    @Override
                                    public void onSuccess(String s) {
                                        progressBar.setVisibility(View.GONE);
                                        data_two = GsonUtils.getGsonToList(s, Product.class);
                                        data_two_final.addAll(data_two);
                                        pages[0] = RecruitViewOne.getView(mActivity, mActivity,data_one);
                                        pages[1] = RecruitViewTwo.getView(mActivity, mActivity,data_two);
                                        mViewPager.setAdapter(new PagerAdapter() {
                                            @Override
                                            public int getCount() {
                                                return pages.length;
                                            }

                                            @Override
                                            public boolean isViewFromObject(View view, Object object) {
                                                return view == object;
                                            }

                                            @Override
                                            public Object instantiateItem(ViewGroup container, int position) {
                                                container.addView(pages[position]);
                                                return pages[position];
                                            }

                                            @Override
                                            public void destroyItem(ViewGroup container, int position, Object object) {
                                                container.removeView(pages[position]);
                                            }
                                        });
                                        mTitle.setupWithViewPager(mViewPager);
                                        TabLayout.Tab tab0 = mTitle.getTabAt(0).setText("招聘需求");
                                        if (tab0 == null) {
                                            throw new NullPointerException("tab0");
                                        }
                                        TabLayout.Tab tab1 = mTitle.getTabAt(1).setText("应聘需求");
                                        if (tab1 == null) {
                                            throw new NullPointerException("tab1");
                                        }
                                    }

                                });
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name," + ProductTableName.require6 + ".* FROM login," + ProductTableName.require6 + " WHERE login.phone = " + ProductTableName.require6 + ".phone AND product_name = '招聘需求' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                    @Override
                    public void onSuccess(String s) {
                        progressBar.setVisibility(View.GONE);
                        data_one.clear();
                        data_one_final.clear();
                        data_one.addAll(GsonUtils.getGsonToList(s, Product.class));
                        data_one_final.addAll(data_one);
                        RecruitViewOne.refresh();
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "SELECT login.url3,login.user_name," + ProductTableName.require6 + ".* FROM login," + ProductTableName.require6 + " WHERE login.phone = " + ProductTableName.require6 + ".phone AND product_name = '应聘需求' ORDER BY id DESC")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                                    @Override
                                    public void onSuccess(String s) {
                                        data_two.clear();
                                        data_two_final.clear();
                                        data_two.addAll(GsonUtils.getGsonToList(s, Product.class));
                                        data_two_final.addAll(data_two);
                                        RecruitViewTwo.refresh();
                                    }

                                });
                    }
                });
    }

    @OnClick({R.id.img_issue, R.id.img_search})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_issue:
                if (checkPhone(mActivity)) {
                    Intent intent = new Intent(mActivity,RecruitActivity.class);
                    startActivityForResult(intent,15);
//                    startActivity(RecruitActivity.class);
                }
                break;
            case R.id.img_search:
                tv_no_result.setVisibility(View.GONE);
                keyWords = et_search.getText().toString().trim();
                if (keyWords.length() > 0) {
                    insertKeyWords(keyWords,"招聘搜索");
                    List<String> words = Arrays.asList(keyWords.split("\\s+"));
                    count_keyWord.clear();
                    data.clear();
                    map.clear();
                    switch (tag){
                        case 0:
                            data_one.clear();
                            for (int i = 0; i < data_one_final.size(); i++) {
                                map.put(i, 0);
                                content_search = data_one_final.get(i).getUser_name() + data_one_final.get(i).getContent();
                                for (int j = 0; j < words.size(); j++) {
                                    if (content_search.contains(words.get(j))) {
                                        map.put(i, map.get(i) + 1);
                                    }
                                }
                            }
                            for (int i = 0; i < map.size(); i++) {

                                if (map.get(i) != 0) {
                                    count_keyWord.add(map.get(i));
                                    data_one.add(data_one_final.get(i));
                                }
                            }
                            if (count_keyWord.size() < 2) {
                                if (count_keyWord.size() > 0) {
                                    RecruitViewOne.refresh();
                                } else {
                                    tv_no_result.setVisibility(View.VISIBLE);
                                }
                            } else {
                                for (int i = 0; i < count_keyWord.size() - 1; i++) {
                                    for (int j = 0; j < count_keyWord.size() - 1 - i; j++) {
                                        if (count_keyWord.get(j) < count_keyWord.get(j + 1)) {
                                            data_one.add(j, data_one.get(j + 1));
                                            count_keyWord.add(j, count_keyWord.get(j + 1));
                                            data_one.remove((j + 2));
                                            count_keyWord.remove((j + 2));
                                        }
                                    }
                                    if (i == count_keyWord.size() - 2) {
                                        RecruitViewOne.refresh();
                                    }
                                }
                            }
                            break;
                        case 1:
                            data_two.clear();
                            for (int i = 0; i < data_two_final.size(); i++) {
                                map.put(i, 0);
                                content_search = data_two_final.get(i).getUser_name() + data_two_final.get(i).getContent();
                                for (int j = 0; j < words.size(); j++) {
                                    if (content_search.contains(words.get(j))) {
                                        map.put(i, map.get(i) + 1);
                                    }
                                }
                            }
                            for (int i = 0; i < map.size(); i++) {

                                if (map.get(i) != 0) {
                                    count_keyWord.add(map.get(i));
                                    data_two.add(data_two_final.get(i));
                                }
                            }
                            if (count_keyWord.size() < 2) {
                                if (count_keyWord.size() > 0) {
                                    RecruitViewTwo.refresh();
                                } else {
                                    tv_no_result.setVisibility(View.VISIBLE);
                                }
                            } else {
                                for (int i = 0; i < count_keyWord.size() - 1; i++) {
                                    for (int j = 0; j < count_keyWord.size() - 1 - i; j++) {
                                        if (count_keyWord.get(j) < count_keyWord.get(j + 1)) {
                                            data_two.add(j, data_two.get(j + 1));
                                            count_keyWord.add(j, count_keyWord.get(j + 1));
                                            data_two.remove((j + 2));
                                            count_keyWord.remove((j + 2));
                                        }
                                    }
                                    if (i == count_keyWord.size() - 2) {
                                        RecruitViewTwo.refresh();
                                    }
                                }
                            }
                            break;
                    }
                } else {
                    Util.showToast(mActivity, "请输入关键词");
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable str) {
        if (str.toString().length() > 0) {
        } else {
            tv_no_result.setVisibility(View.GONE);
//            data.clear();
//            data.addAll(data_final);
            data_one.clear();
            data_two.clear();
            data_one.addAll(data_one_final);
            data_two.addAll(data_two_final);
            RecruitViewOne.refresh();
            RecruitViewTwo.refresh();
        }
    }

    protected void insertSearch() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });

    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(mActivity, activity);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FragmentRecruit");
        MobclickAgent.onResume(mActivity); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentRecruit");
        MobclickAgent.onPause(mActivity); //统计时长
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
