package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.base.BaseActivityComment;

import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.ProductTableName;
import com.zhouyou.http.EasyHttp;

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

public class QueryRequireActivity extends BaseActivityComment implements View.OnClickListener {
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.title_tv)
    public TextView title_tv;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.progressbar)
    public ProgressBar progressbar;
    public List<Product> data;
    private MyAdapter myAdapter;
    private int type;
    private String title = "";
    private String sql = "";
    private String phone;
    private String[] table_require = new String[]{ProductTableName.require1, ProductTableName.require2, ProductTableName.require3, ProductTableName.require4, ProductTableName.require0};
    private Class[] class_require = new Class[]{IssueRequire1.class, IssueRequire2.class, IssueRequire3.class, IssueRequire4.class, IssueRequireOthers.class,};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__query_require);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        initData();
    }

    public void initData() {
        phone = LoginInfo.getString(this, "phone", "");
        initProgress(this);
        data = new ArrayList<>();
        data_comment = new ArrayList<>();
        type = getIntent().getIntExtra("type", 0);
        title = getIntent().getStringExtra("title");
        title_tv.setText(title);
        sql = "SELECT login.url3,login.user_name," + table_require[type] + ".* FROM login," + table_require[type] + " WHERE " + table_require[type] + ".phone = login.phone AND `status` = '未完成'  ORDER BY id DESC";

        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>(QueryRequireActivity.this, progressbar) {
                    @Override
                    public void onSuccess(String str) {
                        data.clear();
                        data = GsonUtils.getGsonToList(str, Product.class);
                        progressbar.setVisibility(View.GONE);
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_require[type] + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                            if (data_comment.size() > 0) {
                                                data.get(key).setLike_person(1);
                                            } else {
                                                data.get(key).setLike_person(0);
                                            }
                                            key = key + 1;
                                            if (key == data.size()) {
                                                recycler.setLayoutManager(new LinearLayoutManager(QueryRequireActivity.this, LinearLayoutManager.VERTICAL, false));
                                                myAdapter = new MyAdapter();
                                                recycler.setAdapter(myAdapter);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @OnClick({R.id.title_im, R.id.img_issue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img_issue:
                if (checkPhone(QueryRequireActivity.this)) {
                    startActivity(class_require[type]);
                }
                finish();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(QueryRequireActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;
            case R.id.img_comment:
                int id = data.get(position).getId();
                String table_name = table_require[type];
                String url3 = data.get(position).getUrl3();
                String user_name = data.get(position).getUser_name();
                String other_phone = data.get(position).getPhone();
                clickImgComment(QueryRequireActivity.this, view, title, id, table_name, url3, user_name, other_phone);
                break;
            case R.id.img_like:
                if (checkPhone(QueryRequireActivity.this)) {
                    int id1 = data.get(position).getId();
                    String table_name1 = table_require[type];
                    int flag_like = data.get(position).getLike_person();
                    if (flag_like == 0) {
                        data.get(position).setLike_person(1);
                    } else {
                        data.get(position).setLike_person(0);
                    }
                    clickImgLike(QueryRequireActivity.this, view, position, id1, table_name1, flag_like);
                }
                break;
            case R.id.share:
                content_pdf = data.get(position).getContent();
                String user_name2 = data.get(position).getUser_name();
                String other_phone2 = data.get(position).getPhone();
                String url2 = data.get(position).getUrl();
                clickShare("",user_name2, other_phone2, title, position, url2);
                break;
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(QueryRequireActivity.this).inflate(R.layout.item_product, parent, false);
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
                holder.gridView.setAdapter(new AssNineGridViewClickAdapter(QueryRequireActivity.this, imageInfos));
            }

            String url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            holder.tv.setText(data.get(position).getContent());
            Glide.with(QueryRequireActivity.this).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.tv_time.setText(data.get(position).getDate());
            holder.tv_like.setText(String.valueOf(data.get(position).getLike()));
            holder.tv_comment.setText(String.valueOf(data.get(position).getComment()));
            holder.img.setOnClickListener(QueryRequireActivity.this);
            holder.img.setTag(position_all, position);
            holder.share.setOnClickListener(QueryRequireActivity.this);
            holder.share.setTag(position_all, position);
            holder.img_comment.setOnClickListener(QueryRequireActivity.this);
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
            }
            holder.img_like.setOnClickListener(QueryRequireActivity.this);
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


    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.putExtra("type",type);
        intent.setClass(QueryRequireActivity.this, activity);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            QueryRequireActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }
}
