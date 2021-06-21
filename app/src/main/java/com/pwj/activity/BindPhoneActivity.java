package com.pwj.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.bean.Login;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.Xmpp;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.DeviceNumber;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PhoneFormatCheckUtils;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by han on 2018/8/17.
 */

public class BindPhoneActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.user_phone)
    EditText user_phone;
    @BindView(R.id.user_code_et)
    EditText user_code_et;
    @BindView(R.id.user_code_btn)
    TextView user_code_btn;
    @BindView(R.id.user_code_tv)
    TextView user_code_tv;
    @BindView(R.id.bind)
    Button bind;

    private String uuid;
    private String openid;
    private String user_name;
    private String head_url;
    private String phone_str;
    private String code_str;
    private List<Login> data = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        title_tv.setText("绑定手机号");
        initData();
    }

    private void initData() {
        uuid = DeviceNumber.getDeviceId(this);
        head_url = getIntent().getStringExtra("user_img");
        openid = getIntent().getStringExtra("openid");
        user_name = getIntent().getStringExtra("user_name");
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
                        user_code_tv.setText(recLen + " S");
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

    @OnClick({R.id.title_im, R.id.user_code_btn, R.id.bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                BindPhoneActivity.this.finish();
                break;
            case R.id.user_code_btn:
                phone_str = user_phone.getText().toString().trim();
                judgeCode();
                break;
            case R.id.bind:
                code_str = user_code_et.getText().toString().trim();
                judgeName();
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:             //验证码验证成功
                    //执行bmob注册
                    EasyHttp.post(IpConfig.URL_SQL)
                            .params("query", "SELECT phone FROM login WHERE phone = "+phone_str+"")
                            .timeStamp(true)
                            .execute(new SuccessCallBack<String>() {
                                @Override
                                public void onSuccess(String str) {
                                    data = GsonUtils.getGsonToList(str, Login.class);
                                    if (data.size()>0){
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "update login set `openid`='" + openid + "' ,`url3`='" + head_url + "' ,`user_name`= '" + user_name + "' where `phone`='" + phone_str + "'")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        Util.showToast(BindPhoneActivity.this,"恭喜你成功绑定手机号！");
                                                        Intent intent = new Intent(BindPhoneActivity.this, SelectLoveActivity.class);
                                                        intent.putExtra("phone",phone_str);
                                                        startActivity(intent);//点击按钮立马跳转
                                                        finish();
                                                        initRegist();
                                                        LoginInfo.setString(BindPhoneActivity.this, "phone", phone_str);
                                                        LoginInfo.setString(BindPhoneActivity.this, "user_name", user_name);
                                                        LoginInfo.setString(BindPhoneActivity.this, "user_img", head_url);
                                                    }
                                                });
                                    }else {
                                        if ("".equals(uuid)) {
                                            EasyHttp.post(IpConfig.URL_SQL)
                                                    .params("query", "insert into login(`openid`,`url3`,`user_name`,`phone`)" + "values('" + openid + "','" + head_url + "','" + user_name + "','" + phone_str + "')")
                                                    .timeStamp(true)
                                                    .execute(new SuccessCallBack<String>() {
                                                        @Override
                                                        public void onSuccess(String s) {
                                                            Util.showToast(BindPhoneActivity.this,"恭喜你成功绑定手机号！");
                                                            Intent intent = new Intent(BindPhoneActivity.this, SelectLoveActivity.class);
                                                            intent.putExtra("phone",phone_str);
                                                            startActivity(intent);//点击按钮立马跳转
                                                            finish();
                                                            initRegist();
                                                            LoginInfo.setString(BindPhoneActivity.this, "phone", phone_str);
                                                            LoginInfo.setString(BindPhoneActivity.this, "user_name", user_name);
                                                            LoginInfo.setString(BindPhoneActivity.this, "user_img", head_url);
                                                        }
                                                    });
                                        }else {
                                            EasyHttp.post(IpConfig.URL_SQL)
                                                    .params("query", "update login set `openid`='" + openid + "' ,`url3`='" + head_url + "',`user_name`= '" + user_name + "' ,`phone`= '" + phone_str + "'where `uid`='" + uuid + "'")
                                                    .timeStamp(true)
                                                    .execute(new SuccessCallBack<String>() {
                                                        @Override
                                                        public void onSuccess(String s) {
                                                            Util.showToast(BindPhoneActivity.this,"恭喜你成功绑定手机号！");
                                                            Intent intent = new Intent(BindPhoneActivity.this, SelectLoveActivity.class);
                                                            intent.putExtra("phone",phone_str);
                                                            startActivity(intent);//点击按钮立马跳转
                                                            finish();
                                                            initRegist();
                                                            LoginInfo.setString(BindPhoneActivity.this, "phone", phone_str);
                                                            LoginInfo.setString(BindPhoneActivity.this, "user_name", user_name);
                                                            LoginInfo.setString(BindPhoneActivity.this, "user_img", head_url);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                    break;
                case 2:             //获取验证码成功,注意查看
                    Util.showToast(BindPhoneActivity.this, "获取验证码成功,注意查看");
                    break;
                case 3:
                    Util.showToast(BindPhoneActivity.this, "获取验证码失败,请填写正确的手机号");//获取验证码失败,请填写正确的手机号码
                    break;
                case 4:             //验证码验证错误
                    Util.showToast(BindPhoneActivity.this, "验证码错误");
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
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
            getSmsCode("86", phone_str);  //发送验证码
            recLenCode();
            user_code_et.setFocusable(true);
            user_code_et.setFocusableInTouchMode(true);
            user_code_et.requestFocus();
            Util.showToast(BindPhoneActivity.this, "获取验证码成功,注意查看");
        } else {
            Util.showToast(BindPhoneActivity.this, "请输入正确的手机号");
        }
    }

    private void judgeName() {
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone_str)) {//手机号正确
            submitCode("86", phone_str, code_str);
            callBack();
        } else {
            Util.showToast(BindPhoneActivity.this, "请输入正确的手机号");
        }
    }

    private void initRegist() {
        if (phone_str.equals("")) {
        } else {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT `creationDate` FROM ofuser WHERE username = '" + phone_str + "'")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s.length() > 25) {
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Xmpp.getConnections() != null) {
                                            XMPPConnection connection = Xmpp.getConnections();
                                            Xmpp.getInstance().regist(connection, phone_str, phone_str);
                                        } else {

                                        }

                                    }
                                }).start();
                            }
                        }
                    });
        }
    }

    //    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("FindPwdActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("FindPwdActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHandler();
        myHandler.removeCallbacksAndMessages(null);
//        Jdbc.releaseConnection(Jdbc.getConnection("root", "1q23lyc45j"));
    }
}
