package com.suypower.stereo.suypowerview.Chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.DB.MessageDB;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stereo on 16/4/12.
 */
public class ChatMessage {

    public static final int CHATPTP = 0;//单聊
    public static final int CHATGROUP = 1;//群聊


    public enum MessageTypeEnum {
        UNKNOW,
        TEXT,//文字聊天
        PICTURE,//图片
        AUDIO,//语音
        EAUDIO,//EB语音
        VIDEO,//视频
        GPSLOACTION,//位置信息
        SHARE,//分享
        System,//99
    }

    private String fromDevice="";
    private String uuid;
    private String msgid;
    private String messageid;
    private MessageTypeEnum messageTypeEnum;
    private SpannableString msg;
    private String mediaid;
    private String RICHTXT;
    private String msgdate;
    private String sender;
    private String senderid;
    private String nickimg;
    private Boolean IsSelf;
    private String ex1;
    private String cretedt;
    private int msgMode;
    private int msgSendState;
    private Boolean Isplay=false;
    private Boolean istop=false;
    private Boolean disturb=false;


    public Boolean getIsplay() {
        return Isplay;
    }

    public void setIsplay(Boolean isplay) {
        Isplay = isplay;
    }

    public String getFromDevice() {
        return fromDevice;
    }

    public void setFromDevice(String fromDevice) {
        this.fromDevice = fromDevice;
    }

    public String getCretedt() {
        return cretedt;
    }

    public void setCretedt(String cretedt) {
        this.cretedt = cretedt;
    }

    public String getNickimg() {
        return nickimg;
    }

    public void setNickimg(String nickimg) {
        this.nickimg = nickimg;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getEx1() {
        return ex1;
    }

    public void setEx1(String ex1) {
        this.ex1 = ex1;
    }

    public int getMsgMode() {
        return msgMode;
    }

    public void setMsgMode(int msgMode) {
        this.msgMode = msgMode;
    }

    public int getMsgType() {
        switch (getMessageTypeEnum()) {
            case TEXT:
                return 1;
            case PICTURE:
                return 2;
            case AUDIO:
                return 3;
            case System:
                return 99;
            case EAUDIO:
                return 7;
        }
        return -1;
    }

    public Boolean getIstop() {
        return istop;
    }

    public void setIstop(Boolean istop) {
        this.istop = istop;
    }

    public Boolean getDisturb() {
        return disturb;
    }

    public void setDisturb(Boolean disturb) {
        this.disturb = disturb;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getMsgSendState() {
        return msgSendState;
    }

    public void setMsgSendState(int msgSendState) {
        this.msgSendState = msgSendState;
    }

    public Boolean getSelf() {
        return IsSelf;
    }

    public void setSelf(Boolean self) {
        IsSelf = self;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }

    public void setMessageTypeEnum(MessageTypeEnum messageTypeEnum) {
        this.messageTypeEnum = messageTypeEnum;
    }

    public SpannableString getMsg() {
        return msg;
    }

    public void setMsg(String msg) {


        SpannableString spannableString=new SpannableString(msg);

        Pattern p = Pattern.compile("\\[\\w*\\]");
        Matcher m = p.matcher(msg);
        int startindex=0;
        while (m.find())
        {
            Log.i("表情",m.group());
            int i= msg.indexOf(m.group(),startindex);
            String str = msg.substring(i,i+m.group().length());
            try {
                String emojifile = Emoji.stringMapemoji.get(str);
                String emojipath = String.format("emoji/%1$s.png", emojifile);
                InputStream inputStream = Init.getContext().getAssets().open(emojipath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageSpan imageSpan = new ImageSpan(Init.getContext(), bitmap);
                spannableString.setSpan(imageSpan, i, i+m.group().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            startindex = i + m.group().length();
            Log.i("表情str",str);



        }
        this.msg = spannableString;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getRICHTXT() {
        return RICHTXT;
    }

    public void setRICHTXT(String RICHTXT) {
        this.RICHTXT = RICHTXT;
    }

    public String getMsgdateInit() {
        return msgdate;
    }

    public String getMsgdate() {
        return Common.GetSysTimeWithFormat(msgdate);
    }

    public void setMsgdate(String msgdate) {

        this.msgdate = msgdate;
    }


    /**
     * 发送系统消息
     */
    public static void sendSysMsg(String msg,String sendtime,String groupid)
    {
        ChatMessage chatMessage1=new ChatMessage();
        chatMessage1.setMessageTypeEnum(MessageTypeEnum.System);
        chatMessage1.setMsg(msg);
        chatMessage1.setMsgdate(sendtime);
        chatMessage1.setSender("系统消息");
        chatMessage1.setSenderid("100000");
        chatMessage1.setSelf(true);
        chatMessage1.setMessageid(groupid);
        chatMessage1.setCretedt(sendtime);
        chatMessage1.setMsgSendState(9);//系统信息状态
        MessageDB messageDB=new MessageDB();
        messageDB.insertChatlog(chatMessage1);
    }

    /**
     * 发送系统消息
     */
    public static void sendMsg(String msg,String sendtime,String groupid,String senderid,String sender)
    {
        ChatMessage chatMessage1=new ChatMessage();
        chatMessage1.setMessageTypeEnum(MessageTypeEnum.TEXT);
        chatMessage1.setMsg(msg);
        chatMessage1.setMsgdate(sendtime);
        chatMessage1.setSender(sender);
        chatMessage1.setSenderid(senderid);
        chatMessage1.setSelf(false);
        chatMessage1.setMessageid(groupid);
        chatMessage1.setCretedt(sendtime);
        chatMessage1.setMsgSendState(1);//系统信息状态
        MessageDB messageDB=new MessageDB();
        messageDB.insertChatlog(chatMessage1);
    }

}
