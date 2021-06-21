package com.pwj.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.R;
import com.pwj.utils.LoginInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13688 on 2018/5/13.
 */

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<MsgContent> data = new ArrayList<>();
    private int type;
    private static final int ITEM_1 = 0;
    private static final int ITEM_2 = 1;
    private String phone;
    private String user_name;
    private String user_img;


    public MsgAdapter(Context context, List<MsgContent> data) {
        this.mContext = context;
        this.data = data;
        getUser();
    }

    private void getUser() {
        user_name = LoginInfo.getString(mContext, "user_name", "");
        user_img = LoginInfo.getString(mContext, "user_img", "");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_right, parent, false);
            return new ViewHolder_right(view);
        }
        if (viewType == ITEM_2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_left, parent, false);
            return new ViewHolder_left(view);
        }
        return null;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder_right) {
            ((ViewHolder_right) holder).date_tv.setText(data.get(position).getDate());
            Glide.with(mContext).load(data.get(position).getUrl3()).into(((ViewHolder_right) holder).img_user);
            if (data.get(position).getContent().equals("")) {
                ((ViewHolder_right) holder).msg_tv.setVisibility(View.GONE);
                ((ViewHolder_right) holder).img_content.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(data.get(position).getPath()).into(((ViewHolder_right) holder).img_content);
                ((ViewHolder_right) holder).img_content.setOnClickListener(MsgAdapter.this);
                ((ViewHolder_right) holder).img_content.setTag(data.get(position).getPath());
            } else {
                ((ViewHolder_right) holder).msg_tv.setVisibility(View.VISIBLE);
                ((ViewHolder_right) holder).img_content.setVisibility(View.GONE);
                ((ViewHolder_right) holder).msg_tv.setText(data.get(position).getContent());
            }
        } else if (holder instanceof ViewHolder_left) {
            ((ViewHolder_left) holder).date_tv.setText(data.get(position).getDate());
            Glide.with(mContext).load(data.get(position).getUrl3()).into(((ViewHolder_left) holder).img_user);
            if (data.get(position).getContent().equals("")) {
//                Log.e("onBindViewHolder: ", "发送--" + data.get(position).getContent());
                ((ViewHolder_left) holder).msg_tv.setVisibility(View.GONE);
                ((ViewHolder_left) holder).img_content.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(data.get(position).getPath()).into(((ViewHolder_left) holder).img_content);
                ((ViewHolder_left) holder).img_content.setOnClickListener(MsgAdapter.this);
                ((ViewHolder_left) holder).img_content.setTag(data.get(position).getPath());
            } else {
//                Log.e("onBindViewHolder: ", "接收--" + data.get(position).getContent());
                ((ViewHolder_left) holder).msg_tv.setVisibility(View.VISIBLE);
                ((ViewHolder_left) holder).img_content.setVisibility(View.GONE);
                ((ViewHolder_left) holder).msg_tv.setText(data.get(position).getContent());
            }
        }
    }

    @Override
    public void onClick(View view) {
        String path = (String) view.getTag();
        Intent intent = new Intent(mContext, LoadBigPicture.class);
        intent.putExtra("path", path);
        mContext.startActivity(intent);
    }

    class ViewHolder_right extends RecyclerView.ViewHolder {
        private TextView date_tv;
        private CircleImageView img_user;
        private TextView msg_tv;
        private ImageView img_content;

        ViewHolder_right(View itemView) {
            super(itemView);
            date_tv = itemView.findViewById(R.id.date_tv);
            img_user = itemView.findViewById(R.id.img_user);
            msg_tv = itemView.findViewById(R.id.msg_tv);
            img_content = itemView.findViewById(R.id.img_content);
        }
    }

    class ViewHolder_left extends RecyclerView.ViewHolder {
        private TextView date_tv;
        private CircleImageView img_user;
        private TextView msg_tv;
        private ImageView img_content;

        ViewHolder_left(View itemView) {
            super(itemView);
            date_tv = itemView.findViewById(R.id.date_tv);
            img_user = itemView.findViewById(R.id.img_user);
            msg_tv = itemView.findViewById(R.id.msg_tv);
            img_content = itemView.findViewById(R.id.img_content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getType();   //数据库中表的类型
        return type == 2 ? ITEM_2 : ITEM_1;
        //获取数据类型，如果是2就返回布局2，否则全部返回数据1
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
