package com.suypower.stereo.suypowerview.Protocol;

import com.suypower.stereo.suypowerview.Chat.ChatMsgJson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/8/9.
 */
public class WeAppProtocol {


    public enum WeAppType {
        TEXT, PICTURANDTEXT,
    }

    private WeAppType weAppType;
    private String from;
    private String msgid;
    private String sendtime;
    private Boolean noDisturb;
    private Boolean top;
    private JSONObject bodyJson;
    private JSONArray bodyArry;
    private WeAppBody weAppBody;
    private WeAppBody[] weAppBodies;
    private int Bodys;
    private int mode;


    public WeAppType getWeAppType() {
        return weAppType;
    }

    public int getWeAppTypeInt()
    {
        switch (weAppType)
        {
            case TEXT:
                return 1;
            case PICTURANDTEXT:
                return 2;
        }
        return 1;
    }
    public String getFrom() {
        return from;
    }

    public String getMsgid() {
        return msgid;
    }

    public String getSendtime() {
        return sendtime;
    }

    public Boolean getNoDisturb() {
        return noDisturb;
    }

    public Boolean getTop() {
        return top;
    }

    public JSONArray getBodyArry() {
        return bodyArry;
    }

    public JSONObject getBodyJson() {
        return bodyJson;
    }

    public int getBodys() {
        return Bodys;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public WeAppProtocol(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            from = jsonObject.getString("from");
            msgid = jsonObject.getString("msgId");
            sendtime = jsonObject.getString("sendTime");
            noDisturb = jsonObject.getBoolean("noDisturb");
            top = jsonObject.getBoolean("top");

            if (jsonObject.getString("type").equals("1")) {
                weAppType = WeAppType.TEXT;
                bodyJson = jsonObject.getJSONObject("body");
                weAppBody = new WeAppBody(bodyJson,weAppType);
            } else if (jsonObject.getString("type").equals("2")) {
                weAppType = WeAppType.PICTURANDTEXT;
                bodyArry = jsonObject.getJSONArray("body");
                Bodys = bodyArry.length();
                weAppBodies = new WeAppBody[Bodys];

                for (int i=0;i<Bodys;i++)
                {
                    JSONObject jsonObject1 = bodyArry.getJSONObject(i);
                    weAppBodies[i] = new WeAppBody(jsonObject1,weAppType);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public WeAppProtocol(JSONObject jsonObject) {
        try {

            from = jsonObject.getString("from");
            msgid = jsonObject.getString("msgId");
            sendtime = jsonObject.getString("sendTime");
            noDisturb = jsonObject.getBoolean("noDisturb");
            top = jsonObject.getBoolean("top");

            if (jsonObject.getString("type").equals("1")) {
                weAppType = WeAppType.TEXT;
                bodyJson = jsonObject.getJSONObject("body");
                weAppBody = new WeAppBody(bodyJson,weAppType);
            } else if (jsonObject.getString("type").equals("2")) {
                weAppType = WeAppType.PICTURANDTEXT;
                bodyArry = jsonObject.getJSONArray("body");
                Bodys = bodyArry.length();
                weAppBodies = new WeAppBody[Bodys];

                for (int i=0;i<Bodys;i++)
                {
                    JSONObject jsonObject1 = bodyArry.getJSONObject(i);
                    weAppBodies[i] = new WeAppBody(jsonObject1,weAppType);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class WeAppBody {

        private String title,content,uri,media;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return uri;
        }

        public String getMedia() {
            return media;
        }

        public WeAppBody(JSONObject jsonObject,WeAppType weAppType) {
            try {
                switch (weAppType)
                {
                    case TEXT:
                        title = jsonObject.getString("title");
                        content = jsonObject.getString("content");
                        uri = jsonObject.getString("uri");
                        break;
                    case PICTURANDTEXT:
                        title = jsonObject.getString("title");
                        media = jsonObject.getString("media");
                        uri = jsonObject.getString("uri");
                        break;
                }

            }
            catch (Exception e)
            {e.printStackTrace();}

        }



    }

}
