package com.pwj.jdbc;
import android.util.Log;

import com.pwj.eventbus.Event_frg_ads;

import org.greenrobot.eventbus.EventBus;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by delphi0127 on 2018/7/13.
 */
public class Jdbc {
    //数据库连接
    public static Connection getConnection(String user, String pass) {
        Connection conn = null;//声明连接对象
        String driver = "com.mysql.jdbc.Driver";// 驱动程序类名
        String url = "jdbc:mysql://119.18.207.203:6606/parts?" + "useUnicode=true&characterEncoding = UTF8"; // 数据库URL
//        String url = "jdbc:mysql://103.40.28.9:6606/parts?" + "charset=gb2312";
        try {
            Class.forName(driver);// 注册(加载)驱动程序

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, user, pass);// 获取数据库连接

        } catch (Exception e) {
            //如果连接失败可以通知，是否没网

            EventBus.getDefault().post(new Event_frg_ads(3));
            e.printStackTrace();
        }
        return conn;
    }
    //释放数据库连接
    public static void releaseConnection(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
