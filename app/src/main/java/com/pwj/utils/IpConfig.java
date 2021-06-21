package com.pwj.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by 13688 on 2019/4/8.
 */

public class IpConfig {
    public final static String URL_IMG_LOGO = "http://paowan.com.cn/upload/logo.png";
    public final static String DOWNLOAD_EXPLAIN = "安卓手机请用wps 打开文件后点击此处下载\"抛丸机助手app\"" + "  https://paowan.com.cn/apk/paowanji.apk"+"或者搜索微信小程序\"抛丸清理机\"";
    public final static String OPEN_BIDDING = "跳转至原网站";
    //  File.separator就是斜线的意思
    public final static String PATH_DATA = Environment.getExternalStorageDirectory() + File.separator + "pwj" + File.separator;
    public final static String PATH_PDF = "pdf" + File.separator;
    public final static String URL_CHAT = "@119.18.207.203";
    public final static String URL_SQL = "http://paowan.com.cn/server_mariadb_ios_use/service_mariadb.php?mysql_pass=1q23lyc45j";
    public final static String URL_IMG = "http://paowan.com.cn/server_mariadb_ios_use/upload.php?image_pass=1q23lyc45j&filename=";
    public final static String ip = "http://119.18.207.203:80/server_mariadb_ios_use/upload.php";
    public final static String IP = "119.18.207.203";
    public final static String BASE_URL = "http://paowan.com.cn/upload/";
    public final static String BASE = "http://paowan.com.cn/";
//    public static String URL_SQL = "http://103.40.28.9:8000/server_mariadb_ios_use/service_mariadb.php?mysql_pass=1q23lyc45j";
//    public static String URL_IMG = "http://103.40.28.9:8000/server_mariadb_ios_use/upload.php?image_pass=1q23lyc45j&filename=";
//    public static String ip = "http://103.40.28.9:8000/server_mariadb_ios_use/upload.php";
//    public static String BASE_URL = "http://103.40.28.9:8000/upload/";

}
