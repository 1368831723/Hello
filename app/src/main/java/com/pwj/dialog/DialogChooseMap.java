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

public class DialogChooseMap extends Dialog implements View.OnClickListener {


    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private View view;
    private boolean baiDu;
    private boolean gaoDe;
    private TextView dialog_no_map;
    private TextView dialog_bai_du;
    private TextView dialog_gao_de;
    private TextView dialog_choose_cancel;

    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(int id);
    }


    public DialogChooseMap(Context context, boolean baiDu, boolean gaoDe, int theme, ICustomDialogEventListener listener) {
        super(context, theme);
        mContext = context;
        this.baiDu=baiDu;
        this.gaoDe=gaoDe;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        dialog_no_map = view.findViewById(R.id.dialog_no_map);
        dialog_bai_du = view.findViewById(R.id.dialog_bai_du);
        dialog_gao_de = view.findViewById(R.id.dialog_gao_de);
        dialog_choose_cancel = view.findViewById(R.id.dialog_choose_cancel);

        if (!baiDu){
            dialog_bai_du.setVisibility(View.GONE);
        }
        if (!gaoDe){
            dialog_gao_de.setVisibility(View.GONE);
        }
        if (!baiDu&&!gaoDe){
            dialog_no_map.setVisibility(View.VISIBLE);
        }
        dialog_bai_du.setOnClickListener(this);
        dialog_gao_de.setOnClickListener(this);
        dialog_choose_cancel.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_choose_map, null);
        initView(view);
        this.setContentView(view);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id) {
            case R.id.dialog_bai_du:
                drawableID = R.id.dialog_bai_du;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_gao_de:
                drawableID = R.id.dialog_gao_de;//此时的drawableID的值已经发生改变了为控件的id，方便传过去
                break;
            case R.id.dialog_choose_cancel:
                break;
        }
        if (drawableID != -1) {                     //此时的drawableID的值已经发生改变了为控件的id，方便传过去
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
        dismiss();
    }
}
