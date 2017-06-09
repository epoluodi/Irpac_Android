package com.suypower.stereo.suypowerview.WXCore;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.suypower.stereo.suypowerview.Base.Init;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Stereo on 2017/5/2.
 */

public class WXCore {
    private static final String APIID = "wxb9cf79b44461868a";

    private static IWXAPI iwxapi;


    public static void WXCoreInit(Context context) {
        iwxapi = WXAPIFactory.createWXAPI(context, APIID);
        boolean r = iwxapi.registerApp(APIID);
        Log.i("微信注册 ", String.valueOf(r));

    }


    public static void sendWebLink(String url, String title, String desc, byte[] thumbBmp,int Scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;

        if (thumbBmp != null)
            msg.thumbData = thumbBmp;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = Scene;
        iwxapi.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
