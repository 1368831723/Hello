package com.pwj.activity;




import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;

import com.pwj.BaseActivity;
import com.pwj.helloya.R;


import butterknife.ButterKnife;
import butterknife.OnClick;


public class SurfaceVideoActivity extends BaseActivity {
//    private SurfaceVideoViewCreator surfaceVideoViewCreator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_surface_video);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

//        surfaceVideoViewCreator = new SurfaceVideoViewCreator(this,findViewById(R.id.activity_main)) {
//            @Override
//            protected Activity getActivity() {
//                return SurfaceVideoActivity.this;
//            }
//
//            @Override
//            protected boolean setAutoPlay() {
//                return false;
//            }
//
//            @Override
//            protected int getSurfaceWidth() {
//                return 0;
//            }
//
//            @Override
//            protected int getSurfaceHeight() {
//                return 250;
//            }
//
//            @Override
//            protected void setThumbImage(ImageView imageView) {
//                RequestOptions requestOptions = new RequestOptions()
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.all_darkbackground)
//                        .dontAnimate();
//                //或者
//                Glide.with(SurfaceVideoActivity.this).load(getIntent().getStringExtra("url")).apply(requestOptions).into(imageView);
//            }
//
//            @Override
//            protected String getSecondVideoCachePath() {
//                return null;
//            }
//
//            @Override
//            protected String getVideoPath() {
//                return getIntent().getStringExtra("url");
//            }
//        };
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        finish();
    }

}
