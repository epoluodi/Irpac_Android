package com.suypower.stereo.suypowerview.Server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.suypower.stereo.suypowerview.Common.Common;


public class StereoServiceMonitor extends Service {
    public static final String RECEIVERSTARTUPSERVER = "RESETSERVERBROADCAST";
    public static final String ClassName = StereoServiceMonitor.class.getName();

    private Boolean iswhile= true;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("消息服务", "启动");
        new Thread(runnable).start();
    }


    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            try
            {
                while (iswhile)
                {
                    Thread.sleep(5000);
                    Boolean r = Common.isCoreServiceRunning(StereoService.ClassName);
                    if (!r)
                    {
                        //被杀掉
                        Intent intent1 = new Intent(StereoServiceMonitor.this, StereoService.class);
                        intent1.putExtra("mode","0");
                        startService(intent1);

                        //发送通知
                        Intent intent = new Intent(RECEIVERSTARTUPSERVER);
                        sendBroadcast(intent);

                    }
                }
            }
            catch (Exception e)
            {e.printStackTrace();}
        }
    };






    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("消息服务", "app启动");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "停止");
        iswhile=false;
    }

    @Override
    public IBinder onBind(Intent intent) {
            return  null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }




}
