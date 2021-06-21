package com.pwj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pwj.umeng.PushFactory;
import com.pwj.helloya.MainActivity;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

import java.util.HashMap;
import java.util.Map;

public class FactoryMessageActivity extends UmengNotifyClickActivity {
    private static final String TAG = "FactoryMessageActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.activity_push_factory);
//        if (type_class.equals("1")) {
//            layoutId = R.layout.activity_person_push_product;
//        }
//        if (type_class.equals("2")) {
//            layoutId = R.layout.activity_person_push_recruit;
//        }
//        if (type_class.equals("3")) {
//            layoutId = R.layout.activity_push_background;
//        }
//        setContentView(layoutId);
        initData();

    }

    private void initData() {
        Log.e(TAG, "aaa--");
    }

    @Override
    public void onMessage(Intent intent1) {
        super.onMessage(intent1);
        Bundle bundle = intent1.getExtras();
        if (bundle != null) {
        }
        String body_message = intent1.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.e(TAG, "bbb--" + body_message);
        Map<String, Class> map = new HashMap<>();
        map.put("1", PushProductActivity.class);    // 1 产品详情跳转
        map.put("2", PushRecruitActivity.class);    // 2 招聘详情跳转
        map.put("3", PushBackgroundActivity.class); // 3 后台帮别个推送
        map.put("4", MainActivity.class);           // 4 跳转到主页招标页面
        PushFactory pushFactory = JSONObject.parseObject(body_message, new TypeReference<PushFactory>() {
        });
        String type = pushFactory.getExtra().getType_class();
        if (type == null) {
            return;
        }
        Intent intent = new Intent(FactoryMessageActivity.this, map.get(type));
        if (type.equals("1") || type.equals("2")) {
            String phone_from = pushFactory.getExtra().getFrom();
            String table_id = pushFactory.getExtra().getTable_id();
            String table_name = pushFactory.getExtra().getTable_name();
            intent.putExtra("phone", phone_from);
            intent.putExtra("table_name", table_name);
            intent.putExtra("table_id", table_id);
        } else if (type.equals("3")) {
            String title = pushFactory.getBody().getTitle();
            String content = pushFactory.getBody().getText();
            intent.putExtra("title", title);
            intent.putExtra("content", content);
        } else {
            intent.putExtra("page", 2);
        }
        startActivity(intent);
        FactoryMessageActivity.this.finish();
//        if (!TextUtils.isEmpty(body)) {
//            runOnUiThread(() -> ((TextView) findViewById(R.id.tv)).setText(body));
//        }
    }
}
