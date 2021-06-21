package com.pwj.tree;

import android.view.View;
import android.widget.TextView;

import com.pwj.bean.Countys;
import com.pwj.helloya.R;


/**
 * Created by zxy on 17/4/23.
 */

public class ThirdLevelNodeViewBinder extends CheckableNodeViewBinder {
    TextView textView;
    TextView count_tv;
    public ThirdLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.county_tv);
        count_tv = (TextView) itemView.findViewById(R.id.count_tv);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.checkBox;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_county;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        Countys countys= (Countys) treeNode.getValue();
        textView.setText(countys.getCounty());
        count_tv.setText("客户数量:"+countys.getCount());
    }
}
