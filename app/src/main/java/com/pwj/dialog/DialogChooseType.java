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

public class DialogChooseType extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private String mStr;
    private View view;
    private TextView dialog_type1;
    private TextView dialog_type2;
    private TextView dialog_type3;
    private TextView dialog_type4;
    private TextView dialog_type5;
    private TextView dialog_type6;
    private TextView dialog_type7;
    private TextView dialog_type8;
    private TextView dialog_type9;
    private TextView dialog_type10;


    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(int id);
    }

    public DialogChooseType(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogChooseType(Context context, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        mContext = context;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        dialog_type1=view.findViewById(R.id.dialog_type1);
        dialog_type2=view.findViewById(R.id.dialog_type2);
        dialog_type3=view.findViewById(R.id.dialog_type3);
        dialog_type4=view.findViewById(R.id.dialog_type4);
        dialog_type5=view.findViewById(R.id.dialog_type5);
        dialog_type6=view.findViewById(R.id.dialog_type6);
        dialog_type7=view.findViewById(R.id.dialog_type7);
        dialog_type8=view.findViewById(R.id.dialog_type8);
        dialog_type9=view.findViewById(R.id.dialog_type9);
        dialog_type10=view.findViewById(R.id.dialog_type10);
        dialog_type1.setOnClickListener(this);
        dialog_type2.setOnClickListener(this);
        dialog_type3.setOnClickListener(this);
        dialog_type4.setOnClickListener(this);
        dialog_type5.setOnClickListener(this);
        dialog_type6.setOnClickListener(this);
        dialog_type7.setOnClickListener(this);
        dialog_type8.setOnClickListener(this);
        dialog_type9.setOnClickListener(this);
        dialog_type10.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_choose_type, null);
        initView(view);
        this.setContentView(view);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.dialog_type1:
                drawableID=R.id.dialog_type1;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_type2:
                drawableID=R.id.dialog_type2;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_type3:
                drawableID=R.id.dialog_type3;
                break;
            case R.id.dialog_type4:
                drawableID=R.id.dialog_type4;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_type5:
                drawableID=R.id.dialog_type5;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_type6:
                drawableID=R.id.dialog_type6;
                break;
            case R.id.dialog_type7:
                drawableID=R.id.dialog_type7;
                break;
            case R.id.dialog_type8:
                drawableID=R.id.dialog_type8;
                break;
            case R.id.dialog_type9:
                drawableID=R.id.dialog_type9;
                break;
            case R.id.dialog_type10:
                drawableID=R.id.dialog_type10;
                break;
        }
        if (drawableID != -1) {                     //此时的drawableID的值已经发生改变了为控件的id，方便传过去
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
        dismiss();
    }
}
