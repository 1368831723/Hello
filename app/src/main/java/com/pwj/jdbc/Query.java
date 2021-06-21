package com.pwj.jdbc;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.pwj.bean.Bidding;
import com.pwj.bean.BiddingCurrent;
import com.pwj.bean.BiddingHistory;
import com.pwj.bean.BiddingSituation;
import com.pwj.bean.Product;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Query {
    private static List<Product> data;
    private static Map<String, String> map = new HashMap<String, String>();
    private static Product product;
    private static Bidding bidding;
    private static String id_card;
    private static String name;
    private static String pwd;
    private static Bitmap bitmap;

    //查询表login的数据，定义的query方法
    public static boolean query_register(Connection conn, String phone_str) {
        String sql = "SELECT * FROM login where phone=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, phone_str);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            } else {
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //查询表login的数据，定义的query方法
    public static boolean query_phone(Connection conn, String phone_str, String pwd_str) {
        String sql = "SELECT * FROM login where phone=? and pwd=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, phone_str);
            preparedStatement.setString(2, pwd_str);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            } else {

            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //查询表login的身份证号码，定义的query方法
    public static Map<String, String> query_id_card(Connection conn, String phone) {
        String Sql = "SELECT * FROM login where phone=" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                pwd = rs.getString("pwd");
                name = rs.getString("name");
                id_card = rs.getString("id_card");
                map.put("pwd", pwd);
                map.put("name", name);
                map.put("id_card", id_card);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //查询表bid_pwj的数据，定义的query方法
    public static List<BiddingCurrent> query_current(Connection conn) {
        List<BiddingCurrent> data = new ArrayList<>();
        String Sql = "SELECT * FROM bid_pwj order by times desc"; //时间字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String beginTime = rs.getString("times");
                String endTime = rs.getString("valid");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date toDay = new Date(System.currentTimeMillis());
                Date bt = dateFormat.parse(beginTime);
                if (!"".equals(endTime)) {
                    Date et = dateFormat.parse(endTime);
                    if (toDay.before(et)) {
                        BiddingCurrent biddingCurrent = new BiddingCurrent();
                        biddingCurrent.setTimes(rs.getString("times"));
                        biddingCurrent.setTitle(rs.getString("title"));
                        biddingCurrent.setUrl_pdf(rs.getString("url_pdf"));
                        biddingCurrent.setLink(rs.getString("link"));
                        biddingCurrent.setContent(rs.getString("content"));
                        biddingCurrent.setCompany(rs.getString("company"));
                        data.add(biddingCurrent);
                    }
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    //查询表bid_pwj的数据，定义的query方法
    public static List<BiddingSituation> query_situation(Connection conn) {
        List<BiddingSituation> data = new ArrayList<>();
        data.clear();
        String Sql = "SELECT company,longitude,latitude,COUNT(*) AS COUNT FROM bid_pwj GROUP BY company HAVING COUNT>0 ORDER BY COUNT DESC"; //时间字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                BiddingSituation biddingSituation = new BiddingSituation();
                biddingSituation.setCompany(rs.getString(1));
                biddingSituation.setLongitude(rs.getString(2));
                biddingSituation.setLatitude(rs.getString(3));
                biddingSituation.setCOUNT(rs.getString(4));
                data.add(biddingSituation);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表bid_pwj的数据，定义的query方法
    public static List<BiddingHistory> query_history(Connection conn) {
        List<BiddingHistory> data = new ArrayList<>();
        String Sql = "SELECT * FROM bid_pwj order by times desc"; //时间字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String beginTime = rs.getString("times");
                String endTime = rs.getString("valid");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date toDay = new Date(System.currentTimeMillis());
                Date bt = dateFormat.parse(beginTime);
                if (!"".equals(endTime)) {
                    Date et = dateFormat.parse(endTime);
                    if (et.before(toDay)) {
                        BiddingHistory biddingHistory = new BiddingHistory();
                        biddingHistory.setTimes(rs.getString("times"));
                        biddingHistory.setTitle(rs.getString("title"));
                        biddingHistory.setLink(rs.getString("link"));
                        biddingHistory.setContent(rs.getString("content"));
                        biddingHistory.setCompany(rs.getString("company"));
                        data.add(biddingHistory);
                    }
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //按照公司名字查询表bid_pwj的数据，定义的query方法
    public static List<Bidding> query_company(Connection conn, String company) {
        List<Bidding> data = new ArrayList<>();
        String Sql = "SELECT * FROM bid_pwj WHERE company LIKE '%" + company + "%' order by times desc"; //时间字段降序查询数据

        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                bidding = new Bidding(rs.getString("times"), rs.getString("valid"), rs.getString("title"),rs.getString("url_pdf"),  rs.getString("link"), rs.getString("content"), rs.getString("company"), rs.getString("contact"), rs.getString("phone"), rs.getString("email"), rs.getString("location"), rs.getString("require"), rs.getString("bank"), rs.getString("account"));
                data.add(bidding);
//                String beginTime = rs.getString("times");
//                String endTime = rs.getString("valid");
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date toDay = new Date(System.currentTimeMillis());
//                Date bt = dateFormat.parse(beginTime);
//                Date et = dateFormat.parse(endTime);
//                if (et.before(toDay)) {
//                    bidding = new Bidding(rs.getString("times"), rs.getString("valid"), rs.getString("title"), rs.getString("link"), rs.getString("content"), rs.getString("company"), rs.getString("contact"), rs.getString("phone"), rs.getString("email"), rs.getString("location"), rs.getString("require"), rs.getString("bank"), rs.getString("account"));
//                    data.add(bidding);
//                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query0(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_0_others order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_0_others order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("spec"), rs.getString("goods"), rs.getString("long"), rs.getString("wide"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 14);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表transporter1的数据，定义的query方法
    public static List<Product> query1(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product11_transporter order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product11_transporter order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束
            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 1);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_2_wool的数据，定义的query方法
    public static List<Product> query2(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_2_wool order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_2_wool order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束
            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("spec"), "", "", "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 2);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_3_resist的数据，定义的query方法
    public static List<Product> query3(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_3_resist order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_3_resist order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束
            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", "", rs.getString("wide"), rs.getString("thickness"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 3);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query4(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_4_sponge order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_4_sponge order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), rs.getString("thickness"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 4);
                data.add(product);

            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_5_tube的数据，定义的query方法
    public static List<Product> query5(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_5_tube order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_5_tube order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("diameter"), rs.getString("long"), "", "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 5);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_6_door的数据，定义的query方法
    public static List<Product> query6(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_6_door order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_6_door order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 6);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_7_elect_machine的数据，定义的query方法
    public static List<Product> query7(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_7_elect_machine order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_7_elect_machine order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 7);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_8_shield的数据，定义的query方法
    public static List<Product> query8(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_8_shield order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_8_shield order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("spec"), "", "", "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 8);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_9_plate的数据，定义的query方法
    public static List<Product> query9(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_9_plate order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_9_plate order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 9);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product10_lock_ring的数据，定义的query方法
    public static List<Product> query10(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product10_lock_ring order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product10_lock_ring order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 10);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query11(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product_1_second_hand1 order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product_1_second_hand1 order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), rs.getString("thickness"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 11);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query12(Connection conn) {
        List<Product> data = new ArrayList<>();
        String Sql = "SELECT * FROM product12_factory order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("area"), rs.getString("price"), rs.getString("location"));
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query13(Connection conn) {
        List<Product> data = new ArrayList<>();
        String Sql = "SELECT * FROM product13_logistics order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("start_time"), rs.getString("location_start"), rs.getString("location_end"));
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query14(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product14_casting order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product14_casting order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("spec"), rs.getString("goods"), rs.getString("long"), rs.getString("wide"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 14);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query15(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product15_pwj order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product15_pwj order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), rs.getString("thickness"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 15);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_4_sponge的数据，定义的query方法
    public static List<Product> query16(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product16_track order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product16_track order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, "", rs.getString("long"), rs.getString("wide"), rs.getString("thickness"), rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 16);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表product_8_shield的数据，定义的query方法
    public static List<Product> query17(Connection conn, int field) {
        List<Product> data = new ArrayList<>();
        String Sql = "";
        if (field == 0) {
            Sql = "SELECT * FROM product17_sand order by id desc"; //id的字段降序查询数据
        } else if (field == 1) {
            Sql = "SELECT * FROM product17_sand order by price+0 asc"; //price的字段升序查询数据
        }
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(url_list, rs.getString("spec"), "", "", "", rs.getString("price"), rs.getString("contact"), rs.getString("phone"), rs.getString("consign"), rs.getString("postage"), 17);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表purchase采购需求的数据，定义的query方法
    public static List<Product> query_others(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM others order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                if ((rs.getString("status")).equals("未完成")) {
                    String url = rs.getString("url");
                    List<String> url_list = Arrays.asList(url.split(","));
                    product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("remark"), rs.getString("status"), 0);
                    data.add(product);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表purchase采购需求的数据，定义的query方法
    public static List<Product> query_purchase(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM require_1_purchase order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                if ((rs.getString("status")).equals("未完成")) {
                    String url = rs.getString("url");
                    List<String> url_list = Arrays.asList(url.split(","));
                    product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("remark"), rs.getString("status"), 0);
                    data.add(product);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表purchase采购需求的数据，定义的query方法，只是个人手机号对应的订单
    public static List<Product> query_purchase_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM require_1_purchase where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("remark"), rs.getString("status"), 0);
                data.add(product);
//                System.out.println("foreach输出："+"项目:"+(rs.getString("p_name")+rs.getString("p_mess")+rs.getString("p_secret")+rs.getString("valid_time")+rs.getString("p_remarks")+rs.getString("issue_time")));
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表repair的数据，定义的query方法
    public static List<Product> query_repair(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM require_2_repair order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                if ((rs.getString("status")).equals("未完成")) {
                    String url = rs.getString("url");
                    List<String> url_list = Arrays.asList(url.split(","));
                    product = new Product(rs.getInt("id"), url_list, rs.getString("name"), rs.getString("phone"), rs.getString("location"), rs.getString("lcn_remark"), rs.getString("remarks"), rs.getString("status"), 1);
                    data.add(product);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表repair的数据，定义的query方法，只是个人手机号对应的订单
    public static List<Product> query_repair_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM require_2_repair where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(rs.getInt("id"), url_list, rs.getString("name"), rs.getString("phone"), rs.getString("location"), rs.getString("lcn_remark"), rs.getString("remarks"), rs.getString("status"), 1);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表transporter1的数据，定义的query方法
    public static List<Product> query_recruit(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM recruit order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                if ((rs.getString("status")).equals("未完成")) {
                    product = new Product(rs.getInt("id"), rs.getString("contact"), rs.getString("phone"), rs.getString("issue_date"), rs.getString("valid_date"), rs.getString("location"), rs.getString("identity"), rs.getString("remarks"), rs.getString("status"), rs.getDouble("longitude"), rs.getDouble("latitude"), 2);
                    data.add(product);
//                System.out.println("foreach输出："+"项目:"+(rs.getString("p_name")+rs.getString("p_mess")+rs.getString("p_secret")+rs.getString("valid_time")+rs.getString("p_remarks")+rs.getString("issue_time")));
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表transporter1的数据，定义的query方法，只是个人手机号对应的订单
    public static List<Product> query_recruit_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM recruit where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                product = new Product(rs.getInt("id"), rs.getString("contact"), rs.getString("phone"), rs.getString("issue_date"), rs.getString("valid_date"), rs.getString("location"), rs.getString("identity"), rs.getString("remarks"), rs.getString("status"), 0.00, 0.00, 2);
                data.add(product);
//                System.out.println("foreach输出："+"项目:"+(rs.getString("p_name")+rs.getString("p_mess")+rs.getString("p_secret")+rs.getString("valid_time")+rs.getString("p_remarks")+rs.getString("issue_time")));
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表出租rent的数据，定义的query方法
    public static List<Product> query_rent(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM rent order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                if ((rs.getString("status")).equals("未完成")) {
                    product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("duration"), rs.getString("remark"), rs.getString("status"), 3);
                    data.add(product);
                }

            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表出租rent的数据，定义的query方法，只是个人手机号对应的订单
    public static List<Product> query_rent_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM rent where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("duration"), rs.getString("remark"), rs.getString("status"), 3);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表logistics物流需求的数据，定义的query方法
    public static List<Product> query_logistics(Connection conn) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM logistics order by id desc"; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束
            while (rs.next()) {//遍历结果集 ，向下一行
                if ((rs.getString("status")).equals("未完成")) {
                    String url = rs.getString("url");
                    List<String> url_list = Arrays.asList(url.split(","));
                    product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("destination"), rs.getString("remark"), rs.getString("status"), 4);
                    data.add(product);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //查询表logistics物流需求的数据，定义的query方法，只是个人手机号对应的订单
    public static List<Product> query_logistics_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM logistics where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("destination"), rs.getString("remark"), rs.getString("status"), 4);
                data.add(product);
//                System.out.println("foreach输出："+"项目:"+(rs.getString("p_name")+rs.getString("p_mess")+rs.getString("p_secret")+rs.getString("valid_time")+rs.getString("p_remarks")+rs.getString("issue_time")));
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    //查询表purchase采购需求的数据，定义的query方法
    public static List<Product> query_others_form(Connection conn, String phone) {
        data = new ArrayList<>();
        String Sql = "SELECT * FROM others where phone =" + phone + ""; //id的字段降序查询数据
        try {
            Statement statement = conn.createStatement(); //也可以使用PreparedStatement来做
            ResultSet rs = statement.executeQuery(Sql);//执行sql语句并返还结束

            while (rs.next()) {//遍历结果集 ，向下一行
                String url = rs.getString("url");
                List<String> url_list = Arrays.asList(url.split(","));
                product = new Product(rs.getInt("id"), url_list, rs.getString("contact"), rs.getString("phone"), rs.getString("location"), rs.getString("name"), rs.getString("remark"), rs.getString("status"), 5);
                data.add(product);
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}