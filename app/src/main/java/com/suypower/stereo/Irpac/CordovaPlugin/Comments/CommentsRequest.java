package com.suypower.stereo.Irpac.CordovaPlugin.Comments;

import android.os.Message;

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
public class CommentsRequest extends CordovaPlugin {


    private CallbackContext callbackContext = null;


    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Message message = new Message();
        message.arg1 = this.webView.cordovaWebViewId; //前端webview  id

        //web返回
        if (action.equals("init")) {
            message.obj = args.getJSONObject(0);//json数据
            this.cordova.onMessage(BaseViewPlugin.COMMENTS_INIT, message);
            return true;
        }

        //div 跳转
        if (action.equals("unInit")) {
            message.obj = null;
            this.cordova.onMessage(BaseViewPlugin.COMMENTS_UNINIT, message);
            return true;
        }

        //设置收藏
        if (action.equals("updateState")) {
            message.obj = args.getJSONObject(0);//json数据
            this.cordova.onMessage(BaseViewPlugin.COMMENTS_UPDATESTATE, message);
            return true;
        }



        return true;
    }
}
