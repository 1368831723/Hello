package com.pwj.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pwj.BaseActivity;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.MainActivity;
import com.pwj.helloya.R;
import com.pwj.umeng.PushFactory;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.LoginInfo;

import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingsActivity extends BaseActivity {
    private List<PushFactory> data_factory = new ArrayList<>();

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.relative_push)
    RelativeLayout relative_push;

    private String phone;

    private String phone_all = "";
    private List<User> data_user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
//        CrashReport.testJavaCrash();
        title_tv.setText("设置");
        String user_img = LoginInfo.getString(this, "user_img", "");
        phone = LoginInfo.getString(this, "phone", "");

        //  删除
//        String aaa = "{\"display_type\":\"notification\",\"extra\":{\"type_class\":\"1\",\"from\":\"17600482472\",\"table_id\":\"311\",\"table_name\":\"require_1_purchase\"},\"body\":{\"after_open\":\"go_app\",\"ticker\":\"Android customizedcast ticker\",\"title\":\"新需求提醒\",\"play_sound\":\"true\",\"play_lights\":\"true\",\"play_vibrate\":\"true\",\"text\":\"雷行机械抛丸17879723668发布了求购需求\"},\"msg_id\":\"uant8f5162385183054801\"}";
//        String bbb = "{\"display_type\":\"notification\",\"extra\":{\"type_class\":\"3\"},\"body\":{\"after_open\":\"go_app\",\"ticker\":\"抛丸机安装需求\",\"title\":\"抛丸机安装需求\",\"play_sound\":\"true\",\"text\":\"群里哪个师傅有时间的、明天3月4号需要去太原安装通过式抛丸机一台、有时间的师傅、请与我联系13815561090，谢谢大家支持。\"},\"msg_id\":\"ua87ns6162385543992811\"}";
//        data_factory = GsonUtils.getGsonToList(aaa, PushFactory.class);
//        PushFactory pushFactory = JSONObject.parseObject(aaa, new TypeReference<PushFactory>() {
//        });
//        PushFactory pushFactory1 = JSONObject.parseObject(bbb, new TypeReference<PushFactory>() {
//        });
//        String phone_from = pushFactory.getExtra().getFrom();
//        String table_id = pushFactory.getExtra().getTable_id();
//        String table_name = pushFactory.getExtra().getTable_name();
//        String title = pushFactory1.getBody().getTitle();
//        String content = pushFactory1.getBody().getText();
//        Log.e("aaa", "" + phone_from + "--" + table_id + "--" + table_name);
//        Log.e("bbb", "" + title + "--" + content);
    }


    @OnClick({R.id.title_im, R.id.relative_message, R.id.relative_save, R.id.relative_about, R.id.relative_update, R.id.relative_policy, R.id.relative_suggestion, R.id.relative_push, R.id.relative_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.relative_about:
                startActivity(SettingAboutActivity.class);
                break;
            case R.id.relative_message:
                startActivity(SettingMessageActivity.class);
                break;
            case R.id.relative_save:
                startActivity(SettingSaveActivity.class);
                break;
            case R.id.relative_update:
                startActivity(SettingUpdateActivity.class);
                break;
            case R.id.relative_policy:
                startActivity(SettingPolicyActivity.class);
                break;
            case R.id.relative_suggestion:
                startActivity(SettingSuggestionActivity.class);
                break;
            case R.id.relative_push:
                showCoverDialog();
//                startActivity(SettingSuggestionActivity.class);
                break;
            case R.id.relative_exit:
                startActivity(LoginActivity.class);
                MobclickAgent.onProfileSignOff();
                LoginInfo.setString(SettingsActivity.this, "phone", "");
                finish();
                break;
        }
    }

    private void showCoverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确认提交么？--- 8 条");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("TAG", "点击了确定--");
                pushAll();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void pushAll() {
        EasyHttp.post("http://paowan.com.cn/server_mariadb_ios_use/service_mariadb.php?mysql_pass=1q23lyc45j")
                .params("query", "SELECT phone FROM login ORDER BY phone DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data_user = GsonUtils.getGsonToList(s, User.class);
                        for (int i = 0; i < data_user.size(); i++) {
                            if (data_user.get(i).getPhone() != null) {
                                phone_all = phone_all + data_user.get(i).getPhone() + ",";
                            } else {
                                phone_all = phone_all.substring(0, phone_all.length() - 1);
                                break;
                            }
                        }
                        Log.e("TAG", "" + phone_all);
                        EasyHttp.post("http://paowan.com.cn/server_mariadb_ios_use/umpush.php")
                                .params("alias", phone_all)
                                .params("title", "新增招标")
                                .params("text", "又更新了8条招标信息！")
                                .params("from", phone)
                                .params("table_name", "0")
                                .params("table_id", "0")
                                .params("type_class", "4")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Util.showToast(SettingsActivity.this, "推送成功");
                                    }
                                });
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
