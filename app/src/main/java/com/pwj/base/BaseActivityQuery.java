package com.pwj.base;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.view.KeyEvent;


import com.itextpdf.text.PageSize;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.downLoadImg.ImageCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.IpConfig;
import com.pwj.utils.PdfItextUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;


/**
 * Created by 13688 on 2019/3/7.
 */

public class BaseActivityQuery extends Activity {
    public Context mContext;
    private ProgressDialog myDialog; // 保存进度框
    private DownImageUtil mDownImageUtil;
    private PdfItextUtil pdfItextUtil;
    private String path_base;
    private String path;
    private List<String> urls = new ArrayList<>();
    private List<String> mUrls = new ArrayList<>();
    public int type;
    public String title;
    private String title_pdf;
    public Map<Integer, String> content_pdf = new HashMap<>();
    public List<Comment> data_comment = new ArrayList<>();
    public int key;
    public Map<Integer, Integer> like_map = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initProgressDialog();

    }

    private void init() {
        type = getIntent().getIntExtra("type", 1);
        title = getIntent().getStringExtra("title");
    }

    private void initProgressDialog() {

        mDownImageUtil = new DownImageUtil(mContext);
        myDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progress_pdf));
        myDialog.setMessage("正在生成PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);

    }

    //分享成pdf文件
    public void savePictures(List<Product> data, int position) {
        path_base = IpConfig.PATH_DATA + "pdf";
        File file = new File(path_base);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            new DeleteFolder().DeleteFolder(path_base);
        }
        mUrls.clear();
        urls = Arrays.asList(data.get(position).getUrl().split(","));
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
                        createPDF(data, position);
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

    private void createPDF(List<Product> data, int position) {
        title = data.get(position).getTable();
        title_pdf = data.get(position).getUser_name() + "-" + data.get(position).getPhone() + "-" + title + ".pdf";
        path = path_base + File.separator + title_pdf;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    //判断是否有图片没有土司提示选择图片
                    //如果有进行合成
                    pdfItextUtil.addTitleToPdf(title).addTextToPdf(content_pdf.get(position));
                    pdfItextUtil.addTextLinkToPdf();
                    if (mUrls.size() > 0) {
                        for (int i = 0; i < mUrls.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mUrls.get(i)));
                            //这里当然可以输入文字和标题之类的。我们项目里面是只有图片所以。只需要.addImageToPdfCenterH();当然这里的图片在pdf中的放置可以通过设置的。看工具类
                            pdfItextUtil.addImageToPdfCenterH(mUrls.get(i), PageSize.A4.getWidth() - 20, PageSize.A4.getWidth() / bitmap.getWidth() * bitmap.getHeight());
                        }

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
                    sp.setImageUrl(IpConfig.URL_IMG_LOGO);
                    sp.setFilePath(path);
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    wechat.share(sp);
                    break;
            }
            return false;
        }
    });

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivityQuery.this, activity);
        startActivity(intent);
    }

    public void startActivity(String key, String value, Class<?> activity) {
        Intent intent = new Intent();
        intent.setClass(BaseActivityQuery.this, activity);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivityQuery.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }
}
