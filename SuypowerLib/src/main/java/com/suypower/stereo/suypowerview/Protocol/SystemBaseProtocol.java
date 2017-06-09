package com.suypower.stereo.suypowerview.Protocol;

import com.suypower.stereo.suypowerview.Base.LibConfig;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Stereo on 16/7/18.
 */
public class SystemBaseProtocol {

    //广播
    public final static String BroadCastFriend = "BoradCast.Friend";
    public final static String BroadCastUpdateSysMsg = "BoradCast.UpdateSysMsg";

    public static final int CREATEGROUP = 2;//创建群聊
    public static final int PULLINGROUP = 3;//有人加入群
    public static final int KICIOUTGROUP = 4;//删除人
    public static final int OTHEREXITGROUP = 5;//有人退出群了
    public static final int UPDATEGROUPADMIN = 6;//更新群管理员
    public static final int ADDFRIEND = 7;//添加好友
    public static final int FRIENDACCEPT = 8;// 验证好友通过
    public static final int DELETEFRIEND = 9;// 验证好友通过
    public static final int UPDATEGROUPNAME = 12;//修改群名称
    public static final int UPDATEGROUPNICKNAME = 13;//修改群昵称
    public static final int PULLINGROUPSELF = 14;//自己被拉入
    public static final int EBNOTICESALERTINFO = 15;//EB报警
    public static final int FAMILYADD = 16;//亲情号码
    public static final int EBNOTICES17 = 17;//EB报警
    private String sendTime = "";//发送时间
    private int noticeType = 0;//提醒类型
    private String noticeId = "";//提醒ID
    private JSONObject body;


    protected JSONObject jsonObject;


    /**
     * 解析接收到的信息
     *
     * @param json
     * @throws Exception
     */
    public SystemBaseProtocol(String json) throws Exception {

        jsonObject = new JSONObject(json);
        sendTime = jsonObject.getString("sendTime");
        noticeId =  jsonObject.getString("noticeId");
        noticeType = Integer.valueOf(jsonObject.getString("noticeType"));
        body = jsonObject.getJSONObject("body");
        LibConfig.saveMsgDateLong(sendTime);


    }
    public SystemBaseProtocol(JSONObject jsonObject) throws Exception {

        sendTime = jsonObject.getString("sendTime");
        noticeId =  jsonObject.getString("noticeId");
        noticeType = Integer.valueOf(jsonObject.getString("noticeType"));
        body = jsonObject.getJSONObject("body");

        LibConfig.saveMsgDateLong(sendTime);


    }

    /**
     * 返回对应的处理对象
     *
     * @return
     */
    public Object getProtocol() {
        try {
            GroupProtocol groupProtocol;
            switch (noticeType) {
                case ADDFRIEND:
                case FRIENDACCEPT:
                case DELETEFRIEND:

                    FriendProtocol addFriendProtocol = new FriendProtocol(this);
                    return addFriendProtocol;
                case CREATEGROUP:
                case UPDATEGROUPNAME:
                case UPDATEGROUPNICKNAME:
                case KICIOUTGROUP:
                case PULLINGROUP:
                case PULLINGROUPSELF:
                case OTHEREXITGROUP:
                case UPDATEGROUPADMIN:
                    groupProtocol=new GroupProtocol(this);
                    return groupProtocol;
                case EBNOTICESALERTINFO:
                case EBNOTICES17:
                    EBNoticesProtocol ebNoticesProtocol=new EBNoticesProtocol(this);
                    return ebNoticesProtocol;
                case FAMILYADD:
                    FamilyProtocol familyProtocol=new FamilyProtocol(this);
                    return familyProtocol;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getSendTime() {
        return sendTime;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public JSONObject getBody() {
        return body;
    }
}
