package com.pwj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import com.assionhonty.lib.assninegridview.AssNineGridView;
import com.assionhonty.lib.assninegridview.AssNineGridViewClickAdapter;
import com.assionhonty.lib.assninegridview.ImageInfo;
import com.bumptech.glide.Glide;

import com.pwj.bean.Product;
import com.pwj.helloya.R;
import com.pwj.utils.LoginInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 13688 on 2018/5/13.
 */

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private clickViewIdListener listener;
    private getMapCallBack getMapCallBack;
    private Context mContext;
    private List<Product> data = new ArrayList<>();
    private Map<Integer, String> map = new HashMap<>();
    private int type;
    private static final int ITEM_1 = 0;
    private static final int ITEM_2 = 2;
    private String phone;
    private String user_name;
    private String user_img;
    private String tv1;
    private String tv2;

    @Override
    public void onClick(View view) {
        int clickId = -1;
        int position = (int) view.getTag();
        switch (view.getId()) {
            case R.id.share:
                clickId = R.id.share;//对clickId重新赋值，谁点击了就把谁的id给他。然后传过去
                break;
            case R.id.share_recruit:
                clickId = R.id.share_recruit;//对clickId重新赋值，谁点击了就把谁的id给他。然后传过去
                break;
            case R.id.status_btn1:
                clickId = R.id.status_btn1;//对clickId重新赋值，谁点击了就把谁的id给他。然后传过去
                break;
            case R.id.status_btn3:
                clickId = R.id.status_btn3;//对clickId重新赋值，谁点击了就把谁的id给他。然后传过去
                break;
        }
        if (clickId != -1) {        //如果！=-1,就说明点击按钮
            listener.onClick(position, clickId);
        }
    }

    public interface clickViewIdListener {
        void onClick(int position, int id);     //点击时候当前的postion,+ 控件id
    }
    public interface getMapCallBack {
        void getMap(Map<Integer,String>map);
    }
    public FormAdapter(Context context, List<Product> data, clickViewIdListener clickViewIdListener,getMapCallBack getMapCallBack) {
        this.mContext = context;
        this.data = data;
        listener = clickViewIdListener;
        this.getMapCallBack = getMapCallBack;
        getUser();
    }

    private void getUser() {
        user_name = LoginInfo.getString(mContext, "user_name", "");
        user_img = LoginInfo.getString(mContext, "user_img", "");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_me_form, parent, false);
            return new ViewHolder_require(view);
        }
        if (viewType == ITEM_2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_recruit_form, parent, false);
            return new ViewHolder_require_recruit(view);
        }
        return null;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        type = data.get(position).getNumber();
        if (type != 2) {
            List<ImageInfo> imageInfos = getImageInfos(position);
            ((ViewHolder_require) holder).gridView.setAdapter(new AssNineGridViewClickAdapter(mContext, imageInfos));
            tv2 = data.get(position).getContact() + "," + data.get(position).getPhone() + "," + data.get(position).getRemarks();
            if (type == 0 || type == 5) {
                tv1 = data.get(position).getName() + "," + data.get(position).getLocation() + ",";
                ((ViewHolder_require) holder).tv.setText(tv1 + tv2);
            }
            if (type == 1) {
                tv1 = data.get(position).getLocation() + data.get(position).getLcn_remark() + ",";
                ((ViewHolder_require) holder).tv.setText(tv1 + tv2);
            }
            if (type == 3) {
                tv1 = data.get(position).getLocation() + "," + data.get(position).getDuration() + ",";
                ((ViewHolder_require) holder).tv.setText(tv1 + tv2);
            }
            if (type == 4) {
                tv1 = data.get(position).getName() + "," + data.get(position).getLocation() + "," + data.get(position).getDestination() + ",";
                ((ViewHolder_require) holder).tv.setText(tv1 + tv2);
            }
            map.put(position,tv1+ tv2);
            ((ViewHolder_require) holder).share.setOnClickListener(FormAdapter.this);//为点击分享设置点击事件
            ((ViewHolder_require) holder).status_btn1.setOnClickListener(FormAdapter.this);//为btn1未完成设置点击事件
            ((ViewHolder_require) holder).share.setTag(position);
            ((ViewHolder_require) holder).status_btn1.setTag(position);         //为btn1设置tag，点击的时候把position传过去
            Glide.with(mContext).load(user_img).into(((ViewHolder_require) holder).img);
            ((ViewHolder_require) holder).name_tv.setText(user_name);
            if (data.get(position).getStatus().equals("已完成")) {
                ((ViewHolder_require) holder).status_btn1.setVisibility(View.INVISIBLE);
                ((ViewHolder_require) holder).status_btn2.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder_require) holder).status_btn1.setVisibility(View.VISIBLE);
                ((ViewHolder_require) holder).status_btn2.setVisibility(View.GONE);
            }
        } else {
            String str1 = data.get(position).getContact();
            String str2 = data.get(position).getPhone();
            String str3 = data.get(position).getIssue_date();
            String str4 = data.get(position).getValid_date();
            String str5 = data.get(position).getLocation();
            String str6 = data.get(position).getRemarks();
            ((ViewHolder_require_recruit) holder).contact_tv.setText(str1);
            ((ViewHolder_require_recruit) holder).phone_tv.setText(str2);
            ((ViewHolder_require_recruit) holder).issue_date_tv.setText(str3);
            ((ViewHolder_require_recruit) holder).valid_date_tv.setText(str4);
            ((ViewHolder_require_recruit) holder).location_tv.setText(str5);
            ((ViewHolder_require_recruit) holder).remarks_tv.setText(str6);
            ((ViewHolder_require_recruit) holder).share_recruit.setOnClickListener(FormAdapter.this);//为点击分享设置点击事件
            ((ViewHolder_require_recruit) holder).share_recruit.setTag(position);
            ((ViewHolder_require_recruit) holder).status_btn3.setOnClickListener(FormAdapter.this);//为btn1未完成设置点击事件
            ((ViewHolder_require_recruit) holder).status_btn3.setTag(position);         //为btn1设置tag，点击的时候把position传过去
            if (data.get(position).getStatus().equals("已完成")) {
                ((ViewHolder_require_recruit) holder).status_btn3.setVisibility(View.INVISIBLE);
                ((ViewHolder_require_recruit) holder).status_btn4.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder_require_recruit) holder).status_btn3.setVisibility(View.VISIBLE);
                ((ViewHolder_require_recruit) holder).status_btn4.setVisibility(View.GONE);
            }
            map.put(position,"联系人:"+str1+","+"联系电话:"+str2+","+"日期:"+str3+"至"+str4+","+"位置:"+str5+","+"备注:"+str6);
        }
        getMapCallBack.getMap(map);
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

    class ViewHolder_require extends RecyclerView.ViewHolder {
        private TextView tv;
        private AssNineGridView gridView;
        private Button share;
        private Button status_btn1;
        private Button status_btn2;
        private ImageView img;
        private TextView name_tv;

        ViewHolder_require(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            gridView = itemView.findViewById(R.id.gridView);
            share = itemView.findViewById(R.id.share);
            status_btn1 = itemView.findViewById(R.id.status_btn1);
            status_btn2 = itemView.findViewById(R.id.status_btn2);
            img = itemView.findViewById(R.id.img);
            name_tv = itemView.findViewById(R.id.name_tv);
        }
    }

    class ViewHolder_require_recruit extends RecyclerView.ViewHolder {
        private TextView contact_tv;
        private TextView phone_tv;
        private TextView issue_date_tv;
        private TextView valid_date_tv;
        private TextView location_tv;
        private TextView remarks_tv;
        private Button status_btn3;
        private Button status_btn4;
        private Button share_recruit;
        ViewHolder_require_recruit(View itemView) {
            super(itemView);
            contact_tv = itemView.findViewById(R.id.contact_tv);
            phone_tv = itemView.findViewById(R.id.phone_tv);
            issue_date_tv = itemView.findViewById(R.id.issue_date_tv);
            valid_date_tv = itemView.findViewById(R.id.valid_date_tv);
            location_tv = itemView.findViewById(R.id.location_tv);
            remarks_tv = itemView.findViewById(R.id.remarks_tv);
            status_btn3 = itemView.findViewById(R.id.status_btn3);
            status_btn4 = itemView.findViewById(R.id.status_btn4);
            share_recruit = itemView.findViewById(R.id.share_recruit);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getNumber();   //数据库中表的类型
        return type == 2 ? ITEM_2 : ITEM_1;
        //获取数据类型，如果是2就返回布局2，否则全部返回数据1
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
