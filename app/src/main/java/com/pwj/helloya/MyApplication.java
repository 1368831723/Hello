package com.pwj.helloya;

import android.content.Context;

import android.content.Intent;

import android.support.multidex.MultiDex;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.request.target.ViewTarget;
import com.mob.MobSDK;

import com.pwj.activity.PushBackgroundActivity;

import com.pwj.activity.PushProductActivity;
import com.pwj.activity.PushRecruitActivity;
import com.pwj.activity.SettingsActivity;

import com.pwj.image.GlideImageLoader;

import com.pwj.umeng.MyPreferences;
import com.pwj.umeng.PushHelper;
import com.pwj.utils.LoginInfo;


import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.zhouyou.http.EasyHttp;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.Beta;


/**
 * Created by delphi0127 on 2018/7/6.
 */

public class MyApplication extends LitePalApplication{
    private static Context mContext;
    private boolean flag_device = false;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        MultContext(this);
        MobSDK.init(this);
        LitePal.initialize(this);
        ViewTarget.setTagId(R.id.glideTag);
        initBaiDu();
//        initBugly();
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(this);
        //正式初始化
        initPushSDK();
//        initUM();
        initGridView();
        initEasyHttp();
    }

    protected void MultContext(Context base) {
        MultiDex.install(base);
    }

    public static Context getApplication() {
        return mContext;
    }

    private void initGridView() {
        AssNineGridView.setImageLoader(new GlideImageLoader());
    }

    private void initBaiDu() {
        SDKInitializer.initialize(mContext);
    }

//    private void initBugly() {
//        Beta.autoInit = true;
//        Beta.autoCheckUpgrade = true;
//        Beta.upgradeCheckPeriod = 60 * 1000;
//        Beta.initDelay = 1 * 1000;
//        Bugly.init(mContext, "adf552c808", false);
//
//    }


    /**
     * 初始化推送SDK，在用户隐私政策协议同意后，再做初始化
     */
    private void initPushSDK() {
        Log.e("友盟---","aaa");
        /*
         * 判断用户是否已同意隐私政策
         * 当同意时，直接进行初始化；
         * 当未同意时，待用户同意后，通过PushHelper.init(...)方法进行初始化。
         */
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (agreed && PushHelper.isMainProcess(this)) {
            Log.e("友盟---","bbb");
            //建议在线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        }
    }

    private void initUM() {
//        HuaWeiRegister.register(this);
//        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE, "24a401285899469248fcb7c7b1544b61");
        UMConfigure.init(mContext, "5c70c30fb465f5f532000185", "应用", UMConfigure.DEVICE_TYPE_PHONE, "24a401285899469248fcb7c7b1544b61");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setLogEnabled(true);
        //        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE, "24a401285899469248fcb7c7b1544b61");

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LoginInfo.setString(mContext, "deviceToken", deviceToken);
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("注册", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("注册", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Map<String, Class> map = new HashMap<>();
                map.put("1", PushProductActivity.class);    // 1 产品详情跳转
                map.put("2", PushRecruitActivity.class);    // 2 招聘详情跳转
                map.put("3", PushBackgroundActivity.class); // 3 后台帮别个推送
                map.put("4", MainActivity.class);           // 4 跳转到主页招标页面
                String type = msg.extra.get("type_class");
                if (type==null){
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), map.get(type));
                if (type.equals("1") || type.equals("2")) {
                    String phone_from = msg.extra.get("from");
                    String table_name = msg.extra.get("table_name");
                    String table_id = msg.extra.get("table_id");

                    intent.putExtra("phone", phone_from);
                    intent.putExtra("table_name", table_name);
                    intent.putExtra("table_id", table_id);
                } else if (type.equals("3")) {
                    String title = msg.extra.get("title");
                    String content = msg.extra.get("content");
                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                }else {
                    intent.putExtra("page", 2);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
                Log.e("注册", "2打开自定义通知栏" + msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                Log.e("注册", "3打开自定义通知栏" + msg.getRaw().toString());
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                List<String> data = Arrays.asList(msg.custom.split(","));
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                Log.e("注册", "4打开自定义通知栏" + msg.custom + "--" + data.size());
                Log.e("注册5", "" + data.get(0));
                Log.e("注册6", "" + data.get(1));
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

//    private void initPushSDK() {
//        /*
//         * 判断用户是否已同意隐私政策
//         * 当同意时，直接进行初始化；
//         * 当未同意时，待用户同意后，通过PushHelper.init(...)方法进行初始化。
//         */
//        boolean agreed = if (agreed && PushHelper.isMainProcess(this)) {
//            //建议在线程中执行初始化
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    PushHelper.init(getApplicationContext());
//                }
//            }).start();
//        }
//    }
//
//
//    public class PushHelper {
//        public  void init(Context context) {
//            //获取消息推送实例
//            PushAgent pushAgent = PushAgent.getInstance(context);
//            //注册推送服务，每次调用register方法都会回调该接口
//            pushAgent.register(new IUmengRegisterCallback() {
//
//                @Override
//                public void onSuccess(String deviceToken) {
//                    //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//                    Log.i(TAG, "注册成功：deviceToken：--> " + deviceToken);
//                }
//
//                @Override
//                public void onFailure(String s, String s1) {
//                    Log.e(TAG, "注册失败：--> " + "s:" + s + ",s1:" + s1);
//                }
//            });
//        }
//    }

    private void initEasyHttp() {
        EasyHttp.init(this);//网络框架初始化
        EasyHttp.getInstance()
                .setReadTimeOut(30 * 1000)
                .setWriteTimeOut(30 * 1000)
                .setConnectTimeout(30 * 1000);

    }



    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
