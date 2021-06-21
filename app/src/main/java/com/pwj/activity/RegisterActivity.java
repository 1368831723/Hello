package com.pwj.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.helloya.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by han on 2018/8/17.
 */

public class RegisterActivity extends BaseActivity{
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.next)
    Button next;
    @BindView(R.id.register_rgp)
    RadioGroup register_rgp;
    @BindView(R.id.register_btn1)
    RadioButton register_btn1;
    @BindView(R.id.register_btn2)
    RadioButton register_btn2;
    @BindView(R.id.register_btn3)
    RadioButton register_btn3;
    @BindView(R.id.register_btn4)
    RadioButton register_btn4;
    private int type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_register);
        ButterKnife.bind(this);
        title_tv.setText("注册类型");
        register_btn1.setChecked(true);
        type = 1;
        register_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.register_btn1:
                        type = 1;

                        break;
                    case R.id.register_btn2:
                        type = 2;

                        break;
                    case R.id.register_btn3:

                        type = 3;

                        break;
                    case R.id.register_btn4:
                        type = 4;

                        break;
                    case R.id.register_btn5:
                        type = 5;

                        break;
                    case R.id.register_btn6:
                        type = 6;

                        break;
                }
            }
        });
    }

    @OnClick({R.id.title_im, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                RegisterActivity.this.finish();
                break;
            case R.id.next:
                switch (type) {
                    case 1:
                        startActivity(RegisterType1.class);
                        RegisterActivity.this.finish();
                        break;
                    case 2:
                        startActivity(RegisterType2.class);
                        RegisterActivity.this.finish();
                        break;
                    case 3:
                        startActivity(RegisterType2.class);
                        RegisterActivity.this.finish();
                        break;
                    case 4:
                        startActivity(RegisterType1.class);
                        RegisterActivity.this.finish();
                        break;
                    case 5:
                        startActivity(RegisterType1.class);
                        RegisterActivity.this.finish();
                        break;
                    case 6:
                        startActivity(RegisterType1.class);
                        RegisterActivity.this.finish();
                        break;
                }
                break;
        }

    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent();
        intent.putExtra("typ",type);
        intent.setClass(RegisterActivity.this, activity);
        startActivity(intent);
    }
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("RegisterActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("RegisterActivity");
//        MobclickAgent.onPause(this); //统计时长
//    }
}
