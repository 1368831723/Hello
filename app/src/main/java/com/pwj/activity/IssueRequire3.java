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

public class IssueRequire3 extends BaseActivityIssueRequire {
    private String et1_str = "";
    private EditText et1;

    @Override
    public void initChild() {
        initLayout(R.layout.activity_require3);
        initView();
    }

    private void initView() {
        title_tv.setText("发布租厂需求");
        et1 = view.findViewById(R.id.et1);

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
        if (submit()) {
//            if (TextUtils.isEmpty(et1_str)) {
//                Util.showToast(this, "产品的参数不能为空");
//            } else {
//            content = "租赁时长：" + et1_str + "个月。\n位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。" + remarks;
            if (!TextUtils.isEmpty(et1_str)) {
                content = "租赁时长：" + et1_str + "个月。\n";
            }
            content = content + "位置：" + location + "。\n姓名：" + contact + "。\n电话：" + phone_address + "。\n备注：" + remarks;
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
                .params("query", "insert into " + ProductTableName.require3 + "(`url`,`url_type`,`contact`,`phone`,`phone_address`,`location`,`province`,`city`,`county`,`street`,`specific`,`duration`, `remarks`, `content`, `date`)values('" + url + "','" + url_type + "','" + contact + "','" + phone + "','" + phone_address + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + et1_str + "','" + remarks + "','" + content + "','" + date + "')")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        insertIssue(phone, ProductTableName.require3);
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
