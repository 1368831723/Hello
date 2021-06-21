package com.pwj.base;


import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.ImageInfo;


import com.itextpdf.text.PageSize;

import com.pwj.activity.LoginActivity;
import com.pwj.activity.ProductDetailActivity;

import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.ChatActivity;
import com.pwj.dialog.DialogChooseMap;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.downLoadImg.ImageCallBack;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.ufreedom.uikit.FloatingText;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * Created by 13688 on 2019/2/25.
 */

public class BaseActivityComment extends Activity {
    private FrameLayout frm_content;
    private View view;
    public LinearLayout title_linear;
    public TextView title_tv;

    public List<Comment> data_comment = new ArrayList<>();
    public Intent intent;
    public final static int position_all = 5 << 24;
    public final static int btn_paly = 6 << 24;
    public final static int circleBar = 7 << 24;
    public final static int content = 8 << 24;
    public final static int count_comment = 9 << 24;
    public final static int view_img_like = 10 << 24;
    public final static int view_tv_like = 11 << 24;
    public String phone;
    public int flag_like = 0;
    public int key;
    public SparseIntArray like_map = new SparseIntArray();
    private String path_base;
    private DownImageUtil mDownImageUtil;
    private List<String> urls = new ArrayList<>();
    private List<String> mUrls = new ArrayList<>();
    private PdfItextUtil pdfItextUtil;
    private String url3 = "";//用户图像
    private String title_pdf;
    private String path;
    public String content_pdf = "";
    private ProgressDialog myDialog; // 分享进度框
    private String locations = "";

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

    protected void startChat(String other_phone, String other_name, String other_img) {
        if (checkPhone(BaseActivityComment.this)) {
            if (phone.equals(other_phone)) {
                Util.showToast(BaseActivityComment.this, "对不起,不能与自己聊天");
            } else {
                Intent intent = new Intent(BaseActivityComment.this, ChatActivity.class);
                intent.putExtra("other_phone", other_phone);
                intent.putExtra("other_name", other_name);
                intent.putExtra("other_img", other_img);
                startActivity(intent);
            }
        }
    }

    //设置点击事件时，内容和评论数的tag要加上去
    protected void clickImgComment(Context context, View view, String title, int id, String table_name, String url3, String user_name, String other_phone) {
        String contents = (String) view.getTag(content);
        String count_comments = (String) view.getTag(count_comment);
        intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("table_id", id);
        intent.putExtra("table_name", table_name);
        intent.putExtra("user_img", url3);
        intent.putExtra("user_name", user_name);
        intent.putExtra("other_phone", other_phone);
        intent.putExtra("content", contents);
        intent.putExtra("count_comment", count_comments);
        startActivity(intent);
    }

    protected void clickImgLike(Activity activity, View view, int position, int id, String table_name, int flag_like) {
        if (checkPhone(activity)) {
            ImageView img_like = (ImageView) view.getTag(view_img_like);
            TextView tv_like = (TextView) view.getTag(view_tv_like);
            switch (flag_like) {
                case 0:
                    plus_like(activity, position, id, table_name, img_like, tv_like);
                    break;
                case 1:
                    cut_like(activity, position, id, table_name, img_like, tv_like);
                    break;
            }
        }
    }

    protected void clickShare(String url3, String user_name, String other_phone, String title, int position, String url) {
        this.url3 = url3;
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
                        createPDF(user_name, other_phone, title, position);
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

    protected void clickShareNoPicture(String url3, String user_name, String other_phone, String title) {
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
                    break;
            }
            return false;
        }
    });

    private void createPDF(String user_name, String other_phone, String title, int position) {
        title_pdf = title + "-" + user_name + ".pdf";
//      title_pdf = user_name + "-" + other_phone + "-" + title + ".pdf";
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

    protected void share(List<Product> data, String title) {

        title_pdf = title + ".pdf";
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

    private void plus_like(Activity activity, int position, int id, String table_name, ImageView img_like, TextView tv_like) {
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like_selected));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) + 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(activity)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("+1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into comment_3_like_product(`phone`,`table_id`,`table_name`) values('" + phone + "','" + id + "','" + table_name + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_name + " SET `like`= `like`+1 WHERE id = '" + id + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {

                                    }
                                });
                    }
                });
    }

    private void cut_like(Activity activity, int position, int id, String table_name, ImageView img_like, TextView tv_like) {
        img_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
        String count_like = tv_like.getText().toString();
        tv_like.setText(String.valueOf(Integer.parseInt(count_like) - 1));
        FloatingText floatingText = new FloatingText.FloatingTextBuilder(activity)
                .textColor(Color.RED) // floating  text color
                .textSize(100)   // floating  text size
                .textContent("-1") // floating  text content
                .offsetX(50) // the x offset  relate to the attached view
                .offsetY(-100) // the y offset  relate to the attached view
                .build();
        floatingText.attach2Window();
        floatingText.startFloating(img_like);
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "delete from comment_3_like_product WHERE `table_id` = '" + id + "' AND phone = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "UPDATE " + table_name + " SET `like`= `like`-1 WHERE id = '" + id + "'")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {

                                    }
                                });
                    }
                });
    }

    private boolean baiDu;
    private boolean gaoDe;

    protected void selectMap(Context context, List<Product> data, int position, double latitude, double longitude) {
        double com_longitude = data.get(position).getLongitude();
        double com_latitude = data.get(position).getLatitude();
        String end_location = data.get(position).getLocation();
        checkMap();
        DialogChooseMap dialogChooseMap = new DialogChooseMap(context, baiDu, gaoDe, R.style.dialog_choose, new DialogChooseMap.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(int id) {
                switch (id) {
                    case R.id.dialog_bai_du:
                        Intent intent1 = null;
                        try {
                            intent1 = Intent.getIntent("intent://map/direction?origin=latlng:" + latitude + "," + longitude + "|name:" + "请选择起始位置" + "&destination=latlng:" + com_latitude + "," + com_longitude + "|name:" + end_location + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(intent1);
//                            Log.e("起点经纬度", "my_longitude: " + my_longitude + " my_latitude" + my_latitude);
                        break;
                    case R.id.dialog_gao_de:
                        Intent intent2 = null;
                        try {
                            intent2 = Intent.getIntent("androidamap://navi?sourceApplication=appname&poiname=" + end_location + "&lat=" +
                                    com_latitude + "&lon=" + com_longitude + "&dev=1&style=2");
//                                intent2 = Intent.getIntent("androidamap://route?sourceApplication=softname" + "&sname=" + "请选择起点" + "&dname=" + end_location + "&dev=0&m=0&t=1");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(intent2);
                        break;
                }
            }
        });
        dialogChooseMap.show();
        full_choose_map(dialogChooseMap);
    }

    private void checkMap() {
        if (isInstallByread("com.baidu.BaiduMap")) {
            baiDu = true;
        } else {
            baiDu = false;
        }
        if (isInstallByread("com.autonavi.minimap")) {
            gaoDe = true;
        } else {
            gaoDe = false;
        }
    }

    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    //设置弹窗宽度
    private void full_choose_map(DialogChooseMap dialogChooseMap) {
        WindowManager windowManager = BaseActivityComment.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogChooseMap.getWindow().setAttributes(lp);
    }

    protected void insertSearch(Context context, String page, String keyWords, String locations) {
        String uuid = LoginInfo.getString(context, "uuid", "");
        String brand = SearchUtil.getInstance().getSystemBrand();
        String model = SearchUtil.getInstance().getSystemModel();
        String os_version = SearchUtil.getInstance().getSystemVersion();
        String dates = SearchUtil.getInstance().getNowDate();
        if ("".equals(keyWords)) {
            Util.showToast(context, "请输入关键词");
        } else {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values('" + uuid + "','" + brand + "','" + model + "','" + os_version + "','" + dates + "','" + page + "','" + keyWords + "','" + locations + "')")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {

                        }
                    });
        }
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivityComment.this, activity);
        startActivity(intent);
    }

    public List<ImageInfo> getImageInfos(List<Product> data, int position) {
        List<ImageInfo> imageInfos = new ArrayList<>();
        List<String> uri_list = Arrays.asList(data.get(position).getUrl().split(","));
        for (String url : uri_list) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setBigImageUrl(url);
            imageInfo.setThumbnailUrl(url);
            imageInfos.add(imageInfo);
        }
        return imageInfos;
    }

    protected void insertKeyWords(String keyWords, String page) {
        String uuid = LoginInfo.getString(BaseActivityComment.this, "uuid", "");
        String dates = SearchUtil.getInstance().getNowDate();
        String brand = SearchUtil.getInstance().getSystemBrand();
        String model = SearchUtil.getInstance().getSystemModel();
        String os_version = SearchUtil.getInstance().getSystemVersion();
        SearchUtil.getInstance().initLocation(BaseActivityComment.this, new StringCallbackOne() {
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
