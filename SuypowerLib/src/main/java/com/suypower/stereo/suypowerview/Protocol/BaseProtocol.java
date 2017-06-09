package com.suypower.stereo.suypowerview.Protocol;

import com.suypower.stereo.suypowerview.Common.BaseUserInfo;

import org.json.JSONObject;


/**
 * Created by Stereo on 16/7/18.
 */
public class BaseProtocol {



    public static final int TEXT = 1;
    public static final int PICTURE = 2;
    public static final int VOICE = 3;
    public static final int VIDEO = 4;
    public static final int LOCATION = 5;
    public static final int SHAREMSG = 6;
    public static final int OTHERMSG = 99;

    public static final int OPE_SINGLE = 1;//单聊
    public static final int OPE_GROUP = 2;//群聊


    private String fromId = "";//发送者
    private String fromName = "";//发送者中文
    private String sendTime = "";//发送时间
    private String toId = "";//接收
    private String toName="";//接收
    private int msgType = 0;//消息类别
    private int opeType = 0;//消息类型
    private String atUser = "";//@用户

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getOpeType() {
        return opeType;
    }

    public void setOpeType(int opeType) {
        this.opeType = opeType;
    }

    public String getAtUser() {
        return atUser;
    }

    public void setAtUser(String atUser) {
        this.atUser = atUser;
        try {
            jsonObject.put("atUsers", atUser);
        }catch (Exception e)
        {e.printStackTrace();}
    }

    protected JSONObject jsonObject;


    /**
     * 解析接收到的信息
     * @param json
     * @throws Exception
     */
    public BaseProtocol(String json)throws Exception
    {
        jsonObject = new JSONObject(json);
        String ope = jsonObject.getString("ope");
        setOpeType(Integer.valueOf(ope));
        String type = jsonObject.getString("type");
        setMsgType(Integer.valueOf(type));
        setSendTime(jsonObject.getString("sendTime"));
        setFromId(jsonObject.getString("from"));
        setFromName(jsonObject.getString("fromName"));
        setToId(jsonObject.getString("to"));
        setToName(jsonObject.getString("toName"));
        if (getOpeType()==OPE_GROUP)
            setAtUser(jsonObject.getString("atUsers"));


    }

    public BaseProtocol() throws Exception {
        jsonObject = new JSONObject();
        if (BaseUserInfo.getBaseUserInfo() != null) {
            jsonObject.put("from", fromId);
            jsonObject.put("fromName", fromName);
            jsonObject.put("sendTime", sendTime);
            jsonObject.put("ope", String.valueOf(opeType));
            jsonObject.put("to", toId);
            jsonObject.put("type", String.valueOf(msgType));

        }
        else
            throw new Exception("用户对象不存在");
    }




    /**
     * 输出json对象
     *
     * @return
     */
    public JSONObject toJsonObject() {
        return jsonObject;
    }


    /**
     * 输出json对象字符串
     *
     * @return
     */
    public String toJsonString() {
        return toJsonObject().toString();
    }


}
