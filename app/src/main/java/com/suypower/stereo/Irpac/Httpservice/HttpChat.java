package com.suypower.stereo.Irpac.Httpservice;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Chat.ChatMessage;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/15.
 */
public class HttpChat extends BaseTask {


    public static final int SENDMSG = 0;//发送消息
    public static final int CREATEGROUP = 1;//创建群聊
    public static final int QUERYGROUP = 2;//查询群组信息
    public static final int UPDATEGROUPNAME = 3;//修改群名称
    public static final int UPDATEGROUPNICKNAME = 4;//修改群名称
    public static final int GROUPKICKOUT = 5;//踢人
    public static final int PULLINGROUP = 6;//追加人
    public static final int SELFEXITGROUP = 7;//自己退出
    public static final int QUERYUSERANDGROUPSWITCHDISSTURB = 8;//查询聊天开关
    public static final int SETUSERANDGROUPSWITCHDISSTURB = 9;//设置聊天开关免打扰
    public static final int SETUSERANDGROUPSWITCHTOP = 10;//设置聊天开关置顶

    public static final int NOFRIEND = 10002;//不是好友
    public static final int NOINGROUP = 10003;//不是好友
    public static final int UNKNOW = 10004;//不是好友


    private InterfaceTask interfaceTask;
    private int taskid;
    private JSONObject jsonObject;
    public Object flag;
    private List<NameValuePair> pairList;

    public Object object;


    public HttpChat(InterfaceTask interfaceTask, int taskid, JSONObject jsonObject) {
        super();
        this.interfaceTask = interfaceTask;
        this.taskid = taskid;
        this.jsonObject = jsonObject;
        pairList = new ArrayList<>();
    }

    public HttpChat(InterfaceTask interfaceTask, int taskid) {
        super();
        this.interfaceTask = interfaceTask;
        this.taskid = taskid;
        pairList = new ArrayList<>();
    }


    public void setPostValuesForKey(String Key, String value) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key, value);
        pairList.add(basicNameValuePair);


    }

    UrlEncodedFormEntity getPostData() {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void startTask() {

        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (taskid) {
                        case SENDMSG:
                            sendMsg();
                            break;
                        case CREATEGROUP:
                            creategroup();
                            break;
                        case QUERYGROUP:
                            querygroup();
                            break;
                        case UPDATEGROUPNAME:
                            updateGroupName();
                            break;
                        case UPDATEGROUPNICKNAME:
                            updateGroupNickName();
                            break;
                        case GROUPKICKOUT:
                            groupKickout();
                            break;
                        case PULLINGROUP:
                            pullingroup();
                            break;
                        case SELFEXITGROUP:
                            selfExitGroup();
                            break;
                        case QUERYUSERANDGROUPSWITCHDISSTURB:
                            queryChatSwitch();
                            break;
                        case SETUSERANDGROUPSWITCHDISSTURB:
                            setChatSwitch();
                            break;
                        case SETUSERANDGROUPSWITCHTOP:
                            setChatSwitchtop();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (interfaceTask != null)
                try {
                    interfaceTask.TaskResultForMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    };

    @Override
    public void stopTask() {
        m_httpClient.closeRequest();
        m_httpClient = null;
        handler = null;
    }


    /**
     * 发送消息
     */
    private void sendMsg() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$smsg/send", AppConfig.IMUrl);
            Log.i("url", loginurl);
            String ret = AjaxHttp.OutputStream(loginurl, jsonObject.toString());
            Log.i("http stream 返回", ret);
            JSONObject jsonObject = new JSONObject(ret);


            Message message = new Message();
            message.what = CHATTASK;
            if (jsonObject.getString("status").equals("10002"))
                message.arg1 = NOFRIEND;
            else if (jsonObject.getString("status").equals("10003"))
                message.arg1 = NOINGROUP;
            else if (jsonObject.getString("status").equals("10004"))
                message.arg1 = UNKNOW;
            else {
                ((ChatMessage) object).setMsgdate(jsonObject.getJSONObject("data").getString("sendTime"));
                ((ChatMessage) object).setMsgid(jsonObject.getJSONObject("data").getString("msgId"));
                message.arg1 = SUCCESS;
            }
            message.arg2 = SENDMSG;
            message.obj = object;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = SENDMSG;
            message.arg1 = FAILED;
            message.obj = object;
            handler.sendMessage(message);
        }
    }


    /**
     * 发送消息
     */
    private void creategroup() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/add", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() == 10005) {
                    throw new Exception("该群组成员已达上限!");
                } else if (returnData.getReturnCode() == 1) {
                    throw new Exception("处理异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = CREATEGROUP;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = CREATEGROUP;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 查询群组
     */
    private void querygroup() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/query", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() == 10005) {
                    throw new Exception("该群组成员已达上限!");
                } else if (returnData.getReturnCode() == 1) {
                    throw new Exception("处理异常");
                }

                JSONArray jsonArray = returnData.getJsonArray();

                MessageDB messageDB = new MessageDB();
                messageDB.deleteGroupInfo();
                messageDB.deleteGroupMemberInfo();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("members");
                    String strname = "";
                    for (int l = 0; l < jsonArray1.length(); l++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(l);
                        messageDB.insertGroupMemberInfo(jsonObject1.getString("groupId"),
                                jsonObject2.getString("groupNickName"), jsonObject2.getString("userId"),
                                jsonObject2.getString("picId"));

                        if (jsonObject2.getString("groupNickName").equals("null")) {


                            if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.OLDMAN) {
                                //这里意思是，如果是好友要判断是否有备注，没有备注显示昵称，如果不是好友 ，直接显示昵称
                                Contacts contacts = messageDB.getContactsForSamlple(jsonObject2.getString("userId"));
                                if (contacts == null)
                                    strname += jsonObject2.getString("nickName") + ",";
                                else {
                                    if (contacts.getNameRemark().equals(""))
                                        strname += contacts.getNickname() + ",";
                                    else
                                        strname += contacts.getNameRemark() + ",";
                                }

                            } else if (BaseUserInfo.getBaseUserInfo().getUserRight() == BaseUserInfo.UserRight.MANGER) {
                                Cursor cursor = messageDB.getPhoneBookContactsInfo(jsonObject2.getString("userId"));

                                if (cursor != null) {
                                    cursor.moveToNext();
                                    strname += cursor.getString(3) + ",";
                                }
                                cursor.close();
                            }
                        } else
                            strname += jsonObject2.getString("groupNickName") + ",";

                    }
                    strname = strname.substring(0, strname.length() - 1);
                    if (!jsonObject1.getString("groupName").equals("null"))
                        strname = jsonObject1.getString("groupName");


                    messageDB.insertGroupInfo(jsonObject1.getString("groupId"),
                            strname,
                            jsonObject1.getString("groupAvatar"),
                            jsonObject1.getString("userId"));


                    if (!Common.checkCacheIsExits(jsonObject1.getString("groupAvatar"), "_40.jpg")) {
                        if (!jsonObject1.getString("groupAvatar").equals("null")) {
                            HttpFile httpFIle = new HttpFile(null, HttpFile.DOWNLOADFILE, ".jpg",
                                    jsonObject1.getString("groupAvatar"), "_40");
                            httpFIle.startTask();
                        }
                        System.gc();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYGROUP;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = QUERYGROUP;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 修改群名称
     */
    private void updateGroupName() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/update", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("更新失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = UPDATEGROUPNAME;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = UPDATEGROUPNAME;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 修改群昵称
     */
    private void updateGroupNickName() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/user/update", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("更新失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = UPDATEGROUPNICKNAME;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = UPDATEGROUPNICKNAME;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 踢人
     */
    private void groupKickout() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/kickOut", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0 &&
                        returnData.getReturnCode() != 10008//已经不再该群
                        ) {
                    throw new Exception("更新失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = GROUPKICKOUT;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = GROUPKICKOUT;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 追加人
     */
    private void pullingroup() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/pullIn", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0 &&
                        returnData.getReturnCode() == 10005
                        )
                    throw new Exception("加入失败");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = PULLINGROUP;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = PULLINGROUP;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 自己退出
     */
    private void selfExitGroup() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sgroup/signOut", "");
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0)
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = SELFEXITGROUP;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = SELFEXITGROUP;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 查询聊天开关
     */
    private void queryChatSwitch() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sconfig/query", AppConfig.cHost);
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0)
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYUSERANDGROUPSWITCHDISSTURB;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = QUERYUSERANDGROUPSWITCHDISSTURB;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 设置聊天开关
     */
    private void setChatSwitch() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sconfig/disturb/set", AppConfig.cHost);
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0)
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = SETUSERANDGROUPSWITCHDISSTURB;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = SETUSERANDGROUPSWITCHDISSTURB;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 设置聊天开关置顶
     */
    private void setChatSwitchtop() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sconfig/top/set", AppConfig.cHost);
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0)
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = CHATTASK;
            message.arg1 = SUCCESS;
            message.arg2 = SETUSERANDGROUPSWITCHTOP;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = CHATTASK;
            message.arg2 = SETUSERANDGROUPSWITCHTOP;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }



}
