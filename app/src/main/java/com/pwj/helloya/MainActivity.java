package com.pwj.helloya;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.baidu.mapapi.SDKInitializer;
import com.pwj.activity.PushBackgroundActivity;
import com.pwj.chat.EventBusInt;
import com.pwj.classes.NoScrollViewPager;
import com.pwj.dialog.DialogFirstInstalled;
import com.pwj.fragment.FragmentBids;
import com.pwj.umeng.MyPreferences;
import com.pwj.umeng.PushHelper;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends AppCompatActivity {
    private String[] mTitles = new String[]{"购买", "出售", "招标", "客户", "招聘", "我的"};
    private TextView tv;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    private TabLayout tabLayout;
    private DialogFirstInstalled dialogFirstInstalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        handleAgreement();
        PushAgent.getInstance(this).onAppStart();
        initInstalled();
        EventBus.getDefault().register(this);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        NoScrollViewPager viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        tv = (TextView) findViewById(R.id.tv);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this));
        tabLayout.setupWithViewPager(viewPager);
        int page = getIntent().getIntExtra("page",0);
        switch (page){
            case 0:
                setTitle0();
                break;
            case 2:
                setTitle2();
                viewPager.setCurrentItem(2);
                Log.e("当前页--",""+page);
                break;
            case 5:
                setTitle5();
                viewPager.setCurrentItem(5);
                Log.e("当前页--",""+page);
                break;
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int a = tab.getPosition();
                switch (a) {
                    case 0:
                        setTitle0();
                        break;
                    case 1:
                        setTitle1();
                        break;
                    case 2:
                        setTitle2();
                        break;
                    case 3:
                        setTitle3();
                        break;
                    case 4:
                        setTitle4();
                        break;
                    case 5:
                        setTitle5();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void initInstalled() {
        boolean first_installed = LoginInfo.getBoolean(this, "first_installed", false);
        if (!first_installed) {
            dialogFirstInstalled = new DialogFirstInstalled(this, this, new DialogFirstInstalled.ICustomDialogEventListener() {
                @Override
                public void customDialogEvent(int id) {
                    switch (id) {
                        case R.id.dia_policy:
                            Uri uri = Uri.parse("https://paowan.com.cn/secret/privacy.html");
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent1);
                            break;
                        case R.id.dia_secret:
                            Uri uri2 = Uri.parse("https://paowan.com.cn/secret/secret.html");
                            Intent intent2= new Intent(Intent.ACTION_VIEW, uri2);
                            startActivity(intent2);
                            break;
                        case R.id.dia_no:
                            //彻底关闭整个APP
                            int currentVersion = android.os.Build.VERSION.SDK_INT;
                            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startMain);
                                System.exit(0);
                            } else {// android2.1
                                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                am.restartPackage(getPackageName());
                            }
                            dialogFirstInstalled.dismiss();
//                                MobSDK.submitPolicyGrantResult(false, null);
                            break;
                        case R.id.dia_yes:
                            LoginInfo.setBoolean(MainActivity.this, "first_installed", true);
                            dialogFirstInstalled.dismiss();
                            break;
                    }
                }
            }, R.style.dialog_choose);
            dialogFirstInstalled.show();
            full(MainActivity.this, dialogFirstInstalled);
        }
    }

    //设置弹窗宽度
    private void full(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Util.showToast(MainActivity.this, "再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            MobclickAgent.onKillProcess(MainActivity.this);
            //彻底关闭整个APP
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            } else {// android2.1
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                am.restartPackage(getPackageName());
            }
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EventBusInt eventBusInt) {
        int count = eventBusInt.count;
        ShortcutBadger.applyCount(MainActivity.this, count);
        if (count<=0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
        tv.setText(String.valueOf(count));
    }

    private void setTitle0() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home_selected);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me);
    }

    private void setTitle1() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding_selected);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me);
    }

    private void setTitle2() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require_selected);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me);
    }

    private void setTitle3() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom_selected);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me);
    }

    private void setTitle4() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits_selected);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me);
    }

    private void setTitle5() {
        tabLayout.getTabAt(0).setText(mTitles[0]).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setText(mTitles[1]).setIcon(R.drawable.bidding);
        tabLayout.getTabAt(2).setText(mTitles[2]).setIcon(R.drawable.require);
        tabLayout.getTabAt(3).setText(mTitles[3]).setIcon(R.drawable.custom);
        tabLayout.getTabAt(4).setText(mTitles[4]).setIcon(R.drawable.recruits);
        tabLayout.getTabAt(5).setText(mTitles[5]).setIcon(R.drawable.me_selected);
    }


    private boolean checkAgreement() {
        return MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
    }

    private void handleAgreement() {
        MyPreferences.getInstance(this).setAgreePrivacyAgreement(true);
        PushHelper.init(this);
//        if (!checkAgreement()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.agreement_title);
//            builder.setMessage(R.string.agreement_msg);
//            builder.setPositiveButton(R.string.agreement_ok, (dialog, which) -> {
//                dialog.dismiss();
//                //用户点击隐私协议同意按钮后，初始化PushSDK
//                MyPreferences.getInstance(this).setAgreePrivacyAgreement(true);
//                PushHelper.init(this);
//            });
//            builder.setNegativeButton(R.string.agreement_cancel, (dialog, which) -> {
//                dialog.dismiss();
//                finish();
//            });
//
//            builder.create().show();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
