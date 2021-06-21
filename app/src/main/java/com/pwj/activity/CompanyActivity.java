package com.pwj.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Bidding;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.downLoadImg.DeleteFolder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.wechat.friends.Wechat;


public class CompanyActivity extends Activity {
    private List<Bidding> data = new ArrayList<>();
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    private BaseRcyAdapter adapter;
    private String company;
    private String content ;
    private String path_base;
    private String title_pdf;
    private String path;
    private PdfItextUtil pdfItextUtil;
    private ProgressDialog myDialog; // 分享进度框
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        initProgress(this);
        initData();
    }

    protected void initProgress(Context context) {
        myDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progress_pdf));
        myDialog.setMessage("正在生成PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }
    private void initData() {
        ImmersionBar.with(this)
                .titleBarMarginTop(title_relative)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        company = getIntent().getStringExtra("company");
        title_tv.setText(company);

        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT times,title,link,content,company FROM bid_pwj where company = '" + company + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data = GsonUtils.getGsonToList(s, Bidding.class);
                        data.addAll(data);
                        initAdapter();
                    }
                });
    }

    private void initAdapter() {
        adapter = new BaseRcyAdapter(data, R.layout.item_word) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                TextView bid_tv1 = holder.getView(R.id.bid_tv1);
                TextView bid_tv2 = holder.getView(R.id.bid_tv2);
                TextView bid_tv3 = holder.getView(R.id.bid_tv3);
                bid_tv1.setText(data.get(position).getTimes());
                bid_tv2.setText(data.get(position).getTitle());
                bid_tv3.setText(data.get(position).getLink());
            }
        };
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CompanyActivity.this, BidDetailActivity.class);
                intent.putExtra("company", data.get(position).getCompany());
                intent.putExtra("content", data.get(position).getContent());
                intent.putExtra("url", data.get(position).getLink());
                intent.putExtra("url_pdf", data.get(position).getUrl_pdf());
                intent.putExtra("title", data.get(position).getTitle());
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CompanyActivity.this);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(CompanyActivity.this, DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(CompanyActivity.this, R.drawable.custom_divider));
//        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.title_im,R.id.tv_share})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.title_im:
                finish();
                break;
            case R.id.tv_share:
                if(data.size()>0){
                    openPDF();
                }else {
                    Util.showToast(CompanyActivity.this,"数据加载中");
                }
                break;
        }
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
    private void openPDF() {
        content = "";
//        for (int i = 0; i < data.size(); i++) {
//            if (i>0){
//                content = content + "\n"+ "\n"+ "\n"+ "\n"+ "\n"+"-----------------------第 "+(i+1)+" 条 招 标 信 息--------------------"+"\n"+ "\n"+data.get(i).getContent();
//            }else {
//                content = data.get(i).getContent();
//            }
//        }
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
        title_pdf = company + ".pdf";
        path = path_base + File.separator + title_pdf;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdfItextUtil = new PdfItextUtil(path);
                    //判断是否有图片没有土司提示选择图片
                    //如果有进行合成
                    pdfItextUtil.addTitleLeftToPdf(data.get(0).getCompany()+"的招标信息"+data.size()+"条"+"\n");
                    pdfItextUtil.addTitleLeftToPdf("时间："+ SearchUtil.getInstance().getNowDate()+"\n");
                    pdfItextUtil.addTextToPdf("\n");
                    for (int i = 0; i < data.size(); i++) {
                        pdfItextUtil.addTitleLeftToPdf("(第"+(i+1)+"条)"+data.get(i).getTitle());
                        pdfItextUtil.addTextLinkTitleToPdf(data.get(i).getLink()).addTextToPdf("\n");
                    }
                    for (int i = 0; i < data.size(); i++) {
                        pdfItextUtil.addTitleToPdf("第"+(i+1)+"条").addTextToPdf(data.get(i).getContent());
                    }
//                    pdfItextUtil.addTitleToPdf(company).addTextToPdf(content);
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
}
