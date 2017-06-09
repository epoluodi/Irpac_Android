package com.suypower.stereo.suypowerview.Protocol;

import org.json.JSONObject;

/**
 * Created by Stereo on 16/7/18.
 */
public class TextProtocol extends BaseProtocol {

    private JSONObject body;
    private String content;


    public TextProtocol (String json) throws Exception
    {
        super(json);
        try
        {
            body = jsonObject.getJSONObject("body");
            content = body.getString("msg");


        }catch (Exception e)
        {e.printStackTrace();}

    }

    public TextProtocol() throws Exception {
        super();
        body= new JSONObject();
    }

    public TextProtocol(int Opetype) throws Exception {
        this();
        this.setMsgType(TEXT);
        this.setOpeType(Opetype);
    }

    /**
     * 设置文本内容
     * @param content
     */
    public void setText(String content) {
        try {
            this.content=content;
            body.put("msg", content);
            jsonObject.put("body",body);
        }
        catch (Exception e)
        {e.printStackTrace();}
    }

    public String getContent()
    {
        return content;
    }


}
