package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.DB.MessageDB;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class HttpWeApp extends BaseTask {


    public static final int QUERYLIST = 1;//查询微应用列表


    public String mediaid;
    public Object flag;
    private InterfaceTask interfaceTask;
    private int type;


    //单上传
    public HttpWeApp(InterfaceTask interfaceTask, int type) {
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
                        case QUERYLIST:
                            querylist();
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


    public void querylist() {
        String loginurl;
        try {

            Looper.prepare();
            loginurl = String.format("%1$sweapp/query", AppConfig.IMUrl);
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
            Log.i("微应用订阅信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("网络异常");
            }
            if (returnData.getReturnCode() != 0)
                throw new Exception("积分获取失败");


            MessageDB messageDB=new MessageDB();
            messageDB.deleteWeApp();
            for (int i =0;i<returnData.getJsonArray().length();i++)
            {
                messageDB.insertWeApp(returnData.getJsonArray().getJSONObject(i));
            }


            message.what = ScoreTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYLIST;
            message.obj = null;

            handler.sendMessage(message);
            m_httpClient.closeRequest();
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = ScoreTask;
            message.arg2 = QUERYLIST;
            message.arg1 = FAILED;
            message.obj = e.getMessage();
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
