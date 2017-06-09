package com.suypower.stereo.Irpac.CordovaPlugin.share;

import android.os.Message;
import android.util.Log;

import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * 发布插件
 *
 * @author yxg
 */
public class SharePlugin extends CordovaPlugin {





    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;


        Message message = new Message();
        message.arg1 = this.webView.cordovaWebViewId; //前端webview  id


        //web返回
        if (action.equals("share")) {

            message.obj = args.getJSONObject(0);//json数据
            this.cordova.onMessage(BaseViewPlugin.SHAREINFO, message);
            return true;
        }





        return true;
    }
}
