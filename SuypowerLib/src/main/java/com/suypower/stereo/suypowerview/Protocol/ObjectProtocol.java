package com.suypower.stereo.suypowerview.Protocol;

import org.json.JSONObject;

/**
 * Created by Stereo on 16/7/18.
 */
public class ObjectProtocol extends BaseProtocol {

    private JSONObject body;
    private String mediaid;


    public ObjectProtocol(String json)throws Exception
    {
        super(json);
        try
        {
            body = jsonObject.getJSONObject("body");
            mediaid = body.getString("media");


        }catch (Exception e)
        {e.printStackTrace();}
    }

    public ObjectProtocol() throws Exception {
        super();
        body= new JSONObject();
    }

    public ObjectProtocol(int MsgType,int Opetype) throws Exception {
        this();
        this.setMsgType(MsgType);
        this.setOpeType(Opetype);
    }

    /**
     * 设置多媒体内容
     * @param mediaid
     */
    public void setMedia(String mediaid) {
        try {
            this.mediaid=mediaid;
            body.put("media", mediaid);
            jsonObject.put("body",body);
        }
        catch (Exception e)
        {e.printStackTrace();}
    }

    public String getMediaid()
    {
        return mediaid;
    }



}
