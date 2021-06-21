package com.pwj.chat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.adapter.BaseRcyAdapter;

import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;


import org.litepal.LitePal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class MessageActivity extends AppCompatActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    RelativeLayout title_linear;
    @BindView(R.id.no_msg_tv)
    TextView no_msg_tv;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private String phone = "";
    private String pwd = "";
    private List<User> data = new ArrayList<>();
    private List<User> data_sqlite = new ArrayList<>();
    private List<User> data_mysql = new ArrayList<>();
    public List<User> data_user = new ArrayList<>();
    private List<User> data_users = new ArrayList<>();
    private HashMap<String, Integer> map = new HashMap<>();
    private HashMap<String, Integer> map_sqlite = new HashMap<>();
    private List<Ofoffline> data_off = new ArrayList<>();
    private BaseRcyAdapter baseRcyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        title_tv.setText("消息列表");
        phone = LoginInfo.getString(this, "phone", "");
        getPermissions();
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    public void getPermissions() {
        Log.e("getPermissions: ", "申请权限");
        initSqlite();
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
    }

    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(MessageActivity.this, "禁止权限会影响到app的正常使用");
        MessageActivity.this.finish();
    }

    public void initOfoffine() {
        Log.e(TAG, "data_sqlite的长度 " + data_sqlite.size());
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT username,stanza FROM ofoffline WHERE username = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data_off.clear();
                        data_off = GsonUtils.getGsonToList(s, Ofoffline.class);
                        Log.e(TAG, "data_off的长度 " + data_off.size() + "phone:" + phone);
                        if (data_off.size() > 0) {
                            for (int i = 0; i < data_off.size(); i++) {
                                String stanza = data_off.get(i).getStanza();
                                String str = stanza.substring(stanza.indexOf("from=") + 6);
                                String other_phone = str.substring(0, 11);
                                if (map.get(other_phone) != null) {
                                    map.put(other_phone, map.get(other_phone) + 1);
                                } else {
                                    map.put(other_phone, 1);
                                }
                                if (i == data_off.size() - 1) {
                                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                        String phone = entry.getKey();
                                        int count = entry.getValue();
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "SELECT url3,user_name FROM login where phone = '" + phone + "'")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        data_user.clear();
                                                        data_user = GsonUtils.getGsonToList(s, User.class);
                                                        User user = new User(phone, data_user.get(0).getUser_name(), data_user.get(0).getUrl3(), count);
                                                        data_mysql.add(user);
                                                        Log.e("data_off: ", "onSuccess: " + phone + "--" + count);
                                                        if (data_mysql.size() == map.size()) {
                                                            data.clear();
                                                            data.addAll(data_mysql);
                                                            initAdapter();
//                                                            if (data_sqlite.size() == 0) {
//                                                                initAdapter();
//                                                            } else {
//                                                                for (int j = 0; j < data_sqlite.size(); j++) {
//                                                                    if (map.get(data_sqlite.get(j).getPhone()) != null) {
//
//                                                                    } else {
//                                                                        User user1 = new User(data_sqlite.get(j).getPhone(),data_sqlite.get(j).getUser_name(), data_sqlite.get(j).getUrl3(), 0);
//                                                                        data.add(user1);
//                                                                    }
//                                                                    if (j == data_sqlite.size() - 1) {
//                                                                        initAdapter();
//                                                                    }
//                                                                }
//                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                String phone = entry.getKey();
                                int count = entry.getValue();
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "SELECT url3,user_name FROM login where phone = '" + phone + "'")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                data_user.clear();
                                                data_user = GsonUtils.getGsonToList(s, User.class);
                                                User user = new User(phone, data_user.get(0).getUser_name(), data_user.get(0).getUrl3(), count);
                                                data_mysql.add(user);
                                                Log.e("data_off: ", "onSuccess: " + phone + "--" + count);
                                                if (data_mysql.size() == map.size()) {
                                                    data.clear();
                                                    data.addAll(data_mysql);
                                                    initAdapter();

                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void initAdapter() {
        if (data.size() == 0) {
            no_msg_tv.setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
            recycler.setLayoutManager(linearLayoutManager);
            baseRcyAdapter = new BaseRcyAdapter(data, R.layout.item_chat_message) {
                @Override
                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                    CircleImageView imageView = holder.getView(R.id.img);
                    TextView name_tv = holder.getView(R.id.name_tv);
                    TextView count_tv = holder.getView(R.id.count_tv);
                    Glide.with(MessageActivity.this).load(data.get(position).getUrl3()).into(imageView);
                    name_tv.setText(data.get(position).getUser_name());
                    Log.e("data_off", "initAdapter: " + data.get(position).getPhone() + "--" + map.get(data.get(position).getPhone()));
                    if (map.get(data.get(position).getPhone()) != null) {
                        if (map.get(data.get(position).getPhone()) > 0) {
                            count_tv.setVisibility(View.VISIBLE);
                            count_tv.setText(String.valueOf(map.get(data.get(position).getPhone())));
                        } else {
                            count_tv.setVisibility(View.GONE);
                        }
                        Log.e("data_off", "initAdapter2: " + data.get(position).getPhone() + "--" + map.get(data.get(position).getPhone()));
                    } else {
                        count_tv.setVisibility(View.GONE);
                    }
                }
            };
            recycler.setAdapter(baseRcyAdapter);
            baseRcyAdapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String other_phone = data.get(position).getPhone();
                    String other_name = data.get(position).getUser_name();
                    String other_img = data.get(position).getUrl3();
                    Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
                    intent.putExtra("other_phone", other_phone);
                    intent.putExtra("other_name", other_name);
                    intent.putExtra("other_img", other_img);
                    map.put(other_phone, 0);
                    startActivity(intent);
                    data.remove(position);
                    User user = new User(other_phone, other_name, other_img, 0);
                    data.add(position, user);
                    baseRcyAdapter.notifyDataSetChanged();
                    finish();
                }
            });
        }
    }

    private void initSqlite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (LitePal.findAll(MsgContent.class).size() > 0) {
                    Cursor cursor = LitePal.findBySQL("select url3,other_phone,status from msgcontent where my_phone = '" + phone + "'group by id");
                    Log.e(TAG, "initSqlite: 111" + cursor.getCount());
                    if (cursor.getCount() <= 0) {
                        initOfoffine();
                        return;
                    }
                    while (cursor.moveToNext()) {
                        String other_phone = cursor.getString(1);
                        int status = cursor.getInt(2);
                        String sql = "SELECT url3 user_name FROM login where phone = '" + other_phone + "'";
                        Log.e(TAG, "initSqlite: 222" + sql);
                        if (status == 2) {
                            if (map.get(other_phone) != null) {
                                map.put(other_phone, map.get(other_phone) + 1);
                            } else {
                                map.put(other_phone, 1);
                            }
                        } else if (status == 1) {
                            if (map.get(other_phone) != null) {

                            } else {
                                map.put(other_phone, 0);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "initSqlite: 333");
                    initOfoffine();
                }
                if (LitePal.findAll(MsgContent.class).size() > 0) {
                    initOfoffine();
                    Cursor cursor = LitePal.findBySQL("select url3,other_phone from msgcontent where my_phone = '" + phone + "'group by other_phone");
                    Log.e(TAG, "initSqlite: 111" + cursor.getCount());
                    if (cursor.getCount() <= 0) {
                        return;
                    }
                    while (cursor.moveToNext()) {
                        String other_phone = cursor.getString(1);
                        String sql = "SELECT url3 user_name FROM login where phone = '" + other_phone + "'";
                        Log.e(TAG, "initSqlite: 222" + sql);
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "SELECT url3,user_name FROM login where phone = '" + other_phone + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        data_users.clear();
                                        data_users = GsonUtils.getGsonToList(s, User.class);
                                        String url3 = data_users.get(0).getUrl3();
                                        String other_name = data_users.get(0).getUser_name();
                                        User user = new User(other_phone, other_name, url3);
                                        data_sqlite.add(user);
                                        Log.e("消息", "onSuccess:" + data_sqlite.get(0).getUser_name() + "--" + data_sqlite.get(0).getUrl3());
                                    }
                                });
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

        }
    };

    private void initLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }


    @OnClick({R.id.title_im, R.id.img_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                Intent intent = new Intent(MessageActivity.this,MainActivity.class);
                intent.putExtra("page",5);
                startActivity(intent);
                break;
            case R.id.img_search:
                startActivity(SearchUserActivity.class);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Intent intent = new Intent(MessageActivity.this,MainActivity.class);
            intent.putExtra("page",5);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(MessageActivity.this, activity);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        if (baseRcyAdapter != null) {
            baseRcyAdapter.notifyDataSetChanged();
        }
        MobclickAgent.onResume(this); //统计时长

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

}
