package com.suypower.stereo.suypowerview.Chat;

import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.StringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/8/8.
 */
public class ChatMsgJson {


    private String from = BaseUserInfo.getBaseUserInfo().getUserId();
    private String to = "";
    private ChatMessage.MessageTypeEnum messageTypeEnum;
    private String atuser = "";
    private List<String> atUserList =new ArrayList<>();
    private JSONObject body;
    private JSONObject mainjson;
    private String sendtime;
    private String msgid;
    private Boolean noDisturb = false;
    private Boolean istop=false;
    private int ope;

    public Boolean getIstop() {
        return istop;
    }

    public void setIstop(Boolean istop) {
        this.istop = istop;
    }

    public Boolean getNoDisturb() {
        return noDisturb;
    }

    public String getMsgid() {
        return msgid;
    }

    public String getSendtime() {
        return sendtime;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getAtUserList() {
        return atUserList;
    }

    public int getOpe() {
        return ope;
    }

    public void setOpe(int ope) {
        this.ope = ope;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public ChatMessage.MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }

    public void setMessageTypeEnum(ChatMessage.MessageTypeEnum messageTypeEnum) {
        this.messageTypeEnum = messageTypeEnum;
    }

    public String getAtuser() {
        return atuser;
    }

    public void setAtuser(String atuser) {
        this.atuser = atuser;
        try {
            mainjson.put("atUsers", atuser);
        }
        catch (Exception e)
        {e.printStackTrace();}
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }


    /**
     * 根据json创建对象
     * @param jsonObject
     */
    public ChatMsgJson(JSONObject jsonObject) {

        try {
            this.from = jsonObject.getString("from");
            this.to = jsonObject.getString("to");
            this.ope = Integer.valueOf(jsonObject.getString("ope"));
            this.sendtime = jsonObject.getString("sendTime");
            LibConfig.saveMsgDateLong(this.sendtime);
            this.msgid = jsonObject.getString("msgId");
            this.noDisturb = jsonObject.getBoolean("noDisturb");
            this.istop = jsonObject.getBoolean("top");

            if (!jsonObject.isNull("atUsers"))
            {
                String s = jsonObject.getString("atUsers");
                String[] ss =  s.split(",");
                for (int i =0;i<ss.length;i++)
                {
                    atUserList.add(ss[i]);
                }
            }

            body = jsonObject.getJSONObject("body");
            if (jsonObject.getString("type").equals("1"))
                this.messageTypeEnum= ChatMessage.MessageTypeEnum.TEXT;
            if (jsonObject.getString("type").equals("2"))
                this.messageTypeEnum= ChatMessage.MessageTypeEnum.PICTURE;
            if (jsonObject.getString("type").equals("3"))
                this.messageTypeEnum= ChatMessage.MessageTypeEnum.AUDIO;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 单聊
     *
     * @param to
     * @param ope
     * @param messageTypeEnum
     */
    public ChatMsgJson(String to, int ope, ChatMessage.MessageTypeEnum messageTypeEnum) {
        mainjson = new JSONObject();
        try {
            mainjson.put("from", from);
            this.to = to;
            this.ope = ope;
            this.messageTypeEnum = messageTypeEnum;
            mainjson.put("to", to);
            mainjson.put("ope", String.valueOf(ope));
            switch (messageTypeEnum) {
                case TEXT:
                    mainjson.put("type", String.valueOf(1));
                    break;
                case PICTURE:
                    mainjson.put("type", String.valueOf(2));
                    break;
                case AUDIO:
                    mainjson.put("type", String.valueOf(3));
                    break;
                case VIDEO:
                    mainjson.put("type", String.valueOf(4));
                    break;
                case GPSLOACTION:
                    mainjson.put("type", String.valueOf(5));
                    break;
                case SHARE:
                    mainjson.put("type", String.valueOf(6));
                    break;
            }
            body = new JSONObject();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 设置内容
     *
     * @param s
     */
    public void setBodyMsg(String s) {
        try {
            if (messageTypeEnum == ChatMessage.MessageTypeEnum.TEXT) {
                body.put("msg", s);
            } else if (messageTypeEnum == ChatMessage.MessageTypeEnum.PICTURE ||
                    messageTypeEnum == ChatMessage.MessageTypeEnum.AUDIO ||
                    messageTypeEnum == ChatMessage.MessageTypeEnum.VIDEO
                    ) {
                body.put("media", s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 返回消息体
     *
     * @return
     * @throws Exception
     */
    public JSONObject getJson() {
        try {
            mainjson.put("body", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainjson;
    }


}
