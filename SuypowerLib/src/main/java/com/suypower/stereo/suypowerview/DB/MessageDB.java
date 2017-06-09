package com.suypower.stereo.suypowerview.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.suypower.stereo.suypowerview.Chat.ChatMessage;
import com.suypower.stereo.suypowerview.Chat.ChatMsgJson;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.Protocol.FriendProtocol;
import com.suypower.stereo.suypowerview.Protocol.SystemBaseProtocol;
import com.suypower.stereo.suypowerview.Protocol.WeAppProtocol;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Stereo on 16/4/5.
 */
public class MessageDB {

    SQLiteDatabase db;


    public MessageDB() {
        this.db = SuyDB.getSuyDB().getDb();
    }


    /**
     * 判断消息通知信息是否存在
     *
     * @param msgid
     * @return
     */
    public int isExitsMsgid(String msgid) {
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("select msgmark from message where msgid =? and (msgtype = 100 or msgtype = 101)",
                    new String[]{msgid});
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                count = cursor.getInt(0);
            }
            cursor.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }

    /**
     * 获取当前messageid 是否存在
     *
     * @param msgid
     * @return
     */
    public int isExitsMsgidForCount(String msgid) {
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("select * from message where msgid =? and (msgtype = 100 or msgtype = 101 or msgtype = 103)",
                    new String[]{msgid});
            count = cursor.getCount();
            cursor.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }


    public int getChatlogCounts(String msgid) {
        Cursor cursor = null;
        Log.i("msgid 获取历史", msgid);

        try {
            cursor = db.rawQuery("select * from chatlog where groupid =?",
                    new String[]{msgid});
            int i = cursor.getCount();
            cursor.close();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 根据备注更新用户信息
     *
     * @param userid
     * @param remark
     * @param py
     * @param p
     */
    public void updateUserInfoWhitRemark(String userid, String remark, String py, String p) {
        ContentValues cv = new ContentValues();
        cv.put("py", py);
        cv.put("firstLetter", p);
        cv.put("nameRemark", remark);
        db.update("friend", cv, "userid = ?", new String[]{userid});

        if (remark.equals(""))
            remark = getContactsForSamlple(userid).getNickname();

        cv = new ContentValues();
        cv.put("msgtitle", remark);
        db.update("message", cv, "msgid = ?", new String[]{userid});

        cv = new ContentValues();
        cv.put("sender", remark);
        db.update("chatlog", cv, "senderid = ?", new String[]{userid});
    }
//
//
//    /**
//     * 添加置顶
//     * @param groupid
//     */
//    public void addtop(String groupid)
//    {
//        ContentValues cv = new ContentValues();
//        cv.put("groupid",groupid);
//        db.insert("t_istop", null, cv);
//    }
//
//    //删除置顶
//    public void deltop(String groupid)
//    {
//
//        db.delete("t_istop", "groupid = ? ", new String[]{groupid});
//    }
//
//

    /**
     * 获得聊天信息列表
     *
     * @return
     */
    public Cursor getMessageList() {
        try {
            Cursor cursor = db.rawQuery("select * from message where (msgtype = 100 or msgtype =101 or msgtype=103 or msgtype=90) order by msgdate desc", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Cursor getMessageListForKey(String key) {
        try {
            Cursor cursor = db.rawQuery("select * from message where (msgtype = 100 or msgtype =101 or msgtype=90) and (msgtitle like ? or  msgcontent like ?) order by msgdate desc",
                    new String[]{"%" + key + "%", "%" + key + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Cursor getMessageList(String msgid) {
        try {
            Cursor cursor = db.rawQuery("select * from message where (msgtype = 100 or msgtype =101 or msgtype=90) and msgid = ?",
                    new String[]{msgid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除消息列表
     *
     * @param msgid
     */
    public void deletemessage(String msgid) {
        db.delete("message", "msgid = ?", new String[]{msgid});
    }


    /**
     * 获取拼音
     *
     * @param az
     * @return
     */
    public Cursor getContactswhitAZ(String az) {

        try {
            Cursor cursor = db.rawQuery("select * from friend where firstletter = ?",
                    new String[]{az});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 搜索联系人
     *
     * @param key
     * @return
     */
    public Cursor getContactswhitSearch(String key) {

        try {
            Cursor cursor = db.rawQuery("select * from friend where py like ? or nickname like ? or nameRemark like ?",
                    new String[]{"%" + key + "%", "%" + key + "%", "%" + key + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 添加好友
     *
     * @param contacts
     */
    public void insertFriend(Contacts contacts) {
        ContentValues cv = new ContentValues();
        cv.put("userId", contacts.getId());
        cv.put("picId", contacts.getNickimgurl());
        cv.put("nickname", contacts.getNickname());
        cv.put("nameRemark", contacts.getNameRemark());
        cv.put("gender", contacts.getGender());
        cv.put("areaname", contacts.getAreaName());
        cv.put("firstLetter", contacts.getFirstLetter());
        cv.put("py", contacts.getPY());
        db.insert("friend", null, cv);
    }

    /**
     * 删除好友列表
     */
    public void deletefriend() {
        db.delete("friend", null, null);

    }


    public void deletefriend(String userid) {
        db.delete("friend", "userId = ?", new String[]{userid});

    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public Cursor getAllNewFriend() {
        try {
            Cursor cursor = db.rawQuery("select * from message where msgtype = ? ",
                    new String[]{String.valueOf(SystemBaseProtocol.ADDFRIEND)});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 插入一个微应用信息
     *
     * @param weAppProtocol
     */
    public void insertMessageList(WeAppProtocol weAppProtocol) {
        ContentValues cv = new ContentValues();
        cv.put("msgid", weAppProtocol.getFrom());
        cv.put("msgtype", 103);//公告
        cv.put("msgtitle", getWeAppName(weAppProtocol.getFrom()));
        try {
            if (weAppProtocol.getWeAppType() == WeAppProtocol.WeAppType.TEXT) {
                cv.put("msgcontent", weAppProtocol.getBodyJson().getString("title"));
            } else {
                if (weAppProtocol.getBodyArry().length() == 1) {

                }
                cv.put("msgcontent", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        cv.put("msgmark", 0);
        cv.put("senderid", weAppProtocol.getFrom());
        cv.put("msgstate", 1);
        cv.put("msgdate", weAppProtocol.getSendtime());
        cv.put("msgico", getWeAppIco(weAppProtocol.getFrom()));
        cv.put("isdisturb", weAppProtocol.getNoDisturb() ? 1 : 0);
        cv.put("istop", weAppProtocol.getTop() ? 1 : 0);
        db.insert("message", null, cv);
    }

    /**
     * 插入一个新的消息通知
     */
    public void insertMessageList(FriendProtocol addFriendProtocol) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid", addFriendProtocol.getUserId());
            cv.put("msgtype", addFriendProtocol.getSystemBaseProtocol().getNoticeType());
            cv.put("msgtitle", addFriendProtocol.getNickName());
            cv.put("msgcontent", addFriendProtocol.getDesc());
            cv.put("msgmark", 0);
            cv.put("senderid", addFriendProtocol.getUserId());
            cv.put("msgstate", 1);
            cv.put("msgdate", addFriendProtocol.getSystemBaseProtocol().getSendTime());
            cv.put("msgico", addFriendProtocol.getPicId());
            cv.put("isdisturb", 0);
            cv.put("istop", 0);
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insertMessageList(ChatMessage chatMessage) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid", chatMessage.getMessageid());
            cv.put("msgtype", chatMessage.getMsgMode());
            cv.put("msgtitle", chatMessage.getSender());
            cv.put("msgcontent", chatMessage.getMsg().toString());
            cv.put("msgmark", 1);
            cv.put("senderid", chatMessage.getSenderid());
            cv.put("msgstate", 1);
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgico", chatMessage.getNickimg());
            cv.put("isdisturb", chatMessage.getDisturb() ? 1 : 0);
            cv.put("istop", chatMessage.getIstop() ? 1 : 0);

            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 选择人发送
     *
     * @param userid
     * @param name
     * @param img
     */
    public void insertMessageList(String userid, String name, String img, int type) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid", userid);
            cv.put("msgtype", type);
            cv.put("msgtitle", name);
            cv.put("msgcontent", "");
            cv.put("msgmark", 0);
            cv.put("senderid", userid);
            cv.put("msgstate", 0);
            cv.put("msgdate", Common.getTimeInMillis(Common.GetSysTime()));
            cv.put("msgico", img);
            cv.put("isdisturb", 0);
            cv.put("istop", 0);
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insertMessageList(String userid, String name, String content, String img, int type) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid", userid);
            cv.put("msgtype", type);
            cv.put("msgtitle", name);
            cv.put("msgcontent", content);
            cv.put("msgmark", 0);
            cv.put("senderid", userid);
            cv.put("msgstate", 0);
            cv.put("msgdate", Common.getTimeInMillis(Common.GetSysTime()));
            cv.put("msgico", img);
            cv.put("isdisturb", 0);
            cv.put("istop", 0);
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 更新标题
     *
     * @param msgid
     * @param name
     */
    public void updateMessageListForName(String msgid, String name) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("msgtitle", name);
            db.update("message", cv, "msgid = ? ",
                    new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 更新内容
     *
     * @param msgid
     * @param content
     */
    public void updateMessageListForContent(String msgid, String content) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("msgcontent", content);
            db.update("message", cv, "msgid = ? ",
                    new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 消息更新信息列表
     *
     * @param chatMessage
     */
    public void updateMessageList(ChatMessage chatMessage, int count) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgtype", chatMessage.getMsgMode());
            cv.put("msgtitle", chatMessage.getSender());
            cv.put("msgcontent", chatMessage.getMsg().toString());
            cv.put("msgmark", count);
            cv.put("msgstate", 1);
            cv.put("senderid", chatMessage.getSenderid());
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgico", chatMessage.getMediaid());
            db.update("message", cv, "msgid = ? and (msgtype = 100 or msgtype =101)", new String[]{chatMessage.getMessageid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateMessageList(ChatMessage chatMessage, int count, String title, String img) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgtype", chatMessage.getMsgMode());
            cv.put("msgtitle", title);
            cv.put("msgcontent", chatMessage.getMsg().toString());
            cv.put("msgmark", count);
            cv.put("msgstate", 1);
            cv.put("senderid", chatMessage.getSenderid());
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgico", img);
            db.update("message", cv, "msgid = ? and (msgtype = 100 or msgtype =101)", new String[]{chatMessage.getMessageid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 更新聊天状态
     *
     * @param msgid
     * @param state
     */
    public void updateMessageListForState(String msgid, int state) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgstate", state);

            db.update("message", cv, "msgid = ? ", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 更新时间
     *
     * @param msgid
     * @param dt
     */
    public void updateMessageListForDT(String msgid, String dt) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgdate", dt);

            db.update("message", cv, "msgid = ? ", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 系统协议更新信息列表
     *
     * @param addFriendProtocol
     */
    public void updateMessageList(FriendProtocol addFriendProtocol) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid", addFriendProtocol.getSystemBaseProtocol().getNoticeId());
            cv.put("msgtitle", addFriendProtocol.getNickName());
            cv.put("msgcontent", addFriendProtocol.getDesc());
            cv.put("msgmark", 0);
            cv.put("msgstate", 1);
            cv.put("msgdate", addFriendProtocol.getSystemBaseProtocol().getSendTime());
            cv.put("msgico", addFriendProtocol.getPicId());
            db.update("message", cv, "senderid = ? and msgtype = ?",
                    new String[]{addFriendProtocol.getUserId(),
                            String.valueOf(addFriendProtocol.getSystemBaseProtocol().getNoticeType())});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateMessageListForAddFriend(String id) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgmark", 0);
            cv.put("msgstate", 2);// 已经添加
            db.update("message", cv, "senderid = ? and msgtype = 7 and msgstate= 1 ", new String[]{id});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 检查信息是否有存在的
     *
     * @param addFriendProtocol
     * @return
     */
    public String checkMessageAddFriend(FriendProtocol addFriendProtocol) {
        try {
            Cursor cursor = db.rawQuery("select msgid from message where msgid = ? and msgtype = ?",
                    new String[]{addFriendProtocol.getUserId(),
                            String.valueOf(addFriendProtocol.getSystemBaseProtocol().getNoticeType())});
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToNext();
            String r = cursor.getString(0);
            cursor.close();
            return r;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * 获取消息类型的未读总数
     *
     * @param msgtype
     * @param state
     * @return
     */
    public int LoadMsgCountForMsgType(int msgtype, int state) {
        try {
            Cursor cursor = db.rawQuery("select count(*) from message where msgstate =?  and msgtype = ?",
                    new String[]{String.valueOf(state), String.valueOf(msgtype)});
            cursor.moveToNext();
            int r = cursor.getInt(0);
            cursor.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取消息历史
     *
     * @param msgid
     * @param start
     * @param end
     * @return
     */
    public Cursor getChatlog(String msgid, int start, int end) {
        Cursor cursor = null;
        Log.i("msgid 获取历史", msgid);
        String sql = String.format("select * from chatlog where groupid =? order by 6 desc limit %1$s,%2$s", start, end);
        try {
            cursor = db.rawQuery(sql,
                    new String[]{msgid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 更新消息状态
     *
     * @param content
     */
    public void updatemsgstate(String groupid, String content, int state) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("msgsendstate", state);
            db.update("chatlog", cv, "groupid = ? and content =?", new String[]{groupid, content});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 发送消息后更新消息状态
     *
     * @param chatMessage
     */
    public void updateChatlog(ChatMessage chatMessage) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgsendstate", chatMessage.getMsgSendState());
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgid", chatMessage.getMsgid());
            db.update("chatlog", cv, "groupid = ? and createdt = ?",
                    new String[]{chatMessage.getMessageid(), chatMessage.getCretedt()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 更新图片mediaid
     *
     * @param oldmedia
     * @param newmediaid
     */
    public void updateChatlogFormediaid(String oldmedia, String newmediaid) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("content", newmediaid);
            db.update("chatlog", cv, "content = ?",
                    new String[]{oldmedia});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 插入聊天日志
     *
     * @param chatMessage
     */
    public void insertChatlog(ChatMessage chatMessage) {
        try {
//            Cursor cursor = db.rawQuery("select content from chatlog where msgid = ？", new String[]{chatMessage.getMsgid()});
//            if (cursor.getCount() > 0) {
//                cursor.close();
//                return;
//            }

            ContentValues cv = new ContentValues();
            cv.put("groupid", chatMessage.getMessageid());
            switch (chatMessage.getMessageTypeEnum()) {
                case TEXT:
                case System:
                    cv.put("content", chatMessage.getMsg().toString());
                    break;
                case PICTURE:
                case AUDIO:
                case EAUDIO:
                    cv.put("content", chatMessage.getMediaid());
                    break;


            }
            cv.put("isself", (chatMessage.getSelf()) ? 1 : 0);
            cv.put("sender", chatMessage.getSender());
            cv.put("msgsendstate", chatMessage.getMsgSendState());
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgType", chatMessage.getMsgType());
            cv.put("senderid", chatMessage.getSenderid());
            cv.put("msgid", chatMessage.getMsgid());
            cv.put("createdt", chatMessage.getCretedt());
            db.insert("chatlog", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Cursor getChatLogForPhoto(String msgid) {
        Cursor cursor = db.rawQuery("select content from chatlog where msgType = 2 and groupid= ?", new String[]{msgid});
        return cursor;
    }

    /**
     * 删除一个信息
     *
     * @param msgid
     */
    public void delChatLog(String msgid) {
        try {

            db.delete("chatlog", "groupid = ? ", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 删除聊天记录
     *
     * @param msgid
     */
    public void deletechatlog(String msgid) {
        db.delete("chatlog", "msgid = ?", new String[]{msgid});
    }


    /**
     * 发送消息后更新消息列表信息
     *
     * @param chatMessage
     */
    public void updateMessageForServer(ChatMessage chatMessage) {
        try {

            ContentValues cv = new ContentValues();
            switch (chatMessage.getMessageTypeEnum()) {
                case TEXT:
                    cv.put("msgcontent", chatMessage.getMsg().toString());
                    cv.put("msgstate", 1);
                    break;
                case PICTURE:
                    cv.put("msgcontent", "一张图片");
                    cv.put("msgstate", 1);
                    break;
                case AUDIO:
                    cv.put("msgcontent", "一段语音");
                    cv.put("msgstate", 1);
                    break;
                case EAUDIO:
                    cv.put("msgcontent", "E伴留言");
                    cv.put("msgstate", 1);
                    break;
            }
            cv.put("msgico", "");
            cv.put("msgmark", 0);
            cv.put("msgdate", chatMessage.getMsgdateInit());
            db.update("message", cv, "msgid = ?", new String[]{chatMessage.getMessageid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public Contacts getContactsForSamlple(String userid) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from friend where userid = ?",
                    new String[]{userid});
            if (cursor.getCount() == 0)
                return null;
            cursor.moveToNext();
            Contacts contacts = new Contacts();
            contacts.setId(cursor.getString(0));
            contacts.setNickname(cursor.getString(2));
            contacts.setNickimgurl(cursor.getString(1));
            contacts.setNameRemark(cursor.getString(3));
            contacts.setAreaName(cursor.getString(5));
            cursor.close();
            return contacts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Contacts getContactsForPhoneBook(String userid) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from T_UserInfo where USER_ID = ?",
                    new String[]{userid});
            if (cursor.getCount() == 0)
                return null;
            cursor.moveToNext();
            Contacts contacts = new Contacts();
            contacts.setId(cursor.getString(0));
            contacts.setNickname(cursor.getString(3));
            contacts.setNickimgurl(cursor.getString(2));
            contacts.setNameRemark("");
            contacts.setAreaName("");
            cursor.close();
            return contacts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 更新消息通知列表标记
     *
     * @param msgid
     * @param mark
     */
    public void updateMessageList(String msgid, int mark) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgmark", mark);
            db.update("message", cv, "msgid = ?", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 更新消息标题
     *
     * @param msgid
     * @param title
     */
    public void updateMessageList(String msgid, String title) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgtitle", title);
            db.update("message", cv, "msgid = ?", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public void updateMessageList(String msgid, int istop, int disturb) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("isdisturb", disturb);
            cv.put("istop", istop);
            db.update("message", cv, "msgid = ?", new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 清除所有暂存消息通知
     */
    public void delMessage() {
        try {

            db.delete("message", null, null);
            db.delete("weapp_detail", null, null);
            db.delete("weapp", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 删除群组信息表
     */
    public void deleteGroupInfo() {
        db.delete("groupinfo", null, null);
    }

    public void deleteGroupInfo(String groupid) {
        db.delete("groupinfo", "groupid = ?", new String[]{groupid});
    }


    /**
     * 获取聊天日志
     *
     * @param key
     * @return
     */
    public Cursor getChatlogLogforgroupid(String key, String groupid) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from chatlog where content like ? and  groupid = ? and msgtype <> 99  order by 6 desc",
                    new String[]{"%" + key + "%", groupid});
            if (cursor.getCount() == 0)
                return null;
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除群组成员
     */
    public void deleteGroupMemberInfo() {
        db.delete("groupmember", null, null);
    }

    public void deleteGroupMemberInfo(String userid) {
        db.delete("groupmember", "memberid = ?", new String[]{userid});
    }

    public void deleteGroupMemberInfoForGroupid(String groupid) {
        db.delete("groupmember", "groupid = ?", new String[]{groupid});
    }

    /**
     * 插入群信息
     */
    public void insertGroupInfo(String groupid, String groupname, String groupico, String groupadmin) {
        ContentValues cv = new ContentValues();
        cv.put("groupid", groupid);
        cv.put("groupname", groupname);
        cv.put("groupico", groupico);
        cv.put("groupadmin", groupadmin);

        db.insert("groupinfo", null, cv);
    }

    /**
     * 插入成员信息
     *
     * @param groupid
     * @param membername
     * @param memberid
     * @param memberico
     */
    public void insertGroupMemberInfo(String groupid, String membername, String memberid, String memberico) {
        ContentValues cv = new ContentValues();
        cv.put("groupid", groupid);
        cv.put("membername", membername);
        cv.put("memberid", memberid);
        cv.put("memberico", memberico);
        db.insert("groupmember", null, cv);
    }

    /**
     * 获取群信息
     *
     * @return
     */
    public Cursor getAllGroupInfo() {
        try {
            Cursor cursor = db.rawQuery("select * from groupinfo ", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取成员信息
     *
     * @return
     */
    public Cursor getAllGroupMemberInfo(String memberid) {
        try {
            Cursor cursor = db.rawQuery("select * from groupmember where memberid = ? ", new String[]{memberid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 查询群名称信息
     *
     * @param groupid
     * @return
     */
    public String getGroupName(String groupid) {
        String[] ss = getGroupAllName(groupid).split(",");
        if (ss.length == 0 || ss.length == 1)
            return getGroupAllName(groupid);
        ss = getGroupAllNamewithsplet(groupid).split(",");
        return "群聊(" + String.valueOf(ss.length) + ")";
    }

    /**
     * 查询群名称信息
     *
     * @param groupid
     * @return
     */
    public String getGroupAllName(String groupid) {
        Cursor cursor = db.rawQuery("select groupname from groupinfo where groupid = ?", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }

    /**
     * 获得名称带逗号
     *
     * @param groupid
     * @return
     */
    public String getGroupAllNamewithsplet(String groupid) {
        Cursor cursor = db.rawQuery("select membername from groupmember where groupid = ?", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        String s = "";

        while (cursor.moveToNext()) {
            s += cursor.getString(0) + ",";
        }
        s = s.substring(0, s.length() - 1);
        cursor.close();
        return s;
    }


    /**
     * 获得群成员昵称
     *
     * @param memberid
     * @param groupid
     * @return
     */
    public String getGroupNickName(String memberid, String groupid) {
        Cursor cursor = db.rawQuery("select membername from groupmember where groupid = ? and  memberid = ?", new String[]{groupid, memberid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }


    /**
     * 获得群管理员
     *
     * @param groupid
     * @return
     */
    public String getGroupAdmin(String groupid) {
        Cursor cursor = db.rawQuery("select groupadmin from groupinfo where groupid = ? ", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }


    /**
     * 得到群头像
     *
     * @param groupid
     * @return
     */
    public String getGroupNickImg(String groupid) {
        Cursor cursor = db.rawQuery("select groupico from groupinfo where groupid = ? ", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }


    /**
     * 获取成员信息
     *
     * @param groupid
     * @return
     */
    public Cursor getGroupMemberList(String groupid) {
        Cursor cursor = db.rawQuery("select * from groupmember where groupid = ? ", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    /**
     * 更新群组名称
     *
     * @param groupid
     * @param groupname
     */
    public void updateGroupName(String groupid, String groupname) {
        ContentValues cv = new ContentValues();
        cv.put("groupname", groupname);
        db.update("groupinfo", cv, "groupid = ?", new String[]{groupid});

        cv = new ContentValues();
        cv.put("msgtitle", groupname);
        db.update("message", cv, "msgid = ? ", new String[]{groupid});


    }


    /**
     * 更新群头像
     *
     * @param groupid
     * @param img
     */
    public void updateGroupNickImg(String groupid, String img) {
        ContentValues cv = new ContentValues();
        cv.put("groupico", img);
        db.update("groupinfo", cv, "groupid = ?", new String[]{groupid});
    }


    /**
     * 更新管理员
     *
     * @param groupid
     * @param admin
     */
    public void updateGroupAdminId(String groupid, String admin) {
        ContentValues cv = new ContentValues();
        cv.put("groupadmin", admin);
        db.update("groupinfo", cv, "groupid = ?", new String[]{groupid});
    }

    /**
     * 修改群昵称
     *
     * @param groupid
     * @param groupnickname
     */
    public void updateGroupNickName(String groupid, String groupnickname, String userid) {
        ContentValues cv = new ContentValues();
        cv.put("membername", groupnickname);
        db.update("groupmember", cv, "groupid = ? and memberid = ?", new String[]{groupid, userid});
    }


    /**
     * 获取群成员数量
     *
     * @param groupid
     * @return
     */
    public int getGroupMembers(String groupid) {
        Cursor cursor = db.rawQuery("select count(*) from groupmember where groupid = ?", new String[]{groupid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        }
        cursor.moveToNext();
        int i = cursor.getInt(0);
        cursor.close();
        return i;
    }


    /**
     * 删除亲情号码列表
     */
    public void deleteFamilyList() {
        db.delete("familylist", null, null);
    }

    /**
     * 删除亲情好友
     *
     * @param userid
     */
    public void deleteFamilyList(String userid) {
        db.delete("familylist", "userid = ?", new String[]{userid});
    }

    /**
     * 插入亲情号码
     *
     * @param userid
     */
    public void insertFamilyList(String userid) {
        ContentValues cv = new ContentValues();
        cv.put("userid", userid);
        db.insert("familylist", null, cv);
    }


///////////////////////////////企业通讯录

    /**
     * 清除通讯里信息
     */
    public void deletePhoneBook() {
        db.delete("T_ORG", null, null);
        db.delete("T_DEPT", null, null);
        db.delete("T_UserInfo", null, null);
    }

    /**
     * 插入单位数据
     *
     * @param jsonObject
     */
    public void insertOrg(JSONObject jsonObject) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("ORG_ID", jsonObject.getString("orgId"));
            cv.put("ORG_NAME", jsonObject.getString("orgName"));
            cv.put("PY", Common.getPingYin(jsonObject.getString("orgName")));
            cv.put("P_ORG_ID", jsonObject.getString("pOrgId"));
            cv.put("DISP_SN", jsonObject.getInt("dispSn"));
            db.insert("T_ORG", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getOrgName(String orgid) {
        Cursor cursor = db.rawQuery("select ORG_NAME from T_ORG where ORG_ID = ?", new String[]{orgid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String r = cursor.getString(0);
        cursor.close();
        return r;
    }


    /**
     * 获取主单位信息 ID和名称
     *
     * @return
     */
    public Map<String, String> getMainOrg() {
        Cursor cursor = db.rawQuery("select ORG_ID,ORG_NAME from T_ORG where P_ORG_ID = \"\" ", null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToNext();
        Map<String, String> map = new Hashtable<>();
        map.put("orgid", cursor.getString(0));
        map.put("orgname", cursor.getString(1));
        cursor.close();
        return map;
    }


    /**
     * 根据orgid获取单位子信息
     *
     * @param orgid
     * @return
     */
    public List<Map<String, String>> LoadChildOrgForOrg(String orgid) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_ORG where P_ORG_ID = ? order by DISP_SN asc", new String[]{orgid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pid", cursor.getString(2));
            map.put("state", "1");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }

    public List<Map<String, String>> LoadChildOrgForSearch(String key) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_ORG where ORG_NAME like ? or PY like ?",
                new String[]{"%" + key + "%", "%" + key + "%"});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pid", cursor.getString(2));
            map.put("state", "1");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }


    /**
     * 根据orgID 获取部门子信息
     *
     * @param orgid
     * @return
     */
    public List<Map<String, String>> LoadChildDeptForOrg(String orgid) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_DEPT where ORG_ID = ? and p_dept_id=? order by DISP_SN asc", new String[]{orgid, ""});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pid", cursor.getString(2));
            map.put("state", "2");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }


    public List<Map<String, String>> LoadChildDeptForDept(String deptid) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_DEPT where  p_dept_id=? order by DISP_SN asc", new String[]{deptid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pid", cursor.getString(2));
            map.put("state", "2");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }


    public List<Map<String, String>> LoadChildDeptForSearch(String key) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_DEPT where PY like ? or DEPT_NAME like ? ",
                new String[]{"%" + key + "%", "%" + key + "%"});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pid", cursor.getString(2));
            map.put("state", "2");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }

    /**
     * 根据部门选择人
     *
     * @param deptid
     * @return
     */
    public List<Map<String, String>> LoadChildUserForOrg(String deptid) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_UserInfo where DEPT_ID = ?  ", new String[]{deptid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pic", cursor.getString(2));
            map.put("deptid", cursor.getString(4));
            map.put("orgid", cursor.getString(5));
            map.put("nickname", cursor.getString(3));
            map.put("gender", cursor.getString(6));
            map.put("phone", cursor.getString(9));
            map.put("state", "3");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }


    /**
     * 根据部门选择人
     *
     * @param key
     * @return
     */
    public List<Map<String, String>> LoadChildUserForSearch(String key) {
        List<Map<String, String>> mapList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from T_UserInfo where PY like ? or FPY like  ? or NICK_NAME like  ? ", new String[]{
                "%" + key + "%", "%" + key + "%", "%" + key + "%"});
        if (cursor.getCount() == 0) {
            cursor.close();
            return mapList;
        }

        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new Hashtable<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("pic", cursor.getString(2));
            map.put("deptid", cursor.getString(4));
            map.put("orgid", cursor.getString(5));
            map.put("nickname", cursor.getString(3));
            map.put("gender", cursor.getString(6));
            map.put("state", "3");
            mapList.add(map);
        }
        cursor.close();
        return mapList;
    }

    public Cursor LoadPhoneBookUserForSearch(String key) {
        Cursor cursor = db.rawQuery("select * from T_UserInfo where PY like ? or FPY like  ? or NICK_NAME like  ? ", new String[]{
                "%" + key + "%", "%" + key + "%", "%" + key + "%"});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor queryPIDForDeptid(String id) {
        Cursor cursor = db.rawQuery("select * from T_DEPT where dept_id = ? ", new String[]{id});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor queryPIDForOrgid(String id) {

        Cursor cursor = db.rawQuery("select * from T_ORG where org_id = ? ", new String[]{id});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * 插入部门数据
     *
     * @param jsonObject
     */
    public void insertDept(JSONObject jsonObject) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("DEPT_ID", jsonObject.getString("deptId"));
            cv.put("DEPT_NAME", jsonObject.getString("deptName"));
            cv.put("PY", Common.getPingYin(jsonObject.getString("deptName")));
            cv.put("P_DEPT_ID", jsonObject.getString("pDeptId"));
            cv.put("ORG_ID", jsonObject.getString("orgId"));
            cv.put("DISP_SN", jsonObject.getInt("dispSn"));
            db.insert("T_DEPT", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getDeptName(String dempid) {
        Cursor cursor = db.rawQuery("select DEPT_NAME from T_DEPT where DEPT_ID = ?", new String[]{dempid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String r = cursor.getString(0);
        cursor.close();
        return r;
    }


    /**
     * 插入通讯里用户
     *
     * @param jsonObject
     */
    public void insertUserInfo(JSONObject jsonObject) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("USER_ID", jsonObject.getString("userId"));
            cv.put("USER_NAME", jsonObject.getString("userName"));
            cv.put("PY", Common.getPingYin(jsonObject.getString("name")));
            cv.put("FPY", Common.getFirstSpell(jsonObject.getString("name")));
            if (jsonObject.isNull("picId"))
                cv.put("PIC_ID", "");
            else
                cv.put("PIC_ID", jsonObject.getString("picId"));
            cv.put("NICK_NAME", jsonObject.getString("name"));
            cv.put("DEPT_ID", jsonObject.getString("deptId"));
            cv.put("ORG_ID", jsonObject.getString("orgId"));
            cv.put("GENDER", jsonObject.getString("gender"));
            if (jsonObject.isNull("phone"))
                cv.put("phone", "");
            else
                cv.put("phone", jsonObject.getString("phone"));
            db.insert("T_UserInfo", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void deleteUserInfo() {
        db.delete("T_UserInfo", null, null);
    }

    public String getPhoneBookImg(String userid) {
        Cursor cursor = db.rawQuery("select PIC_ID from T_UserInfo where USER_ID = ?", new String[]{userid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String r = cursor.getString(0);
        cursor.close();
        return r;
    }


    /**
     * 获得部门中得所有成员
     *
     * @param deptid
     * @return
     */
    public Cursor LoadContactsForDept(String deptid) {
        Cursor cursor = db.rawQuery("select * from T_UserInfo where DEPT_ID = ? ", new String[]{deptid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    public Cursor getPhoneBookContactsInfo(String userid) {
        Cursor cursor = db.rawQuery("select * from T_UserInfo where USER_ID= ? ", new String[]{userid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    //weapp

    /**
     * 插入订阅的微应用信息
     *
     * @param jsonObject
     */
    public void insertWeApp(JSONObject jsonObject) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("unsubEnable", jsonObject.getString("unsubEnable"));
            cv.put("menuEnable", jsonObject.getString("menuEnable"));
            cv.put("appName", jsonObject.getString("appName"));
            cv.put("appIndex", jsonObject.getString("appIndex"));
            cv.put("appId", jsonObject.getString("appId"));
            cv.put("appCode", jsonObject.getInt("appCode"));
            cv.put("chatEnable", jsonObject.getInt("chatEnable"));
            cv.put("appType", jsonObject.getInt("appType"));
            cv.put("defSubType", jsonObject.getInt("defSubType"));
            cv.put("remindType", jsonObject.getInt("remindType"));
            cv.put("appIco", jsonObject.getString("appLogo"));
            db.insert("weapp", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteWeApp() {
        db.delete("weapp", null, null);
    }


    public String getWeAppName(String appid) {
        Cursor cursor = db.rawQuery("select appName from weapp where appId = ?", new String[]{appid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String r = cursor.getString(0);
        cursor.close();
        return r;
    }

    public String getWeAppIco(String appid) {
        Cursor cursor = db.rawQuery("select appIco from weapp where appId = ?", new String[]{appid});
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        cursor.moveToNext();
        String r = cursor.getString(0);
        cursor.close();
        return r;
    }


    /**
     * 插入系统公告详细信息
     *
     * @param from
     * @param msgid
     * @param body
     * @param title
     * @param content
     */
    public void insertWeAppDetail(String from, String msgid, String body, String title, String content, String senddt,
                                  int weapptype) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("weapp_from", from);
            cv.put("msgid", msgid);
            cv.put("body", body);
            cv.put("title", title);
            cv.put("content", content);
            cv.put("senddt", senddt);
            cv.put("weapptype", weapptype);
            db.insert("weapp_detail", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获得微应用信息
     *
     * @param from
     * @return
     */
    public Cursor getWeAppData(String from) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from weapp_detail where weapp_from = ? order by 6 asc", new String[]{from});
            if (cursor.getCount() == 0)
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }


}
