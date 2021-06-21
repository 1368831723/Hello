package com.pwj.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;


import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;


import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.activity.SearchActivity;
import com.pwj.base.BaseActivityPermission;

import com.pwj.bean.BiddingCurrent;
import com.pwj.bean.BiddingHistory;
import com.pwj.bean.BiddingSituation;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.interfaces.BiddingHistoryCallback;
import com.pwj.interfaces.QueryRunnable;
import com.pwj.helloya.R;
import com.pwj.interfaces.BiddingCurrentCallback;
import com.pwj.interfaces.StringCallbackOne;
import com.pwj.interfaces.UpdateCallback;
import com.pwj.pages.BiddingCurrentView;
import com.pwj.pages.BiddingHistoryView;
import com.pwj.pages.BiddingSituationView;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.SearchUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.zhouyou.http.EasyHttp;


import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by leon on 3/7/18.
 */

public class FragmentBids extends BaseFragment implements View.OnClickListener {
    private Activity mActivity;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.img_search)
    ImageView img_search;
    @BindView(R.id.img_issue)
    ImageView img_issue;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    private Unbinder unbinder;
    private TableRow tab_1;
    private TableRow tab_2;
    private TableRow tab_3;
    private TextView bid_tv1;
    private TextView bid_tv2;
    private TextView bid_tv3;
    private List<BiddingCurrent> data_current = new ArrayList<>();
    private List<BiddingCurrent> data_currents = new ArrayList<>();
    private List<BiddingSituation> data_situation = new ArrayList<>();
    private List<BiddingSituation> data_situations = new ArrayList<>();
    private List<BiddingHistory> data_history = new ArrayList<>();
    private List<BiddingHistory> data_historys = new ArrayList<>();
    private ViewPager mViewPager;
    private TabLayout mTitle;
    private View[] pages = new View[3];
    private int a = 0;
    private int history;
    private SimpleDateFormat dateFormat;
    private Date toDay;
    private boolean flag = false;
    private boolean status = true;
    private String location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bids, container, false);
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (status) {
            status = false;
            Log.e("Fragment", "FragmentBids第一次可见");
        } else {
            Log.e("Fragment", "FragmentBids第二次可见");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        status = true;
    }

    private void initView(View view) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        img_issue.setVisibility(View.GONE);
        toDay = new Date(System.currentTimeMillis());
        a = a + 1;
        mViewPager = view.findViewById(R.id.vp_content);
        mTitle = view.findViewById(R.id.tl_title);
        img_search.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        getPermissions();
//        EasyHttp.post(IpConfig.URL_SQL)
//                .params("query","SELECT times,valid,title,link,content,company FROM bid_pwj order by times desc")
//                .timeStamp(true)
//                .execute(new SuccessCallBack<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        data.clear();
//                        data_current.clear();
//                        data_history.clear();
//                        progressbar.setVisibility(View.GONE);
//                        data = GsonUtils.getGsonToList(s,Bidding.class);
//                        for (int i = 0; i < data.size(); i++) {
//                            String valid = data.get(i).getValid();
//                            Date endTime = null;
//                            try {
//                                endTime = dateFormat.parse(valid);
//                                if (toDay.before(endTime)){
//                                    data_current.add(data.get(i));
//                                    Log.e("data_current：", "长度" +data_current.size());
//                                }else{
//                                    data_history.add(data.get(i));
//                                    Log.e("data_current：", "长度" +data_history.size());
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//        EasyHttp.post(IpConfig.URL_SQL)
//                .params("query","SELECT company,longitude,latitude,content,COUNT(*) AS COUNT FROM bid_pwj GROUP BY company HAVING COUNT>0 ORDER BY COUNT DESC")
//                .timeStamp(true)
//                .execute(new SuccessCallBack<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        data_situation= GsonUtils.getGsonToList(s,Bidding.class);
//                        Log.e("data_situation", "的长度 " + data_situation.size());
//                    }
//                });

        new Thread(new QueryRunnable(new BiddingCurrentCallback() {
            @Override
            public void threadEndLisener(List<BiddingCurrent> list) {
                data_currents.clear();
                data_currents.addAll(list);
                handler.sendEmptyMessageDelayed(0, 4000);
            }
        }, 20)).start();
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT company,longitude,latitude,COUNT(*) AS COUNT FROM bid_pwj GROUP BY company HAVING COUNT>0 ORDER BY COUNT DESC")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                        data_situations.clear();
                        data_situations = GsonUtils.getGsonToList(s, BiddingSituation.class);
                    }
                });
        new Thread(new QueryRunnable(new BiddingHistoryCallback() {
            @Override
            public void threadEndLisener(List<BiddingHistory> list) {
                data_historys.clear();
                data_historys.addAll(list);
                handler.sendEmptyMessageDelayed(0, 4000);
            }
        }, 22)).start();
    }

    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void getPermissions() {
        Log.e("getPermissions: ", "申请权限");
        initSave();
        getLocation();
    }

    private void initSave() {
        flag = true;
        if (LitePal.findAll(BiddingCurrent.class).size() > 0) {
            data_current.clear();
            data_current.addAll(LitePal.findAll(BiddingCurrent.class));
            pages[0] = BiddingCurrentView.getView(mActivity, data_current);
        } else {
        }
        if (LitePal.findAll(BiddingSituation.class).size() > 0) {
            data_situation.clear();
            data_situation.addAll(LitePal.findAll(BiddingSituation.class));
            pages[1] = BiddingSituationView.getView(mActivity, data_situation, new UpdateCallback() {
                @Override
                public void finishUpdateListener(int sum) {
                    history = sum;
                }
            });
        } else {
        }
        if (LitePal.findAll(BiddingHistory.class).size() > 0) {
            data_history.clear();
            data_history.addAll(LitePal.findAll(BiddingHistory.class));
            pages[2] = BiddingHistoryView.getView(mActivity, data_history);
        } else {
        }
        if (data_current.size() > 0 && data_situation.size() > 0 && data_history.size() > 0) {
            initAdapter();
        }
    }

    private void getLocation() {
        String phone = LoginInfo.getString(mActivity, "phone", "");
        if (!"".equals(phone)) {
            location = "";
            SearchUtil.getInstance().initLocation(mActivity, new StringCallbackOne() {
                @Override
                public void stringOne(String str, double longitude, double latitude) {
                    location = str;
                }
            });
            String date_old = LoginInfo.getString(mActivity, "date", "");
            String date_new = SearchUtil.getInstance().getNowDate().substring(0, 10);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!"".equals(location)) {
                        if (!date_new.equals(date_old)) {
                            EasyHttp.post(IpConfig.URL_SQL)
                                    .params("query", "insert into location_gather(`phone`, `location`,`date`)values('" + phone + "','" + location + "','" + date_new + "')")
                                    .timeStamp(true)
                                    .execute(new SuccessCallBack<String>() {
                                    });
                            LoginInfo.setString(mActivity, "date", date_new);
                            LoginInfo.setInt(mActivity, "date_count", 1);
                        } else {
                            int date_count = LoginInfo.getInt(mActivity, "date_count", 1);
                            if (date_count < 2) {
                                EasyHttp.post(IpConfig.URL_SQL)
                                        .params("query", "insert into location_gather(`phone`, `location`,`date`)values('" + phone + "','" + location + "','" + date_new + "')")
                                        .timeStamp(true)
                                        .execute(new SuccessCallBack<String>() {
                                        });
                                LoginInfo.setInt(mActivity, "date_count", date_count + 1);
                            }
                        }
                    }
                }
            }, 2000);
        }
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        startActivity(BaseActivityPermission.class);
    }

    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(mActivity, "禁止权限会影响到app的正常使用");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progressbar != null) {
                progressbar.setVisibility(View.GONE);
            }
            initData();
        }
    };

    private void initData() {
        if (flag) {
            if (data_current.size() > 0 && data_currents.size() > 0) {
                if (data_current.size() != data_currents.size() || data_history.size() != data_historys.size()) {
                    data_current.clear();
                    data_current.addAll(data_currents);
                    BiddingCurrentView.refresh();
                    LitePal.deleteAll(BiddingCurrent.class);
                    LitePal.saveAll(data_current);
                    TabLayout.Tab tab0 = mTitle.getTabAt(0).setText("当前招标(" + String.valueOf(data_current.size()) + "条)");
                    if (tab0 == null) {
                        throw new NullPointerException("tab0");
                    }
                }
            } else if (data_current.size() == 0) {
                Connector.getDatabase();
                data_current.addAll(data_currents);
                pages[0] = BiddingCurrentView.getView(mActivity, data_current);
                LitePal.saveAll(data_current);
            }
            if (data_situation.size() > 0 && data_situations.size() > 0) {
                if (data_situation.size() != (data_situations.size())) {
                    data_situation.clear();
                    data_situation.addAll(data_situations);
                    BiddingSituationView.refresh();
                    LitePal.deleteAll(BiddingSituation.class);
                    LitePal.saveAll(data_situation);
                    TabLayout.Tab tab1 = mTitle.getTabAt(1).setText("招标公司(" + String.valueOf(data_situation.size()) + "个)");
                    if (tab1 == null) {
                        throw new NullPointerException("tab1");
                    }
                }
            } else if (data_situation.size() == 0) {
                Connector.getDatabase();
                data_situation.addAll(data_situations);
                pages[1] = BiddingSituationView.getView(mActivity, data_situation, new UpdateCallback() {
                    @Override
                    public void finishUpdateListener(int sum) {
                        history = sum;
                    }
                });
                LitePal.saveAll(data_situation);
            }
            if (data_history.size() > 0 && data_historys.size() > 0) {
                if (data_history.size() != data_historys.size()) {
                    data_history.clear();
                    data_history.addAll(data_historys);
                    BiddingHistoryView.refresh();
                    LitePal.deleteAll(BiddingHistory.class);
                    LitePal.saveAll(data_history);
                    TabLayout.Tab tab2 = mTitle.getTabAt(2).setText("历史招标(" + String.valueOf(history - data_current.size()) + "条)");
                    if (tab2 == null) {
                        throw new NullPointerException("tab2");
                    }
                }
            } else if (data_history.size() == 0) {
                Connector.getDatabase();
                data_history.addAll(data_historys);
                pages[2] = BiddingHistoryView.getView(mActivity, data_history);
                LitePal.saveAll(data_history);
                initAdapter();
            }
        } else {
            data_current.clear();
            data_current.addAll(data_currents);
            data_situation.clear();
            data_situation.addAll(data_situations);
            data_history.clear();
            data_history.addAll(data_historys);
        }
    }

    private void initAdapter() {
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pages.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pages[position]);
                return pages[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pages[position]);
            }
        });
        mTitle.setupWithViewPager(mViewPager);
        TabLayout.Tab tab0 = mTitle.getTabAt(0).setText("当前招标(" + String.valueOf(data_current.size()) + "条)");
        if (tab0 == null) {
            throw new NullPointerException("tab0");
        }
        TabLayout.Tab tab1 = mTitle.getTabAt(1).setText("招标公司(" + String.valueOf(data_situation.size()) + "个)");
        if (tab1 == null) {
            throw new NullPointerException("tab1");
        }
        TabLayout.Tab tab2 = mTitle.getTabAt(2).setText("历史招标(" + String.valueOf(history - data_current.size()) + "条)");
        if (tab2 == null) {
            throw new NullPointerException("tab2");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryChat();
        MobclickAgent.onPageStart("FragmentBids");
        MobclickAgent.onResume(mActivity); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentBids");
        MobclickAgent.onPause(mActivity); //统计时长
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mActivity, SearchActivity.class);
        startActivity(intent);
    }

}
