package com.suypower.stereo.suypowerview.Protocol;

import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Chat.ChatMsgJson;

import org.json.JSONObject;

/**
 * Created by Stereo on 16/8/9.
 */
public class BaseChatProtocol {

    private ChatMsgJson chatMsgJson=null;
    private int mode= 0;//不提示通知,1 提示通知

    public ChatMsgJson getChatMsgJson() {
        return chatMsgJson;
    }


    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public BaseChatProtocol (String json)
    {
        try {
            chatMsgJson = new ChatMsgJson(new JSONObject(json));

        }
        catch (Exception e)
        {e.printStackTrace();}
    }
    public BaseChatProtocol (JSONObject jsonObject)
    {
        try {
            chatMsgJson = new ChatMsgJson(jsonObject);

        }
        catch (Exception e)
        {e.printStackTrace();}
    }
}
