package com.suypower.stereo.Irpac.System;

import com.suypower.stereo.suypowerview.Common.BaseUserInfo;

/**
 * Created by Stereo on 16/7/12.
 */
public class UserInfo extends BaseUserInfo {


    private String nickname = "";
    private long score = 0;//积分
    private String birthday = "";//生日
    private String cityname = "";
    private String detailAddr = "";
    private String provinceName = "";
    private String districtName = "";
    private String cardinfo = "";
    private String compantInfo="";



    protected static UserInfo userInfo;

    /**
     * 初始化用户对象
     */
    public static void Init(String username, String userpwd) {

        try {
            userInfo = new UserInfo();

            userInfo.setLginname(username);
            userInfo.setUserpwd(userpwd);
            baseUserInfo = userInfo;
            if (BaseUserInfo.getBaseUserInfo() == null)
                BaseUserInfo.Init("", "");
            BaseUserInfo.setBaseUserInfo(baseUserInfo);


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public String getCardinfo() {
        return cardinfo;
    }

    public void setCardinfo(String cardinfo) {
        this.cardinfo = cardinfo;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getDetailAddr() {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCompantInfo() {
        return compantInfo;
    }

    public void setCompantInfo(String compantInfo) {
        this.compantInfo = compantInfo;
    }
}


