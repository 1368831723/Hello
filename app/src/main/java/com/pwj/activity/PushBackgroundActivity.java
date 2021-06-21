package com.pwj.activity;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pwj.base.BaseActivityComment;
import com.pwj.helloya.R;
import com.pwj.utils.LoginInfo;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PushBackgroundActivity extends BaseActivityComment{
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.push_title)
    TextView push_title;
    @BindView(R.id.push_content)
    TextView push_content;

//    @BindView(R.id.img)
//    ImageView img;
//    @BindView(R.id.name_tv)
//    TextView name_tv;
//    @BindView(R.id.tv_time)
//    TextView tv_time;
//    @BindView(R.id.tv)
//    TextView tv;
    private String phones;
    private String title;
    private String content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_background);
        ButterKnife.bind(this);
//        initLocations();
        initData();
    }

    private void initData() {
//        phones = LoginInfo.getString(this, "phone", "");
//        phone = getIntent().getStringExtra("phone");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        push_title.setText(title);
        push_content.setText(content);
        Pattern pattern = Pattern.compile("\\d{11,}");
        Linkify.addLinks(push_content, pattern, "tel:");
//        phone = "15227397333";
//        title_tv.setText("15227397333");
//        table_name = "require_6_recruit";
//        table_id = "4";
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
//        EasyHttp.post(IpConfig.URL_SQL)
//                .params("query", "SELECT login.url3,login.user_name," + table_name + ".* FROM login," + table_name + " WHERE " + table_name + ".phone=login.phone AND " + table_name + ".id = " + table_id + "")
//                .timeStamp(true)
//                .execute(new SuccessCallBack<String>() {
//                    @Override
//                    public void onSuccess(String json) {
//                        data = GsonUtils.getGsonToList(json, Product.class);
//                        Glide.with(PushBackgroundActivity.this).load(data.get(0).getUrl3()).into(img);
//                        name_tv.setText(data.get(0).getUser_name());
//                        tv_time.setText(data.get(0).getDate());
//                        tv.setText(data.get(0).getContent());
//                        Pattern pattern = Pattern.compile("\\d{11,}");
//                        Linkify.addLinks(tv, pattern, "tel:");
//                    }
//                });

    }


    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;

        }
    }

}
