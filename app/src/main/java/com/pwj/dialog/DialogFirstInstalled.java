package com.pwj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mob.MobSDK;
import com.mob.PrivacyPolicy;
import com.pwj.helloya.R;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogFirstInstalled extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private int mTag;
    private View view;
    private TextView dia_tv;
    private TextView dia_policy;
    private TextView dia_secret;
    private Button dia_no;
    private Button dia_yes;
    private Activity activity;
    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
         void customDialogEvent(int id);
    }

    public DialogFirstInstalled(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogFirstInstalled(Activity activity, Context context, ICustomDialogEventListener listener, int theme) {
        super(context, theme);
        this.activity=activity;
        mContext = context;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        dia_policy=view.findViewById(R.id.dia_policy);
        dia_secret=view.findViewById(R.id.dia_secret);
        dia_tv=view.findViewById(R.id.dia_tv);
        dia_no=view.findViewById(R.id.dia_no);
        dia_yes=view.findViewById(R.id.dia_yes);
        dia_policy.setOnClickListener(this);
        dia_secret.setOnClickListener(this);
        dia_no.setOnClickListener(this);
        dia_yes.setOnClickListener(this);

//
//// 异步方法
//        MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_TXT, new PrivacyPolicy.OnPolicyListener() {
//            @Override
//            public void onComplete(PrivacyPolicy data) {
//                if (data != null) {
//                    // 富文本内容
//                    String text = data.getContent();
//                    dia_tv.setText(text);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // 请求失败
//            }
//        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_first_installed, null);
        initView(view);
        this.setContentView(view);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.dia_policy:
                drawableID=R.id.dia_policy;
                break;
            case R.id.dia_secret:
                drawableID=R.id.dia_secret;
                break;
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
