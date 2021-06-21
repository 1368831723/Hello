package com.pwj.activity;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.AppSpec;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.Xmpp;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.utils.DeviceNumber;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;


import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import org.jivesoftware.smack.XMPPConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by delphi0127 on 2018/7/15.
 */

public class SplashActivity extends AppCompatActivity {


    private String phone;
    private RelativeLayout title_relative;
    private String uuid;
    public XMPPConnection connection;
    private ImageView iv;
    private TextView tv;
    private List<AppSpec> data = new ArrayList<>();
    private int urlCode = 0;
    private boolean flag = true;
    private String path_base;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        title_relative = findViewById(R.id.title_relative);
        iv = findViewById(R.id.iv);
        tv = findViewById(R.id.tv);
        insertData();
        ImmersionBar.with(this)
                .titleBar(title_relative, false)
                .transparentBar()
                .init();
        phone = LoginInfo.getString(SplashActivity.this, "phone", "");//获取电话号码不为空登录

//        handler.sendEmptyMessageDelayed(0, 3000);
        getPermissions();
        initRegister();
        recLenCode();
    }

    private Timer timer;
    private TimerTask timerTask;
    private int recLen;

    private void recLenCode() {
        recLen = 4;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        tv.setText("跳过 " + recLen + " S");
                        if (recLen < 1) {
                            timer.cancel();
                            startActivity(MainActivity.class);
                            finish();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);       // timeTask
    }

//    private void getImg() {
//        EasyHttp.post(IpConfig.URL_SQL)
//                .params("query", "SELECT url FROM app")
//                .timeStamp(true)
//                .execute(new SuccessCallBack<String>() {
//                    @Override
//                    public void onSuccess(String str) {
//                        data = GsonUtils.getGsonToList(str, AppSpec.class);
//                        Glide.with(SplashActivity.this).load(data.get(0).getUrl()).into(iv);
//                    }
//                });
//    }

    private void insertData() {
        uuid = LoginInfo.getString(this, "uuid", "");
        if ("".equals(uuid)) {
            uuid = DeviceNumber.getDeviceId(this);
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "insert into login(`uid`)" + "values('" + uuid + "')")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            LoginInfo.setString(SplashActivity.this, "uuid", uuid);
                        }
                    });
        }
    }

    //点击事件的判断
    @OnClick({R.id.tv, R.id.iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv:
                recLen = 10;
                timer.cancel();
                startActivity(MainActivity.class);
                finish();
                Log.e("点击了", "跳过");
                break;
            case R.id.iv:
                recLen = 10;
                timer.cancel();
                startActivity(AdDetailActivity.class);
                finish();
                Log.e("点击了", "图片");
                break;
        }
    }

    private void initRegister() {
        if (phone.equals("")) {

        } else {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT `creationDate` FROM ofuser WHERE username = '" + phone + "'")
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
                                            Xmpp.getInstance().regist(connection, phone, phone);
                                        } else {

                                        }

                                    }
                                }).start();
                            }
                        }
                    });
        }
    }


//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            startActivity(MainActivity.class);
//            finish();
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, activity);
        startActivity(intent);
    }

    protected void getIMG() {
        urlCode = LoginInfo.getInt(this, "urlCode", 0);
        String name = "ad.png";
        path_base = IpConfig.PATH_DATA + "ad";
        String path = path_base + File.separator + name;
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path);
        if (file2.exists()) { //文件存在直接加载图片，并查询图片编号是否更新
            flag = true;
            iv.setImageBitmap(getLocalBitmap(path));
            getAdUrl();
        } else {     //文件不存在直接加载图片
            flag = false;
            getAdUrl();
        }
    }

    protected void getAdUrl() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT * FROM app")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data = GsonUtils.getGsonToList(str, AppSpec.class);
                        int code = data.get(0).getUrlCode();
                        if (flag) { // 图片存在
                            if (code > urlCode) {
                                Glide.with(SplashActivity.this).load(data.get(0).getUrl()).into(iv);
                                new DeleteFolder().DeleteFolder(path_base);
                                downLoadIMG();
                                //下载图片保存
                            }
                        } else { // 图片不存在
                            Glide.with(SplashActivity.this).load(data.get(0).getUrl()).into(iv);
                            LoginInfo.setInt(SplashActivity.this, "urlCode", code);
                            //下载图片保存
                            new DeleteFolder().DeleteFolder(path_base);
                            downLoadIMG();
                        }
                    }
                });
    }

    protected Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void downLoadIMG() {
        EasyHttp.downLoad(data.get(0).getUrl())
                .savePath(path_base)
                .saveName("ad.png")
                .execute(null);
    }


    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    public void getPermissions() {
        getIMG();
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
        Util.showToast(SplashActivity.this, "禁止权限会影响到app的正常使用");
    }



//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SplashActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SplashActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
