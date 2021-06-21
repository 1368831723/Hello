package com.pwj.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.pwj.BaseActivity;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Comment;
import com.pwj.bean.Login;
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Keyboard;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;
import com.ufreedom.uikit.FloatingText;
import com.zhouyou.http.EasyHttp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mlxy.utils.S;

import static android.content.ContentValues.TAG;


public class AdDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_concern)
    TextView tv_concern;
    @BindView(R.id.tv_concern_ready)
    TextView tv_concern_ready;
    @BindView(R.id.tv_collect)
    TextView tv_collect;
    @BindView(R.id.tv_collect_ready)
    TextView tv_collect_ready;
    @BindView(R.id.img_user)
    ImageView img_user;
    @BindView(R.id.tv_user)
    TextView tv_user;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.content_iv1)
    ImageView content_iv1;
    @BindView(R.id.content_iv2)
    ImageView content_iv2;
    @BindView(R.id.content_iv3)
    ImageView content_iv3;
    @BindView(R.id.tv_comment_all)
    TextView tv_comment_all;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.et_comment)
    EditText et_comment;
    private String title;
    private String phone;
    private String other_phone;
    private int table_id;
    private String table_name;
    private String time;
    private String user_url;
    private String user_name;
    private String content;
    private String comment;
    private String count_comment;
    private SimpleDateFormat sdf;
    private List<Product> data_list = new ArrayList<>();
    private List<Comment> data = new ArrayList<>();
    private List<Comment> data_concern = new ArrayList<>();
    private List<Comment> data_collect = new ArrayList<>();
    private List<Comment> data_comment_person = new ArrayList<>();
    private BaseRcyAdapter adapter;
    private TextView tv_name;
    private final static int position_all = 5 << 24;
    private final static int view_tv_like = 8 << 24;
    private final static int view_img_like = 9 << 24;
    private SparseIntArray like_map = new SparseIntArray();
    private int key;
    private int flag_like = 0;
    private String urlsss;
    private String namesss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_ad_detail);
        ButterKnife.bind(this);
        initData();
        insertSql();
    }

    private void insertSql() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "UPDATE app SET `number`= `number`+1")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });
    }

    private void initData() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        title = "";
        phone = LoginInfo.getString(this, "phone", "");
        table_id = 3;
        table_name = ProductTableName.product2;
//        user_url = "http://paowan.com.cn/upload/1580723963873.PNG";
//        user_name = "江苏英达机械有限公司";
        other_phone = "15195688008";
//        content = getIntent().getStringExtra("content");
//        count_comment = getIntent().getStringExtra("count_comment");
        title_tv.setText(title);
        initDatas();
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = 3")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_list = GsonUtils.getGsonToList(str, Product.class);
                        if (data_list.size() < 1) {
                            return;
                        }
                        tv_user.setText(data_list.get(0).getUser_name());
                        tv_content.setText(data_list.get(0).getContent());

                        Pattern pattern = Pattern.compile("\\d{11,}");
                        Linkify.addLinks(tv_content, pattern, "tel:");
                        tv_comment_all.setText(data_list.get(0).getComment()+"");
                        Glide.with(AdDetailActivity.this).load(data_list.get(0).getUrl3()).into(img_user);
                        Glide.with(AdDetailActivity.this).load(getImageInfos(0)).into(content_iv1);
                        Glide.with(AdDetailActivity.this).load(getImageInfos(1)).into(content_iv2);
                        Glide.with(AdDetailActivity.this).load(getImageInfos(2)).into(content_iv3);
                    }
                });
    }
    private String getImageInfos(int position) {
        List<String> uri_list = Arrays.asList(data_list.get(0).getUrl().split(","));
        return uri_list.get(position);
    }
    private void initDatas() {
        recycle.setNestedScrollingEnabled(false);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM  comment_5_concern WHERE `phone` = " + phone + " AND phone_concern = " + other_phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_concern = GsonUtils.getGsonToList(str, Comment.class);

                        if (data_concern.size() > 0) {
                            tv_concern.setVisibility(View.INVISIBLE);
                            tv_concern_ready.setVisibility(View.VISIBLE);
                        } else {

                        }
                    }
                });

        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM  comment_6_collect WHERE `phone` = " + phone + " AND `table_id` = " + table_id + " AND `table_name` = '" + table_name + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data_concern = GsonUtils.getGsonToList(str, Comment.class);
                        if (data_concern.size() > 0) {
                            tv_collect.setVisibility(View.INVISIBLE);
                            tv_collect_ready.setVisibility(View.VISIBLE);
                        } else {

                        }
                    }
                });
        queryDataComment();
    }

    private void queryDataComment() {
        String sqls = "SELECT login.url3,login.user_name,comment_2_comment.* FROM login,comment_2_comment WHERE login.phone=comment_2_comment.phone AND table_name = '" + table_name + "' AND table_id = '" + table_id + "' ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name,comment_2_comment.* FROM login,comment_2_comment WHERE login.phone=comment_2_comment.phone AND table_name = '" + table_name + "' AND table_id = '" + table_id + "' ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data.clear();
                        data.addAll(GsonUtils.getGsonToList(json, Comment.class));

                        if ("".equals(phone)) {
                            initAdapter();
                            return;
                        }
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_4_like_person WHERE phone = " + phone + " AND  comment_id = '" + data.get(i).getId() + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment_person.clear();
                                            data_comment_person = GsonUtils.getGsonToList(s, Comment.class);
                                            if (data_comment_person.size() > 0) {
                                                data.get(key).setLike_personal_comment(1);
                                                like_map.put(key, 1);
                                            } else {
                                                data.get(key).setLike_personal_comment(0);
                                                like_map.put(key, 0);
                                            }
                                            key = key + 1;
                                            if (key == data.size()) {
                                                initAdapter();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void initAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new BaseRcyAdapter(data, R.layout.item_product_comment) {
                @Override
                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                    CircleImageView img = holder.getView(R.id.img);
                    TextView tv_name = holder.getView(R.id.tv_name);
                    TextView tv_like = holder.getView(R.id.tv_like);
                    ImageView img_like = holder.getView(R.id.img_like);
                    TextView tv_comment = holder.getView(R.id.tv_comment);
                    TextView tv_time = holder.getView(R.id.tv_time);
                    Glide.with(AdDetailActivity.this).load(data.get(position).getUrl3()).into(img);
                    tv_name.setText(data.get(position).getUser_name());
                    tv_like.setText(String.valueOf(data.get(position).getLike()));
                    tv_comment.setText(data.get(position).getContent());
                    tv_time.setText(data.get(position).getTime());
                    if (!"".equals(phone)) {
                        if (data.get(position).getLike_personal_comment() == 1) {
                            img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
                        } else {
                            img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                        }

                    } else {
                        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                    }
                    img_like.setOnClickListener(AdDetailActivity.this);
                    img_like.setTag(position_all, position);
                    img_like.setTag(view_img_like, img_like);
                    img_like.setTag(view_tv_like, tv_like);

                }
            };
            recycle.setLayoutManager(new LinearLayoutManager(AdDetailActivity.this));
            recycle.setAdapter(adapter);
        }
    }

    @OnClick({R.id.title_im, R.id.tv_publish, R.id.tv_concern, R.id.tv_concern_ready, R.id.tv_collect, R.id.tv_collect_ready})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                startActivity(MainActivity.class);
                finish();
                break;
            case R.id.tv_publish:
                if (checkPhone(AdDetailActivity.this)) {
                    comment = et_comment.getText().toString().trim();
                    if (!TextUtils.isEmpty(comment)) {
                        Log.e(TAG, "onViewClicked: comment" + comment);
                        insertComment();
                    } else {
                        Util.showToast(AdDetailActivity.this, "抱歉,评论不能为空");
                    }
                }
                break;
            case R.id.tv_concern:
                if (checkPhone(AdDetailActivity.this)) {
                    insertConcern();
                }
                break;
            case R.id.tv_concern_ready:
                deleteConcern();
                break;
            case R.id.tv_collect:
                if (checkPhone(AdDetailActivity.this)) {
                    insertCollect();
                }
                break;
            case R.id.tv_collect_ready:
                deleteCollect();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(MainActivity.class);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void insertComment() {
        time = sdf.format(System.currentTimeMillis());
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_2_comment(`phone`,`table_id`,`table_name`,`content`,`time`)values('" + phone + "','" + table_id + "','" + table_name + "','" + comment + "','" + time + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_name + " SET COMMENT= COMMENT+1 WHERE id = " + table_id + "")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tv_comment_all.setText(String.valueOf(Integer.parseInt(tv_comment_all.getText().toString()) + 1));
                                        Util.showToast(AdDetailActivity.this, "发表成功");
                                        et_comment.setText("");
                                        Keyboard.getInstance().hideKeyBoard(et_comment);
                                        urlsss = LoginInfo.getString(AdDetailActivity.this, "user_img", "");
                                        namesss = LoginInfo.getString(AdDetailActivity.this, "user_name", "");
                                        if ("".equals(urlsss) || "".equals(namesss)) {
                                            EasyHttp.post(IpConfig.URL_SQL)
                                                    .params("query", "SELECT url3,user_name FROM login where phone = '" + phone + "'")
                                                    .timeStamp(true)
                                                    .execute(new SuccessCallBack<String>() {
                                                        @Override
                                                        public void onSuccess(String s) {
                                                            List<User> data_user = GsonUtils.getGsonToList(s, User.class);
                                                            namesss = data.get(0).getUser_name();
                                                            urlsss = data.get(0).getUrl3();
                                                            Comment comment1 = new Comment();
                                                            comment1.setUrl3(urlsss);
                                                            comment1.setUser_name(namesss);
                                                            comment1.setLike(0);
                                                            comment1.setContent(comment);
                                                            comment1.setTime(time);
                                                            comment1.setLike_personal_comment(0);
                                                            data.add(0, comment1);
                                                            if (adapter != null) {
                                                                adapter.notifyDataSetChanged();
                                                            } else {
                                                                initAdapter();
                                                            }
                                                            LoginInfo.setString(AdDetailActivity.this, "user_name", namesss);
                                                            LoginInfo.setString(AdDetailActivity.this, "user_img", urlsss);
                                                        }
                                                    });
                                        } else {
                                            Comment comment1 = new Comment();
                                            comment1.setUrl3(urlsss);
                                            comment1.setUser_name(namesss);
                                            comment1.setLike(0);
                                            comment1.setContent(comment);
                                            comment1.setTime(time);
                                            comment1.setLike_personal_comment(0);
                                            data.add(0, comment1);
                                            if (adapter != null) {
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                initAdapter();
                                            }
                                        }
                                    }
                                });
                    }
                });
    }

    private void insertConcern() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_5_concern(`phone`,`phone_concern`)values('" + phone + "','" + other_phone + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Util.showToast(AdDetailActivity.this, "关注成功");
                        tv_concern.setVisibility(View.INVISIBLE);
                        tv_concern_ready.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void deleteConcern() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_5_concern where `phone` = " + phone + " AND phone_concern = " + other_phone + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Util.showToast(AdDetailActivity.this, "已取消关注");
                        tv_concern_ready.setVisibility(View.GONE);
                        tv_concern.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void insertCollect() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_6_collect(`phone`,`table_id`,`table_name`)values('" + phone + "','" + table_id + "','" + table_name + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Util.showToast(AdDetailActivity.this, "收藏成功");
                        tv_collect.setVisibility(View.INVISIBLE);
                        tv_collect_ready.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void deleteCollect() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_6_collect where `phone` = " + phone + " AND table_id = " + table_id + " AND table_name = '" + table_name + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Util.showToast(AdDetailActivity.this, "已取消收藏");
                        tv_collect_ready.setVisibility(View.GONE);
                        tv_collect.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(position_all);
        switch (view.getId()) {
            case R.id.img_like:
                if (checkPhone(AdDetailActivity.this)) {
                    ImageView img_like = (ImageView) view.getTag(view_img_like);
                    TextView tv_like = (TextView) view.getTag(view_tv_like);
                    flag_like = data.get(position).getLike_personal_comment();
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
        }
    }

    private void plus_like(int position, ImageView img_like, TextView tv_like) {
        data.get(position).setLike_personal_comment(1);
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) + 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(AdDetailActivity.this)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("+1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_4_like_person(`phone`,`comment_id`) values(" + phone + "," + data.get(position).getId() + ")")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE comment_2_comment SET `like`= `like`+1 WHERE id = '" + data.get(position).getId() + "'")
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
        data.get(position).setLike_personal_comment(0);
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) - 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(AdDetailActivity.this)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("-1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_4_like_person WHERE `comment_id` = '" + data.get(position).getId() + "' AND phone = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE comment_2_comment SET `like`= `like`-1 WHERE id = '" + data.get(position).getId() + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                    }
                                });
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
