package com.pwj.activity;



import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.pwj.base.BaseActivityComment;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.OtherUserActivity;
import com.pwj.helloya.R;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.pages.RecruitViewOne;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SearchUtil;
import com.zhouyou.http.EasyHttp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PushRecruitActivity extends BaseActivityComment{
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.push_tv_no)
    TextView push_tv_no;
    @BindView(R.id.push_linear)
    LinearLayout push_linear;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv)
    TextView tv;
    private String user_img;
    private String user_name;
    private List<Comment> data_comment = new ArrayList<>();
    private List<Product> data = new ArrayList<>();
    private String phones;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private String table_name;
    private String table_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_push_recruit);
        ButterKnife.bind(this);
        initLocations();
        initData();
    }

    private void initData() {
        initProgress(this);
        phones = LoginInfo.getString(this, "phone", "");
//        user_img = getIntent().getStringExtra("user_img");
//        user_name = getIntent().getStringExtra("user_name");
        phone = getIntent().getStringExtra("phone");
        table_name = getIntent().getStringExtra("table_name");
        table_id = getIntent().getStringExtra("table_id");
//        phone = "15227397333";
//        title_tv.setText("15227397333");
//        table_name = "require_6_recruit";
//        table_id = "4";
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = " + table_id + "")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        data = GsonUtils.getGsonToList(json, Product.class);
                        if (data.size()==0){
                            push_tv_no.setVisibility(View.VISIBLE);
                            push_linear.setVisibility(View.GONE);
                            return;
                        }
                        Glide.with(PushRecruitActivity.this).load(data.get(0).getUrl3()).into(img);
                        name_tv.setText(data.get(0).getUser_name());
                        tv_time.setText(data.get(0).getDate());
                        tv.setText(data.get(0).getContent());
                        Pattern pattern = Pattern.compile("\\d{11,}");
                        Linkify.addLinks(tv, pattern, "tel:");
                    }
                });

    }


    @OnClick({R.id.title_im,R.id.img,  R.id.share, R.id.go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
            case R.id.img:
                intent = new Intent(PushRecruitActivity.this, OtherUserActivity.class);
                intent.putExtra("other_name", data.get(0).getUser_name());
                intent.putExtra("other_phone", data.get(0).getPhone());
                intent.putExtra("other_img", data.get(0).getUrl3());
                startActivity(intent);
                break;
            case R.id.share:
                content_pdf = data.get(0).getContent();
                String contact = data.get(0).getContact();
                String url3 = data.get(0).getUrl3();
                String title = data.get(0).getProduct_name();
                String other_phone = data.get(0).getPhone_address();
                clickShareNoPicture(url3, contact, other_phone, title);
                break;
            case R.id.go:
                selectMap(PushRecruitActivity.this,data,0,latitude,longitude);
                break;
        }
    }

    @NeedPermission(value = {Manifest.permission.ACCESS_FINE_LOCATION})
    public void initLocations() {
        SearchUtil.getInstance().initLocation(PushRecruitActivity.this, new StringCallbackOne() {
            @Override
            public void stringOne(String location, double longitude, double latitude) {
                PushRecruitActivity.this.longitude = longitude;
                PushRecruitActivity.this.latitude = latitude;
            }
        });
    }


//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SettingAboutActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SettingAboutActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
