package com.pwj.pages;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pwj.activity.BidDetailActivity;

import com.pwj.adapter.BaseRcyAdapter;

import com.pwj.bean.BiddingCurrent;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han13688 on 2018/4/25.
 */

public class BiddingCurrentView extends LinearLayout{

    private RecyclerView recyclerView;
    private List<BiddingCurrent> data = new ArrayList<>();
    private static Context mContext;
    private static BaseRcyAdapter adapter;
    private LinearLayout item_linear;

    public static BiddingCurrentView getView(Context context, List<BiddingCurrent> data) {
        mContext = context;
        BiddingCurrentView currentView = (BiddingCurrentView) View.inflate(context, R.layout.view_bidding_current, null);
        currentView.initData(data);
        currentView.initListener();
        return currentView;
    }


    public static void refresh() {
        adapter.notifyDataSetChanged();
    }

    public BiddingCurrentView(Context context) {
        super(context);
    }

    public BiddingCurrentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BiddingCurrentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initData(List<BiddingCurrent> data) {
        if (getChildCount() == 0) {
            return;
        }
        recyclerView = findViewById(R.id.page1_rcy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseRcyAdapter(data, R.layout.item_word) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                TextView bid_tv1 = holder.getView(R.id.bid_tv1);
                TextView bid_tv2 = holder.getView(R.id.bid_tv2);
                TextView bid_tv3 = holder.getView(R.id.bid_tv3);
                bid_tv1.setText(data.get(position).getTimes());
                bid_tv2.setText(data.get(position).getTitle());
                bid_tv3.setText(data.get(position).getLink());
            }
        };
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, BidDetailActivity.class);
                intent.putExtra("company", data.get(position).getCompany());
                intent.putExtra("content", data.get(position).getContent());
                intent.putExtra("url", data.get(position).getLink());
                intent.putExtra("url_pdf", data.get(position).getUrl_pdf());
                intent.putExtra("title", data.get(position).getTitle());
                mContext.startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void initListener() {

    }
}
