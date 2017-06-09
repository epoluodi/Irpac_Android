package com.suypower.stereo.suypowerview.Base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

/**
 * Created by Stereo on 16/7/14.
 */
public class LibConfig {

    /**
     * 获取本地用户信息
     * @param key
     * @return
     */
    public static String getKeyShareVarForString(String key) {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_APPEND);
        return sharedPreferences.getString(key, "null");
    }


    public static Boolean getKeyShareVarForBoolean(String key)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static int getKeyShareVarForint(String key)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    public static long getKeyShareVarForLong(String key)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, -1);
    }
    /**
     * 设置信息

     * @param key
     * @param value
     */
    public static void setKeyShareVar(String key,String value)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static void setKeyShareVar(String key,int value)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static void setKeyShareVar(String key,boolean value)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    /**
     * 删除信息
     * @param key
     */
    public static void delKeyShareVar(String key)
    {
        SharedPreferences sharedPreferences = Init.getContext().getSharedPreferences("userinfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }




    /**
     * 记录消息的最后一次时间
     * @param newdtlong
     */
    public static void saveMsgDateLong(String newdtlong)
    {
        String oldsendtime= LibConfig.getKeyShareVarForString("sysnotcistime");

        if (oldsendtime.equals(""))
        {
            LibConfig.setKeyShareVar("sysnotcistime",newdtlong);
            return;
        }
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date olddate =new Date(Long.valueOf(oldsendtime)); //sDateFormat.parse(oldsendtime);
        Date newdate =new Date(Long.valueOf(newdtlong)); //sDateFormat.parse(sendTime);

        if (olddate.getTime() < newdate.getTime())
        {
            LibConfig.setKeyShareVar("sysnotcistime",newdtlong);
        }
        Log.e("消息时间long:",LibConfig.getKeyShareVarForString("sysnotcistime"));
    }

}
