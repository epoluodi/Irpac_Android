package com.suypower.stereo.suypowerview.DataClass;

import java.io.Serializable;

/**
 * Created by Stereo on 16/3/29.
 */
public class Contacts implements Serializable {


    //    "areaCode" : "110102",
//            "status" : null,
//            "score" : "0",
//            "phone" : "13584015252",
//            "cityName" : "市辖区",
//            "detailAddr" : "和呵呵的和呵呵符合不的和呵呵符合哈哈哈的",
//            "idCode" : "321322199112056544",
//            "userId" : "ce58946a-4d92-11e6-9b9a-0025118f5e64",
//            "birthday" : "2016-01-02",
//            "picId" : "8e099de8-df26-4b50-bdb8-f4ce44240f77",
//            "provinceName" : "北京市",
//            "districtName" : "西城区",
//            "nickname" : "吞吞吐吐",
//            "email" : "yyyy6@qq.com",
//            "name" : "王",
//            "gender" : "1"


    private String id;
    private String realname = "";
    private String nickname = "";
    private String phone = "";
    private String nickimgurl;
    private String PY;
    private String email = "";
    private String score = "";
    private String areaCode = "";
    private String cityName = "";
    private String detailAddr = "";
    private String idCode = "";
    private String birthday = "";
    private String provinceName = "";
    private String districtName = "";
    private String gender = "";
    private String nameRemark = "";
    private String areaName = "";
    private String firstLetter = "";
    private String deptId="";
    private String deptName="";
    private String orgId="";


    public String getName()
    {
        if (nameRemark.equals(""))
            return nickname;
        else
            return nameRemark;
    }
    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getNameRemark() {
        return nameRemark;
    }

    public void setNameRemark(String nameRemark) {
        this.nameRemark = nameRemark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickimgurl() {
        return nickimgurl;
    }

    public void setNickimgurl(String nickimgurl) {
        this.nickimgurl = nickimgurl;
    }

    public String getPY() {
        return PY;
    }

    public void setPY(String PY) {
        this.PY = PY;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDetailAddr() {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
