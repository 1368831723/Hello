package com.pwj.pages;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.pwj.activity.CompanyActivity;
import com.pwj.adapter.BaseRcyAdapter;

import com.pwj.bean.BiddingSituation;
import com.pwj.helloya.R;
import com.pwj.interfaces.UpdateCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han13688 on 2018/4/25.
 */

public class BiddingSituationView extends LinearLayout {
  BaiduMap mBaiduMap;
  MapView mMapView;
  private RecyclerView recyclerView;
  private static BaseRcyAdapter adapter;
  private TextView textView;
  private List<BiddingSituation> data = new ArrayList<>();
  private static Context mContext;
  private static UpdateCallback mUpdateCallback;
  private int sum=0;
  public static BiddingSituationView getView(Context context, List<BiddingSituation> data, UpdateCallback updateCallback) {
    mContext=context;
    mUpdateCallback=updateCallback;
    BiddingSituationView situationView = (BiddingSituationView) View.inflate(context, R.layout.view_bidding_situation, null);
    situationView.initData(data);


    return situationView;
  }
  public static void refresh(){
    adapter.notifyDataSetChanged();
  }
  public BiddingSituationView(Context context) {
    super(context);
  }

  public BiddingSituationView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public BiddingSituationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }


  private void initData(List<BiddingSituation> data) {
    if (getChildCount() == 0) {
      return;
    }
    setLayoutTransition(new LayoutTransition());
    recyclerView = findViewById(R.id.page2_rcy);
    textView = findViewById(R.id.tv);
    mMapView = findViewById(R.id.mapView);
    mBaiduMap = mMapView.getMap();
    addMark(data);
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new BaseRcyAdapter(data, R.layout.item_situation) {
      @Override public void onBindViewHolder(ViewHolder holder, int position) {
        TextView bid_tv1 = holder.getView(R.id.bid_tv1);
        TextView bid_tv2 = holder.getView(R.id.bid_tv2);
        bid_tv1.setText(data.get(position).getCompany());
        bid_tv2.setText((data.get(position).getCOUNT())+"???");
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

  private void addMark(List<BiddingSituation> data) {
    float zoo=  4.777777f;
    for (int i=0 ;i<data.size();i++){
        sum=Integer.parseInt(data.get(i).getCOUNT())+sum;
        if (!TextUtils.isEmpty(data.get(i).getLatitude())){
          //??????Maker?????????
          LatLng point = new LatLng(Double.valueOf(data.get(i).getLatitude()), Double.valueOf(data.get(i).getLongitude()));
          //??????Marker??????
          BitmapDescriptor bitmap = BitmapDescriptorFactory
                  .fromResource(R.drawable.location_pointer_24);
          //??????MarkerOption???????????????????????????Marker
          OverlayOptions option = new MarkerOptions()
                  .position(point)
                  .icon(bitmap);
          //??????????????????Marker????????????
          mBaiduMap.addOverlay(option);
          MapStatus mMapStatus = new MapStatus.Builder()//??????????????????
                  .target(point)
                  .zoom(zoo)       //????????????
                  .build();  //??????MapStatusUpdate??????????????????????????????????????????????????????
          MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
          mBaiduMap.setMapStatus(mMapStatusUpdate);//??????????????????
        }
    }
    mUpdateCallback.finishUpdateListener(sum);
    textView.setText("????????????"+String.valueOf(sum)+"???????????????");
  }

}
