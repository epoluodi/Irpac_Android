package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.os.Looper;

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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaijuan on 2016/7/28.
 */
public class HttpRequestTask {
    ExecutorService m_ThreadPool = null;
    AjaxHttp m_httpClient = null;
    private InterfaceTask m_interfaceTask;
    String m_method;

    public HttpRequestTask(InterfaceTask interfaceTask,String method) throws Exception {
        m_interfaceTask=interfaceTask;
        m_method = method;
        m_httpClient = new AjaxHttp();
        m_ThreadPool = Executors.newFixedThreadPool(50);
    }
    Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (m_interfaceTask != null)
                try {
                    m_interfaceTask.TaskResultForMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    };

    public void startTask(Map<String,Object> params)
    {
        final Map<String,Object> tmp = params;
        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    if (m_method.equals(HttpMethodUtils.upLoadFileMethod))
                    {
                        upLoadFileRequest(tmp);
                    }
                    else if (m_method.equals(HttpMethodUtils.downFileMethod))
                    {
                        downFileRequest(tmp);
                    }
                    else
                    {
                        postHttpRequest(tmp);
                    }

                } catch (Exception e) {
                    stopTask();
                    e.printStackTrace();
                }
            }
        });
    }
    public void stopTask() {
        m_httpClient.closeRequest();
        m_httpClient = null;
        m_handler = null;
        m_interfaceTask=null;
    }

    /**
     * 一般的服务请求
     * @param params
     * @throws Exception
     */
    protected  void  postHttpRequest(Map<String,Object> params) throws Exception {
        /**拼接URL*/
        String url = String.format("%1$s%2$s", AppConfig.AppUrl, m_method);

        m_httpClient.openRequest(url, AjaxHttp.REQ_METHOD_POST);

        /**push 参数*/
        for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value =(String) entry.getValue();
            m_httpClient.setPostValuesForKey(key, value);
        }
        m_httpClient.setEntity(m_httpClient.getPostData());
        if (!m_httpClient.sendRequest()) {
            throw new Exception("网络异常");
        }
        byte[] buffer = m_httpClient.getRespBodyData();
        if (buffer == null) {
            throw new Exception("网络异常");
        }

        String result = new String(buffer, "utf-8");
        Log.i("信息返回:", result);
        regroupHttpResult(result,params);
    }

    /***
     * 下载文件
     * @param params
     * @throws Exception
     */
    protected  void  downFileRequest(Map<String,Object> params) throws Exception {
        /**拼接URL*/
        String url = String.format("%1$s%2$s", AppConfig.AppUrl, m_method);
        params.put("url",url);
        FileDownload fileDownLoad = new FileDownload(params);
        Boolean result = fileDownLoad.streamDownLoadFile();
        regroupHttpResult(result.toString(),params);
    }

    /***
     * 上传文件
     * @param params
     * @throws Exception
     */
    protected  void  upLoadFileRequest(Map<String,Object> params) throws Exception {
        /**拼接URL*/
        String url = String.format("%1$s%2$s", AppConfig.AppUrl, m_method);
        String mediaType = params.get("mediaType").toString();
        String imageType = params.get("imageType").toString();
        FileUpLoad fileUpLoad = new FileUpLoad(url, mediaType, imageType);
        String result = fileUpLoad.uploadfile();
        Log.i("上传返回:", result);
        System.gc();

        ReturnData returnData = new ReturnData(new JSONObject(result), true);
        if (returnData.getReturnCode() == 0)
        {
            String mediaid = returnData.getReturnData().getString("mediaId");
            FileCommon.CopyFIle(APP.getApp().getCacheDir() + File.separator + mediaid + imageType,
                    returnData.getReturnData().getString("mediaId") + imageType);
        }
        regroupHttpResult(result,params);
    }

    /**
     *
     */
    private void regroupHttpResult(String result,Map<String,Object> params) {
        Message message = m_handler.obtainMessage();
        message.what =new Integer(params.get("what").toString());
        message.arg2 = new Integer(params.get("arg2").toString());
        if (result.equals(""))
        {
            message.arg1 = HttpUtils.FAILED;
            message.obj = "返回结果为空";
            m_handler.sendMessage(message);
            return;
        }
        try {
            message.arg1 = HttpUtils.SUCCESS;
            message.obj = result;
            m_httpClient.closeRequest();
            m_handler.sendMessage(message);
        } catch (Exception e)
        {

            message.arg1 = HttpUtils.FAILED;
            message.obj = e.getMessage();
            m_handler.sendMessage(message);
        }
        return;
    }

}


