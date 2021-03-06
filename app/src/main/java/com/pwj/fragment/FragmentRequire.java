package com.pwj.fragment;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
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
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.activity.AddressAddActivity;
import com.pwj.activity.IssueRequire1;
import com.pwj.activity.IssueRequire2;
import com.pwj.activity.IssueRequire3;
import com.pwj.activity.IssueRequire4;
import com.pwj.activity.IssueRequireOthers;
import com.pwj.activity.LoginActivity;
import com.pwj.activity.MenuRequireActivity;

import com.pwj.activity.PictureVideoPlayActivity;
import com.pwj.activity.ProductDetailActivity;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.Comment;

import com.pwj.bean.Item_purchase;
import com.pwj.bean.MyContacts;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;

import com.pwj.classes.CenterLayoutManager;


import com.pwj.classes.LoadingCircleView;
import com.pwj.dialog.DialogForm;
import com.pwj.helloya.R;
import com.pwj.utils.ClickTimeUtil;
import com.pwj.utils.ContactUtils;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;

import com.pwj.utils.ProductTableName;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ufreedom.uikit.FloatingText;
import com.umeng.analytics.MobclickAgent;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.content.ContentValues.TAG;
import static android.provider.Settings.EXTRA_APP_PACKAGE;


/**
 * Created by leon on 3/7/18.
 */

public class FragmentRequire extends BaseFragment implements View.OnClickListener, TextWatcher {
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
    @BindView(R.id.rgp)
    RadioGroup rgp;
    @BindView(R.id.rbn1)
    RadioButton rbn1;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recycle2)
    RecyclerView recycle2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private MyAdapter myAdapter;
    private String phone;
    private Intent intent;
    private BaseRcyAdapter baseRcyAdapter;
    private CenterLayoutManager centerLayoutManager;
    private List<Item_purchase> data1;
    private List<Product> data = new ArrayList<>();
    private List<Comment> data_comment = new ArrayList<>();
    private final static int position_all = 5 << 24;
    private final static int btn_paly = 6 << 24;
    private final static int circleBar = 7 << 24;
    private final static int content = 8 << 24;
    private final static int count_comment = 9 << 24;
    private final static int view_img_like = 10 << 24;
    private final static int view_tv_like = 11 << 24;
    private int flag_like = 0;
    private int key;
    private int tag = 0;
    private TextView tv_title;
    private String[] title = {"????????????", "????????????", "????????????", "????????????", "????????????"};
    private String[] table_require = new String[]{ProductTableName.require1, ProductTableName.require2, ProductTableName.require3, ProductTableName.require4, ProductTableName.require0};
    private boolean status = false;
    private List<Product> data_final;
    private int count; //??????????????????
    private DialogForm dialogForm;
    private String content_search;
    private SparseIntArray map = new SparseIntArray();
    private List<Integer> counts = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_require, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRadioGroup();
        initRefreshLayout();
        initData1();
        initData2();
        insertUm();
        getPermission();
        return view;
    }

    private void insertUm() {
        Log.e("??????", "aaa");
        phone = LoginInfo.getString(mActivity, "phone", "");
        String deviceToken = LoginInfo.getString(mActivity, "deviceToken", "");
        String times_day = SearchUtil.getInstance().getNowDate10();
        String times_before = LoginInfo.getString(mActivity, "time_um", "");
        if (!times_day.equals(times_before)) {
//            LoginInfo.setString(mActivity, "time_um", times_day);
            NotificationManagerCompat manager = NotificationManagerCompat.from(mActivity);
            // areNotificationsEnabled??????????????????????????????????????????API 19?????????19??????????????????????????????????????????true?????????????????????????????????????????????
            boolean isOpened = manager.areNotificationsEnabled();
            if (!isOpened) {
                DialogForm dialogForm = new DialogForm(mActivity, "????????????????????????????????????????????????", new DialogForm.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int view) {
                        switch (view) {
                            case R.id.dia_yes:
                                initSettingMessage();
                                break;
                        }
                    }
                }, R.style.dialog_choose);
                dialogForm.show();
            }
            Log.e("??????", "bbb");
            if (!"".equals(phone) && !"".equals(deviceToken)) {
                Log.e("??????", "ccc");
//                PushAgent.getInstance(mActivity).deleteAlias(phone, "????????????", new UTrack.ICallBack() {
//                    @Override
//                    public void onMessage(boolean isSuccess, String s) {
//                        if (isSuccess) {
//                            Log.e("??????","????????????");
//                        }
//                    }
//                });
                PushAgent.getInstance(mActivity).addAlias(phone, "????????????", new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, String s) {
                        if (isSuccess) {
                            Log.e("??????", "????????????");
                            LoginInfo.setString(mActivity, "time_um", times_day);
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "update login set deviceToken = '" + deviceToken + "' where phone = " + phone + "")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                        }
                                    });
                        }
                    }
                });
            }
        }
    }

    private void initSettingMessage() {
        try {
            // ??????isOpened?????????????????????????????????????????????AppInfo??????????????????App????????????
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //????????????????????? API 26, ???8.0??????8.0??????????????????
            intent.putExtra(EXTRA_APP_PACKAGE, mActivity.getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, mActivity.getApplicationInfo().uid);

            //????????????????????? API21??????25?????? 5.0??????7.1 ???????????????????????????
            intent.putExtra("app_package", mActivity.getPackageName());
            intent.putExtra("app_uid", mActivity.getApplicationInfo().uid);

            // ??????6 -MIUI9.6-8.0.0??????????????????????????????????????????????????????"????????????????????????"??????????????????????????????????????????????????????????????????I'm not ok!!!
            //  if ("MI 6".equals(Build.MODEL)) {
            //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //      Uri uri = Uri.fromParts("package", getPackageName(), null);
            //      intent.setData(uri);
            //      // intent.setAction("com.android.settings/.SubSettings");
            //  }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // ?????????????????????????????????????????????????????????3??????OC105 API25
            Intent intent = new Intent();

            //??????????????????????????????????????????????????????????????????
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private void initRadioGroup() {
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                progressBar.setVisibility(View.VISIBLE);
                if (myAdapter != null) {
                    switch (id) {
                        case R.id.rbn1:
                            String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC";
                            queryProduct(tag, sql);
                            break;
                        case R.id.rbn2:
                            if (tag == 1) {
                                String sql2 = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY price+0 ASC";
                                queryProduct(tag, sql2);
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }
                            break;
                        case R.id.rbn3:
                            progressBar.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    Util.showToast(mActivity, "???????????????????????????...");
                }
            }
        });
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                et_search.setText("");
                String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY  id DESC limit 0,10";
                queryProduct(tag, sql);
                refreshlayout.finishRefresh(2000/*,false*/);//??????false??????????????????
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
                    String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0," + data_final.size() + 10 + "";
                    queryProduct(tag, sql);
                }
                refreshlayout.finishLoadMore(2000/*,false*/);//??????false??????????????????
            }
        });
    }
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
                        progressBar.setVisibility(View.VISIBLE);
                        rbn1.setChecked(true);
                        progressBar.setVisibility(View.VISIBLE);
                        String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0,10";
                        status = false;
                        queryProduct(position, sql);
                    } else {
                        Util.showToast(mActivity, "?????????????????????????????????...");
                    }
                } else {
                    Util.showToast(mActivity, "?????????????????????");
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
        //??????
        recycle2.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        myAdapter = new MyAdapter();
        recycle2.setAdapter(myAdapter);
        String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0,10";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0,10")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(mActivity, null) {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data = GsonUtils.getGsonToList(s, Product.class);
                        data_final.addAll(data);
                        Log.e(TAG, "onSuccess: " + sql);
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
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_require[0] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
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
        Log.e(TAG, "sql??????: " + sql);
        data_final = new ArrayList<>();     //??????
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
                        Log.e(TAG, "onSuccess: " + data.size());
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
                        } else {
                            if (tv_no_result!=null){
                                tv_no_result.setVisibility(View.GONE);
                            }
                        }
                        if ("".equals(phone)) {
//                            myAdapter = new MyAdapter(); //??????
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            return;
                        } else {
                            if (data.size() == 0) {
//                                myAdapter = new MyAdapter(); //??????
                                if (myAdapter != null) {
                                    myAdapter.notifyDataSetChanged();
                                }
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                                return;
                            }
                        }
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_require[position] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
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

    @OnClick({R.id.img_search, R.id.img_issue, R.id.tv_share, R.id.img_menu})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_search:
                status = true;
                tv_no_result.setVisibility(View.GONE);
                String keyWords = et_search.getText().toString().trim();
                if (keyWords.length() > 0) {
                    getSearchResult(keyWords, 10);
                    insertKeyWords(keyWords, "????????????");
                } else {
                    Util.showToast(mActivity, "??????????????????");
                }
//                insertSearch(QueryProduct.this, "????????????", keyWords, locations);
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
//                    Util.showToast(mActivity, "??????????????????");
//                }
                break;
            case R.id.img_issue:
                switch (tag) {
                    case 0:
                        intent = new Intent(mActivity, IssueRequire1.class);
                        break;
                    case 1:
                        intent = new Intent(mActivity, IssueRequire2.class);
                        break;
                    case 2:
                        intent = new Intent(mActivity, IssueRequire3.class);
                        break;
                    case 3:
                        intent = new Intent(mActivity, IssueRequire4.class);
                        break;
                    case 4:
                        intent = new Intent(mActivity, IssueRequireOthers.class);
                        break;
                }
                intent.putExtra("type", tag);
                intent.putExtra("title", title[tag]);
                startActivity(intent);
                break;
            case R.id.tv_share:
                String keyWord = et_search.getText().toString().trim();
                if (status) {
                    if (!"".equals(keyWord)) {
                        keyWord = "-?????????-" + keyWord;
                    } else {
                        keyWord = "";
                    }
                } else {
                    keyWord = "";
                }
                share(data, title[tag], keyWord);
                break;
            case R.id.img_menu:
                if (ClickTimeUtil.isFastClick()) {
                    if (myAdapter != null) {
                        status = false;
                        Intent intent = new Intent(mActivity, MenuRequireActivity.class);
                        intent.putExtra("tag", tag);
                        startActivityForResult(intent, 100);
                    } else {
                        Util.showToast(mActivity, "?????????????????????????????????...");
                    }
                } else {
                    Util.showToast(mActivity, "?????????????????????");
                }
                break;
        }
    }

    protected void getSearchResult(String keyWords, int len) {
        String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* ,(";
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
                sql = sql + ") AS counts FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone=login.phone AND `status` = '?????????' AND CONCAT_WS(\" \",user_name,content) REGEXP \"" + word_str + "\" ORDER BY counts DESC LIMIT 0," + len + "";
                progressBar.setVisibility(View.VISIBLE);
                queryProduct(tag, sql);
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
                if (job == null) {
                    job = "";
                }
                if ("".equals(url)) {
                    clickShareNoPicture(url3, user_name, contact, job, other_phone, title[tag]);
                } else {
                    if (url_type == 1) {
                        clickShare(url3, user_name, contact, job, other_phone, title[tag], position, url);
                    } else {
                        clickShareNoPicture(url3, user_name, contact, job, other_phone, title[tag]);
                    }
                }
                break;
            case R.id.delete:
                dialogForm = new DialogForm(mActivity, getString(R.string.dialog_form_delete), new DialogForm.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int view) {
                        switch (view) {
                            case R.id.dia_yes:
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "delete from " + table_require[tag] + " where id = " + data.get(position).getId() + "")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>(mActivity, progressBar) {
                                            @Override
                                            public void onSuccess(String s) {
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_1_issue where table_name  = '" + table_require[tag] + "' AND table_id = " + data.get(position).getId())
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>(mActivity, null) {
                                                            @Override
                                                            public void onSuccess(String s) {
                                                            }
                                                        });
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_2_comment WHERE table_name = '" + table_require[tag] + "' AND table_id = " + data.get(position).getId())
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>() {
                                                            @Override
                                                            public void onSuccess(String s) {
                                                            }
                                                        });
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "delete from comment_6_collect WHERE table_name = '" + table_require[tag] + "' AND table_id = " + data.get(position).getId())
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
                intent.putExtra("table_name", table_require[tag]);
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
                Button btn_play = (Button) view.getTag(btn_paly);
                LoadingCircleView progressCircleBar = (LoadingCircleView) view.getTag(circleBar);
                String urls = data.get(position).getUrl();
                String name = urls.substring(urls.lastIndexOf("/") + 1);
                Log.e("????????????", "" + name);
                String path = IpConfig.PATH_DATA + "video" + File.separator + name;
//                Uri uri = Uri.parse(path);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri,"video/mp4");
//                try{
//                    getContext().startActivity(intent);
//                }catch (Exception e){
//                    Util.showToast(mActivity,"???????????????");
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
                            Util.showToast(mActivity, "???????????????...");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    downLoad(position, name, btn_play, progressCircleBar, path);
                                }
                            }, 2500);
                        } else {
                            count = 1;
                            Util.showToast(mActivity, "????????????");
                        }

                    }

                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        HttpLog.e(progress + "% ");
                        progressCircleBar.setProgerss(progress, true);
                        if (done) {//????????????
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
                .params("query", "insert into comment_3_like_product(`phone`,`table_id`,`table_name`) values('" + phone + "','" + data.get(position).getId() + "','" + table_require[tag] + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_require[tag] + " SET `like`= `like`+1 WHERE id = '" + data.get(position).getId() + "'")
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
                                .params("query", "UPDATE " + table_require[tag] + " SET `like`= `like`-1 WHERE id = '" + data.get(position).getId() + "'")
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
                recycle1.scrollToPosition(tag);
                baseRcyAdapter.notifyDataSetChanged();
                rbn1.setChecked(true);
                progressBar.setVisibility(View.VISIBLE);
                String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0,10";
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
        if (str.toString().length() > 0) {
        } else {
            status = false;
            tv_no_result.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String sql = "SELECT login.url3,login.user_name,login.job," + table_require[tag] + ".* FROM login," + table_require[tag] + " WHERE " + table_require[tag] + ".phone = login.phone AND `status` = '?????????'  ORDER BY id DESC limit 0,10";
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
                    holder.btn_play.setOnClickListener(FragmentRequire.this);
                    holder.btn_play.setTag(position_all, position);
                    holder.btn_play.setTag(btn_paly, holder.btn_play);
                    holder.btn_play.setTag(circleBar, holder.progressCircleBar);
                }
            } else {
                holder.gridView.setVisibility(View.GONE);
                holder.relative.setVisibility(View.GONE);
            }

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
            holder.img.setOnClickListener(FragmentRequire.this);
            holder.img.setTag(position_all, position);
            holder.share.setOnClickListener(FragmentRequire.this);
            holder.share.setTag(position_all, position);
            holder.delete.setOnClickListener(FragmentRequire.this);
            holder.delete.setTag(position_all, position);
            holder.img_vx.setOnClickListener(FragmentRequire.this);
            holder.img_vx.setTag(position_all, position);
            holder.img_comment.setOnClickListener(FragmentRequire.this);
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
            holder.img_like.setOnClickListener(FragmentRequire.this);
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
            }
        }
    }

    /**
     * ??????????????????
     */
    @NeedPermission(value = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void getPermission() {
        if (!phone.equals("")) {
            String toDay = SearchUtil.getInstance().getNowDate10();
            String lastDay = "";
            lastDay = LoginInfo.getString(mActivity, "time_tel", "");
            if ("".equals(lastDay)) {
                getTelPhone(toDay);
            } else {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = df.parse(toDay);
                    d2 = df.parse(lastDay);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long time = (d1.getTime() - d2.getTime()) / (60 * 60 * 1000 * 24);
                if (time > 30) {
                    getTelPhone(toDay);
                }
            }
        }
    }

    /**
     * ???????????????
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
    }

    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(mActivity, "????????????????????????app???????????????");
    }

    private void getTelPhone(String toDay) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "DELETE FROM telephone WHERE phone = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MyContacts> data_contact = ContactUtils.getAllContacts(mActivity);
                for (int i = 0; i < data_contact.size(); i++) {
                    EasyHttp.post(IpConfig.URL_SQL)
                            .params("query", "insert into telephone(`phone`,`contact`,`relation`,`phone_number`,`address`,`company`,`company_position`,`email`,`pwj`,`remarks`)values('" + phone + "','" + data_contact.get(i).getContact() + "','" + data_contact.get(i).getRelation() + "','" + data_contact.get(i).getPhone_number() + "','" + data_contact.get(i).getAddress() + "','" + data_contact.get(i).getCompany() + "','" + data_contact.get(i).getCompany_position() + "','" + data_contact.get(i).getEmail() + "','" + data_contact.get(i).getPwj() + "','" + data_contact.get(i).getRemarks() + "')")
                            .timeStamp(true)
                            .execute(new SuccessCallBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    LoginInfo.setString(mActivity, "time_tel", toDay);
                                }
                            });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryChat();
        MobclickAgent.onPageStart("FragmentRequire");
        MobclickAgent.onResume(mActivity); //????????????
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentRequire");
        MobclickAgent.onPause(mActivity); //????????????
    }

}
