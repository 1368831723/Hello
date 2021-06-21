package com.pwj.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.pwj.bean.Product;
import com.pwj.bean.User;
import com.pwj.chat.ChatActivity;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 13688 on 2019/9/14.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> implements View.OnClickListener{

    private Context mContext;
    private List<Product> data = new ArrayList<>();
    private List<User> data_users= new ArrayList<>();
    private String tv1 = "";
    private String tv2 = "";

    public ProductAdapter(Context context, List<Product> data,List<User> data_users) {
        mContext = context;
        this.data = data;
        this.data_users = data_users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<ImageInfo> imageInfos = getImageInfos(position);
        String url = data_users.get(position).getUrl3();
        String user_name = data_users.get(position).getUser_name();
        int type = data.get(position).getNumber();
        if (type == 12 || type == 13) {
            tv2 = "," + data.get(position).getContact() + "," + data.get(position).getPhone() + "," + data.get(position).getDescription();
        } else {
            tv2 = "," + data.get(position).getPrice() + "," + data.get(position).getConsign() + "," + data.get(position).getPostage() + "," + data.get(position).getContact() + "," + data.get(position).getPhone() + "," + data.get(position).getLocation() + "," + data.get(position).getDescription();
        }
        switch (type) {
            case 0:
                tv1 = data.get(position).getGoods() + "," + data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getSpec();
                break;
            case 1:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getThickness();
                break;
            case 2:
                tv1 = data.get(position).getSpec();
                break;
            case 3:
                tv1 = data.get(position).getWide() + "," + data.get(position).getThickness();
                break;
            case 4:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getThickness();
                break;
            case 5:
                tv1 = data.get(position).getDiameter() + "," + data.get(position).getLongs();
                break;
            case 6:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide();
                break;
            case 7:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide();
                break;
            case 8:
                tv1 = data.get(position).getSpec();
                break;
            case 9:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide();
                break;
            case 10:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide();
                break;
            case 11:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide();
                break;
            case 12:
                tv1 =  data.get(position).getLocation()+ "," +data.get(position).getArea() + ","  + data.get(position).getPrice();
                break;
            case 13:
                tv1 = data.get(position).getStart_time() + "," + data.get(position).getLocation_start() + "," + data.get(position).getLocation_end();
                break;
            case 14:
                tv1 = data.get(position).getGoods() + "," + data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getSpec();
                break;
            case 15:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getThickness();
                break;
            case 16:
                tv1 = data.get(position).getLongs() + "," + data.get(position).getWide() + "," + data.get(position).getThickness();
                break;
            case 17:
                tv1 = data.get(position).getSpec();
                break;
        }
        holder.gridView.setAdapter(new AssNineGridViewClickAdapter(mContext, imageInfos));
        holder.tv.setText(tv1 + tv2);
        Glide.with(mContext).load(url).into(holder.img);
        holder.name_tv.setText(user_name);
        holder.img.setOnClickListener(ProductAdapter.this);
        holder.img.setTag(position);
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("other_phone", data.get(position).getPhone());
        intent.putExtra("other_img",data_users.get(position).getUrl3() );
        mContext.startActivity(intent);
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

        MyViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.tv);
            gridView = view.findViewById(R.id.gridView);
            img = view.findViewById(R.id.img);
            name_tv = view.findViewById(R.id.name_tv);
        }
    }

}
