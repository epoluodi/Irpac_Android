package com.suypower.stereo.Irpac.System;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.suypower.stereo.Irpac.Activity.CordovaWebViewActivity;

import com.suypower.stereo.Irpac.R;
import com.suypower.stereo.Irpac.SplashActivity;
import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Common.FileCommon;
import com.suypower.stereo.suypowerview.DB.SuyDB;
import com.suypower.stereo.suypowerview.Notification.NotificationSwitch;
import com.suypower.stereo.suypowerview.WXCore.WXCore;

import java.io.File;

/**
 * Created by Stereo on 16/7/12.
 */
public class APP extends Application {

    private static APP app;
    public static final int DBVER = 17;

    public static APP getApp() {
        return app;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Init.Init(this);//初始化lib库
        //加载数据库
        Boolean isrun = LibConfig.getKeyShareVarForBoolean("IsRun");//判断程序是否运行
        if (!isrun) {

            LibConfig.setKeyShareVar("username", "null");
            LibConfig.setKeyShareVar("userpwd", "null");
            LibConfig.setKeyShareVar("IsRun", true);
            LibConfig.setKeyShareVar("serverUrl", "");
            LibConfig.setKeyShareVar("splashimg", "null");
//            LibConfig.setKeyShareVar("serverUrl", "172.25.29.229:80");
            LibConfig.setKeyShareVar("token", "");

        } else {

            Common.Token = LibConfig.getKeyShareVarForString("token");
        }

        if (!Common.checkFileIsExits("main.db"))
            FileCommon.CopyRaw(R.raw.db, "main.db");

        SuyDB suyDB = new SuyDB(APP.getApp(), getFilesDir() + "/main.db", false);
        SuyDB.setSuyDB(suyDB);
        //更新基本数据




//        WXCore.WXCoreInit(getApplicationContext());
    }




    /**
     * 获取版本信息
     *
     * @return
     */
    public String AppVerName() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取数字版本号
     *
     * @return
     */
    public int AppVerCode() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息

            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
