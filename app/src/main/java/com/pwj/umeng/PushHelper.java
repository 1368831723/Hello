package com.pwj.umeng;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.pwj.utils.LoginInfo;
import com.taobao.accs.ACCSClient;
import com.taobao.accs.AccsClientConfig;
import com.taobao.agoo.TaobaoRegister;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;


/**
 * PushSDK集成帮助类
 */
public class PushHelper {

    private static final String TAG = PushHelper.class.getSimpleName();

    /**
     * 预初始化，已添加子进程中初始化sdk。
     * 使用场景：用户未同意隐私政策协议授权时，延迟初始化
     *
     * @param context 应用上下文
     */
    public static void preInit(Context context) {
        try {
            //解决推送消息显示乱码的问题
            AccsClientConfig.Builder builder = new AccsClientConfig.Builder();
            builder.setAppKey("umeng:" + PushConstants.APP_KEY);
            builder.setAppSecret(PushConstants.MESSAGE_SECRET);
            builder.setTag(AccsClientConfig.DEFAULT_CONFIGTAG);
            ACCSClient.init(context, builder.build());
            TaobaoRegister.setAccsConfigTag(context, AccsClientConfig.DEFAULT_CONFIGTAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UMConfigure.preInit(context, PushConstants.APP_KEY, PushConstants.CHANNEL);
        if (!isMainProcess(context)) {
            init(context);
        }
    }

    /**
     * 初始化。
     * 场景：用户已同意隐私政策协议授权时
     *
     * @param context 应用上下文
     */
    public static void init(Context context) {
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息
        UMConfigure.init(
                context,
                PushConstants.APP_KEY,
                PushConstants.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE,
                PushConstants.MESSAGE_SECRET
        );

        //获取消息推送实例
        final PushAgent mPushAgent = PushAgent.getInstance(context);
//        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //服务端控制声音
//        pushAgent.setDisplayNotificationNumber(0);
//        MessageSharedPrefs.getInstance(context).setDisplayNotificationNumber(100);
        //注册推送服务，每次调用register方法都会回调该接口
        Log.e("注册--", "aaa");
//        mPushAgent.register(new IUmengRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                LoginInfo.setString(context, "deviceToken", deviceToken);
//                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//                Log.e("注册", "注册成功：deviceToken：-------->  " + deviceToken);
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//                Log.e("注册", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
//            }
//        });
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LoginInfo.setString(context, "deviceToken", deviceToken);
                Log.e("注册", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                Log.e("注册--", "ccc");
                Log.e("注册", "register failure：--> " + "code:" + errCode + ",desc:" + errDesc);
            }
        });
        if (isMainProcess(context)) {
            registerDeviceChannel(context);
        }
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     *
     * @param context 应用上下文
     */
    private static void registerDeviceChannel(Context context) {
        //华为，注意华为通道的初始化参数在minifest中配置
        HuaWeiRegister.register((Application) context.getApplicationContext());
        VivoRegister.register((Application) context.getApplicationContext());
        OppoRegister.register((Application) context.getApplicationContext(), "d7dd7b9371504c3eb5a47467164cca28", "31d796a26a6547ce9eff1041538a5b46");
//        //小米通道，填写您在小米后台APP对应的xiaomi id和key
//        MiPushRegistar.register(context, PushConstants.MI_ID, PushConstants.MI_KEY);
//        //魅族，填写您在魅族后台APP对应的app id和key
//        MeizuRegister.register(context, PushConstants.MEI_ZU_ID, PushConstants.MEI_ZU_KEY);
//        //OPPO，填写您在OPPO后台APP对应的app key和secret
//        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
//        //vivo，注意vivo通道的初始化参数在minifest中配置
//        VivoRegister.register(context);
    }

    /**
     * 是否运行在主进程
     *
     * @param context 应用上下文
     * @return true: 主进程；false: 子进程
     */
    public static boolean isMainProcess(Context context) {
        return UMUtils.isMainProgress(context);
    }

}
