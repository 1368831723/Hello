package com.pwj.activity;


import android.content.Intent;
import android.database.Cursor;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pwj.BaseActivity;
import com.pwj.bean.Citys;
import com.pwj.bean.Countys;
import com.pwj.bean.Customer;

import com.pwj.bean.Provinces;
import com.pwj.utils.LoginInfo;
import com.pwj.tree.DataBaseHelper;


import com.pwj.helloya.R;
import com.pwj.tree.MyNodeViewFactory;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.pwj.tree.TreeNode;
import com.pwj.tree.TreeView;
import com.umeng.analytics.MobclickAgent;

public class CityListActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_im)
    LinearLayout title_im;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.viewGroup)
    ViewGroup viewGroup;
    private SQLiteDatabase db;
    private DataBaseHelper dataBaseHelper;
    //    private List<CityCount>data=new ArrayList<>();
    private List<Customer> data_province = new ArrayList<>();
    private List<String> list_province = new ArrayList<>();
    private List<String> list_city = new ArrayList<>();
    private Provinces provinces;
    private Citys citys;
    private Countys countys;
    private String[] choices;
    private int child_first = 0;
    //    private ViewGroup viewGroup;
    private TreeNode root;
    private TreeView treeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_city_list);
        ButterKnife.bind(this);
        title_im.setOnClickListener(this);
        title_tv.setText("客户列表");
        initSqlData();
        treeView = new TreeView(root, this, new MyNodeViewFactory());
        View view = treeView.getView();
//        setLightStatusBar(viewGroup);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewGroup.addView(view);
        initAdapter();
    }

    private void initSqlData() {
        root = TreeNode.root();
        Intent intent = getIntent();
//        String choice = intent.getStringExtra("choice");
        String choice = LoginInfo.getString(CityListActivity.this, "choice", "");
        choices = choice.split(",");
//        String sql = "SELECT province,companyname,liaisons,www,address,description,main_products_using_pwj,COUNT(*) AS Count from pwj_user WHERE ";
        String sql = "SELECT province,COUNT(*) AS Count from pwj_user WHERE ";
        String st = "";
        for (int i = 0; i < choices.length; i++) {
            if (i == 0) {
                st += "main_products_using_pwj = \"" + choices[i] + "\"";
            } else {
                st += " or main_products_using_pwj = \"" + choices[i] + "\"";
            }
        }
        sql = sql + st;
        sql += " GROUP BY province";

        try {
            dataBaseHelper = new DataBaseHelper(this);
            db = dataBaseHelper.openDatabase();
//        db = dataBaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String province = cursor.getString(0);
                    String count = cursor.getString(1);
                    provinces = new Provinces(province, count);
                    TreeNode treeNode = new TreeNode(new Provinces(province, count));
                    treeNode.setLevel(0);
                    root.addChild(treeNode);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }


    private void initAdapter() {


    }

    @Override
    public void onClick(View view) {
        CityListActivity.this.finish();
    }


    private void setLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            getWindow().setStatusBarColor(Color.WHITE);
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

}