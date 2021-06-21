package com.pwj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;

import com.pwj.helloya.R;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogChoosePicture extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private String mStr;
    private View view;
    private TextView tv_photo;
    private TextView tv_video;
    private TextView tv_camera;
    private TextView tv_cancel;


    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(int id);
    }

    public DialogChoosePicture(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogChoosePicture(Context context, String str, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        mContext = context;
        mStr = str;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        tv_photo = view.findViewById(R.id.tv_photo);
        tv_video = view.findViewById(R.id.tv_video);
        tv_camera = view.findViewById(R.id.tv_camera);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_photo.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_choose_picture, null);
        initView(view);
        this.setContentView(view);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.tv_photo:
                drawableID = R.id.tv_photo;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.tv_video:
                drawableID = R.id.tv_video;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.tv_camera:
                drawableID = R.id.tv_camera;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.tv_cancel:
                break;
        }
        if (drawableID != -1) {                     //此时的drawableID的值已经发生改变了为控件的id，方便传过去
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
        dismiss();
    }
}
