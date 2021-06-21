package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.pwj.base.BaseActivityComment;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.classes.CircleImageView;
import com.pwj.classes.LoadingCircleView;
import com.pwj.dialog.DialogChooseMap;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


public class PersonConcernActivity extends BaseActivityComment implements View.OnClickListener {
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
    private List<Comment> data_comment_concern = new ArrayList<>();
    private List<Comment> data_comment_issue = new ArrayList<>();
    private List<Product> data = new ArrayList<>();
    private List<Product> datas = new ArrayList<>();
    private MyAdapter adapter;
    private String title = "";
    private static final int ITEM_RECRUIT = 0;
    private static final int ITEM_PRODUCT = 1;
    private boolean baiDu;
    private boolean gaoDe;
    private DialogChooseMap dialogChooseMap;
    private int count; //下载视频次数
    private int invalid = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_concern);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }

    private void initData() {
        key = 0;
        initProgress(this);
//        user_img = LoginInfo.getString(this, "user_img", "");
//        user_name = LoginInfo.getString(this, "user_name", "");
        phone = LoginInfo.getString(this, "phone", "");
        title_tv.setText(getResources().getString(R.string.me_concern));
//        tv_user_name.setText(user_name);
//        Glide.with(this).load(user_img).into(img);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    private void initAdapter() {
        invalid = 0;
        recycle.setNestedScrollingEnabled(false);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_5_concern WHERE phone = '" + phone + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data_comment_concern = GsonUtils.getGsonToList(json, Comment.class);
                        if (data_comment_concern.size() == 0) {
                            tv.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "onSuccess: " + data_comment_concern.size());
                        for (int i = 0; i < data_comment_concern.size(); i++) {
                            int final_number = i;
                            String phone_concern = data_comment_concern.get(i).getPhone_concern();
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_1_issue WHERE phone = '" + phone_concern + "' ORDER BY id DESC")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String json) {
                                            data_comment_issue = GsonUtils.getGsonToList(json, Comment.class);
                                            Log.e("长度", "onSuccess: " + data_comment_issue.size());
                                            if (data_comment_issue.size() > 0) {
                                                int table_id = data_comment_issue.get(0).getTable_id();
                                                String table_name = data_comment_issue.get(0).getTable_name();
                                                EasyHttp.post(IpConfig.URL_SQL)
                                                        .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = " + table_id + " ORDER BY id DESC")
                                                        .timeStamp(true)
                                                        .execute(new SuccessCallBack<String>() {
                                                            @Override
                                                            public void onSuccess(String json) {
                                                                datas.clear();
                                                                datas = GsonUtils.getGsonToList(json, Product.class);
                                                                if (datas.size() > 0) {
                                                                    datas.get(0).setTable_name(table_name);
                                                                    data.addAll(datas);
                                                                }
                                                                Log.e("onSuccess", "data--"+data.size()+"--final_number"+final_number);
                                                                if (invalid == data_comment_concern.size()-data.size()) {
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
//                                                                                            adapter = new BaseRcyAdapter(data, R.layout.item_person_concern) {
//                                                                                                @Override
//                                                                                                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//                                                                                                    List<ImageInfo> imageInfos = getImageInfos(data, position);
//                                                                                                    CircleImageView img = holder.getView(R.id.img);
//                                                                                                    TextView tv_name = holder.getView(R.id.tv_name);
//                                                                                                    TextView tv_time = holder.getView(R.id.tv_time);
//                                                                                                    TextView tv_content = holder.getView(R.id.tv_content);
//                                                                                                    AssNineGridView gridView = holder.getView(R.id.gridView);
//                                                                                                    Glide.with(PersonConcernActivity.this).load(data.get(position).getUrl3()).into(img);
//                                                                                                    tv_name.setText(data.get(position).getUser_name());
//                                                                                                    tv_time.setText(data.get(position).getDate());
//                                                                                                    tv_content.setText(data.get(position).getContent());
//                                                                                                    gridView.setAdapter(new AssNineGridViewClickAdapter(PersonConcernActivity.this, imageInfos));
//
//                                                                                                    TextView tv_like = holder.getView(R.id.tv_like);
//                                                                                                    TextView tv_comment = holder.getView(R.id.tv_comment);
//                                                                                                    Button share = holder.getView(R.id.share);
//                                                                                                    ImageView img_comment = holder.getView(R.id.img_comment);
//                                                                                                    ImageView img_like = holder.getView(R.id.img_like);
//
//                                                                                                    tv_like.setText(String.valueOf(data.get(position).getLike()));
//                                                                                                    tv_comment.setText(String.valueOf(data.get(position).getComment()));
//                                                                                                    img.setOnClickListener(PersonConcernActivity.this);
//                                                                                                    img.setTag(position_all, position);
//                                                                                                    share.setOnClickListener(PersonConcernActivity.this);
//                                                                                                    share.setTag(position_all, position);
//                                                                                                    img_comment.setOnClickListener(PersonConcernActivity.this);
//                                                                                                    img_comment.setTag(position_all, position);
//                                                                                                    img_comment.setTag(content, tv_content.getText().toString());
//                                                                                                    img_comment.setTag(count_comment, tv_comment.getText().toString());
//                                                                                                    img_comment.setTag(view_tv_like, tv_like);
//                                                                                                    if (data.get(position).getLike_person() == 1) {
//                                                                                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
//                                                                                                    } else {
//                                                                                                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
//                                                                                                    }
//                                                                                                    img_like.setOnClickListener(PersonConcernActivity.this);
//                                                                                                    img_like.setTag(position_all, position);
//                                                                                                    img_like.setTag(view_img_like, img_like);
//                                                                                                    img_like.setTag(view_tv_like, tv_like);
//                                                                                                }
//                                                                                            };
//                                                                                            adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
//                                                                                                @Override
//                                                                                                public void onItemClick(View view, int position) {
//                                                                                                    Intent intent = new Intent(PersonConcernActivity.this, PersonIssueActivity.class);
//                                                                                                    intent.putExtra("user_img", data.get(position).getUrl3());
//                                                                                                    intent.putExtra("user_name", data.get(position).getUser_name());
//                                                                                                    intent.putExtra("phone", data.get(position).getPhone());
//                                                                                                    startActivity(intent);
//                                                                                                }
//                                                                                            });
                                                                                            adapter = new MyAdapter();
                                                                                            recycle.setLayoutManager(new LinearLayoutManager(PersonConcernActivity.this));
                                                                                            recycle.setAdapter(adapter);
//                                                                                            recycle.setLayoutManager(new LinearLayoutManager(PersonConcernActivity.this));
//                                                                                            recycle.setAdapter(adapter);
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            }
                                                        });
                                            } else {
                                                invalid = invalid+1;
                                                if (final_number == data_comment_concern.size()) {
                                                    for (int i = 0; i < data.size(); i++) {
                                                        EasyHttp.post(IpConfig.URL_SQL)
                                                                .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + data.get(i).getTable_name() + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
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
                                                                            adapter = new MyAdapter();
                                                                            recycle.setLayoutManager(new LinearLayoutManager(PersonConcernActivity.this));
                                                                            recycle.setAdapter(adapter);
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.tv_content:
                intent = new Intent(PersonConcernActivity.this, PersonIssueActivity.class);
                intent.putExtra("user_img", data.get(position).getUrl3());
                intent.putExtra("user_name", data.get(position).getUser_name());
                intent.putExtra("phone", data.get(position).getPhone());
                startActivity(intent);
//                int id = data.get(position).getId();
//                String title = data.get(position).getTable();
//                String table_name = data_comment.get(position).getTable_name();
//                String other_phone = data.get(position).getPhone();
//                clickImgComment(PersonConcernActivity.this, view, title, id, table_name, user_img, user_name, other_phone);
                break;
            case R.id.img:
                Intent intent = new Intent(PersonConcernActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                startActivity(intent);
                break;
            case R.id.img_comment:
                int id0 = data.get(position).getId();
                title = data.get(position).getProduct_name();
                String table_name0 = data.get(position).getTable_name();
                String url0 = data.get(position).getUrl3();
                user_name = data.get(position).getUser_name();
                String other_phone0 = data.get(position).getPhone();
                clickImgComment(PersonConcernActivity.this, view, title, id0, table_name0, url0, user_name, other_phone0);
                break;
            case R.id.img_like:
                int id1 = data.get(position).getId();
                String table_name1 = data.get(position).getTable_name();
                int flag_like = data.get(position).getLike_person();
                if (flag_like == 0) {
                    data.get(position).setLike_person(1);
                } else {
                    data.get(position).setLike_person(0);
                }
                clickImgLike(PersonConcernActivity.this, view, position, id1, table_name1, flag_like);
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
            case R.id.share_recruit:
                content_pdf = data.get(position).getContent();
                title = data.get(position).getProduct_name();
                String url4 = data.get(position).getUrl3();
                String user_name3 = data.get(position).getUser_name();
                String other_phone3 = data.get(position).getPhone();
                clickShareNoPicture(url4, user_name3, other_phone3, title);
                break;
            case R.id.go:
                selectMap(position);
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
                            Intent intent1 = new Intent(PersonConcernActivity.this, PictureVideoPlayActivity.class);
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
                            Util.showToast(PersonConcernActivity.this, "正在加载中...");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    downLoad(position, name, btn_play, progressCircleBar, path);
                                }
                            }, 2500);
                        } else {
                            count = 1;
                            Util.showToast(PersonConcernActivity.this, "播放出错");
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
                                Intent intent = new Intent(PersonConcernActivity.this, PictureVideoPlayActivity.class);
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

    private void selectMap(int position) {
        double com_longitude = data.get(position).getLongitude();
        double com_latitude = data.get(position).getLatitude();
        String end_location = data.get(position).getLocation();
        checkMap();
        dialogChooseMap = new DialogChooseMap(PersonConcernActivity.this, baiDu, gaoDe, R.style.dialog_choose, new DialogChooseMap.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                switch (id) {
                    case R.id.dialog_bai_du:
                        Intent intent1 = null;
                        try {
                            intent1 = Intent.getIntent("intent://map/direction?origin=latlng:" + null + "," + null + "|name:" + "请选择起始位置" + "&destination=latlng:" + com_latitude + "," + com_longitude + "|name:" + end_location + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent1);
//                            Log.e("起点经纬度", "my_longitude: " + my_longitude + " my_latitude" + my_latitude);
                        break;
                    case R.id.dialog_gao_de:
                        Intent intent2 = null;
                        try {
                            intent2 = Intent.getIntent("androidamap://navi?sourceApplication=appname&poiname=" + end_location + "&lat=" +
                                    com_latitude + "&lon=" + com_longitude + "&dev=1&style=2");
//                                intent2 = Intent.getIntent("androidamap://route?sourceApplication=softname" + "&sname=" + "请选择起点" + "&dname=" + end_location + "&dev=0&m=0&t=1");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent2);
                        break;
                }
            }
        });
        dialogChooseMap.show();
        full_choose_map();
    }

    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void checkMap() {
        if (isInstallByread("com.baidu.BaiduMap")) {
            baiDu = true;
        } else {
            baiDu = false;
        }
        if (isInstallByread("com.autonavi.minimap")) {
            gaoDe = true;
        } else {
            gaoDe = false;
        }
    }

    //设置弹窗宽度
    private void full_choose_map() {
        WindowManager windowManager = PersonConcernActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogChooseMap.getWindow().setAttributes(lp);
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_RECRUIT) {
                View view = LayoutInflater.from(PersonConcernActivity.this).inflate(R.layout.item_recruit_issue, parent, false);
                Recruit_ViewHolder recruit_viewHolder = new Recruit_ViewHolder(view);
                return recruit_viewHolder;
            } else if (viewType == ITEM_PRODUCT) {
                View view = LayoutInflater.from(PersonConcernActivity.this).inflate(R.layout.item_person_concern, parent, false);
                Product_ViewHolder product_viewHolder = new Product_ViewHolder(view);
                return product_viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String img_url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            if (holder instanceof Recruit_ViewHolder) {
                Glide.with(PersonConcernActivity.this).load(img_url).into(((Recruit_ViewHolder) holder).img);
                ((Recruit_ViewHolder) holder).tv_name.setText(user_name);
                ((Recruit_ViewHolder) holder).tv_time.setText(data.get(position).getDate());
                ((Recruit_ViewHolder) holder).tv_content.setText(data.get(position).getContent());
                ((Recruit_ViewHolder) holder).share_recruit.setOnClickListener(PersonConcernActivity.this);
                ((Recruit_ViewHolder) holder).share_recruit.setTag(position_all, position);
                ((Recruit_ViewHolder) holder).go.setOnClickListener(PersonConcernActivity.this);
                ((Recruit_ViewHolder) holder).go.setTag(position_all, position);
                ((Recruit_ViewHolder) holder).tv_content.setOnClickListener(PersonConcernActivity.this);
                ((Recruit_ViewHolder) holder).tv_content.setTag(position_all, position);
                ((Recruit_ViewHolder) holder).tv_content.setTag(content, ((Recruit_ViewHolder) holder).tv_content.getText().toString());
            } else if (holder instanceof Product_ViewHolder) {
                if (!"".equals(data.get(position).getUrl())) {
                    if (data.get(position).getUrl_type() == 1) {
                        ((Product_ViewHolder) holder).relative.setVisibility(View.GONE);
                        ((Product_ViewHolder) holder).gridView.setVisibility(View.VISIBLE);
                        List<ImageInfo> imageInfos = getImageInfos(data, position);
                        ((Product_ViewHolder) holder).gridView.setAdapter(new AssNineGridViewClickAdapter(PersonConcernActivity.this, imageInfos));
                    } else {
                        ((Product_ViewHolder) holder).gridView.setVisibility(View.GONE);
                        ((Product_ViewHolder) holder).relative.setVisibility(View.VISIBLE);
                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.all_darkbackground)
                                .dontAnimate();
                        Glide.with(PersonConcernActivity.this).load(data.get(position).getUrl()).apply(requestOptions).into(((Product_ViewHolder) holder).iv);
                        ((Product_ViewHolder) holder).btn_play.setOnClickListener(PersonConcernActivity.this);
                        ((Product_ViewHolder) holder).btn_play.setTag(position_all, position);
                        ((Product_ViewHolder) holder).btn_play.setTag(btn_paly, ((Product_ViewHolder) holder).btn_play);
                        ((Product_ViewHolder) holder).btn_play.setTag(circleBar, ((Product_ViewHolder) holder).progressCircleBar);
                    }
                } else {
                    ((Product_ViewHolder) holder).gridView.setVisibility(View.GONE);
                    ((Product_ViewHolder) holder).relative.setVisibility(View.GONE);
                }
                Glide.with(PersonConcernActivity.this).load(img_url).into(((Product_ViewHolder) holder).img);
                ((Product_ViewHolder) holder).tv_name.setText(user_name);
                ((Product_ViewHolder) holder).tv_time.setText(data.get(position).getDate());
                ((Product_ViewHolder) holder).tv_content.setText(data.get(position).getContent());
                ((Product_ViewHolder) holder).tv_like.setText(String.valueOf(data.get(position).getLike()));
                ((Product_ViewHolder) holder).tv_comment.setText(String.valueOf(data.get(position).getComment()));
                ((Product_ViewHolder) holder).share.setOnClickListener(PersonConcernActivity.this);
                ((Product_ViewHolder) holder).share.setTag(position_all, position);
                ((Product_ViewHolder) holder).tv_content.setOnClickListener(PersonConcernActivity.this);
                ((Product_ViewHolder) holder).tv_content.setTag(position_all, position);
                ((Product_ViewHolder) holder).tv_content.setTag(content, ((Product_ViewHolder) holder).tv_content.getText().toString());
                ((Product_ViewHolder) holder).tv_content.setTag(count_comment, ((Product_ViewHolder) holder).tv_comment.getText().toString());
                ((Product_ViewHolder) holder).tv_content.setTag(view_tv_like, ((Product_ViewHolder) holder).tv_like);
                ((Product_ViewHolder) holder).img_comment.setOnClickListener(PersonConcernActivity.this);
                ((Product_ViewHolder) holder).img_comment.setTag(position_all, position);
                ((Product_ViewHolder) holder).img_comment.setTag(content, ((Product_ViewHolder) holder).tv_content.getText().toString());
                ((Product_ViewHolder) holder).img_comment.setTag(count_comment, ((Product_ViewHolder) holder).tv_comment.getText().toString());
                ((Product_ViewHolder) holder).img_comment.setTag(view_tv_like, ((Product_ViewHolder) holder).tv_like);
                if (data.get(position).getLike_person() == 1) {
                    ((Product_ViewHolder) holder).img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                } else {
                    ((Product_ViewHolder) holder).img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                }
                ((Product_ViewHolder) holder).img_like.setOnClickListener(PersonConcernActivity.this);
                ((Product_ViewHolder) holder).img_like.setTag(position_all, position);
                ((Product_ViewHolder) holder).img_like.setTag(view_img_like, ((Product_ViewHolder) holder).img_like);
                ((Product_ViewHolder) holder).img_like.setTag(view_tv_like, ((Product_ViewHolder) holder).tv_like);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position).getNumber() == 100 ? ITEM_RECRUIT : ITEM_PRODUCT;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class Recruit_ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView img;
            private TextView tv_name;
            private TextView tv_time;
            private TextView tv_content;
            private Button share_recruit;
            private Button go;

            public Recruit_ViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                share_recruit = itemView.findViewById(R.id.share_recruit);
                go = itemView.findViewById(R.id.go);
            }
        }

        class Product_ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView img;
            private RelativeLayout relative;
            private ImageView iv;
            private Button btn_play;
            private LoadingCircleView progressCircleBar;
            private TextView tv_name;
            private TextView tv_time;
            private TextView tv_content;
            private AssNineGridView gridView;
            private TextView tv_like;
            private TextView tv_comment;
            private Button share;
            private ImageView img_comment;
            private ImageView img_like;

            public Product_ViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
                relative = itemView.findViewById(R.id.relative);
                iv = itemView.findViewById(R.id.iv);
                btn_play = itemView.findViewById(R.id.btn_play);
                progressCircleBar = itemView.findViewById(R.id.progressCircleBar);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                gridView = itemView.findViewById(R.id.gridView);
                tv_like = itemView.findViewById(R.id.tv_like);
                tv_comment = itemView.findViewById(R.id.tv_comment);
                share = itemView.findViewById(R.id.share);
                img_comment = itemView.findViewById(R.id.img_comment);
                img_like = itemView.findViewById(R.id.img_like);
            }
        }

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
