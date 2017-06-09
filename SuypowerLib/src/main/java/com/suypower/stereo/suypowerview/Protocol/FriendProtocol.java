package com.suypower.stereo.suypowerview.Protocol;

/**
 * Created by Stereo on 16/7/29.
 */
public class FriendProtocol {


    private String nickName = "";
    private String picId = "";
    private String userId = "";
    private String desc = "";
    private String status = "";
    private String releaseUserId = "";


    private SystemBaseProtocol systemBaseProtocol;


    public FriendProtocol(SystemBaseProtocol systemBaseProtocol) throws Exception {
        this.systemBaseProtocol = systemBaseProtocol;

        if (systemBaseProtocol.getNoticeType() == SystemBaseProtocol.DELETEFRIEND) {
            releaseUserId = systemBaseProtocol.getBody().getString("releaseUserId");
            return;
        }

        nickName = systemBaseProtocol.getBody().getString("nickName");
        picId = systemBaseProtocol.getBody().getString("picId");
        userId = systemBaseProtocol.getBody().getString("userId");
        if (systemBaseProtocol.getNoticeType() == SystemBaseProtocol.ADDFRIEND)
            desc = systemBaseProtocol.getBody().getString("desc");
        if (systemBaseProtocol.getNoticeType() == SystemBaseProtocol.FRIENDACCEPT)
            status = systemBaseProtocol.getBody().getString("status");
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

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
    }

    public String getReleaseUserId() {
        return releaseUserId;
    }
}
