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
import com.pwj.utils.IpConfig;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by 13688 on 2019/3/7.
 */

public class IssueProductOffice extends BaseActivityIssue {
    private String et1_str = "";
    private String et2_str = "";
    private EditText et1;
    private EditText et2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_product_office);
        initView();
    }

    private void initView() {
        ll_postage.setVisibility(View.GONE);
        tv_location.setText("位置:");
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
//            if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str)) {
//                Util.showToast(this, "产品的参数不能为空");
//            } else {
            if (!TextUtils.isEmpty(et1_str)) {
                content = "办事处名称：" + et1_str + "。\n";
            } else {
                Util.showToast(IssueProductOffice.this, "办事处名称不能为空");
                Log.e("办事处","名称为空");
                return;
            }
            if (!TextUtils.isEmpty(et2_str)) {
                content = content + "主营产品：" + et2_str + "。\n";
            } else {
                Util.showToast(IssueProductOffice.this, "办事处主营的产品不能为空");
                return;
            }
            content = content + "位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
//            et2_str = "规格：" + et2_str;
//            et3_str = "单价：" + et3_str;
//            content = "名称：" + et1_str + "。\n" + et2_str + "。\n" + et3_str + "。\n运送：" + consign + "。\n运费：" + postage + "。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
            if (url_type == 1) {
                insert1();
            } else {
                compress();
            }
//            }
        }
    }

    @Override
    public void insert1() {
        Log.e("insert1: ", "insert1: 2");
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
                url = url.substring(0, url.length() - 1);
                EasyHttp.post(IpConfig.URL_SQL)
                        .params("query", "insert into " + table_product[type] + "(`url`,`url_type`,`contact`,`phone`,`phone_address`,`location`,`office_name`,`product_name`,`description`,`content`,`date`,`province`,`city`,`county`,`street`,`specific`)values('" + url + "','" + url_type + "','" + contact + "','" + phone + "','" + phone_address + "','" + location + "','" + et1_str + "','" + et2_str + "','" + description + "','" + content + "','" + date + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "')")
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
