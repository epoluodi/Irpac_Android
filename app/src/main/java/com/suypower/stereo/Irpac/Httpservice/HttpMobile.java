package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.Common.FileCommon;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.File.FileUpLoad;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class HttpMobile extends BaseTask {


    public static final int CHECKUSER = 1;//检查用户是否绑定
    public static final int SubmitEBAAudio = 2;//提交语音接口


    public String mediaid;
    public Object flag;
    private InterfaceTask interfaceTask;
    private int type;
    public String userId;//


    //单上传
    public HttpMobile(InterfaceTask interfaceTask, int type) {
        super();
        this.interfaceTask = interfaceTask;
        this.type = type;
    }


    @Override
    public void startTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case CHECKUSER:
                            checkUser();
                            break;
                        case SubmitEBAAudio:
                            uploadVoice();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void stopTask() {

    }


    public void checkUser() {
        String loginurl;
        try {

            Looper.prepare();
            loginurl = String.format("%1$scare/eHealth/checkUserEquip", AppConfig.MobileUrl);
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("userId", userId);
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
            Log.i("信息返回:", result);
//            JSONObject jsonObject = null;
//            ReturnData returnData;
//            try {
//                //解析json
//                jsonObject = new JSONObject(result);
//                returnData = new ReturnData(jsonObject, true);
//                if (returnData.getReturnCode() == 10005) {
//                    throw new Exception("该群组成员已达上限!");
//                } else if (returnData.getReturnCode() == 1) {
//                    throw new Exception("处理异常");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new Exception("网络异常");
//            }
            if (result.equals("true")) {
                message.what = MobileTask;
                message.arg1 = SUCCESS;
                message.arg2 = CHECKUSER;

            }
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = MobileTask;
            message.arg2 = CHECKUSER;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
            handler.sendMessage(message);
        }

    }


    public void uploadVoice() {
        String loginurl;
        try {

            Looper.prepare();
            loginurl = String.format("%1$scare/eHealth/uploadVoice?mediaId=%2$s", AppConfig.MobileUrl, mediaid);
            m_httpClient.openRequest(loginurl, AjaxHttp.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("userId", userId);
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
            Log.i("信息返回:", result);
//            JSONObject jsonObject = null;
//            ReturnData returnData;
//            try {
//                //解析json
//                jsonObject = new JSONObject(result);
//                returnData = new ReturnData(jsonObject, true);
//                if (returnData.getReturnCode() == 10005) {
//                    throw new Exception("该群组成员已达上限!");
//                } else if (returnData.getReturnCode() == 1) {
//                    throw new Exception("处理异常");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new Exception("网络异常");
//            }
            if (result.equals("true")) {
                message.what = MobileTask;
                message.arg1 = SUCCESS;
                message.arg2 = SubmitEBAAudio;
                message.obj = flag;
            }
            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = MobileTask;
            message.arg2 = SubmitEBAAudio;
            message.arg1 = FAILED;
            message.obj = flag;
            handler.sendMessage(message);
        }

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (interfaceTask != null) {
                try {
                    interfaceTask.TaskResultForMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    };


}
