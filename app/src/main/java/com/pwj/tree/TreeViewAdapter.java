/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under.
 */

package com.pwj.tree;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pwj.activity.CityDetailList;
import com.pwj.bean.Citys;
import com.pwj.bean.Countys;
import com.pwj.bean.Customer;
import com.pwj.bean.Provinces;
import com.pwj.utils.LoginInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xinyuanzhong on 2017/4/21.
 */

public class TreeViewAdapter extends RecyclerView.Adapter {
    private TreeViewListener treeViewListener;
    private SQLiteDatabase db;
    private DataBaseHelper dataBaseHelper;
    private Context context;
    /**
     * Tree root.
     */
    private TreeNode root;
    /**
     * The current data set of Adapter,which means excluding the collapsed nodes.
     */
    private List<TreeNode> expandedNodeList;
    /**
     * The binder factory.A binder provide the layoutId which needed in method
     * <code>onCreateViewHolder</code> and the way how to render ViewHolder.
     */
    private BaseNodeViewFactory baseNodeViewFactory;
    /**
     * This parameter make no sense just for avoiding IllegalArgumentException of ViewHolder's
     * constructor.
     */
    private View EMPTY_PARAMETER;

    private TreeView treeView;

    private List<Customer>data=new ArrayList<>();

    public TreeViewAdapter(Context context, TreeNode root,
                           @NonNull BaseNodeViewFactory baseNodeViewFactory) {
        this.context = context;
        this.root = root;
        this.baseNodeViewFactory = baseNodeViewFactory;

        this.EMPTY_PARAMETER = new View(context);
        this.expandedNodeList = new ArrayList<>();

        buildExpandedNodeList();
    }

    private void buildExpandedNodeList() {
        expandedNodeList.clear();

        for (TreeNode child : root.getChildren()) {
            insertNode(expandedNodeList, child);
        }
    }

    private void insertNode(List<TreeNode> nodeList, TreeNode treeNode) {
        nodeList.add(treeNode);

        if (!treeNode.hasChild()) {
            return;
        }
        if (treeNode.isExpanded()) {
            for (TreeNode child : treeNode.getChildren()) {
                insertNode(nodeList, child);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return expandedNodeList.get(position).getLevel();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int level) {
        View view = LayoutInflater.from(context).inflate(baseNodeViewFactory
                .getNodeViewBinder(EMPTY_PARAMETER, level).getLayoutId(), parent, false);

        BaseNodeViewBinder nodeViewBinder = baseNodeViewFactory.getNodeViewBinder(view, level);
        nodeViewBinder.setTreeView(treeView);
        return nodeViewBinder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final View nodeView = holder.itemView;
        final TreeNode treeNode = expandedNodeList.get(position);
        final BaseNodeViewBinder viewBinder = (BaseNodeViewBinder) holder;
        if (viewBinder.getToggleTriggerViewId() != 0) {
            View triggerToggleView = nodeView.findViewById(viewBinder.getToggleTriggerViewId());

            if (triggerToggleView != null) {
                triggerToggleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        } else if (treeNode.isItemClickEnable()) {
            nodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (treeNode.getChildren().isEmpty()) {
                        int level = treeNode.getLevel();
                        switch (level) {
                            case 0:
                                Provinces provinces = (Provinces) treeNode.getValue();
                                String location = provinces.getProvince();
                                String sql = "SELECT prefecture_level_city,COUNT(*) AS Count from pwj_user WHERE province = " + "'" + location + "'" + " and ";
                                String st = "";
                                String choice = LoginInfo.getString(context, "choice", "");
                                String[] choices = choice.split(",");
                                for (int i = 0; i < choices.length; i++) {
                                    if (i == 0) {
                                        st += "(main_products_using_pwj = \"" + choices[i] + "\"";
                                    } else {
                                        st += " or main_products_using_pwj = \"" + choices[i] + "\"";
                                    }
                                }
                                sql = sql + st;
                                sql += ") GROUP BY prefecture_level_city";

                                try {
                                    dataBaseHelper = new DataBaseHelper(context);
                                    db = dataBaseHelper.openDatabase();
                                    Cursor cursor = db.rawQuery(sql, null);
                                    if (cursor != null && cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {
                                            String city = cursor.getString(0);
                                            String count = cursor.getString(1);
                                            TreeNode treeNode1 = new TreeNode(new Citys(city, count));
                                            treeNode1.setLevel(1);
                                            treeNode.addChild(treeNode1);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw e;
                                }
                                refreshView();
                                onNodeToggled(treeNode);
                                break;
                            case 1:
                                Citys citys = (Citys) treeNode.getValue();
                                String location1 = citys.getCity();
                                String sql1 = "SELECT county_level_city,COUNT(*) AS Count from pwj_user WHERE prefecture_level_city = " + "'" + location1 + "'" + " and ";
                                String st1 = "";
                                String choice1 = LoginInfo.getString(context, "choice", "");
                                String[] choices1 = choice1.split(",");
                                for (int i = 0; i < choices1.length; i++) {
                                    if (i == 0) {
                                        st1 += "(main_products_using_pwj = \"" + choices1[i] + "\"";
                                    } else {
                                        st1 += " or main_products_using_pwj = \"" + choices1[i] + "\"";
                                    }
                                }
                                sql1 = sql1 + st1;
                                sql1 += ") GROUP BY county_level_city";

                                try {
                                    dataBaseHelper = new DataBaseHelper(context);
                                    db = dataBaseHelper.openDatabase();
                                    Cursor cursor = db.rawQuery(sql1, null);
                                    if (cursor != null && cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {
                                            String county = cursor.getString(0);
                                            String count = cursor.getString(1);
                                            TreeNode treeNode2 = new TreeNode(new Countys(county, count));
                                            treeNode2.setLevel(2);
                                            treeNode.addChild(treeNode2);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw e;
                                }
                                refreshView();
                                onNodeToggled(treeNode);
                                break;
                            case 2:
                                data.clear();
                                Countys countys = (Countys) treeNode.getValue();
                                String location2 = countys.getCounty();
                                String sql2 = "SELECT companyname,liaisons,www,address,description,main_products_using_pwj,longitude,latitude,COUNT(*) AS Count from pwj_user WHERE county_level_city = " + "'" + location2 + "'" + " and ";
                                String st2 = "";
                                String choice2 = LoginInfo.getString(context, "choice", "");
                                String[] choices2 = choice2.split(",");
                                for (int i = 0; i < choices2.length; i++) {
                                    if (i == 0) {
                                        st2 += "(main_products_using_pwj = \"" + choices2[i] + "\"";
                                    } else {
                                        st2 += " or main_products_using_pwj = \"" + choices2[i] + "\"";
                                    }
                                }
                                sql2 = sql2 + st2;
                                sql2 += ") GROUP BY companyname";
                                try {
                                    dataBaseHelper = new DataBaseHelper(context);
                                    db = dataBaseHelper.openDatabase();
                                    Cursor cursor = db.rawQuery(sql2, null);
                                    if (cursor != null && cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {
                                            String companyname = cursor.getString(0);
                                            String liaisons = cursor.getString(1);
                                            String www = cursor.getString(2);
                                            String address = cursor.getString(3);
                                            String description = cursor.getString(4);
                                            String main_products_using_pwj = cursor.getString(5);
                                            String longitude=cursor.getString(6);
                                            String latitude=cursor.getString(7);
                                            String count = cursor.getString(8);
                                            Customer customer = new Customer(companyname, liaisons, www, address, description, main_products_using_pwj, count,longitude,latitude);
                                            data.add(customer);
                                        }
                                    }
                                } catch (SQLException e) {
                                    throw e;
                                }
                                Intent intent=new Intent(context, CityDetailList.class);
                                intent.putExtra("type",2);
                                intent.putExtra("resultlist", (Serializable) data);
                                context.startActivity(intent);
                                break;
                        }
                    } else {
                        onNodeToggled(treeNode);
                        viewBinder.onNodeToggled(treeNode, treeNode.isExpanded());
                    }
                }
            });
        }

        if (viewBinder instanceof CheckableNodeViewBinder) {
            final View view = nodeView
                    .findViewById(((CheckableNodeViewBinder) viewBinder).getCheckableViewId());

            if (view != null && view instanceof CheckBox) {
                final CheckBox checkableView = (CheckBox) view;
                checkableView.setChecked(treeNode.isSelected());

                checkableView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = checkableView.isChecked();
                        selectNode(checked, treeNode);
                        ((CheckableNodeViewBinder) viewBinder).onNodeSelectedChanged(treeNode, checked);
                    }
                });
            } else {
                throw new ClassCastException("The getCheckableViewId() " +
                        "must return a CheckBox's id");
            }
        }

        viewBinder.bindView(treeNode);
    }

    public void selectNode(boolean checked, TreeNode treeNode) {
        treeNode.setSelected(checked);

        selectChildren(treeNode, checked);
        selectParentIfNeed(treeNode, checked);
    }

    private void selectChildren(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedChildren = TreeHelper.selectNodeAndChild(treeNode, checked);
        int index = expandedNodeList.indexOf(treeNode);
        if (index != -1 && impactedChildren.size() > 0) {
            notifyItemRangeChanged(index, impactedChildren.size() + 1);
        }
    }

    private void selectParentIfNeed(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedParents = TreeHelper.selectParentIfNeedWhenNodeSelected(treeNode, checked);
        if (impactedParents.size() > 0) {
            for (TreeNode parent : impactedParents) {
                int position = expandedNodeList.indexOf(parent);
                if (position != -1) notifyItemChanged(position);
            }
        }
    }

    private void onNodeToggled(TreeNode treeNode) {
        treeNode.setExpanded(!treeNode.isExpanded());

        if (treeNode.isExpanded()) {
            expandNode(treeNode);
        } else {
            collapseNode(treeNode);
        }
    }

    @Override
    public int getItemCount() {
        return expandedNodeList == null ? 0 : expandedNodeList.size();
    }

    /**
     * Refresh all,this operation is only used for refreshing list when a large of nodes have
     * changed value or structure because it take much calculation.
     */
    public void refreshView() {
        buildExpandedNodeList();
        notifyDataSetChanged();
    }

    /**
     * Insert a node list after index.
     *
     * @param index         the index before new addition nodes's first position
     * @param additionNodes nodes to add
     */
    private void insertNodesAtIndex(int index, List<TreeNode> additionNodes) {
        if (index < 0 || index > expandedNodeList.size() - 1 || additionNodes == null) {
            return;
        }
        expandedNodeList.addAll(index + 1, additionNodes);
        notifyItemRangeInserted(index + 1, additionNodes.size());
    }

    /**
     * Remove a node list after index.
     *
     * @param index        the index before the removedNodes nodes's first position
     * @param removedNodes nodes to remove
     */
    private void removeNodesAtIndex(int index, List<TreeNode> removedNodes) {
        if (index < 0 || index > expandedNodeList.size() - 1 || removedNodes == null) {
            return;
        }
        expandedNodeList.removeAll(removedNodes);
        notifyItemRangeRemoved(index + 1, removedNodes.size());
    }

    /**
     * Expand node. This operation will keep the structure of children(not expand children)
     *
     * @param treeNode
     */
    public void expandNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> additionNodes = TreeHelper.expandNode(treeNode, false);
        int index = expandedNodeList.indexOf(treeNode);

        insertNodesAtIndex(index, additionNodes);
    }


    /**
     * Collapse node. This operation will keep the structure of children(not collapse children)
     *
     * @param treeNode
     */
    public void collapseNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> removedNodes = TreeHelper.collapseNode(treeNode, false);
        int index = expandedNodeList.indexOf(treeNode);

        removeNodesAtIndex(index, removedNodes);
    }

    /**
     * Delete a node from list.This operation will also delete its children.
     *
     * @param node
     */
    public void deleteNode(TreeNode node) {
        if (node == null || node.getParent() == null) {
            return;
        }
        List<TreeNode> allNodes = TreeHelper.getAllNodes(root);
        if (allNodes.indexOf(node) != -1) {
            node.getParent().removeChild(node);
        }

        //remove children form list before delete
        collapseNode(node);

        int index = expandedNodeList.indexOf(node);
        if (index != -1) {
            expandedNodeList.remove(node);
        }
        notifyItemRemoved(index);
    }

    public void setTreeView(TreeView treeView) {
        this.treeView = treeView;
    }
}
