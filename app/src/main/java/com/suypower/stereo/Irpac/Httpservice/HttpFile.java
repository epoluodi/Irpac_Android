package com.suypower.stereo.Irpac.Httpservice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.AppConfig;
import com.suypower.stereo.suypowerview.Common.FileCommon;
import com.suypower.stereo.suypowerview.File.FileDownload;
import com.suypower.stereo.suypowerview.File.FileUpLoad;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.apache.cordova.App;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class HttpFile extends BaseTask {


    public static final int UPLOADFILE = 1;//文件上传
    public static final int DOWNLOADFILE = 2;//文件下载
    public static final int MULTIILEUPLOAD = 3;//多文件上传
    public static final int URLDOWNLOADFILE = 4;//文件直接下载


    private String mediaid;
    private String filetype = "";
    private String uploadtype;//资源类型 02 图片 03 头像
    private String subtype;
    private InterfaceTask interfaceTask;
    private int type;
    private String[] fileids;
    private String imgurl;




    //单上传
    public HttpFile(InterfaceTask interfaceTask, int type, String filetype, String mediaid, String uploadtype, String subtype) {
        super();
        this.interfaceTask = interfaceTask;
        this.filetype = filetype;
        this.mediaid = mediaid;
        this.uploadtype = uploadtype;
        this.type = type;
        this.subtype = subtype;

    }

    //批量上传
    public HttpFile(InterfaceTask interfaceTask, int type, String filetype, String uploadtype, String subtype, String[] fileids) {
        super();
        this.interfaceTask = interfaceTask;
        this.filetype = filetype;
        this.fileids = fileids;
        this.type = type;
        this.subtype = subtype;
        this.uploadtype = uploadtype;
    }

    public HttpFile(InterfaceTask interfaceTask, int type, String filetype, String mediaid, String subtype) {
        super();
        this.interfaceTask = interfaceTask;
        this.filetype = filetype;
        this.mediaid = mediaid;
        this.type = type;
        this.subtype = subtype;

    }

    public HttpFile(InterfaceTask interfaceTask, int type, String mediaid,String url) {
        super();
        this.interfaceTask = interfaceTask;
        this.filetype = ".jpg";
        this.type = type;
        this.mediaid = mediaid;
        imgurl = url;

    }
    @Override
    public void startTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case UPLOADFILE:
                            uploadfile();
                            break;
                        case DOWNLOADFILE:
                            downloadfile();
                            break;
                        case MULTIILEUPLOAD:
                            multiUploadfile();
                            break;
                        case URLDOWNLOADFILE:
                            downloadfileUrl();
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


    public void uploadfile() {



        System.gc();
        AppConfig appConfig=new AppConfig();
        String url = String.format("%1$smedia/upload", appConfig.AppUrl);
//        String url = "http://192.168.0.196:8080/Cloudx/app/media/upload";
        FileUpLoad fileUpLoad = new FileUpLoad(url, filetype, mediaid, uploadtype, subtype);
        String result = fileUpLoad.uploadfile();
        Log.i("上传返回:", result);

        try {
            if (result.equals(""))
                throw new Exception("上传错误");
            ReturnData returnData = new ReturnData(new JSONObject(result), true);
            Message message = handler.obtainMessage();
            if (returnData.getReturnCode() == 0) {
                message.what = FileTask;
                message.arg1 = SUCCESS;
                message.arg2 = UPLOADFILE;
                message.obj = returnData.getReturnData().getString("mediaId");
                FileCommon.CopyFIle(APP.getApp().getCacheDir() + File.separator + mediaid + filetype,
                        returnData.getReturnData().getString("mediaId") + filetype);
                FileCommon.DeleteFile(APP.getApp().getCacheDir() + File.separator + mediaid + filetype);

                if (uploadtype.equals("02") && subtype.equals("01")) {
                    FileCommon.CopyFIle(APP.getApp().getCacheDir() + File.separator + mediaid + "_200" + filetype,
                            returnData.getReturnData().getString("mediaId") + "_200" + filetype);
                    FileCommon.DeleteFile(APP.getApp().getCacheDir() + File.separator + mediaid + "_200" + filetype);
                }
                handler.sendMessage(message);


            } else {
                throw new Exception("上传错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = FileTask;
            message.arg1 = FAILED;
            message.arg2 = UPLOADFILE;
            message.obj = mediaid;
            handler.sendMessage(message);
        }

    }


    /**
     * 多文件上传
     */
    public void multiUploadfile() {

        AppConfig appConfig=new AppConfig();
        final  String url = String.format("%1$supload", appConfig.AppUrl);
//        final String url = "http://192.168.0.196:8080/Cloudx/app/media/upload";
        for (int i=0;i<fileids.length;i++)
        {
            final String fileid =fileids[i];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUpLoad fileUpLoad = new FileUpLoad(url, filetype, fileid, uploadtype, subtype);
                    String result = fileUpLoad.uploadfile();
                    try {
                        if (result.equals(""))
                            throw new Exception("上传错误");
                        ReturnData returnData = new ReturnData(new JSONObject(result), true);
                        Message message = handler.obtainMessage();
                        if (returnData.getReturnCode() == 0) {
                            message.what = FileTask;
                            message.arg1 = SUCCESS;
                            message.arg2 = MULTIILEUPLOAD;
                            message.obj = returnData.getReturnData().getString("fileId");
                            FileCommon.CopyFIle(APP.getApp().getCacheDir() + File.separator + fileid + filetype,
                                    returnData.getReturnData().getString("fileId") + filetype);


                            handler.sendMessage(message);
                        } else {
                            throw new Exception("上传错误");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message message = handler.obtainMessage();
                        message.what = FileTask;
                        message.arg1 = FAILED;
                        message.arg2 = MULTIILEUPLOAD;
                        message.obj = e.getMessage();
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }


    public void downloadfile() {
        AppConfig appConfig=new AppConfig();
        Message message = handler.obtainMessage();
        try {
            if (mediaid == null || mediaid.equals(""))
                throw new Exception("下载错误");
        String url = String.format("%1$smedia/download", appConfig.AppUrl);

//            String url = "http://192.168.0.196:8080/Cloudx/app/media/download";

            FileDownload fileDownload = new FileDownload(url, mediaid);
            fileDownload.imgamub = subtype;
            fileDownload.mediatype = filetype;
            Boolean r = fileDownload.streamDownLoadFile();

            System.gc();


            if (!r)
                throw new Exception("下载错误");

            message.what = FileTask;
            message.arg1 = SUCCESS;
            message.arg2 = DOWNLOADFILE;
            message.obj = mediaid;
//                FileDB fileDB = new FileDB(SuyApplication.getApplication().getSuyDB().getDb());
//                fileDB.insertMediaAndUUID(returnData.getReturnData().getString("mediaId"), mediaid);

            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = FileTask;
            message.arg1 = FAILED;
            message.arg2 = DOWNLOADFILE;
            message.obj = mediaid;
            handler.sendMessage(message);
        }

    }



    public void downloadfileUrl() {

        Message message = handler.obtainMessage();
        try {
            if (mediaid == null || mediaid.equals(""))
                throw new Exception("下载错误");


//            String url = "http://192.168.0.196:8080/Cloudx/app/media/download";

            FileDownload fileDownload = new FileDownload(imgurl, mediaid);
            fileDownload.imgamub = "";
            fileDownload.mediatype = filetype;
            Boolean r = fileDownload.streamDownLoadFile();

            System.gc();


            if (!r)
                throw new Exception("下载错误");

            message.what = FileTask;
            message.arg1 = SUCCESS;
            message.arg2 = URLDOWNLOADFILE;
            message.obj = mediaid;
//                FileDB fileDB = new FileDB(SuyApplication.getApplication().getSuyDB().getDb());
//                fileDB.insertMediaAndUUID(returnData.getReturnData().getString("mediaId"), mediaid);

            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = FileTask;
            message.arg1 = FAILED;
            message.arg2 = URLDOWNLOADFILE;
            message.obj = mediaid;
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
