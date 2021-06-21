package com.pwj.jdbc;



import com.pwj.eventbus.EventIssue;
import com.pwj.eventbus.EventRegister;
import com.pwj.interfaces.UpdateCallback;



import org.greenrobot.eventbus.EventBus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by han13688 on 2018/7/13.
 */

public class Insert {

    //静态插入第二张表数据
    public static void insert_uuid(Connection conn, String uuid, UpdateCallback updateCallback) {
        try {
            String sql = "insert into login(`uid`)" + "values('" + uuid + "')"; // 插入数据的sql语句
            Statement statement = conn.createStatement();// 创建用于执行静态sql语句的Statement对象
            int count = statement.executeUpdate(sql);   // 执行插入操作的sql语句，并返回插入数据的个数
            updateCallback.finishUpdateListener(count);
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //动态插入用户表数据
    public static void insert_keyWords(Connection conn, String uid,String brand, String model,String os_version,String dates,String page, String keyWords, String location) {
        try {
            String sql = "insert into keywords(`uid`,`brand`,`model`,`os_version`,`dates`,`page`,`keyWords`, `location`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, uid);
            ps.setString(2, brand);
            ps.setString(3, model);
            ps.setString(4, os_version);
            ps.setString(5, dates);
            ps.setString(6, page);
            ps.setString(7, keyWords);
            ps.setString(8, location);
            ps.executeUpdate(); // 执行插入操作的sql语句，并返回插入数据的个数
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //静态插入第二张表数据
    public static void insert_phone1(Connection conn, String user_name, String pwd, String phone, String type, String others_remarks) {
        try {
            String sql = "insert into login(`user_name`, `pwd`,`phone`,`type`,`remarks`)" +
                    "values('" + user_name + "','" + pwd + "','" + phone + "','" + type + "','" + others_remarks + "')"; // 插入数据的sql语句
            Statement statement = conn.createStatement();// 创建用于执行静态sql语句的Statement对象
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


    //动态插入用户表数据
    public static void insert_phone2(Connection conn, String url, String user_name, String pwd, String phone, String location, String type, String others_remarks, double longitude, double latitude) {
        try {
            String sql = "insert into login(`url1`,`user_name`,`pwd`, `phone`,`location`, `type`,`remarks`,`longitude`,`latitude`)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, user_name);
            ps.setString(3, pwd);
            ps.setString(4, phone);
            ps.setString(5, location);
            ps.setString(6, type);
            ps.setString(7, others_remarks);
            ps.setDouble(8, longitude);
            ps.setDouble(9, latitude);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
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
    //动态插入第四张表数据
    public static void insert_0(Connection conn, String url, String goods, String lon, String wide,String spec, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_0_others(`url`,`goods`, `long`,`wide`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, goods);
            ps.setString(3, lon);
            ps.setString(4, wide);
            ps.setString(5, spec);
            ps.setString(6, price);
            ps.setString(7, contact);
            ps.setString(8, phone);
            ps.setString(9, consign);
            ps.setString(10, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //动态插入第一张表数据
    public static void insert_1(Connection conn, String url, String lon, String wide, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into transporter1(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //动态态插入第二张表数据
    public static void insert_2(Connection conn, String url, String spec, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_2_wool(`url`,`spec`, `price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, spec);
            ps.setString(3, price);
            ps.setString(4, contact);
            ps.setString(5, phone);
            ps.setString(6, consign);
            ps.setString(7, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第三张表数据
    public static void insert_3(Connection conn, String url, String wide, String thickness, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_3_resist(`url`,`wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, wide);
            ps.setString(3, thickness);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第四张表数据
    public static void insert_4(Connection conn, String url, String lon, String wide, String thickness, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_4_sponge(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, thickness);
            ps.setString(5, price);
            ps.setString(6, contact);
            ps.setString(7, phone);
            ps.setString(8, consign);
            ps.setString(9, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第五张表数据
    public static void insert_5(Connection conn, String url, String diameter, String lon, String price, String contact, String phone, String consign, String postage) {
        try {

            String sql = "insert into product_5_tube(`url`,`diameter`, `long`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, diameter);
            ps.setString(3, lon);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第六张表数据

    public static void insert_6(Connection conn, String url, String lon, String wide, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_6_door(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第七张表数据  复用第一张表方法
    public static void insert_7(Connection conn, String url, String lon, String wide, String price, String contact, String phone, String consign, String postage) {
        try {                               //long代表品牌，wide代表型号
            String sql = "insert into product_7_elect_machine(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态态插入第八张表数据 复用第二张表方法
    public static void insert_8(Connection conn, String url, String spec, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_8_shield(`url`,`spec`, `price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, spec);
            ps.setString(3, price);
            ps.setString(4, contact);
            ps.setString(5, phone);
            ps.setString(6, consign);
            ps.setString(7, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第九张表数据 复用第一张表方法
    public static void insert_9(Connection conn, String url, String lon, String wide, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_9_plate(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }


            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第十张表数据 复用第一张表方法
    public static void insert_10(Connection conn, String url, String lon, String wide, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product10_lock_ring(`url`,`long`, `wide`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, price);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, consign);
            ps.setString(8, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第11张表数据
    public static void insert_11(Connection conn, String url, String lon, String wide, String thickness, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product_1_second_hand1(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, thickness);
            ps.setString(5, price);
            ps.setString(6, contact);
            ps.setString(7, phone);
            ps.setString(8, consign);
            ps.setString(9, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第11张表数据
    public static void insert_12(Connection conn, String url, String area, String price, String location, String contact, String phone) {
        try {
            String sql = "insert into product12_factory(`url`,`area`,`price`,`location`, `contact`,`phone`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, area);
            ps.setString(3, price);
            ps.setString(4, location);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }

            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第11张表数据
    public static void insert_13(Connection conn, String url, String start_time, String location_start, String location_end, String contact, String phone) {
        try {
            String sql = "insert into product13_logistics(`url`,`start_time`,`location_start`,`location_end`, `contact`,`phone`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, start_time);
            ps.setString(3, location_start);
            ps.setString(4, location_end);
            ps.setString(5, contact);
            ps.setString(6, phone);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第四张表数据
    public static void insert_14(Connection conn, String url, String goods, String lon, String wide,String spec, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product14_casting(`url`,`goods`, `long`,`wide`,`spec`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, goods);
            ps.setString(3, lon);
            ps.setString(4, wide);
            ps.setString(5, spec);
            ps.setString(6, price);
            ps.setString(7, contact);
            ps.setString(8, phone);
            ps.setString(9, consign);
            ps.setString(10, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第11张表数据
    public static void insert_15(Connection conn, String url, String lon, String wide, String thickness, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product15_pwj(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, thickness);
            ps.setString(5, price);
            ps.setString(6, contact);
            ps.setString(7, phone);
            ps.setString(8, consign);
            ps.setString(9, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //动态插入第11张表数据
    public static void insert_16(Connection conn, String url, String lon, String wide, String thickness, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product16_track(`url`,`long`, `wide`,`thickness`,`price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, lon);
            ps.setString(3, wide);
            ps.setString(4, thickness);
            ps.setString(5, price);
            ps.setString(6, contact);
            ps.setString(7, phone);
            ps.setString(8, consign);
            ps.setString(9, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态态插入第八张表数据 复用第二张表方法
    public static void insert_17(Connection conn, String url, String spec, String price, String contact, String phone, String consign, String postage) {
        try {
            String sql = "insert into product17_sand(`url`,`spec`, `price`, `contact`,`phone`, `consign`, `postage`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, spec);
            ps.setString(3, price);
            ps.setString(4, contact);
            ps.setString(5, phone);
            ps.setString(6, consign);
            ps.setString(7, postage);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //动态插入其他需求
    public static void insert_others(Connection conn, String url, String contact, String phone, String location, String name, String remark) {
        try {
            String sql = "insert into others(`url`,`contact`,`phone`,`location`,`name`, `remark`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, contact);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, name);
            ps.setString(6, remark);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //动态插入第12张表数据  求购需求
    public static void insert_purchase(Connection conn, String url, String contact, String phone, String location, String name, String remark) {
        try {
            String sql = "insert into purchase(`url`,`contact`,`phone`,`location`,`name`, `remark`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, contact);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, name);
            ps.setString(6, remark);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第维修表表数据
    public static void insert_repair(Connection conn, String url, String name, String phone, String location, String lcn_remark, String remarks) {
        try {
            String sql = "insert into repair(`url`,`name`, `phone`,`location`,`lcn_remark`, `remarks`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, lcn_remark);
            ps.setString(6, remarks);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //动态插入第15张表数据  求购需求
    public static void insert_rent(Connection conn, String url, String contact, String phone, String location, String duration, String remark) {
        try {
            String sql = "insert into rent(`url`,`contact`,`phone`,`location`,`duration`,`remark`,`status`)values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, contact);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, duration);
            ps.setString(6, remark);
            ps.setString(7, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }

            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入第12张表数据  求购需求
    public static void insert_logistics(Connection conn, String url, String contact, String phone, String location, String name, String destination, String remark) {
        try {
            String sql = "insert into logistics(`url`,`contact`,`phone`,`location`,`name`,`destination`, `remark`,`status`)values(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, url);
            ps.setString(2, contact);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, name);
            ps.setString(6, destination);
            ps.setString(7, remark);
            ps.setString(8, "未完成");
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //动态插入招聘表数据
    public static void insert_recruit(Connection conn, String contact, String phone, String issue_date, String valid_date, String location, String identity, String remarks, double longitude, double latitude) {
        try {
            String sql = "insert into recruit(`contact`,`phone`,`issue_date`,`valid_date`, `location`,`identity`,`remarks`,`status`,`longitude`,`latitude`)values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);// 创建用于执行动态sql语句的PreparedStatement对象
            ps.setString(1, contact);
            ps.setString(2, phone);
            ps.setString(3, issue_date);
            ps.setString(4, valid_date);
            ps.setString(5, location);
            ps.setString(6, identity);
            ps.setString(7, remarks);
            ps.setString(8, "未完成");
            ps.setDouble(9, longitude);
            ps.setDouble(10, latitude);
            int count = ps.executeUpdate();          // 执行插入操作的sql语句，并返回插入数据的个数
            if (count >= 1) {
                EventBus.getDefault().post(new EventIssue(1));//发布成功
            } else {
                EventBus.getDefault().post(new EventIssue(2));//发布失败
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
