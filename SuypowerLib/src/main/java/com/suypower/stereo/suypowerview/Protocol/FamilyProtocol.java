package com.suypower.stereo.suypowerview.Protocol;

/**
 * Created by Stereo on 16/7/29.
 */
public class FamilyProtocol {


    private String nickName = "";
    private String picId = "";
    private String userId = "";
    private String gender="";
    private String org="";
    private String dept="";
    private String phone="";






    private SystemBaseProtocol systemBaseProtocol;


    public FamilyProtocol(SystemBaseProtocol systemBaseProtocol) throws Exception {
        this.systemBaseProtocol = systemBaseProtocol;



        nickName = systemBaseProtocol.getBody().getString("nickName");
        picId = systemBaseProtocol.getBody().getString("picId");
        userId = systemBaseProtocol.getBody().getString("userId");
        gender = systemBaseProtocol.getBody().getString("gender");
        org = systemBaseProtocol.getBody().getString("org");
        dept = systemBaseProtocol.getBody().getString("dept");
        phone = systemBaseProtocol.getBody().getString("phone");

    }


    public SystemBaseProtocol getSystemBaseProtocol() {
        return systemBaseProtocol;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPicId() {
        return picId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
