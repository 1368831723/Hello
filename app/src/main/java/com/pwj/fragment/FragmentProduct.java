package com.pwj.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import com.luck.picture.lib.tools.DoubleUtils;
import com.pwj.activity.FormActivity;
import com.pwj.activity.IssueProductOffice;
import com.pwj.activity.IssueProductType1;
import com.pwj.activity.IssueProductType2;
import com.pwj.activity.IssueProductType3;
import com.pwj.activity.IssueProductType4;
import com.pwj.activity.IssueProductType5;

import com.pwj.activity.IssueProductTypeOthers;
import com.pwj.activity.MenuProductActivity;
import com.pwj.activity.PictureVideoPlayActivity;
import com.pwj.activity.ProductDetailActivity;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Comment;
import com.pwj.bean.Item_purchase;
import com.pwj.bean.Product;

import com.pwj.callBack.SuccessCallBack;

import com.pwj.chat.OtherUserActivity;
import com.pwj.classes.CenterLayoutManager;

import com.pwj.classes.LoadingCircleView;
import com.pwj.dialog.DialogForm;
import com.pwj.helloya.R;
import com.pwj.utils.ClickTimeUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;

import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.ufreedom.uikit.FloatingText;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


import q.rorbin.badgeview.Badge;


import static android.content.ContentValues.TAG;


/**
 * Created by leon on 3/7/18.
 */

public class FragmentProduct extends BaseFragment implements View.OnClickListener, TextWatcher {
    private Unbinder unbinder;
    private Activity mActivity;
    //    @BindView(R.id.tv_search)
//    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_no_result)
    TextView tv_no_result;
    @BindView(R.id.recycle1)
    RecyclerView recycle1;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rgp)
    RadioGroup rgp;
    @BindView(R.id.rbn1)
    RadioButton rbn1;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recycle_product)
    RecyclerView recycle_product;
    private MyAdapter myAdapter;
    private DialogForm dialogForm;
    private String[] title = {
            "二手抛丸机", "办事处", "履带式抛丸机", "吊钩式抛丸机",
            "转台式抛丸机", "网带通过式抛丸机", "履带通过式抛丸机", "滚道通过式抛丸机",
            "悬链通过式抛丸机", "环保除尘设备", "喷砂机", "碾砂机",
            "空压机", "电机", "耐磨铸件", "合金钢丸",
            "不锈钢丸", "钢砂", "密封材料", "橡胶件",
            "气动元件", "电器", "方头螺丝", "叶轮",
            "铁水包", "链条", "出租厂房", "物流提供",
            "轴承", "钢材", "其他二手", "其他"};
    private String[] table_product = new String[]{
            ProductTableName.product1, ProductTableName.product30, ProductTableName.product2, ProductTableName.product3,
            ProductTableName.product4, ProductTableName.product5, ProductTableName.product6, ProductTableName.product7,
            ProductTableName.product8, ProductTableName.product9, ProductTableName.product10, ProductTableName.product11,
            ProductTableName.product31, ProductTableName.product12, ProductTableName.product13, ProductTableName.product14,
            ProductTableName.product15, ProductTableName.product16, ProductTableName.product17, ProductTableName.product18,
            ProductTableName.product19, ProductTableName.product20, ProductTableName.product21, ProductTableName.product22,
            ProductTableName.product23, ProductTableName.product24, ProductTableName.product25, ProductTableName.product26,
            ProductTableName.product27, ProductTableName.product28, ProductTableName.product29, ProductTableName.product0};

    private List<Item_purchase> data1;
    private List<Product> data = new ArrayList<>();
    private List<Comment> data_comment = new ArrayList<>();
    private TextView tv_title;
    private int tag = 0;
    private BaseRcyAdapter baseRcyAdapter;
    private Intent intent;
    private final static int position_all = 5 << 24;
    private final static int btn_paly = 6 << 24;
    private final static int circleBar = 7 << 24;
    private final static int content = 8 << 24;
    private final static int count_comment = 9 << 24;
    private final static int view_img_like = 10 << 24;
    private final static int view_tv_like = 11 << 24;
    private int flag_like = 0;
    private String phone;
    private int key;
    private CenterLayoutManager centerLayoutManager;
    private boolean status = false;
    private List<Product> data_final;
    private int count; //下载视频次数
    private String content_search;
    private SparseIntArray map = new SparseIntArray();
    private List<Integer> counts = new ArrayList<>();
    private String sql;
//    private SurfaceVideoViewCreator surfaceVideoViewCreator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        initProgress(mActivity);
    }


    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(mActivity, activity);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRadioGroup();
        initRefreshLayout();
        initData1();
        initData2();
        return view;
    }

    private void initRadioGroup() {
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                progressBar.setVisibility(View.VISIBLE);
                if (myAdapter != null) {
                    switch (id) {
                        case R.id.rbn1:
                            String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC";
                            queryProduct(tag, sql);
                            break;
                        case R.id.rbn2:
                            String sql2 = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY price+0 ASC";
                            queryProduct(tag, sql2);
                            break;
                        case R.id.rbn3:
                            progressBar.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    Util.showToast(mActivity, "请稍后，数据加载中...");
                }
            }
        });
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                et_search.setText("");
                String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10";
                queryProduct(tag, sql);
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                String keyWords = et_search.getText().toString().trim();
                if (keyWords.length() > 0) {
                    int len = data.size() + 10;
                    getSearchResult(keyWords, len);
                } else {
                    String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0," + data_final.size() + 10 + "";
                    queryProduct(tag, sql);
                }
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

//    @Override
//    public void onLazyAfterView() {
//        super.onLazyAfterView();
//        Log.e(TAG, "onLazyAfterView:再次执行" );
//        if (status) {
//            initData2();
//            status = false;
//        }
//    }

//    @Override
//    public void onVisible() {
//        super.onVisible();
//        if (status) {
//            initData2();
//            status = false;
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        status = false;
        unbinder.unbind();
    }


    private void initData1() {
        et_search.addTextChangedListener(this);
        Keyboard.getInstance().hideKeyBoard(et_search);
//        tv_search.setVisibility(View.GONE);
        et_search.setVisibility(View.VISIBLE);
        data1 = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Item_purchase item_purchase = new Item_purchase(title[i]);
            data1.add(item_purchase);
        }
        centerLayoutManager = new CenterLayoutManager(mActivity, CenterLayoutManager.HORIZONTAL, false);
        recycle1.setLayoutManager(centerLayoutManager);
        baseRcyAdapter = new BaseRcyAdapter(data1, R.layout.item_frg_purchase) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                tv_title = holder.getView(R.id.tv_title);
                tv_title.setText(data1.get(position).getTitle());
                if (position == tag) {
                    tv_title.setTextColor(getResources().getColor(R.color.red_all));
                } else {
                    tv_title.setTextColor(getResources().getColor(R.color.deep_black));
                }
            }
        };
        baseRcyAdapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ClickTimeUtil.isFastClick()) {
                    tv_no_result.setVisibility(View.GONE);
                    if (myAdapter != null) {
                        try {
                            centerLayoutManager.smoothScrollToPosition(recycle1, new RecyclerView.State(), position);
                        } catch (Exception e) {

                        }
                        tag = position;
                        baseRcyAdapter.notifyDataSetChanged();
                        rbn1.setChecked(true);
                        progressBar.setVisibility(View.VISIBLE);
                        String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10";
                        Log.e(TAG, "onItemClick: " + sql);
                        status = false;
                        queryProduct(position, sql);
                    } else {
                        Util.showToast(mActivity, "请稍后操作，数据加载中...");
                    }
                } else {
                    Util.showToast(mActivity, "请不要点击过快");
                }
            }
        });
        recycle1.setAdapter(baseRcyAdapter);
    }

    private void initData2() {
        data = new ArrayList<>();
        data_final = new ArrayList<>();
        data_comment = new ArrayList<>();
        key = 0;
        //改动
        recycle_product.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        myAdapter = new MyAdapter();
        recycle_product.setAdapter(myAdapter);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data = GsonUtils.getGsonToList(s, Product.class);
                        data_final.addAll(data);
                        Log.e(TAG, "onSuccess: " + data.size());
                        if ("".equals(phone)) {
//                            recycle2.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//                            myAdapter = new MyAdapter();
//                            recycle2.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_product[0] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment.clear();
                                            data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                            if (data_comment.size() > 0) {
                                                if (data.size() > key) {
                                                    data.get(key).setLike_person(1);
                                                }
                                            } else {
                                                if (data.size() > key) {
                                                    data.get(key).setLike_person(0);
                                                }
                                            }
                                            key = key + 1;
                                            if (key == data.size()) {
                                                try {
//                                                    recycle2.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//                                                    myAdapter = new MyAdapter();
//                                                    recycle2.setAdapter(myAdapter);
                                                    myAdapter.notifyDataSetChanged();
                                                    progressBar.setVisibility(View.GONE);
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void queryProduct(int position, String sql) {
        data_final = new ArrayList<>();     //新加
        key = 0;
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(mActivity, null) {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data.addAll(GsonUtils.getGsonToList(s, Product.class));
                        data_final.clear();
                        data_final.addAll(data);
                        if (data.size() == 0) {
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                            if (tv_no_result!=null){
                                tv_no_result.setVisibility(View.VISIBLE);
                            }
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            return;
                        }else {
                            if (tv_no_result!=null){
                                tv_no_result.setVisibility(View.GONE);
                            }
                        }
                        if ("".equals(phone)) {
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            return;
                        }
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_product[position] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment.clear();
                                            data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                            if (data_comment.size() > 0) {
                                                if (data.size() > key) {
                                                    data.get(key).setLike_person(1);
                                                }
                                            } else {
                                                if (data.size() > key) {
                                                    data.get(key).setLike_person(0);
                                                }
                                            }
                                            key = key + 1;
                                            if (key == data.size()) {
                                                if (myAdapter != null) {
                                                    myAdapter.notifyDataSetChanged();
                                                }
                                                if (progressBar != null) {
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    @OnClick({R.id.img_search, R.id.tv_share, R.id.img_issue, R.id.img_menu})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_search:
                status = true;
                tv_no_result.setVisibility(View.GONE);
                String keyWords = et_search.getText().toString().trim();
//                insertSearch(QueryProduct.this, "产品搜索", keyWords, locations);
                if (keyWords.length() > 0) {
                    getSearchResult(keyWords, 10);
                    insertKeyWords(keyWords, "产品搜索");
                } else {
                    Util.showToast(mActivity, "请输入关键词");
                }
                break;
//                if (keyWords.length() > 0) {
//                    List<String> words = Arrays.asList(keyWords.split("\\s+"));
//                    counts.clear();
//                    data.clear();
//                    map.clear();
//                    for (int i = 0; i < data_final.size(); i++) {
//                        map.put(i, 0);
//                        content_search = data_final.get(i).getUser_name() + data_final.get(i).getContent();
//                        for (int j = 0; j < words.size(); j++) {
//                            if (content_search.contains(words.get(j))) {
//                                map.put(i, map.get(i) + 1);
//                            }
//                        }
//                    }
//                    for (int i = 0; i < map.size(); i++) {
//
//                        if (map.get(i) != 0) {
//                            counts.add(map.get(i));
//                            data.add(data_final.get(i));
//                        }
//                    }
//                    if (counts.size() < 2) {
//                        if (counts.size() > 0) {
//                            myAdapter.notifyDataSetChanged();
//                        } else {
//                            tv_no_result.setVisibility(View.VISIBLE);
//                        }
//                    } else {
//                        for (int i = 0; i < counts.size() - 1; i++) {
//                            for (int j = 0; j < counts.size() - 1 - i; j++) {
//                                if (counts.get(j) < counts.get(j + 1)) {
//                                    data.add(j, data.get(j + 1));
//                                    counts.add(j, counts.get(j + 1));
//                                    data.remove((j + 2));
//                                    counts.remove((j + 2));
//                                }
//                            }
//                            if (i == counts.size() - 2) {
//                                myAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                } else {
//                    Util.showToast(mActivity, "请输入关键词");
//                }
            case R.id.tv_share:
                String keyWord = et_search.getText().toString().trim();
                if (status) {
                    if (!"".equals(keyWord)) {
                        keyWord = "-关键词-" + keyWord;
                    } else {
                        keyWord = "";
                    }
                } else {
                    keyWord = "";
                }
                share(data, title[tag], keyWord);
                break;
            case R.id.img_issue:

                if (tag <= ProductTableName.product_type1) {
                    intent = new Intent(mActivity, IssueProductType1.class);
                } else if (tag < ProductTableName.product_type2) {
                    intent = new Intent(mActivity, IssueProductOffice.class);
                } else if (tag < ProductTableName.product_type3) {
                    intent = new Intent(mActivity, IssueProductType2.class);
                } else if (tag < ProductTableName.product_type4) {
                    intent = new Intent(mActivity, IssueProductType3.class);
                } else if (tag == ProductTableName.product_type4) {
                    intent = new Intent(mActivity, IssueProductType4.class);
                } else if (tag == ProductTableName.product_type5) {
                    intent = new Intent(mActivity, IssueProductType5.class);
                } else if (tag < ProductTableName.product_type6) {
                    intent = new Intent(mActivity, IssueProductType3.class);
                } else if (tag == ProductTableName.product_type6) {
                    intent = new Intent(mActivity, IssueProductType1.class);
                } else if (tag == ProductTableName.product_type7) {
                    intent = new Intent(mActivity, IssueProductTypeOthers.class);
                }
                intent.putExtra("type", tag);
                intent.putExtra("title", title[tag]);
                startActivity(intent);
                break;
            case R.id.img_menu:
                if (ClickTimeUtil.isFastClick()) {
                    if (myAdapter != null) {
                        status = false;
                        Intent intent = new Intent(mActivity, MenuProductActivity.class);
                        intent.putExtra("tag", tag);
                        startActivityForResult(intent, 100);
                    } else {
                        Util.showToast(mActivity, "请稍后操作，数据加载中...");
                    }
                } else {
                    Util.showToast(mActivity, "请不要点击过快");
                }
                break;
        }
    }

    protected void getSearchResult(String keyWords, int len) {
//        String sql = "SELECT login.url3,login.user_name," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10";
        String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* ,(";
        List<String> words = Arrays.asList(keyWords.split("\\s+"));
        String word_str = "";
        for (int i = 0; i < words.size(); i++) {
            word_str = word_str + words.get(i) + "|";
            if (i == 0) {
                sql = sql + "(IF( CONCAT_WS(\" \", user_name,content) LIKE \"%" + words.get(0) + "%\", 1, 0))";
            } else {
                sql = sql + " + (IF( CONCAT_WS(\" \", user_name,content) LIKE \"%" + words.get(i) + "%\", 1, 0))";
            }
            if (i == words.size() - 1) {
                word_str = word_str.substring(0, word_str.length() - 1);
                sql = sql + ") AS counts FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' AND CONCAT_WS(\" \",user_name,content) REGEXP \"" + word_str + "\" ORDER BY counts DESC LIMIT 0," + len + "";
                progressBar.setVisibility(View.VISIBLE);
                queryProduct(tag, sql);
                Log.e(TAG, "getSearchResult: " + sql);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.share:
                content_pdf = data.get(position).getContent();
                String user_name = data.get(position).getUser_name();
                String contact = data.get(position).getContact();
                String job = data.get(position).getJob();
                String other_phone = data.get(position).getPhone_address();
                String url = data.get(position).getUrl();
                String url3 = data.get(position).getUrl3();
                int url_type = data.get(position).getUrl_type();
                if(job==null){
                    job = "";
                }
                if ("".equals(url)) {
                    clickShareNoPicture(url3,user_name, contact,job, other_phone, title[tag]);
                } else {
                    if (url_type == 1) {
                        clickShare(url3,user_name, contact,job, other_phone, title[tag], position, url);
                    } else {
                        clickShareNoPicture(url3,user_name, contact,job, other_phone, title[tag]);
                    }
                }
//                savePictures(position);
                break;
            case R.id.delete:
                dialogForm = new DialogForm(mActivity, getString(R.string.dialog_form_delete), new DialogForm.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int view) {
                        switch (view) {
                            case R.id.dia_yes:
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "delete from " + table_product[tag] + " where id = " + data.get(position).getId() + "")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                                            @Override
                                            public void onSuccess(String s) {
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_1_issue where table_name  = '" + table_product[tag] + "' AND table_id = " + data.get(position).getId())
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>(mActivity, null) {
                                                            @Override
                                                            public void onSuccess(String s) {
                                                            }
                                                        });
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_2_comment WHERE table_name = '" + table_product[tag] + "' AND table_id = " + data.get(position).getId())
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>() {
                                                            @Override
                                                            public void onSuccess(String s) {
                                                            }
                                                        });
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_6_collect WHERE table_name = '" + table_product[tag] + "' AND table_id = " + data.get(position).getId())
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>() {
                                                            @Override
                                                            public void onSuccess(String s) {
                                                                refreshLayout.autoRefresh();
                                                            }
                                                        });

                                            }
                                        });
                                break;
                        }
                    }
                }, R.style.dialog_choose);
                dialogForm.show();
                full_dialog(dialogForm);
                break;
            case R.id.img:
                intent = new Intent(mActivity, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;
            case R.id.img_vx:
                String other_phone1 = data.get(position).getPhone();
                String other_name = data.get(position).getUser_name();
                String other_img = data.get(position).getUrl3();
                startChat(other_phone1, other_name, other_img);
                break;
            case R.id.img_comment:
                String contents = (String) view.getTag(content);
                String count_comments = (String) view.getTag(count_comment);
                intent = new Intent(mActivity, ProductDetailActivity.class);
                intent.putExtra("title", title[tag]);
                intent.putExtra("table_id", data.get(position).getId());
                intent.putExtra("table_name", table_product[tag]);
                intent.putExtra("user_img", data.get(position).getUrl3());
                intent.putExtra("user_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("content", contents);
                intent.putExtra("count_comment", count_comments);
                startActivity(intent);
                break;
            case R.id.img_like:
                if (checkPhone(mActivity)) {
                    ImageView img_like = (ImageView) view.getTag(view_img_like);
                    TextView tv_like = (TextView) view.getTag(view_tv_like);
                    flag_like = data.get(position).getLike_person();
                    switch (flag_like) {
                        case 0:
                            plus_like(position, img_like, tv_like);
                            break;
                        case 1:
                            cut_like(position, img_like, tv_like);
                            break;
                    }
                }
                break;
            case R.id.btn_play:
                count = 0;
                Button btn_play = (Button) view.getTag(btn_paly);
                LoadingCircleView progressCircleBar = (LoadingCircleView) view.getTag(circleBar);
                String urls = data.get(position).getUrl();
                String name = urls.substring(urls.lastIndexOf("/") + 1);
                Log.e("路径为：", "" + name);
                String path = IpConfig.PATH_DATA + "video" + File.separator + name;
//                Uri uri = Uri.parse(path);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri,"video/mp4");
//                try{
//                    getContext().startActivity(intent);
//                }catch (Exception e){
//                    Util.showToast(mActivity,"没有播放器");
//                }
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        if (!DoubleUtils.isFastDoubleClick()) {
                            Intent intent = new Intent(getActivity(), PictureVideoPlayActivity.class);
                            intent.putExtra("video_path", path);
                            startActivity(intent);
                        }
                    } else {
                        downLoad(position, name, btn_play, progressCircleBar, path);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    private void downLoad(int position, String name, Button btn_play, LoadingCircleView progressCircleBar, String path) {
        EasyHttp.downLoad(data.get(position).getUrl())
                .savePath(IpConfig.PATH_DATA + "video")
                .saveName(name)
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void onStart() {
                        btn_play.setVisibility(View.GONE);
                        progressCircleBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(ApiException e) {
                        if (count == 0) {
                            Util.showToast(mActivity, "正在加载中...");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    downLoad(position, name, btn_play, progressCircleBar, path);
                                }
                            }, 2500);
                        } else {
                            count = 1;
                            Util.showToast(mActivity, "播放出错");
                        }

                    }

                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        HttpLog.e(progress + "% ");
                        progressCircleBar.setProgerss(progress, true);
                        if (done) {//下载完成
                            progressCircleBar.setVisibility(View.GONE);
                            btn_play.setVisibility(View.VISIBLE);
                            if (!DoubleUtils.isFastDoubleClick()) {
                                Intent intent = new Intent(getActivity(), PictureVideoPlayActivity.class);
                                intent.putExtra("video_path", path);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onComplete(String path) {

                    }
                });
    }

    private void plus_like(int position, ImageView img_like, TextView tv_like) {
        data.get(position).setLike_person(1);
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) + 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(mActivity)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("+1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_3_like_product(`phone`,`table_id`,`table_name`) values('" + phone + "','" + data.get(position).getId() + "','" + table_product[tag] + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_product[tag] + " SET `like`= `like`+1 WHERE id = '" + data.get(position).getId() + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {

                                    }
                                });
                    }
                });
    }

    private void cut_like(int position, ImageView img_like, TextView tv_like) {
        data.get(position).setLike_person(0);
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) - 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(mActivity)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("-1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_3_like_product WHERE `table_id` = '" + data.get(position).getId() + "' AND phone = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_product[tag] + " SET `like`= `like`-1 WHERE id = '" + data.get(position).getId() + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {

                                    }
                                });
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (data != null) {
                tag = data.getIntExtra("tag", tag);
                recycle1.scrollToPosition(tag); //可选，减少滑动效果
                try {
                    centerLayoutManager.smoothScrollToPosition(recycle1, new RecyclerView.State(), tag);
                } catch (Exception e) {

                }
                baseRcyAdapter.notifyDataSetChanged();
                rbn1.setChecked(true);
                progressBar.setVisibility(View.VISIBLE);
                String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10";
                queryProduct(tag, sql);
            }
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
        Log.e(TAG, "afterTextChanged: 1");
        if (str.toString().length() > 0) {
        } else {
            status = false;
            tv_no_result.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String sql = "SELECT login.url3,login.user_name,login.job," + table_product[tag] + ".* FROM login," + table_product[tag] + " WHERE " + table_product[tag] + ".phone=login.phone AND `status` = '未完成' ORDER BY id DESC limit 0,10";
            queryProduct(tag, sql);
//            if (data != null) {
//                data.clear();
//                data.addAll(data_final);
//            }
//            if (myAdapter != null) {
//                myAdapter.notifyDataSetChanged();
//            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_product, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (!"".equals(data.get(position).getUrl())) {
                if (data.get(position).getUrl_type() == 1) {
                    holder.relative.setVisibility(View.GONE);
                    holder.gridView.setVisibility(View.VISIBLE);
                    List<ImageInfo> imageInfos = getImageInfos(position);
                    holder.gridView.setAdapter(new AssNineGridViewClickAdapter(mActivity, imageInfos));
                } else {
                    holder.gridView.setVisibility(View.GONE);
                    holder.relative.setVisibility(View.VISIBLE);
                    RequestOptions requestOptions = new RequestOptions()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.all_darkbackground)
                            .dontAnimate();
                    Glide.with(mActivity).load(data.get(position).getUrl()).apply(requestOptions).into(holder.iv);
                    holder.btn_play.setOnClickListener(FragmentProduct.this);
                    holder.btn_play.setTag(position_all, position);
                    holder.btn_play.setTag(btn_paly, holder.btn_play);
                    holder.btn_play.setTag(circleBar, holder.progressCircleBar);
                }
            } else {
                holder.gridView.setVisibility(View.GONE);
                holder.relative.setVisibility(View.GONE);
            }
//            String tv1 = data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getThickness() + "," + data.get(position).getPrice() + "," + data.get(position).getConsign() + ",";
//            String tv2 = data.get(position).getPostage() + "," + data.get(position).getContact() + "," + data.get(position).getPhone() + "," + data.get(position).getDescription();
//            +"\n"+"https://android.myapp.com/myapp/detail.htm?apkName=com.pwj.helloya"
            String url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            Glide.with(mActivity).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.tv_time.setText(data.get(position).getDate());
            holder.tv.setText(data.get(position).getContent());
            Pattern pattern = Pattern.compile("\\d{11,}");
            Linkify.addLinks(holder.tv, pattern, "tel:");
            holder.tv_like.setText(String.valueOf(data.get(position).getLike()));
            holder.tv_comment.setText(String.valueOf(data.get(position).getComment()));
            holder.img.setOnClickListener(FragmentProduct.this);
            holder.img.setTag(position_all, position);
            holder.share.setOnClickListener(FragmentProduct.this);
            holder.share.setTag(position_all, position);
            holder.delete.setOnClickListener(FragmentProduct.this);
            holder.delete.setTag(position_all, position);
            holder.img_vx.setOnClickListener(FragmentProduct.this);
            holder.img_vx.setTag(position_all, position);
            holder.img_comment.setOnClickListener(FragmentProduct.this);
            holder.img_comment.setTag(position_all, position);
            holder.img_comment.setTag(content, holder.tv.getText().toString());
            holder.img_comment.setTag(count_comment, holder.tv_comment.getText().toString());
            holder.img_comment.setTag(view_tv_like, holder.tv_like);
            if (!"".equals(phone)) {
                if (data.get(position).getPhone().equals(phone)) {
                    holder.delete.setVisibility(View.VISIBLE);
                } else {
                    holder.delete.setVisibility(View.INVISIBLE);
                }
                if (data.get(position).getLike_person() == 1) {
                    holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                } else {
                    holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                }
            } else {
                holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
            }
            holder.img_like.setOnClickListener(FragmentProduct.this);
            holder.img_like.setTag(position_all, position);
            holder.img_like.setTag(view_img_like, holder.img_like);
            holder.img_like.setTag(view_tv_like, holder.tv_like);
//            holder.badge.setBadgeNumber(105);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private List<ImageInfo> getImageInfos(int position) {
            List<ImageInfo> imageInfos = new ArrayList<>();
            List<String> uri_list = Arrays.asList(data.get(position).getUrl().split(","));
            for (String url : uri_list) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setBigImageUrl(url);
                imageInfo.setThumbnailUrl(url);
                imageInfos.add(imageInfo);
            }
            return imageInfos;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private CardView cv_product;
            private AssNineGridView gridView;
            private TextView tv;
            private ImageView img;
            private RelativeLayout relative;
            private ImageView iv;
            private Button btn_play;
            private LoadingCircleView progressCircleBar;
            private TextView name_tv;
            private TextView tv_time;
            private Button share;
            private Button delete;
            private ImageView img_vx;
            private ImageView img_comment;
            private ImageView img_like;
            private TextView tv_like;
            private TextView tv_comment;
            Badge badge;

            MyViewHolder(View itemView) {
                super(itemView);
                cv_product = itemView.findViewById(R.id.cv_product);
                tv = itemView.findViewById(R.id.tv);
                gridView = itemView.findViewById(R.id.gridView);
                img = itemView.findViewById(R.id.img);
                relative = itemView.findViewById(R.id.relative);
                iv = itemView.findViewById(R.id.iv);
                btn_play = itemView.findViewById(R.id.btn_play);
                progressCircleBar = itemView.findViewById(R.id.progressCircleBar);
                name_tv = itemView.findViewById(R.id.name_tv);
                tv_time = itemView.findViewById(R.id.tv_time);
                share = itemView.findViewById(R.id.share);
                delete = itemView.findViewById(R.id.delete);
                img_vx = itemView.findViewById(R.id.img_vx);
                img_comment = itemView.findViewById(R.id.img_comment);
                img_like = itemView.findViewById(R.id.img_like);
                tv_like = itemView.findViewById(R.id.tv_like);
                tv_comment = itemView.findViewById(R.id.tv_comment);
//                badge = new QBadgeView(mActivity).bindTarget(itemView.findViewById(R.id.img_comment));
//                badge.setBadgeGravity(Gravity.END | Gravity.TOP);
//                badge.setBadgeTextSize(8, true);
//                badge.setBadgePadding(3, true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        phone = LoginInfo.getString(mActivity, "phone", "");
        queryChat();
        MobclickAgent.onPageStart("FragmentProduct");
        MobclickAgent.onResume(mActivity); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentProduct");
        MobclickAgent.onPause(mActivity); //统计时长
    }
}
