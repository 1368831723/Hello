package com.pwj.utils;

import android.os.Build;

/**
 * Created by 13688 on 2019/5/17.
 */

public class SystemUtil {
    //获取当前系统号
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    //获取手机型号
    public static String getSystemBrand() {
        return Build.BRAND;
    }
    //获取手机型号
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }
}
