package com.pwj.chat;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.BaseActivity;


import com.pwj.activity.PersonIssueActivity;
import com.pwj.activity.PushProductActivity;
import com.pwj.helloya.R;

import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OtherUserActivity extends BaseActivity {
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name_tv;
    private String other_img;
    private String other_name;
    private String other_phone;
    private String phone;
    private XMPPConnection connection;
    public List<RosterEntry> friends = new ArrayList<>();
    private String id = "";
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_other_user);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        title_tv.setText("");
        title_linear.setBackgroundColor(getColor(R.color.white));
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.white)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        other_img = getIntent().getStringExtra("other_img");
        other_name = getIntent().getStringExtra("other_name");
        other_phone = getIntent().getStringExtra("other_phone");
        Glide.with(this).load(other_img).into(img);
        name_tv.setText(other_name);
        phone = LoginInfo.getString(this, "phone", "");
    }


    private void addFriends() {
        if ("".equals(phone)) {
            Util.showToast(OtherUserActivity.this, "请登录先登录账号");
        } else {
            Intent intent = new Intent(OtherUserActivity.this, SubmitApplyActivity.class);
            intent.putExtra("other_phone", other_phone);
            intent.putExtra("id", other_phone + IpConfig.URL_CHAT);
            startActivity(intent);
        }
    }

    @OnClick({R.id.title_im, R.id.img, R.id.tv_issue, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img:
                intent = new Intent(OtherUserActivity.this, LoadBigPicture.class);
                intent.putExtra("path", other_img);
                startActivity(intent);
                break;
            case R.id.tv_issue:
                intent = new Intent(OtherUserActivity.this, PersonIssueActivity.class);
                intent.putExtra("user_img", other_img);
                intent.putExtra("user_name", other_name);
                intent.putExtra("phone", other_phone);
                startActivity(intent);
                break;
            case R.id.tv_send:
                if (checkPhone(OtherUserActivity.this)) {
                    if (phone.equals(other_phone)) {
                        Util.showToast(OtherUserActivity.this, "对不起,不能与自己聊天");
                    } else {
                        Intent intent = new Intent(OtherUserActivity.this, ChatActivity.class);
                        intent.putExtra("other_img", other_img);
                        intent.putExtra("other_phone", other_phone);
                        intent.putExtra("other_name", other_name);
                        startActivity(intent);
                    }
                }
                break;
//            case R.id.send_msg_btn:
//                Intent intent = new Intent(OtherUserActivity.this, ChatActivity.class);
//                intent.putExtra("id", id);
//                intent.putExtra("other_img",url3);
//                startActivity(intent);
//                break;
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
