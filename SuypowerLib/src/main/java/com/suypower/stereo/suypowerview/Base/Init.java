package com.suypower.stereo.suypowerview.Base;

import android.content.Context;

import com.suypower.stereo.suypowerview.Chat.Emoji;

/**
 * Created by Stereo on 16/7/19.
 */
public final class Init {

    private static Context context;

    public static void Init(Context APPContext)
    {
        context = APPContext;

    }
    public static Context getContext()
    {
        return context;
    }
}


