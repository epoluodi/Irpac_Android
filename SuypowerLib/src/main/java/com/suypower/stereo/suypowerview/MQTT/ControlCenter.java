package com.suypower.stereo.suypowerview.MQTT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Chat.ChatMessage;
import com.suypower.stereo.suypowerview.Chat.ChatMsgJson;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Common.StringUtil;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.Notification.NotificationClass;
import com.suypower.stereo.suypowerview.Notification.NotificationSwitch;
import com.suypower.stereo.suypowerview.Protocol.BaseChatProtocol;
import com.suypower.stereo.suypowerview.Protocol.EBNoticesProtocol;
import com.suypower.stereo.suypowerview.Protocol.FamilyProtocol;
import com.suypower.stereo.suypowerview.Protocol.FriendProtocol;
import com.suypower.stereo.suypowerview.Protocol.GroupProtocol;
import com.suypower.stereo.suypowerview.Protocol.SystemBaseProtocol;
import com.suypower.stereo.suypowerview.Protocol.WeAppProtocol;
import com.suypower.stereo.suypowerview.Server.StereoService;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 服务中心控制类
 */
public class ControlCenter extends Binder {

    //服务全局
    private Context context;
    public static ControlCenter controlCenter = null;
    public static final long msgidoffet = 2803227000l;
    private String user = "";
    private String pwd = "";
    public Boolean disturb = false;//通知开关开关
    public Boolean msgdisturb = false;//消息免打扰开关
    private Boolean islogin = false;
    private Boolean isRunAPP = false;
    private String _token;
    public int mode = 0;
    private IMessageControl iMessageControl;//信息回调
    private NotificationClass notificationClass; //全局通知类
    private MQTTClient mqttClient;//mqtt 服务对象
    private Boolean isNotification = false;//是否通知
    private Boolean isDisMqttConnect = false;
    public Boolean IsLogOut = false;//是否住校


    public void setIslogin(Boolean islogin) {
        this.islogin = islogin;
    }

    public void publishNotics(String strtopic) {
        mqttClient.setPublictopic(strtopic);
    }

    public void publishNotics(List<String> strtopics) {
        mqttClient.setGrouptopic(strtopics);
    }

    public Boolean getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(Boolean isNotification) {
        this.isNotification = isNotification;
    }

    public Boolean getIsRunAPP() {
        return isRunAPP;
    }

    public void setIsRunAPP(Boolean isRunAPP) {
        this.isRunAPP = isRunAPP;
    }

    public void setiMessageControl(IMessageControl iMessageControl) {
        this.iMessageControl = iMessageControl;

    }

    public ControlCenter() {

        notificationClass = new NotificationClass(Init.getContext());
        notificationClass.Clear_Notify();
        disturb = LibConfig.getKeyShareVarForBoolean("disturb");
        msgdisturb = LibConfig.getKeyShareVarForBoolean("msgdisturb");
        setIsNotification(true);
    }

    public void Init(Context context) {
        Init.Init(context);


    }

    public boolean init(String token, String userid) {


        user = LibConfig.getKeyShareVarForString("username");
        pwd = LibConfig.getKeyShareVarForString("userpwd");
        disturb = LibConfig.getKeyShareVarForBoolean("disturb");


        Log.i("user", user);
        Log.i("pwd", pwd);


        return true;
    }


    //设置Token
    public void setToken(String token) {
        _token = token;
    }

    public void LoopMsgStart() {

        if (mqttClient == null)
            mqttClient = new MQTTClient(mqttCallBack);
        if (!mqttClient.getConnected())
            mqttClient.connecetServer();

    }


    /**
     * 登陆上一次用户信息
     */
    public void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pwd.equals(""))
                        return;
                    while (true) {
                        islogin = true;
                        //初始插件

                        String token = onlyLogin();


                        if (token.equals("")) {
                            islogin = false;
                            if (isRunAPP)
                                return;
                            Thread.sleep(10000);
                            continue;
                        } else if (token.equals("-1"))
                            return;
                        Message message = handler.obtainMessage();
                        message.obj = token;
                        handler.sendMessage(message);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /**
     * 服务器登录
     *
     * @return
     */
    public String onlyLogin() {
        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制

            AjaxHttp ajaxHttp = new AjaxHttp();
            loginurl = String.format("%1$sgetToken?username=%2$s&password=%3$s&token=%4$s",
                    StereoService.AuthUrl, user, pwd,LibConfig.getKeyShareVarForString("token"));
            Log.e("server loginurl", loginurl);
            ajaxHttp.openRequest(loginurl, AjaxHttp.REQ_METHOD_GET);
            if (!ajaxHttp.sendRequest()) {
                return "";
            }
            byte[] buffer = ajaxHttp.getRespBodyData();
            if (buffer == null) {
                return "";
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    return "-1";
                }
                JSONObject ajax_data = returnData.getReturnData();
                String token = ajax_data.getString("token");

                String userid = ajax_data.getString("userId");
                BaseUserInfo baseUserInfo = new BaseUserInfo();
                BaseUserInfo.setBaseUserInfo(baseUserInfo);
                BaseUserInfo.getBaseUserInfo().setUserId(userid);

                Common.Token = ajax_data.getString("token");
                //初始化mqtt信息
                JSONObject mqtt = ajax_data.getJSONObject("MQTT");
                MQTTConfig.cliectId = Common.DeviceID;
                MQTTConfig.serverAddress = String.format("tcp://%1$s:%2$s",
                        mqtt.getString("mqttip"), mqtt.getString("mqttport"));
                MQTTConfig.userName = mqtt.getString("username");
                MQTTConfig.password = mqtt.getString("password");
                MQTTConfig.SystemNoice = "topic/notice/" + userid;
                MQTTConfig.ChatNoice = "topic/chat/" + userid;
                MQTTConfig.WeAppNoice = "topic/weapp/" + userid;
                LibConfig.setKeyShareVar("username", user);
                LibConfig.setKeyShareVar("userpwd", pwd);

                if (ajax_data.getInt("userType") == 1)
                    BaseUserInfo.getBaseUserInfo().setUserRight(BaseUserInfo.UserRight.MANGER);//工作人员
                else if (ajax_data.getInt("userType") == 2)
                    BaseUserInfo.getBaseUserInfo().setUserRight(BaseUserInfo.UserRight.OLDMAN);//老年人


                return token;

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setToken((String) msg.obj);
            islogin = false;
            //执行登陆后启动轮询服务
            LoopMsgStart();


        }
    };


    MQTTClient.MQTTCallBack mqttCallBack = new MQTTClient.MQTTCallBack() {
        @Override
        public void OnMsgCallBack(byte[] buffer) {
            Log.i("接收到MQTT的数据", new String(buffer));

        }

        @Override
        public void OnMsgCallBack(BaseChatProtocol baseChatProtocol) {
            MessageDB messageDB = new MessageDB();
            Contacts contacts;
            int count;
            try {
                ChatMsgJson chatMsgJson = baseChatProtocol.getChatMsgJson();
                if (chatMsgJson == null)
                    return;


                ChatMessage chatMessage = new ChatMessage();
                if (chatMsgJson.getOpe() == 0)
                    chatMessage.setMessageid(chatMsgJson.getFrom());
                else if (chatMsgJson.getOpe() == 1)
                    chatMessage.setMessageid(chatMsgJson.getTo());
                chatMessage.setMsgid(chatMsgJson.getMsgid());
                chatMessage.setSenderid(chatMsgJson.getFrom());
                chatMessage.setMsgdate(chatMsgJson.getSendtime());
                chatMessage.setCretedt(chatMsgJson.getSendtime());
                chatMessage.setMsgSendState(2);//接收
                chatMessage.setIstop(chatMsgJson.getIstop());
                chatMessage.setDisturb(chatMsgJson.getNoDisturb());
                //消息类型
                if (chatMsgJson.getOpe() == 0) {
                    if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                        contacts = messageDB.getContactsForSamlple(chatMsgJson.getFrom());
                        chatMessage.setMsgMode(100);
                        chatMessage.setNickimg(contacts.getNickimgurl());// 头像
                        //设置标题
                        if (contacts.getNameRemark().equals(""))
                            chatMessage.setSender(contacts.getNickname());
                        else
                            chatMessage.setSender(contacts.getNameRemark());
                    } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                        Cursor cursor = messageDB.getPhoneBookContactsInfo(chatMsgJson.getFrom());
                        if (cursor == null)
                            return;
                        cursor.moveToNext();
                        chatMessage.setMsgMode(100);
                        chatMessage.setNickimg(cursor.getString(2));// 头像
                        //设置标题
                        chatMessage.setSender(cursor.getString(3));
                        cursor.close();
                    }
                }
                if (chatMsgJson.getOpe() == 1) {
                    chatMessage.setMsgMode(101);
                    if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                        contacts = messageDB.getContactsForSamlple(chatMsgJson.getFrom());
                        if (contacts == null) {
                            Cursor cursor = messageDB.getAllGroupMemberInfo(chatMsgJson.getFrom());
                            if (cursor.getCount() != 0) {
                                cursor.moveToNext();
                                chatMessage.setSender(cursor.getString(1));
                                chatMessage.setNickimg(cursor.getString(3));
                                chatMessage.setSenderid(cursor.getString(2));
                                cursor.close();
                            }
                        } else {
                            if (contacts.getNameRemark().equals(""))
                                chatMessage.setSender(messageDB.getGroupNickName(chatMessage.getSenderid(),
                                        chatMessage.getMessageid()));
                            else
                                chatMessage.setSender(contacts.getNameRemark());
                            chatMessage.setNickimg(contacts.getNickimgurl());
                        }
                    } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                        Cursor cursor = messageDB.getPhoneBookContactsInfo(chatMsgJson.getFrom());
                        if (cursor == null)
                            return;
                        cursor.moveToNext();
                        chatMessage.setNickimg(cursor.getString(2));// 头像
                        chatMessage.setSender(messageDB.getGroupNickName(chatMessage.getSenderid(),
                                chatMessage.getMessageid()));
                        chatMessage.setSenderid(cursor.getString(0));
                        cursor.close();
                    }

                }

                // 内容
                String desc = "";
                switch (chatMsgJson.getMessageTypeEnum()) {
                    case TEXT:
                        chatMessage.setMsg(chatMsgJson.getBody().getString("msg"));
                        chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
                        desc = String.format("来自[%1$s]的一条%2$s", chatMessage.getSender(), "文本信息");
                        break;
                    case PICTURE:
                        chatMessage.setMsg("一张图片");
                        chatMessage.setMediaid(chatMsgJson.getBody().getString("media"));
                        chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                        desc = String.format("来自[%1$s]的一条%2$s", chatMessage.getSender(), "图片信息");
                        break;
                    case AUDIO:
                        chatMessage.setMsg("一段语音");
                        chatMessage.setMediaid(chatMsgJson.getBody().getString("media"));
                        chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
                        chatMessage.setMsgSendState(0);
                        desc = String.format("来自[%1$s]的一条%2$s", chatMessage.getSender(), "语音信息");
                        break;
                    case EAUDIO:
                        chatMessage.setMsg("一段语音");
                        chatMessage.setMediaid(chatMsgJson.getBody().getString("media"));
                        chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.EAUDIO);
                        chatMessage.setMsgSendState(0);
                        desc = String.format("来自[%1$s]的一条%2$s", chatMessage.getSender(), "语音信息");
                        break;
                }


                if (messageDB.isExitsMsgidForCount(chatMessage.getMessageid()) == 0) {
                    if (chatMsgJson.getOpe() == 0) {
                        messageDB.insertMessageList(chatMessage);
                    } else if (chatMsgJson.getOpe() == 1) {

                        messageDB.insertMessageList(chatMessage.getMessageid(),
                                messageDB.getGroupAllName(chatMessage.getMessageid())
                                , chatMessage.getMsg().toString(),
                                messageDB.getGroupNickImg(chatMessage.getMessageid()), 101);
                        messageDB.updateMessageList(chatMessage.getMessageid(), 1);
                    }
                } else {
                    count = messageDB.isExitsMsgid(chatMessage.getMessageid());
                    if (chatMsgJson.getOpe() == 0)
                        messageDB.updateMessageList(chatMessage, ++count);
                    else if (chatMsgJson.getOpe() == 1)
                        messageDB.updateMessageList(chatMessage, ++count,
                                messageDB.getGroupAllName(chatMessage.getMessageid()),
                                messageDB.getGroupNickImg(chatMessage.getMessageid()));
                }
                chatMessage.setSelf(false);
                messageDB.insertChatlog(chatMessage);
                messageDB.updateMessageList(chatMessage.getMessageid(), chatMessage.getIstop() ? 1 : 0,
                        chatMessage.getDisturb() ? 1 : 0);
                // 进行提示
                if (baseChatProtocol.getMode() == 1) {

                    if (chatMsgJson.getAtUserList().contains(BaseUserInfo.getBaseUserInfo().getUserId()))
                    {
                        if (isNotification) {
                            NotificationClass notificationClass = new NotificationClass(Init.getContext());
                            notificationClass.add_Notification(desc, chatMessage.getSender(), chatMessage.getMsg().toString(),
                                    NotificationClass.CHATMSG, getpendingIntentForchat(
                                            chatMessage.getMessageid(),
                                            NotificationSwitch.getGetChatViewPendingIntent(),
                                            chatMessage.getMsgMode(), chatMessage.getMsgMode()));
                        }
                    }
                    else{
                        if (!chatMsgJson.getNoDisturb()) {
                            if (isNotification) {
                                NotificationClass notificationClass = new NotificationClass(Init.getContext());
                                notificationClass.add_Notification(desc, chatMessage.getSender(), chatMessage.getMsg().toString(),
                                        NotificationClass.CHATMSG, getpendingIntentForchat(
                                                chatMessage.getMessageid(),
                                                NotificationSwitch.getGetChatViewPendingIntent(),
                                                chatMessage.getMsgMode(), chatMessage.getMsgMode()));
                            }
                        }
                    }
                    if (iMessageControl != null) {
                        Message message = msghandler.obtainMessage();
                        message.obj = chatMessage;
                        msghandler.sendMessage(message);
                    }
                } else {
                    if (iMessageControl != null) {
                        iMessageControl.OnRefresh();
                        Message message = msghandler.obtainMessage();
                        message.obj = chatMessage;
                        msghandler.sendMessage(message);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void OnMsgCallBack(SystemBaseProtocol systemBaseProtocol) {
            try {
                MessageDB messageDB = new MessageDB();
                FriendProtocol addFriendProtocol;
                GroupProtocol groupProtocol;
                EBNoticesProtocol ebNoticesProtocol;
                String msgid;
                String desc = "";
                Intent intent1;
                Contacts contacts;
                String inviteUserId, inviteUserName = "";
                String groupname = "";
                String strmsg = "";
                String opusername = "";
                switch (systemBaseProtocol.getNoticeType()) {
                    case SystemBaseProtocol.ADDFRIEND:
                        addFriendProtocol = (FriendProtocol) systemBaseProtocol.getProtocol();
                        msgid = messageDB.checkMessageAddFriend(addFriendProtocol);
                        if (msgid == null)
                            messageDB.insertMessageList(addFriendProtocol);
                        else
                            messageDB.updateMessageList(addFriendProtocol);
                        Intent intent = new Intent(SystemBaseProtocol.BroadCastFriend);
                        intent.putExtra("noticeId", addFriendProtocol.getSystemBaseProtocol().getNoticeId());
                        intent.putExtra("noticeType", addFriendProtocol.getSystemBaseProtocol().getNoticeType());
                        Init.getContext().sendBroadcast(intent);

                        if (iMessageControl != null) {
                            msghandler.sendEmptyMessage(15);
                        }

//                        if (NotificationSwitch.IsAddfriend)
//                        {
//                            // 发通知
//                            NotificationClass notificationClass=new NotificationClass(Init.getContext());
//
//                            notificationClass.add_Notification("有朋友申请添加好友",addFriendProtocol.getNickName(),"申请好友验证",
//                                    NotificationClass.ADDFRIENDID,getpendingIntent(
//                                            addFriendProtocol.getSystemBaseProtocol().getNoticeId(),
//                                            NotificationSwitch.getGetAddFriendViewPendingIntent(),6));//TODO 需要增加pendintent
//                        }
                        break;
                    case SystemBaseProtocol.FRIENDACCEPT:
                        addFriendProtocol = (FriendProtocol) systemBaseProtocol.getProtocol();
                        messageDB = new MessageDB();
                        addFriendProtocol.getSystemBaseProtocol().setNoticeType(100);// 单聊天
                        msgid = messageDB.checkMessageAddFriend(addFriendProtocol);
                        if (msgid == null) {
                            messageDB.insertMessageList(addFriendProtocol);
                        }
                        messageDB.updateMessageListForContent(addFriendProtocol.getUserId(),
                                addFriendProtocol.getNickName() + "已经成为你的好友");
                        ChatMessage.sendSysMsg("你和" + addFriendProtocol.getNickName() + "已经成为好友,可以开始聊天了",
                                addFriendProtocol.getSystemBaseProtocol().getSendTime(), addFriendProtocol.getUserId());
                        JSONObject jsonObject = addFriendProtocol.getSystemBaseProtocol().getBody();
                        contacts = new Contacts();
                        contacts.setId(jsonObject.getString("userId"));
                        contacts.setPY(jsonObject.getString("pinyin"));
                        contacts.setNickimgurl(jsonObject.getString("picId"));
                        contacts.setNickname(jsonObject.getString("nickName"));
                        contacts.setAreaName(jsonObject.getString("areaName"));
                        contacts.setFirstLetter(jsonObject.getString("firstLetter"));
                        messageDB = new MessageDB();
                        messageDB.insertFriend(contacts);
                        if (iMessageControl != null) {

                            msghandler.sendEmptyMessage(15);

                        }
                        break;
                    case SystemBaseProtocol.DELETEFRIEND:
                        addFriendProtocol = (FriendProtocol) systemBaseProtocol.getProtocol();
                        messageDB = new MessageDB();
                        messageDB.deletefriend(addFriendProtocol.getReleaseUserId());
                        messageDB.deletemessage(addFriendProtocol.getReleaseUserId());
                        messageDB.deletechatlog(addFriendProtocol.getReleaseUserId());
                        break;
                    case SystemBaseProtocol.CREATEGROUP:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();

//                        if (messageDB.isExitsMsgidForCount(groupProtocol.getGroupid()) != 0) {
//                            messageDB.deletemessage(groupProtocol.getGroupid());
//                        }

                        messageDB.deleteGroupMemberInfo(groupProtocol.getGroupid());
                        for (Map<String, String> map : groupProtocol.getMenbers()) {
                            if (!map.get("userId").equals(BaseUserInfo.getBaseUserInfo().getUserId())) {
                                if (!groupProtocol.getGroupadminid().equals(map.get("userId"))) {
                                    if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                                        contacts = messageDB.getContactsForSamlple(map.get("userId"));
                                        if (contacts == null)
                                            groupname += map.get("nickName") + ",";
                                        else {
                                            if (contacts.getNameRemark().equals(""))
                                                groupname += map.get("nickName") + ",";
                                            else
                                                groupname += contacts.getNameRemark() + ",";
                                        }
                                    } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                                        groupname += map.get("nickName") + ",";
                                    }
                                }


                            }
                            messageDB.insertGroupMemberInfo(groupProtocol.getGroupid(), map.get("nickName"),
                                    map.get("userId"), map.get("picId"));
                        }

                        if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                            contacts = messageDB.getContactsForSamlple(groupProtocol.getGroupadminid());

                            if (contacts != null) {
                                String s = "";
                                if (contacts.getNameRemark().equals("")) {
                                    s = contacts.getNickname();
                                } else {
                                    s = contacts.getNameRemark();
                                }
                                strmsg = s + "邀请" + "你," + groupname.substring(0, groupname.length() - 1) + "加入了群聊";
                                groupname = s + "," + groupname;
                            }

                        } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                            Cursor cursor = messageDB.getPhoneBookContactsInfo(groupProtocol.getGroupadminid());

                            if (cursor != null) {
                                cursor.moveToNext();
                                String s = cursor.getString(3);
                                strmsg = s + "邀请" + "你," + groupname.substring(0, groupname.length() - 1) + "加入了群聊";
                                groupname = s + "," + groupname;
                            }
                            cursor.close();
                        }

                        groupname += BaseUserInfo.getBaseUserInfo().getUsername(); //groupname.substring(0, groupname.length() - 1);
                        messageDB.insertGroupInfo(groupProtocol.getGroupid(), groupname, groupProtocol.getGroupAvatar(),
                                groupProtocol.getGroupadminid());
                        messageDB.insertMessageList(groupProtocol.getGroupid(), groupname,
                                groupProtocol.getGroupAvatar(), 101);
                        messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                systemBaseProtocol.getSendTime());
                        ChatMessage.sendSysMsg(strmsg,
                                systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());

                        break;
                    case SystemBaseProtocol.UPDATEGROUPNAME:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();
                        messageDB.updateGroupName(groupProtocol.getGroupid(), groupProtocol.getGroupName());


                        if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                            Contacts contacts2 = messageDB.getContactsForSamlple(groupProtocol.getOpUserid());
                            if (contacts2.getNameRemark().equals(""))
                                desc = messageDB.getGroupNickName(groupProtocol.getOpUserid(),
                                        groupProtocol.getGroupid());

                            else
                                desc = contacts2.getNameRemark();
                        } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                            desc = messageDB.getGroupNickName(groupProtocol.getOpUserid(),
                                    groupProtocol.getGroupid());
                        }


                        desc = String.format("%1$s修改群名称为\"%2$s\"", desc, groupProtocol.getGroupName());
                        ChatMessage.sendSysMsg(desc,
                                systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());
                        messageDB.updateMessageListForContent(groupProtocol.getGroupid(), desc);
                        messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                systemBaseProtocol.getSendTime());
                        intent1 = new Intent(SystemBaseProtocol.BroadCastUpdateSysMsg);
                        intent1.putExtra("groupid", groupProtocol.getGroupid());
                        intent1.putExtra("noticeType", systemBaseProtocol.getNoticeType());
                        intent1.putExtra("sendtime", systemBaseProtocol.getSendTime());
                        intent1.putExtra("content", desc);
                        Init.getContext().sendBroadcast(intent1);
                        if (iMessageControl != null) {
                            msghandler.sendEmptyMessage(15);
                        }

                        break;
                    case SystemBaseProtocol.UPDATEGROUPNICKNAME:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();
                        messageDB.updateGroupNickName(groupProtocol.getGroupid(), groupProtocol.getGroupnickname(),
                                groupProtocol.getGroupuserid());

                        break;
                    case SystemBaseProtocol.KICIOUTGROUP:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();

                        String deleduserid = groupProtocol.getArray().getString(0);
                        opusername = messageDB.getGroupNickName(groupProtocol.getKickUserId(),
                                groupProtocol.getGroupid());


                        if (deleduserid.equals(BaseUserInfo.getBaseUserInfo().getUserId()))//自己
                        {
                            messageDB.updateMessageListForName(groupProtocol.getGroupid(),
                                    messageDB.getGroupAllName(groupProtocol.getGroupid()));
                            messageDB.deleteGroupInfo(groupProtocol.getGroupid());
                            messageDB.deleteGroupMemberInfo(groupProtocol.getGroupid());
                            ChatMessage.sendSysMsg(String.format("你被%1$s移除群聊", opusername),
                                    systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());
                            messageDB.updateMessageListForState(groupProtocol.getGroupid(), -1);
                            if (iMessageControl != null) {
                                msghandler.sendEmptyMessage(15);
                            }
                            messageDB.updateMessageListForContent(groupProtocol.getGroupid(),
                                    String.format("你被%1$s移除群聊", opusername));
                            messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                    systemBaseProtocol.getSendTime());
                            intent1 = new Intent(SystemBaseProtocol.BroadCastUpdateSysMsg);
                            intent1.putExtra("groupid", groupProtocol.getGroupid());
                            intent1.putExtra("noticeType", systemBaseProtocol.getNoticeType());
                            intent1.putExtra("sendtime", systemBaseProtocol.getSendTime());
                            intent1.putExtra("content", String.format("你被%1$s移除群聊", opusername));
                            Init.getContext().sendBroadcast(intent1);
                            if (iMessageControl != null) {
                                msghandler.sendEmptyMessage(15);
                            }

                        } else {

                            messageDB.deleteGroupMemberInfo(deleduserid);
                            String tmpgroupname = messageDB.getGroupAllName(groupProtocol.getGroupid());
                            if (tmpgroupname.split(",").length < 2)
                                messageDB.updateGroupName(groupProtocol.getGroupid(),
                                        messageDB.getGroupAllName(groupProtocol.getGroupid()));
                            else {
                                messageDB.updateGroupName(groupProtocol.getGroupid(),
                                        messageDB.getGroupAllNamewithsplet(groupProtocol.getGroupid()));
                                messageDB.updateMessageListForName(groupProtocol.getGroupid(),
                                        messageDB.getGroupAllNamewithsplet(groupProtocol.getGroupid()));
                            }
                            messageDB.updateGroupNickImg(groupProtocol.getGroupid(), groupProtocol.getGroupAvatar());
                            messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                    systemBaseProtocol.getSendTime());
                        }


                        break;
                    case SystemBaseProtocol.PULLINGROUP:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();
                        inviteUserId = groupProtocol.getInviteUserId();

                        if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                            contacts = messageDB.getContactsForSamlple(inviteUserId);

                            if (contacts == null)
                                inviteUserName = messageDB.getGroupNickName(inviteUserId, groupProtocol.getGroupid());
                            else {
                                if (contacts.getNameRemark().equals(""))
                                    inviteUserName = messageDB.getGroupNickName(inviteUserId, groupProtocol.getGroupid());
                                else
                                    inviteUserName = contacts.getNameRemark();
                            }
                        } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                            inviteUserName = messageDB.getGroupNickName(inviteUserId, groupProtocol.getGroupid());
                        }


                        JSONArray jsonArray = groupProtocol.getArray();
                        String tmpstr = "";
//                        messageDB.deleteGroupMemberInfoForGroupid(groupProtocol.getGroupid());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            tmpstr += jsonObject1.getString("nickName") + ",";
                            messageDB.updateGroupNickImg(groupProtocol.getGroupid(),
                                    groupProtocol.getGroupAvatar());
                            messageDB.insertGroupMemberInfo(groupProtocol.getGroupid(),
                                    jsonObject1.getString("nickName"), jsonObject1.getString("userId"),
                                    jsonObject1.getString("picId"));
                        }
                        tmpstr = tmpstr.substring(0, tmpstr.length() - 1);
                        desc = String.format("%1$s邀请了%2$s", inviteUserName, tmpstr);

                        ChatMessage.sendSysMsg(desc,
                                systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());
                        messageDB.updateMessageListForContent(groupProtocol.getGroupid(), desc);
                        messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                systemBaseProtocol.getSendTime());
                        intent1 = new Intent(SystemBaseProtocol.BroadCastUpdateSysMsg);
                        intent1.putExtra("groupid", groupProtocol.getGroupid());
                        intent1.putExtra("noticeType", systemBaseProtocol.getNoticeType());
                        intent1.putExtra("sendtime", systemBaseProtocol.getSendTime());
                        intent1.putExtra("content", desc);
                        Init.getContext().sendBroadcast(intent1);
                        if (iMessageControl != null) {
                            msghandler.sendEmptyMessage(15);
                        }
                        break;
                    case SystemBaseProtocol.PULLINGROUPSELF:

                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();
                        messageDB.deleteGroupInfo(groupProtocol.getGroupid());
                        messageDB.deleteGroupMemberInfoForGroupid(groupProtocol.getGroupid());
                        String allgroupname = "";
                        for (Map<String, String> map : groupProtocol.getMenbers()) {


                            if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                                contacts = messageDB.getContactsForSamlple(map.get("userId"));
                                if (contacts == null)
                                    allgroupname += map.get("nickName") + ",";
                                else {
                                    if (contacts.getNameRemark().equals(""))
                                        allgroupname += map.get("nickName") + ",";
                                    else
                                        allgroupname += contacts.getNameRemark() + ",";
                                }
                            } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                                allgroupname += map.get("nickName") + ",";
                            }


                            messageDB.insertGroupMemberInfo(groupProtocol.getGroupid(), map.get("nickName"),
                                    map.get("userId"), map.get("picId"));
                        }

                        for (int i = 0; i < groupProtocol.getArray().length(); i++) {
                            JSONObject jsonObject1 = groupProtocol.getArray().getJSONObject(i);
                            if (!jsonObject1.getString("userId").equals(BaseUserInfo.getBaseUserInfo().getUserId())) {


                                if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                                    contacts = messageDB.getContactsForSamlple(jsonObject1.getString("userId"));
                                    if (contacts == null)
                                        groupname += jsonObject1.getString("nickName") + ",";
                                    else {
                                        if (contacts.getNameRemark().equals(""))
                                            groupname += jsonObject1.getString("nickName") + ",";
                                        else
                                            groupname += contacts.getNameRemark() + ",";
                                    }
                                } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                                    groupname += jsonObject1.getString("nickName") + ",";
                                }
                            }
                        }

                        if (groupname != "")
                            groupname = "你," + groupname.substring(0, groupname.length() - 1);
                        else
                            groupname = "你";

                        String s = "";
                        if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                            contacts = messageDB.getContactsForSamlple(groupProtocol.getInviteUserId());
                            if (contacts != null) {
                                if (contacts.getNameRemark().equals("")) {
                                    s = contacts.getNickname();
                                } else {
                                    s = contacts.getNameRemark();
                                }
                            } else
                                s = messageDB.getGroupNickName(groupProtocol.getInviteUserId(), groupProtocol.getGroupid());
                        } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                            Cursor cursor = messageDB.getPhoneBookContactsInfo(groupProtocol.getInviteUserId());
                            if (cursor != null) {
                                cursor.moveToNext();
                                s = cursor.getString(3);
                            }
                            cursor.close();
                        }

                        strmsg = s + "邀请" + groupname + "加入了群聊";
                        groupname = s + "," + groupname;
                        if (groupProtocol.getGroupName() != null)
                            allgroupname = groupProtocol.getGroupName();
                        messageDB.insertGroupInfo(groupProtocol.getGroupid(), allgroupname, groupProtocol.getGroupAvatar(),
                                "");

                        if (messageDB.isExitsMsgidForCount(groupProtocol.getGroupid()) == 0) {
                            messageDB.insertMessageList(groupProtocol.getGroupid(), "",
                                    groupProtocol.getGroupAvatar(), 101);
                            messageDB.updateMessageListForContent(groupProtocol.getGroupid(), strmsg);

                        } else {
                            messageDB.updateMessageListForDT(groupProtocol.getGroupid(),
                                    systemBaseProtocol.getSendTime());

                            messageDB.updateMessageListForName(groupProtocol.getGroupid(),
                                    messageDB.getGroupAllName(groupProtocol.getGroupid()));
                            messageDB.updateMessageListForState(groupProtocol.getGroupid(), 101);
                            messageDB.updateMessageListForContent(groupProtocol.getGroupid(), strmsg);
                        }
                        ChatMessage.sendSysMsg(strmsg,
                                systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());
                        intent1 = new Intent(SystemBaseProtocol.BroadCastUpdateSysMsg);
                        intent1.putExtra("groupid", groupProtocol.getGroupid());
                        intent1.putExtra("noticeType", systemBaseProtocol.getNoticeType());
                        intent1.putExtra("sendtime", systemBaseProtocol.getSendTime());
                        intent1.putExtra("content", strmsg);
                        Init.getContext().sendBroadcast(intent1);
                        if (iMessageControl != null) {
                            msghandler.sendEmptyMessage(15);
                        }
                        break;
                    case SystemBaseProtocol.OTHEREXITGROUP:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();


                        if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                            contacts = messageDB.getContactsForSamlple(groupProtocol.getGroupuserid());
                            if (contacts == null)
                                opusername = messageDB.getGroupNickName(groupProtocol.getGroupuserid(),
                                        groupProtocol.getGroupid());
                            else {
                                if (contacts.getNameRemark().equals(""))
                                    opusername = messageDB.getGroupNickName(groupProtocol.getGroupuserid(),
                                            groupProtocol.getGroupid());
                                else
                                    opusername = contacts.getNameRemark();
                            }
                        } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                            opusername = messageDB.getGroupNickName(groupProtocol.getGroupuserid(),
                                    groupProtocol.getGroupid());
                        }


                        strmsg = opusername + "退出了群聊";
                        messageDB.deleteGroupMemberInfo(groupProtocol.getGroupuserid());
                        messageDB.updateMessageListForContent(groupProtocol.getGroupid(), strmsg);
                        ChatMessage.sendSysMsg(strmsg,
                                systemBaseProtocol.getSendTime(), groupProtocol.getGroupid());
                        intent1 = new Intent(SystemBaseProtocol.BroadCastUpdateSysMsg);
                        intent1.putExtra("groupid", groupProtocol.getGroupid());
                        intent1.putExtra("noticeType", systemBaseProtocol.getNoticeType());
                        intent1.putExtra("sendtime", systemBaseProtocol.getSendTime());
                        intent1.putExtra("content", strmsg);
                        Init.getContext().sendBroadcast(intent1);

                        if (iMessageControl != null) {
                            msghandler.sendEmptyMessage(15);
                        }

                        break;
                    case SystemBaseProtocol.UPDATEGROUPADMIN:
                        groupProtocol = (GroupProtocol) systemBaseProtocol.getProtocol();
                        messageDB.updateGroupAdminId(groupProtocol.getGroupid(),
                                BaseUserInfo.getBaseUserInfo().getUserId());
                        break;
                    case SystemBaseProtocol.EBNOTICESALERTINFO:
                    case SystemBaseProtocol.EBNOTICES17:

                        ebNoticesProtocol = (EBNoticesProtocol) systemBaseProtocol.getProtocol();
                        NotificationClass notificationClass = new NotificationClass(Init.getContext());
                        notificationClass.add_Notification(ebNoticesProtocol.getTitle(), ebNoticesProtocol.getTitle(), ebNoticesProtocol.getContent(),
                                NotificationClass.EBINFO, getpendingIntentForUrl(
                                        ebNoticesProtocol.getUrl(),
                                        NotificationSwitch.getGetWebViewViewPendingIntent(),
                                        21));
                        break;
                    case SystemBaseProtocol.FAMILYADD:
                        FamilyProtocol familyProtocol = (FamilyProtocol) systemBaseProtocol.getProtocol();

                        contacts = new Contacts();
                        contacts.setId(familyProtocol.getUserId());
                        contacts.setPY(Common.getPingYin(familyProtocol.getNickName()));
                        contacts.setNickimgurl(familyProtocol.getPicId());
                        contacts.setNickname(familyProtocol.getNickName());
                        contacts.setAreaName("");
                        contacts.setGender(familyProtocol.getGender());

                        String fpy = Common.getFirstSpell(familyProtocol.getNickName());
                        int ascii = fpy.charAt(0);
                        if (ascii > 64 && ascii < 91)
                            contacts.setFirstLetter(fpy);
                        else
                            contacts.setFirstLetter("@");
                        messageDB.insertFriend(contacts);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnMsgCallBack(WeAppProtocol weAppProtocol) {
            try {


                MessageDB messageDB = new MessageDB();


                if (messageDB.isExitsMsgidForCount(weAppProtocol.getFrom()) == 0) {
                    messageDB.insertMessageList(weAppProtocol);
                    messageDB.updateMessageList(weAppProtocol.getFrom(), 1);
                } else {
                    int count = messageDB.isExitsMsgid(weAppProtocol.getFrom());
                    messageDB.updateMessageList(weAppProtocol.getFrom(), messageDB.getWeAppName(weAppProtocol.getFrom()));
                    messageDB.updateMessageList(weAppProtocol.getFrom(), ++count);
                    if (weAppProtocol.getWeAppType() == WeAppProtocol.WeAppType.TEXT)
                        messageDB.updateMessageListForContent(weAppProtocol.getFrom(), weAppProtocol.getBodyJson().getString("title"));
                    else if (weAppProtocol.getWeAppType() == WeAppProtocol.WeAppType.PICTURANDTEXT) {
                        messageDB.updateMessageListForContent(weAppProtocol.getFrom(),
                                weAppProtocol.getBodyArry().getJSONObject(0).getString("title"));
                    }
                    messageDB.updateMessageListForDT(weAppProtocol.getFrom(), weAppProtocol.getSendtime());
                }


                switch (weAppProtocol.getWeAppType()) {
                    case TEXT:

                        messageDB.insertWeAppDetail(weAppProtocol.getFrom(), weAppProtocol.getMsgid(),
                                weAppProtocol.getBodyJson().toString(), weAppProtocol.getBodyJson().getString("title"),
                                weAppProtocol.getBodyJson().getString("content"), weAppProtocol.getSendtime(),
                                weAppProtocol.getWeAppTypeInt());

                        break;
                    case PICTURANDTEXT:
                        messageDB.insertWeAppDetail(weAppProtocol.getFrom(), weAppProtocol.getMsgid(),
                                weAppProtocol.getBodyArry().toString(), "",
                                "", weAppProtocol.getSendtime(),
                                weAppProtocol.getWeAppTypeInt());
                        break;
                }


                if (weAppProtocol.getMode() == 1) {
                    if (!weAppProtocol.getNoDisturb()) {
                        if (isNotification) {
                            NotificationClass notificationClass = new NotificationClass(Init.getContext());

                            notificationClass.add_Notification("一条新消息", messageDB.getWeAppName(weAppProtocol.getFrom()),
                                    "一条信息",
                                    NotificationClass.WEAPPINFO, getpendingIntentForWeApp(weAppProtocol.getFrom(),
                                            NotificationSwitch.getGetWeAppViewPendingIntent()));
                        }
                    }
                }
                if (iMessageControl != null) {
                    msghandler.sendEmptyMessage(15);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnConnectServerFail() {
            //重新连接
            new Thread(checkMqttstate).start();
        }

        @Override
        public void OnStartReceive() {
            //聊天消息主题 topic/chat/[userid]
            //通知主题 topic/notice/[userid]

            mqttClient.setPublictopic(MQTTConfig.ChatNoice);
            mqttClient.setPublictopic(MQTTConfig.SystemNoice);
            mqttClient.setPublictopic(MQTTConfig.WeAppNoice);

        }

        @Override
        public void OnStartReceiveGroup() {

        }

        @Override
        public void OnConnected() {
            Log.i("接收到MQTT的数据", "连接成功============");
            Message message = msghandler.obtainMessage();
            message.what = 11;
            msghandler.sendMessage(message);
            isDisMqttConnect = false;
            new Thread(reciveServerMsgThread).start();//开始启动接受
        }

        @Override
        public void OnDisConnected() {
            Log.i("接收到MQTT的数据", "连接断开============");

            Message message = msghandler.obtainMessage();
            message.what = 10;
            msghandler.sendMessage(message);
            isDisMqttConnect = true;
            if (!IsLogOut)
                new Thread(checkMqttstate).start();
        }
    };


    /**
     * 开始拉去一遍所有数据
     */
    Runnable reciveServerMsgThread = new Runnable() {
        @Override
        public void run() {
            try {


                msghandler.sendEmptyMessage(12);


                AjaxHttp ajaxHttp = new AjaxHttp();
                String url = String.format("%1$snotice/unreceived",
                        StereoService.IMUrl);
                Log.i("loginurl", url);
                ajaxHttp.openRequest(url, AjaxHttp.REQ_METHOD_POST);
                if (!ajaxHttp.sendRequest()) {
                    return;
                }
                byte[] buffer = ajaxHttp.getRespBodyData();
                if (buffer == null) {
                    return;
                }

                String result = new String(buffer, "utf-8");
                Log.i("获取未收到消息:", result);
                JSONObject jsonObject = null;
                ReturnData returnData;

                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {
                    return;
                }
                JSONArray noticedata = returnData.getJsonArray();
                for (int i = 0; i < noticedata.length(); i++) {
                    JSONObject jsonObject1 = noticedata.getJSONObject(i);

                    SystemBaseProtocol systemBaseProtocol = new SystemBaseProtocol(jsonObject1);
                    JSONObject jsonObjectreceip = new JSONObject();
                    jsonObjectreceip.put("noticeId", systemBaseProtocol.getNoticeId());

                    mqttClient.sendMessage(mqttClient.receiptNoticeTopic, jsonObjectreceip.toString(), new MQTTClient.IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功", "");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败", "");
                        }
                    });


                    Message message = handlerrecivie.obtainMessage();
                    message.what = 2;
                    message.obj = systemBaseProtocol;
                    handlerrecivie.sendMessage(message);
                }


                url = String.format("%1$smsg/unreceived",
                        StereoService.IMUrl);
                Log.i("loginurl", url);
                ajaxHttp.openRequest(url, AjaxHttp.REQ_METHOD_POST);

                if (!ajaxHttp.sendRequest()) {
                    return;
                }
                buffer = ajaxHttp.getRespBodyData();
                if (buffer == null) {
                    return;
                }

                result = new String(buffer, "utf-8");
                Log.i("获取未收到消息:", result);


                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    return;
                }
                JSONObject msgdata = returnData.getReturnData();
                JSONArray singleMsgs = msgdata.getJSONArray("singleMsgs");//聊天消息
                JSONArray groupMsgs = msgdata.getJSONArray("groupMsgs");


                for (int i = 0; i < singleMsgs.length(); i++) {
                    JSONObject jsonObject1 = singleMsgs.getJSONObject(i);

                    BaseChatProtocol baseChatProtocol = new BaseChatProtocol(jsonObject1);
                    baseChatProtocol.setMode(0);

                    JSONObject jsonObjectreceip = new JSONObject();
                    jsonObjectreceip.put("ope", baseChatProtocol.getChatMsgJson().getOpe());
                    jsonObjectreceip.put("msgId", baseChatProtocol.getChatMsgJson().getMsgid());

                    mqttClient.sendMessage(mqttClient.receipMmsgTopic, jsonObjectreceip.toString(), new MQTTClient.IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功", "");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败", "");
                        }
                    });


                    Message message = handlerrecivie.obtainMessage();
                    message.what = 1;
                    message.obj = baseChatProtocol;
                    handlerrecivie.sendMessage(message);
                }

                for (int i = 0; i < groupMsgs.length(); i++) {
                    JSONObject jsonObject1 = groupMsgs.getJSONObject(i);

                    BaseChatProtocol baseChatProtocol = new BaseChatProtocol(jsonObject1);
                    baseChatProtocol.setMode(0);
                    JSONObject jsonObjectreceip = new JSONObject();
                    jsonObjectreceip.put("ope", baseChatProtocol.getChatMsgJson().getOpe());
                    jsonObjectreceip.put("msgId", baseChatProtocol.getChatMsgJson().getMsgid());
                    jsonObjectreceip.put("userId", BaseUserInfo.getBaseUserInfo().getUserId());

                    mqttClient.sendMessage(mqttClient.receipMmsgTopic, jsonObjectreceip.toString(), new MQTTClient.IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功", "");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败", "");
                        }
                    });
                    Message message = handlerrecivie.obtainMessage();
                    message.what = 1;
                    message.obj = baseChatProtocol;
                    handlerrecivie.sendMessage(message);
                }


                url = String.format("%1$sappMsg/unreceived",
                        StereoService.IMUrl);
                Log.i("appMsg", url);
                ajaxHttp.openRequest(url, AjaxHttp.REQ_METHOD_POST);

                if (!ajaxHttp.sendRequest()) {
                    return;
                }
                buffer = ajaxHttp.getRespBodyData();
                if (buffer == null) {
                    return;
                }

                result = new String(buffer, "utf-8");
                Log.i("获取未收到消息:", result);


                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {
                    return;
                }

                JSONArray weappdatas = returnData.getJsonArray();//聊天消息

                for (int i = 0; i < weappdatas.length(); i++) {
                    JSONObject jsonObject1 = weappdatas.getJSONObject(i);

                    WeAppProtocol weAppProtocol = new WeAppProtocol(jsonObject1);
                    weAppProtocol.setMode(0);//提醒打开
                    JSONObject jsonObjectweapp = new JSONObject();
                    jsonObjectweapp.put("msgId", weAppProtocol.getMsgid());
                    jsonObjectweapp.put("userId", BaseUserInfo.getBaseUserInfo().getUserId());
                    mqttClient.sendMessage(mqttClient.receiptWeAppTopic, jsonObjectweapp.toString(), new MQTTClient.IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功", "");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败", "");
                        }
                    });
                    Message message = handlerrecivie.obtainMessage();
                    message.what = 3;
                    message.obj = weAppProtocol;
                    handlerrecivie.sendMessage(message);
                }

                msghandler.sendEmptyMessage(11);
                return;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler handlerrecivie = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                mqttCallBack.OnMsgCallBack((BaseChatProtocol) msg.obj);
            if (msg.what == 2)
                mqttCallBack.OnMsgCallBack((SystemBaseProtocol) msg.obj);
            if (msg.what == 3)
                mqttCallBack.OnMsgCallBack((WeAppProtocol) msg.obj);
        }
    };

    public void disconnecetMQTT() {
        islogin = false;
        if (mqttClient != null) {
            mqttClient.disConnectServer();
        }
        mqttClient = null;
    }

    /***
     * 检查网络是否连接
     */
    Runnable checkMqttstate = new Runnable() {
        @Override
        public void run() {
            int i = 0;
            if (!islogin)
                return;
            while (isDisMqttConnect) {
                i++;
                if (mqttClient != null) {
                    if (mqttClient.getConnected()) {
                        isDisMqttConnect = false;
                        return;
                    }
                }
                try {
                    if (i > 10) {
                        if (mqttClient != null)
                            mqttClient.disConnectServer();
                        mqttClient = null;
                        mqttClient = new MQTTClient(mqttCallBack);
                        mqttClient.connecetServer();

                        Thread.sleep(5000);
                    } else
                        Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Handler msghandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10)//断开
            {
                if (iMessageControl != null)
                    iMessageControl.OnMQTTState(10);
            } else if (msg.what == 11)// 连接
            {
                if (iMessageControl != null)
                    iMessageControl.OnMQTTState(11);
            } else if (msg.what == 12)// 连接
            {
                if (iMessageControl != null)
                    iMessageControl.OnMQTTState(12);
            } else if (msg.what == 15) {
                if (iMessageControl != null)
                    iMessageControl.OnRefresh();
            } else {
                if (iMessageControl != null)
                    iMessageControl.OnNewMessage((ChatMessage) msg.obj);
            }
        }
    };


    public void sendNotification(int msgmode, String name, String content, String sender, int id) {
        notificationClass = new NotificationClass(Init.getContext());

        notificationClass.add_Notification(name, "来自:" + name, "一条聊天信息",
                id, getpendingIntent(sender, msgmode));
    }

    PendingIntent getpendingIntent(String userid, int msgMode) {

        //        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
        PendingIntent contentIntent = null;
//        Intent notificationIntent;
//        if (this.getIsRunAPP()) {
//            notificationIntent = new Intent(SuyApplication.getApplication(), ChatActivity.class);
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            notificationIntent.putExtra("msgid", userid);
//            if (msgMode == 1)
//                notificationIntent.putExtra("chattype", "1");
//            if (msgMode == 2)
//                notificationIntent.putExtra("chattype", "2");
//            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
//                    , msgMode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } else {
//            notificationIntent = new Intent(SuyApplication.getApplication(),
//                    SplashActivity.class);
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
//                    , 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
        return contentIntent;
    }


    /**
     * 设置当前通知跳转到的intent
     *
     * @param msgid
     * @param intent
     * @param mode
     * @return
     */
    private PendingIntent getpendingIntentForchat(String msgid, Intent intent, int mode, int chattype) {


        PendingIntent contentIntent = null;

        if (this.getIsRunAPP()) {
            intent.putExtra("msgid", msgid);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Log.i("调用，准备启动", Init.getContext().getPackageName());
            Intent appIntent = NotificationSwitch.getGetSplashViewPendingIntent();
            appIntent.putExtra("msgid", msgid);
            appIntent.putExtra("chattype", chattype);
            appIntent.putExtra("type", NotificationSwitch.CHATACTIVTY);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }

    private PendingIntent getpendingIntentForWeApp(String msgid, Intent intent) {


        PendingIntent contentIntent = null;

        if (this.getIsRunAPP()) {
            intent.putExtra("from", msgid);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Log.i("调用，准备启动", Init.getContext().getPackageName());
            Intent appIntent = NotificationSwitch.getGetSplashViewPendingIntent();
            appIntent.putExtra("msgid", msgid);
            appIntent.putExtra("type", NotificationSwitch.WEAPPURLACTIVTY);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }
    //显示通知跳转 URL
    private PendingIntent getpendingIntentForUrl(String url, Intent intent, int mode) {


        PendingIntent contentIntent = null;

        if (this.getIsRunAPP()) {
            intent.putExtra("url", url);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Log.i("调用，准备启动", Init.getContext().getPackageName());
            Intent appIntent = NotificationSwitch.getGetSplashViewPendingIntent();
            appIntent.putExtra("url", url);
            appIntent.putExtra("type", NotificationSwitch.WEBVIEWURLACTIVTY);
            contentIntent = PendingIntent.getActivity(Init.getContext()
                    , 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }


    public interface IMessageControl {
        void OnNewMessage(ChatMessage chatMessage);

        void OnMQTTState(int state);

        void OnRefresh();//拉取数据刷新

    }

}
