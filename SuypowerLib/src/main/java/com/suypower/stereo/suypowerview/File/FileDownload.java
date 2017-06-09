package com.suypower.stereo.suypowerview.File;

import android.bluetooth.BluetoothHealthAppConfiguration;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;


import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class FileDownload {

    public String imgamub = "";
    public String mediatype = "";
    private AjaxHttp ajaxHttp;
    private String downloadurl;
    private String mediaid;

    public FileDownload(String url, String mediaid) {
        super();
        ajaxHttp = new AjaxHttp();
        this.downloadurl = url;
        this.mediaid = mediaid;

    }

    public FileDownload(Map<String,Object> map) {
        super();
        ajaxHttp = new AjaxHttp();
        this.downloadurl =  (String) map.get("url");
        this.mediaid =  (String) map.get("mediaid");
        imgamub = map.get("imgamub")==null?"" :(String)map.get("imgamub");
        mediatype = (String)map.get("mediatype");

    }


    public Boolean streamDownLoadFile() {
        Log.i("下载地址", downloadurl);
//		FileDB fileDB=new FileDB(SuyApplication.getApplication().getSuyDB().getDb());
//		String uuid = fileDB.getUUIDForMediaid(mediaid);
        ajaxHttp.openRequest(downloadurl, AjaxHttp.REQ_METHOD_POST);
        ajaxHttp.setPostValuesForKey("mediaId", mediaid);
        ajaxHttp.setPostValuesForKey("imgSize", imgamub);
        ajaxHttp.setEntity(ajaxHttp.getPostData());
        ajaxHttp.sendRequest();
        HttpEntity httpEntity = ajaxHttp.getHttpResponse().getEntity();
        if (httpEntity == null)
            return false;
        InputStream inStream;
        ByteArrayOutputStream outStream;
        byte[] bufferfile = null;
        try {
            inStream = httpEntity.getContent();
            outStream = new ByteArrayOutputStream();
            Log.i("下载文件大小inStream:", String.valueOf(inStream.available()));
            int maxbuff = 1024 * 5000;
            byte[] buffer = new byte[maxbuff];
            int len = 0;

            if (inStream == null) {
                return true;
            }
            System.gc();
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            bufferfile = outStream.toByteArray();
            outStream.close();
            inStream.close();
            if (bufferfile == null)
                throw new Exception();
            if (bufferfile.length <=50) {
                Log.e("下载",new String (bufferfile));
                throw new Exception();

            }
            Log.i("下载文件大小:", String.valueOf(bufferfile.length));

            /***
             * 下载存到本地
             */
            File file = new File(Init.getContext().getCacheDir(),
                    mediaid + imgamub + mediatype);
            if (file.exists())
            {
                file.delete();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bufferfile);
            fileOutputStream.close();
            ajaxHttp.closeRequest();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ajaxHttp.closeRequest();
            return false;
        }
    }





}
