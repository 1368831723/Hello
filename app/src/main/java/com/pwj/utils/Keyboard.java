package com.pwj.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.pwj.helloya.MyApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 13688 on 2019/5/11.
 */

public class Keyboard {

    static Keyboard instance;

    private Keyboard() {
        // construct
    }

    public static Keyboard getInstance() {
        if (instance == null) {
            instance = new Keyboard();
        }
        return instance;
    }


    /**
     * 隐藏软键盘
     *
     * @param editText
     */
    //  在AndroidManifest.xml的文件中声明对应的Activity时加上android:windowSoftInputMode属性，
    // android:windowSoftInputMode="stateAlwaysHidden|adjustResize"  否则无效
    public void hideKeyBoard(EditText editText) {
        InputMethodManager imm =
                (InputMethodManager)
                        MyApplication.getApplication()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 弹起软键盘
     *
     * @param editText
     */
    public void openKeyBoard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm =
                        (InputMethodManager)
                                MyApplication.getApplication()
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
                editText.setSelection(editText.getText().length());
            }
        }, 200);

    }
}
