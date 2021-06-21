package com.pwj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogChooseLocation extends Dialog implements TextWatcher , OnGetPoiSearchResultListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private View view;
    private RecyclerView recyclerView;
    private EditText editText;
    private List<PoiInfo> data;
    private String city;
    private BaseRcyAdapter adapter;
    private PoiSearch poiSearch;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable et) {
        //关键字
        // 创建PoiSearch实例
        if (et.length()==0||"".equals(et.toString())){

        }else{
            PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
            poiCitySearchOption.keyword(et.toString());
            poiCitySearchOption.city(city);
            poiCitySearchOption.pageCapacity(15);
            // 分页编号
            poiCitySearchOption.pageNum(3);
            poiSearch.searchInCity(poiCitySearchOption);
        }

    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 1) {
            data.clear();
            data.addAll(poiResult.getAllPoi());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(String location);
    }


    public DialogChooseLocation(Context context,String city, int theme, List<PoiInfo> poiInfo, ICustomDialogEventListener listener) {
        super(context, theme);
        this.city=city;
        data = new ArrayList<>();
        mContext = context;
        data = poiInfo;
        mCustomDialogEventListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_choose_location, null);
        initView(view);
        this.setContentView(view);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.search_recycle);
        editText = view.findViewById(R.id.location_et);
        editText.addTextChangedListener(this);
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
        initData();
    }

    private void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BaseRcyAdapter(data, R.layout.item_location_search) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                TextView name = holder.getView(R.id.name_tv);
                TextView address = holder.getView(R.id.address_tv);
                name.setText(data.get(position).name);
                address.setText(data.get(position).address);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              String  location= data.get(position).name;
              mCustomDialogEventListener.customDialogEvent(location);
              dismiss();
            }
        });

    }

}
