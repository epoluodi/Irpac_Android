package com.suypower.stereo.suypowerview.Protocol;

import com.suypower.stereo.suypowerview.DataClass.Contacts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stereo on 16/7/29.
 */
public class GroupProtocol {


    private String groupid = "";
    private String groupName = "";
    private String groupAvatar = "";
    private String groupadminid = "";
    private String groupnickname = "";
    private String groupuserid = "";
    private String opUserid= "";
    private String inviteUserId="";
    private JSONArray array;
    private String kickUserId;

    public String getInviteUserId() {
        return inviteUserId;
    }

    private List<Map<String, String>> menbers = null;


    private SystemBaseProtocol systemBaseProtocol;


    public GroupProtocol(SystemBaseProtocol systemBaseProtocol) throws Exception {
        this.systemBaseProtocol = systemBaseProtocol;

        if (systemBaseProtocol.getNoticeType() == SystemBaseProtocol.CREATEGROUP) {


            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupAvatar = systemBaseProtocol.getBody().getString("groupAvatar");
            groupadminid = systemBaseProtocol.getBody().getString("userId");
            menbers = new ArrayList<>();
            try {
                JSONArray jsonArray = systemBaseProtocol.getBody().getJSONArray("members");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String,String> map =new HashMap<>();
                    map.put("picId",jsonObject.getString("picId"));
                    map.put("userId",jsonObject.getString("userId"));
                    map.put("nickName",jsonObject.getString("nickName"));
                    menbers.add(map);
                }
            }
            catch (Exception e)
            {e.printStackTrace();}


            return;
        }


        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.UPDATEGROUPNAME)
        {
            groupName = systemBaseProtocol.getBody().getString("groupName");
            groupid = systemBaseProtocol.getBody().getString("groupId");
            opUserid=systemBaseProtocol.getBody().getString("userId");
            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.UPDATEGROUPNICKNAME)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupuserid=systemBaseProtocol.getBody().getString("userId");
            groupnickname=systemBaseProtocol.getBody().getString("groupNickName");
            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.KICIOUTGROUP)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupAvatar=systemBaseProtocol.getBody().getString("groupAvatar");
            array = systemBaseProtocol.getBody().getJSONArray("kickedUserIds");
            kickUserId=systemBaseProtocol.getBody().getString("kickUserId");

            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.PULLINGROUP)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupAvatar=systemBaseProtocol.getBody().getString("groupAvatar");
            inviteUserId=systemBaseProtocol.getBody().getString("inviteUserId");
            array = systemBaseProtocol.getBody().getJSONArray("invitedUsers");
            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.PULLINGROUPSELF)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupAvatar=systemBaseProtocol.getBody().getString("groupAvatar");
            inviteUserId=systemBaseProtocol.getBody().getString("inviteUserId");
            array = systemBaseProtocol.getBody().getJSONArray("invitedUsers");
            if (systemBaseProtocol.getBody().isNull("groupName"))
                groupName=null;
            else
                groupName=systemBaseProtocol.getBody().getString("groupName");
            menbers = new ArrayList<>();
            try {
                JSONArray jsonArray = systemBaseProtocol.getBody().getJSONArray("members");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String,String> map =new HashMap<>();
                    map.put("picId",jsonObject.getString("picId"));
                    map.put("userId",jsonObject.getString("userId"));
                    map.put("nickName",jsonObject.getString("groupNickName"));
                    menbers.add(map);
                }
                for (int i=0;i<array.length();i++)
                {
                    JSONObject jsonObject = array.getJSONObject(i);
                    Map<String,String> map =new HashMap<>();
                    map.put("picId",jsonObject.getString("picId"));
                    map.put("userId",jsonObject.getString("userId"));
                    map.put("nickName",jsonObject.getString("nickName"));
                    menbers.add(map);
                }

            }catch (Exception e)
            {e.printStackTrace();}



            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.OTHEREXITGROUP)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            groupAvatar=systemBaseProtocol.getBody().getString("groupAvatar");
            groupuserid=systemBaseProtocol.getBody().getString("userId");
            return;
        }
        if (systemBaseProtocol.getNoticeType()==SystemBaseProtocol.UPDATEGROUPADMIN)
        {
            groupid = systemBaseProtocol.getBody().getString("groupId");
            return;
        }
    }


    public String getKickUserId() {
        return kickUserId;
    }

    public JSONArray getArray() {
        return array;
    }

    public String getGroupid() {
        return groupid;
    }


    public String getGroupName() {
        return groupName;
    }


    public String getGroupAvatar() {
        return groupAvatar;
    }


    public String getGroupadminid() {
        return groupadminid;
    }


    public List<Map<String, String>> getMenbers() {
        return menbers;
    }


    public String getOpUserid() {
        return opUserid;
    }

    public String getGroupuserid() {
        return groupuserid;
    }

    public String getGroupnickname() {
        return groupnickname;
    }
}
