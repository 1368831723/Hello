package com.pwj.chat;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pwj.BaseActivity;
import com.pwj.adapter.BaseRcyAdapter;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.classes.CircleImageView;
import com.pwj.helloya.R;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.zhouyou.http.EasyHttp;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class XmppFriendsActivity extends BaseActivity {

    @BindView(R.id.friends_recycle)
    RecyclerView recycler;
    public XMPPConnection connection;
    public List<RosterEntry> friends = new ArrayList<>();
    private String phone = "";
    private String pwd = "";
    private List<User> data = new ArrayList<>();
    private List<User> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_xmpp_friends);
        ButterKnife.bind(this);
        initUser();
    }

    private void initUser() {
        phone = LoginInfo.getString(this, "phone", "");
        EasyHttp.post(IpConfig.URL_SQL)
                .params("query", "SELECT `creationDate` FROM ofuser WHERE username = '" + phone + "'")
                .timeStamp(true)
                .execute(new SuccessCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                        if (s.length() > 25) {
                            initLogin();
                        } else {
                            initRegist();
                        }
                    }
                });
    }

    private void initRegist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Xmpp.getInstance().getConnections() != null) {
                    connection = Xmpp.getInstance().getConnections();

                    Xmpp.getInstance().regist(connection, phone, phone);
//                    if (Xmpp.getInstance().login(connection,"test1", "test1")) {
//                        Log.e("TAG", "run: " + "登录成功");
//                    } else {
//                        Log.e("TAG", "run: " + "登录失败");
//                    }
                } else {

                }

            }
        }).start();
    }

    public List<RosterEntry> getEntriesByGroup(Roster roster, String groupName) {
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();

        RosterGroup rosterGroup = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            Entrieslist.add(i.next());
        }
        return Entrieslist;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            friends = Xmpp.getInstance().getEntriesByGroup(connection.getRoster(), "Friends");
            queryImg();
        }
    };

    private void initLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Xmpp.getInstance().getConnections() != null) {
                    connection = Xmpp.getInstance().getConnections();
                    if (connection != null) {

                    } else {

                    }
                    if (Xmpp.getInstance().login(connection, phone, phone)) {
                        friends =Xmpp.getInstance().getEntriesByGroup(connection.getRoster(), "Friends");
                        if (friends.size() == 0) {

                            handler.sendEmptyMessageDelayed(0, 500);
                        } else {
                           queryImg();
                        }
                    } else {

                    }
                }
            }
        }).start();
    }

    private void queryImg() {

        for (int i = 0; i < friends.size(); i++) {
            String id = friends.get(i).getUser();

            EasyHttp.post(IpConfig.URL_SQL)
                    .params("query", "SELECT url3,phone FROM login WHERE phone = '" + friends.get(i).getName() + "'")
                    .timeStamp(true)
                    .execute(new SuccessCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            datas.clear();
                            datas = GsonUtils.getGsonToList(s, User.class);
                            User user = new User(id, datas.get(0).getPhone(), datas.get(0).getUrl3());
                            data.add(user);
                            if (data.size() == friends.size()) {
                                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(XmppFriendsActivity.this);
                                recycler.setLayoutManager(gridLayoutManager);
                                BaseRcyAdapter adapter = new BaseRcyAdapter(data, R.layout.item_xmpp_user) {
                                    @Override
                                    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                                        CircleImageView imageView = holder.getView(R.id.img);
                                        TextView textView = holder.getView(R.id.name_tv);
                                        Glide.with(XmppFriendsActivity.this).load(data.get(position).getUrl3()).into(imageView);
                                        textView.setText(data.get(position).getPhone());
                                    }
                                };
                                recycler.setAdapter(adapter);
                                adapter.setOnItemClickListener(new BaseRcyAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(XmppFriendsActivity.this, ChatActivity.class);
                                        intent.putExtra("id", data.get(position).getId());
                                        intent.putExtra("other_img", data.get(position).getUrl3());
                                        startActivity(intent);
                                    }
                                });

                            }
                        }
                    });
        }
    }

    @OnClick({R.id.title_im})
    public void onViewClicked(View view) {
        finish();
    }

}
