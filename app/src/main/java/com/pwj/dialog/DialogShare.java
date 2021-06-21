package com.pwj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.Share;
import com.pwj.helloya.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogShare extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private String mStr;
    private View view;
    private TextView share_tv;
    private RecyclerView share_recycle;
    private BaseRcyAdapter adapter;
    private List<Share>data=new ArrayList<>();
    private int[] img = {R.drawable.wechat, R.drawable.wechatmoments,R.drawable.qq1,R.drawable.sinaweibo};
    private String[] name = {"微信", "朋友圈", "qq1", "新浪"};

    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        void customDialogEvent(int id);
    }

    public DialogShare(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogShare(Context context, String str, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        mContext = context;
        mStr = str;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        share_tv = view.findViewById(R.id.share_tv);
        share_recycle = view.findViewById(R.id.share_recycle);
        initData();
    }

    private void initData() {
        for(int i = 0;i<img.length;i++){
            Share share=new Share(img[i],name[i]);
            data.add(share);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
        share_recycle.setLayoutManager(linearLayoutManager);
        adapter = new BaseRcyAdapter(data,R.layout.item_share) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                ImageView imageView = holder.getView(R.id.img);
                TextView tv = holder.getView(R.id.tv);
                imageView.setImageResource(data.get(position).getImg());
                tv.setText(data.get(position).getName());
            }
        };
        share_recycle.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_share, null);
        initView(view);
        this.setContentView(view);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.share_tv:
                dismiss();
                break;
//            case R.id.dia_yes:
//                drawableID = R.id.dia_yes;
//                dismiss();
//                break;
        }
        if (drawableID != -1) {
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
    }
}
