package com.pwj.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.pwj.base.BaseActivityIssue;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;
import com.zhouyou.http.EasyHttp;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by 13688 on 2019/3/7.
 */

public class IssueProductOthers extends BaseActivityIssue {
    private String et1_str = "";
    private String et2_str = "";
    private String et3_str = "";
    private String et4_str = "";
    private String et5_str = "";
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private EditText et5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_product_2_pwj);
        initView();
    }

    private void initView() {
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et2.setText(pwj_brand);
    }

    @OnClick({R.id.submit})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                insert();
                break;
        }
    }

    @Override
    public void insert1() {

    }

    private void insert() {
        et1_str = et1.getText().toString().trim();
        et2_str = et2.getText().toString().trim();
        et3_str = et3.getText().toString().trim();
        et4_str = et4.getText().toString().trim();
        et5_str = et5.getText().toString().trim();
        if (submit()) {
            if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str) || TextUtils.isEmpty(et5_str)) {
                Util.showToast(this, "产品的参数不能为空");
            } else {
                LoginInfo.setString(IssueProductOthers.this,"pwj_brand",et2_str);
                et1_str = "名称：" + et1_str;
                et2_str = "品牌：" + et2_str;
                et3_str = "型号：" + et3_str;
                et4_str = "规格：" + et4_str;
                et5_str = "单价：" + et5_str;
                content = et1_str + "。" + et2_str + "，" + et3_str + "，" + et4_str + "，" + et5_str + "，" + consign + "，" + postage + "，" + location + "，" + contact + "，" + phone_address + "，" + description;
                for (int i = 0; i < mUrls.length; i++) {
                    if (!TextUtils.isEmpty(mUrls[i])) {
                        File file = new File(BitmapUtil.compressImage(mUrls[i]));
                        String filename = file.getName();
                        String postfix = filename.substring(filename.lastIndexOf("."));
                        String names = String.valueOf(time + i) + postfix;
                        url = url + IpConfig.BASE_URL + names + ",";
                        EasyHttp.post(IpConfig.URL_IMG + names)
                                .params("uploadfile", file, names, null)
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                    }
                                });
                    } else {
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "insert into " + ProductTableName.product0 + "(`url`,`contact`,`phone`,`consign`,`postage`,`location`,`product_name`,`brand`,`type`,`spec`,`price`,`description`,`content`,`date`)values('" + url + "','" + contact + "','" + phone + "','" + consign + "','" + postage + "','" + location + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + et5_str + "','" + description + "','" + content + "','" + date + "')")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        insertIssue(phone, ProductTableName.product0);
                                    }
                                });
                        break;
                    }
                }
            }
        }
    }
}
