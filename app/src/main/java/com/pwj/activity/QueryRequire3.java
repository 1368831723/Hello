package com.pwj.activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;
import com.pwj.base.BaseActivityQueryRequire;
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.chat.ChatActivity;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;


/**
 * Created by 13688 on 2019/3/7.
 */

public class QueryRequire3 extends BaseActivityQueryRequire {
    private MyAdapter myAdapter;

    @Override
    public void initData() {
        data = new ArrayList<>();
        title_tv.setText("租厂需求");
        queryType();
    }


    @OnClick({R.id.issue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.issue:
                startActivity(IssueRequire3.class);
                finish();
                break;
        }
    }

    public void queryType() {
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query","SELECT url,location,duration,remarks,contact,phone FROM rent order by id desc")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data= GsonUtils.getGsonToList(s,Product.class);
                        if (data.size()==0){
                            progressbar.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < data.size(); i++) {
                            phone = data.get(i).getPhone();
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query","SELECT url3,user_name FROM login where phone = '"+phone+"'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_user.clear();
                                            data_user= GsonUtils.getGsonToList(s,User.class);
                                            data_users.addAll(data_user);
                                            if (data_users.size()==data.size()){
                                                progressbar.setVisibility(View.GONE);
                                                recycler.setLayoutManager(new LinearLayoutManager(QueryRequire3.this, LinearLayoutManager.VERTICAL, false));
                                                myAdapter = new MyAdapter();
                                                recycler.setAdapter(myAdapter);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(QueryRequire3.this).inflate(R.layout.item_product, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            List<ImageInfo> imageInfos = getImageInfos(position);
            holder.gridView.setAdapter(new AssNineGridViewClickAdapter(QueryRequire3.this, imageInfos));
            url = data_users.get(position).getUrl3();
            String user_name = data_users.get(position).getUser_name();
            String tv1 = data.get(position).getLocation()+data.get(position).getDuration() + ",";
            String tv2 = data.get(position).getContact() + "," + data.get(position).getPhone()+ "," + data.get(position).getRemarks() ;
            holder.tv.setText(tv1 + tv2);
            Glide.with(QueryRequire3.this).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QueryRequire3.this, ChatActivity.class);
                    intent.putExtra("other_phone", data.get(position).getPhone());
                    intent.putExtra("other_img",data_users.get(position).getUrl3() );
                    startActivity(intent);
                }
            });
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
            private AssNineGridView gridView;
            private TextView tv;
            private ImageView img;
            private TextView name_tv;
            MyViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                gridView = itemView.findViewById(R.id.gridView);
                img = itemView.findViewById(R.id.img);
                name_tv = itemView.findViewById(R.id.name_tv);
            }
        }
    }
}
