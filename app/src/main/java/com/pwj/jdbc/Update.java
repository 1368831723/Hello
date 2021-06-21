package com.pwj.jdbc;

import android.util.Log;

import com.pwj.eventbus.EventRegister;
import com.pwj.interfaces.UpdateCallback;

import org.greenrobot.eventbus.EventBus;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by han on 2018/8/25.
 */

public class Update {
    private static UpdateCallback updateCallback;
    private static String sql;

    //更新数据
    public static void update(Connection conn, String phone, String url, String name, String id_card) {
        String sql = "update login set url2 = ?,name = ? ,id_card = ? where phone = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, name);
            ps.setString(3, id_card);
            ps.setString(4, phone);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventRegister(1));//RegisterSuccess页面  成功
            } else {
                EventBus.getDefault().post(new EventRegister(2));//RegisterSuccess页面  成功
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //静态插入第二张表数据
    public static void update_type1(Connection conn, String user_name, String pwd, String phone, String type, String others_remarks, String uid) {
        try {
//            String sql ="update login set `user_name`=?,`pwd`=? ,`phone`=?where `uid`=?";
            String sql ="update login set `user_name`='"+user_name+"',`pwd`='"+pwd+"' ,`phone`='"+phone+"',`type`='"+type+"',`remarks`='"+others_remarks+"' where `uid`='"+uid+"'";
//            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行静态sql语句的Statement对象
//            ps.setString(1, user_name);
//            ps.setString(2, pwd);
//            ps.setString(3, phone);
//            ps.setString(4, "aa");
//            ps.setString(6, uid);
//            int count = ps.executeUpdate(sql);
            Statement statement =conn.createStatement();
            int count = statement.executeUpdate(sql);   // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {

                EventBus.getDefault().post(new EventRegister(1));//发布成功
            } else {

                EventBus.getDefault().post(new EventRegister(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //静态插入第二张表数据
    public static void update_type2(Connection conn,String url, String user_name, String pwd, String phone,String location, String type, String others_remarks, double longitude, double latitude, String uid) {
        try {
            String sql ="update login set `url1`='"+url+"',`user_name`='"+user_name+"',`pwd`='"+pwd+"' ,`phone`='"+phone+"',`location`='"+location+"',`type`='"+type+"',`remarks`='"+others_remarks+"',`longitude`='"+longitude+"',`latitude`='"+latitude+"' where `uid`='"+uid+"'";
            Statement statement =conn.createStatement();
            int count = statement.executeUpdate(sql);   // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {

                EventBus.getDefault().post(new EventRegister(1));//发布成功
            } else {

                EventBus.getDefault().post(new EventRegister(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void update_keyWords(Connection conn, String keyWords, String uuid) {
        String sql = "update login set keyWords = CONCAT(keyWords,'','," + keyWords + "') where uid = '" + uuid + "'";

        try {
            Statement statement =conn.createStatement();// 创建用于执行动态sql语句的PreparedStatement对象
            int count = statement.executeUpdate(sql);          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {

            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_purchase(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update purchase set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {

                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {

//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_repair(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update repair set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {
                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {
//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_recruit(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update recruit set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {
                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {
//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_rent(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update rent set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {
                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {
//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_logistics(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update logistics set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {
                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {
//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更新数据
    public static void update_others(Connection conn, int id, UpdateCallback Callback) {
        updateCallback = Callback;
        String sql = "update others set status = ? where id = " + id + "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, "已完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数

            if (count >= 1) {
                updateCallback.finishUpdateListener(1);//如果tag=1的话，就上传成功
            } else {
//                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
