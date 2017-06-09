package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.MQTT.MQTTConfig;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/15.
 */
public class HttpLogin extends BaseTask {


    public Boolean isToken;
    InterfaceTask interfaceTask;

    public HttpLogin(InterfaceTask interfaceTask) {
        super();
        this.interfaceTask = interfaceTask;
    }


    @Override
    public void startTask() {
        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    login();
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


    void login() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();

            loginurl = String.format("%1$sgetToken?userName=%2$s&pwd=%3$s&token=%4$s", AppConfig.AuthUrl,
                    UserInfo.getUserInfo().getLginname(), UserInfo.getUserInfo().getUserpwd(), Common.Token);


            Log.e(" view  loginurl", loginurl);
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_GET);
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
                    message.what = LoginTask;
                    message.arg1 = PASSWORD_ERROR;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);
                    return;
                }

                LibConfig.setKeyShareVar("username", UserInfo.getBaseUserInfo().getLginname());
                LibConfig.setKeyShareVar("userpwd", UserInfo.getBaseUserInfo().getUserpwd());
                JSONObject ajax_data = returnData.getReturnData();
                UserInfo.getBaseUserInfo().setToken(ajax_data.getString("token"));
                UserInfo.getBaseUserInfo().setUserId(ajax_data.getString("userId"));
//                UserInfo.getBaseUserInfo().setQrcode(ajax_data.getString("domainUrl"));

                BaseUserInfo.setBaseUserInfo(UserInfo.getBaseUserInfo());
                Common.Token = ajax_data.getString("token");
                LibConfig.setKeyShareVar("token", Common.Token);
                BaseUserInfo.setBaseUserInfo(UserInfo.getBaseUserInfo());


                AppConfig.IsAPPLogin = true;

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            message.what = LoginTask;
            message.arg1 = SUCCESS;
            message.obj = "登录成功";
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = LoginTask;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }
    }


}
