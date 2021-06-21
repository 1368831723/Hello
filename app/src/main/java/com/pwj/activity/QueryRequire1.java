package com.pwj.activity;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.pwj.bean.Comment;
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

import static android.content.ContentValues.TAG;


/**
 * Created by 13688 on 2019/3/7.
 */

public class QueryRequire1 extends BaseActivityQueryRequire {
    private MyAdapter myAdapter;

    @Override
    public void initData() {
        data = new ArrayList<>();
        title_tv.setText("求购需求");
        queryType();
    }


    @OnClick({R.id.issue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.issue:
                startActivity(IssueRequire1.class);
                finish();
                break;
        }
    }
    public void queryType() {
        key = 0;
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query","SELECT login.url3,login.user_name,require_0_purchase.* FROM login,require_0_purchase WHERE require_0_purchase.phone = login.phone ORDER BY id DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        data.clear();
                        data= GsonUtils.getGsonToList(s,Product.class);
                        for (int i = 0; i < data.size(); i++) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "SELECT * FROM comment_3_like_product WHERE table_name = 'product_1_second_hand' AND  table_id = '" + data.get(i).getId() + "'")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            data_comment = GsonUtils.getGsonToList(s, Comment.class);
                                            if(data_comment.size()>0){
                                                if (data_comment.get(0).getLike()==1){
                                                    like_map.put(key,1);

                                                }else {
                                                    like_map.put(key,0);

                                                }
                                            }else {
                                                like_map.put(key,0);

                                            }
                                            key = key+1;
                                            if (key == data.size()) {
                                                recycler.setLayoutManager(new LinearLayoutManager(QueryRequire1.this, LinearLayoutManager.VERTICAL, false));
                                                myAdapter = new MyAdapter();
                                                recycler.setAdapter(myAdapter);

                                            }
                                        }
                                    });
                        }
                        if (data.size()==0){
                            progressbar.setVisibility(View.GONE);
                        }
//                        for (int i = 0; i < data.size(); i++) {
//                            phone = data.get(i).getPhone();
//                            EasyHttp.post(IpConfig.URL_SQL)
//                                    .params("query","SELECT url3,user_name FROM login where phone = '"+phone+"'")
//                                    .timeStamp(true)
//                                    .execute(new SuccessCallBack<String>() {
//                                        @Override
//                                        public void onSuccess(String s) {
//                                            data_user.clear();
//                                            data_user= GsonUtils.getGsonToList(s,User.class);
//                                            data_users.addAll(data_user);
//                                            if (data_users.size()==data.size()){
//                                                progressbar.setVisibility(View.GONE);
//                                                recycler.setLayoutManager(new LinearLayoutManager(QueryRequire1.this, LinearLayoutManager.VERTICAL, false));
//                                                myAdapter = new MyAdapter();
//                                                recycler.setAdapter(myAdapter);
//                                            }
//                                        }
//                                    });
//                        }
                    }
                });
    }





    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(QueryRequire1.this).inflate(R.layout.item_product, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            List<ImageInfo> imageInfos = getImageInfos(position);
            holder.gridView.setAdapter(new AssNineGridViewClickAdapter(QueryRequire1.this, imageInfos));
            url = data.get(position).getUrl3();
            String user_name = data.get(position).getUser_name();
            String tv1 = data.get(position).getName() + "," + data.get(position).getLocation()  + ",";
            String tv2 = data.get(position).getContact() + "," + data.get(position).getPhone()+ "," + data.get(position).getRemarks() ;
            holder.tv.setText(tv1 + tv2);
            Glide.with(QueryRequire1.this).load(url).into(holder.img);
            holder.name_tv.setText(user_name);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QueryRequire1.this, ChatActivity.class);
                    intent.putExtra("other_phone", data.get(position).getPhone());
                    intent.putExtra("other_img",data.get(position).getUrl3() );
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
