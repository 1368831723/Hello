package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pwj.base.BaseActivityComment;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.classes.CircleImageView;
import com.pwj.classes.LoadingCircleView;
import com.pwj.fragment.FragmentProduct;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


public class PersonCollectActivity extends BaseActivityComment implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv)
    TextView tv;
    private String user_img;
    private String user_name;
    private String phone;
    private List<Comment> data_comment_collect = new ArrayList<>();
    private List<Comment> data_comment_issue = new ArrayList<>();
    private List<Product> data = new ArrayList<>();
    private List<Product> datas = new ArrayList<>();
    private BaseRcyAdapter adapter;
    private String title = "";
    private int count; //下载视频次数
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_collect);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }

    private void initData() {
        key = 0;
        initProgress(this);
//        user_img = LoginInfo.getString(this, "user_img", "");
        user_name = LoginInfo.getString(this, "user_name", "");
        phone = LoginInfo.getString(this, "phone", "");
        title_tv.setText(getResources().getString(R.string.me_collect));
//        tv_user_name.setText(user_name);
//        Glide.with(this).load(user_img).into(img);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    private void initAdapter() {
        recycle.setNestedScrollingEnabled(false);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_6_collect WHERE phone = '" + phone + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data_comment_collect = GsonUtils.getGsonToList(json, Comment.class);
                        if (data_comment_collect.size() == 0) {
                            tv.setText(getResources().getString(R.string.no_collect));
                            tv.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "onSuccess: " + data_comment_collect.size());
                        for (int i = 0; i < data_comment_collect.size(); i++) {
                            String table_name = data_comment_collect.get(i).getTable_name();
                            int table_id = data_comment_collect.get(i).getTable_id();
                            int finalI = i;
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = " + table_id + " ORDER BY id DESC")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String json) {
                                            datas.clear();
                                            datas = GsonUtils.getGsonToList(json, Product.class);
                                            if (datas.size() > 0) {
                                                data.addAll(datas);
                                            }
                                            if (finalI == data_comment_collect.size() - 1) {
                                                for (int i = 0; i < data.size(); i++) {
                                                    EasyHttp.post(IpConfig.URL_SQL)
                                                            .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + table_name + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
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
                                                                    if (key == data.size()) {
//                                                                        Log.e(TAG, "onSuccess3: " + data.size());
                                                                        adapter = new BaseRcyAdapter(data, R.layout.item_person_concern) {
                                                                            @Override
                                                                            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                                                                                CircleImageView img = holder.getView(R.id.img);
                                                                                TextView tv_name = holder.getView(R.id.tv_name);
                                                                                TextView tv_time = holder.getView(R.id.tv_time);
                                                                                TextView tv_content = holder.getView(R.id.tv_content);
                                                                                RelativeLayout relative = holder.getView(R.id.relative);
                                                                                AssNineGridView gridView = holder.getView(R.id.gridView);
                                                                                Button btn_play = holder.getView(R.id.btn_play);
                                                                                ImageView iv = holder.getView(R.id.iv);
                                                                                LoadingCircleView progressCircleBar = holder.getView(R.id.progressCircleBar);
                                                                                if (!"".equals(data.get(position).getUrl())) {
                                                                                    if (data.get(position).getUrl_type() == 1) {
                                                                                        relative.setVisibility(View.GONE);
                                                                                        gridView.setVisibility(View.VISIBLE);
                                                                                        List<ImageInfo> imageInfos = getImageInfos(data, position);
                                                                                        gridView.setAdapter(new AssNineGridViewClickAdapter(PersonCollectActivity.this, imageInfos));
                                                                                    } else {
                                                                                        gridView.setVisibility(View.GONE);
                                                                                        relative.setVisibility(View.VISIBLE);
                                                                                        RequestOptions requestOptions = new RequestOptions()
                                                                                                .centerCrop()
                                                                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                                                .placeholder(R.drawable.all_darkbackground)
                                                                                                .dontAnimate();
                                                                                        Glide.with(PersonCollectActivity.this).load(data.get(position).getUrl()).apply(requestOptions).into(iv);
                                                                                        btn_play.setOnClickListener(PersonCollectActivity.this);
                                                                                        btn_play.setTag(position_all, position);
                                                                                        btn_play.setTag(btn_paly, btn_play);
                                                                                        btn_play.setTag(circleBar, progressCircleBar);
                                                                                    }
                                                                                } else {
                                                                                    gridView.setVisibility(View.GONE);
                                                                                    relative.setVisibility(View.GONE);
                                                                                }
                                                                                Glide.with(PersonCollectActivity.this).load(data.get(position).getUrl3()).into(img);
                                                                                tv_name.setText(data.get(position).getUser_name());
                                                                                tv_time.setText(data.get(position).getDate());
                                                                                tv_content.setText(data.get(position).getContent());
                                                                                TextView tv_like = holder.getView(R.id.tv_like);
                                                                                TextView tv_comment = holder.getView(R.id.tv_comment);
                                                                                Button share = holder.getView(R.id.share);
                                                                                ImageView img_comment = holder.getView(R.id.img_comment);
                                                                                ImageView img_like = holder.getView(R.id.img_like);

                                                                                tv_like.setText(String.valueOf(data.get(position).getLike()));
                                                                                tv_comment.setText(String.valueOf(data.get(position).getComment()));
                                                                                img.setOnClickListener(PersonCollectActivity.this);
                                                                                img.setTag(position_all, position);
                                                                                share.setOnClickListener(PersonCollectActivity.this);
                                                                                share.setTag(position_all, position);
//                                                                                img_comment.setOnClickListener(PersonCollectActivity.this);
//                                                                                img_comment.setTag(position_all, position);
//                                                                                img_comment.setTag(content, tv_content.getText().toString());
//                                                                                img_comment.setTag(count_comment, tv_comment.getText().toString());
//                                                                                img_comment.setTag(view_tv_like, tv_like);
                                                                                if (data.get(position).getLike_person() == 1) {
                                                                                    img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                                                                                } else {
                                                                                    img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                                                                }
                                                                                img_like.setOnClickListener(PersonCollectActivity.this);
                                                                                img_like.setTag(position_all, position);
                                                                                img_like.setTag(view_img_like, img_like);
                                                                                img_like.setTag(view_tv_like, tv_like);
                                                                            }
                                                                        };
                                                                        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
                                                                            @Override
                                                                            public void onItemClick(View view, int position) {
//                                                                                Intent intent = new Intent(PersonCollectActivity.this, PersonIssueActivity.class);
//                                                                                intent.putExtra("user_img", data.get(position).getUrl3());
//                                                                                intent.putExtra("user_name", data.get(position).getUser_name());
//                                                                                intent.putExtra("phone", data.get(position).getPhone());
//                                                                                startActivity(intent);

                                                                                int id = data.get(position).getId();
                                                                                title = data.get(position).getProduct_name();
                                                                                String table_name = data_comment_collect.get(position).getTable_name();
                                                                                String url3 = data.get(position).getUrl3();
                                                                                String user_name = data.get(position).getUser_name();
                                                                                String other_phone = data.get(position).getPhone();
//                                                                                clickImgComment(PersonCollectActivity.this, view, title, id, table_name, url3, user_name, other_phone);
                                                                                String contents = data.get(position).getContent();
                                                                                String count_comments = String.valueOf(data.get(position).getComment());
                                                                                intent = new Intent(PersonCollectActivity.this, ProductDetailActivity.class);
                                                                                intent.putExtra("title", title);
                                                                                intent.putExtra("table_id", id);
                                                                                intent.putExtra("table_name", table_name);
                                                                                intent.putExtra("user_img", url3);
                                                                                intent.putExtra("user_name", user_name);
                                                                                intent.putExtra("other_phone", other_phone);
                                                                                intent.putExtra("content", contents);
                                                                                intent.putExtra("count_comment", count_comments);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                        recycle.setLayoutManager(new LinearLayoutManager(PersonCollectActivity.this));
                                                                        recycle.setAdapter(adapter);
                                                                    }
                                                                }
                                                            });
                                                }

                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    @OnClick({R.id.title_im, R.id.tv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.tv_share:
                share(data, user_name + "的收藏");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(PersonCollectActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;
//            case R.id.img_comment:
//                int id = data.get(position).getId();
//                title = data.get(position).getTable();
//                String table_name = data_comment_collect.get(position).getTable_name();
//                String url3 = data.get(position).getUrl3();
//                String user_name = data.get(position).getUser_name();
//                String other_phone = data.get(position).getPhone();
//                clickImgComment(PersonCollectActivity.this, view, title, id, table_name, url3, user_name, other_phone);
//                break;
            case R.id.img_like:
                int id1 = data.get(position).getId();
                String table_name1 = data_comment_collect.get(position).getTable_name();
                int flag_like = data.get(position).getLike_person();
                if (flag_like == 0) {
                    data.get(position).setLike_person(1);
                } else {
                    data.get(position).setLike_person(0);
                }
                clickImgLike(PersonCollectActivity.this, view, position, id1, table_name1, flag_like);
                break;
            case R.id.share:
                content_pdf = data.get(position).getContent();
                title = data.get(position).getProduct_name();
                String contact = data.get(position).getContact();
                String url3 = data.get(position).getUrl3();
                String other_phone = data.get(position).getPhone_address();
                String url = data.get(position).getUrl();
                int url_type = data.get(position).getUrl_type();
                if ("".equals(url)) {
                    clickShareNoPicture(url3, contact, other_phone, title);
                } else {
                    if (url_type == 1) {
                        clickShare(url3, contact, other_phone, title, position, url);
                    } else {
                        clickShareNoPicture(url3, contact, other_phone, title);
                    }
                }
                break;
            case R.id.btn_play:
                count = 0;
                Button btn_play = (Button) view.getTag(btn_paly);
                LoadingCircleView progressCircleBar = (LoadingCircleView) view.getTag(circleBar);
                String urls = data.get(position).getUrl();
                String name = urls.substring(urls.lastIndexOf("/") + 1);
                String path = IpConfig.PATH_DATA + "video" + File.separator + name;
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        if (!DoubleUtils.isFastDoubleClick()) {
                            Intent intent1 = new Intent(PersonCollectActivity.this, PictureVideoPlayActivity.class);
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
                        if (count == 0) {
                            Util.showToast(PersonCollectActivity.this, "正在加载中...");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    downLoad(position, name, btn_play, progressCircleBar, path);
                                }
                            }, 2500);
                        } else {
                            count = 1;
                            Util.showToast(PersonCollectActivity.this, "播放出错");
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
                                Intent intent = new Intent(PersonCollectActivity.this, PictureVideoPlayActivity.class);
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
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SettingAboutActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SettingAboutActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
