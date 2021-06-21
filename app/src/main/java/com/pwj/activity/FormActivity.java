package com.pwj.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.tools.DoubleUtils;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.adapter.FormAdapter;
import com.pwj.base.BaseActivityComment;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.classes.CircleImageView;
import com.pwj.classes.LoadingCircleView;
import com.pwj.fragment.FragmentProduct;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.dialog.DialogForm;
import com.pwj.helloya.R;

import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class FormActivity extends BaseActivityComment implements RadioGroup.OnCheckedChangeListener, TextWatcher, View.OnClickListener {
    private List<String> list_spinner = new ArrayList<>();
    private int type_product = 0;     //查询表的种类
    private int type_rbn = 1;   //rbn按钮
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.title_im)
    ImageView title_im;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    @BindView(R.id.rgp)
    RadioGroup rgp;
    @BindView(R.id.rbn1)
    RadioButton rbn1;
    @BindView(R.id.rbn2)
    RadioButton rbn2;
    @BindView(R.id.rbn3)
    RadioButton rbn3;
    @BindView(R.id.tv_no_result)
    TextView tv_no_result;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.recycler)
    RecyclerView recycle;
    private List<Comment> data_comment_like = new ArrayList<>();
    private FormAdapter formAdapter;
    private MyAdapter myAdapter;
    private BaseRcyAdapter baseRcyAdapter;
    private Button status_btn1;
    private Button status_btn2;
    private DialogForm dialogForm;
    private String phone;
    private String sql = "";
    private int id;
    private List<Product> data_user = new ArrayList<>();
    private List<Product> data;
    private List<Product> datas;
    private List<Product> data_final;
    private String content_search;
    private SparseIntArray map = new SparseIntArray();
    private List<Integer> count = new ArrayList<>();
    private String user_name;
    private String user_img;
    private String arr_sql[] = {" AND `number` < 90 ", " AND `number` > 89  AND `number` < 100 ", " AND `number` = 100 "};
    private int count_init = 0;
    private final static int btn_paly = 6 << 24;
    private final static int circleBar = 7 << 24;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);
        initView();
        initSpinner();
        initData();
    }

    private void initView() {
        initProgress(this);
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
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshing("");
            }
        });
        swipe.setSize(SwipeRefreshLayout.LARGE);
        swipe.setColorSchemeResources(R.color.orange, R.color.blue_bg, R.color.green);
    }


    @SuppressLint("ResourceAsColor")
    private void initSpinner() {
        rgp.setOnCheckedChangeListener(this);
        NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        niceSpinner.setBackground(getDrawable(R.drawable.shape_spinner_default));
        niceSpinner.setTextColor(Color.BLUE);
        list_spinner.add("产品");
        list_spinner.add("需求");
        list_spinner.add("招聘");
        niceSpinner.attachDataSource(list_spinner);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type_product = i;
                progressbar.setVisibility(View.VISIBLE);
                queryType("");
                rgp.clearCheck();
                rbn1.setChecked(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int id) {
        switch (id) {
            case R.id.rbn1:
                type_rbn = 1;
                setBtnData1();
                break;
            case R.id.rbn2:
                type_rbn = 2;
                setBtnData2();
                break;
            case R.id.rbn3:
                type_rbn = 3;
                setBtnData3();
                break;
        }

    }

    private void onRefreshing(String toast) {
        if (!swipe.isRefreshing()) {
            swipe.setRefreshing(true);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryType(toast);
            }
        }, 4000);
    }

    private void setBtnData1() {
        data.clear();
        data.addAll(data_final);
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void setBtnData2() {
        data.clear();
        for (int i = 0; i < data_final.size(); i++) {
            if (data_final.get(i).getStatus().equals("已完成")) {
            } else {
                data.add(data_final.get(i));
            }
            if (i == data_final.size() - 1) {
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setBtnData3() {
        data.clear();
        Log.e(TAG, "onSuccess3-0: " + data.size() + "--" + data_final.size());
        for (int i = 0; i < data_final.size(); i++) {
            if (data_final.get(i).getStatus().equals("未完成")) {
            } else {
                data.add(data_final.get(i));
            }
            Log.e(TAG, "onSuccess3-1: " + data.size() + "--" + data_final.size());
            if (i == data_final.size() - 1) {
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void initData() {
        data = new ArrayList<>();
        datas = new ArrayList<>();
        data_final = new ArrayList<>();
        user_name = LoginInfo.getString(this, "user_name", "");
        phone = LoginInfo.getString(FormActivity.this, "phone", "");
        user_img = LoginInfo.getString(this, "user_img", "");
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM login WHERE phone = " + phone)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data_user = GsonUtils.getGsonToList(json, Product.class);
                    }
                });
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_1_issue WHERE phone = '" + phone + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                            @Override
                            public void onSuccess(String json) {
                                data_comment = GsonUtils.getGsonToList(json, Comment.class);
                                Log.e(TAG, "发布语句" + "SELECT * FROM comment_1_issue WHERE phone = '" + phone + "' ORDER BY id DESC");
                                Log.e(TAG, "发布长度" + data_comment.size());
                                if (data_comment.size() == 0) {
                                    progressbar.setVisibility(View.GONE);
                                    return;
                                }
                                for (int i = 0; i < data_comment.size(); i++) {
                                    String sql = "SELECT * FROM " + data_comment.get(i).getTable_name() + " WHERE id = " + data_comment.get(i).getTable_id() + " AND `status` = '未完成'";
                                    Log.e(TAG, "sql语句" + sql);
                                    int finalI = i;
                                    EasyHttp.post(IpConfig.URL_SQL)
                                            .params("query", "SELECT * FROM " + data_comment.get(i).getTable_name() + " WHERE id = " + data_comment.get(i).getTable_id() + arr_sql[0])
                                            .timeStamp(true)
                                            .cacheMode(CacheMode.NO_CACHE)
                                            .execute(new SuccessCallBack<String>() {
                                                @Override
                                                public void onSuccess(String json) {
                                                    datas = GsonUtils.getGsonToList(json, Product.class);
                                                    if (datas.size() > 0) {
                                                        data.addAll(datas);
                                                    }
                                                    if (finalI == data_comment.size() - 1) {
                                                        data_final.addAll(data);
                                                        if (data_final.size() == 0 && count_init == 0) {
                                                            count_init = 1;
                                                            initData();
                                                            return;
                                                        }
                                                        progressbar.setVisibility(View.GONE);
                                                        myAdapter = new MyAdapter();
                                                        recycle.setLayoutManager(new LinearLayoutManager(FormActivity.this));
                                                        recycle.setAdapter(myAdapter);
                                                        Log.e(TAG, "onSuccess:初始化 data长度" + data.size() + "-i值" + finalI + "-data_final的长度" + data_final.size());
                                                    }
                                                }
                                            });
                                }
                            }
                        });
    }


    private void queryType(String toast) {
        key = 0;
        data.clear();
        data_final.clear();
        if (data_comment.size() == 0) {
            progressbar.setVisibility(View.GONE);
        }
        for (int i = 0; i < data_comment.size(); i++) {
            String sql = "SELECT * FROM " + data_comment.get(i).getTable_name() + " WHERE id = " + data_comment.get(i).getTable_id() + arr_sql[type_product];
            Log.e(TAG, "sql语句" + sql);
            int finalI = i;
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT * FROM " + data_comment.get(i).getTable_name() + " WHERE id = " + data_comment.get(i).getTable_id() + arr_sql[type_product])
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String json) {
                            datas = GsonUtils.getGsonToList(json, Product.class);
                            if (datas.size() > 0) {
                                data.addAll(datas);
                            }
                            if (data.size() == 0) {
                                progressbar.setVisibility(View.GONE);
                            }
                            if (finalI == data_comment.size() - 1) {
                                swipe.setRefreshing(false);
                                data_final.addAll(data);
                                progressbar.setVisibility(View.GONE);
                                rgp.clearCheck();
                                rbn1.setChecked(true);
                                myAdapter.notifyDataSetChanged();
                                if (!"".equals(toast)) {
                                    Util.showToast(FormActivity.this, toast);
                                }
                                Log.e(TAG, "onSuccess:queryType data长度" + data.size() + "-i值" + finalI + "-data_final的长度" + data_final.size());
                            }
                        }
                    });
        }
    }

    private void upDateStatus(int position, String mStr, String status, String toast) {
        if (swipe.isRefreshing()) {
            Util.showToast(FormActivity.this, "正在提交中，请稍后操作");
        } else {
            dialogForm = new DialogForm(FormActivity.this, mStr, new DialogForm.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(int view) {
                    switch (view) {
                        case R.id.dia_yes:
                            id = data.get(position).getId();
                            sql = "update " + data.get(position).getTable_name() + " set status = '" + status + "' where id = " + id + "";
                            update(sql, toast);
                            Log.e(TAG, "customDialogEvent: " + sql);
                            break;
                    }
                }
            }, R.style.dialog_choose);
            dialogForm.show();
            full_dialog();
        }
    }

    private void deleteStatus(int position, String mStr, String toast) {
        if (swipe.isRefreshing()) {
            Util.showToast(FormActivity.this, "正在提交中，请稍后操作");
        } else {
            dialogForm = new DialogForm(FormActivity.this, mStr, new DialogForm.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(int view) {
                    switch (view) {
                        case R.id.dia_yes:
                            id = data.get(position).getId();
                            sql = "delete from " + data.get(position).getTable_name() + " where id = " + id + "";
                            delete(sql, position, id, toast);
                            Log.e(TAG, "customDialogEvent: " + sql);
                            break;
                    }
                }
            }, R.style.dialog_choose);
            dialogForm.show();
            full_dialog();
        }
    }

    private void update(String sql, String toast) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        onRefreshing(toast);
                    }
                });
    }

    //    "delete from comment_1_issue where phone = '" + phone + "' AND table_id = " + id + " AND table_name = '" + data.get(position).getTable_name() + "'"
    private void delete(String sql, int position, int id, String toast) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_1_issue where  table_id = " + id + " AND table_name = '" + data.get(position).getTable_name() + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", sql)
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
//                                        EasyHttp.post(IpConfig.URL_SQL)
//                                                .params("query", "delete from comment_1_issue where table_name  = '" + data.get(position).getTable_name() + "' AND table_id = " + id + "")
//                                                .timeStamp(true)
//                                                .execute(new SuccessCallBack<String>() {
//                                                    @Override
//                                                    public void onSuccess(String s) {
//                                                        onRefreshing(toast);
//                                                    }
//                                                });
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "delete from comment_2_comment WHERE table_name = '" + data.get(position).getTable_name() + "' AND table_id = " + id + "")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        onRefreshing(toast);
                                                    }
                                                });
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "delete from comment_6_collect WHERE table_name = '" + data.get(position).getTable_name() + "' AND table_id = " + id + "")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    @OnClick({R.id.title_im, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_search:
                tv_no_result.setVisibility(View.GONE);
                String keyWords = et_search.getText().toString().trim();
                if (keyWords.length() > 0) {
                    insertKeyWords(keyWords, "我的发布");
                    List<String> words = Arrays.asList(keyWords.split("\\s+"));
                    count.clear();
                    data.clear();
                    map.clear();
                    for (int i = 0; i < data_final.size(); i++) {
                        map.put(i, 0);
                        content_search = data_final.get(i).getContent();
                        for (int j = 0; j < words.size(); j++) {
                            if (content_search.contains(words.get(j))) {
                                map.put(i, map.get(i) + 1);
                            }
                        }
                    }
                    for (int i = 0; i < map.size(); i++) {

                        if (map.get(i) != 0) {
                            count.add(map.get(i));
                            data.add(data_final.get(i));
                        }
                    }
                    if (count.size() < 2) {
                        if (count.size() > 0) {
                            myAdapter.notifyDataSetChanged();
                        } else {
                            tv_no_result.setVisibility(View.VISIBLE);
                        }
                    } else {
                        for (int i = 0; i < count.size() - 1; i++) {
                            for (int j = 0; j < count.size() - 1 - i; j++) {
                                if (count.get(j) < count.get(j + 1)) {
                                    data.add(j, data.get(j + 1));
                                    count.add(j, count.get(j + 1));
                                    data.remove((j + 2));
                                    count.remove((j + 2));
                                }
                            }
                            if (i == count.size() - 2) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    Util.showToast(FormActivity.this, "请输入关键词");
                }
                break;
        }
    }


    //设置弹窗宽度
    private void full_dialog() {
        WindowManager windowManager = FormActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogForm.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()) - 150; //设置宽度
        dialogForm.getWindow().setAttributes(lp);
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
            queryType("");
        }

    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(FormActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", user_name);
                intent.putExtra("other_phone", phone);
                intent.putExtra("other_img", user_img);
                startActivity(intent);
                break;
            case R.id.img_comment:
                int id = data.get(position).getId();
                String table_name = data.get(position).getTable_name();
                String other_phone = data.get(position).getPhone();
                clickImgComment(FormActivity.this, view, data.get(position).getProduct_name(), id, table_name, user_img, user_name, other_phone);
                break;
            case R.id.tv:
                int id1 = data.get(position).getId();
                String table_name1 = data.get(position).getTable_name();
                String other_phone1 = data.get(position).getPhone();
                clickImgComment(FormActivity.this, view, data.get(position).getProduct_name(), id1, table_name1, user_img, user_name, other_phone1);
                break;
//            case R.id.img_like:
//                int id1 = data.get(position).getId();
//                String table_name1 = table_require[type_product];
//                int flag_like = data.get(position).getLike_person();
//                if (flag_like == 0) {
//                    data.get(position).setLike_person(1);
//                } else {
//                    data.get(position).setLike_person(0);
//                }
//                clickImgLike(FormActivity.this, view, position, id1, table_name1, flag_like);
//                break;
            case R.id.share:
                String contact = data.get(position).getContact();
                String other_phone2 = data.get(position).getPhone_address();
                content_pdf = data.get(position).getContent();
                String url3 = data_user.get(0).getUrl3();
                String title = data.get(position).getProduct_name();
                if (data.get(position).getNumber() > 99) {
                    clickShareNoPicture(url3, contact, other_phone2, title);
                } else {
                    String url2 = data.get(position).getUrl();
                    int url_type = data.get(position).getUrl_type();
                    if ("".equals(url2)) {
                        clickShareNoPicture(url3, contact, other_phone2, title);
                    } else {
                        if (url_type == 1) {
                            clickShare(url3, contact, other_phone2, title, position, url2);
                        } else {
                            clickShareNoPicture(url3, contact, other_phone2, title);
                        }
                    }
                }
                break;
            case R.id.no_ready:
                upDateStatus(position, getString(R.string.dialog_form_down), "已完成", "恭喜你，下架成功");
                break;
            case R.id.ready:
                upDateStatus(position, getString(R.string.dialog_form_up), "未完成", "恭喜你，上架成功");
                break;
            case R.id.delete:
                deleteStatus(position, getString(R.string.dialog_form_delete), "订单成功删除");
                break;
            case R.id.btn_play:
                Button btn_play = (Button) view.getTag(btn_paly);
                LoadingCircleView progressCircleBar = (LoadingCircleView) view.getTag(circleBar);
                String urls = data.get(position).getUrl();
                String name = urls.substring(urls.lastIndexOf("/") + 1);
                String path = IpConfig.PATH_DATA + "video" + File.separator + name;
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        if (!DoubleUtils.isFastDoubleClick()) {
                            Intent intent1 = new Intent(FormActivity.this, PictureVideoPlayActivity.class);
                            intent1.putExtra("video_path", path);
                            startActivity(intent1);
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
                        Util.showToast(FormActivity.this, "播放出错");
                    }

                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        HttpLog.e(progress + "% ");
                        progressCircleBar.setProgerss(progress, true);
                        if (done) {//下载完成
                            btn_play.setVisibility(View.VISIBLE);
                            progressCircleBar.setVisibility(View.GONE);
                            if (!DoubleUtils.isFastDoubleClick()) {
                                Intent intent = new Intent(FormActivity.this, PictureVideoPlayActivity.class);
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

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FormActivity.this).inflate(R.layout.item_me_form, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            Log.e(TAG, "onBindViewHolder: " + position);
            if (data.get(position).getNumber() != 100) {
                if (!"".equals(data.get(position).getUrl())) {
                    if (data.get(position).getUrl_type() == 1) {
                        holder.relative.setVisibility(View.GONE);
                        holder.gridView.setVisibility(View.VISIBLE);
                        List<ImageInfo> imageInfos = getImageInfos(position);
                        holder.gridView.setAdapter(new AssNineGridViewClickAdapter(FormActivity.this, imageInfos));
                    } else {
                        holder.gridView.setVisibility(View.GONE);
                        holder.relative.setVisibility(View.VISIBLE);
                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.all_darkbackground)
                                .dontAnimate();
                        Glide.with(FormActivity.this).load(data.get(position).getUrl()).apply(requestOptions).into(holder.iv);
                        holder.btn_play.setOnClickListener(FormActivity.this);
                        holder.btn_play.setTag(position_all, position);
                        holder.btn_play.setTag(btn_paly, holder.btn_play);
                        holder.btn_play.setTag(circleBar, holder.progressCircleBar);
                    }
                } else {
                    holder.gridView.setVisibility(View.GONE);
                    holder.relative.setVisibility(View.GONE);
                }
                holder.img_like.setVisibility(View.VISIBLE);
                holder.tv_like.setVisibility(View.VISIBLE);
                holder.img_comment.setVisibility(View.VISIBLE);
                holder.tv_comment.setVisibility(View.VISIBLE);
                holder.tv_like.setText(String.valueOf(data.get(position).getLike()));
                holder.tv_comment.setText(String.valueOf(data.get(position).getComment()));
            } else {
                holder.relative.setVisibility(View.GONE);
                holder.gridView.setVisibility(View.GONE);
                holder.img_like.setVisibility(View.GONE);
                holder.tv_like.setVisibility(View.GONE);
                holder.img_comment.setVisibility(View.GONE);
                holder.tv_comment.setVisibility(View.GONE);
            }
//            String url = data.get(position).getUrl3();
            holder.tv.setText(data.get(position).getContent());
            Glide.with(FormActivity.this).load(user_img).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.tv_time.setText(data.get(position).getDate());
            holder.img.setOnClickListener(FormActivity.this);
            holder.img.setTag(position_all, position);
            holder.share.setOnClickListener(FormActivity.this);
            holder.share.setTag(position_all, position);
            holder.delete.setOnClickListener(FormActivity.this);
            holder.delete.setTag(position_all, position);
            if (data.get(position).getStatus().equals("已完成")) {
                holder.no_ready.setVisibility(View.INVISIBLE);
                holder.ready.setVisibility(View.VISIBLE);
            } else {
                holder.ready.setVisibility(View.INVISIBLE);
                holder.no_ready.setVisibility(View.VISIBLE);
            }
            holder.no_ready.setOnClickListener(FormActivity.this);
            holder.no_ready.setTag(position_all, position);
            holder.ready.setOnClickListener(FormActivity.this);
            holder.ready.setTag(position_all, position);
            holder.tv.setOnClickListener(FormActivity.this);
            holder.tv.setTag(position_all, position);
            holder.tv.setTag(content, holder.tv.getText().toString());
            holder.tv.setTag(count_comment, holder.tv_comment.getText().toString());
            holder.img_comment.setOnClickListener(FormActivity.this);
            holder.img_comment.setTag(position_all, position);
            holder.img_comment.setTag(content, holder.tv.getText().toString());
            holder.img_comment.setTag(count_comment, holder.tv_comment.getText().toString());
//            holder.img_comment.setTag(view_tv_like, holder.tv_like);
//            holder.img_like.setOnClickListener(FormActivity.this);
//            holder.img_like.setTag(position_all, position);
//            holder.img_like.setTag(view_img_like, holder.img_like);
//            holder.img_like.setTag(view_tv_like, holder.tv_like);
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
            private AssNineGridView gridView;
            private TextView tv;
            private CircleImageView img;
            private RelativeLayout relative;
            private ImageView iv;
            private Button btn_play;
            private LoadingCircleView progressCircleBar;
            private TextView name_tv;
            private TextView tv_time;
            private Button share;
            private Button no_ready;
            private Button ready;
            private Button delete;
            private ImageView img_comment;
            private ImageView img_like;
            private TextView tv_like;
            private TextView tv_comment;

            MyViewHolder(View itemView) {
                super(itemView);
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
                no_ready = itemView.findViewById(R.id.no_ready);
                ready = itemView.findViewById(R.id.ready);
                delete = itemView.findViewById(R.id.delete);
                img_comment = itemView.findViewById(R.id.img_comment);
                img_like = itemView.findViewById(R.id.img_like);
                tv_like = itemView.findViewById(R.id.tv_like);
                tv_comment = itemView.findViewById(R.id.tv_comment);
            }
        }

    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("FormActivity"); //统计页面("MainScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("FormActivity");
        MobclickAgent.onPause(this); //统计时长
    }

}
