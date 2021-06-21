package com.pwj.tree;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pwj.bean.Provinces;
import com.pwj.helloya.R;

/**
 * Created by zxy on 17/4/23.
 */

public class FirstLevelNodeViewBinder extends CheckableNodeViewBinder {
    TextView textView;
    TextView count_tv;
    ImageView imageView;
    public FirstLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.province_tv);
        count_tv = (TextView) itemView.findViewById(R.id.count_tv);
        imageView = (ImageView) itemView.findViewById(R.id.arrow_img);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.checkBox;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_province;
    }

    @Override
    public void bindView(final TreeNode treeNode) {
        Provinces provinces= (Provinces) treeNode.getValue();
        textView.setText(provinces.getProvince());

        count_tv.setText("客户数量:"+provinces.getCount());
        imageView.setRotation(treeNode.isExpanded() ? 90 : 0);
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        if (expand) {
            imageView.animate().rotation(90).setDuration(200).start();
        } else {
            imageView.animate().rotation(0).setDuration(200).start();
        }
    }
}
