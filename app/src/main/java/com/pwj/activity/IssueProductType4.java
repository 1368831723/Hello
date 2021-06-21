package com.pwj.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pwj.base.BaseActivityIssue;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.callback.ProgressDialogCallBack;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by 13688 on 2019/3/7.
 */

public class IssueProductType4 extends BaseActivityIssue {
    private String et1_str = "";
    private String et2_str = "";
    private EditText et1;
    private EditText et2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_product_4_factory);
        initView();
    }

    private void initView() {
        ll_postage.setVisibility(View.GONE);
        tv_location.setText("厂房的位置:");
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
    }

    @OnClick({R.id.submit})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                insert();
                break;
        }
    }

    private void insert() {
        et1_str = et1.getText().toString().trim();
        et2_str = et2.getText().toString().trim();
        if (submit()) {
//            if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
//                Util.showToast(this, "产品的参数不能为空");
//            } else {
            if (!TextUtils.isEmpty(et1_str)) {
                et1_str = et1_str + "㎡";
                content = "面积：" + et1_str + "。\n";
            }
            if (!TextUtils.isEmpty(et2_str)) {
                et2_str = et2_str + "元/月";
                content = content + "价格：" + et2_str + "。\n";
            }
            content = content + "位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
//            et1_str = et1_str + "㎡";
//            et2_str = et2_str + "元/月";
//            content = "面积：" + et1_str + "。\n价格：" + et2_str + "。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
            if (url_type == 1){
                insert1();
            }else{
                compress();
            }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String url = Test2.uploadFile(IpConfig.ip, mUrls);
//                        Connection connection = Jdbc.getConnection("root", "1q23lyc45j");
//                        Insert.insert_12(connection, url, et1_str + "㎡", et2_str + "元/月","位置:"+et3_str,"联系人:" +  name, phone);
//                    }
//                }).start();
//                startActivity(IssueSuccess.class);//点击按钮立马跳转
//                finish();                       //关闭当前页面
//            }
        }
    }
    @Override
    public void insert1() {
        for (int i = 0; i < mUrls.length; i++) {
            File file = new File(mUrls[i]);
            String filename = file.getName();
            String postfix = filename.substring(filename.lastIndexOf("."));
            String names = String.valueOf(time + i) + postfix;
            url = url + IpConfig.BASE_URL + names + ",";
            EasyHttp.post(IpConfig.URL_IMG + names)
                    .params("uploadfile", file, names, null)
                    .timeStamp(true)
                    .execute(new ProgressDialogCallBack<String>(mProgressDialog, true, true) {
                        @Override
                        public void onSuccess(String s) {
                        }
                    });
            if (i == mUrls.length - 1) {
                url = url.substring(0,url.length()-1);
                EasyHttp.post(IpConfig.URL_SQL)
                        .params("query", "insert into " + table_product[type] + "(`url`,`url_type`,`contact`,`phone`,`phone_address`,`location`,`area`, `price`,  `description`, `content`, `date`,`province`,`city`,`county`,`street`,`specific`)values('" + url + "','" + url_type + "','" + contact + "','" + phone + "','" + phone_address + "','" + location + "','" + et1_str + "','" + et2_str + "','" + description + "','" + content + "','" + date + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "')")
                        .timeStamp(true)
                        .execute(new SuccessCallBack<String>() {
                            @Override
                            public void onSuccess(String s) {
                                insertIssue(phone, table_product[type]);
                            }
                        });
                break;
            }
        }
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
