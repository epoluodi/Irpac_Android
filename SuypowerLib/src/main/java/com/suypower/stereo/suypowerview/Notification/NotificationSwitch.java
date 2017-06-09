package com.suypower.stereo.suypowerview.Notification;

import android.app.PendingIntent;
import android.content.Intent;

import com.suypower.stereo.suypowerview.Base.Init;

/**
 * Created by Stereo on 16/8/1.
 */
public class NotificationSwitch {
    public static Boolean IsAddfriend = true;//新增朋友开关通知
    public static Boolean IsChat = true;//聊天开关通知

    public static final int CHATACTIVTY=1;// 聊天窗口
    public static final int WEBVIEWURLACTIVTY=2;// URL窗口
    public static final int WEAPPURLACTIVTY=3;// 微应用

    private static Intent getAddFriendViewPendingIntent;
    private static Intent getChatViewPendingIntent;
    private static Intent getSplashViewPendingIntent;
    private static Intent getWebViewViewPendingIntent;
    private static Intent getWeAppViewPendingIntent;


    public static Intent getGetWebViewViewPendingIntent() {
        return getWebViewViewPendingIntent;
    }

    public static void setGetWebViewViewPendingIntent(Intent getWebViewViewPendingIntent) {
        NotificationSwitch.getWebViewViewPendingIntent = getWebViewViewPendingIntent;
    }

    public static Intent getGetSplashViewPendingIntent() {
        return getSplashViewPendingIntent;
    }

    public static void setGetSplashViewPendingIntent(Intent getSplashViewPendingIntent) {
        NotificationSwitch.getSplashViewPendingIntent = getSplashViewPendingIntent;
    }

    public static Intent getGetAddFriendViewPendingIntent() {
        return getAddFriendViewPendingIntent;
    }

    public static Intent getGetChatViewPendingIntent() {
        return getChatViewPendingIntent;
    }

    public static void setGetAddFriendViewPendingIntent(Intent getAddFriendViewPendingIntent) {
        NotificationSwitch.getAddFriendViewPendingIntent = getAddFriendViewPendingIntent;
    }

    public static void setGetChatdViewPendingIntent(Intent getChatViewPendingIntent) {
        NotificationSwitch.getChatViewPendingIntent = getChatViewPendingIntent;
    }

    public static Intent getGetWeAppViewPendingIntent() {
        return getWeAppViewPendingIntent;
    }

    public static void setGetWeAppViewPendingIntent(Intent getWeAppViewPendingIntent) {
        NotificationSwitch.getWeAppViewPendingIntent = getWeAppViewPendingIntent;
    }
}
