package com.pwj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 13688 on 2018/4/7.
 */

public class LoginInfo {

    //判断是否第一次登录
    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences("first_enter", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    public static  Boolean getBoolean(Context context, String key, Boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences("first_enter", Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    //保存String类型
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("value", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences("value", Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    //保存int类型
    public static void setInt(Context context, String key, int day) {
        SharedPreferences sp = context.getSharedPreferences("garbage", Context.MODE_PRIVATE);
        sp.edit().putInt(key, day).apply();
    }
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences("garbage", Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

}
