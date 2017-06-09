package com.suypower.stereo.Irpac.Httpservice;

import java.util.Map;


/**
 * Created by Stereo on 16/4/15.
 */
public class HttpFileTransfer {
    public static final int UPLOAD_FILE = 0;//上传文件
    public static final int DOWN_FILE = 1;//下载文件

    InterfaceTask m_interfaceTasks;

    public HttpFileTransfer(InterfaceTask interfaceTask) {
        m_interfaceTasks =interfaceTask;
    }

    /***
     * 查询我的收藏列表
     * @param param
     */
    public void upLoadFile(Map<String,Object> param) throws Exception{
        Map<String, Object> params = param;//调用的参数
        params.put("what", String.valueOf(HttpUtils.UploadFileTransferTask));
        params.put("arg2",  String.valueOf(UPLOAD_FILE));
        HttpRequestTask task = null;
        try {
            task = new HttpRequestTask(m_interfaceTasks, HttpMethodUtils.upLoadFileMethod);
            task.startTask(params);
        }
        catch (Exception e)
        {
            if(task != null)
            {
                task.stopTask();
            }
        }

    }

    /***
     * 下载文件
     */
    public  void  downLoadFile(Map<String,Object> param)
    {
        Map<String, Object> params = param;//调用的参数
        params.put("what", String.valueOf(HttpUtils.DownloadFileTransferTask));
        params.put("arg2", String.valueOf(DOWN_FILE));
        HttpRequestTask task = null;
        try
        {
            task = new HttpRequestTask(m_interfaceTasks, HttpMethodUtils.downFileMethod);
            task.startTask(params);
        }
        catch (Exception e)
        {
            if(task != null)
            {
                task.stopTask();
            }
        }
    }


}
