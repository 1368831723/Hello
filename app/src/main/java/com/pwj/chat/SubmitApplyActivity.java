package com.pwj.chat;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.BaseActivity;
import com.pwj.helloya.R;
import com.pwj.utils.LoginInfo;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


public class SubmitApplyActivity extends Activity {
    @BindView(R.id.title_im)
    RelativeLayout title_im;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private String url3;
    private String user_name;
    private String other_phone;
    private String phone;
    private String id;
    private XMPPConnection connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_apply);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_im)  //可以为任意view
                .statusBarColor(R.color.colorPrimary)  //指定状态栏颜色,根据情况是否设置
                .init();
        initData();
        initFriends();
    }

    private void initData() {
        title_tv.setText("添加好友");
//        url3 = getIntent().getStringExtra("url3");
//        user_name = getIntent().getStringExtra("user_name");
        other_phone = getIntent().getStringExtra("other_phone");
        id = getIntent().getStringExtra("id");
//        Glide.with(this).load(url3).into(img);
//        name_tv.setText(user_name);
    }
    private void initFriends() {

    }

    private void addFriends() {
        phone = LoginInfo.getString(this, "phone", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Xmpp.getInstance().getConnections() != null) {
                    connection = Xmpp.getInstance().getConnections();
                    if (Xmpp.getInstance().login(connection, phone, phone)) {
                        if (Xmpp.getInstance().addUser(connection.getRoster(), id, phone,"Friends")) {

                        }
                    }

                } else {

                }
            }
        }).start();
    }

    @OnClick({R.id.title_im,R.id.define_tv})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.title_im:
                finish();
                break;
            case R.id.define_tv:
                addFriends();
                break;
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
