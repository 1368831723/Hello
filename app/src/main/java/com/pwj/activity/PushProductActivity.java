package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
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


public class PushProductActivity extends BaseActivityComment{
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.push_tv_no)
    TextView push_tv_no;
    @BindView(R.id.push_linear)
    LinearLayout push_linear;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.relative)
    RelativeLayout relative;
    @BindView(R.id.gridView)
    AssNineGridView gridView;
    @BindView(R.id.btn_play)
    Button btn_play;
    @BindView(R.id.progressCircleBar)
    LoadingCircleView progressCircleBar;
    @BindView(R.id.iv)
    ImageView iv;
    private String user_img = "";
    private String user_name = "";
    private List<Product> data = new ArrayList<>();
    private String table_name ;
    private String table_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_push_product);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        initProgress(this);
        phone = getIntent().getStringExtra("phone");
        table_name = getIntent().getStringExtra("table_name");
        table_id = getIntent().getStringExtra("table_id");
        title_tv.setText("抛丸机助手");
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = " + table_id + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data = GsonUtils.getGsonToList(json, Product.class);
                        if (data.size()==0){
                            push_tv_no.setVisibility(View.VISIBLE);
                            push_linear.setVisibility(View.GONE);
                            return;
                        }
                        if (!"".equals(data.get(0).getUrl())) {
                            if (data.get(0).getUrl_type() == 1) {
                                relative.setVisibility(View.GONE);
                                gridView.setVisibility(View.VISIBLE);
                                List<ImageInfo> imageInfos = getImageInfos(data, 0);
                                gridView.setAdapter(new AssNineGridViewClickAdapter(PushProductActivity.this, imageInfos));
                            } else {
                                gridView.setVisibility(View.GONE);
                                relative.setVisibility(View.VISIBLE);
                                RequestOptions requestOptions = new RequestOptions()
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.all_darkbackground)
                                        .dontAnimate();
                                Glide.with(PushProductActivity.this).load(data.get(0).getUrl()).apply(requestOptions).into(iv);
//                                                btn_play.setOnClickListener(PushProductActivity.this);
                            }
                        } else {
                            gridView.setVisibility(View.GONE);
                            relative.setVisibility(View.GONE);
                        }
                        Glide.with(PushProductActivity.this).load(data.get(0).getUrl3()).into(img);
                        name_tv.setText(data.get(0).getUser_name());
                        tv_time.setText(data.get(0).getDate());
                        tv.setText(data.get(0).getContent());
                    }
                });

    }


    @OnClick({R.id.title_im,R.id.img, R.id.btn_play,R.id.img_vx,R.id.share,R.id.img_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img:
                intent = new Intent(PushProductActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(0).getUser_name());
                intent.putExtra("other_phone", data.get(0).getPhone());
                intent.putExtra("other_img", data.get(0).getUrl3());
                startActivity(intent);
                break;
            case R.id.btn_play:
                String urls = data.get(0).getUrl();
                String name = urls.substring(urls.lastIndexOf("/") + 1);
                String path = IpConfig.PATH_DATA + "video" + File.separator + name;
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        if (!DoubleUtils.isFastDoubleClick()) {
                            Intent intent1 = new Intent(PushProductActivity.this, PictureVideoPlayActivity.class);
                            intent1.putExtra("video_path", path);
                            startActivity(intent1);
                        }
                    } else {
                        downLoad(0, name, btn_play, progressCircleBar, path);
                    }
                } catch (Exception e) {

                }
                break;
            case R.id.img_vx:
                String other_phone1 = data.get(0).getPhone();
                String other_name = data.get(0).getUser_name();
                String other_img = data.get(0).getUrl3();
                startChat(other_phone1,other_name, other_img);
                break;
            case R.id.share:
                content_pdf = data.get(0).getContent();
                String contact = data.get(0).getContact();
                String url3 = data.get(0).getUrl3();
                String title2 = data.get(0).getProduct_name();
                String other_phone2 = data.get(0).getPhone_address();
                String url2 = data.get(0).getUrl();
                int url_type = data.get(0).getUrl_type();
                if ("".equals(url2)) {
                    clickShareNoPicture(url3, contact, other_phone2, title2);
                } else {
                    if (url_type == 1) {
                        clickShare(url3, contact, other_phone2, title2, 0, url2);
                    } else {
                        clickShareNoPicture(url3, contact, other_phone2, title2);
                    }
                }
                break;
            case R.id.img_comment:
                String title0 = data.get(0).getTable();
                user_img = data.get(0).getUrl3();
                user_name = data.get(0).getUser_name();
                intent = new Intent(PushProductActivity.this, ProductDetailActivity.class);
                intent.putExtra("title", title0);
                intent.putExtra("table_id", Integer.parseInt(table_id));
                intent.putExtra("table_name", table_name);
                intent.putExtra("user_img", user_img);
                intent.putExtra("user_name", user_name);
                intent.putExtra("other_phone", phone);
                intent.putExtra("content", data.get(0).getContent());
                intent.putExtra("count_comment", String.valueOf(data.get(0).getComment()));
                startActivity(intent);
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
                        Util.showToast(PushProductActivity.this, "播放出错");
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
                                Intent intent = new Intent(PushProductActivity.this, PictureVideoPlayActivity.class);
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
