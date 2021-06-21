package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.pwj.classes.CircleImageView;
import com.pwj.classes.LoadingCircleView;
import com.pwj.dialog.DialogChooseMap;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.utils.HttpLog;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PersonIssueActivity extends BaseActivityComment implements View.OnClickListener, TextWatcher {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.tv_count_concern)
    TextView tv_count_concern;
    @BindView(R.id.tv_count_collect)
    TextView tv_count_collect;
    @BindView(R.id.tv_profile)
    TextView tv_profile;
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    @BindView(R.id.tv_no_result)
    TextView tv_no_result;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    private String user_img;
    private String user_name;
    private String phone;
    private String phones = "";
    private List<Comment> data_comment = new ArrayList<>();
    private List<Comment> data_comment_like = new ArrayList<>();
    private List<Product> data = new ArrayList<>();
    private List<Product> data_profile = new ArrayList<>();
    private List<Product> datas = new ArrayList<>();
    private MyAdapter adapter;
    private String profile = "简介:";
    private static final int ITEM_RECRUIT = 0;
    private static final int ITEM_PRODUCT = 1;
    private boolean baiDu;
    private boolean gaoDe;
    private DialogChooseMap dialogChooseMap;
    private List<Integer> count = new ArrayList<>();
    private SparseIntArray map = new SparseIntArray();
    private String content_search = "";
    private List<Product> data_final = new ArrayList<>();
    private final static int btn_paly = 6 << 24;
    private final static int circleBar = 7 << 24;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_issue);
        ButterKnife.bind(this);
        initData();
        initAdapter();
    }

    private void initData() {
        initProgress(this);
        Keyboard.getInstance().hideKeyBoard(et_search);
        et_search.addTextChangedListener(this);
        phones = LoginInfo.getString(this, "phone", "");
        user_img = getIntent().getStringExtra("user_img");
        user_name = getIntent().getStringExtra("user_name");
        phone = getIntent().getStringExtra("phone");
        title_tv.setText(user_name);
        title_relative.setBackground(getDrawable(R.color.white));
        tv_search.setVisibility(View.GONE);
        et_search.setVisibility(View.VISIBLE);
        img_issue.setVisibility(View.GONE);
        Glide.with(this).load(user_img).into(img);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone,profile FROM login WHERE phone = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_profile = GsonUtils.getGsonToList(str, Product.class);
                        profile = profile + data_profile.get(0).getProfile();
                        tv_profile.setText(profile);
                    }
                });
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
    }

    private void initAdapter() {
        key = 0;
        recycle.setNestedScrollingEnabled(false);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_5_concern WHERE phone = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        List<Comment> data_comment_concern = GsonUtils.getGsonToList(json, Comment.class);
                        tv_count_concern.setText("关注:" + String.valueOf(data_comment_concern.size()));
                    }

                });
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_5_concern WHERE phone_concern = " + phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        List<Comment> data_comment_concern = GsonUtils.getGsonToList(json, Comment.class);
                        tv_count_collect.setText("粉丝:" + String.valueOf(data_comment_concern.size()));
                    }

                });
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM comment_1_issue WHERE phone = '" + phone + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data_comment = GsonUtils.getGsonToList(json, Comment.class);

                        for (int i = 0; i < data_comment.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM " + data_comment.get(i).getTable_name() + " WHERE id = " + data_comment.get(i).getTable_id() + " ORDER BY id DESC")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String json) {
                                            datas = GsonUtils.getGsonToList(json, Product.class);
                                            data.addAll(datas);
                                            data_final.addAll(datas);
                                            if (data.size() == data_comment.size()) {
                                                for (int i = 0; i < data.size(); i++) {
                                                    EasyHttp.post(IpConfig.URL_SQL)
                                                            .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = '" + data_comment.get(i).getTable_name() + "' AND  table_id = '" + data.get(i).getId() + "' AND phone = '" + phone + "'")
                                                            .timeStamp(true)
                                                            .execute(new SuccessCallBack<String>() {
                                                                @Override
                                                                public void onSuccess(String s) {
                                                                    data_comment_like.clear();
                                                                    data_comment_like = GsonUtils.getGsonToList(s, Comment.class);
                                                                    if (data_comment_like.size() > 0) {
                                                                        data.get(key).setLike_person(1);
                                                                    } else {
                                                                        data.get(key).setLike_person(0);
                                                                    }
                                                                    key = key + 1;
                                                                    if (key == data.size()) {
                                                                        adapter = new MyAdapter();
                                                                        recycle.setLayoutManager(new LinearLayoutManager(PersonIssueActivity.this));
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

    @OnClick({R.id.title_im, R.id.tv_share, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.tv_share:
                share(data,user_name+"发布的产品信息");
                break;
            case R.id.img_search:
                tv_no_result.setVisibility(View.GONE);
                String keyWords = et_search.getText().toString().trim();
                if (keyWords.length() > 0) {
                    insertKeyWords(keyWords, "他的发布");
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
                            adapter.notifyDataSetChanged();
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
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    Util.showToast(PersonIssueActivity.this, "请输入关键词");
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.tv_content:
                int id = data.get(position).getId();
                String title = data.get(position).getTable();
                String table_name = data_comment.get(position).getTable_name();
                String other_phone = data.get(position).getPhone();
                clickImgComment(PersonIssueActivity.this, view, title, id, table_name, user_img, user_name, other_phone);
                break;
            case R.id.img_comment:
                int id0 = data.get(position).getId();
                String title0 = data.get(position).getTable();
                String table_name0 = data_comment.get(position).getTable_name();
                String other_phone0 = data.get(position).getPhone();
                clickImgComment(PersonIssueActivity.this, view, title0, id0, table_name0, user_img, user_name, other_phone0);
                break;
            case R.id.img_like:
                if (checkPhone(PersonIssueActivity.this)) {
                    int id1 = data.get(position).getId();
                    String table_name1 = data_comment.get(position).getTable_name();
                    int flag_like = data.get(position).getLike_person();
                    if (flag_like == 0) {
                        data.get(position).setLike_person(1);
                    } else {
                        data.get(position).setLike_person(0);
                    }
                    clickImgLike(PersonIssueActivity.this, view, position, id1, table_name1, flag_like);
                }
                break;
            case R.id.share:
                content_pdf = data.get(position).getContent();
                String contact = data.get(position).getContact();
                String title2 = data.get(position).getProduct_name();
                String other_phone2 = data.get(position).getPhone_address();
                String url2 = data.get(position).getUrl();
                int url_type = data.get(position).getUrl_type();
                if ("".equals(url2)) {
                    clickShareNoPicture(user_img, contact, other_phone2, title2);
                } else {
                    if (url_type == 1) {
                        clickShare(user_img, contact, other_phone2, title2, position, url2);
                    } else {
                        clickShareNoPicture(user_img, contact, other_phone2, title2);
                    }
                }
                break;
            case R.id.share_recruit:
                content_pdf = data.get(position).getContent();
                String title3 = data.get(position).getProduct_name();
                String other_phone3 = data.get(position).getPhone();
                clickShareNoPicture(user_img,user_name, other_phone3, title3);
                break;
            case R.id.go:
                selectMap(position);
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
                            Intent intent1 = new Intent(PersonIssueActivity.this, PictureVideoPlayActivity.class);
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
                        Util.showToast(PersonIssueActivity.this, "播放出错");
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
                                Intent intent = new Intent(PersonIssueActivity.this, PictureVideoPlayActivity.class);
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
        dialogChooseMap = new DialogChooseMap(PersonIssueActivity.this, baiDu, gaoDe, R.style.dialog_choose, new DialogChooseMap.ICustomDialogEventListener() {
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
        WindowManager windowManager = PersonIssueActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogChooseMap.getWindow().setAttributes(lp);
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
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_RECRUIT) {
                View view = LayoutInflater.from(PersonIssueActivity.this).inflate(R.layout.item_recruit_issue, parent, false);
                Recruit_ViewHolder recruit_viewHolder = new Recruit_ViewHolder(view);
                return recruit_viewHolder;
            } else if (viewType == ITEM_PRODUCT) {
                View view = LayoutInflater.from(PersonIssueActivity.this).inflate(R.layout.item_person_concern, parent, false);
                Product_ViewHolder product_viewHolder = new Product_ViewHolder(view);
                return product_viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Recruit_ViewHolder) {
                Glide.with(PersonIssueActivity.this).load(user_img).into(((Recruit_ViewHolder) holder).img);
                ((Recruit_ViewHolder) holder).tv_table.setText(data.get(position).getProduct_name());
                ((Recruit_ViewHolder) holder).tv_time.setText(data.get(position).getDate());
                ((Recruit_ViewHolder) holder).tv_content.setText(data.get(position).getContent());
                ((Recruit_ViewHolder) holder).share_recruit.setOnClickListener(PersonIssueActivity.this);
                ((Recruit_ViewHolder) holder).share_recruit.setTag(position_all, position);
                ((Recruit_ViewHolder) holder).go.setOnClickListener(PersonIssueActivity.this);
                ((Recruit_ViewHolder) holder).go.setTag(position_all, position);
            } else if (holder instanceof Product_ViewHolder) {
                if ("".equals(data.get(position).getUrl())) {
                    ((Product_ViewHolder) holder).gridView.setVisibility(View.GONE);
                    ((Product_ViewHolder) holder).relative.setVisibility(View.GONE);
                } else {
                    if (data.get(position).getUrl_type() == 1) {
                        ((Product_ViewHolder) holder).relative.setVisibility(View.GONE);
                        ((Product_ViewHolder) holder).gridView.setVisibility(View.VISIBLE);
                        List<ImageInfo> imageInfos = getImageInfos(data, position);
                        ((Product_ViewHolder) holder).gridView.setAdapter(new AssNineGridViewClickAdapter(PersonIssueActivity.this, imageInfos));
                    } else {
                        ((Product_ViewHolder) holder).relative.setVisibility(View.VISIBLE);
                        ((Product_ViewHolder) holder).gridView.setVisibility(View.GONE);
                        RequestOptions requestOptions = new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.all_darkbackground)
                                .dontAnimate();
                        Glide.with(PersonIssueActivity.this).load(data.get(position).getUrl()).apply(requestOptions).into(((Product_ViewHolder) holder).iv);
                        ((Product_ViewHolder) holder).btn_play.setOnClickListener(PersonIssueActivity.this);
                        ((Product_ViewHolder) holder).btn_play.setTag(position_all, position);
                        ((Product_ViewHolder) holder).btn_play.setTag(btn_paly, ((Product_ViewHolder) holder).btn_play);
                        ((Product_ViewHolder) holder).btn_play.setTag(circleBar, ((Product_ViewHolder) holder).progressCircleBar);
                    }
                }
                Glide.with(PersonIssueActivity.this).load(user_img).into(((Product_ViewHolder) holder).img);
                ((Product_ViewHolder) holder).tv_table.setText(data.get(position).getProduct_name());
                ((Product_ViewHolder) holder).tv_time.setText(data.get(position).getDate());
                ((Product_ViewHolder) holder).tv_content.setText(data.get(position).getContent());
                ((Product_ViewHolder) holder).tv_like.setText(String.valueOf(data.get(position).getLike()));
                ((Product_ViewHolder) holder).tv_comment.setText(String.valueOf(data.get(position).getComment()));
                ((Product_ViewHolder) holder).share.setOnClickListener(PersonIssueActivity.this);
                ((Product_ViewHolder) holder).share.setTag(position_all, position);

                ((Product_ViewHolder) holder).tv_content.setOnClickListener(PersonIssueActivity.this);
                ((Product_ViewHolder) holder).tv_content.setTag(position_all, position);
                ((Product_ViewHolder) holder).tv_content.setTag(content, ((Product_ViewHolder) holder).tv_content.getText().toString());
                ((Product_ViewHolder) holder).tv_content.setTag(count_comment, ((Product_ViewHolder) holder).tv_comment.getText().toString());
                ((Product_ViewHolder) holder).tv_content.setTag(view_tv_like, ((Product_ViewHolder) holder).tv_like);
                ((Product_ViewHolder) holder).img_comment.setOnClickListener(PersonIssueActivity.this);
                ((Product_ViewHolder) holder).img_comment.setTag(position_all, position);
                ((Product_ViewHolder) holder).img_comment.setTag(content, ((Product_ViewHolder) holder).tv_content.getText().toString());
                ((Product_ViewHolder) holder).img_comment.setTag(count_comment, ((Product_ViewHolder) holder).tv_comment.getText().toString());
                ((Product_ViewHolder) holder).img_comment.setTag(view_tv_like, ((Product_ViewHolder) holder).tv_like);
                if (!"".equals(phones)) {
                    if (data.get(position).getLike_person() == 1) {
                        ((Product_ViewHolder) holder).img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                    } else {
                        ((Product_ViewHolder) holder).img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                    }
                } else {
                    ((Product_ViewHolder) holder).img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                }
                ((Product_ViewHolder) holder).img_like.setOnClickListener(PersonIssueActivity.this);
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
            private TextView tv_table;
            private TextView tv_time;
            private TextView tv_content;
            private Button share_recruit;
            private Button go;

            public Recruit_ViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
                tv_table = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                share_recruit = itemView.findViewById(R.id.share_recruit);
                go = itemView.findViewById(R.id.go);
            }
        }

        class Product_ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView img;
            private TextView tv_table;
            private TextView tv_time;
            private TextView tv_content;
            private AssNineGridView gridView;
            private TextView tv_like;
            private TextView tv_comment;
            private Button share;
            private ImageView img_comment;
            private ImageView img_like;
            private RelativeLayout relative;
            private ImageView iv;
            private Button btn_play;
            private LoadingCircleView progressCircleBar;

            public Product_ViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
                tv_table = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                gridView = itemView.findViewById(R.id.gridView);
                tv_like = itemView.findViewById(R.id.tv_like);
                tv_comment = itemView.findViewById(R.id.tv_comment);
                share = itemView.findViewById(R.id.share);
                img_comment = itemView.findViewById(R.id.img_comment);
                img_like = itemView.findViewById(R.id.img_like);
                relative = itemView.findViewById(R.id.relative);
                iv = itemView.findViewById(R.id.iv);
                btn_play = itemView.findViewById(R.id.btn_play);
                progressCircleBar = itemView.findViewById(R.id.progressCircleBar);
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
