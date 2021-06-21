package com.pwj.chat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import com.ninetripods.aopermission.permissionlib.annotation.NeedPermission;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionCanceled;
import com.ninetripods.aopermission.permissionlib.annotation.PermissionDenied;
import com.ninetripods.aopermission.permissionlib.bean.CancelBean;
import com.ninetripods.aopermission.permissionlib.bean.DenyBean;
import com.pwj.base.BaseActivityPermission;
import com.pwj.bean.Login;
import com.pwj.bean.User;
import com.pwj.callBack.SuccessCallBack;
import com.pwj.helloya.R;
import com.pwj.utils.BitmapUtil;
import com.pwj.utils.GsonUtils;
import com.pwj.utils.IpConfig;
import com.pwj.utils.LoginInfo;
import com.pwj.utils.NotificationUtil;
import com.pwj.utils.SoftHideKeyBoardUtil;
import com.pwj.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.ycbjie.notificationlib.NotificationUtils;
import com.zhouyou.http.EasyHttp;


import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatActivity extends Activity implements ChatManagerListener, MessageListener, TextWatcher, View.OnTouchListener {
    @BindView(R.id.title_linear)
    LinearLayout title_linear;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.recycle)
    RecyclerView recycler;
    @BindView(R.id.relative_pro)
    RelativeLayout relative_pro;
    @BindView(R.id.send_et)
    EditText send_et;
    @BindView(R.id.send_btn)
    Button send_btn;
    @BindView(R.id.plus_iv)
    ImageView plus_iv;
    @BindView(R.id.panel_root)
    LinearLayout mPanelRoot;
    @BindView(R.id.send_img_tv)
    TextView send_img_tv;
    private ChatManager chatManager;
    private Chat chat;
    private XMPPConnection connection;
    private String TAG = "ChatActivity";
    private String phone;
    private String user_img;
    private String other_img;
    private String other_phone;
    private String other_name;
    private List<MsgContent> data = new ArrayList<>();
    private List<MsgContent> datas = new ArrayList<>();
    private List<MsgContent> data3 = new ArrayList<>();
    private MsgAdapter msgAdapter;
    private String id;
    private String msg_send;
    public List<LocalMedia> selectList = new ArrayList<>();
    private final static String Folder = "pwj";
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + Folder + File.separator;
    private String path;
    private boolean flag;
    public List<User> data_user = new ArrayList<>();
    private NotificationUtils notificationUtils;
    private String phone_id;
    private Thread thread;
    private List<MsgContent> data_2;
    private List<MsgContent> data_3;
    private MsgContent msgContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
        getPermissions();

    }
    /**
     * 申请多个权限
     */
    @NeedPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    public void getPermissions() {
        Log.e("getPermissions: ", "申请权限");
        initData();
        initChatManager();
    }

    /**
     * 权限被拒绝
     */
    @PermissionDenied
    public void dealPermission(DenyBean bean) {
        if (bean == null) return;
        Intent intent = new Intent(ChatActivity.this,BaseActivityPermission.class);
        startActivity(intent);
    }

    @PermissionCanceled
    public void dealCancelPermission(CancelBean bean) {
        Util.showToast(ChatActivity.this, "禁止权限会影响到app的正常使用");
        ChatActivity.this.finish();
    }

    private void initView() {
        notificationUtils = new NotificationUtils(this);
        recycler.setOnTouchListener(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        ImmersionBar.with(this)
                .titleBarMarginTop(title_linear)  //可以为任意view
                .statusBarColor(R.color.title_relative)  //指定状态栏颜色,根据情况是否设置
                .statusBarDarkFont(true)
                .init();
        phone = LoginInfo.getString(this, "phone", "");
        user_img = LoginInfo.getString(this, "user_img", "");
        send_et.addTextChangedListener(this);
        send_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Log.e(TAG, "onFocusChange:1 ");
                    mPanelRoot.setVisibility(View.GONE);
                }
                if(msgAdapter!=null){
                    Log.e(TAG, "onFocusChange:2 ");
                    if (msgAdapter.getItemCount() > 1) {
                        recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                    }
                }
            }
        });
    }

    private void initData() {
        flag = false;
        if (flag) {
        } else {
        }
        other_img = getIntent().getStringExtra("other_img");
        other_phone = getIntent().getStringExtra("other_phone");
        other_name = getIntent().getStringExtra("other_name");
        id = other_phone + IpConfig.URL_CHAT;
        title_tv.setText(other_name);
        LoginInfo.setString(this, "other_phone", other_phone);
        LoginInfo.setString(this, "other_name", other_name);
        LoginInfo.setString(this, "other_img", other_img);
        int count_off = LoginInfo.getInt(this, "count_off", 0);
        int counts = LoginInfo.getInt(this, "counts", 0);
        if (counts>=count_off){
            LoginInfo.setInt(this, "counts", counts-count_off);
        }else {
            LoginInfo.setInt(this, "counts", 0);
        }
        LoginInfo.setInt(this, "count_off", 0);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (data == null) {
                        data = new ArrayList<>();
                        Log.e(TAG, "handleMessage: 1");
                    } else {
                        Log.e(TAG, "handleMessage: 2");
                        data.clear();
                        data.addAll(LitePal.where("my_phone = ? and other_phone = ?", phone, other_phone).find(MsgContent.class));
                    }
                    if (msgAdapter == null) {
                        Log.e(TAG, "handleMessage: 3");
                    } else {
                        Log.e(TAG, "handleMessage: 4");
                        if (msgAdapter.getItemCount() > 1) {
                            recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                        }
                        msgAdapter.notifyDataSetChanged();
                    }
                    phone = LoginInfo.getString(ChatActivity.this, "phone", "");
                    data.clear();
                    data = (LitePal.where("my_phone = ? and other_phone = ?", phone, other_phone).find(MsgContent.class));
                    Log.e(TAG, "handleMessage: 5 ");
                    Log.e(TAG, "handleMessage: 6长度" + LitePal.where("other_phone = ?", other_phone).find(MsgContent.class).size());
                    String sql = "select content,date,other_phone,url3 from msgcontent where my_phone = '" + phone + "' and other_phone = '" + other_phone + "'";
                    Cursor cursor = LitePal.findBySQL("select content,date,other_phone,url3 from msgcontent where my_phone = '" + phone + "' and other_phone = '" + other_phone + "'");
                    while (cursor.moveToNext()) {
                        String content = cursor.getString(0);
                        Log.e(TAG, "handleMessage: 内容是" + content);
                        Log.e(TAG, "sql: 语句是" + sql);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                    linearLayoutManager.setStackFromEnd(true);
                    recycler.setLayoutManager(linearLayoutManager);
                    msgAdapter = new MsgAdapter(ChatActivity.this, data);
                    recycler.setAdapter(msgAdapter);
                    if (msgAdapter.getItemCount() > 1) {
                        recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                    }
                    msgAdapter.notifyDataSetChanged();
                    addFileListerer();
                    break;
                case 1:
                    phone = LoginInfo.getString(ChatActivity.this, "phone", "");
                    other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
                    other_name = LoginInfo.getString(ChatActivity.this, "other_name", "");
                    other_img = LoginInfo.getString(ChatActivity.this, "other_img", "");
                    String dateStr2 = DateTimeUtils.formatDate(new Date());
                    String msg_receive = (String) msg.obj;
                    msgContent = null;
                    if (flag) {
                        int counts = LoginInfo.getInt(ChatActivity.this, "counts", 0);
                        msgContent = new MsgContent(2, phone, other_phone, other_name, dateStr2, other_img, msg_receive, 2);
                        LoginInfo.setInt(ChatActivity.this, "counts", counts + 1);
                        Log.e("CHAT", "1");
                    } else {
                        Log.e("CHAT", "2");
                        msgContent = new MsgContent(1, phone, other_phone, other_name, dateStr2, other_img, msg_receive, 2);
                    }
                    data.add(msgContent);
                    datas.clear();
                    datas.add(msgContent);
                    if (msgAdapter != null) {
                        msgAdapter.notifyDataSetChanged();
                    }
                    recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                    LitePal.saveAll(datas);
                    if (flag) {
                        Log.e(TAG, "onSuccess:消息2 ");
                        int counts = LoginInfo.getInt(ChatActivity.this,"counts",0);
                        sendNotification(other_phone, other_name, other_img, msg_receive);
                        EventBus.getDefault().post(new EventBusInt(counts));
                    }

                    break;
                case 2:
                    plus_iv.setVisibility(View.VISIBLE);
                    send_btn.setVisibility(View.GONE);
                    send_et.setText("");
                    msgAdapter.notifyDataSetChanged();
                    recycler.scrollToPosition(msgAdapter.getItemCount() - 1);

                    LitePal.saveAll(datas);
                    break;
                case 3:
                    plus_iv.setVisibility(View.VISIBLE);
                    send_btn.setVisibility(View.GONE);
                    break;
                case 4:
                    phone = LoginInfo.getString(ChatActivity.this, "phone", "");
                    other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
                    other_name = LoginInfo.getString(ChatActivity.this, "other_name", "");
                    String dateStr1 = DateTimeUtils.formatDate(new Date());
                    MsgContent msgContent1 = new MsgContent(phone, other_phone, other_name, dateStr1, user_img, "", 1, path);
                    data.add(msgContent1);
                    datas.clear();
                    datas.add(msgContent1);
                    msgAdapter.notifyDataSetChanged();
                    recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                    relative_pro.setVisibility(View.GONE);
                    LitePal.saveAll(datas);
                    break;
                case 5:
                    phone = LoginInfo.getString(ChatActivity.this, "phone", "");
                    other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
                    other_name = LoginInfo.getString(ChatActivity.this, "other_name", "");
                    other_img = LoginInfo.getString(ChatActivity.this, "other_img", "");
                    String dateStr3 = DateTimeUtils.formatDate(new Date());
                    String other_path = (String) msg.obj;
                    MsgContent msgContent2 = new MsgContent(phone, other_phone, other_name, dateStr3, other_img, "", 2, ALBUM_PATH + other_path);
                    data.add(msgContent2);
                    datas.clear();
                    datas.add(msgContent2);
                    msgAdapter.notifyDataSetChanged();
                    recycler.scrollToPosition(msgAdapter.getItemCount() - 1);
                    LitePal.saveAll(datas);

                    break;
                case 6:
                    //再次打开页面
//                    data.clear();
//                    data.addAll(LitePal.where("other_phone = ?", other_phone).find(MsgContent.class));

                    if (msgAdapter == null) {

                    } else {

                    }
                    break;
                case 7:
                    if (data_2 != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int counts = LoginInfo.getInt(ChatActivity.this, "counts", 0);
                                if (counts > data_2.size()) {
                                    LoginInfo.setInt(ChatActivity.this, "counts", counts - data_2.size());
                                } else {
                                    LoginInfo.setInt(ChatActivity.this, "counts", 0);
                                }
                                ContentValues values = new ContentValues();
                                values.put("status", 1);
                                LitePal.updateAll(MsgContent.class, values, "my_phone = ? and other_phone = ?", phone, other_phone);
                                Log.e(TAG, "CHAT: " + data_2.size() + "--" + counts);
                            }
                        }).start();
                    } else {
                        Log.e(TAG, "CHAT: " + "无");
                    }
                    break;
                case 8:
                    if (data_3 != null) {
                        if (data_3.size() <= 0) {
                            LoginInfo.setInt(ChatActivity.this, "counts", 0);
                        }
                    } else {
                        LoginInfo.setInt(ChatActivity.this, "counts", 0);
                    }
                    break;
            }
        }
    };

    private void initChatManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection = Xmpp.getConnection();
                if (connection.getUser() != null) {
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    chatManager = connection.getChatManager();
                    chat = chatManager.createChat(id, null);
                    data_2 = (LitePal.where("my_phone = ? and other_phone = ? and status = '2'", phone, other_phone).find(MsgContent.class));
                    data_3 = (LitePal.where("my_phone = ? and status = '2'", phone).find(MsgContent.class));
                    handler.sendEmptyMessageDelayed(0, 0);
                    handler.sendEmptyMessageDelayed(7, 800);
                    handler.sendEmptyMessageDelayed(8, 800);
                } else {
                    Xmpp.login(connection, phone, phone);
                    chatManager = connection.getChatManager();
                    chatManager.addChatListener(ChatActivity.this);
                    chat = chatManager.createChat(id, null);
                    data_2 = (LitePal.where("my_phone = ? and other_phone = ? and status = '2'", phone, other_phone).find(MsgContent.class));
                    data_3 = (LitePal.where("my_phone = ? and status = '2'", phone).find(MsgContent.class));
                    handler.sendEmptyMessageDelayed(0, 0);
                    handler.sendEmptyMessageDelayed(7, 800);
                    handler.sendEmptyMessageDelayed(8, 800);
                }
            }
        }).start();
    }

    private void initChat() {


    }

    @Override
    public void chatCreated(Chat chat, boolean b) {
        chat.addMessageListener(ChatActivity.this);

    }

    @Override
    public void processMessage(Chat chat, Message message) {
        phone = LoginInfo.getString(ChatActivity.this, "phone", "");
        phone_id = message.getFrom().substring(0, 11);
        other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
        if (message.getType().equals(Message.Type.chat) || message.getType().equals(org.jivesoftware.smack.packet.Message.Type.normal)) {
            if (message.getBody() != null) {
                if (phone_id.equals(other_phone)) {
                    android.os.Message msg = android.os.Message.obtain();
                    msg.what = 1;
                    msg.obj = message.getBody();
                    handler.sendMessage(msg);
                    Log.e("CHAT", "1");
                } else {
                    String dateStr = DateTimeUtils.formatDate(new Date());
                    EasyHttp.post(IpConfig.URL_SQL)
                            .params("query", "SELECT url3,user_name FROM login where phone = '" + phone_id + "'")
                            .timeStamp(true)
                            .execute(new SuccessCallBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    data_user.clear();
                                    data_user = GsonUtils.getGsonToList(s, User.class);
                                    int counts = LoginInfo.getInt(ChatActivity.this, "counts", 0);
                                    LoginInfo.setInt(ChatActivity.this, "counts", counts + 1);
                                    MsgContent msgContent = new MsgContent(2, phone, phone_id, data_user.get(0).getUser_name(), dateStr, data_user.get(0).getUrl3(), message.getBody(), 2);
                                    datas.clear();
                                    datas.add(msgContent);
                                    LitePal.saveAll(datas);
                                    sendNotification(phone_id, data_user.get(0).getUser_name(), data_user.get(0).getUrl3(), message.getBody());
                                    EventBus.getDefault().post(new EventBusInt(counts+ 1));
                                    Log.e("CHAT", "3");
                                }
                            });
                }
            }
        }
    }

    private void sendNotification(String id, String other_name, String img, String msg) {
//        long[] vibrate = new long[]{0, 500, 1000, 1500};
//        Notification.Builder builder = new Notification.Builder(this);
//        Notification notify = builder.build();
//        //调用系统默认震动
//        notify.defaults = Notification.DEFAULT_VIBRATE;
//        //调用自己设置的震动
//        notify.vibrate = vibrate;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);           //添加为栈顶Activity
        intent.putExtra("other_phone", id);
        intent.putExtra("other_name", other_name);
        intent.putExtra("other_img", img);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationUtils
                .setContentIntent(resultPendingIntent)
                //设置sound
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                //设置优先级
                .setPriority(Notification.PRIORITY_MAX)
                //自定义震动效果
//                .setVibrate(vibrate)
                .sendNotification(1, other_name, msg, R.drawable.logo);
    }

    @OnClick({R.id.title_im, R.id.recycle, R.id.send_btn, R.id.plus_iv, R.id.send_img_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_im:
                flag = true;
                Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.recycle:

                break;
            case R.id.send_btn:
                phone = LoginInfo.getString(ChatActivity.this, "phone", "");
                msg_send = send_et.getText().toString();
                if (!msg_send.equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (chat != null && (connection != null)) {
                                try {
                                    //发送消息，参数为发送的消息内容
                                    other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
                                    other_name = LoginInfo.getString(ChatActivity.this, "other_name", "");
                                    chat.sendMessage(msg_send);
                                    String dateStr1 = DateTimeUtils.formatDate(new Date());
                                    MsgContent msgContent = new MsgContent(1, phone, other_phone, other_name, dateStr1, user_img, msg_send, 1);
                                    data.add(msgContent);
                                    datas.clear();
                                    datas.add(msgContent);
                                    handler.sendEmptyMessageDelayed(2, 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "run: 是空的");
                                if (connection == null) {
                                    connection = Xmpp.getConnection();
                                }
                                if (connection.getUser() != null) {
                                    Presence presence = new Presence(Presence.Type.available);
                                    connection.sendPacket(presence);
                                    chatManager = connection.getChatManager();
                                    chat = chatManager.createChat(id, null);
                                    handler.sendEmptyMessageDelayed(0, 0);
                                } else {
                                    Xmpp.login(connection, phone, phone);
                                    chatManager = connection.getChatManager();
                                    chatManager.addChatListener(ChatActivity.this);
                                    chat = chatManager.createChat(id, null);
                                    handler.sendEmptyMessageDelayed(0, 0);

                                }
                            }
                        }
                    }).start();
                }
                break;
            case R.id.plus_iv:
                if (mPanelRoot.getVisibility() == View.VISIBLE) {
                    showKeyboard();
                } else {
                    hideKeyboard();
                    mPanelRoot.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.send_img_tv:
                PictureSelector.create(ChatActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            images = PictureSelector.obtainMultipleResult(data);
            selectList.clear();
            selectList.addAll(images);
            path = selectList.get(0).getPath();
            path = BitmapUtil.compressImage(path);
//            sendFile(path);
            if (connection != null && connection.isConnected()) {
                new SendFileTask().execute("", "");
            } else {

            }
//            new SendFileTask().execute("", "");
            relative_pro.setVisibility(View.VISIBLE);
        }
    }

    class SendFileTask extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... params) {

            FileTransferManager fileTransferManager = Xmpp.getFileTransferManager(connection);
            File filetosend = new File(path);
            if (filetosend.exists() == false) {
                return -1;
            }
            OutgoingFileTransfer transfer = fileTransferManager
                    .createOutgoingFileTransfer(id + "/Smack");// 创建一个输出文件传输对象
            try {
                transfer.sendFile(filetosend, "recv img");
                while (!transfer.isDone()) {
                    if (transfer.getStatus().equals(FileTransfer.Status.error)) {

                    } else {

                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (transfer.isDone()) {
                    handler.sendEmptyMessageDelayed(4, 0);
                }
            } catch (XMPPException e1) {
                e1.printStackTrace();
            }
            return 0;
        }
    }

    public void sendFile(String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建文件传输管理器
//                FileTransferManager manager = new FileTransferManager(connection);
                FileTransferManager manager = Xmpp.getFileTransferManager(connection);
                FileTransferNegotiator.setServiceEnabled(connection, true);
                // 创建输出的文件传输
                OutgoingFileTransfer transfer = manager
                        .createOutgoingFileTransfer(id + "/Smack");
                // 发送文件
                try {
                    transfer.sendFile(new File(filePath), "You won't believe this!");

                    while (!transfer.isDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (transfer.isDone()) {
                        handler.sendEmptyMessageDelayed(4, 0);
                    }
                } catch (XMPPException e) {

                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showKeyboard() {
        send_et.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) send_et.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(send_et, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        send_et.clearFocus();
        imm.hideSoftInputFromWindow(send_et.getWindowToken(), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() > 0) {
            plus_iv.setVisibility(View.GONE);
            send_btn.setVisibility(View.VISIBLE);
        } else {
            handler.sendEmptyMessageDelayed(3, 0);
        }
    }

    private void addFileListerer() {
        FileTransferManager manager = Xmpp.getFileTransferManager(connection);
        manager.addFileTransferListener(new FileTransferListener() {
            @Override
            public void fileTransferRequest(final FileTransferRequest request) {
                new Thread() {
                    @Override
                    public void run() {
                        //文件接收
                        IncomingFileTransfer transfer = request.accept();
                        //获取文件名字
                        String fileName = transfer.getFileName();
                        //本地创建文件
                        File sdCardDir = new File(ALBUM_PATH);
                        if (!sdCardDir.exists()) {//判断文件夹目录是否存在
                            sdCardDir.mkdir();//如果不存在则创建
                        }
                        String save_path = ALBUM_PATH + fileName;
                        File file = new File(save_path);
                        //接收文件
                        try {
                            transfer.recieveFile(file);
                            while (!transfer.isDone()) {
                                if (transfer.getStatus().equals(FileTransfer.Status.error)) {

                                } else {
                                    System.out.println(transfer.getStatus());
                                    System.out.println(transfer.getProgress());
                                }
                                try {
                                    Thread.sleep(1000L);
                                } catch (Exception e) {
                                }
                            }
                            if (transfer.isDone()) {
                                other_phone = LoginInfo.getString(ChatActivity.this, "other_phone", "");
                                String phone_id = transfer.getPeer().substring(0, 11);
                                if (phone_id.equals(other_phone)) {
                                    android.os.Message msg = handler.obtainMessage();
                                    msg.what = 5;
                                    msg.obj = fileName;
                                    msg.sendToTarget();
                                    if (flag) {
                                        sendNotification(phone_id, data_user.get(0).getUser_name(), data_user.get(0).getUrl3(), "[图片]");
                                    }
                                } else {
                                    String dateStr = DateTimeUtils.formatDate(new Date());
                                    EasyHttp.post(IpConfig.URL_SQL)
                                            .params("query", "SELECT url3,user_name FROM login where phone = '" + phone_id + "'")
                                            .timeStamp(true)
                                            .execute(new SuccessCallBack<String>() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    data_user.clear();
                                                    data_user = GsonUtils.getGsonToList(s, User.class);
                                                    MsgContent msgContent = new MsgContent(phone, phone_id, data_user.get(0).getUser_name(), dateStr, data_user.get(0).getUrl3(), "", 2, ALBUM_PATH + fileName);
                                                    data.add(msgContent);
                                                    datas.clear();
                                                    datas.add(msgContent);
                                                    LitePal.saveAll(datas);
                                                    sendNotification(phone_id, data_user.get(0).getUser_name(), data_user.get(0).getUrl3(), "[图片]");
                                                }
                                            });

                                }

                            }
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }

                }.start();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int count_off = LoginInfo.getInt(this, "count_off", 0);
        int counts = LoginInfo.getInt(this, "counts", 0);
        if (counts>=count_off){
            LoginInfo.setInt(this, "counts", counts-count_off);
        }else {
            LoginInfo.setInt(this, "counts", 0);
        }
        LoginInfo.setInt(this, "count_off", 0);
        flag = false;
        if (flag) {

        } else {

        }
        other_img = intent.getStringExtra("other_img");
        other_phone = intent.getStringExtra("other_phone");
        other_name = intent.getStringExtra("other_name");
        id = other_phone + IpConfig.URL_CHAT;
        title_tv.setText(other_name);
        LoginInfo.setString(this, "other_phone", other_phone);
        LoginInfo.setString(this, "other_img", other_img);
        Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        chatManager = connection.getChatManager();
        chat = chatManager.createChat(id, null);
        data_2 = (LitePal.where("my_phone = ? and other_phone = ? and status = '2'", phone, other_phone).find(MsgContent.class));
        handler.sendEmptyMessageDelayed(0, 0);
        handler.sendEmptyMessageDelayed(7, 800);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            flag = true;
            Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideKeyboard();
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                mPanelRoot.setVisibility(View.GONE);
            }
        }
        return false;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }
}
