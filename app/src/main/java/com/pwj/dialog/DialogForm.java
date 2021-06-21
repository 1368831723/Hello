package com.pwj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pwj.helloya.R;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogForm extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private String mStr;
    private View view;
    private Button dia_no;
    private Button dia_yes;
    private TextView tv;

    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(int id);
    }

    public DialogForm(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogForm(Context context, String str, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        mContext = context;
        mStr = str;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        tv = view.findViewById(R.id.dialog_form_tv);
        dia_no=view.findViewById(R.id.dia_no);
        dia_yes=view.findViewById(R.id.dia_yes);
        dia_no.setOnClickListener(this);
        dia_yes.setOnClickListener(this);
        tv.setText(mStr);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_form, null);
        initView(view);
        this.setContentView(view);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.dia_no:
                drawableID=R.id.dia_no;
                dismiss();
                break;
            case R.id.dia_yes:
                drawableID=R.id.dia_yes;
                dismiss();
                break;
        }
        if (drawableID != -1) {
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
    }
}
