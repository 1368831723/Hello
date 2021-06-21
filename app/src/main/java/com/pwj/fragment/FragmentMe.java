package com.pwj.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.pwj.activity.AddressActivity;

import com.pwj.activity.BusinessActivity;
import com.pwj.activity.PushMessageActivity;
import com.pwj.activity.SettingProfile;
import com.pwj.activity.FormActivity;
import com.pwj.activity.LoginActivity;
import com.pwj.activity.PersonCollectActivity;
import com.pwj.activity.PersonConcernActivity;


import com.pwj.activity.SettingsActivity;
import com.pwj.chat.MessageActivity;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;

import com.pwj.helloya.R;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by leon on 3/7/18.
 */

public class FragmentMe extends BaseFragment {
    private Unbinder unbinder;
    @BindView(R.id.count_tv)
    TextView count_tv;
    @BindView(R.id.fragment_me_img)
    CircleImageView fragment_me_img;
    @BindView(R.id.fragment_me_name)
    TextView fragment_me_name;
    @BindView(R.id.login_linear)
    LinearLayout login_linear;
    @BindView(R.id.login_linear2)
    LinearLayout login_linear2;
    @BindView(R.id.login)
    Button login;
    private Activity mActivity;
    public static final String ARG_PAGE = "ARG_PAGE_SETTING";
    private int mPage;
    private String phone = "";
    private List<User> data;
    private String user_img;
    private String user_name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

//    @OnClick(R.id.fragment_ads_issue)
//    public void onViewClicked() {
//        startActivity(CityListActivity.class);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        phone = LoginInfo.getString(mActivity, "phone", "");//获取电话号码不为空登录
//        String img_url = LoginInfo.getString(mActivity, "qq_image", "");
//        String qq_name = LoginInfo.getString(mActivity, "qq_name", "");
        if (phone.equals("")) {
            login_linear.setVisibility(View.VISIBLE);
            login_linear2.setVisibility(View.GONE);
        } else {
            data = new ArrayList<>();
            initImg();
            login_linear.setVisibility(View.GONE);
            login_linear2.setVisibility(View.VISIBLE);
        }
        //个人中心加载图片
//        if (img_url!=null){
//            Glide.with(mActivity).load(img_url).into(fragment_me_img);
//            fragment_me_name.setText(LoginInfo.getString(mActivity,"qq_name",""));
//        }
    }

    private void initImg() {
        user_name = LoginInfo.getString(mActivity, "user_name", "");
        user_img = LoginInfo.getString(mActivity, "user_img", "");
        if ("".equals(user_name) || "".equals(user_img)) {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT url3,user_name FROM login where phone = '" + phone + "'")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            data = GsonUtils.getGsonToList(s, User.class);
                            user_name = data.get(0).getUser_name();
                            user_img = data.get(0).getUrl3();
                            Glide.with(mActivity).load(user_img).into(fragment_me_img);
                            fragment_me_name.setText(user_name);
                            LoginInfo.setString(mActivity, "user_name", user_name);
                            LoginInfo.setString(mActivity, "user_img", user_img);
                        }
                    });
        } else {
            Glide.with(mActivity).load(user_img).into(fragment_me_img);
            fragment_me_name.setText(user_name);
        }
    }


    @OnClick({R.id.login, R.id.fragment_me_img, R.id.fragment_me_name, R.id.img_settings, R.id.me_data, R.id.me_push,  R.id.me_message, R.id.me_concern, R.id.me_collect, R.id.me_form, R.id.me_address, R.id.me_business, R.id.me_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                startActivity(LoginActivity.class, 2);
                break;
            case R.id.fragment_me_img:
                startActivity(SettingProfile.class);
                break;
            case R.id.fragment_me_name:
                startActivity(SettingProfile.class);
                break;
            case R.id.img_settings:
                startActivity(SettingsActivity.class);
                break;
            case R.id.me_data:
//                startActivity(PersonalData.class);
                startActivity(SettingProfile.class);
                break;
            case R.id.me_form:
                startActivity(FormActivity.class);
//                startActivity(BaseActivityQueryForm.class);
                break;
            case R.id.me_push:
                startActivity(PushMessageActivity.class);
                break;
            case R.id.me_message:
                startActivity(MessageActivity.class);
                break;
            case R.id.me_concern:
                startActivity(PersonConcernActivity.class);
                break;
            case R.id.me_collect:
                startActivity(PersonCollectActivity.class);
                break;
            case R.id.me_address:
                Intent intent = new Intent(mActivity, AddressActivity.class);
                intent.putExtra("flag", false);
                startActivity(intent);
                break;
            case R.id.me_business:
//                startActivity(SelectLoveActivity.class);
                startActivity(BusinessActivity.class);
                break;
            case R.id.me_setting:
                startActivity(SettingsActivity.class);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryChat();
        int counts = LoginInfo.getInt(mActivity, "counts", 0);
        if (counts<=0) {
            count_tv.setVisibility(View.GONE);
        } else {
            count_tv.setVisibility(View.VISIBLE);
            count_tv.setText(String.valueOf(counts));
        }
        initData();
        MobclickAgent.onPageStart("FragmentMe");
        MobclickAgent.onResume(mActivity); //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentMe");
        MobclickAgent.onPause(mActivity); //统计时长
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
