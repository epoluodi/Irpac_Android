package com.suypower.stereo.Irpac.System;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Stereo on 16/7/14.
 */
public class AppConfig {

//    public static final String host = "http://192.168.0.77:8080/Cloudx";// DK

    public static final String host = "http://120.26.101.38:8081/riskControl/app";// 开发
    public static String host2 = "http://192.168.0.77:8080/Irpac";// 正式
//    public static final String host = "http://192.168.0.44:8190/Cloudx";// ZB
    //    public static final String host = getHostForSD();
    public static final String QRHhost = "";//二维码host
    public static final String AuthUrl = host + "/sysItf/";
    public static final String AppUrl = host + "/sysItf/";
    public static final String IMUrl = host + "/sysItf/";
    //    public static final String MQTTServer = "tcp://218.94.111.38:61613";
    public static final String ApiUrl = host + "/sysItf/";
    public static final String AppUpgrade = host + "/sysItf/";
    public static final String ArticleUrl = host + "/sysItf/";
    public static final String MobileUrl = host + "/sysItf/";

    public static final String APP_CACHE_HOME = APP.getApp().getCacheDir().getAbsolutePath();



    public static int WebTextZoom = 100;//web字体大小


    //标签页
    public static String Tab1Url1 = host2 + "/mobile/html/home/index.html";
    public static String Tab1Url2 = host2 + "/mobile/html/notice/index.html";
    public static String Tab1Url3 = host2 + "/mobile/html/mine/index.html";



    public static Boolean IsAPPLogin = false;// APP 是否登录


    public static String getHostForSD() {
        StringBuffer sb = new StringBuffer("");


        String filepath = Environment.getExternalStorageDirectory() + "/serverconfig.txt";
        try {
            InputStream inputStream = new FileInputStream(filepath);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



}
