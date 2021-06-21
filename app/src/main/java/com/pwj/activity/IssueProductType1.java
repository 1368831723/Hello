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

import com.pwj.utils.DecimalInputTextWatcher;
import com.pwj.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;

import java.io.File;

import butterknife.OnClick;
import okhttp3.MediaType;

/**
 * Created by 13688 on 2019/3/7.
 */

public class IssueProductType1 extends BaseActivityIssue {
    private String et1_str = "";
    private String et2_str = "";
    private String et3_str = "";
    private String et4_str = "";
    private String et5_str = "";
    private String et6_str = "";
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private EditText et5;
    private EditText et6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_product_1_pwj_second);
        initView();
    }

    private void initView() {
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        if (type == 0) {
            et1.setText("二手抛丸机");
        }
        et2.addTextChangedListener(new DecimalInputTextWatcher(et2, 1, 1));
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
        et4_str = et4.getText().toString().trim();
        et5_str = et5.getText().toString().trim();
        et6_str = et6.getText().toString().trim();
        if (submit()) {
            if (et2_str.length() == 2) {
                et2_str = et2_str.substring(0, 1);
            }
            if (!TextUtils.isEmpty(et1_str)) {
                content = "名称：" + et1_str + "。\n";
            }
            if (!TextUtils.isEmpty(et2_str)) {
                content = content + "新旧：" + et2_str + "成新" + "。\n";
            }
            if (!TextUtils.isEmpty(et3_str)) {
                content = content + "品牌：" + et3_str + "。\n";
            }
            if (!TextUtils.isEmpty(et4_str)) {
                content = content + "型号：" + et4_str + "。\n";
            }
            if (!TextUtils.isEmpty(et5_str)) {
                content = content + "规格：" + et5_str + "。\n";
            }
            if (!TextUtils.isEmpty(et6_str)) {
                content = content + "单价：" + et6_str + "。\n";
            }
            content = content + "运费：" + postage + "。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
//            et2_str = "新旧：" + et2_str + "成新";
//            et3_str = "品牌：" + et3_str;
//            et4_str = "型号：" + et4_str;
//            et5_str = "规格：" + et5_str;
//            et6_str = "单价：" + et6_str;
//            content = "名称：" + et1_str + "。\n" + et2_str + "。\n" + et3_str + "。\n" + et4_str + "。\n" + et5_str + "。\n" + et6_str + "。\n运送：" + consign + "。\n运费：" + postage + "。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
            Log.e("长度", "" + mUrls.length + "--" + mUrls[0] + "--" + selectList.get(0).getPath());
            if (url_type == 1){
                insert1();
            }else{
                compress();
            }
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
                            Log.e("上传成功", "onSuccess: " + s);
                        }
                    });

            if (i == mUrls.length - 1) {
                url = url.substring(0, url.length() - 1);
                EasyHttp.post(IpConfig.URL_SQL)
                        .params("query", "insert into " + table_product[type] + "(`url`,`url_type`,`contact`,`phone`,`phone_address`,`consign`,`postage`,`location`,`province`,`city`,`county`,`street`,`specific`,`product_name`,`new_grade`,`brand`,`type`,`spec`,`price`,`description`,`content`,`date`)values('" + url + "','" + url_type + "','" + contact + "','" + phone + "','" + phone_address + "','" + consign + "','" + postage + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + et5_str + "','" + et6_str + "','" + description + "','" + content + "','" + date + "')")
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
//        if (submit()) {
//            if (TextUtils.isEmpty(et1_str) || TextUtils.isEmpty(et2_str) || TextUtils.isEmpty(et3_str) || TextUtils.isEmpty(et4_str) || TextUtils.isEmpty(et5_str) || TextUtils.isEmpty(et6_str)) {
//                Util.showToast(this, "产品的参数不能为空");
//            } else {
//                if (et2_str.length() == 2) {
//                    et2_str = et2_str.substring(0, 1);
//                }
//                et2_str = "新旧：" + et2_str + "成新";
//                et3_str = "品牌：" + et3_str;
//                et4_str = "型号：" + et4_str;
//                et5_str = "规格：" + et5_str;
//                et6_str = "单价：" + et6_str;
//                content = "名称：" + et1_str + "。\n" + et2_str + "。\n" + et3_str + "。\n" + et4_str + "。\n" + et5_str + "。\n" + et6_str + "。\n运送：" + consign + "。\n运费：" + postage + "。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n描述：" + description;
//                for (int i = 0; i < mUrls.length; i++) {
//                    File file = new File(BitmapUtil.compressImage(mUrls[i]));
//                    String filename = file.getName();
//                    String postfix = filename.substring(filename.lastIndexOf("."));
//                    String names = String.valueOf(time + i) + postfix;
//                    url = url + IpConfig.BASE_URL + names + ",";
//                    EasyHttp.post(IpConfig.URL_IMG + names)
//                            .params("uploadfile", file, names, null)
//                            .timeStamp(true)
//                            .execute(new ProgressDialogCallBack<String>(mProgressDialog, true, true) {
//                                @Override
//                                public void onSuccess(String s) {
//                                }
//                            });
//
//                    if (i == mUrls.length - 1) {
//                        EasyHttp.post(IpConfig.URL_SQL)
//                                .params("query", "insert into " + table_product[type] + "(`url`,`contact`,`phone`,`phone_address`,`consign`,`postage`,`location`,`province`,`city`,`county`,`street`,`specific`,`product_name`,`new_grade`,`brand`,`type`,`spec`,`price`,`description`,`content`,`date`)values('" + url + "','" + contact + "','" + phone + "','" + phone_address + "','" + consign + "','" + postage + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + et1_str + "','" + et2_str + "','" + et3_str + "','" + et4_str + "','" + et5_str + "','" + et6_str + "','" + description + "','" + content + "','" + date + "')")
//                                .timeStamp(true)
//                                .execute(new SuccessCallBack<String>() {
//                                    @Override
//                                    public void onSuccess(String s) {
//                                        insertIssue(phone, table_product[type]);
//                                    }
//                                });
//                        break;
//                    }
//                }
//
//            }
//        }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

}
