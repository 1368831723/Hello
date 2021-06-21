package com.pwj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.pwj.BaseActivity;
import com.pwj.bean.Product;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.KeyWords;
import com.pwj.helloya.R;
import com.pwj.interfaces.QueryRunnable;
import com.pwj.interfaces.ThreadCallback;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 13688 on 2019/5/9.
 */

public class SearchAdsResultActivity extends BaseActivity {
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private String keyWords;
    private List<Product> data = new ArrayList<>();
    private List<Product> data1 = null;
    private List<Product> data2 = null;
    private List<Product> data3 = null;
    private List<Product> data4 = null;
    private List<Product> data5 = null;
    private List<Product> data6 = null;
    private List<Product> data7 = null;
    private List<Product> data8 = null;
    private List<Product> data9 = null;
    private List<Product> data10 = null;
    private List<Product> data11 = null;
    private List<Product> data12 = null;
    private List<Product> data13 = null;
    private List<Product> data14 = null;
    private List<Product> data15 = null;
    private List<Product> data16 = null;
    private List<Product> data17 = null;
    private MyAdapter myAdapter;
    private String[] products;
    private boolean flag = true;
    private String[] table_product = new String[]{
            "product_1_second_hand", "product_2_wool", "product_3_resist", "product_4_sponge", "product_5_tube",
            "product_6_door", "product_7_elect_machine", "product_8_shield", "product_9_plate", "product10_lock_ring",
            "product11_transporter", "product12_factory", "product13_logistics", "product14_casting", "product15_pwj",
            "product16_track", "product17_sand", "product_0_others"};
    private int max = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_search_result);
        ButterKnife.bind(this);
        initArrays();
        initData();
    }

    private void initArrays() {
        products = new String[]{KeyWords.product1, KeyWords.product2, KeyWords.product3, KeyWords.product4, KeyWords.product5,
                KeyWords.product6, KeyWords.product7, KeyWords.product8, KeyWords.product9, KeyWords.product10,
                KeyWords.product11, KeyWords.product12, KeyWords.product13, KeyWords.product14, KeyWords.product15,
                KeyWords.product16, KeyWords.product17};
    }

    private void initData() {
        keyWords = getIntent().getStringExtra("keyWords");
        for (int i = 0; i < products.length; i++) {
            if (products[i].contains(keyWords)) {

                tv.setVisibility(View.GONE);
                progressbar.setVisibility(View.VISIBLE);
                break;
            } else {
                tv.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
            }
        }
        if (products[0].contains(keyWords)) {
            data1 = new ArrayList<>();
            queryType1(0);
        }
        if (products[1].contains(keyWords)) {
            data2 = new ArrayList<>();
            queryType2(1);
        }
        if (products[2].contains(keyWords)) {
            data3 = new ArrayList<>();
            queryType3(2);
        }
        if (products[3].contains(keyWords)) {
            data4 = new ArrayList<>();
            queryType4(3);
        }
        if (products[4].contains(keyWords)) {
            data5 = new ArrayList<>();
            queryType5(4);
        }
        if (products[5].contains(keyWords)) {
            data6 = new ArrayList<>();
            queryType6(5);
        }
        if (products[6].contains(keyWords)) {
            data7 = new ArrayList<>();
            queryType7(6);
        }
        if (products[7].contains(keyWords)) {
            data8 = new ArrayList<>();
            queryType8(7);
        }
        if (products[8].contains(keyWords)) {
            data9 = new ArrayList<>();
            queryType9(8);
        }
        if (products[9].contains(keyWords)) {
            data10 = new ArrayList<>();
            queryType10(9);
        }
        if (products[10].contains(keyWords)) {
            data11 = new ArrayList<>();
            queryType11(10);
        }
        if (products[11].contains(keyWords)) {
            data12 = new ArrayList<>();
            queryType12(11);
        }
        if (products[12].contains(keyWords)) {
            data13 = new ArrayList<>();
            queryType13(12);
        }
        if (products[13].contains(keyWords)) {
            data14 = new ArrayList<>();
            queryType14(13);
        }
        if (products[14].contains(keyWords)) {
            data15 = new ArrayList<>();
            queryType15(14);
        }
        if (products[15].contains(keyWords)) {
            data16 = new ArrayList<>();
            queryType16(15);
        }
        if (products[16].contains(keyWords)) {
            data17 = new ArrayList<>();
            queryType17(16);
        }
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                finish();
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (flag) {
                if (data1 != null) {
                    data.addAll(data1);
                }
                if (data2 != null) {
                    data.addAll(data2);
                }
                if (data3 != null) {
                    data.addAll(data3);
                }
                if (data4 != null) {
                    data.addAll(data4);
                }
                if (data5 != null) {
                    data.addAll(data5);
                }
                if (data6 != null) {
                    data.addAll(data6);
                }
                if (data7 != null) {
                    data.addAll(data7);
                }
                if (data8 != null) {
                    data.addAll(data8);
                }
                if (data9 != null) {
                    data.addAll(data9);
                }
                if (data10 != null) {
                    data.addAll(data10);
                }
                if (data11 != null) {
                    data.addAll(data11);
                }
                if (data12 != null) {
                    data.addAll(data12);
                }
                if (data13 != null) {
                    data.addAll(data13);
                }
                if (data14 != null) {
                    data.addAll(data14);
                }
                if (data15 != null) {
                    data.addAll(data15);
                }
                if (data16 != null) {
                    data.addAll(data16);
                }
                if (data17 != null) {
                    data.addAll(data17);
                }

                progressbar.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchAdsResultActivity.this, LinearLayoutManager.VERTICAL, false));
                myAdapter = new MyAdapter();
                recyclerView.setAdapter(myAdapter);
                flag = false;
            }
        }
    };

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchAdsResultActivity.this).inflate(R.layout.item_product_search, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            List<ImageInfo> imageInfos = getImageInfos(position);
            holder.gridView.setAdapter(new AssNineGridViewClickAdapter(SearchAdsResultActivity.this, imageInfos));
            String url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            holder.tv.setText(data.get(position).getContent());
            Glide.with(SearchAdsResultActivity.this).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private List<ImageInfo> getImageInfos(int position) {
            List<ImageInfo> imageInfos = new ArrayList<>();
            List<String> uri_list = Arrays.asList(data.get(position).getUrl().split(","));
            for (String url : uri_list) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setBigImageUrl(url);
                imageInfo.setThumbnailUrl(url);
                imageInfos.add(imageInfo);
            }
            return imageInfos;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView img;
            private TextView name_tv;
            private TextView tv;
            private AssNineGridView gridView;

            MyViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
                name_tv = itemView.findViewById(R.id.name_tv);
                tv = itemView.findViewById(R.id.tv);
                gridView = itemView.findViewById(R.id.gridView);
            }
        }
    }

    private void queryType1(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data1 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType2(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data2 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType3(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data3 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType4(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data4 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType5(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data5 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType6(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data6 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType7(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data7 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType8(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data8 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType9(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data9 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType10(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data10 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType11(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data11 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType12(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data12 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType13(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data13 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType14(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data14 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType15(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data15 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType16(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data16 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    private void queryType17(int type) {
        String sql = "SELECT login.url3,login.user_name," + table_product[type] + ".* FROM login," + table_product[type] + " WHERE " + table_product[type] + ".phone = login.phone ORDER BY id DESC";
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", sql)
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String str) {
                        data17 = GsonUtils.getGsonToList(str, Product.class);
                        handler.sendEmptyMessageDelayed(0, 5000);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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
