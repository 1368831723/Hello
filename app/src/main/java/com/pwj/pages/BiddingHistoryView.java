package com.pwj.pages;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pwj.activity.CompanyActivity;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.BiddingHistory;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han13688 on 2018/4/25.
 */

public class BiddingHistoryView extends LinearLayout {

  private RecyclerView recyclerView;
  private static BaseRcyAdapter adapter;
  private List<BiddingHistory> data = new ArrayList<>();
  private static Context mContext;
  public static BiddingHistoryView getView(Context context, List<BiddingHistory> data) {
    mContext=context;
    BiddingHistoryView historyView = (BiddingHistoryView) View.inflate(context, R.layout.view_bidding_history, null);
    historyView.initData(data);

    return historyView;
  }

  public BiddingHistoryView(Context context) {
    super(context);
  }

  public BiddingHistoryView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public BiddingHistoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public static void refresh(){
    adapter.notifyDataSetChanged();
  }
  private void initData(List<BiddingHistory> data) {
    if (getChildCount() == 0) {
      return;
    }
    setLayoutTransition(new LayoutTransition());
    recyclerView = findViewById(R.id.page3_rcy);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new BaseRcyAdapter(data, R.layout.item_word) {
      @Override public void onBindViewHolder(ViewHolder holder, int position) {
        TextView bid_tv1 = holder.getView(R.id.bid_tv1);
        TextView bid_tv2 = holder.getView(R.id.bid_tv2);
        TextView bid_tv3 = holder.getView(R.id.bid_tv3);
        bid_tv1.setText(data.get(position).getTimes());
        bid_tv2.setText(data.get(position).getTitle());
        bid_tv3.setText(data.get(position).getLink());
      }
    };
    adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
      @Override public void onItemClick(View view, int position) {
        Intent intent=new Intent(mContext,CompanyActivity.class);
        intent.putExtra("company",data.get(position).getCompany());
        mContext.startActivity(intent);
      }
    });
    recyclerView.setAdapter(adapter);
  }

}
