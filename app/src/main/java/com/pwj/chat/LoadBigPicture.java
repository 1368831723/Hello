package com.pwj.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.pwj.helloya.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 13688 on 2019/8/3.
 */

public class LoadBigPicture extends AppCompatActivity {
    @BindView(R.id.title_relative)
    RelativeLayout title_relative;
    @BindView(R.id.img)
    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_picture);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .titleBar(title_relative, false)
                .transparentBar()
                .init();
        initData();
    }

    private void initData() {
        String path = getIntent().getStringExtra("path");
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, this.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, this.getResources().getDisplayMetrics());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.logo).error(R.drawable.logo);
        Glide.with(this) //上下文
                .load(path) //图片地址
                .apply(requestOptions)
                .into(img); //显示在哪个控件中
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
