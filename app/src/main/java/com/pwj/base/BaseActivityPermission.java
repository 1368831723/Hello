package com.pwj.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.ninetripods.aopermission.permissionlib.util.SettingUtil;
import com.pwj.helloya.R;
import com.pwj.utils.Util;

import java.util.List;

/**
 * Created by 13688 on 2019/3/7.
 */

public class BaseActivityPermission extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        seller();
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.CAMERA ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION}, requestCode = 10)
    public void seller() {

    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
//        Toast.makeText(this, "requestCode:" + bean.getRequestCode()
//                + ",Permissions: " + Arrays.toString(bean.getDenyList().toArray()), Toast.LENGTH_SHORT).show();
        List<String> denyList = bean.getDenyList();
        switch (bean.getRequestCode()) {
            case 10:
                //多个权限申请返回结果
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < denyList.size(); i++) {
                    if (Manifest.permission.CAMERA.equals(denyList.get(i))) {
                        builder.append("相机");
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(denyList.get(i))) {
                        builder.append("存储");
                    }else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(denyList.get(i))) {
                        builder.append("存储");
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(denyList.get(i))) {
                        builder.append("位置");
                    }
                }
                builder.append("权限被禁止，需要手动打开");
                new AlertDialog.Builder(BaseActivityPermission.this)
                        .setTitle("提示")
                        .setMessage(builder)
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SettingUtil.go2Setting(BaseActivityPermission.this);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .create().show();
                break;
        }
    }
    /**
     * 权限被取消
     */
    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(this,"禁止权限会影响到app的正常使用");
    }

}
