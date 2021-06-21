package com.pwj.adapter;

import android.content.Context;

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

/**
 * Created by 13688 on 2018/5/13.
 */

public class BuyersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Product> data = new ArrayList<>();
    //    private View view;
    private static final int ITEM_1 = 0;
    private static final int ITEM_2 = 1;
    private static final int ITEM_3 = 2;
    private static final int ITEM_4 = 3;

    public BuyersAdapter(Context context, List<Product> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //這個類型是條目的類型，因為只有2種，所以只有0跟1兩種,类型的下标是从0开始计算的

        if (viewType == ITEM_1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_purchase, parent, false);
            MyHolder_item_purchase myHolder_item12 = new MyHolder_item_purchase(view);
            return myHolder_item12;
        }
        if (viewType == ITEM_2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_repair, parent, false);
            MyHolder_item_repair myHolder_item_repair = new MyHolder_item_repair(view);
            return myHolder_item_repair;
        }
        if (viewType == ITEM_3) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_purchase, parent, false);
            MyHolder_item_purchase myHolder_item_purchase = new MyHolder_item_purchase(view);
            return myHolder_item_purchase;
        }
        if (viewType == ITEM_4) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_logistics, parent, false);
            MyHolder_item_logistics myHolder_item_logistics = new MyHolder_item_logistics(view);
            return myHolder_item_logistics;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder_item_purchase) {
            ((MyHolder_item_purchase) holder).name_tv.setText(data.get(position).getLcn_remark());
            ((MyHolder_item_purchase) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item_purchase) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item_purchase) holder).location_tv.setText(data.get(position).getLocation());
            ((MyHolder_item_purchase) holder).remarks_tv.setText(data.get(position).getRemarks());

        } else if (holder instanceof MyHolder_item_repair) {


            ((MyHolder_item_repair) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item_repair) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item_repair) holder).location_tv.setText(data.get(position).getLocation() + data.get(position).getLcn_remark());
            ((MyHolder_item_repair) holder).remarks_tv.setText( data.get(position).getRemarks());
        } else if (holder instanceof MyHolder_item_logistics) {

            ((MyHolder_item_logistics) holder).name_tv.setText(data.get(position).getName());
            ((MyHolder_item_logistics) holder).phone_tv.setText(data.get(position).getPhone());
            ((MyHolder_item_logistics) holder).contact_tv.setText(data.get(position).getContact());
            ((MyHolder_item_logistics) holder).location_tv.setText(data.get(position).getLocation());
            ((MyHolder_item_logistics) holder).destination_tv.setText(data.get(position).getDestination());
            ((MyHolder_item_logistics) holder).remarks_tv.setText(data.get(position).getRemarks());

        }
    }


    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getNumber();
//        Log.e(TAG, "MyAdapter中的data的长度是: " + data.size());

        return type == 1 ? ITEM_1 : type == 2 ? ITEM_2 : type == 3 ? ITEM_3 : ITEM_4;
        //這個類型是由data這個list獲取的，所以只有1根2，上面的意思是1代表左，1以外的代表右
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder_item_repair extends RecyclerView.ViewHolder {

        private TextView contact_tv;
        private TextView phone_tv;
        private TextView location_tv;
        private TextView remarks_tv;

        public MyHolder_item_repair(View itemView) {
            super(itemView);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            remarks_tv = itemView.findViewById(R.id.remarks_tv);
        }
    }

    class MyHolder_item_purchase extends RecyclerView.ViewHolder {
        private TextView name_tv;
        private TextView contact_tv;
        private TextView location_tv;
        private TextView phone_tv;
        private TextView remarks_tv;

        public MyHolder_item_purchase(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.name_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            remarks_tv = itemView.findViewById(R.id.remarks_tv);
        }
    }

    class MyHolder_item_logistics extends RecyclerView.ViewHolder {
        private TextView name_tv;
        private TextView contact_tv;
        private TextView location_tv;
        private TextView destination_tv;
        private TextView phone_tv;
        private TextView remarks_tv;

        public MyHolder_item_logistics(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.name_tv);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            destination_tv = itemView.findViewById(R.id.destination_tv);
            remarks_tv = itemView.findViewById(R.id.remarks_tv);
        }
    }
}
