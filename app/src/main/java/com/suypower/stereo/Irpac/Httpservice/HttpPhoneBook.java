package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Chat.ChatMessage;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.DataClass.Contacts;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/15.
 */
public class HttpPhoneBook extends BaseTask {



    public static final int QUERYORG = 0;//查询单位
    public static final int QUERYDEPT = 1;//查询部门
    public static final int QUERYUSERINFO = 2;//查询用户信息





    private InterfaceTask interfaceTask;
    private int taskid;
    private JSONObject jsonObject;
    public Object flag;
    private List<NameValuePair> pairList;

    public Object object;






    public HttpPhoneBook(InterfaceTask interfaceTask, int taskid) {
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
                        case QUERYORG:
                            queryOrg();
                            break;
                        case QUERYDEPT:
                            queryDept();
                            break;
                        case QUERYUSERINFO:
                            queryUserInfo();
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
    void queryOrg() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$saddrbook/org/query", AppConfig.IMUrl);
            Log.i("url", "");

            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            if (LibConfig.getKeyShareVarForint("orgver") != -1) {
                m_httpClient.setPostValuesForKey("numVer", String.valueOf(LibConfig.getKeyShareVarForint("orgver")));
                m_httpClient.setEntity(m_httpClient.getPostData());
            }
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.e("通讯录信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0  )
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            if (returnData.getReturnData().getInt("lastNumVer") > LibConfig.getKeyShareVarForint("orgver"))
            {
                LibConfig.setKeyShareVar("orgver",returnData.getReturnData().getInt("lastNumVer"));
                MessageDB messageDB=new MessageDB();
                messageDB.deletePhoneBook();
                for (int i =0;i<returnData.getReturnData().getJSONArray("orgs").length();i++)
                {
                    messageDB.insertOrg(returnData.getReturnData().getJSONArray("orgs").getJSONObject(i));
                }
            }
            message.what = PhoneBookTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYORG;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            LibConfig.setKeyShareVar("orgver",0);
            Message message = handler.obtainMessage();
            message.what = PhoneBookTask;
            message.arg2 = QUERYORG;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


    void queryDept() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$saddrbook/dept/query", AppConfig.IMUrl);
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            if (LibConfig.getKeyShareVarForint("deptver") != -1) {
                m_httpClient.setPostValuesForKey("numVer", String.valueOf(LibConfig.getKeyShareVarForint("deptver")));
                m_httpClient.setEntity(m_httpClient.getPostData());
            }
            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.e("通讯录信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0  )
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            if (returnData.getReturnData().getInt("lastNumVer") > LibConfig.getKeyShareVarForint("deptver"))
            {
                LibConfig.setKeyShareVar("deptver",returnData.getReturnData().getInt("lastNumVer"));
                MessageDB messageDB=new MessageDB();
                for (int i =0;i<returnData.getReturnData().getJSONArray("depts").length();i++)
                {
                    messageDB.insertDept(returnData.getReturnData().getJSONArray("depts").getJSONObject(i));
                }
            }

            message.what = PhoneBookTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYDEPT;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            LibConfig.setKeyShareVar("deptver",0);
            Message message = handler.obtainMessage();
            message.what = PhoneBookTask;
            message.arg2 = QUERYDEPT;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }

    void queryUserInfo() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            loginurl = String.format("%1$saddrbook/user/query", AppConfig.IMUrl);
            Log.i("url", "");
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            if (LibConfig.getKeyShareVarForint("userver") != -1) {
                m_httpClient.setPostValuesForKey("numVer", String.valueOf(LibConfig.getKeyShareVarForint("userver")));
                m_httpClient.setEntity(m_httpClient.getPostData());
            }


            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                throw new Exception("网络异常");
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                throw new Exception("网络异常");
            }

            String result = new String(buffer, "utf-8");
            Log.e("通讯录信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0  )
                    throw new Exception("无法退出，请重新尝试");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }

            if (returnData.getReturnData().getInt("lastNumVer") > LibConfig.getKeyShareVarForint("userver"))
            {
                LibConfig.setKeyShareVar("userver",returnData.getReturnData().getInt("lastNumVer"));
                MessageDB messageDB=new MessageDB();
                messageDB.deleteUserInfo();
                for (int i =0;i<returnData.getReturnData().getJSONArray("users").length();i++)
                {
                    messageDB.insertUserInfo(returnData.getReturnData().getJSONArray("users").getJSONObject(i));
                }
            }

            message.what = PhoneBookTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYUSERINFO;
            message.obj = returnData;
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            LibConfig.setKeyShareVar("userver",0);
            Message message = handler.obtainMessage();
            message.what = PhoneBookTask;
            message.arg2 = QUERYUSERINFO;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }

}
