package com.pwj.activity;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.pwj.base.BaseActivityIssueRequire;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;

import java.io.File;

import butterknife.OnClick;


/**
 * Created by 13688 on 2019/3/7.
 */

public class IssueRequire2 extends BaseActivityIssueRequire {
    private String et1_str = "";
    private String et2_str = "";
    private String et3_str = "";
    private EditText et1;
    private EditText et2;
    private EditText et3;


    @Override
    public void initChild() {
        initLayout(R.layout.activity_require2);
        initView();
    }

    private void initView() {
        title_tv.setText("发布维修需求");
        tv_location.setText("机器地址:");
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
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
        et3_str = et3.getText().toString().trim();
        if (submit()) {
//            if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str)) {
//                Util.showToast(this, "产品的参数不能为空");
//            } else {
//            if ("".equals(et3_str)) {
//            } else {
//                et3_str = "所需耗材：" + et3_str + "。\n";
//            }
//            content = "位置：" + location + "。\n" + "预估价格：" + et1_str + "元。\n预估维修人数：" + et2_str + "人。\n" + et3_str + "姓名：" + contact + "。\n电话：" + phone_address + "。" + remarks;
            content = "位置：" + location + "。\n";
            if (!TextUtils.isEmpty(et1_str)) {
                content = content + "预估价格：" + et1_str + "元。\n";
            }
            if (!TextUtils.isEmpty(et2_str)) {
                content = content + "预估维修人数：" + et2_str + "人。\n";
            }
            if (!TextUtils.isEmpty(et3_str)) {
                content = content + "所需耗材：" + et3_str + "。\n";
            }
            content = content + "姓名：" + contact + "。\n电话：" + phone_address + "。\n备注：" + remarks;
            if (mUrls != null && mUrls.length > 0) {
                if (url_type == 1){
                    insert1();
                }else{
                    compress();
                }
            } else {
                insert2();
            }
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
                insert2();
                break;
            }
        }
    }
    private void insert2() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "insert into " + ProductTableName.require2 + "(`url`,`url_type`,`contact`,`phone`,`phone_address`,`location`,`province`,`city`,`county`,`street`,`specific`,`price`,`people`,`material`, `remarks`, `content`, `date`)values('" + url + "','" + url_type + "','" + contact + "','" + phone + "','" + phone_address + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + remarks + "','" + content + "','" + date + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        insertIssue(phone, ProductTableName.require2);
                    }
                });
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
