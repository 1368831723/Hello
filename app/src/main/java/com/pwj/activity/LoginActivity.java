package com.pwj.activity;

import android.annotation.SuppressLint;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwj.BaseActivity;

import com.pwj.bean.Login;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.Xmpp;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.jdbc.Jdbc;

import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.jdbc.Query;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.utils.SoftHideKeyBoardUtil;
import com.pwj.utils.Util;

import com.zhouyou.http.EasyHttp;


import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by delphi0127 on 2018/7/6.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.login_qq)
    ImageView login_qq;
    @BindView(R.id.login_vx)
    ImageView login_vx;
    @BindView(R.id.login_phone)
    EditText login_phone;
    @BindView(R.id.login_pwd)
    EditText login_pwd;
    @BindView(R.id.login_find)
    TextView login_find;
    @BindView(R.id.login)
    Button login;
    private String phone_str;
    private String pwd_str;
    private String pwd_data;
    private EventHandler eventHandler;
    private int page;
    public XMPPConnection connection;
    private DownImageUtil mDownImageUtil;
    private ProgressDialog myDialog;
    private List<Login>data = new ArrayList<>();
    private String uuid = "";
    private String openid = "";
    private String user_name = "";
    private String head_url = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_login);
        ButterKnife.bind(this);
        initProgress(this);
        initPage();
        login_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void initPage() {
        title_tv.setText("????????????");
        SoftHideKeyBoardUtil.assistActivity(this);
        page = getIntent().getIntExtra("page", 0);
    }

    protected void initProgress(Context context) {
        mDownImageUtil = new DownImageUtil(context);
        myDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progress_pdf));
        myDialog.setMessage("???????????????????????????...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }

    //?????????????????????
    @OnClick({R.id.title_im, R.id.login_qq, R.id.login_vx, R.id.register, R.id.login, R.id.login_find})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                switch (page) {
                    case 0:
                        startActivity(MainActivity.class);
                        break;
                    case 1:
                        startActivity(IssueActivity.class);
                        break;
                    case 2:
                        //????????????fragment
                        break;
                }
                finish();
                break;
            case R.id.login_qq:
                Util.showToast(LoginActivity.this, "QQ??????????????????");
//                Platform plat = ShareSDK.getPlatform(qq.NAME);
//                plat.removeAccount(true); //???????????????????????????????????????????????????????????????
//                plat.SSOSetting(false); //SSO????????????false????????????????????????????????????????????????????????????????????????????????????web??????
//                plat.setPlatformActionListener(new PlatformActionListener() {
//                    @Override
//                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                        //   ????????????
//                        startActivity(MainActivity.class);
//                        String image = (String) hashMap.get("figureurl_qq_2");
//                        String name = (String) hashMap.get("nickname");
//                        LoginInfo.setString(LoginActivity.this, "qq_name", name);
//                        LoginInfo.setString(LoginActivity.this, "qq_image", image);
//                        Log.e(TAG, "onComplete: " + image);
//                        Log.e(TAG, "onComplete: " + name);
//                    }
//
//                    @Override
//                    public void onError(Platform platform, int i, Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onCancel(Platform platform, int i) {
//
//                    }
//                });//???????????????????????????oncomplete???onerror???oncancel????????????
//                if (plat.isClientValid()) {
//                    //?????????????????????????????????????????????true??????????????????false??????
//                }
//                if (plat.isAuthValid()) {
//                    //??????????????????????????????????????????????????????????????????????????????
//                    Util.showToast(LoginActivity.this,"??????????????????");
//                    return;
//                }
////                plat.authorize();	//????????????????????????
//                plat.showUser(null);    //?????????????????????????????????????????????????????????????????????
                break;
            case R.id.login_vx:
                Platform platform= ShareSDK.getPlatform(Wechat.NAME);
                platform.removeAccount(true); //???????????????????????????????????????????????????????????????
                platform.SSOSetting(false);
                platform.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        openid = (String) hashMap.get("openid");
                        user_name = (String) hashMap.get("nickname");
                        head_url = (String) hashMap.get("headimgurl");
                        Log.e("??????","?????????"+hashMap);
                        String sql = "SELECT openid , phone FROM login WHERE openid = '"+openid+"'";
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "SELECT openid , phone FROM login WHERE openid = '"+openid+"'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String str) {
                                        data = GsonUtils.getGsonToList(str,Login.class);
                                        if(data.size()>0){  // ???????????????????????????????????????
                                            String phone = data.get(0).getPhone();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);//????????????????????????
                                            finish();
                                            initRegist(phone);
                                            LoginInfo.setString(LoginActivity.this, "phone", phone);
                                            LoginInfo.setString(LoginActivity.this, "user_name", user_name);
                                            LoginInfo.setString(LoginActivity.this, "user_img", head_url);
                                        }else {     // ?????????????????? ????????????????????????
                                            Intent intent = new Intent(LoginActivity.this, BindPhoneActivity.class);
                                            intent.putExtra("openid",openid);
                                            intent.putExtra("user_name",user_name);
                                            intent.putExtra("user_img",head_url);
                                            startActivity(intent);
                                        }

                                    }
                                });
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        Log.e("??????","??????");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Log.e("??????: ", "??????" );
                    }
                });
//                if (platform.isClientValid()) {
//                    //?????????????????????????????????????????????true??????????????????false??????
//                }
//                if (platform.isAuthValid()) {
//                    //??????????????????????????????????????????????????????????????????????????????
//                    Util.showToast(LoginActivity.this,"??????????????????");
//                    return;
//                }
                platform.showUser(null);
                break;
            case R.id.register:
                startActivity(RegisterActivity.class);
                break;
            case R.id.login_find:
                startActivity(FindPwdActivity.class);
                break;
            case R.id.login:
                phone_str = login_phone.getText().toString().trim();
                pwd_str = login_pwd.getText().toString().trim();
                if ("".equals(phone_str) || "".equals(pwd_str)) {
                    Util.showToast(LoginActivity.this, "?????????????????????????????????");
                } else {
                    if (!myDialog.isShowing()) {
                        myDialog.show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean flag = Query.query_phone(Jdbc.getConnection("root", "1q23lyc45j"), phone_str, pwd_str);
                            if (flag) {
                                LoginInfo.setString(LoginActivity.this, "phone", phone_str);
                                LoginInfo.setString(LoginActivity.this, "pwd", pwd_str);
                                LoginInfo.setString(LoginActivity.this, "user_name", "");
                                LoginInfo.setString(LoginActivity.this, "user_img", "");
                                startActivity(MainActivity.class);
                                mHandler.sendEmptyMessageDelayed(0, 0);
                                initRegist(phone_str);
                            } else {
                                mHandler.sendEmptyMessageDelayed(1, 0);
                            }
                        }
                    }).start();
                }
                break;
        }
    }

    private void initRegist(String phone_str) {
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
                                            connection = Xmpp.getConnections();
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
//    private void startActivity(Class<?> activity) {
//        Intent intent = new Intent();
//        intent.setClass(LoginActivity.this, activity);
//        startActivity(intent);
//    }

    private void sendMessage() {
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
//                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
        SMSSDK.getVerificationCode("86", phone_str);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    break;
                case 1:
                    Util.showToast(LoginActivity.this, "???????????????????????????");
                    break;
            }
            if (myDialog.isShowing()) {
                myDialog.dismiss();
            }
        }
    };

    protected void onStop() {
        super.onStop();
        //????????????????????????????????????????????????????????????
        SMSSDK.unregisterEventHandler(eventHandler);
        mHandler.removeCallbacksAndMessages(null);
    }

//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("LoginActivity"); //????????????("MainScreen"??????????????????????????????)
//        MobclickAgent.onResume(this); //????????????
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("LoginActivity");
//        MobclickAgent.onPause(this); //????????????
//    }
}