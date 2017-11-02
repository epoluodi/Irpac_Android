package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.MQTT.MQTTConfig;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.apache.cordova.App;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/15.
 */
public class HttpUserInfo extends BaseTask {

    public static final int GETBASEUSERINFO = 0;//获取用户基本信息
    public static final int UPDATEUSERINFO = 1;//修改用户基本信息
    public static final int SEARCHUSERINFOS = 2;//查询用户信息列表
    public static final int ADDFRIEND = 3;//添加好友列表
    public static final int QUERYFRIEND = 4;//查询好友列表
    public static final int ACCEPTFRIEND = 5;//接受好友列表
    public static final int DELFRIEND = 6;//删除好友列表
    public static final int UPDATEUSERREMARK = 7;//修改用户备注
    public static final int QUERYSAMPLEFORUSERID = 8;//查询简单用户信息
    public static final int QUERYFAMILYLIST = 9;//查询亲情列表
    public static final int DOWNLOADWLIMG = 10;//下载开机图片


    private InterfaceTask interfaceTask;
    private int taskid;
    public Object flag;
    private List<NameValuePair> pairList;


    public HttpUserInfo(InterfaceTask interfaceTask, int taskid) {
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
                        case GETBASEUSERINFO:
                            getBaseUserinfo();
                            break;
                        case UPDATEUSERINFO:
                            updateBaseUserinfo();
                            break;
                        case SEARCHUSERINFOS:
                            searchUserinfoList();
                            break;
                        case ADDFRIEND:
                            addFriend();
                            break;
                        case QUERYFRIEND:
                            queryFriend();
                            break;
                        case ACCEPTFRIEND:
                            acceptFriend();
                            break;
                        case DELFRIEND:
                            deleteFriend();
                            break;
                        case UPDATEUSERREMARK:
                            updateFriendRemark();
                            break;
                        case QUERYSAMPLEFORUSERID:
                            queryforuserid();
                            break;
                        case QUERYFAMILYLIST:
                            queryfamilylist();
                            break;
                        case DOWNLOADWLIMG:
                            downloadSplashImg();
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
     * 获得用户信息
     */
    void getBaseUserinfo() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            AppConfig appConfig=new AppConfig();

            loginurl = String.format("%1$sgetUserDetail", appConfig.AppUrl);


            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("userId", UserInfo.getUserInfo().getUserId());
            m_httpClient.setEntity(m_httpClient.getPostData());
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
                    throw new Exception("网络异常");
                }
                JSONObject data = returnData.getReturnData();


                UserInfo.getUserInfo().setNickname((data.isNull("userName"))//昵称
                        ? "" : data.getString("userName"));


                UserInfo.getBaseUserInfo().setUsername(data.getString("userName"));//真实名称
                UserInfo.getUserInfo().setPhoto(data.getString("photo"));
                UserInfo.getUserInfo().setGh(data.getString("gh"));
                UserInfo.getUserInfo().setOfficeName(data.getString("officeName"));




                Log.e("获取用户基本信息完成", "ok");

//                UserInfo.getBaseUserInfo().setUserType(Integer.valueOf(user.getString("status")));
                //下载头像照片

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = GETBASEUSERINFO;
            message.obj = "登录成功";
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = GETBASEUSERINFO;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 更新用户基本信息
     */
    void updateBaseUserinfo() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            AppConfig appConfig=new AppConfig();
            loginurl = String.format("%1$sperson/update", appConfig.AppUrl);
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
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = UPDATEUSERINFO;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = UPDATEUSERINFO;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 更新用户基本信息
     */
    void searchUserinfoList() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sstranger/query", AppConfig.IMUrl);
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
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = SEARCHUSERINFOS;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = SEARCHUSERINFOS;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 添加好友
     */
    void addFriend() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sfriend/make", AppConfig.IMUrl);
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
                if (returnData.getReturnCode() == 1) {
                    throw new Exception("网络异常");
                } else if (returnData.getReturnCode() == 10001) {
                    message.what = UserInfoTask;
                    message.arg1 = SUCCESS;
                    message.arg2 = ADDFRIEND;
                    message.obj = "你们已经是好友";
                } else {
                    message.what = UserInfoTask;
                    message.arg1 = SUCCESS;
                    message.arg2 = ADDFRIEND;
                    message.obj = "提交成功，等待好友验证通过";
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = ADDFRIEND;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 查询好友好友
     */
    void queryFriend() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sfriend/query", AppConfig.IMUrl);
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
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            MessageDB messageDB = new MessageDB();
            messageDB.deletefriend();
            JSONArray jsonArray = returnData.getJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Contacts contacts = new Contacts();
                    contacts.setId(jsonObject1.getString("userId"));
                    contacts.setPY(jsonObject1.getString("pinyin"));
                    contacts.setNickimgurl(jsonObject1.getString("picId"));
                    contacts.setNickname(jsonObject1.getString("nickName"));
                    contacts.setNameRemark(jsonObject1.getString("friendRemark"));
                    contacts.setAreaName(jsonObject1.getString("areaName"));
                    contacts.setFirstLetter(jsonObject1.getString("firstLetter"));
                    messageDB.insertFriend(contacts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYFRIEND;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = QUERYFRIEND;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }

    /**
     * 查询好友简单信息
     */
    void queryforuserid() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sperson/info", AppConfig.cHost);
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
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYSAMPLEFORUSERID;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = QUERYSAMPLEFORUSERID;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 接受好友
     */
    void acceptFriend() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sfriend/respond", AppConfig.IMUrl);
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
                    throw new Exception("网络异常");
                }

                if (!jsonObject.isNull("data")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    Contacts contacts = new Contacts();
                    contacts.setId(jsonObject1.getString("userId"));
                    contacts.setPY(jsonObject1.getString("pinyin"));
                    contacts.setNickimgurl(jsonObject1.getString("picId"));
                    contacts.setNickname(jsonObject1.getString("nickName"));
                    contacts.setAreaName(jsonObject1.getString("areaName"));
                    contacts.setFirstLetter(jsonObject1.getString("firstLetter"));

                    MessageDB messageDB = new MessageDB();
                    messageDB.insertFriend(contacts);
                }


            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }


            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = ACCEPTFRIEND;
            message.obj = flag;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = ACCEPTFRIEND;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 删除s好友
     */
    void deleteFriend() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sfriend/release", AppConfig.IMUrl);
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
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = DELFRIEND;
            message.obj = flag;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = DELFRIEND;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }

    /**
     * 修改好友备注
     */
    void updateFriendRemark() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sfriend/remark", AppConfig.IMUrl);
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
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = UPDATEUSERREMARK;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = UPDATEUSERREMARK;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    /**
     * 查询亲情好友
     */
    void queryfamilylist() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$sperson/families", AppConfig.cHost);
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
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("网络异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            MessageDB messageDB = new MessageDB();

            messageDB.deletefriend();
            JSONArray jsonArray = returnData.getJsonArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Contacts contacts = new Contacts();
                    contacts.setId(jsonObject1.getString("userId"));
                    contacts.setPY(jsonObject1.getString("pinyin"));
                    contacts.setNickimgurl(jsonObject1.getString("picId"));
                    contacts.setNickname(jsonObject1.getString("nickName"));
//                    contacts.setNameRemark(jsonObject1.getString("friendRemark"));
                    contacts.setAreaName(jsonObject1.getString("areaName"));


                    int ascii = jsonObject1.getString("firstLetter").charAt(0);
                    if (ascii > 64 && ascii < 91)
                        contacts.setFirstLetter(jsonObject1.getString("firstLetter"));
                    else
                        contacts.setFirstLetter("@");

                    contacts.setGender(jsonObject1.getString("gender"));
                    messageDB.insertFriend(contacts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


//            messageDB.deleteFamilyList();
//            for (int i=0;i<jsonArray.length();i++)
//            {
//                JSONObject jsonObject1=jsonArray.getJSONObject(i);
//                messageDB.insertFamilyList(jsonObject1.getString("USER_ID"));
//            }
            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYFAMILYLIST;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = QUERYFAMILYLIST;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }

    /**
     * 查询亲情好友
     */
    void downloadSplashImg() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            AppConfig appConfig=new AppConfig();
            loginurl = String.format("%1$sgetWelcomePage", appConfig.AppUrl);
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
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    throw new Exception("网络异常");
                }

                String imgid = returnData.getReturnData().getString("pageId");
                String url = returnData.getReturnData().getString("pageUrl");
                String oldimgid = LibConfig.getKeyShareVarForString("splashimg");
                if (!imgid.equals(oldimgid)) {
                    LibConfig.setKeyShareVar("splashimg", imgid);
                    FileDownload fileDownload = new FileDownload(url, imgid);
                    fileDownload.mediatype = ".jpg";
                    fileDownload.streamDownLoadFile();

                }

            } catch (Exception e) {
                e.printStackTrace();
                LibConfig.setKeyShareVar("splashimg", "null");
                throw new Exception("网络异常");
            }


            message.what = UserInfoTask;
            message.arg1 = SUCCESS;
            message.arg2 = DOWNLOADWLIMG;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UserInfoTask;
            message.arg2 = DOWNLOADWLIMG;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


}
