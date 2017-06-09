package com.suypower.stereo.suypowerview.Common;

import android.graphics.Bitmap;

/**
 * Created by Stereo on 16/7/18.
 */
public class BaseUserInfo {

    public enum UserRight
    {
        OLDMAN,MANGER,
    }

    private String username = "";
    private String userpwd = "";
    private String lginname = "";
    private String token = "";
    private String sex = "";
    private String photo = "";
    private String email = "";
    private String mobile = "";
    private String userId = "";
    private String qrcode="";
    private String photoUrl="";
    private String departmentname="";
    private String unitName="";
    private String deptId="";
    private String orgId="";
    private UserRight userRight;
    private String gh;
    private String officeName;


    public String getGh() {
        return gh;
    }

    public void setGh(String gh) {
        this.gh = gh;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    protected static BaseUserInfo baseUserInfo;

    public static void Init(String username,String userpwd)
    {

        try {
            baseUserInfo = new BaseUserInfo(username,userpwd);
        }
        catch (Exception e)
        {e.printStackTrace();
        }

    }



    /**
     * 单例模式s
     *
     * @return
     */
    public static BaseUserInfo getBaseUserInfo() {
        if (baseUserInfo == null)
            return null;
        return baseUserInfo;
    }


    public static void setBaseUserInfo(BaseUserInfo baseUserInfo) {
        BaseUserInfo.baseUserInfo = baseUserInfo;
    }

    /**
     * 初始化
     */
    protected static void Init(BaseUserInfo _baseUserInfo) {
        baseUserInfo = _baseUserInfo;
    }

    public BaseUserInfo() {
    }


    public BaseUserInfo(String username, String userpwd) {

        this.lginname = username;
        this.userpwd = userpwd;

    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLginname() {
        return lginname;
    }

    public void setLginname(String lginname) {
        this.lginname = lginname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public UserRight getUserRight() {
        return userRight;
    }

    public void setUserRight(UserRight userRight) {
        this.userRight = userRight;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
