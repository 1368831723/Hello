package com.pwj.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;
import com.pwj.helloya.R;
import com.umeng.analytics.MobclickAgent;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by delphi0127 on 2018/7/15.
 */

public class FindPwdResult extends BaseActivity{
    @BindView(R.id.tv_register_no)
    TextView tv_register_no;
    @BindView(R.id.issue_result_linear)
    LinearLayout issue_result_linear;
    @BindView(R.id.tv_pwd)
    TextView tv_pwd;
    @BindView(R.id.issue_success)
    TextView issue_success;
    @BindView(R.id.type_display_pro)
    ProgressBar type_display_pro;
    private String phone;
    private String pwd;
    private Map<String, String> map ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_find_result);
        ButterKnife.bind(this);
        initData();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pwd= (String) msg.obj;
            type_display_pro.setVisibility(View.GONE);
            if(TextUtils.isEmpty(pwd)){ //密码为空
                tv_register_no.setVisibility(View.VISIBLE);

            }else{  //密码不为空

                issue_result_linear.setVisibility(View.VISIBLE);
                tv_pwd.setText(pwd);
            }
        }
    };
    private void initData() {
        map=new HashMap<>();
        phone = getIntent().getStringExtra("phone_str");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = Jdbc.getConnection("root", "1q23lyc45j");
                map=Query.query_id_card(connection, phone);
                pwd=map.get("pwd");
                Message msg = Message.obtain();
                msg.obj = pwd;
//                handler.sendEmptyMessageDelayed()
                handler.sendMessageDelayed(msg,0);
            }
        }).start();

    }

    @OnClick({R.id.title_im,R.id.issue_success})
    public void onViewClicked(View view) {
        FindPwdResult.this.finish();
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("FindPwdResult"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("FindPwdResult");
//        MobclickAgent.onPause(this); //统计时长
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面关闭时，释放连接
        handler.removeCallbacksAndMessages(null);
    }



}
