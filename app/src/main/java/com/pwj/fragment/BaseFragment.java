package com.pwj.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionFragment;
import com.itextpdf.text.PageSize;
import com.pwj.activity.FormActivity;
import com.pwj.activity.LoginActivity;

import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.ChatActivity;
import com.pwj.chat.EventBusInt;
import com.pwj.chat.MessageActivity;
import com.pwj.chat.Ofoffline;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.downLoadImg.ImageCallBack;
import com.pwj.downLoadImg.ImageCallBacks;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.ycbjie.notificationlib.NotificationUtils;
import com.zhouyou.http.EasyHttp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * Created by 13688 on 2019/5/4.
 */

public abstract class BaseFragment extends ImmersionFragment {
    private String locations = "";
    private Activity mActivity;
    private String phone;
    public List<Ofoffline> data_off;
    public int counts = 0;
    //分享pdf文件
    private String url3 = "";//用户图像
    public String content_pdf = "";
    private String path_base;
    private String title_pdf;
    private String path;
    private List<String> urls;
    private List<String> mUrls = null;
    private PdfItextUtil pdfItextUtil;
    private DownImageUtil mDownImageUtil;
    private ProgressDialog myDialog; // 分享进度框

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(getUserVisibleHint()) {
//            isVisible = true;
//            onVisible();
//        } else {
//            isVisible = false;
//            onInvisible();
//        }
//    }
//    /**
//     * 可见
//     */
//    public void onVisible() {
//        lazyLoad();
//    }


//    /**
//     * 不可见
//     */
//    public void onInvisible() {
//
//
//    }

    public void queryChat() {
        data_off = new ArrayList<>();
        phone = LoginInfo.getString(mActivity, "phone", "");
        String sql = "SELECT username,stanza,COUNT(*) AS COUNT FROM ofoffline WHERE username = '" + phone + "'";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT username,stanza,COUNT(*) AS COUNT FROM ofoffline WHERE username = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data_off.clear();
                        data_off = GsonUtils.getGsonToList(s, Ofoffline.class);
                        int count_off = LoginInfo.getInt(mActivity, "count_off", 0);
                        int counts = LoginInfo.getInt(mActivity, "counts", 0);
                        int count = data_off.get(0).getCOUNT();//data_off的长度就是1，多少条未读count
                        if (count > count_off) {
                            Log.e("离线消息", "onSuccess1: " + count + "--" + data_off.get(0).getStanza());
                            EventBus.getDefault().post(new EventBusInt(counts + count - count_off));
                            LoginInfo.setInt(mActivity, "count_off", count);
                            if (count_off != count) {
                                NotificationUtils notificationUtils = new NotificationUtils(mActivity);
                                Intent intent = new Intent(mActivity, MessageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);           //添加为栈顶Activity
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(mActivity, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationUtils
                                        .setContentIntent(resultPendingIntent)
                                        //设置sound
                                        .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                                        //设置优先级
                                        .setPriority(Notification.PRIORITY_MAX)
                                        //自定义震动效果
                                        //.setVibrate(vibrate)
                                        .sendNotification(1, "消息提醒", "你有" + count + "条未读消息", R.drawable.logo);
                                LoginInfo.setInt(mActivity, "counts", counts + count - count_off);
                            }
                        } else {
                            Log.e("离线消息", "onSuccess2: ");
                            EventBus.getDefault().post(new EventBusInt(counts));
                        }

                    }
                });
    }

    protected void startChat(String other_phone, String other_name, String other_img) {
        if (checkPhone(mActivity)) {
            if (phone.equals(other_phone)) {
                Util.showToast(mActivity, "对不起,不能与自己聊天");
            } else {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("other_phone", other_phone);
                intent.putExtra("other_name", other_name);
                intent.putExtra("other_img", other_img);
                startActivity(intent);
            }
        }
    }

    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.title_relative)
                .statusBarDarkFont(true)
                .init();
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(mActivity, activity);
        startActivity(intent);
    }

    protected void startActivity(Class<?> activity, int page) {
        Intent intent = new Intent();
        intent.setClass(mActivity, activity);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    protected boolean checkPhone(Context mContext) {
        phone = LoginInfo.getString(mContext, "phone", "");
        if (phone.equals("")) {
            startActivity(LoginActivity.class);
            return false;
        }
        return true;
    }

    protected void initProgress(Context context) {
        mDownImageUtil = new DownImageUtil(context);
        myDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progress_pdf));
        myDialog.setMessage("正在生成PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }

    protected void clickShareNoPicture(String url3, String user_name, String contact, String job, String other_phone, String title) {
        this.url3 = url3;
        path_base = IpConfig.PATH_DATA + "pdf";
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new DeleteFolder().DeleteFolder(path_base);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
        title_pdf = title + "-" + user_name + ".pdf";
//        title_pdf = user_name + "-" + contact + job + "-" + other_phone + "-" + title + ".pdf";
        path = path_base + File.separator + title_pdf;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    //判断是否有图片没有土司提示选择图片
                    //如果有进行合成
                    pdfItextUtil.addTitleToPdf(title).addTextToPdf(content_pdf);
                    pdfItextUtil.addTextLinkToPdf();
                    pdfItextUtil.close();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pdfItextUtil != null) {
                        pdfItextUtil.close();
                    }
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    ShareParams sp = new ShareParams();
                    sp.setTitle(title_pdf);
                    sp.setShareType(Platform.SHARE_FILE);
                    sp.setImageUrl(url3);
                    sp.setFilePath(path);
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    wechat.share(sp);
                    wechat.setPlatformActionListener(new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            Log.e("分享", "成功" + hashMap);
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            Log.e("分享", "错误" + throwable);
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            Log.e("分享", "取消" + platform);
                        }
                    });
                    break;
            }
            return false;
        }
    });

    protected void clickShare(String url3, String user_name, String contact, String job, String other_phone, String title, int position, String url) {
//        urls = new ArrayList<>();
        this.url3 = url3;
        if (mUrls == null) {
            mUrls = new ArrayList<>();
        }
        path_base = IpConfig.PATH_DATA + "pdf";
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new DeleteFolder().DeleteFolder(path_base);
        }
        mUrls.clear();
        urls = Arrays.asList(url.split(","));
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
        for (int i = 0; i < urls.size(); i++) {
            mDownImageUtil.onDownLoad(urls.get(i), i, "pdf");
            mDownImageUtil.setImageCallBack(new ImageCallBack() {

                @Override
                public void onSuccess(int i, String url) {
                    mUrls.add(url);
                    if (i == urls.size() - 1) {
                        createPDF(user_name, contact, job, other_phone, title, position);
                    }
                }

                @Override
                public void onFailed() {
                    if (myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }
            });
        }
    }

    private void createPDF(String user_name, String contact, String job, String other_phone, String title, int position) {
        title_pdf = title + "-" + user_name + ".pdf";
//        title_pdf = user_name + "-" + contact + job + "-" + other_phone + "-" + title + ".pdf";
        path = path_base + File.separator + title_pdf;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    //判断是否有图片没有土司提示选择图片
                    //如果有进行合成
                    pdfItextUtil.addTitleToPdf(title).addTextToPdf(content_pdf);
                    pdfItextUtil.addTextLinkToPdf();
                    if (mUrls.size() > 0) {
                        for (int i = 0; i < mUrls.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mUrls.get(i)));
                            //这里当然可以输入文字和标题之类的。我们项目里面是只有图片所以。只需要.addImageToPdfCenterH();当然这里的图片在pdf中的放置可以通过设置的。看工具类
                            pdfItextUtil.addImageToPdfCenterH(mUrls.get(i), PageSize.A4.getWidth() - 20, PageSize.A4.getWidth() / bitmap.getWidth() * bitmap.getHeight());
                        }

                    } else {

                    }
                    pdfItextUtil.close();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pdfItextUtil != null) {
                        pdfItextUtil.close();
                    }
                }
            }
        }).start();
    }

    protected void share(List<Product> data, String title, String keyWords) {

        title_pdf = title + keyWords + ".pdf";
        path_base = IpConfig.PATH_DATA + "pdf";
        path = path_base + File.separator + title_pdf;
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new DeleteFolder().DeleteFolder(path_base);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    for (int i = 0; i < data.size(); i++) {
                        if (i < 10) {
                            content_pdf = data.get(i).getContent() + "\n" + "\n";
                            pdfItextUtil.addTitleToPdf("第" + (i + 1) + "条").addTextToPdf(content_pdf);
                            if (i == data.size() - 1) {
                                pdfItextUtil.close();
                                handler.sendEmptyMessageDelayed(1, 1000);
                                break;
                            }
                            if (i == 9) {
                                pdfItextUtil.close();
                                handler.sendEmptyMessageDelayed(1, 100);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pdfItextUtil != null) {
                        pdfItextUtil.close();
                    }
                }
            }
        }).start();
    }

    protected void shares(List<Product> data, String title, String keyWords) {
        if (mUrls == null) {
            mUrls = new ArrayList<>();
        }
        title_pdf = title + keyWords + ".pdf";
        path_base = IpConfig.PATH_DATA + "pdf";
        path = path_base + File.separator + title_pdf;
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new DeleteFolder().DeleteFolder(path_base);
        }
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        int count = 0;
        int count_before = 0;
        for (int i = 0; i < data.size(); i++) {
            if (!"".equals(data.get(i).getUrl()) && data.get(i).getUrl_type() == 1) {
                count = count + 1;
                if (count <= 5) {
                    count_before = i;
                }
                if (count == 6) {
                    count = i;
                    break;
                }
            }
        }
        if (count < 5) {
            count = data.size();
        }
        for (int i = 0; i < count; i++) {
            if (count_before > 0) {
                if (!"".equals(data.get(i).getUrl()) && data.get(i).getUrl_type() == 1) {
                    urls = Arrays.asList(data.get(i).getUrl().split(","));
                    for (int j = 0; j < urls.size(); j++) {
                        if (i == count_before && j == urls.size() - 1) {
                            String url = urls.get(j);
                            String name = url.substring(url.lastIndexOf("/") + 1);
                            mDownImageUtil.onDownLoads(url, i, j, name, true);
                            int finalCount = count;
                            mDownImageUtil.setImageCallBacks(new ImageCallBacks() {
                                @Override
                                public void onSuccess(int i, int j, String url) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bitmap bitmap = null;
                                            try {
                                                Log.e("路径1", "run: " + path);
                                                pdfItextUtil = new PdfItextUtil(path);
                                                for (int k = 0; k < finalCount; k++) {
                                                    String url = data.get(k).getUrl();
                                                    content_pdf = data.get(k).getContent();
                                                    pdfItextUtil.addTitleToPdf("第" + (k + 1) + "条").addTextToPdf(content_pdf);
                                                    if (!"".equals(url) && data.get(k).getUrl_type() == 1) {
                                                        List<String> urls = Arrays.asList(url.split(","));
                                                        Log.e("路径2 ", "为 " + urls.size());
                                                        for (int m = 0; m < urls.size(); m++) {
                                                            String url_m = path_base + File.separator + urls.get(m).substring(urls.get(m).lastIndexOf("/") + 1);
                                                            Log.e("路径3 ", "为 " + url_m);
                                                            bitmap = BitmapFactory.decodeStream(new FileInputStream(url_m));
                                                            pdfItextUtil.addImageToPdfCenterH(url_m, PageSize.A4.getWidth() - 20, PageSize.A4.getWidth() / bitmap.getWidth() * bitmap.getHeight());
                                                        }
                                                    }
                                                    if (k == finalCount - 1) {
                                                        pdfItextUtil.close();
                                                        handler.sendEmptyMessageDelayed(1, 1000);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }

                                @Override
                                public void onFailed() {

                                }
                            });
                        } else {
                            String url = urls.get(j);
                            String name = url.substring(url.lastIndexOf("/") + 1);
                            mDownImageUtil.onDownLoads(url, i, j, name, false);
                        }
//
//                        mDownImageUtil.setImageCallBacks(new ImageCallBacks() {
//                            @Override
//                            public void onSuccess(int i, int j, String url) {
//                                Bitmap bitmap = null;
//                                try {
//                                    if (j == 0) {
//                                        pdfItextUtil.addTitleToPdf("第" + (i + 1) + "条").addTextToPdf(content_pdf);
//                                    }
//                                    bitmap = BitmapFactory.decodeStream(new FileInputStream(url));
//                                    pdfItextUtil.addImageToPdfCenterH(url, PageSize.A4.getWidth() - 20, PageSize.A4.getWidth() / bitmap.getWidth() * bitmap.getHeight());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                } catch (DocumentException e) {
//                                    e.printStackTrace();
//                                }
//                                if (i == data.size() - 1) {
//                                    if (j == urls.size() - 1) {
//                                        pdfItextUtil.close();
////                                                handler.sendEmptyMessage(1);
//                                        if (myDialog.isShowing()) {
//                                            myDialog.dismiss();
//                                        }
//                                        ShareParams sp = new ShareParams();
//                                        sp.setTitle(title_pdf);
//                                        sp.setShareType(Platform.SHARE_FILE);
//                                        sp.setImageUrl(IpConfig.URL_IMG_LOGO);
//                                        sp.setFilePath(path);
//                                        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//                                        wechat.share(sp);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailed() {
//                                if (myDialog.isShowing()) {
//                                    myDialog.dismiss();
//                                }
//                            }
//                        });
                    }
                } else {

                }
            }
        }

    }

    protected void insertKeyWords(String keyWords, String page) {
        String uuid = LoginInfo.getString(mActivity, "uuid", "");
        String dates = SearchUtil.getInstance().getNowDate();
        String brand = SearchUtil.getInstance().getSystemBrand();
        String model = SearchUtil.getInstance().getSystemModel();
        String os_version = SearchUtil.getInstance().getSystemVersion();
        SearchUtil.getInstance().initLocation(mActivity, new StringCallbackOne() {
            @Override
            public void stringOne(String location, double longitude, double latitude) {
                locations = location;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EasyHttp.post(IpConfig.URL_SQL)
                        .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
                        .timeStamp(true)
                        .execute(new SuccessCallBack<String>() {
                        });
            }
        }, 1000);
    }


    //设置弹窗宽度
    protected void full_dialog(Dialog dialog) {
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()) - 150; //设置宽度
        dialog.getWindow().setAttributes(lp);
    }
}
