package com.pwj.chat;

import android.util.Log;

import com.baidu.mapsdkplatform.comapi.map.C;
import com.pwj.utils.IpConfig;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 13688 on 2019/7/17.
 */

public class Xmpp {
    private static XMPPConnection connection;
    public static Xmpp instance;
    private OfflineMessageManager offlineManager;
    private static FileTransferManager fileManager;

    public static Xmpp getInstance() {
        if (instance == null) {
            instance = new Xmpp();
        }
        return instance;
    }

    //连接服务器
    public static XMPPConnection getConnections() {
        ConnectionConfiguration connConfig = new ConnectionConfiguration(
                IpConfig.IP, 5222);
        /** 是否启用安全验证 */
        connConfig.setReconnectionAllowed(true);
        connConfig.setCompressionEnabled(false);
        connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connConfig.setSASLAuthenticationEnabled(true);
        /** 是否启用调试 */
//         config.setDebuggerEnabled(true);
        /** 创建connection链接 */
        try {
            connection = new XMPPConnection(connConfig);
            /** 建立连接 */
            connection.connect();
            return connection;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return null;

    }
    //一直保持连接
    public static XMPPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    private static void openConnection() {
        try {
            if (null == connection || !connection.isAuthenticated()) {
                XMPPConnection.DEBUG_ENABLED = true;
                //配置文件  参数（服务地地址，端口号，域）
                ConnectionConfiguration conConfig = new ConnectionConfiguration(
                        IpConfig.IP, 5222, IpConfig.IP);
                //设置断网重连 默认为true
                conConfig.setReconnectionAllowed(true);
                //设置登录状态 true-为在线
                conConfig.setSendPresence(true);
                //设置不需要SAS验证
                conConfig.setSASLAuthenticationEnabled(true);
                //开启连接
                connection = new XMPPConnection(conConfig);
                connection.connect();
//                添加额外配置信息
//                 配置各种Provider，如果不配置，则会无法解析数据
                configureConnection(ProviderManager.getInstance());
                configureConnection();
                ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(IpConfig.IP, 5222);
                connectionConfiguration.setReconnectionAllowed(true);
            }
        } catch (XMPPException e) {

        }
    }

    public static XMPPConnection openConnections() {
        try {
            XMPPConnection.DEBUG_ENABLED = true;
            //配置文件  参数（服务地地址，端口号，域）
            ConnectionConfiguration conConfig = new ConnectionConfiguration(
                    IpConfig.IP, 5222, IpConfig.IP);
            //设置断网重连 默认为true
            conConfig.setReconnectionAllowed(true);
            //设置登录状态 true-为在线
            conConfig.setSendPresence(true);
            //设置不需要SAS验证
            conConfig.setSASLAuthenticationEnabled(true);
            conConfig.setCompressionEnabled(false);
            conConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            //开启连接
            connection = new XMPPConnection(conConfig);
            connection.connect();
            //添加额外配置信息
//            configureConnection();
            // 配置各种Provider，如果不配置，则会无法解析数据
            configureConnection(ProviderManager.getInstance());
            return connection;
        } catch (XMPPException e) {

        }
        return null;
    }

    private static void configureConnection(ProviderManager pm) {

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient",
                    "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
    }


    /**
     * ASmack在/META-INF缺少一个smack.providers 文件，配置文件
     * 不然会出现 空指针异常或者是ClassCastExceptions
     */
    private static void configureConnection() {
        ProviderManager pm = ProviderManager.getInstance();
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());
        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
    }

    //注册
    public String regist(XMPPConnection connection, String account, String password) {
        if (connection == null)
            return "0";
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(connection.getServiceName());
        reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
        reg.setPassword(password);
        reg.addAttribute("android", "createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
        PacketFilter filter = new AndFilter(new PacketIDFilter(
                reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = connection
                .createPacketCollector(filter);
        connection.sendPacket(reg);
        IQ result = (IQ) collector.nextResult(SmackConfiguration
                .getPacketReplyTimeout());
        // Stop queuing results
        collector.cancel();// 停止请求results（是否成功的结果）
        if (result == null) {
            return "0";
        } else if (result.getType() == IQ.Type.RESULT) {
            return "1";
        } else { // if (result.getType() == IQ.Type.ERROR)
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                return "2";
            } else {
                return "3";
            }
        }
    }

    //登录
    public static boolean login(XMPPConnection connection, String a, String p) {
        try {
            if (connection == null)
                return false;
            /** 登录 */
            connection.login(a, p);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(Roster roster, String userName, String name) {
        try {
            roster.createEntry(userName, name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加好友 有分组
     *
     * @param roster
     * @param userName
     * @param name
     * @param groupName
     * @return
     */
    public static boolean addUser(Roster roster, String userName, String name,
                                  String groupName) {
        try {
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<RosterEntry> getEntriesByGroup(Roster roster, String groupName) {
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
        RosterGroup rosterGroup = roster.getGroup(groupName);
        try {
            if (rosterGroup.getEntries() != null) {
                Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
                Iterator<RosterEntry> i = rosterEntry.iterator();
                while (i.hasNext()) {
                    Entrieslist.add(i.next());
                }
            } else {

            }
        } catch (Exception e) {

        }
        return Entrieslist;
    }

    public OfflineMessageManager getOffLineMessageManager(XMPPConnection connection) {
        if (offlineManager == null) {
            offlineManager = new OfflineMessageManager(connection);
        }
        return offlineManager;
    }

    public static FileTransferManager getFileTransferManager(XMPPConnection connection) {
        if (fileManager == null) {
            ServiceDiscoveryManager sdManager = ServiceDiscoveryManager
                    .getInstanceFor(connection);
            if (sdManager == null) {
                sdManager = new ServiceDiscoveryManager(connection);
            }
            sdManager.addFeature("http://jabber.org/protocol/disco#info");
            sdManager.addFeature("jabber:iq:privacy");
            FileTransferNegotiator.setServiceEnabled(connection, true);
            fileManager = new FileTransferManager(connection);
        }
        return fileManager;
    }


    public static List<org.jivesoftware.smack.packet.Message> getOffLine(XMPPConnection connection) {
        List<org.jivesoftware.smack.packet.Message> msglist = new ArrayList<org.jivesoftware.smack.packet.Message>();
        // 获取离线消息,线程阻塞 不能Toast
        try {
            Iterator<Message> it = Xmpp.getInstance().getOffLineMessageManager(connection).getMessages();
            while (it.hasNext()) {
                org.jivesoftware.smack.packet.Message message = it.next();
                msglist.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 设置在线
                Presence presence = new Presence(Presence.Type.available);
                connection.sendPacket(presence);
                Xmpp.getInstance().getOffLineMessageManager(connection).deleteMessages();
            } catch (XMPPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return msglist;
    }

}
