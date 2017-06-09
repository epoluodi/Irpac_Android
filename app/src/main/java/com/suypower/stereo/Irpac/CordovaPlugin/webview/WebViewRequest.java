package com.suypower.stereo.Irpac.CordovaPlugin.webview;

import android.os.Message;

import com.suypower.stereo.Irpac.System.APP;
import com.suypower.stereo.Irpac.System.UserInfo;
import com.suypower.stereo.suypowerview.CordovaPlugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * app 系统插件
 *
 * @author yxg
 */
public class WebViewRequest extends CordovaPlugin {





    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {



        Message message = new Message();

        message.arg1 = this.webView.cordovaWebViewId; //前端webview  id

        //web返回
        if (action.equals("goback")) {
            message.obj = null;
            this.cordova.onMessage(BaseViewPlugin.GOBACK_ACTION, message);
            return true;
        }
        //div 跳转
        if (action.equals("goback2")) {
            message.obj = null;
            this.cordova.onMessage(BaseViewPlugin.GOBACK2_ACTION, message);
            return true;
        }
        //新窗口
        if (action.equals("open")) {
            message.obj = args.getJSONObject(0);//json数据
            this.cordova.onMessage(BaseViewPlugin.OPENWINDOW_ACTION, message);
            return true;
        }
        //web返回
        if (action.equals("close")) {

            message.obj = args.getString(0);

            this.cordova.onMessage(BaseViewPlugin.CLOSEWINDOW_ACTION, message);
            return true;
        }

        //注册close事件
        if (action.equals("closeEvent")) {

            message.obj = args.getString(0);

            this.cordova.onMessage(BaseViewPlugin.CLOSEEVENT_ACTION, message);
            return true;
        }

        //清除缓存
        if (action.equals("clear")) {
            message.obj = null;
            this.cordova.onMessage(BaseViewPlugin.CLEARWEBCACHE_ACTION, message);
            return true;
        }
        //清除缓存
        if (action.equals("setBarRight")) {
            message.obj = args.getJSONObject(0);
            this.cordova.onMessage(BaseViewPlugin.SETBARRIGHT_ACTION, message);
            return true;
        }

        if (action.equals("openSignView")) {

            message.obj = args.getString(0);
            this.cordova.onMessage(BaseViewPlugin.openSignView, message);
            return true;
        }


        return true;
    }
}
