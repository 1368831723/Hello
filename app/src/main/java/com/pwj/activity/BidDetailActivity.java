package com.pwj.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import com.gyf.immersionbar.ImmersionBar;

import com.pwj.base.BaseActivityIssue;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.copy.SelectableTextHelper;
import com.pwj.dialog.DialogShare;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;
import java.io.File;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * Created by delphi0127 on 2018/7/15.
 */

public class BidDetailActivity extends AppCompatActivity {
    private SelectableTextHelper mSelectableTextHelper;
    private DialogShare dialogShare;
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.share)
    TextView share;
    @BindView(R.id.bid_detail_tv1)
    TextView bid_detail_tv1;
    //    @BindView(R.id.bid_detail_tv2)
//    TextView bid_detail_tv2;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    //    @BindView(R.id.bid_detail_tv3)
//    TextView bid_detail_tv3;
//    @BindView(R.id.bid_detail_tv4)
//    TextView bid_detail_tv4;
//    @BindView(R.id.bid_detail_tv5)
//    TextView bid_detail_tv5;
//    @BindView(R.id.bid_detail_tv6)
//    TextView bid_detail_tv6;
//    @BindView(R.id.bid_detail_tv7)
//    TextView bid_detail_tv7;
//    @BindView(R.id.bid_detail_tv8)
//    TextView bid_detail_tv8;
//    @BindView(R.id.bid_detail_tv9)
//    TextView bid_detail_tv9;
    private String url = "";
    private String url_pdf = "";
    private String title;
    private String content = "";
    private String path_base;
    private String title_pdf;
    private String path;
    private PdfItextUtil pdfItextUtil;
    private ProgressDialog myDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_detail);
        ButterKnife.bind(this);
        initData();
        initCopy();
    }

    private void initData() {
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        title_tv.setText("招标详情");
        Intent intent = getIntent();
//        int position=intent.getIntExtra("position",0);
        content = intent.getStringExtra("content").trim();
        String company = intent.getStringExtra("company").trim();
//        String contact = "联系人：" + intent.getStringExtra("contact").trim();
//        String phone = "电 话：" + intent.getStringExtra("phone").trim();
//        String email = "邮 箱：" + intent.getStringExtra("email").trim();
//        String location = "地 址：" + intent.getStringExtra("location").trim();
//        String bank = "银 行：" + intent.getStringExtra("bank").trim();
//        String account = "账 号：" + intent.getStringExtra("account").trim();
        url = intent.getStringExtra("url").trim();
        if(intent.getStringExtra("url_pdf")!=null){
            url_pdf = intent.getStringExtra("url_pdf").trim();
        }
        title = intent.getStringExtra("title").trim();
        bid_detail_tv1.setText("点击此处跳转至原文");
        if ("".equals(url_pdf)){
            openPDF();
        }else {
            String postfix = url_pdf.substring(url_pdf.lastIndexOf(".") + 1);
            if ("pdf".equals(postfix)){
                DownLoadPDF();
            }else {
                openPDF();
            }
        }
//        bid_detail_tv2.setText(company);
//        bid_detail_tv2.setText("查看pdf文件");
//        bid_detail_tv3.setText(contact);
//        bid_detail_tv4.setText(phone);
//        bid_detail_tv5.setText(email);
//        bid_detail_tv6.setText(location);
//        bid_detail_tv7.setText(bank);
//        bid_detail_tv8.setText(account);
//        bid_detail_tv9.setText("查看pdf文件");
    }

    private void initCopy() {
//        mSelectableTextHelper = new SelectableTextHelper.Builder(bid_detail_tv9)
//                .setSelectedColor(getResources().getColor(R.color.selected_blue))
//                .setCursorHandleSizeInDp(20)
//                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
//                .build();
    }

    @OnClick({R.id.title_im, R.id.bid_detail_tv1, R.id.share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                BidDetailActivity.this.finish();
                break;
            case R.id.bid_detail_tv1:
                Uri uri = Uri.parse(url);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
                break;
//            case R.id.bid_detail_tv2:
////                openPDF();
//                break;
            case R.id.share:
                Intent intent = new Intent(BidDetailActivity.this, ShareActivity.class);
                startActivityForResult(intent, 12);
                break;
        }
    }

    private void DownLoadPDF() {
        //        "http://119.18.207.203:80/pdf/2020-06-18-00.pdf"
        initProgress(this);
        EasyHttp.downLoad("http://119.18.207.203:80/pdf/"+url_pdf)
                .savePath(IpConfig.PATH_DATA + "pdf")
                .saveName(url_pdf)
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onError(ApiException e) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        myDialog.setMessage("正在加载中 "+progress);
                        myDialog.show();
                    }

                    @Override
                    public void onComplete(String path) {
                        myDialog.dismiss();
                        try {
                            pdfView.fromFile(new File(path))
                                    .load();
                        }catch (Exception e){
                           Util.showToast(BidDetailActivity.this,"加载失败");
                        }
                    }
                });

    }

    private void openPDF() {
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

        title_pdf = title + ".pdf";
        path = path_base + File.separator + title_pdf;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    //判断是否有图片没有土司提示选择图片
                    //如果有进行合成
                    pdfItextUtil.addTitleToPdf(title).addTextToPdf(content);
//                    pdfItextUtil.addTextLinkToPdf();
                    pdfItextUtil.close();
                    pdfView.fromFile(new File(path))
                            .load();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 12:
                if (data != null) {
                    switch (data.getIntExtra("share", 0)) {
                        case 1:
                            ShareParams sp = new ShareParams();
                            sp.setTitle(title);
                            sp.setUrl(url); // 标题的超链接
                            sp.setImageUrl(IpConfig.URL_IMG_LOGO);
                            sp.setShareType(Platform.SHARE_WEBPAGE);
                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                            // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
                            wechat.setPlatformActionListener(new PlatformActionListener() {
                                public void onError(Platform arg0, int arg1, Throwable arg2) {

                                }

                                public void onComplete(Platform arg0, int arg1, HashMap arg2) {

                                }

                                public void onCancel(Platform arg0, int arg1) {

                                }
                            });
                            wechat.share(sp);

                            break;
                        case 2:
                            ShareParams sp2 = new ShareParams();
                            sp2.setTitle(title);
                            sp2.setUrl(url); // 标题的超链接
                            sp2.setImageUrl(IpConfig.URL_IMG_LOGO);
                            sp2.setShareType(Platform.SHARE_WEBPAGE);
                            Platform moments = ShareSDK.getPlatform(WechatMoments.NAME);
                            // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
                            moments.setPlatformActionListener(new PlatformActionListener() {
                                public void onError(Platform arg0, int arg1, Throwable arg2) {
                                }

                                public void onComplete(Platform arg0, int arg1, HashMap arg2) {

                                }

                                public void onCancel(Platform arg0, int arg1) {

                                }
                            });
                            moments.share(sp2);

                            break;
                        case 3:
                            Util.showToast(BidDetailActivity.this, "QQ分享暂未开放");
//                            ShareParams sp3 = new ShareParams();
//                            sp3.setTitle(title);
//                            sp3.setTitleUrl(url);
//                            Platform qq = ShareSDK.getPlatform(QQ.NAME);
//                            // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//                            qq.setPlatformActionListener(new PlatformActionListener() {
//                                public void onError(Platform arg0, int arg1, Throwable arg2) {
//
//                                }
//
//                                public void onComplete(Platform arg0, int arg1, HashMap arg2) {
//
//                                }
//
//                                public void onCancel(Platform arg0, int arg1) {
//
//                                }
//                            });
//                            qq.share(sp3);
//                            Log.e(ContentValues.TAG, "qq1");
                            break;
                        case 4:
                            Util.showToast(BidDetailActivity.this, "微博分享暂未开放");
//                            ShareParams sp4 = new ShareParams();
//                            sp4.setTitle(title);
//                            sp4.setTitleUrl(url);
//                            Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//                            // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//                            weibo.setPlatformActionListener(new PlatformActionListener() {
//                                public void onError(Platform arg0, int arg1, Throwable arg2) {
//                                }
//
//                                public void onComplete(Platform arg0, int arg1, HashMap arg2) {
//
//                                }
//
//                                public void onCancel(Platform arg0, int arg1) {
//
//                                }
//                            });
//                            weibo.share(sp4);
//                            Log.e(ContentValues.TAG, "新浪");
                            break;
                    }
                }
                break;
            default:

                break;
        }
    }
    protected void initProgress(Context context) {
        myDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progress_pdf));
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }
    //设置弹窗宽度
    private void full() {
        WindowManager windowManager = BidDetailActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogShare.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.gravity = Gravity.BOTTOM;
        dialogShare.getWindow().setAttributes(lp);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
}
