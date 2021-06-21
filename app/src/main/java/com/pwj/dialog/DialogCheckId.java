package com.pwj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pwj.activity.IssueActivity;
import com.pwj.helloya.R;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogCheckId extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private int mTag;
    private View view;
    private TextView dia_tv;
    private Button dia_no;
    private Button dia_yes;
    private Activity activity;
    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
         void customDialogEvent(int id);
    }

    public DialogCheckId(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogCheckId(Activity activity,Context context, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        this.activity=activity;
        mContext = context;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        dia_tv=view.findViewById(R.id.dia_tv);
        dia_no=view.findViewById(R.id.dia_no);
        dia_yes=view.findViewById(R.id.dia_yes);
        dia_no.setOnClickListener(this);
        dia_yes.setOnClickListener(this);
        String tv = "        "+mContext.getString(R.string.no_authentication);
        dia_tv.setText(tv);
//        if (mTag==0){
//            dia_tv.setText(mContext.getString(R.string.no_register));
//            dia_yes.setText(mContext.getString(R.string.login_in));
//        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_check_id, null);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            dismiss();
            activity.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
