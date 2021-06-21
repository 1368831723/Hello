package com.pwj.pages;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Product;
import com.pwj.chat.OtherUserActivity;
import com.pwj.dialog.DialogChooseMap;
import com.pwj.downLoadImg.DeleteFolder;
import com.pwj.downLoadImg.DownImageUtil;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.utils.IpConfig;
import com.pwj.utils.PdfItextUtil;
import com.pwj.utils.SearchUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by han13688 on 2018/4/25.
 */

public class RecruitViewOne extends LinearLayout implements View.OnClickListener {
    private static List<Product> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private static Activity mActivity;
    private static Context mContext;
    private static BaseRcyAdapter adapter;
    private DownImageUtil mDownImageUtil;
    private ProgressDialog myDialog; // 分享进度框
    private String url3 = "";//用户图像
    private String title_pdf;
    private String path;
    private PdfItextUtil pdfItextUtil;
    private String location;
    private double longitude;
    private double latitude;
    private boolean baiDu;
    private boolean gaoDe;
    private DialogChooseMap dialogChooseMap;

    public static RecruitViewOne getView(Activity activity, Context context, List<Product> datas) {
        mActivity = activity;
        mContext = context;
        data = datas;
        RecruitViewOne recruitViewOne = (RecruitViewOne) View.inflate(context, R.layout.view_recruit_one, null);
        recruitViewOne.initProgress(context);
        recruitViewOne.initData(recruitViewOne, data);
        return recruitViewOne;
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

    public static void refresh() {
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    public RecruitViewOne(Context context) {
        super(context);
    }

    public RecruitViewOne(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecruitViewOne(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initData(RecruitViewOne recruitViewOne, List<Product> data) {
        if (getChildCount() == 0) {
            return;
        }

        recyclerView = recruitViewOne.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter = new BaseRcyAdapter(data, R.layout.item_recruit) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                ImageView img = holder.getView(R.id.img);
                TextView name_tv = holder.getView(R.id.name_tv);
                TextView tv = holder.getView(R.id.tv);
                Button share = holder.getView(R.id.share);
                Button go = holder.getView(R.id.go);
                img.setOnClickListener(RecruitViewOne.this);
                share.setOnClickListener(RecruitViewOne.this);
                go.setOnClickListener(RecruitViewOne.this);
                img.setTag(position);
                share.setTag(position);
                go.setTag(position);
                String url = data.get(position).getUrl3();
                name_tv.setText(data.get(position).getUser_name());
                tv.setText(data.get(position).getContent());
                Pattern pattern = Pattern.compile("\\d{11,}");
                Linkify.addLinks(tv, pattern, "tel:");
                Glide.with(mActivity).load(url).into(img);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void initLocations() {
        SearchUtil.getInstance().initLocation(mActivity, new StringCallbackOne() {
            @Override
            public void stringOne(String location, double longitude, double latitude) {
                RecruitViewOne.this.location = location;
                RecruitViewOne.this.longitude = longitude;
                RecruitViewOne.this.latitude = latitude;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (view.getId()) {
            case R.id.img:
                Intent intent = new Intent(mActivity, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(position).getUser_name());
                intent.putExtra("other_phone", data.get(position).getPhone());
                intent.putExtra("other_img", data.get(position).getUrl3());
                mContext.startActivity(intent);
                break;
            case R.id.go:
                selectMap(position);
                break;
            case R.id.share:
                String user_name = data.get(position).getUser_name();
                String contact = data.get(position).getContact();
                String url3 = data.get(position).getUrl3();
                String job = data.get(position).getJob();
                String other_phone = data.get(position).getPhone();
                String title = data.get(position).getProduct_name();
                if (job == null) {
                    job = "";
                }
                shareRecruit(url3,user_name, contact, job, other_phone, title, position);
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
                    Platform.ShareParams sp = new Platform.ShareParams();
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

    protected void shareRecruit(String url3,String user_name, String contact, String job, String other_phone, String title, int position) {
        this.url3 = url3;
        String path_base = IpConfig.PATH_DATA + "pdf";
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
                    pdfItextUtil.addTitleToPdf(title).addTextToPdf(data.get(position).getContent());
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

    private void selectMap(int position) {
        double com_longitude = data.get(position).getLongitude();
        double com_latitude = data.get(position).getLatitude();
        String end_location = data.get(position).getLocation();
        checkMap();
        dialogChooseMap = new DialogChooseMap(mActivity, baiDu, gaoDe, R.style.dialog_choose, new DialogChooseMap.ICustomDialogEventListener() {
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
                        mContext.startActivity(intent1);
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
                        mContext.startActivity(intent2);
                        break;
                }
            }
        });
        dialogChooseMap.show();
        full_choose_map();
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
    private void full_choose_map() {
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogChooseMap.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogChooseMap.getWindow().setAttributes(lp);
    }
}
