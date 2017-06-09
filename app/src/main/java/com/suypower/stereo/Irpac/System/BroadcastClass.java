package com.suypower.stereo.Irpac.System;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Server.StereoService;
import com.suypower.stereo.suypowerview.Server.StereoServiceMonitor;

/**
 * Created by Administrator on 14-11-13.
 */
public class BroadcastClass extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, StereoService.class);

        intent1.putExtra("mode","0");
        context.startService(intent1);
        Log.i("核心服务", "启动");


        Intent intent2 = new Intent(context, StereoServiceMonitor.class);
        context.startService(intent2);
        Log.i("监护服务", "启动");
    }
}
