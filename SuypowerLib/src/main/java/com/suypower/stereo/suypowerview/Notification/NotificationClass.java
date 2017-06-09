package com.suypower.stereo.suypowerview.Notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.suypower.stereo.suypowerview.Base.Init;
import com.suypower.stereo.suypowerview.Base.LibConfig;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.R;


/**
 * Created by Administrator on 14-9-18.
 */
public class NotificationClass {


    public static final int ADDFRIENDID = 1;//添加朋友
    public static final int CHATMSG = 2;//聊天信息
    public static final int EBINFO = 3;//e伴信息
    public static final int WEAPPINFO = 4;//系统公告信息
    private NotificationManager nm;
    private Notification baseNF;


    public NotificationClass(Context context) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }


    public void add_Notification(String msg, String title, String text, int id, PendingIntent pendingIntent) {

        Notification.Builder builder = new Notification.Builder(Init.getContext());
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.logo60);
        builder.setTicker(msg);
        builder.setContentTitle(title);
        builder.setContentText(text);
        if (LibConfig.getKeyShareVarForBoolean("msgsound"))
            builder.setSound(Uri.parse("android.resource://" + Init.getContext().getPackageName() + "/" + R.raw.sound700));
        if (LibConfig.getKeyShareVarForBoolean("msgvibrate")) {
            Common.Vibrator(Init.getContext(),500);
//            builder.setVibrate(new long[]{500});
        }
        nm.notify(id, builder.build());

    }

    public void add_Notification(String msg, String title, String text, int id, boolean issound, int ico) {
        baseNF = new Notification();
//        baseNF.icon = ico;
//        baseNF.tickerText = msg;
//        baseNF.defaults = Notification.DEFAULT_LIGHTS;
////        if (issound)
//            baseNF.sound= Uri.parse("android.resource://" + Common.Appcontext.getPackageName() + "/" + R.raw.talitha);
//        baseNF.sound =Url.pa
//                    RingtoneManager.getDefaultUri(R.raw.talitha);
//        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
//        baseNF.setLatestEventInfo(SuyApplication.getApplication(), title ,text,null);


        Notification.Builder builder = new Notification.Builder(Init.getContext());
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setSmallIcon(R.drawable.logo60);
        builder.setTicker(msg);
        builder.setSubText(text);
        builder.setSound(Uri.parse("android.resource://" + Init.getContext().getPackageName() + "/" + R.raw.sound700));
//        baseNF.setLatestEventInfo(Init.getContext(), title ,text,pendingIntent);
        nm.notify(id, baseNF);


        nm.notify(id, baseNF);

    }


    public static void Clear_Notify() {
        NotificationManager notificationManager = (NotificationManager) Init.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void Clear_Notify(int id) {
        NotificationManager notificationManager = (NotificationManager) Init.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(id);
    }

    public Notification add_Notification_ProgressBar(String msg, String title, int id) {
        baseNF = new Notification();
//        baseNF.icon = R.drawable.appico;
        baseNF.tickerText = msg;
        baseNF.defaults = Notification.DEFAULT_LIGHTS;
//        baseNF.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

//        baseNF.contentView = new RemoteViews("com.seuic.biostime",R.layout.notify_download);
//        baseNF.contentView.setProgressBar(R.id.pb, 50,0, false);
//        baseNF.contentView.setTextViewText(R.id.down_titile,title);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
//        baseNF.setLatestEventInfo(Common.Appcontext, "" ,"",null);

        nm.notify(id, baseNF);
        return baseNF;

    }


}
