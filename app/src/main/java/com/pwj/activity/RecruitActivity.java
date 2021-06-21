package com.pwj.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pwj.BaseActivity;
import com.pwj.bean.Comment;
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.MyRadioGroup;
import com.pwj.dialog.DialogCalendar;
import com.pwj.utils.FullStopUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.helloya.R;

import com.pwj.utils.ProductTableName;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;


import org.angmarch.views.NiceSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by delphi0127 on 2018/7/4.
 */

public class RecruitActivity extends BaseActivity {

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.issue_tv_date)
    TextView issue_tv_date;
    @BindView(R.id.valid_et_date)
    EditText valid_et_date;
    @BindView(R.id.edt_location)
    EditText edt_location;
    @BindView(R.id.type_rgp)
    RadioGroup type_rgp;
    @BindView(R.id.type_per)
    RadioButton type_per;
    @BindView(R.id.type_pro)
    RadioButton type_pro;
    @BindView(R.id.rgp)
    MyRadioGroup rgp;
    @BindView(R.id.tv_post)
    TextView tv_post;
    @BindView(R.id.tv_salary)
    TextView tv_salary;
    @BindView(R.id.et_salary)
    EditText et_salary;
    @BindView(R.id.linear_people)
    LinearLayout linear_people;
    @BindView(R.id.et_people)
    EditText et_people;
    @BindView(R.id.et_remarks)
    EditText et_remarks;
    @BindView(R.id.issue_submit)
    Button issue_submit;
    private int issue = 1;
    private String identity = "厂家";
    //发布产品的类型的的布局页面,以及用户填写的参数
    private DialogCalendar dialogCalendar;
    private String phone;
    private String table = "招聘需求";
    private String contact;  //注册时候的电话
    private String phone_address;  //发布产品时候的电话
    private String location = "";
    private String province;
    private String city;
    private String county;
    private String street = "";
    private String specific = "";
    private double longitude = 0;
    private double latitude = 0;
    private String post = "管理员";
    private String posts[];
    private String salary = "";
    private String people = "";
    private String issue_date;
    private String valid_date;
    private String remarks;
    private String content;
    private String date = "";
    private SimpleDateFormat sdf;
    private String year;
    private String month;
    private String day;
    private List<Comment> data_concern = new ArrayList<>();
    private String concern = "";
    private String user_name;
    private List<User> data_user;
    private String phone_all = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_recruit);    //activity_issue
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        posts = new String[]{getString(R.string.post1), getString(R.string.post2), getString(R.string.post3),
                getString(R.string.post4), getString(R.string.post5), getString(R.string.post6),
                getString(R.string.post7), getString(R.string.post8), getString(R.string.post9),
                getString(R.string.post10), getString(R.string.post11), getString(R.string.post12),
                getString(R.string.post13), getString(R.string.post14), getString(R.string.post15),
                getString(R.string.post16)};
        title_tv.setText("发布需求");
        type_per.setChecked(true);
        type_rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.type_per:
                        linear_people.setVisibility(View.VISIBLE);
                        et_remarks.setHint(R.string.demand_remark);
                        identity = "厂家";
                        table = "招聘需求";
                        tv_post.setText("招聘职位:");
                        tv_salary.setText(getString(R.string.salary1));
                        break;
                    case R.id.type_pro:
                        linear_people.setVisibility(View.GONE);
                        et_remarks.setHint(R.string.per_remark);
                        identity = "个人";
                        table = "应聘需求";
                        tv_post.setText("应聘职位:");
                        tv_salary.setText(getString(R.string.salary2));
                        break;
                }
            }
        });
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                int position = group.indexOfChild(findViewById(i));
                post = posts[position];
            }
        });

        valid_et_date.setEnabled(false);
        phone = LoginInfo.getString(this, "phone", "");
        user_name = LoginInfo.getString(this, "user_name", "");
        if (phone.equals("")) {
            startActivity(LoginActivity.class, 0);
            finish();
        } else {
            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT * FROM address WHERE phone = " + phone + " AND number = 1")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            List<Product> data = GsonUtils.getGsonToList(s, Product.class);
                            if (data.size() > 0) {
                                contact = data.get(0).getContact();
                                phone_address = data.get(0).getPhone_address();
                                location = data.get(0).getLocation();
                                province = data.get(0).getProvince();
                                city = data.get(0).getCity();
                                county = data.get(0).getCounty();
                                street = data.get(0).getStreet();
                                specific = data.get(0).getSpecific();
                                longitude = data.get(0).getLongitude();
                                latitude = data.get(0).getLatitude();
                                edt_location.setText(contact + " " + phone_address + "\n" + location);
                            }
                        }
                    });
            setIssueDate();
        }
    }


    private void setIssueDate() {
        SimpleDateFormat sdf_y = new SimpleDateFormat("yyyy");
        year = sdf_y.format(new Date());

        SimpleDateFormat sdf_m = new SimpleDateFormat("MM");
        month = sdf_m.format(new Date());

        SimpleDateFormat sdf_d = new SimpleDateFormat("dd");
        day = sdf_d.format(new Date());
        //System.out.println("日期为:"+year+"-"+month+"-"+day);
        issue_date = year + "-" + month + "-" + day;
        issue_tv_date.setText(issue_date);
    }

    //几种选择图片的点击事件判断
    @OnClick({R.id.title_im, R.id.img_location, R.id.calendar, R.id.issue_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                RecruitActivity.this.finish();
                break;
            case R.id.img_location:
                Intent intent = new Intent(RecruitActivity.this, AddressActivity.class);
                intent.putExtra("flag", true);
                startActivityForResult(intent, 10);
                break;
            case R.id.calendar:
                showDialog();
                break;
            case R.id.issue_submit:
                getData();
                break;
        }


    }

    private void showDialog() {
        if (dialogCalendar != null) {
            dialogCalendar.show();
        } else {
            dialogCalendar = new DialogCalendar(RecruitActivity.this, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), R.style.dialog_choose, new DialogCalendar.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(String text) {
                    valid_et_date.setText(text);
                }
            });
            dialogCalendar.show();
            full_dialog();
        }
    }


    //设置弹窗宽度
    private void full_dialog() {
        WindowManager windowManager = RecruitActivity.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogCalendar.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialogCalendar.getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (data != null) {
                    contact = data.getStringExtra("contact");
                    phone_address = data.getStringExtra("phone_address");
                    location = data.getStringExtra("location");
                    province = data.getStringExtra("province");
                    city = data.getStringExtra("city");
                    county = data.getStringExtra("county");
                    street = data.getStringExtra("street");
                    specific = data.getStringExtra("specific");
                    longitude = data.getDoubleExtra("longitude", 0.0);
                    latitude = data.getDoubleExtra("latitude", 0.0);
                    edt_location.setText(contact + " " + phone_address + "\n" + location);
                }
                break;
            case 15:
                setResult(15);
                finish();
                break;
            default:

                break;
        }
    }


    //提交之前检查姓名 手机号不为空，产品类型
    private void getData() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sdf.format(System.currentTimeMillis());
        valid_date = valid_et_date.getText().toString().trim();
        salary = et_salary.getText().toString().trim();
        remarks = et_remarks.getText().toString().trim();
        people = et_people.getText().toString().trim();
        if (!"".equals(location)) {
            if (!TextUtils.isEmpty(valid_date)) {
                if (!"".equals(salary)) {
                    if (!TextUtils.isEmpty(remarks)) {
                        remarks = FullStopUtil.getFullStop(remarks);
                        salary = salary + "元/天";
                        if ("招聘需求".equals(table)) {
                            if (!"".equals(people)) {
                                people = people + "人";
                                content = "发布日期：" + issue_date + "。\n有效日期：" + valid_date + "。" + "\n位置：" + location + "。" + "\n招聘职位：" + post + "。" + "\n薪资待遇：" + salary + "。" + "\n招聘人数：" + people + "。" + "\n联系人：" + contact + "。\n联系电话：" + phone_address + "。" + "\n备注：" + remarks;
                            } else {
                                content = "发布日期：" + issue_date + "。\n有效日期：" + valid_date + "。" + "\n位置：" + location + "。" + "\n招聘职位：" + post + "。" + "\n薪资待遇：" + salary + "。" + "\n联系人：" + contact + "。\n联系电话：" + phone_address + "。" + "\n备注：" + remarks;
                            }
                        } else {
                            content = "发布日期：" + issue_date + "。\n有效日期：" + valid_date + "。" + "\n位置：" + location + "。" + "\n应聘职位：" + post + "。" + "\n期望薪资：" + salary + "。" + "\n联系人：" + contact + "。\n联系电话：" + phone_address + "。" + "\n备注：" + remarks;
                        }
                        EasyHttp.post(IpConfig.URL_SQL)
                                .params("query", "insert into " + ProductTableName.require6 + "(`product_name`,`contact`,`phone`,`phone_address`,`issue_date`,`valid_date`, `location`,`province`,`city`,`county`,`street`,`specific`,`longitude`,`latitude`,`post`, `salary`,`people`,`identity`,`remarks`,`content`,`date`)values('" + table + "','" + contact + "','" + phone + "','" + phone_address + "','" + issue_date + "','" + valid_date + "','" + location + "','" + province + "','" + city + "','" + county + "','" + street + "','" + specific + "','" + longitude + "','" + latitude + "','" + post + "','" + salary + "','" + people + "','" + identity + "','" + remarks + "','" + content + "','" + date + "')")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
//                                        EasyHttp.post(IpConfig.URL_SQL)
//                                                .params("query", "SELECT phone FROM comment_5_concern WHERE phone_concern = '" + phone + "' ORDER BY id DESC")
//                                                .timeStamp(true)
//                                                .execute(new SuccessCallBack<String>() {
//                                                    @Override
//                                                    public void onSuccess(String str) {
//                                                        data_concern = GsonUtils.getGsonToList(str, Comment.class);
//                                                        for (int i = 0; i < data_concern.size(); i++) {
//                                                            concern = concern + data_concern.get(i).getPhone() + ",";
//                                                            if (i == data_concern.size() - 1) {
//                                                                concern = concern.substring(0, concern.length() - 1);
//                                                            }
//                                                        }
//                                                    }
//                                                });
                                        EasyHttp.post(IpConfig.URL_SQL)
                                                .params("query", "SELECT * FROM " + ProductTableName.require6 + " WHERE phone = " + phone + " ORDER BY id DESC")
                                                .timeStamp(true)
                                                .execute(new SuccessCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(String str) {
                                                        int table_id = GsonUtils.getGsonToList(str, Product.class).get(0).getId();
                                                        EasyHttp.post(IpConfig.URL_SQL)
                                                                .params("query", "insert into comment_1_issue(`phone`, `table_id`, `table_name`)values('" + phone + "'," + table_id + ",'" + ProductTableName.require6 + "')")
                                                                .timeStamp(true)
                                                                .execute(new SuccessCallBack<String>() {
                                                                    @Override
                                                                    public void onSuccess(String s) {
//                                                                        if (concern.length()>5){
//                                                                            pushConcern(ProductTableName.require6,table_id);
//                                                                        }
                                                                        pushAll(ProductTableName.require6, table_id);
                                                                        Intent intent = new Intent(RecruitActivity.this, IssueSuccess.class);
                                                                        startActivityForResult(intent, 15);
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    } else {
                        Util.showToast(RecruitActivity.this, "备注详情必须填写");
                    }
                } else {
                    Util.showToast(RecruitActivity.this, "请填薪资待遇");
                }
            } else {
                Util.showToast(RecruitActivity.this, "请填写有效日期");
            }
        } else {
            Util.showToast(RecruitActivity.this, "位置不能为空");
        }
    }

    private void pushAll(String table_name, int table_id) {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT phone FROM login ORDER BY phone DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data_user = GsonUtils.getGsonToList(s, User.class);
                        for (int i = 0; i < data_user.size(); i++) {
                            if (data_user.get(i).getPhone()!=null){
                                phone_all = phone_all + data_user.get(i).getPhone()+",";
                            }else {
                                phone_all = phone_all.substring(0, phone_all.length() - 1);
                                break;
                            }
                        }
                        EasyHttp.post(IpConfig.BASE + "server_mariadb_ios_use/umpush.php")
                                .params("alias", phone_all)
                                .params("title", "招聘提醒")
                                .params("text", user_name + "发布了" + table)
                                .params("from", phone)
                                .params("table_name", table_name)
                                .params("table_id", String.valueOf(table_id))
                                .params("type_class", "2")
                                .timeStamp(true)
                                .execute(new SuccessCallBack<String>() {

                                });
                    }
                });
    }

    private void pushConcern(String table_name,int table_id ){
        EasyHttp.post("http://paowan.com.cn/server_mariadb_ios_use/umpush.php")
                .params("alias", concern)
                .params("title", "关注提醒")
                .params("text", user_name+"发布了新的"+table)
                .params("from", phone)
                .params("table_name", table_name)
                .params("table_id", String.valueOf(table_id))
                .params("type_class", "2")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {

                });
    }
//    private void startActivity(Class<?> activity) {
//        Intent intent = new Intent();
//        intent.setClass(RecruitActivity.this, activity);
//        startActivity(intent);
//    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

}
