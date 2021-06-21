package com.pwj.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;


/**
 * Created by 13688 on 2019/2/25.
 */

public class BaseIssueActivity extends Activity {
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPosition();
    }

    public int initPosition() {
        position = getIntent().getIntExtra("position", 1);
        return position;
    }

    public void startActivity(Class<?> activity, int position) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(DisplaySeller.class,position);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置弹窗宽度
    public void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

}
