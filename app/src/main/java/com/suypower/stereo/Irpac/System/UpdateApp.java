package com.suypower.stereo.Irpac.System;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.suypower.stereo.suypowerview.AlertView.AlertDlg;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Http.AjaxHttp;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * 业务更新模块
 *
 * @author YXG 2015-04-13
 */
public class UpdateApp {


    public static final String UPDATEURL = AppConfig.AppUpgrade + "android/download";
    static final String DOWNLOADAPKURL = "";
    private String ver;
    private int vercode;
    private AjaxHttp ajaxHttp;
    private Handler handler = null;
    private Object data = null;
    private HttpURLConnection httpURLConnection;
    private HttpEntity httpEntity;
    private AlertDlg alertDlg;

    public Object getData() {
        return data;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String NewVer="";

    /**
     * 初始化更新
     */
    public UpdateApp() {
        //初始插件
        ajaxHttp = new AjaxHttp();
        PackageManager manager = APP.getApp().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(APP.getApp().getPackageName(), 0);
            ver = info.versionName;
            vercode = info.versionCode;
            Log.i("APP版本:", ver + " " + String.valueOf(vercode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean CheckVerison() {

        String checkUrl = String.format("%1$sgetNewVersion?type=1",
                AppConfig.AppUpgrade);

        ajaxHttp.openRequest(checkUrl, AjaxHttp.REQ_METHOD_GET);
        if (!ajaxHttp.sendRequest())//发送请求
        {
            return false;
        }

        //获得返回数据
        byte[] buffer = ajaxHttp.getRespBodyData();
        if (buffer == null) {
            return false;
        }
        try {
            String result = new String(buffer, "utf-8");
            Log.i("更新信息返回:", result);
            JSONObject jsonObject = new JSONObject(result);
            ReturnData returnData = new ReturnData(jsonObject, true);
            if (returnData.getReturnCode() != 0) {
                return false;
            }
            NewVer = returnData.getReturnData().getString("versionNo");
            int _vercode = Integer.valueOf(returnData.getReturnData().getString("versionNum"));
            if (vercode < _vercode) {
                data = returnData.getReturnData();
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
