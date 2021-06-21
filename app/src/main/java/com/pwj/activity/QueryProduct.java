package com.pwj.activity;


import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;


import com.pwj.base.BaseActivityComment;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;

import com.pwj.callBack.SuccessCallBack;

import com.pwj.chat.OtherUserActivity;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.ProductTableName;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by 13688 on 2019/3/7.
 */

public class QueryProduct extends BaseActivityComment implements View.OnClickListener, TextWatcher {
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
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.tv_no_result)
    TextView tv_no_result;
    @BindView(R.id.rgp)
    RadioGroup rgp;
    @BindView(R.id.rbn1)
    RadioButton rbn1;
    @BindView(R.id.rbn2)
    RadioButton rbn2;
    @BindView(R.id.rbn3)
    RadioButton rbn3;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    private String[] table_product = new String[]{
            ProductTableName.product1, ProductTableName.product2, ProductTableName.product3, ProductTableName.product4,
            ProductTableName.product5, ProductTableName.product6, ProductTableName.product7, ProductTableName.product8,
            ProductTableName.product9, ProductTableName.product10, ProductTableName.product11, ProductTableName.product12,
            ProductTableName.product13, ProductTableName.product14, ProductTableName.product15, ProductTableName.product16,
            ProductTableName.product17, ProductTableName.product18, ProductTableName.product19, ProductTableName.product20,
            ProductTableName.product21, ProductTableName.product22, ProductTableName.product23, ProductTableName.product24,
            ProductTableName.product25, ProductTableName.product26, ProductTableName.product0};

    private MyAdapter myAdapter;
    private int type;
    private String title = "";
    private String sql = "";
    private List<Product> data;
    private List<Product> datas;
    private List<Product> data_final;
    private List<String> list_spinner = new ArrayList<>();
    private NiceSpinner niceSpinner;
    private boolean flag = true;
    private String content_search;
    private SparseIntArray map = new SparseIntArray();
    private List<Integer> count = new ArrayList<>();
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        initProgress(this);
        initData();
    }


    private void initData() {
        phone = LoginInfo.getString(this, "phone", "");
        title_im.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        tv_search.setVisibility(View.GONE);
        img_issue.setVisibility(View.VISIBLE);
        Keyboard.getInstance().hideKeyBoard(et_search);
        et_search.addTextChangedListener(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        type = getIntent().getIntExtra("type", 0);
        title = getIntent().getStringExtra("title");
        et_search.setHint(title);
//        et_search.setHint("关键词");
        initSpinner();
        initRadioGroup();
        data = new ArrayList<>();
        datas = new ArrayList<>();
        data_final = new ArrayList<>();
        requestPermission();
    }

    @SuppressLint("ResourceAsColor")
    private void initSpinner() {
        niceSpinner = (NiceSpinner) findViewById(R.id.spinner);
        niceSpinner.setTextColor(Color.BLUE);
        niceSpinner.setBackground(getDrawable(R.drawable.shape_spinner_default));
        list_spinner.add("日期最近");
        list_spinner.add("价格最低");
        list_spinner.add("距离最近");
        niceSpinner.attachDataSource(list_spinner);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                flag = false;
                progressbar.setVisibility(View.VISIBLE);
                switch (i) {
                    case 0:
                        rgp.clearCheck();
                        rbn1.setChecked(true);
                        progressbar.setVisibility(View.VISIBLE);
                        queryType0();
                        flag = true;
                        break;
                    case 1:
                        rgp.clearCheck();
                        rbn2.setChecked(true);
                        progressbar.setVisibility(View.VISIBLE);
                        queryType1();
                        flag = true;
                        break;
                    case 2:
                        rgp.clearCheck();
                        rbn3.setChecked(true);
                        flag = true;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void initRadioGroup() {
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                switch (id) {
                    case R.id.rbn1:
                        if (flag) {
                            progressbar.setVisibility(View.VISIBLE);
                            niceSpinner.setSelectedIndex(0);
                            queryType0();
                        }
                        break;
                    case R.id.rbn2:
                        if (flag) {
                            progressbar.setVisibility(View.VISIBLE);
                            niceSpinner.setSelectedIndex(1);
                            queryType1();
                        }
                        break;
                    case R.id.rbn3:

                        break;
                }
            }
        });
    }

    @OnClick({R.id.title_im, R.id.img_issue, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_issue:
                if (type == ProductTableName.product_type1) {
                    intent = new Intent(QueryProduct.this, IssueProductType1.class);
                }
                if (type > ProductTableName.product_type1 && type < ProductTableName.product_type2) {
                    intent = new Intent(QueryProduct.this, IssueProductType2.class);
                }
                if (type >= ProductTableName.product_type2 && type < ProductTableName.product_type3) {
                    intent = new Intent(QueryProduct.this, IssueProductType3.class);
                }
                if (type == ProductTableName.product_type3) {
                    intent = new Intent(QueryProduct.this, IssueProductType4.class);
                }
                if (type == ProductTableName.product_type4) {
                    intent = new Intent(QueryProduct.this, IssueProductType5.class);
                }
                if (type == ProductTableName.product_type5) {
                    intent = new Intent(QueryProduct.this, IssueProductTypeOthers.class);
                }
                intent.putExtra("type", type);
                intent.putExtra("title", title);
                startActivity(intent);
                finish();
                break;
            case R.id.img_search:
                tv_no_result.setVisibility(View.GONE);
                String keyWords = et_search.getText().toString().trim();
//                insertSearch(QueryProduct.this, "产品搜索", keyWords, locations);
                if (keyWords.length() > 0) {
                    List<String> words = Arrays.asList(keyWords.split("\\s+"));
                    count.clear();
                    data.clear();
                    map.clear();
                    for (int i = 0; i < data_final.size(); i++) {
                        map.put(i, 0);
                        content_search = data_final.get(i).getUser_name() + data_final.get(i).getContent();
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
                    Util.showToast(QueryProduct.this, "请输入关键词");
                }
                break;
        }
    }

    public void queryType0() {

        SearchUtil.getInstance().initLocation(QueryProduct.this, new StringCallbackOne() {
            @Override
            public void stringOne(String str, double longitude, double latitude) {
//                locations = str;
            }
        });

        if (myAdapter != null) {
            data.clear();
            data.addAll(data_final);
            myAdapter.notifyDataSetChanged();
        } else {
            data_comment = new ArrayList<>();
            key = 0;
            sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone AND `status` = '未完成' ORDER BY id DESC";
            Log.e("queryType0: ", "sql: " + sql);
            Log.e("queryType0: ", "type: " + type);
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", sql)
                    .timeStamp(true)
                    .connectTimeout(3 * 1000)
                    .execute(new SuccessCallBack<String>(QueryProduct.this, progressbar) {
                        @Override
                        public void onSuccess(String s) {
                            progressbar.setVisibility(View.GONE);
                            data.clear();
                            datas.clear();
                            datas = GsonUtils.getGsonToList(s, Product.class);
                            data.addAll(datas);
                            data_final.addAll(datas);
                            for (int i = 0; i < data_final.size(); i++) {
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_product[type] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                data_comment.clear();
                                                data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                                if (data_comment.size() > 0) {
                                                    data.get(key).setLike_person(1);
                                                    data_final.get(key).setLike_person(1);
                                                } else {
                                                    data.get(key).setLike_person(0);
                                                    data_final.get(key).setLike_person(0);
                                                }
                                                key = key + 1;
                                                if (key == data_final.size()) {
                                                    recycler.setLayoutManager(new LinearLayoutManager(QueryProduct.this, LinearLayoutManager.VERTICAL, false));
                                                    myAdapter = new MyAdapter();
                                                    recycler.setAdapter(myAdapter);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    public void queryType1() {
        key = 0;
        sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone  AND `status` = '未完成' ORDER BY price+0 ASC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(QueryProduct.this, progressbar) {
                    @Override
                    public void onSuccess(String s) {
                        progressbar.setVisibility(View.GONE);
                        data.clear();
                        datas.clear();
                        datas = GsonUtils.getGsonToList(s, Product.class);
                        data.addAll(datas);
                        for (int i = 0; i < data_final.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_product[type] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment.clear();
                                            data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                            if (data_comment.size() > 0) {
                                                data.get(key).setLike_person(1);
                                            } else {
                                                data.get(key).setLike_person(0);
                                            }
                                            key = key + 1;
                                            if (key == data_final.size()) {
                                                myAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });
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
            data.clear();
            data.addAll(data_final);
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(QueryProduct.this).inflate(R.layout.item_product, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if ("".equals(data.get(position).getUrl())) {
                holder.gridView.setVisibility(View.GONE);
            } else {
                holder.gridView.setVisibility(View.VISIBLE);
                List<ImageInfo> imageInfos = getImageInfos(position);
                holder.gridView.setAdapter(new AssNineGridViewClickAdapter(QueryProduct.this, imageInfos));
            }
            String url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            holder.tv.setText(data.get(position).getContent());
            Glide.with(QueryProduct.this).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.tv_time.setText(data.get(position).getDate());
            holder.tv_like.setText(String.valueOf(data.get(position).getLike()));
            holder.tv_comment.setText(String.valueOf(data.get(position).getComment()));
            holder.img.setOnClickListener(QueryProduct.this);
            holder.img.setTag(position_all, position);
            holder.share.setOnClickListener(QueryProduct.this);
            holder.share.setTag(position_all, position);
            holder.img_comment.setOnClickListener(QueryProduct.this);
            holder.img_comment.setTag(position_all, position);
            holder.img_comment.setTag(content, holder.tv.getText().toString());
            holder.img_comment.setTag(count_comment, holder.tv_comment.getText().toString());
            holder.img_comment.setTag(view_tv_like, holder.tv_like);
            Pattern pattern = Pattern.compile("\\d{11,}");
            Linkify.addLinks(holder.tv, pattern, "tel:");
            if (!"".equals(phone)) {
                if (data.get(position).getLike_person() == 1) {
                    holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                } else {
                    holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                }
            } else {
                holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
            }
            holder.img_like.setOnClickListener(QueryProduct.this);
            holder.img_like.setTag(position_all, position);
            holder.img_like.setTag(view_img_like, holder.img_like);
            holder.img_like.setTag(view_tv_like, holder.tv_like);
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
            private ImageView img;
            private TextView name_tv;
            private TextView tv_time;
            private Button share;
            private ImageView img_comment;
            private ImageView img_like;
            private TextView tv_like;
            private TextView tv_comment;

            MyViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                gridView = itemView.findViewById(R.id.gridView);
                img = itemView.findViewById(R.id.img);
                name_tv = itemView.findViewById(R.id.name_tv);
                tv_time = itemView.findViewById(R.id.tv_time);
                share = itemView.findViewById(R.id.share);
                img_comment = itemView.findViewById(R.id.img_comment);
                img_like = itemView.findViewById(R.id.img_like);
                tv_like = itemView.findViewById(R.id.tv_like);
                tv_comment = itemView.findViewById(R.id.tv_comment);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(QueryProduct.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;
            case R.id.img_comment:
                int id = data.get(position).getId();
                String table_name = table_product[type];
                String url3 = data.get(position).getUrl3();
                String user_name = data.get(position).getUser_name();
                String other_phone = data.get(position).getPhone();
                clickImgComment(QueryProduct.this, view, title, id, table_name, url3, user_name, other_phone);
                break;
            case R.id.img_like:
                if (checkPhone(QueryProduct.this)) {
                    int id1 = data.get(position).getId();
                    String table_name1 = table_product[type];
                    int flag_like = data.get(position).getLike_person();
                    if (flag_like == 0) {
                        data.get(position).setLike_person(1);
                    } else {
                        data.get(position).setLike_person(0);
                    }
                    clickImgLike(QueryProduct.this, view, position, id1, table_name1, flag_like);
                }
                break;
            case R.id.share:
                content_pdf = data.get(position).getContent();
                String user_name2 = data.get(position).getUser_name();
                String other_phone2 = data.get(position).getPhone();
                String url2 = data.get(position).getUrl();
                if ("".equals(url2)) {
                    clickShareNoPicture("",user_name2, other_phone2, title);
                } else {
                    clickShare("",user_name2, other_phone2, title, position, url2);
                }
                break;
        }

    }


    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        intent.putExtra("title", title);
        startActivity(intent);
    }


    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})

    public void requestPermission() {
        queryType0();
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
        finish();
    }

    /**
     * 权限被取消
     */
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        finish();
        Util.showToast(this, "禁止权限会影响到app的正常使用");
    }

}
