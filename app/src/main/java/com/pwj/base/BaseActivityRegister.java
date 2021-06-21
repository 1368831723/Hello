package com.pwj.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.activity.RegisterAlready;
import com.pwj.activity.RegisterSuccess;
import com.pwj.helloya.R;
import com.pwj.jdbc.Insert;
import com.pwj.jdbc.Jdbc;
import com.pwj.jdbc.Query;
import com.pwj.utils.PhoneFormatCheckUtils;
import com.pwj.utils.Util;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.content.ContentValues.TAG;

/**
 * Created by han on 2018/8/17.
 */

public class BaseActivityRegister extends BaseActivity{
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.user_name)
    EditText user_name;
    @BindView(R.id.user_pwd)
    EditText user_pwd;
    @BindView(R.id.confirm_pwd)
    EditText confirm_pwd;
    @BindView(R.id.user_phone)
    EditText user_phone;
    @BindView(R.id.user_code_et)
    EditText user_code_et;
    @BindView(R.id.user_code_btn)
    Button user_code_btn;
    @BindView(R.id.others_remarks)
    EditText et_others_remarks;
    @BindView(R.id.user_code_tv)
    TextView user_code_tv;
    @BindView(R.id.register)
    Button register;

    private String name_str;
    private String pwd_str;
    private String confirm_str;
    private String phone_str;
    private String code_str;
    private String others_remarks="";
    private String type;
    private int typ;
    private Connection connection = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_base_register);
        ButterKnife.bind(this);
        initType();
        user_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    }


    private void initType() {
        title_tv.setText("用户注册");
        typ = getIntent().getIntExtra("typ", 1);
        switch (typ) {
            case 1:
                type = "销售员";
                break;
            case 4:
                type = "用户";
                break;
            case 5:
                type = "工程师";
                break;
            case 6:
                type = "其他";
                break;
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    private int recLen;

    private void recLenCode() {
        recLen = 60;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        user_code_btn.setVisibility(View.GONE);
                        user_code_tv.setVisibility(View.VISIBLE);
                        user_code_tv.setText(recLen + "s");
                        if (recLen < 1) {
                            timer.cancel();
                            user_code_btn.setVisibility(View.VISIBLE);
                            user_code_tv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);       // timeTask
    }

    @OnClick({R.id.title_im, R.id.user_code_btn, R.id.register})
    public void onViewClicked(View view) {
        name_str = user_name.getText().toString().trim();
        pwd_str = user_pwd.getText().toString().trim();
        confirm_str = confirm_pwd.getText().toString().trim();
        phone_str = user_phone.getText().toString().trim();
        code_str = user_code_et.getText().toString().trim();
        others_remarks = et_others_remarks.getText().toString().trim();
        switch (view.getId()) {
            case R.id.title_im:
                BaseActivityRegister.this.finish();
                break;
            case R.id.user_code_btn:
                judgeCode();
                break;
            case R.id.register:
                judgeName();
                break;

        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(BaseActivityRegister.this, RegisterAlready.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    Intent intent1 = new Intent(BaseActivityRegister.this, RegisterSuccess.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
    };
    private int count=0;
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:             //验证码验证成功
//                    执行bmob注册
                    count=count+1;
                    if (count==1){  //防止验证码输入错误后，再输入正确执行了2次该方法
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                connection = Jdbc.getConnection("root", "1q23lyc45j");
                                boolean flag = Query.query_register(connection, phone_str);
                                if (flag) { //账号存在
                                    handler.sendEmptyMessageDelayed(1, 0);
                                } else {    //没有账号
                                    handler.sendEmptyMessageDelayed(2, 0);
                                    connection = Jdbc.getConnection("root", "1q23lyc45j");
                                    Insert.insert_phone1(connection, name_str, pwd_str, phone_str, type,others_remarks);
                                }
                            }
                        }).start();
                    }
                    break;
                case 2:             //获取验证码成功,注意查看
                    Util.showToast(BaseActivityRegister.this, "获取验证码成功,注意查看");
                    break;
                case 3:
                    //获取验证码失败,请填写正确的手机号码
                    Util.showToast(BaseActivityRegister.this, "获取验证码失败,请填写正确的手机号");
                    break;
                case 4:             //验证码验证错误
                    Util.showToast(BaseActivityRegister.this, "验证码错误");
                    break;
            }
        }
    };
    //1 注册回调接口 初始化短信接送监听器【注意：回掉接口在非UI线程中so要用到Handler来通知用户】在onCreate()方法里init
    private EventHandler eventHandler;
    public int smsFlage = 0;//0:设置为初始化值 1：请求获取验证码 2：提交用户输入的验证码判断是否正确

    private void callBack() {
        this.eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        myHandler.sendEmptyMessageDelayed(1, 0);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        myHandler.sendEmptyMessageDelayed(2, 0);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    //此语句代表接口返回失败
                    //获取验证码失败。短信验证码验证失败（用flage标记来判断）
//                    if (smsFlage == 0) {
//                        myHandler.sendEmptyMessage(4);
//                    } else
                    if (smsFlage == 1) {
                        myHandler.sendEmptyMessageDelayed(3, 0);

                    } else {
                        myHandler.sendEmptyMessageDelayed(4, 0);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);//注册短信回调
    }

    //2 获取短信验证码 请求获取短信验证码，在监听中返回
    private void getSmsCode(String country, String phone) {
        SMSSDK.getVerificationCode(country, phone);//请求获取短信验证码，在监听中返回
    }

    //3 提交验证码
    private void submitCode(String country, String phone, String code) {
        SMSSDK.submitVerificationCode(country, phone, code);//提交短信验证码，在监听中返回
    }

    //4 注销回调接口 registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
    private void unregisterHandler() {
        SMSSDK.unregisterEventHandler(eventHandler);
        //    Log.v("TAG", "注销回调接口");
    }

    private void judgeCode() {
        if (!TextUtils.isEmpty(name_str)) {                                    //用户名不为空
            if (!TextUtils.isEmpty(pwd_str)) {                                //密码不为空
                if (pwd_str.equals(confirm_str)) {                            //密码一致
                    if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
                        getSmsCode("86", phone_str);  //发送验证码
                        recLenCode();
                        Util.showToast(BaseActivityRegister.this, "获取验证码成功,注意查看");
                    } else {
                        Util.showToast(BaseActivityRegister.this, "请输入正确的手机号");
                    }
                } else {
                    Util.showToast(BaseActivityRegister.this, "两次输入的密码不一致");
                }
            } else {
                Util.showToast(BaseActivityRegister.this, "密码不能为空");
            }
        } else {
            Util.showToast(BaseActivityRegister.this, "用户名不能为空");
        }
    }

    private void judgeName() {
        if (!TextUtils.isEmpty(name_str)) {                                    //用户名不为空
            if (!TextUtils.isEmpty(pwd_str)) {                                //密码不为空
                if (pwd_str.equals(confirm_str)) {                            //密码一致
                    if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
                        submitCode("86", phone_str, code_str);
                        callBack();
                    } else {
                        Util.showToast(BaseActivityRegister.this, "请输入正确的手机号");
                    }
                } else {
                    Util.showToast(BaseActivityRegister.this, "两次输入的密码不一致");
                }
            } else {
                Util.showToast(BaseActivityRegister.this, "密码不能为空");
            }
        } else {
            Util.showToast(BaseActivityRegister.this, "用户名不能为空");
        }
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RegisterType1"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RegisterType1");
//        MobclickAgent.onPause(this); //统计时长
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHandler();
        myHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
//        Jdbc.releaseConnection(Jdbc.getConnection("root", "1q23lyc45j"));
    }
}
