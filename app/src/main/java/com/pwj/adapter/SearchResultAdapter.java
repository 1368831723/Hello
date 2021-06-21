package com.pwj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.pwj.bean.Product;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import static android.content.ContentValues.TAG;

/**
 * Created by 13688 on 2018/5/13.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Product> data = new ArrayList<>();
    //    private View view;

    public SearchResultAdapter(Context context, List<Product> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //這個類型是條目的類型，因為只有2種，所以只有0跟1兩種,类型的下标是从0开始计算的
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
        MyHolder_item myHolder_item = new MyHolder_item(view);
        return myHolder_item;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(data.get(position).getNumber()==1){

        }

    }


    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getNumber();
        return type;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder_item extends RecyclerView.ViewHolder {
        private TextView tv;
        private AssNineGridView gridView;
        public MyHolder_item(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            gridView = itemView.findViewById(R.id.gridView);
        }
    }
    private List<ImageInfo> getImageInfos(int position) {
        List<ImageInfo> imageInfos = new ArrayList<>();
        List<String> uri_list = data.get(position).getUri_list();
        for (String url : uri_list) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setBigImageUrl(url);
            imageInfo.setThumbnailUrl(url);
            imageInfos.add(imageInfo);
        }
        return imageInfos;
    }

}
