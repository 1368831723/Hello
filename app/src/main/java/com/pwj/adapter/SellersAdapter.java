package com.pwj.adapter;

import android.content.Context;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwj.bean.Product;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import static android.content.ContentValues.TAG;

/**
 * Created by 13688 on 2018/5/13.
 */

public class SellersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Product> data = new ArrayList<>();
    private List<Product> data_img = new ArrayList<>();
//    private RecyclerView recyclerPicture;
    //    private View view;
    private static final int ITEM_1 = 0;
    private static final int ITEM_2 = 1;
    private static final int ITEM_3 = 2;
    private static final int ITEM_4 = 3;
    private static final int ITEM_5 = 4;
    private static final int ITEM_6 = 5;
    private static final int ITEM_7 = 6;
    private static final int ITEM_8 = 7;
    private static final int ITEM_9 = 8;
    private static final int ITEM_10 = 9;
    private static final int ITEM_11 = 10;
    private static final int ITEM_12 = 11;
    private static final int ITEM_13 = 12;
    private static final int ITEM_14 = 13;

    public SellersAdapter(Context context, List<Product> data) {
        this.mContext = context;
        this.data = data;
    }

    public SellersAdapter(Context context, List<Product> data, List<Product> data_img) {
        this.mContext = context;
        this.data = data;
        this.data_img = data_img;
        initRecycleImg();
    }

    private void initRecycleImg() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //這個類型是條目的類型，因為只有2種，所以只有0跟1兩種,类型的下标是从0开始计算的
        if (viewType == ITEM_1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
            MyHolder_item1 myHolder_item1 = new MyHolder_item1(view);
            return myHolder_item1;
        }
        if (viewType == ITEM_2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_2, parent, false);
            MyHolder_item2 myHolder_item2 = new MyHolder_item2(view);
            return myHolder_item2;
        }
        if (viewType == ITEM_3) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_3, parent, false);
            MyHolder_item3 myHolder_item3 = new MyHolder_item3(view);
            return myHolder_item3;
        }
        if (viewType == ITEM_4) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_4, parent, false);
            MyHolder_item4 myHolder_item4 = new MyHolder_item4(view);
            return myHolder_item4;
        }
        if (viewType == ITEM_5) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_5, parent, false);
            MyHolder_item5 myHolder_item5 = new MyHolder_item5(view);
            return myHolder_item5;
        }
        if (viewType == ITEM_6) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
            MyHolder_item1 myHolder_item1 = new MyHolder_item1(view);
            return myHolder_item1;
        }
        if (viewType == ITEM_7) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
            MyHolder_item1 myHolder_item1 = new MyHolder_item1(view);
            return myHolder_item1;
        }
        if (viewType == ITEM_8) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_2, parent, false);
            MyHolder_item2 myHolder_item2 = new MyHolder_item2(view);
            return myHolder_item2;
        }
        if (viewType == ITEM_9) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
            MyHolder_item1 myHolder_item1 = new MyHolder_item1(view);
            return myHolder_item1;
        }
        if (viewType == ITEM_10) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_1, parent, false);
            MyHolder_item1 myHolder_item1 = new MyHolder_item1(view);
            return myHolder_item1;
        }
        if (viewType == ITEM_11) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_4, parent, false);
            MyHolder_item4 myHolder_item4 = new MyHolder_item4(view);
            return myHolder_item4;
        }
        if (viewType == ITEM_12) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_12, parent, false);
            MyHolder_item12 myHolder_item12 = new MyHolder_item12(view);
            return myHolder_item12;
        }
        if (viewType == ITEM_13) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_13, parent, false);
            MyHolder_item13 myHolder_item13 = new MyHolder_item13(view);
            return myHolder_item13;
        }
        if (viewType == ITEM_14) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_4, parent, false);
            MyHolder_item4 myHolder_item4 = new MyHolder_item4(view);
            return myHolder_item4;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder_item1) {

            ((MyHolder_item1) holder).long_tv.setText(data.get(position).getLon());
            ((MyHolder_item1) holder).wide_tv.setText(data.get(position).getWide());
            ((MyHolder_item1) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item1) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item1) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item1) holder).consign_tv.setText(data.get(position).getConsign());
            ((MyHolder_item1) holder).postage_tv.setText(data.get(position).getPostage());
        } else if (holder instanceof MyHolder_item2) {

            ((MyHolder_item2) holder).spec_tv.setText(data.get(position).getSpec());
            ((MyHolder_item2) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item2) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item2) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item2) holder).consign_tv.setText(data.get(position).getConsign());
            ((MyHolder_item2) holder).postage_tv.setText(data.get(position).getPostage());
        } else if (holder instanceof MyHolder_item3) {

            ((MyHolder_item3) holder).wide_tv.setText(data.get(position).getWide());
            ((MyHolder_item3) holder).thickness_tv.setText(data.get(position).getThickness());
            ((MyHolder_item3) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item3) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item3) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item3) holder).consign_tv.setText(data.get(position).getConsign());
            ((MyHolder_item3) holder).postage_tv.setText(data.get(position).getPostage());
        } else if (holder instanceof MyHolder_item4) {


            ((MyHolder_item4) holder).long_tv.setText(data.get(position).getLon());
            ((MyHolder_item4) holder).wide_tv.setText(data.get(position).getWide());
            ((MyHolder_item4) holder).thickness_tv.setText(data.get(position).getThickness());
            ((MyHolder_item4) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item4) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item4) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item4) holder).consign_tv.setText(data.get(position).getConsign());
            ((MyHolder_item4) holder).postage_tv.setText(data.get(position).getPostage());
        } else if (holder instanceof MyHolder_item5) {

            ((MyHolder_item5) holder).diameter_tv.setText(data.get(position).getSpec());
            ((MyHolder_item5) holder).long_tv.setText(data.get(position).getLon());
            ((MyHolder_item5) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item5) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item5) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item5) holder).consign_tv.setText(data.get(position).getConsign());
            ((MyHolder_item5) holder).postage_tv.setText(data.get(position).getPostage());
        } else if (holder instanceof MyHolder_item12) {

            ((MyHolder_item12) holder).area_tv.setText(data.get(position).getArea());
            ((MyHolder_item12) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item12) holder).price_tv.setText(data.get(position).getPrice());
            ((MyHolder_item12) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item12) holder).location_tv.setText(data.get(position).getLocation());
        } else if (holder instanceof MyHolder_item13) {

            ((MyHolder_item13) holder).start_time_tv.setText(data.get(position).getArea());
            ((MyHolder_item13) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item13) holder).location_start_tv.setText(data.get(position).getPrice());
            ((MyHolder_item13) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item13) holder).location_end_tv.setText(data.get(position).getLocation());
        }
    }


    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getNumber();
//        Log.e(TAG, "MyAdapter中的data的长度是: " + data.size());

        return type == 1 ? ITEM_1 : type == 2 ? ITEM_2 : type == 3 ? ITEM_3 : type == 4 ? ITEM_4 : type == 5 ? ITEM_5 : type == 6 ? ITEM_6 : type == 7 ? ITEM_7 : type == 8 ? ITEM_8 : type == 9 ? ITEM_9 : type == 10 ? ITEM_10 : type == 11 ? ITEM_11 : type == 12 ? ITEM_12 : type == 13 ? ITEM_13 : ITEM_14;
        //這個類型是由data這個list獲取的，所以只有1根2，上面的意思是1代表左，1以外的代表右
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder_item1 extends RecyclerView.ViewHolder {
        private TextView long_tv;
        private TextView wide_tv;
        private TextView price_tv;
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView consign_tv;
        private TextView postage_tv;

        public MyHolder_item1(View itemView) {
            super(itemView);
            long_tv = itemView.findViewById(R.id.long_tv);
            wide_tv = itemView.findViewById(R.id.wide_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            consign_tv = itemView.findViewById(R.id.consign_tv);
            postage_tv = itemView.findViewById(R.id.postage_tv);
        }
    }

    class MyHolder_item2 extends RecyclerView.ViewHolder {
        private TextView spec_tv;
        private TextView price_tv;
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView consign_tv;
        private TextView postage_tv;

        public MyHolder_item2(View itemView) {
            super(itemView);
            spec_tv = itemView.findViewById(R.id.spec_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            consign_tv = itemView.findViewById(R.id.consign_tv);
            postage_tv = itemView.findViewById(R.id.postage_tv);
        }
    }

    class MyHolder_item3 extends RecyclerView.ViewHolder {
        private TextView wide_tv;
        private TextView thickness_tv;
        private TextView price_tv;
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView consign_tv;
        private TextView postage_tv;

        public MyHolder_item3(View itemView) {
            super(itemView);
            wide_tv = itemView.findViewById(R.id.wide_tv);
            thickness_tv = itemView.findViewById(R.id.thickness_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            consign_tv = itemView.findViewById(R.id.consign_tv);
            postage_tv = itemView.findViewById(R.id.postage_tv);
        }
    }

    class MyHolder_item4 extends RecyclerView.ViewHolder {
        private ImageView picture_img;
        private TextView long_tv;
        private TextView wide_tv;
        private TextView thickness_tv;
        private TextView price_tv;
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView consign_tv;
        private TextView postage_tv;

        public MyHolder_item4(View itemView) {
            super(itemView);
            picture_img = itemView.findViewById(R.id.picture_img);
            long_tv = itemView.findViewById(R.id.long_tv);
            wide_tv = itemView.findViewById(R.id.wide_tv);
            thickness_tv = itemView.findViewById(R.id.thickness_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            consign_tv = itemView.findViewById(R.id.consign_tv);
            postage_tv = itemView.findViewById(R.id.postage_tv);
        }
    }

    class MyHolder_item5 extends RecyclerView.ViewHolder {
        private ImageView picture_img;
        private TextView diameter_tv;
        private TextView long_tv;
        private TextView price_tv;
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView consign_tv;
        private TextView postage_tv;

        public MyHolder_item5(View itemView) {
            super(itemView);
            picture_img = itemView.findViewById(R.id.picture_img);
            diameter_tv = itemView.findViewById(R.id.diameter_tv);
            long_tv = itemView.findViewById(R.id.long_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            consign_tv = itemView.findViewById(R.id.consign_tv);
            postage_tv = itemView.findViewById(R.id.postage_tv);
        }
    }

    class MyHolder_item12 extends RecyclerView.ViewHolder {
        private ImageView picture_img;
        private TextView area_tv;
        private TextView contact_tv;
        private TextView price_tv;
        private TextView phone_tv;
        private TextView location_tv;

        public MyHolder_item12(View itemView) {
            super(itemView);
            area_tv = itemView.findViewById(R.id.area_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
        }
    }


    class MyHolder_item13 extends RecyclerView.ViewHolder {
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView start_time_tv;
        private TextView location_start_tv;
        private TextView location_end_tv;

        public MyHolder_item13(View itemView) {
            super(itemView);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            start_time_tv = itemView.findViewById(R.id.start_time_tv);
            location_start_tv = itemView.findViewById(R.id.location_start_tv);
            location_end_tv = itemView.findViewById(R.id.location_end_tv);
        }
    }

}
