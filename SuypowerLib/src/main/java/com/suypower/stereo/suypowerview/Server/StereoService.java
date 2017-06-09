package com.suypower.stereo.suypowerview.Server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.MQTT.ControlCenter;
import com.suypower.stereo.suypowerview.Notification.NotificationClass;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StereoService extends Service {


    //    public static final String host = "http://192.168.1.80:8080/Cloudx";// me
//    public static final String host = "http://192.168.0.162:8080/Cloudx";// DQ
//    public static final String host = "http://192.168.0.77:8080/Cloudx";// DK
//    public static final String host = "http://192.168.0.188:8083/Cloudx";// zt
//    public static final String host = "https://suehome.suypower.com/Cloudx_dev";// 开发
//    public static final String host = "https://om.suypower.com/Cloudx_dev";// 开发
    public static final String host = "https://om.suypower.com/Cloudx_beta";// 开发
//    public static final String host = "https://suehome.suypower.com/Cloudx";// 正式
//    public static final String host = "http://192.168.0.44:8190/Cloudx";// ZB
    //    public static final String host = getHostForSD();// DK
    public static final String AuthUrl = host + "/auth/";
    public static final String IMUrl = host + "/im/";
    public static final String AppUrl = host + "/app/";


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

    /**
     * 消息控制处理类
     */
    IBinder controlCenter;
    String mode = "0";

    private Boolean iswhile = true;
    public static final String ClassName = StereoService.class.getName();


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationClass.Clear_Notify();
        controlCenter = new ControlCenter();
        new Thread(runnable).start();
        Log.i("消息服务", "启动");
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            try {
                while (iswhile) {
                    Thread.sleep(5000);
                    Boolean r = Common.isCoreServiceRunning(StereoServiceMonitor.ClassName);
                    if (!r) {
                        Intent intent2 = new Intent(StereoService.this, StereoServiceMonitor.class);
                        startService(intent2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent == null) {
            Log.i("消息服务", "独立启动");

            ControlCenter.controlCenter = (ControlCenter) controlCenter;
            ControlCenter.controlCenter.Init(this);
            ControlCenter.controlCenter.init("", "");
            ControlCenter.controlCenter.setIsRunAPP(false);
            ControlCenter.controlCenter.login();
            return START_STICKY;
        }

        mode = intent.getStringExtra("mode");

        if (mode.equals("0")) {
            Log.i("消息服务", "独立启动");

            ControlCenter.controlCenter = (ControlCenter) controlCenter;
            ControlCenter.controlCenter.Init(this);
            ControlCenter.controlCenter.init("", "");
            ControlCenter.controlCenter.setIsRunAPP(false);
            ControlCenter.controlCenter.login();
            return START_STICKY;
        }


        Log.i("消息服务", "app启动");

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "停止");
        iswhile = false;
        if (controlCenter != null) {
            ((ControlCenter) controlCenter).disconnecetMQTT();
            ((ControlCenter) controlCenter).setiMessageControl(null);
            controlCenter = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (controlCenter == null)
            return null;

        return controlCenter;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("unbind", "退出");
        ((ControlCenter) controlCenter).mode = 0;
        ((ControlCenter) controlCenter).setIsRunAPP(false);
        ((ControlCenter) controlCenter).setIsNotification(true);
        ((ControlCenter) controlCenter).setiMessageControl(null);
        //进程退出
        return super.onUnbind(intent);

    }


}
