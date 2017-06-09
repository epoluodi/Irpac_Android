package com.suypower.stereo.suypowerview.Protocol;

import org.json.JSONObject;

/**
 * Created by Stereo on 16/7/18.
 */
public class LocationProtocol extends BaseProtocol {

    private JSONObject body;

    private String title,lat,lng;


    public LocationProtocol(String json)throws Exception
    {
        super(json);
        try
        {
            body = jsonObject.getJSONObject("body");
            title = body.getString("title");
            lat = body.getString("lat");
            lng = body.getString("lng");


        }catch (Exception e)
        {e.printStackTrace();}
    }


    public LocationProtocol() throws Exception {
        super();
        body= new JSONObject();
    }

    public LocationProtocol(int Opetype) throws Exception {
        this();
        this.setMsgType(LOCATION);
        this.setOpeType(Opetype);
    }

    /**
     * 设置位置信息
     * @param title
     * @param lat
     * @param lng
     */
    public void setLocationInfo(String title,String lat,String lng) {
        try {
            this.title=title;
            this.lat=lat;
            this.lng=lng;
            body.put("title", title);
            body.put("lat", lat);
            body.put("lng", lng);
            jsonObject.put("body",body);
        }
        catch (Exception e)
        {e.printStackTrace();}
    }


    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }
}
